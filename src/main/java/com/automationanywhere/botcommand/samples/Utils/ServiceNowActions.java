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

        public static String getRecord(String url, String table, String sys_id, String token, List<Value> fields){
            url = url + "api/now/table/" + table + "/" + sys_id + "?sysparm_fields=";

            if(fields!=null && fields.size()>0){
                for (Value element : fields){
                    Map<String, Value> customValuesMap = ((DictionaryValue)element).get();
                    String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue)customValuesMap.get("VALUE")).get();
                    if(!value.equals(null)){
                        url = url + value + "%2C";
                    }
                }
                //remove last comma
                url = url.substring(0, url.length()-3);
            }

            String method = "GET";
            String auth = "Bearer " + token;
            String response = "";
            try {
                response = HTTPRequest.Request(url, method, auth);
                if (response.contains("An error occurred")) {
                    throw new BotCommandException(response);
                }
            }
            catch(Exception e){
                throw new BotCommandException("Something went wrong with the request. Please try again." + response);
            }
            return response;
    }

    public static String getRecords(String url, String table, String token, List<Value> fields){
        url = url + "api/now/table/" + table + "?sysparm_fields=";

        if(fields!=null && fields.size()>0){
            for (Value element : fields){
                Map<String, Value> customValuesMap = ((DictionaryValue)element).get();
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue)customValuesMap.get("VALUE")).get();
                if(!value.equals(null)){
                    url = url + value + "%2C";
                }
            }
            //remove last comma
            url = url.substring(0, url.length()-3);
        }

        String method = "GET";
        String auth = "Bearer " + token;
        String response = "";
        try {
            response = HTTPRequest.Request(url, method, auth);
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("Something went wrong with the request. Please try again." + response);
        }
        return response;
    }

    public static JSONObject CreateProject(String token, String projectName, Long lParentId, Boolean favorite) throws ParseException {

        String url = "https://api.todoist.com/rest/v1/projects";
        String auth = "Bearer " + token;
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("name", projectName);
        jsonBody.put("parent_id", lParentId);
        jsonBody.put("favorite", favorite);
        String response = "";

        try {
            response = HTTPRequest.POST(auth, url, jsonBody.toString());
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("An error occurred when attempting to reach the server. Please try again." + response);
        }

        Object obj = new JSONParser().parse(response);
        JSONObject details = (JSONObject) obj;
        return details;
    }

    public static void UpdateProject(String token, String projectId, String projectName, Boolean favorite) {

        String url = "https://api.todoist.com/rest/v1/projects/" + projectId;
        String auth = "Bearer " + token;
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("name", projectName);
        jsonBody.put("favorite", favorite);
        String response = "";

        try {
            response = HTTPRequest.POST(auth, url, jsonBody.toString());
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("An error occurred when attempting to reach the server. Please try again." + response);
        }
    }


    public static void DeleteProject(String token, String projectId){

        String url = "https://api.todoist.com/rest/v1/projects/" + projectId;
        String auth = "Bearer " + token;
        String response = "";

        try {
            response = HTTPRequest.Request(url, "DELETE", auth);
            if (response.contains("An error occurred")) {
                throw new BotCommandException(response);
            }
        }
        catch(Exception e){
            throw new BotCommandException("Something went wrong with the request to Todoist server. Please try again. " + response);
        }
    }
}
