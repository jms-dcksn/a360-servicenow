package com.automationanywhere.botcommand.samples.Utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceNowActions {

    public static String getRecord(String url, String table, String sys_id, String token, List<Value> fields) {
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
        try {
            response = HTTPRequest.Request(url, method, auth);
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        } catch (Exception e) {
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return response;
    }

    public static String getRecords(String url, String table, String token, List<Value> fields, String limit) {
        url = url + "api/now/table/" + table + "?sysparm_fields=";
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
        try {
            response = HTTPRequest.Request(url, method, auth);
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        } catch (Exception e) {
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return response;
    }

    public static String insertRecord(String url, String table, String token, List<Value> fields){
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
        try {
            response = HTTPRequest.SEND(auth, url, "POST", jsonBody.toString());
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return response;
    }

    public static String updateRecord(String url, String table, String sys_id, String token, List<Value> fields){
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
        try {
            response = HTTPRequest.SEND(auth, url, "PUT", jsonBody.toString());
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return response;
    }

    public static String deleteRecord(String url, String table, String token, String sys_id){
        url = url + "api/now/table/" + table + "/" + sys_id;
        //JSONObject jsonBody = new JSONObject();
        String auth = "Bearer " + token;
        String response = "";
        try {
            response = HTTPRequest.Request(url, "DELETE", auth);
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return "Record deleted";
    }

    public static String addAttachment(String url, String table, String token, String sys_id, String filePath){
        url = url + "api/now/attachment/upload";
        //JSONObject jsonBody = new JSONObject();
        String auth = "Bearer " + token;
        String response = "";
        try {
            response = HTTPRequest.attachFile(url, auth, table, sys_id, filePath);
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return response;
    }
}