package cn.wanther.http.exception;

public class AccessException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public AccessException(){}
	
	public AccessException(String message){
		super(message);
	}
	
	public AccessException(String message, Throwable t){
		super(message, t);
	}
	
	public AccessException(Throwable t){
		super(t);
	}
}
