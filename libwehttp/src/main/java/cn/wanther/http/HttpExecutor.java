package cn.wanther.http;

import java.io.IOException;
import java.nio.charset.Charset;

import cn.wanther.http.Parser;
import cn.wanther.http.exception.AccessException;

public abstract class HttpExecutor {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private static boolean DEBUG = false;

	public static void setDebug(boolean debug) {
		DEBUG = debug;
	}

	public static boolean isDebug() {
		return DEBUG;
	}

	/**
	 * 发送一个Request请求，使用parser解析返回结果
	 * 
	 * @param req
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws AccessException
	 * @throws InterruptedException
	 */
	public abstract <T> T execute(Request req, Parser<T> parser) throws IOException, AccessException;
}
