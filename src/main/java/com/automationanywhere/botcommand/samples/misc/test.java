package com.automationanywhere.botcommand.samples.misc;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.HTTPRequest;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class test {

    public static void main(String[] args) throws Exception {
        //String auth = "Basic YWRtaW46QVlkSWc3dzFkWEt2";
        //String usernameColonPassword = "admin:AYdIg7w1dXKv";
        //String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
        List<Value> list = new ArrayList<>();
        String url = "https://dev103260.service-now.com/";
        String clientId = "93430bfe3b750110cdb899870a901337";
        String clientSecret = "FBb6p`,L6}";
        String username = "admin";
        String password = "8rsZoMb1GkUU";
        String table = "incident";
        String name = "opened";
        String value = "opened_at";
        String sys_id = "e329de99731423002728660c4cf6a73c";
        String folderPath = "C:\\Users\\jamesdickson\\Pictures\\";
        //Double limit = 5.0;
        //Integer iLimit = limit.intValue();
        //String sLimit = iLimit.toString();
        Map<String, StringValue> values = new HashMap<>();
        values.put("description", new StringValue("short_description"));
        //values.put("comments", new StringValue("This is a new comment 2"));
        //values.put("urgency", "2");

        //url = url + "api/now/table/incident/" + sys_id;

        //String method = "GET";
//        StringBuilder jsonBodyStr = new StringBuilder("{");
//        for (Map.Entry<String,String> entry : values.entrySet()){
//            jsonBodyStr.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\",");
//        }
//        jsonBodyStr = new StringBuilder(jsonBodyStr.substring(0, jsonBodyStr.length() - 1)); //remove last comma
//        jsonBodyStr.append("}");
//        System.out.println(jsonBodyStr);

        String response = HTTPRequest.oAuthMethod(url, clientId, clientSecret, username, password);
        //System.out.println(response);
        Object obj = new JSONParser().parse(response);
        JSONObject details = (JSONObject) obj;
        String token = (String) details.get("access_token");
        String errorMessage = "";
        //System.out.println(token);

        String triggerResp = ServiceNowActions.triggerOnRecord(url, "incident", token);
        System.out.println(triggerResp);
        Object obj2 = new JSONParser().parse(triggerResp);
        JSONObject json_result = (JSONObject) obj2;
        JSONArray resultArray = (JSONArray) json_result.get("result");
        JSONObject time = (JSONObject) resultArray.get(0);
        String opened_at = (String) time.get("opened_at");
        ZonedDateTime dt2 = ZonedDateTime.parse(opened_at,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC")));
        System.out.println("Now: "+ ZonedDateTime.now());
        System.out.println("incident creation: "+dt2);
        System.out.println(dt2.isAfter(ZonedDateTime.now().minusSeconds(15)));
//
//        String respUpdate = ServiceNowActions.getRecord(url, token, table, sys_id, values);
//        JSONObject result;
//        try {
//
//            Object obj2 = new JSONParser().parse(respUpdate);
//            JSONObject json_resp = (JSONObject) obj2;
//            result = (JSONObject) json_resp.get("result");
//            System.out.println(result);
//        } catch(Exception e){
//            throw new BotCommandException("ServiceNow didn't respond with a record. Exception caught " + e);
//        }
//        Map<String, Value> ResMap = new LinkedHashMap();
//
//        if(values.size()>0){
//            for (Map.Entry<String,StringValue> entry : values.entrySet()){
//                try {
//                    //System.out.println(String.valueOf(entry.getValue()));
//
//                    ResMap.put(entry.getKey(), new StringValue(result.get(String.valueOf(entry.getValue())).toString()));
//                } catch (Exception e){
//                    throw new BotCommandException("The ServiceNow record returned doesn't contain one of the keys entered in the values. Please check the values entered. " + e);
//                }
//            }
//        }
//        System.out.println(ResMap);


//        String result;
//        try {
//            result = ServiceNowActions.watchIncident(url, token, sys_id);
//        } catch (IOException e) {
//            throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
//        } catch (ParseException e) {
//            throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
//        }
//        Object obj2 = null;
//        try {
//            obj2 = new JSONParser().parse(result);
//        } catch (ParseException e) {
//            throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
//        }
//        JSONObject json_obj = (JSONObject) obj2;
//        JSONObject json_result = (JSONObject) json_obj.get("result");
//        String sys_updated_on = (String) json_result.get("sys_updated_on");
//        ZonedDateTime dt2 = ZonedDateTime.parse(sys_updated_on,
//                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/Los_Angeles")));
//        System.out.println(dt2);
//
//        String comment;
//        System.out.println(json_result.get("comments"));
//        String description = json_result.get("short_description").toString();
//        String updated_by = json_result.get("sys_updated_by").toString();
//        if(!Objects.equals(json_result.get("comments").toString(), "")) {
//            String comments = json_result.get("comments").toString();
//            String[] arrOfStr = comments.split("\\n");
//            comment = arrOfStr[1];
//        } else {comment = "No comments exist on incident";}
//
//        System.out.println(comment);


        //String records = ServiceNowActions.getRecords(url, table, token, list, "");
        //System.out.println(records);

    }


}



