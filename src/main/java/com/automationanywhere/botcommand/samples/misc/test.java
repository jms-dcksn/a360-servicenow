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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        String table = "incident";
        String name = "timestamp";
        String value = "opened_at";
        String sys_id = "e8caedcbc0a80164017df472f39eaed1";
        String filePath = "C:\\Users\\jamesdickson\\Pictures\\thumbnail2.png";
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
        ZonedDateTime lastRun = ZonedDateTime.now();

        String response = HTTPRequest.oAuthMethod(url, clientId, clientSecret, username, password);
        Object obj = new JSONParser().parse(response);
        JSONObject details = (JSONObject) obj;
        String token = (String) details.get("access_token");
        String errorMessage = "";

        String result = ServiceNowActions.deleteAttachment(url, token, "009c53e0bf1101007a6d257b3f0739c0");
        System.out.println(result);

        /*String lastIncident = ServiceNowActions.triggerOnRecord(url, "incident", token);
        System.out.println(lastIncident);
        Object obj2;
        try {
            obj2 = new JSONParser().parse(lastIncident);
        } catch (ParseException e) {
            throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
        }
        JSONObject json_result = (JSONObject) obj2;
        JSONArray resultArray = (JSONArray) json_result.get("result");
        JSONObject time = (JSONObject) resultArray.get(0);
        String opened_at = (String) time.get("opened_at");
        Integer incidentPriority = Integer.parseInt(time.get("priority").toString());
        System.out.println(opened_at);
        System.out.println(incidentPriority);
        ZonedDateTime dt2 = ZonedDateTime.parse(opened_at,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC")));
        if(dt2.isAfter(lastRun) && incidentPriority <= 5) {
            System.out.println("Triggered");
        } else { System.out.println("Not triggered"); }*/
        //String records = ServiceNowActions.getRecords(url, table, token, list, "");
        //System.out.println(records);

    }


}



