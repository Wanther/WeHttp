package cn.wanther.http.request;

import android.util.Log;

import cn.wanther.http.HttpExecutor;
import cn.wanther.http.Request;
import cn.wanther.http.Utils;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * 使用multipart
 * 参考<a href="http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html">http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html</a>
 * 
 * @author wanghe
 *
 */
public class MultipartPostRequest extends Request{
	
private static final String TAG = "MultipartPostRequest";
    
    protected static final String BOUNDARY = "gc0p4Jq0M2Yt08jU534c0p";
    protected static final String LINE_END = "\r\n";
    protected static final int DEFAULT_BUFFER_SIZE = 10 * 1024;
    
    private Map<String, Object> mParams;
    private int mBufferSize;
    private StringBuilder mBuilder = new StringBuilder();

    public MultipartPostRequest(String url, Map<String, Object> params){
        this(url, params, DEFAULT_CHARSET.name(), DEFAULT_BUFFER_SIZE);
    }

    public MultipartPostRequest(String url, Map<String, Object> params, String charsetName, int bufferSize){
        if(params == null || params.isEmpty()){
            throw new RuntimeException("have you forget the parameters");
        }
        mParams = params;
        setCharset(charsetName);
        init(url);
    }

    protected void init(String url){
        setUrl(url);
        setMethod(HttpPost.METHOD_NAME);
        setConnectTimeout(DEFAULT_CONN_TIMEOUT);
        setReadTimeout(DEFAULT_READ_TIMEOUT);
        setDoInput(true);
        setDoOutput(true);

        setHeader(HTTP.CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY);
    }

    @Override
    public void doOutput() throws IOException {
        OutputStream out = getOutput();
        if(out == null){
            if(HttpExecutor.isDebug()){
                Log.w(TAG, "doOutput() executed but outputStream is null");
            }
            return;
        }

        //checkCancelled();
        
        try{
            
            Set<String> keySet = mParams.keySet();
            for(String key : keySet){
                Object content = mParams.get(key);
                if(content == null){
                    continue;
                }
                
                if(content instanceof File){    // upload file
                    writeFile(key, (File)content, out);
                    // 文件的取消写操作放到了writeFile方法中
                }else if(content.getClass() == String.class){   // <-- normal string
                    writeString(key, (String)content, out);
                    //checkCancelled();
                }else{
                    writeString(key, content.toString(), out);
                    //checkCancelled();
                }
                mBuilder.setLength(0);
                mBuilder.append("--").append(BOUNDARY).append("--").append(LINE_END);
                
                out.write(mBuilder.toString().getBytes(getCharset()));
                if(HttpExecutor.isDebug()){
                    Log.v(TAG, mBuilder.toString());
                }
            }
        } finally {
            Utils.close(out);
        }
        
    }

    protected int getBufferSize(){
        return mBufferSize <= 0 ? DEFAULT_BUFFER_SIZE : mBufferSize;
    }
    
    protected void writeFile(String key, File f, OutputStream out) throws IOException {
        mBuilder.setLength(0);
        mBuilder.append("--").append(BOUNDARY).append(LINE_END);
        mBuilder.append("Content-Disposition: form-data;name=\"").append(key).append("\";filename=\"").append(f.getName()).append("\"").append(LINE_END);
        mBuilder.append(HTTP.CONTENT_TYPE).append(":application/octet-stream").append(LINE_END);
        mBuilder.append(LINE_END);
        out.write(mBuilder.toString().getBytes(getCharset()));
        if(HttpExecutor.isDebug()){
            Log.v(TAG, mBuilder.toString());
        }
        
        InputStream fileIn = new FileInputStream(f);
        try{
            byte[] buffer = new byte[getBufferSize()];
            int len;
            while((len = fileIn.read(buffer)) != -1){
                out.write(buffer, 0, len);
                //checkCancelled();
            }
        }finally{
            Utils.close(fileIn);
        }
        
        if(HttpExecutor.isDebug()){
            Log.v(TAG, "[write FileObject]");
        }
        
        out.write(LINE_END.getBytes(getCharset()));
        if(HttpExecutor.isDebug()){
            Log.v(TAG, LINE_END);
        }
    }
    
    protected void writeString(String key, String value, OutputStream out) throws IOException{
        mBuilder.setLength(0);
        mBuilder.append("--").append(BOUNDARY).append(LINE_END);
        mBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END);
        mBuilder.append(HTTP.CONTENT_TYPE).append(":text/plain").append(HTTP.CHARSET_PARAM).append(getCharset()).append(LINE_END);
        mBuilder.append(LINE_END);
        mBuilder.append(value).append(LINE_END);
        
        out.write(mBuilder.toString().getBytes(getCharset()));
        if(HttpExecutor.isDebug()){
            Log.v(TAG, mBuilder.toString());
        }
    }
	
}