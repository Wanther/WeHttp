package cn.wanther.http.exception;

public class ServerException extends HttpStatusException {

    private static final long serialVersionUID = 1L;

    public ServerException(int statusCode){
        super(statusCode);
    }
    
    public ServerException(int statusCode, String message){
        super(statusCode, message);
    }
    
    public ServerException(int statusCode, Throwable t){
        super(statusCode, t);
    }
    
    public ServerException(int statusCode, String errorMsg, Throwable cause) {
        super(statusCode, errorMsg, cause);
    }

}
