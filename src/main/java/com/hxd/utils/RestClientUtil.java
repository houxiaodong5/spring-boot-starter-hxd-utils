package com.hxd.utils;

import com.alibaba.fastjson.JSON;
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

import java.io.IOException;
import java.util.Collections;

public class RestClientUtil {
    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    public  static void main(String[]args){

        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("es", "pwd"));
        RestClient restClient = RestClient.builder(new HttpHost("es-cn-***.public.elasticsearch.aliyuncs.com", 9200))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }).build();
        try {
            String method = "PUT";
            String endpoint = "/book";
            Response response = restClient.performRequest(method, endpoint);
            System.out.println(EntityUtils.toString(response.getEntity()));
            //index a document
           /* HttpEntity entity = new NStringEntity("{\n\"user\" : \"kimchy\"\n}", ContentType.APPLICATION_JSON);
            Response indexResponse = restClient.performRequest(
                    "PUT",
                    "/index/type/123",
                    Collections.<String, String>emptyMap(),
                    entity);
*/

            //search a document
            //Response response = restClient.performRequest("GET", "/index/type/123",
            Response response2 = restClient.performRequest("GET", "/.kibana",
                    Collections.singletonMap("pretty", "true"));
            System.out.println(EntityUtils.toString(response2.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}