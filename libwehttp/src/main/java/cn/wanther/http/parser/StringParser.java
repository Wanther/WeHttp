package cn.wanther.http.parser;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import cn.wanther.http.HttpExecutor;
import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.Utils;
import cn.wanther.http.exception.AccessException;

public class StringParser implements Parser<String> {
	
	private static final String TAG = "StringParser";
    
    public static final int MAX_CONTENT_LEN = 5 * 1024 * 1024 * 1024;
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    
    private int mBufferSize;
    
    public StringParser(){}
    
    public StringParser(int bufferSize){
        mBufferSize = bufferSize;
    }
    
    public int getBufferSize(){
        return mBufferSize <= 0 ? DEFAULT_BUFFER_SIZE : mBufferSize;
    }
    
    protected Charset getCharset(HttpEntity entity){
        String charset = EntityUtils.getContentCharSet(entity);
        if(charset == null){
            charset = HttpExecutor.DEFAULT_CHARSET.name();
        }else{
            charset = charset.toUpperCase();
        }
        return Charset.forName(charset);
    }
    
    public String parse(Request req, HttpResponse resp) throws IOException, AccessException {
        new ErrorParser().parse(req, resp);
        
        HttpEntity entity = resp.getEntity();
        if(entity == null){
            return null;
        }
        
        return Utils.toString(entity, DEFAULT_BUFFER_SIZE, getCharset(entity));
    }
    
}
