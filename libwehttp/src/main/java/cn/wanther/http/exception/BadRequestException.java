package cn.wanther.http.exception;

import org.apache.http.HttpStatus;

public class BadRequestException extends ClientException {

	private static final long serialVersionUID = 1L;
	
	public BadRequestException(){
		super(HttpStatus.SC_BAD_REQUEST);
	}
	
	public BadRequestException(String message){
		super(HttpStatus.SC_BAD_REQUEST, message);
	}
	
	public BadRequestException(String message, Throwable cause) {
		super(HttpStatus.SC_BAD_REQUEST, message, cause);
	}
	
}
