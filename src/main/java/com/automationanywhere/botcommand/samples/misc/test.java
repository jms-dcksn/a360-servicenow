package com.automationanywhere.botcommand.samples.misc;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.HTTPRequest;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
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
        String url = "https://dev117003.service-now.com/";
        String clientId = "c6385d0065c67010c350483f23f85210";
        String clientSecret = ",|cblfsVD}";
        String username = "admin";
        String password = "AYdIg7w1dXKv";
        String table = "incint";
        String name = "opened";
        String value = "opened_at";
        String sys_id = "9b7bf4591b3230104b7a0f26624bcb2d";
        String folderPath = "C:\\Users\\jamesdickson\\Pictures\\";
        //Double limit = 5.0;
        //Integer iLimit = limit.intValue();
        //String sLimit = iLimit.toString();
        Map<String, Value> map = new HashMap<>();
        map.put("NAME", new StringValue(name));
        list.add(new DictionaryValue(map));
        map.put("VALUE", new StringValue(value));
        list.add(new DictionaryValue(map));

        //url = url + "api/now/table/incident/" + sys_id;

        //String method = "GET";

        String response = HTTPRequest.oAuthMethod(url, clientId, clientSecret, username, password);
        Object obj = new JSONParser().parse(response);
        JSONObject details = (JSONObject) obj;
        String token = (String) details.get("access_token");
        String errorMessage = "";


        String result;
        try {
            result = ServiceNowActions.watchIncident(url, token, sys_id);
        } catch (IOException e) {
            throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
        } catch (ParseException e) {
            throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
        }
        Object obj2 = null;
        try {
            obj2 = new JSONParser().parse(result);
        } catch (ParseException e) {
            throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
        }
        JSONObject json_obj = (JSONObject) obj2;
        JSONObject json_result = (JSONObject) json_obj.get("result");
        String sys_updated_on = (String) json_result.get("sys_updated_on");
        ZonedDateTime dt2 = ZonedDateTime.parse(sys_updated_on,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/Los_Angeles")));
        System.out.println(dt2);

        String comment;
        System.out.println(json_result.get("comments"));
        String description = json_result.get("short_description").toString();
        String updated_by = json_result.get("sys_updated_by").toString();
        if(!Objects.equals(json_result.get("comments").toString(), "")) {
            String comments = json_result.get("comments").toString();
            String[] arrOfStr = comments.split("\\n");
            comment = arrOfStr[1];
        } else {comment = "No comments exist on incident";}

        System.out.println(comment);


        //String records = ServiceNowActions.getRecords(url, table, token, list, "");
        //System.out.println(records);

    }


}



