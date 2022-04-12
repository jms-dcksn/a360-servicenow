package com.automationanywhere.botcommand.samples.Utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class ServiceNowActions {

    public static String getRecord(String url, String token, String table, String sys_id, Map<String, StringValue> values) throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder(url + "api/now/table/" + table + "/" + sys_id + "?sysparm_fields=");
        for (Map.Entry<String,StringValue> entry : values.entrySet()) {
                String value = String.valueOf(entry.getValue());
                urlBuilder.append(value).append("%2C");
            }
        url = urlBuilder.toString();
        //remove last comma
        url = url.substring(0, url.length() - 3);
        System.out.println(url);
        String method = "GET";
        String auth = "Bearer " + token;
        String response = "";
        response = HTTPRequest.Request(url, method, auth);
        return response;
    }

    public static String getRecords(String url, String table, String token, Map<String, StringValue> values, String limit, String query) throws IOException, ParseException {
        StringBuilder urlBuilder = new StringBuilder(url + "api/now/table/" + table + "?sysparm_query=" + query + "&sysparm_fields=");
        //add fields as query params
        for (Map.Entry<String,StringValue> entry : values.entrySet()) {
            String value = String.valueOf(entry.getValue());
            urlBuilder.append(value).append("%2C");
        }
        url = urlBuilder.toString();
        //remove last comma
        url = url.substring(0, url.length() - 3);
        url = url + "&sysparm_limit=" + limit; //add record limit as query param
        String method = "GET";
        String auth = "Bearer " + token;
        String response = "";
        response = HTTPRequest.Request(url, method, auth);
        return response;
    }

    public static String triggerOnRecord(String url, String table, String token) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "?sysparm_query=active=true^ORDERBYDESCopened_at&sysparm_fields=opened_at,priority,number,short_description,sys_id&sysparm_limit=1";
        //add fields as query params
        String method = "GET";
        String auth = "Bearer " + token;
        String response = "";

        response = HTTPRequest.Request(url, method, auth);

        return response;
    }

    public static String watchIncident(String url, String token, String sys_id) throws IOException, ParseException {
        url = url + "api/now/table/incident/" + sys_id + "?sysparm_fields=sys_updated_on,sys_updated_by,short_description,comments&sysparm_display_value=true";
        //add fields as query params
        String method = "GET";
        String auth = "Bearer " + token;
        String response = "";

        response = HTTPRequest.Request(url, method, auth);

        return response;
    }

    public static String insertRecord(String url, String table, String token, Map<String, StringValue> values) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "?sysparm_fields=sys_id";
        JSONObject jsonBody = new JSONObject();

        for (Map.Entry<String,StringValue> entry : values.entrySet()){
            String currentValue = String.valueOf(entry.getValue().get());
            jsonBody.put(entry.getKey(), currentValue);
        }
        String auth = "Bearer " + token;
        String response = "";
        response = HTTPRequest.SEND(auth, url, "POST", jsonBody.toString());
        return response;
    }

    public static String modifyRecord(String url, String method, String table, String sys_id, String token, Map<String, StringValue> values) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "/" + sys_id;
        JSONObject jsonBody = new JSONObject();

        for (Map.Entry<String,StringValue> entry : values.entrySet()){
            String currentValue = String.valueOf(entry.getValue().get());
            jsonBody.put(entry.getKey(), currentValue);
        }
        String auth = "Bearer " + token;
        String response = "";

        response = HTTPRequest.SEND(auth, url, method, jsonBody.toString());
        return response;
    }

    public static String updateRecord(String url, String token, String table, String sys_id, Map<String, StringValue> values) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "/" + sys_id;
        JSONObject jsonBody = new JSONObject();
        for (Map.Entry<String,StringValue> entry : values.entrySet()){
            String currentValue = String.valueOf(entry.getValue().get());
            jsonBody.put(entry.getKey(), currentValue);
        }
        String auth = "Bearer " + token;
        String response = "";
        response = HTTPRequest.httpPatch(url, auth, jsonBody.toString());
        return response;
    }

    public static String deleteRecord(String url, String table, String token, String sys_id){
        url = url + "api/now/table/" + table + "/" + sys_id;
        //JSONObject jsonBody = new JSONObject();
        String auth = "Bearer " + token;
        String response = "";
        try {
            response = HTTPRequest.Request(url, "DELETE", auth);
        }
        catch(Exception e){
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return "Record deleted";
    }

    public static String getAttachmentDetails(String url, String sys_id, String token) throws IOException, ParseException {
        url = url + "api/now/attachment/" + sys_id;
        String method = "GET";
        String auth = "Bearer " + token;
        String response =  HTTPRequest.Request (url,method,auth);
        return response;
    }

    public static String addAttachment(String url, String table, String token, String sys_id, String filePath) throws IOException, ParseException {
        url = url + "api/now/attachment/upload";
        //JSONObject jsonBody = new JSONObject();
        String auth = "Bearer " + token;
        String response = "";

        response = HTTPRequest.attachFile(url, auth, table, sys_id, filePath);
        return response;
    }

    public static void downloadAttachment(String url, String token, String sys_id, String filePath) throws IOException {
        url = url + "api/now/attachment/" + sys_id + "/file";
        //JSONObject jsonBody = new JSONObject();
        String auth = "Bearer " + token;
        String response = "";

        HTTPRequest.getFile(url, auth, filePath);
    }

    public static String deleteAttachment(String url, String token, String sys_id) throws IOException, ParseException {
        url = url + "api/now/attachment/" + sys_id;
        //JSONObject jsonBody = new JSONObject();
        String auth = "Bearer " + token;
        String response = "";

        response = HTTPRequest.Request(url, "DELETE", auth);
        return response;
    }
}