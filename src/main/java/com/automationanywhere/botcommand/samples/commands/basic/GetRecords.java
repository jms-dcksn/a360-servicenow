package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.SNOWServer;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListAddButtonLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEmptyLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEntryUnique;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListLabel;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThan;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NumberInteger;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 * @author James Dickson
 *
 */
@BotCommand
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "GetRecords",
        label = "Get Multiple Records",
        node_label = "Get list of records from Servicenow in {{sessionName}}",
        group_label = "Records",
        description = "Gets a list of records from the specified table",
        icon = "snow.svg",
        comment = true ,
        //background_color =  "#293E40",
        return_label = "Assign output to a list of dictionary variables",
        return_type = DataType.LIST,
        return_description = "Output Dictionary keys are specified in the entry list")

public class GetRecords {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public ListValue<DictionaryValue> action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,
            @Idx(index = "2", type = TEXT) @Pkg(label = "Table", default_value_type = STRING)
            @NotEmpty String table,
            @Idx(index = "3", type = DICTIONARY)
            @Pkg(label = "Values to return in output dictionary",
                    description = "The keys contained in this input Dictionary variable will define the keys of each " +
                            "output Dictionary variable inside the List. The values of this input should match a ServiceNow response " +
                            "key. The ServiceNow keys can be found in the API documentation for ServiceNow. E.G. input " +
                            "Dictionary key: description, value: short_description - this implies that the output Dictionary" +
                            " will contain a key of 'description' and the associated value will be the ServiceNow response value for " +
                            "key 'short_description'.")
                    //Header of the entry form
                    Map<String, StringValue> values,
            @Idx(index = "4", type = AttributeType.NUMBER) @Pkg(label = "Limit", default_value_type = DataType.NUMBER,
                    description = "Limits the total number of records returned")
            @GreaterThan("0")
            @NumberInteger
            Double limit,
            @Idx(index = "5", type = TEXT) @Pkg(label = "Encoded Query String (i.e. sysparm_query)", default_value_type = STRING,
            description = "Encoded query string can be obtained by copying from a list filter in ServiceNow. Please see " +
                    "ServiceNow documentation for more info. https://docs.servicenow.com/bundle/rome-platform-user-interface/page/use/using-lists/concept/c_EncodedQueryStrings.html")
            String query
    ) throws IOException, ParseException {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        int iLimit = limit.intValue();
        String sLimit = Integer.toString(iLimit);
        String response = "";
        JSONArray resultArray;
        try {
            response = ServiceNowActions.getRecords(url, table, token, values, sLimit, query);
            Object obj = new JSONParser().parse(response);
            JSONObject json_resp = (JSONObject) obj;
            resultArray = (JSONArray) json_resp.get("result");
        } catch(Exception e){
            throw new BotCommandException("ServiceNow didn't respond with a record. Exception caught: " + e);
        }

        ListValue<DictionaryValue> returnValue = new ListValue<>();
        List<Value> dictionaryObjects = new ArrayList<>();

        for (int i=0; i<resultArray.size(); i++) {
            Map<String, Value> ResMap = new LinkedHashMap();
            JSONObject arrayElement = (JSONObject) resultArray.get(i);
            if (values.size() > 0) {
                for (Map.Entry<String,StringValue> entry : values.entrySet()){
                    try {
                        ResMap.put(entry.getKey(), new StringValue(arrayElement.get(String.valueOf(entry.getValue())).toString()));
                    } catch (Exception e){
                        throw new BotCommandException("The ServiceNow record returned doesn't contain one of the keys entered in the values. Please check the values entered. " + e);
                    }
                }
                dictionaryObjects.add(new DictionaryValue(ResMap));
            }
        }

        returnValue.set(dictionaryObjects);
        return returnValue;
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
