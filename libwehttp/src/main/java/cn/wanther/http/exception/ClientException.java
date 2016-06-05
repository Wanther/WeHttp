package cn.wanther.http.exception;

public class ClientException extends HttpStatusException {

    private static final long serialVersionUID = 1L;
    
    public ClientException(int statusCode){
        super(statusCode);
    }
    
    public ClientException(int statusCode, String message){
        super(statusCode, message);
    }
    
    public ClientException(int statusCode, Throwable cause) {
        super(statusCode, cause);
    }
    
    public ClientException(int statusCode, String message, Throwable t){
        super(statusCode, message, t);
    }

}
