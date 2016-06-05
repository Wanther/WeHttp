package cn.wanther.http.exception;

import org.apache.http.HttpStatus;

public class ForbiddenException extends ClientException {
	
	private static final long serialVersionUID = 1L;
    
	public ForbiddenException(){
		super(HttpStatus.SC_FORBIDDEN);
	}
	
	public ForbiddenException(String message){
		super(HttpStatus.SC_FORBIDDEN, message);
	}
	
	public ForbiddenException(String message, Throwable cause) {
		super(HttpStatus.SC_FORBIDDEN, message, cause);
	}

}
