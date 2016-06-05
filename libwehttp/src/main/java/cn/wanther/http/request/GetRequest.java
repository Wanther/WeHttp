package cn.wanther.http.request;

import org.apache.http.client.methods.HttpGet;

import cn.wanther.http.Request;


public class GetRequest extends Request {

	public GetRequest(String url){
		setUrl(url);
		setMethod(HttpGet.METHOD_NAME);
		setConnectTimeout(DEFAULT_CONN_TIMEOUT);
		setReadTimeout(DEFAULT_READ_TIMEOUT);
		setDoInput(true);
		setDoOutput(false);
	}

}
