package cn.wanther.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.wanther.http.exception.AccessException;

public abstract class Request {
    public static final int DEFAULT_CONN_TIMEOUT = 8 * 1000;
    public static final int DEFAULT_READ_TIMEOUT = 10 * 1000;
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    
    private String mUrl;
    private String mMethod;
    private String mCharset;
    private int mConnectTimeout;
    private int mReadTimeout;
    private Map<String, String> mHeaders;
    private boolean mDoInput = true;
    private boolean mDoOutput;
    private OutputStream mOutput;
    private AtomicBoolean mCancelled = new AtomicBoolean(false);
    
    public void setUrl(String url){
        mUrl = url;
    }
    
    public String getUrl(){
        return mUrl;
    }
    
    public void setMethod(String method){
        mMethod = method;
    }
    
    public String getMethod(){
        return mMethod;
    }
    
    public void setConnectTimeout(int timeoutMs){
        mConnectTimeout = timeoutMs;
    }
    
    public int getConnectTimeout(){
        return mConnectTimeout;
    }
    
    public void setReadTimeout(int timeoutMs){
        mReadTimeout = timeoutMs;
    }
    
    public int getReadTimeout(){
        return mReadTimeout;
    }
    
    public void setDoInput(boolean doInput){
        mDoInput = doInput;
    }
    
    public boolean isDoInput(){
        return mDoInput;
    }
    
    public void setDoOutput(boolean doOutput){
        mDoOutput = doOutput;
    }
    
    public boolean isDoOutput(){
        return mDoOutput;
    }
    
    public void setCharset(String charset){
        mCharset = charset;
    }
    
    public String getCharset(){
        return mCharset == null ? DEFAULT_CHARSET.name() : mCharset;
    }
    
    public void setHeader(String key, String value){
        if(mHeaders == null){
            mHeaders = new HashMap<String, String>();
        }
        mHeaders.put(key, value);
    }
    
    public String getHeader(String key){
        if(mHeaders == null){
            return null;
        }
        return mHeaders.get(key);
    }
    
    public Map<String, String> getHeaders(){
        return mHeaders;
    }
    
    public void setOutput(OutputStream out){
        if(!isDoOutput()){
            throw new RuntimeException("do not set outputStream when doOutput=false");
        }
        mOutput = out;
    }
    
    public OutputStream getOutput(){
        return mOutput;
    }
    
    public boolean isCancelled(){
        return mCancelled.get() || Thread.interrupted();
    }
    
    public void cancel(){
        mCancelled.set(true);
    }
    
    public void doOutput() throws IOException, AccessException {
        return;
    }
    
}
