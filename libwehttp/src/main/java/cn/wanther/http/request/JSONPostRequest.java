package cn.wanther.http.request;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONPostRequest extends SimplePostRequest {
    public JSONPostRequest(String url, JSONObject json){
        super(url, json.toString());
        init();
    }
    
    public JSONPostRequest(String url, JSONArray json){
        super(url, json.toString());
        init();
    }
    
    public JSONPostRequest(String url, String jsonStr){
        super(url, jsonStr);
        init();
    }
    
    protected void init(){
        setHeader(HTTP.CONTENT_TYPE, "application/json" + HTTP.CHARSET_PARAM + getCharset());
    }
}
