package cn.wanther.http.request;

import org.apache.http.client.methods.HttpTrace;


public class TraceRequest extends GetRequest {

	public TraceRequest(String url){
		super(url);
		setMethod(HttpTrace.METHOD_NAME);
	}

}
