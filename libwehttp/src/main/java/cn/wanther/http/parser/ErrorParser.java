package cn.wanther.http.parser;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import cn.wanther.http.Utils;
import cn.wanther.http.Parser;
import cn.wanther.http.Request;
import cn.wanther.http.exception.AccessException;
import cn.wanther.http.exception.AuthorizeException;
import cn.wanther.http.exception.BadRequestException;
import cn.wanther.http.exception.ClientException;
import cn.wanther.http.exception.ForbiddenException;
import cn.wanther.http.exception.HttpStatusException;
import cn.wanther.http.exception.ServerException;

public class ErrorParser implements Parser<Void> {

    @Override
    public Void parse(Request req, HttpResponse resp) throws IOException, AccessException {
        int statusCode = resp.getStatusLine().getStatusCode();
        
        if(statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES){
            return null;
        }
        
        String message = Utils.toString(resp.getEntity());
        
        if(statusCode >= HttpStatus.SC_BAD_REQUEST && statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR){
            switch(statusCode){
            case HttpStatus.SC_UNAUTHORIZED:
                throw new AuthorizeException(message);
            case HttpStatus.SC_BAD_REQUEST:
            	throw new BadRequestException(message);
            case HttpStatus.SC_FORBIDDEN:
            	throw new ForbiddenException(message);
            }
            throw new ClientException(statusCode, message);
        }else if(statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR){
            throw new ServerException(statusCode, message);
        }
        
        throw new HttpStatusException(statusCode, message);
    }

}
