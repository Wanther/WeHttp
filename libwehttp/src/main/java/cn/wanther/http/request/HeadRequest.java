package cn.wanther.http.request;

import org.apache.http.client.methods.HttpHead;


public class HeadRequest extends GetRequest {

	public HeadRequest(String url){
		super(url);
		setMethod(HttpHead.METHOD_NAME);
	}

}
