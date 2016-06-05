package cn.wanther.http.executor;

import android.util.Log;

import cn.wanther.http.HttpExecutor;
import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.Utils;
import cn.wanther.http.exception.AccessException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class SimpleHttpExecutor extends HttpExecutor {
	
	private static final String TAG = "SimpleHttpExecutor";
	
	static{
		if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.FROYO){
			System.setProperty("http.keepAlive", "false");
		}
	}
    
    private SSLSocketFactory mSSLSocketFactory;
    
    public void setSSLSocketFactory(SSLSocketFactory factory){
        mSSLSocketFactory = factory;
    }

    @Override
    public <T> T execute(final Request req, final Parser<T> parser) throws IOException, AccessException {
        
        if(req == null){
            throw new RuntimeException("request cannot be null!");
        }
        
        final String url = urlRewrite(req);
        
        if(HttpExecutor.isDebug()){
            Log.d(TAG, "request.url=" + url);
        }
        
        HttpURLConnection conn = null;
        try{
            conn = openConnection(url);

            setConnectionProperties(conn, req);
            
            setCommonHeaders(conn, req);
            
            setRequestHeaders(conn, req);
            
            processRequest(conn, req);
            
            final HttpResponse resp = getResponse(conn, req);
            
            return parseResponse(parser, req, resp);
        }catch(ProtocolException e){
            throw new AccessException("invalid request protocol", e);
        }catch(MalformedURLException e){
            throw new AccessException("invalid url:" + url, e);
        }finally{
            Utils.disconnect(conn);
        }
        
    }

    /**
     * 子类实现该方法进行url重写
     * 
     * @param req
     * @return
     */
    protected String urlRewrite(Request req){
        return req.getUrl();
    }
    
    protected HttpURLConnection openConnection(String urlString) throws MalformedURLException, IOException{
        return (HttpURLConnection)new URL(urlString).openConnection();
    }
    
    protected void setConnectionProperties(HttpURLConnection conn, Request req) throws ProtocolException{
        // 自定义的SSKSocketFactory
        if(mSSLSocketFactory != null && (conn instanceof HttpsURLConnection)){
            ((HttpsURLConnection)conn).setSSLSocketFactory(mSSLSocketFactory);
        }
        
        conn.setRequestMethod(req.getMethod());
        conn.setConnectTimeout(req.getConnectTimeout());
        conn.setReadTimeout(req.getReadTimeout());
        conn.setDoInput(req.isDoInput());
        conn.setDoOutput(req.isDoOutput());
        conn.setUseCaches(false);
    }
    
    /**
     * 子类实现该方法添加统一的Header，比如UserAgent
     * 
     * @param conn
     * @param req
     */
    protected void setCommonHeaders(HttpURLConnection conn, Request req){}
    /**
     * 此操作中的设置覆盖{@link #setCommonHeaders(HttpURLConnection, Request)}
     * 
     * @param conn
     * @param req
     */
    protected void setRequestHeaders(HttpURLConnection conn, Request req){
        Map<String, String> headers = req.getHeaders();
        if(headers != null && !headers.isEmpty()){
            Set<String> keySet = headers.keySet();
            for(String key : keySet){
                if(HttpExecutor.isDebug()){
                    Log.v(TAG, String.format("setHeader %s = %s", key, headers.get(key)));
                }
                conn.setRequestProperty(key, headers.get(key));
            }
        }
    }
    
    /**
     * 有这么一种情况，post一个请求，发现没有权限，在还没上传body的时候，服务器就返回了401，这时候getOutputStream会失败
     * 所以在这里尝试获取OutputStream失败之后继续执行，目的是看看返回值是什么，然后交给parser做自定义处理
     * 确保OutputStream被关闭
     * 
     * @param conn
     * @param req
     * @throws IOException
     * @throws AccessException
     */
    protected void processRequest(HttpURLConnection conn, Request req) throws IOException, AccessException{
        if(!req.isDoOutput()){
            return;
        }

        try {
            req.setOutput(conn.getOutputStream());
        } catch (IOException e) {
            if(HttpExecutor.isDebug()){
                Log.e(TAG, "getOutputStream failed", e);
            }
            req.setOutput(null);
        }
        
        if(req.getOutput() == null){
            return;
        }

        try{
            req.doOutput();
        }finally{
            Utils.close(req.getOutput());
            req.setOutput(null);
        }
    }
    
    /**
     * 不管返回的是200还是其他的，都创建一个HttpResponse
     * 
     * @param conn
     * @param req
     * @return
     * @throws IOException
     * @throws AccessException
     */
    protected HttpResponse getResponse(HttpURLConnection conn, Request req) throws IOException, AccessException{
        int responseCode = conn.getResponseCode();
        
        if(HttpExecutor.isDebug()){
        	if(responseCode == HttpStatus.SC_OK){
        		Log.d(TAG, "response code = " + responseCode);
        	}else if(responseCode < HttpStatus.SC_BAD_REQUEST){
        		Log.w(TAG, "response code = " + responseCode);
        	}else{
        		Log.e(TAG, "response code = " + responseCode);
        	}
        }
        
        if(responseCode == -1){
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        StatusLine responseStatus = new BasicStatusLine(protocolVersion, responseCode, conn.getResponseMessage());
        BasicHttpResponse resp = new BasicHttpResponse(responseStatus);
        
        BasicHttpEntity entity = new BasicHttpEntity();
        
        entity.setContentLength(conn.getContentLength());
        entity.setContentEncoding(conn.getContentEncoding());
        entity.setContentType(conn.getContentType());
        
        final Map<String, List<String>> respHeaders = conn.getHeaderFields();
        if(respHeaders != null){
            final Set<String> keySet = respHeaders.keySet();
            for (String key : keySet) {
                if(key != null){
                    Header h = new BasicHeader(key, respHeaders.get(key).get(0));
                    resp.addHeader(h);
                }
            }
        }
        
        if(responseCode >= HttpStatus.SC_OK
                && responseCode != HttpStatus.SC_NO_CONTENT
                && responseCode != HttpStatus.SC_RESET_CONTENT
                && responseCode != HttpStatus.SC_NOT_MODIFIED){
            
            InputStream input = null;
            try{
                input = conn.getInputStream();
            }catch(IOException e){
                input = conn.getErrorStream();
            }
            entity.setContent(input);
        }
        
        resp.setEntity(entity);
        
        return resp;
    }
    
    /**
     * 解析HttpResponse，确保InputStream被关闭
     * 
     * @param parser
     * @param req
     * @param resp
     * @return
     * @throws IOException
     * @throws AccessException
     */
    protected <T> T parseResponse(final Parser<T> parser, final Request req, final HttpResponse resp) throws IOException, AccessException{
        HttpEntity entity = resp.getEntity();
        try{
            return parser.parse(req, resp);
        }finally{
            Utils.consumeEntity(entity);
        }
    }
}
