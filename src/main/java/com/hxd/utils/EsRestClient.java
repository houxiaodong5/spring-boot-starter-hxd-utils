package com.hxd.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 阿里云ES连接工具
 */
@Service
public class EsRestClient {
    @Autowired
    private ESProperties esProperties;

    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String DELETE = "DELETE";
    //public static final String HOST = "es-cn-***.public.elasticsearch.aliyuncs.com";
   /* private String host = esProperties.getHost();
    private static String username = esProperties.getUsername();
    private String password = esProperties.getPassword();
    //public static final String HOST = "localhost";
    public int port = esProperties.getPort();*/
    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    /**
     * 带有安全校验的ES客户端连接
     */
    public RestClient getClientWithCheck() {
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(esProperties.getUsername(), esProperties.getPassword()));
        RestClient restClient = RestClient.builder(new HttpHost(esProperties.getHost(), esProperties.getPort()))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }).build();
        return restClient;
    }

    /**
     * 无安全校验的ES客户端连接
     */
    public RestClient getClientNoCheck() {
        return RestClient.builder(new HttpHost(esProperties.getHost(), esProperties.getPort())).setMaxRetryTimeoutMillis(6000).build();
    }

    public static String addIndex(RestClient restClient, String index) {
        //RestClient restClient = getClient();
        try {
            Response response = restClient.performRequest("PUT", "/" + index);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String sendGet(RestClient restClient, String index, String type, String query) {
        //RestClient restClient = getClient(ip, port);
        String rs = null;
        try {
            HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
            String endpoint = "/" + index + "/" + type + "/_search";
            if (StringUtils.isBlank(type)) {
                endpoint = "/" + index + "/_search";
            }
            Response response = restClient.performRequest(GET, endpoint, Collections.singletonMap("pretty", "true"), entity);
            rs = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(restClient);
        }
        return rs;
    }

    public static String sendPost(RestClient restClient, List<String> indexs, List<String> types, String query) {
        //RestClient restClient = getClient();
        String rs = null;
        try {
            String index = StringUtils.join(indexs, ",");
            String type = "/";
            if (Objects.nonNull(types) && !types.isEmpty()) {
                type += StringUtils.join(types, ",") + "/";
            }
            HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
            String endpoint = "/" + index + type + "_search";
            Response response = restClient.performRequest(POST, endpoint, Collections.singletonMap("pretty", "true"), entity);
            rs = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(restClient);
        }
        return rs;
    }

    public static String sendPut(RestClient restClient, String index, String type, String id, String data) {
        //RestClient restClient = getClient(ip, port);
        String rs = null;
        try {
            HttpEntity entity = new NStringEntity(data, ContentType.APPLICATION_JSON);
            String requestType = POST;
            String endpoint = "/" + index + "/" + type;
            if (StringUtils.isNotBlank(id)) {
                requestType = PUT;
                endpoint = "/" + index + "/" + type + "/" + id;
            }
            Response response = restClient.performRequest(requestType, endpoint, Collections.singletonMap("pretty", "true"), entity);
            rs = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(restClient);
        }
        return rs;
    }

    public static String sendDelete(RestClient restClient, String index, String type) {
        return sendDelete(restClient, index, type, null);
    }

    public static String sendDelete(RestClient restClient, String index, String type, String id) {

        String rs = null;
        try {
            String endpoint = "/" + index + "/" + type + "/" + id;
            if (StringUtils.isBlank(id)) {
                endpoint = "/" + index + "/" + type;
            } else if (StringUtils.isBlank(type)) {
                endpoint = "/" + index;
            }
            Response response = restClient.performRequest(DELETE, endpoint);
            rs = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(restClient);
        }
        return rs;
    }

    public static boolean sendHead(RestClient restClient, String index, String type) {
        int code = 200;
        try {
            String endpoint = "/" + index + "/" + type;
//       String endpoint = "/"+index+"/_mapping/"+type;//5.x
            if (StringUtils.isBlank(type)) {
                endpoint = "/" + index;
            }
            Response response = restClient.performRequest(HEAD, endpoint);//200存在，404不存在
            code = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(restClient);
        }
        return code == 200 ? true : false;
    }

    public static void close(RestClient restClient) {
        if (Objects.nonNull(restClient)) {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addDataToES(Object data, String type, Long id, RestClient client) {
        //RestClient client = getClient();
        try {
            String jsonStr = JSON.toJSONString(data);
            HttpEntity entity = new NStringEntity(jsonStr, ContentType.APPLICATION_JSON);
            String endpoint = "/esdata/" + type + "/" + id;
            client.performRequest("PUT", endpoint, Collections.emptyMap(), entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

