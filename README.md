# 概述
- 一个简单的同步的Http Restful库，使用HttpURLConnection实现
  - 自定义Header
  - 自定义超时时间
  - 自定义向服务器写数据
  - 自定义处理Http的StatusCode，400,401...500,...

  如果想使用Cookie等，可以扩展，但是这种情况下建议使用apache的httpclient

- 目前已扩展的功能
  - get,post,put,trace,options,delete,head方法
  - 文件上传
  - 文件下载
  - 返回字符串
  - 返回JSONObject
  - 返回JSONArray

# 例子
- 准备

  ```java
  //先创建一个HttpExecutor
  HttpExecutor executor = new SimpleHttpExecutor();
  ```

- 简单的get

  ```java
  Request request = new GetRequest("http://uda100.com");
  Parser<String> parser = new StringParser();
  String result = executor.execute(request, parser);
  ```

- 自定义Header的get请求

  ```java
  Request request = new GetRequest("http://uda100.com");
  request.setHeader("Authorization", "Bearer a4e3b1dc2146a0225c476a3edd3044f3e2f65949");
  request.setHeader("Accept", "application/json; version=1");
  Parser<String> parser = new StringParser();
  String result = executor.execute(request, parser);
  ```

- form的post请求

  ```java
  Map<String, String> params = new HashMap<String, String>();
  params.put("name", "wanghe");
  params.put("pwd", "123456");
  Request request = new SimplePostRequest("http://uda100.com", params);
  Parser<String> parser = new StringParser();
  String result = executor.execute(request, parser);
  ```

- Multipart的POST（键值对和文件一起上传）

  ```java
  Map<String, Object> params = new HashMap<String, Object>();
  params.put("subject", "2");
  params.put("image", new File("/storage/sdcard0/Uda/tmp.jpg"));
  Request request = new MultipartPostRequest("https://uda100.com", params);
  Parser parser = new StringParser();
  String result = executor.execute(request, parser);
  ```

- json的post请求

  ```java
  Request request = new JSONPostRequest("http://uda100.com");
  Parser<String> parser = new StringParser();
  String result = executor.execute(request, parser);
  ```
- JSON的返回

  ```java
  Request request = new GetRequest("http://uda100.com");
  Parser<JSONObject> parser = new JSONParser();
  JSONObject result = executor.execute(request, parser);
  ```

- JSONArray的返回

  ```java
  Request request = new GetRequest("http://uda100.com");
  Parser<JSONArray> parser = new JSONArrayParser();
  JSONArray result = executor.execute(request, parser);
  ```
- 文件下载

  ```java
  File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS, "DownloadTest.mp4"));

  DownloadObject downloadObject = new DownloadObject("http://uda100.com/download", destinationFile, false);

  Request req = new DownloadRequest(downloadObject);

  DownloadParser parser = new DownloadParser(downloadObject, mDownloadListener);

  new SimpleHttpExecutor().execute(req, parser);
  ```

- 取消下载

  ```java
  parser.cancel();
  ```
  或者使用Thread的interrupt()方法，Future的cancel(**true**)


- 默认会进行断点续传，如果是强制重新下载，将`DownloadObject`的`reset`设置成`true`

  ```
  DownloadObject downloadObject = new DownloadObject(destinationFile, "DownloadTest.mp4"), true);
  ```

- 进度和状态监控

  ```java
  mListener = new DownloadParser.DownloadListener{
      void onDownloadStart(DownloadObject downloadObject){
          
      }
      void onDownloadProgressChanged(DownloadObject downloadObject, long currentLength, long totalLength){

      }
      void onDownloadCancelled(DownloadObject downloadObject){

      }
      void onDownloadComplete(DownloadObject downloadObject){

      }
  }

  ```
  需要注意的是这些回调方法都在异步线程调用，如果要更新UI需要自己往Handler Post


# 异常处理
- IOException
- AccessException
  - HttpStatusException
    - ServerException(500,501...)
    - ClientException(400,401...)
      - AuthorizedException(401)
      - BadRequestException(400)

原则上，自定义的Request和Parser按照上述结构和HTTP状态码抛出响应的异常，然后在调用的时候酌情选择处理

  ```java
  Request request = new GetRequest("http://uda100.com");
  Parser<String> parser = new StringParser();
  try{
      String result = executor.execute(request, parser);
  } catch (IOException e) {
      //网络不通
  } catch (AccessException e) {
      //网络错误，请稍后重试
  }
  ```

如果上述异常结构不满足需求，可以自定义异常，继承AccessException，然后在调用的时候酌情选择捕获处理

比如需求上说服务器返回status=200,message={"errorCode":"1003","detail":"用户名密码不正确"}的时候，此情况下需要做单独处理的时候

我们定义一个CustomeLogicException extends AccessException，然后在自己的Parser中

  ```java
  if(status == 200){
      String message = Utils.toString(response.getEntity());
      try{
          JSONObject json = new JSONObject(message);
          if(json.has("errorCode")){
              message = json.optString("detail");
          }
          throw new CustomeLogicException(json.optInt("errorCode"), message);
      } catch (JSONException e) {
          
      }
      
  }
  ```

  在调用的时候

  ```java
  Request request = new GetRequest("http://uda100.com");
  Parser<String> parser = new StringParser();
  try{
      String result = executor.execute(request, parser);
  } catch (IOException e) {
      //网络不通
  } catch (AuthorizeException e) {
      //需要登录
  } catch (ClientException e) {
      //请求参数错误，Toast提示e.getMessage()
  } catch (ServerException e) {
      //服务器有问题的时候
  } catch (CustomeLogicException e) {
      //自定义处理
  } catch (AccessException e) {
      //其他，自定义的异常可能在这里
  }
  ```

没设计成RuntimeException是想强制让使用者处理响应的异常情况

# 思考&优化

- Android6.0的SDK删除了org.apache.http的包，需要优化成不依赖org.apache.http的库

- 下载的`DownloadObject`可以做更多属性的配置或者Callbacks，比如遇到下载文件已存在的的处理策略等

- HTTP协议的Cache，304 Not Modified

  目前基本都是API的访问，也没做基于HTTP协议的缓存相关的工作，如果想做的话参考Volley源码(核心类HurlStack,BasicNetwork,NetworkDispatcher)

- 绝大部分请求的解析中都需要创建byte[] buffer = new byte[getBufferSize()];，考虑将byte做成池，参考Volley源码