package com.automationanywhere.botcommand.samples.Utils;
import com.automationanywhere.botcommand.exception.BotCommandException;
import okhttp3.*;
import org.apache.http.HttpEntity;
import java.io.FileOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {

    public static String Request(String url, String method, String auth) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method(method, null)
                .addHeader("Authorization", auth)
                //.addHeader("Cookie", "BIGipServerpool_dev117003=427929098.59456.0000; JSESSIONID=7F99F03A11EAD961AAFC4987A02DFA05; glide_session_store=6FFDE7AB1B2230104B7A0F26624BCBD4; glide_user_activity=U0N2MzorNXZYeFJncVo4eUNZZjZVMUhQZG5tOEhBdjBFa1BWdjo3b09lNkRqT016d2VJbTNHeVZBSENBQ3dmTDVlOTJ3ZURrUklwSU50blM4PQ==; glide_user_route=glide.73d6d0a9ea8868a1a5a301c9c850c8cf")
                .build();
        Response response = client.newCall(request).execute();
        String errorMessage = "Error";
        if(response.code() >= 400) {
            Object obj = new JSONParser().parse(response.body().string());
            JSONObject json_resp = (JSONObject) obj;
            if (json_resp.containsKey("error")) {
                JSONObject errorObject = (JSONObject) json_resp.get("error");
                if(errorObject.get("detail") != null){
                    errorMessage = errorObject.get("message").toString() + ", details: " + errorObject.get("detail").toString();
                } else {
                    errorMessage = errorObject.get("message").toString();
                }
                throw new BotCommandException("ServiceNow could not complete the action. " + errorMessage);
            }
            else {throw new BotCommandException("ServiceNow could not complete the action. " + response.code());}
        }
        return response.body().string();
    }


    public static String oAuthMethod(String url, String clientId, String clientSecret, String username, String password) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=password&client_id=" + clientId + "&client_secret=" + clientSecret + "&username=" + username + "&password=" + password);
        Request request = new Request.Builder()
                .url(url+"oauth_token.do")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        if(response.code() >= 400){
            Object obj = new JSONParser().parse(response.body().string());
            JSONObject json_resp = (JSONObject) obj;
            String errorMessage =  json_resp.get("error_description").toString();
            throw new BotCommandException("ServiceNow could not complete the action. " + errorMessage);
        }
        return response.body().string();
    }

    public static String SEND(String auth, String url, String method, String body) throws IOException, ParseException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .addHeader("Authorization", auth)
                //.addHeader("Content-Type", "multipart/form-data")
                //.addHeader("Cookie", "b=bqjwz6qwia1zsqok1ring9va3")
                .build();
        Response response = client.newCall(request).execute();
        //System.out.println(response.body().string());
        String errorMessage = "Error";
        if(response.code() >= 400) {
            Object obj = new JSONParser().parse(response.body().string());
            JSONObject json_resp = (JSONObject) obj;
            if (json_resp.containsKey("error")) {
                JSONObject errorObject = (JSONObject) json_resp.get("error");
                if(errorObject.get("detail") != null){
                    errorMessage = errorObject.get("message").toString() + ", details: " + errorObject.get("detail").toString();
                } else {
                    errorMessage = errorObject.get("message").toString();
                }
                throw new BotCommandException("ServiceNow could not complete the action. " + errorMessage);
            }
            else {throw new BotCommandException("ServiceNow could not complete the action. " + response.code());}
        }
        return response.body().string();
    }

    public static String httpPatch(String url, String auth, String jsonBody) throws IOException, ParseException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .method("PATCH", body)
                .addHeader("Authorization", auth)
                //.addHeader("Content-Type", "multipart/form-data")
                //.addHeader("Cookie", "b=bqjwz6qwia1zsqok1ring9va3")
                .build();
        Response response = client.newCall(request).execute();
        //System.out.println(response.body().string());

        String errorMessage = "Error";
        if(response.code() >= 400) {
            Object obj = new JSONParser().parse(response.body().string());
            JSONObject json_resp = (JSONObject) obj;
            if (json_resp.containsKey("error")) {
                JSONObject errorObject = (JSONObject) json_resp.get("error");
                if(errorObject.get("detail") != null){
                    errorMessage = errorObject.get("message").toString() + ", details: " + errorObject.get("detail").toString();
                } else {
                    errorMessage = errorObject.get("message").toString();
                }
                throw new BotCommandException("ServiceNow could not complete the action. " + errorMessage);
            }
            else {throw new BotCommandException("ServiceNow could not complete the action. " + response.code());}
        }
        return response.body().string();
    }

    public static String attachFile(String url, String auth, String table, String sysId, String filepath) throws IOException, ParseException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("table_name", table)
                .addFormDataPart("table_sys_id", sysId)
                .addFormDataPart("file", filepath,
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(filepath)))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", auth)
                //.addHeader("Content-Type", "multipart/form-data")
                //.addHeader("Cookie", "b=bqjwz6qwia1zsqok1ring9va3")
                .build();
        Response response = client.newCall(request).execute();
        String errorMessage = "Error";
        if(response.code() >= 400) {
            Object obj = new JSONParser().parse(response.body().string());
            JSONObject json_resp = (JSONObject) obj;
            if (json_resp.containsKey("error")) {
                JSONObject errorObject = (JSONObject) json_resp.get("error");
                if(errorObject.get("detail") != null){
                    errorMessage = errorObject.get("message").toString() + ", details: " + errorObject.get("detail").toString();
                } else {
                    errorMessage = errorObject.get("message").toString();
                }
                throw new BotCommandException("ServiceNow could not complete the action. " + errorMessage);
            }
            else {throw new BotCommandException("ServiceNow could not complete the action. " + response.code());}
        }
        return response.body().string();
    }

    public static void getFile(String url, String auth, String path) throws IOException{
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(url);

        request.addHeader("Accept", "*/*");
        request.addHeader("Authorization", auth);
        request.addHeader("Content-Type", "application/json");
        try (CloseableHttpResponse response =  httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (FileOutputStream outstream = new FileOutputStream(path)) {
                    entity.writeTo(outstream);
                }
            }
        }

    }
}
