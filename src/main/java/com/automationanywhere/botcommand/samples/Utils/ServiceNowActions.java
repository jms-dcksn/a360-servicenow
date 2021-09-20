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
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceNowActions {

    public static String getRecord(String url, String table, String sys_id, String token, List<Value> fields) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "/" + sys_id + "?sysparm_fields=";

        if (fields != null && fields.size() > 0) {
            for (Value element : fields) {
                Map<String, Value> customValuesMap = ((DictionaryValue) element).get();
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue) customValuesMap.get("VALUE")).get();
                if (!value.equals(null)) {
                    url = url + value + "%2C";
                }
            }
            //remove last comma
            url = url.substring(0, url.length() - 3);
        }
        String method = "GET";
        String auth = "Bearer " + token;
        String response = "";
        response = HTTPRequest.Request(url, method, auth);
        return response;
    }

    public static String getRecords(String url, String table, String token, List<Value> fields, String limit, String query) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "?sysparm_query=" + query + "&sysparm_fields=";
        //add fields as query params
        if (fields != null && fields.size() > 0) {
            for (Value element : fields) {
                Map<String, Value> customValuesMap = ((DictionaryValue) element).get();
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue) customValuesMap.get("VALUE")).get();
                if (!value.equals(null)) {
                    url = url + value + "%2C";
                }
            }
            //remove last comma
            url = url.substring(0, url.length() - 3);
        }
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

    public static String insertRecord(String url, String table, String token, List<Value> fields) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "?sysparm_fields=sys_id";
        JSONObject jsonBody = new JSONObject();

        if(fields!=null && fields.size()>0){
            for (Value element : fields){
                Map<String, Value> customValuesMap = ((DictionaryValue)element).get();
                String name = customValuesMap.containsKey("NAME") ? ((StringValue)customValuesMap.get("NAME")).get() : "";
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue)customValuesMap.get("VALUE")).get();
                if(!value.equals(null)){
                    jsonBody.put(name, value);
                }
            }
        }
        String auth = "Bearer " + token;
        String response = "";
        response = HTTPRequest.SEND(auth, url, "POST", jsonBody.toString());
        return response;
    }

    public static String modifyRecord(String url, String method, String table, String sys_id, String token, List<Value> fields) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "/" + sys_id;
        JSONObject jsonBody = new JSONObject();

        if(fields!=null && fields.size()>0){
            for (Value element : fields){
                Map<String, Value> customValuesMap = ((DictionaryValue)element).get();
                String name = customValuesMap.containsKey("NAME") ? ((StringValue)customValuesMap.get("NAME")).get() : "";
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue)customValuesMap.get("VALUE")).get();
                if(!value.equals(null)){
                    jsonBody.put(name, value);
                }
            }
        }
        String auth = "Bearer " + token;
        String response = "";

        response = HTTPRequest.SEND(auth, url, method, jsonBody.toString());
        return response;
    }

    public static String updateRecord(String url, String token, String table, String sys_id, List<Value> list) throws IOException, ParseException {
        url = url + "api/now/table/" + table + "/" + sys_id;
        JSONObject jsonBody = new JSONObject();

        if(list!=null && list.size()>0){
            for (Value element : list){
                Map<String, Value> customValuesMap = ((DictionaryValue)element).get();
                String name = customValuesMap.containsKey("NAME") ? ((StringValue)customValuesMap.get("NAME")).get() : "";
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue)customValuesMap.get("VALUE")).get();
                if(!value.equals(null)){
                    jsonBody.put(name, value);
                }
            }
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
        String auth = "Basic " + token;
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