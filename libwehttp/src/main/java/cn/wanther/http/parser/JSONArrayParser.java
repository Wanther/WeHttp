package cn.wanther.http.parser;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;

public class JSONArrayParser implements Parser<JSONArray> {

    @Override
    public JSONArray parse(Request req, HttpResponse resp) throws IOException, AccessException {
        String result = new StringParser().parse(req, resp);
        
        try {
            return new JSONArray(result);
        } catch (JSONException e) {
            throw new AccessException("json parse failed", e);
        }
    }

}
