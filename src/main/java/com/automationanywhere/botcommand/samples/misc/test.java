package com.automationanywhere.botcommand.samples.misc;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.HTTPRequest;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        String name = "short_description";
        String value = "Why is my computer broken";
        String sys_id = "1b2aca391b1230104b7a0f26624bcb3e";
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

        String response = HTTPRequest.oAuthMethod(url, clientId, clientSecret, username, password);
        Object obj = new JSONParser().parse(response);
        JSONObject details = (JSONObject) obj;
        String token = (String) details.get("access_token");
        String errorMessage = "";
        JSONObject result = null;


            ServiceNowActions.downloadAttachment(url, token, sys_id, filePath);
            /*Object object = new JSONParser().parse(recordCreated);
            JSONObject json_resp = (JSONObject) object;
            if (json_resp.containsKey("error")) {
                JSONObject errorObject =  (JSONObject) json_resp.get("error");
                errorMessage = errorObject.get("message").toString() + ", details: " + errorObject.get("detail").toString();
                throw new BotCommandException("ServiceNow did not find the record at the specific sys_id. " + errorMessage);
            }
            result = (JSONObject) json_resp.get("result");


        String sys_id_result = result.get("sys_id").toString();
        System.out.println(sys_id_result);*/

        //String response32 = ServiceNowActions.deleteRecord(url, "incident", token, "jibbersih");

        //System.out.println(response32);


    }


}



