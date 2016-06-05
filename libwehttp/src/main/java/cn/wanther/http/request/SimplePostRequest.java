package cn.wanther.http.request;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;
import android.util.Log;

import cn.wanther.http.HttpExecutor;
import cn.wanther.http.Utils;
import cn.wanther.http.Request;

/**
 * Simple Post Request, body size limited, prefered less than 100K
 * @author wanghe
 *
 */
public class SimplePostRequest extends Request {
	
private static final String TAG = "SimplePostRequest";
    
    private byte[] mBody;
    
    public SimplePostRequest(String url, Map<String, String> params){
        this(url, params, true);
    }
    
    public SimplePostRequest(String url, Map<String, String> params, boolean encode){
        if(params != null && !params.isEmpty()){
            StringBuilder paramStr = new StringBuilder();
            Set<String> keySet = params.keySet();
            boolean first = true;
            for(String name : keySet){
                if(!first){
                    paramStr.append('&');
                }
                String value = params.get(name);
                if(encode){
                    try {
                        value = URLEncoder.encode(value, getCharset());
                    } catch (UnsupportedEncodingException e) {
                        if(HttpExecutor.isDebug()){
                            Log.w(TAG, "url encode failed " + value, e);
                        }
                    }
                }
                paramStr.append(name).append('=').append(value);
                first = false;
            }
            try {
                mBody = paramStr.toString().getBytes(getCharset());
            } catch (UnsupportedEncodingException e) {
                mBody = paramStr.toString().getBytes();
            }
        }
        
        init(url);
    }
    
    public SimplePostRequest(String url, String params){
        if(!TextUtils.isEmpty(params)){
            try {
                mBody = params.getBytes(getCharset());
            } catch (UnsupportedEncodingException e) {
                mBody = params.getBytes();
            }
        }
        init(url);
    }
    
    public SimplePostRequest(String url, byte[] body){
        if(body != null && body.length > 0){
            mBody = body;
        }
        init(url);
    }
    
    protected void init(String url){
        setUrl(url);
        setMethod(HttpPost.METHOD_NAME);
        setConnectTimeout(DEFAULT_CONN_TIMEOUT);
        setReadTimeout(DEFAULT_READ_TIMEOUT);
        setDoInput(true);
        if(mBody == null || mBody.length <= 0){
            setDoOutput(false);
        }else{
            setDoOutput(true);
            //header
            setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded" + HTTP.CHARSET_PARAM + Charset.forName("UTF-8").name());
            setHeader(HTTP.CONTENT_LEN, mBody.length + "");
        }
    }

    @Override
    public void doOutput() throws IOException {
        OutputStream out = getOutput();
        if(out == null){
            Log.w(TAG, "doOutput() executed but outputStream is null");
            return;
        }
        BufferedOutputStream bos = null;
        try{
            if(mBody == null || mBody.length <= 0){
                throw new IOException("invalid post body");
            }
            
            if(HttpExecutor.isDebug()){
            	Log.v(TAG, new String(mBody, getCharset()));
            }
            
            bos = new BufferedOutputStream(out, mBody.length);
            bos.write(mBody);
            
        }finally{
            Utils.close(bos);
            setOutput(null);
        }
        
    }
	
}
