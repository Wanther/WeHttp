package cn.wanther.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

import cn.wanther.http.exception.AccessException;

public class Utils {
    private static final String TAG = "WeHttp.Utils";
	public static void close(Closeable c) {
		if (c == null) {
			return;
		}

		try {
			c.close();
		} catch (Exception e) {
		    if(HttpExecutor.isDebug()){
		        Log.w(TAG, e.getMessage(), e);
		    }
		}
	}

	public static void disconnect(HttpURLConnection con) {
		if (con == null) {
			return;
		}

		try {
			con.disconnect();
		} catch (Exception e) {
		    if(HttpExecutor.isDebug()){
                Log.w(TAG, e.getMessage(), e);
            }
		}
	}
	
	public static void consumeEntity(HttpEntity entity){
	    if(entity == null){
	        return;
	    }
	    try {
            entity.consumeContent();
        } catch (IOException e) {
            if(HttpExecutor.isDebug()){
                Log.w(TAG, e.getMessage(), e);
            }
        }
	}
	
    public static String toString(HttpEntity entity, int bufferSize, Charset charset) throws IOException, AccessException{
    	byte[] data = toByteArray(entity, bufferSize);
    	String result = new String(data, charset.name());
    	if(HttpExecutor.isDebug()){
            Logger.log(Log.VERBOSE, TAG, "/*size=" + data.length + "*/" + result);
        }
        return result;
	}
	
    public static String toString(HttpEntity entity) throws IOException, AccessException{
	    return toString(entity, 8 * 1024, HttpExecutor.DEFAULT_CHARSET);
	}
    
    public static byte[] toByteArray(HttpEntity entity, int bufferSize) throws IOException, AccessException{
        if(entity == null){
            return null;
        }
        
        final InputStream input = entity.getContent();
        if(input == null){
            return null;
        }
        
        try{
            int len = (int)entity.getContentLength();
            if(len <= 0){
                len = bufferSize;
            }
            
            final ByteArrayBuffer result = new ByteArrayBuffer(len);
            final byte[] buffer = new byte[bufferSize];
            int l;
            while((l = input.read(buffer)) != -1){
                result.append(buffer, 0, l);
            }
            
            return result.toByteArray();
            
        }finally{
            Utils.close(input);
        }
    }
    
    public static String getContentMimeType(final HttpEntity entity){
        String mimeType = null;
        Header contentType = entity.getContentType();
        if(contentType != null){
            final HeaderElement[] values = contentType.getElements();
            if(values.length > 0){
                mimeType = values[0].getName();
            }
        }
        return mimeType;
    }
}
