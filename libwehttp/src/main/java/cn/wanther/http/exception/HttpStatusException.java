package cn.wanther.http.exception;

public class HttpStatusException extends AccessException {
	private static final long serialVersionUID = 1L;
	
	private int statusCode;
	
	public HttpStatusException(int statusCode){
		this.statusCode = statusCode;
	}
	
	public HttpStatusException(int statusCode, String errorMsg){
		super(errorMsg);
		this.statusCode = statusCode;
	}
	
	public HttpStatusException(int statusCode, Throwable cause){
		super(cause);
		this.statusCode = statusCode;
	}
	
	public HttpStatusException(int statusCode, String errorMsg, Throwable cause){
		super(errorMsg, cause);
		this.statusCode = statusCode;
	}
	
	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}
	
	public int getStatusCode(){
		return statusCode;
	}
}
