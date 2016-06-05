package cn.wanther.http.request;

import org.apache.http.client.methods.HttpDelete;


public class DeleteRequest extends GetRequest {

	public DeleteRequest(String url){
		super(url);
		setMethod(HttpDelete.METHOD_NAME);
	}

}
