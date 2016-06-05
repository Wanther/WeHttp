package cn.wanther.http.exception;

import org.apache.http.HttpStatus;

public class AuthorizeException extends ClientException {

    private static final long serialVersionUID = 1L;

    public AuthorizeException() {
        super(HttpStatus.SC_UNAUTHORIZED);
    }
    
    public AuthorizeException(String message){
        super(HttpStatus.SC_UNAUTHORIZED, message);
    }
    
    public AuthorizeException(String message, Throwable t){
        super(HttpStatus.SC_UNAUTHORIZED, message, t);
    }

}
