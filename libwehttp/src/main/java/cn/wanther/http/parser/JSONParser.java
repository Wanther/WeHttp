package cn.wanther.http.parser;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;

public class JSONParser implements Parser<JSONObject> {

    @Override
    public JSONObject parse(Request req, HttpResponse resp) throws IOException, AccessException {
        String result = new StringParser().parse(req, resp);
        
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            throw new AccessException("json parse failed", e);
        }
    }

}
