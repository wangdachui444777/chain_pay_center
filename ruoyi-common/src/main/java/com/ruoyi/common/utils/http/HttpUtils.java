package com.ruoyi.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.http.MediaType;

/**
 * 通用 HTTP 发送工具类（增强版）
 *
 * @author ruoyi
 */
public class HttpUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    // 默认超时时间（毫秒）
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private static final int DEFAULT_READ_TIMEOUT = 50000;

    /**
     * 发送 GET 请求
     *
     * @param url 请求地址
     * @return 响应结果
     */
    public static String sendGet(String url) {
        return sendGet(url, (String) null);
    }

    /**
     * 发送 GET 请求
     *
     * @param url   请求地址
     * @param param 请求参数，格式：name1=value1&name2=value2
     * @return 响应结果
     */
    public static String sendGet(String url, String param) {
        return sendGet(url, param, Constants.UTF8);
    }

    /**
     * 发送 GET 请求
     *
     * @param url         请求地址
     * @param param       请求参数
     * @param contentType 编码类型
     * @return 响应结果
     */
    public static String sendGet(String url, String param, String contentType) {
        return sendGet(url, param, null, contentType);
    }

    /**
     * 发送 GET 请求（支持自定义 Headers）
     *
     * @param url         请求地址
     * @param param       请求参数
     * @param headers     自定义请求头
     * @param contentType 编码类型
     * @return 响应结果
     */
    public static String sendGet(String url, String param, Map<String, String> headers, String contentType) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
            log.info("sendGet - {}", urlNameString);

            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();

            // 设置默认请求头
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            // 设置自定义请求头
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 设置超时
            connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            log.debug("recv - {}", result);
        } catch (ConnectException e) {
            log.error("调用 HttpUtils.sendGet ConnectException, url=" + url + ", param=" + param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用 HttpUtils.sendGet SocketTimeoutException, url=" + url + ", param=" + param, e);
        } catch (IOException e) {
            log.error("调用 HttpUtils.sendGet IOException, url=" + url + ", param=" + param, e);
        } catch (Exception e) {
            log.error("调用 HttpUtils.sendGet Exception, url=" + url + ", param=" + param, e);
        } finally {
            closeQuietly(in);
        }
        return result.toString();
    }

    /**
     * 发送 GET 请求（支持 Map 参数）
     *
     * @param url    请求地址
     * @param params 请求参数 Map
     * @return 响应结果
     */
    public static String sendGet(String url, Map<String, Object> params) {
        return sendGet(url, params, null);
    }

    /**
     * 发送 GET 请求（支持 Map 参数和自定义 Headers）
     *
     * @param url     请求地址
     * @param params  请求参数 Map
     * @param headers 自定义请求头
     * @return 响应结果
     */
    public static String sendGet(String url, Map<String, Object> params, Map<String, String> headers) {
        String paramString = buildQueryString(params);
        return sendGet(url, paramString, headers, Constants.UTF8);
    }

    /**
     * 发送 POST 请求
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return 响应结果
     */
    public static String sendPost(String url, String param) {
        return sendPost(url, param, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 发送 POST 请求
     *
     * @param url         请求地址
     * @param param       请求参数
     * @param contentType 内容类型
     * @return 响应结果
     */
    public static String sendPost(String url, String param, String contentType) {
        return sendPost(url, param, null, contentType);
    }

    /**
     * 发送 POST 请求（支持自定义 Headers）
     *
     * @param url         请求地址
     * @param param       请求参数
     * @param headers     自定义请求头
     * @param contentType 内容类型
     * @return 响应结果
     */
    public static String sendPost(String url, String param, Map<String, String> headers, String contentType) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
           // log.info("sendPost - {}", url);
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();

            // 设置默认请求头
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", contentType);

            // 设置自定义请求头
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 设置超时
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            //log.debug("recv - {}", result);
        } catch (ConnectException e) {
            log.error("调用 HttpUtils.sendPost ConnectException, url=" + url + ", param=" + param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用 HttpUtils.sendPost SocketTimeoutException, url=" + url + ", param=" + param, e);
        } catch (IOException e) {
            log.error("调用 HttpUtils.sendPost IOException, url=" + url + ", param=" + param, e);
        } catch (Exception e) {
            log.error("调用 HttpUtils.sendPost Exception, url=" + url + ", param=" + param, e);
        } finally {
            closeQuietly(out);
            closeQuietly(in);
        }
        return result.toString();
    }

    /**
     * 发送 POST 请求（支持 Map 参数，表单格式）
     *
     * @param url    请求地址
     * @param params 请求参数 Map
     * @return 响应结果
     */
    public static String sendPost(String url, Map<String, Object> params) {
        return sendPost(url, params, null);
    }

    /**
     * 发送 POST 请求（支持 Map 参数和自定义 Headers，表单格式）
     *
     * @param url     请求地址
     * @param params  请求参数 Map
     * @param headers 自定义请求头
     * @return 响应结果
     */
    public static String sendPost(String url, Map<String, Object> params, Map<String, String> headers) {
        String paramString = buildQueryString(params);
        return sendPost(url, paramString, headers, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 发送 POST 请求（JSON 格式）
     *
     * @param url  请求地址
     * @param json JSON 字符串
     * @return 响应结果
     */
    public static String sendPostJson(String url, String json) {
        return sendPostJson(url, json, null);
    }

    /**
     * 发送 POST 请求（JSON 格式，支持自定义 Headers）
     *
     * @param url     请求地址
     * @param json    JSON 字符串
     * @param headers 自定义请求头
     * @return 响应结果
     */
    public static String sendPostJson(String url, String json, Map<String, String> headers) {
        return sendPost(url, json, headers, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 发送 POST 请求（Map 转 JSON 格式）
     *
     * @param url    请求地址
     * @param params 请求参数 Map
     * @return 响应结果
     */
    public static String sendPostJsonMap(String url, Map<String, Object> params) {
        return sendPostJsonMap(url, params, null);
    }

    /**
     * 发送 POST 请求（Map 转 JSON 格式，支持自定义 Headers）
     *
     * @param url     请求地址
     * @param params  请求参数 Map
     * @param headers 自定义请求头
     * @return 响应结果
     */
    public static String sendPostJsonMap(String url, Map<String, Object> params, Map<String, String> headers) {
        String json = JSON.toJSONString(params);
        return sendPostJson(url, json, headers);
    }

    /**
     * 发送 SSL POST 请求
     */
    public static String sendSSLPost(String url, String param) {
        return sendSSLPost(url, param, null, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 发送 SSL POST 请求
     */
    public static String sendSSLPost(String url, String param, String contentType) {
        return sendSSLPost(url, param, null, contentType);
    }

    /**
     * 发送 SSL POST 请求（支持自定义 Headers）
     */
    public static String sendSSLPost(String url, String param, Map<String, String> headers, String contentType) {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + (param != null ? "?" + param : "");
        try {
            log.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());

            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();

            // 设置默认请求头
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-Type", contentType);

            // 设置自定义请求头
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 设置超时
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret;
            while ((ret = br.readLine()) != null) {
                if (ret != null && !"".equals(ret.trim())) {
                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            log.debug("recv - {}", result);
            conn.disconnect();
            br.close();
        } catch (ConnectException e) {
            log.error("调用 HttpUtils.sendSSLPost ConnectException, url=" + url + ", param=" + param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用 HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ", param=" + param, e);
        } catch (IOException e) {
            log.error("调用 HttpUtils.sendSSLPost IOException, url=" + url + ", param=" + param, e);
        } catch (Exception e) {
            log.error("调用 HttpUtils.sendSSLPost Exception, url=" + url + ", param=" + param, e);
        }
        return result.toString();
    }

    /**
     * 发送 SSL POST 请求（支持 Map 参数）
     */
    public static String sendSSLPost(String url, Map<String, Object> params) {
        return sendSSLPost(url, params, null);
    }

    /**
     * 发送 SSL POST 请求（支持 Map 参数和自定义 Headers）
     */
    public static String sendSSLPost(String url, Map<String, Object> params, Map<String, String> headers) {
        String paramString = buildQueryString(params);
        return sendSSLPost(url, paramString, headers, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 发送 SSL POST 请求（JSON 格式）
     */
    public static String sendSSLPostJson(String url, String json) {
        return sendSSLPostJson(url, json, null);
    }

    /**
     * 发送 SSL POST 请求（JSON 格式，支持自定义 Headers）
     */
    public static String sendSSLPostJson(String url, String json, Map<String, String> headers) {
        return sendSSLPost(url, json, headers, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 构建查询字符串（Map 转 URL 参数）
     *
     * @param params 参数 Map
     * @return 查询字符串（name1=value1&name2=value2）
     */
    private static String buildQueryString(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder queryString = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name());
                String value = entry.getValue() != null
                        ? URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8.name())
                        : "";
                queryString.append(key).append("=").append(value);
            }
        } catch (Exception e) {
            log.error("构建查询字符串失败", e);
        }
        return queryString.toString();
    }

    /**
     * 安全关闭流
     */
    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error("关闭流异常", e);
            }
        }
    }

    /**
     * 信任所有证书的 TrustManager
     */
    private static class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    /**
     * 信任所有主机名的 HostnameVerifier
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}