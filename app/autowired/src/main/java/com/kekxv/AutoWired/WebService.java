package com.kekxv.AutoWired;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import fi.iki.elonen.NanoHTTPD;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings({"JavadocDeclaration", "unused"})
public class WebService extends NanoHTTPD {
  private static final int MAX_RANGE_LEN = 512 * 1024;

  /**
   * Constructs an HTTP server on given port.
   *
   * @param port 端口
   */
  private WebService(int port) {
    super(port);
  }

  @SuppressLint("StaticFieldLeak")
  static WebService web;
  private Activity mActivity;
  private File webroot = null;

  /**
   * 单例
   */
  public static WebService instance() {
    return web;
  }

  /**
   * 创建 webservice 对象
   *
   * @param activity 上下文
   * @param port     端口
   */
  public static void makeWebservice(Activity activity, int port) {
    if (web != null) {
      web.stop();
      web = null;
    }
    web = new WebService(port);
    web.mActivity = activity;
    IAutoWired.initController();
  }

  /**
   * 更新 web 目录
   *
   * @param path
   */
  public void updateWebRoot(File path) {
    webroot = path;
  }

  private Response invoke(Object obj, java.lang.reflect.Method method, IHTTPSession session, Map<String, String> files, String uri, String postData)
      throws InvocationTargetException, IllegalAccessException {
    Object resp = null;
    if (method.getParameterTypes().length > 2) {
      resp = method.invoke(obj, session, files, JSON.parseObject(postData, method.getParameterTypes()[2]));
    } else if (method.getParameterTypes().length > 1) {
      resp = method.invoke(obj, session, JSON.parseObject(postData, method.getParameterTypes()[1]));
    } else if (method.getParameterTypes().length > 0) {
      if (method.getParameterTypes()[0] == IHTTPSession.class) {
        resp = method.invoke(obj, session);
      } else {
        resp = method.invoke(obj, JSON.parseObject(postData, method.getParameterTypes()[0]));
      }
    } else {
      resp = method.invoke(obj);
    }
    if (resp == null) {
      return newFixedLengthResponse(Response.Status.SERVICE_UNAVAILABLE,
          "plan/text",
          "SERVICE UNAVAILABLE");
    }
    if (resp.getClass() == byte[].class) {
      ByteArrayInputStream is = new ByteArrayInputStream((byte[]) resp);
      return newChunkedResponse(Response.Status.OK, "application/octet-stream", is);
    } else if (resp.getClass() == Byte[].class) {
      byte[] resp1 = new byte[((Byte[]) resp).length];
      for (int i = 0; i < resp1.length; i++) {
        resp1[i] = ((Byte[]) resp)[i];
      }
      ByteArrayInputStream is = new ByteArrayInputStream(resp1);
      return newChunkedResponse(Response.Status.OK, "application/octet-stream", is);
    } else if (resp.getClass() == File.class) {
      return (Response) resp;
    } else if (resp.getClass() == Response.class) {
      return (Response) resp;
    } else if (resp.getClass() == org.json.JSONObject.class || resp.getClass() == org.json.JSONArray.class) {
      return newFixedLengthResponse(Response.Status.OK,
          "application/json",
          resp.toString());
    } else {
      return newFixedLengthResponse(Response.Status.OK,
          "application/json",
          JSON.toJSONString(resp));
    }
  }

  @Override
  public Response serve(IHTTPSession session) {
    String uri = session.getUri();
    Map<String, String> files = new HashMap<>();
    String postData = "{}";
    // noinspection DuplicatedCode
    if (session.getMethod() == Method.POST || session.getMethod() == Method.PUT) {
      try {
        session.parseBody(files);
        postData = files.get("postData");
      } catch (ResponseException | IOException e) {
        postData = "{}";
      }
    }
    try {
      Map<String, Object> clas = IAutoWired.beanList;
      for (String key : clas.keySet()) {
        Object obj = clas.get(key);
        if (obj == null) continue;
        Class<?> cls = obj.getClass();
        if (!cls.isAnnotationPresent(Controller.class)) continue;
        Controller controller = cls.getAnnotation(Controller.class);
        if (controller == null) continue;
        String path = controller.value();
        if (path == null) path = "/";
        if (!path.isEmpty()) {
          if (!uri.startsWith(path)) {
            continue;
          }
        }
        java.lang.reflect.Method[] methods = cls.getMethods();
        for (java.lang.reflect.Method method : methods) {
          RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
          if (requestMapping == null) {
            continue;
          }
          for (int i = 0; i < requestMapping.value().length; i++) {
            String p = requestMapping.value()[i];
            if (!p.startsWith("/")) {
              p = '/' + p;
            }
            if (requestMapping.regex()) {
              if (!Pattern.compile((path + p)).matcher(uri).matches()) {
                continue;
              }
              return invoke(obj, method, session, files, uri, postData);
            } else {
              if ((path + p).equals(uri)) {
                return invoke(obj, method, session, files, uri, postData);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      Log.e("WebService", "执行错误:" + e.getMessage(), e);
    }
    try {
      if (uri.length() > 0) {
        String path = uri;
        if (path.charAt(path.length() - 1) == '/') {
          path += "index.html";
        }
        InputStream is = null;
        if (webroot == null) {
          is = mActivity.getAssets().open("www" + path);
        } else {
          File tmp = new File(webroot, path);
          if (tmp.exists()) {
            is = new FileInputStream(tmp);
          } else {
            return null;
          }
        }
        if (is != null) {
          String type = getMimeTypeForFile(path);
          if (type.contains("text") || type.contains("json") || type.contains("css") || type.contains("js")) {
            type += ";charset=UTF-8";
          }
          // noinspection UnnecessaryLocalVariable
          Response res = newFixedLengthResponse(Response.Status.OK, type, is, is.available());
          // Response res = newChunkedResponse(Response.Status.OK, type, is);
          // res.setGzipEncoding(true);
          return res;
        }
      }
    } catch (FileNotFoundException ignored) {
    } catch (Exception e) {
      Log.e("WebService", "执行错误:" + e.getMessage(), e);
    }
    return null;
  }

  /**
   * Serves file from homeDir and its' subdirectories (only). Uses only URI,
   * ignores all headers and HTTP parameters.
   */
  private Response returnFile(IHTTPSession session, Map<String, String> header, File file, String mime) {
    Response res;
    try {
      // Calculate etag
      String etag = Integer.toHexString((file.getAbsolutePath()
          + file.lastModified() + "" + file.length()).hashCode());
      // Support (simple) skipping:
      long startFrom = 0;
      long endAt = -1;
      String range = header.get("range");
      if (range == null) {
        range = "bytes=0-";
      }
      if (range.startsWith("bytes=")) {
        range = range.substring("bytes=".length());
        int minus = range.indexOf('-');
        try {
          if (minus > 0) {
            startFrom = Long.parseLong(range
                .substring(0, minus));
            endAt = Long.parseLong(range.substring(minus + 1));
          }
        } catch (NumberFormatException ignored) {
        }
      }
      // Change return code and add Content-Range header when skipping is
      // requested
      long fileLen = file.length();
      if (endAt < 0) {
        endAt = fileLen - 1;
      }
      if (startFrom >= 0) {
        if (startFrom >= fileLen) {
          res = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
          // res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
          res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
          res.addHeader("ETag", etag);
        } else {
          long newLen = endAt - startFrom + 1;
          if (newLen < 0) {
            newLen = 0;
          }
          if (newLen > MAX_RANGE_LEN) {
            newLen = MAX_RANGE_LEN;
            endAt = startFrom + newLen;
          }

          int dataLen = (int) newLen;
          byte[] buff = new byte[dataLen];
          try (FileInputStream fis = new FileInputStream(file)) {
            // noinspection ResultOfMethodCallIgnored
            fis.skip(startFrom);
            dataLen = fis.read(buff);
          }
          res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mime, new ByteArrayInputStream(buff, 0, dataLen), dataLen);
          res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
          res.addHeader("ETag", etag);
        }
      } else {
        if (etag.equals(header.get("if-none-match")))
          res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
        else {
          // noinspection IOStreamConstructor
          res = newChunkedResponse(Response.Status.OK, mime, new FileInputStream(file));
          res.addHeader("Content-Length", "" + fileLen);
          res.addHeader("ETag", etag);
        }
      }
    } catch (IOException ioe) {
      res = newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
    }
    return res;
  }
}
