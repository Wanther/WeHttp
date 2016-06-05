package cn.wanther.http.request;

import java.util.Map;

import org.apache.http.client.methods.HttpPut;

public class SimplePutRequest extends SimplePostRequest {

	public SimplePutRequest(String url, Map<String, String> params) {
		super(url, params);
	}

	@Override
	protected void init(String url) {
		super.init(url);
		
		setMethod(HttpPut.METHOD_NAME);
	}

}
