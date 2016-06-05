package cn.wanther.http.request;

import org.apache.http.client.methods.HttpOptions;

public class OptionsRequest extends GetRequest {

    public OptionsRequest(String url) {
        super(url);
        setMethod(HttpOptions.METHOD_NAME);
    }

}
