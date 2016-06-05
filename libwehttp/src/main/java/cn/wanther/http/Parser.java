package cn.wanther.http;

import java.io.IOException;

import org.apache.http.HttpResponse;

import cn.wanther.http.exception.AccessException;

public interface Parser<T> {
    /**
     * 解析http的返回
     * 
     * @param req 最初的请求
     * @param resp 基本的响应信息，包括成功和错误
     * @return
     * @throws IOException
     * @throws AccessException
     */
    T parse(Request req, HttpResponse resp) throws IOException, AccessException;
}
