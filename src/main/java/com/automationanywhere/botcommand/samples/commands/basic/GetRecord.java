package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.bot.service.Bot;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.samples.Utils.HTTPRequest;
import com.automationanywhere.botcommand.samples.Utils.SNOWServer;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListAddButtonLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEmptyLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEntryUnique;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListLabel;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.DataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
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
        name = "GetRecord",
        label = "Get Record",
        node_label = "Get a ServiceNow record in session {{sessionName}}",
        group_label = "Records",
        description = "Gets a record from the input table and sys_id",
        icon = "snow.svg",
        comment = true ,
        //background_color =  "#293E40",
        return_label = "Assign output to a dictionary variable",
        return_type = DataType.DICTIONARY,
        return_description = "Output Dictionary keys are specified with the input Dictionary keys")

public class GetRecord {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public DictionaryValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,
            @Idx(index = "2", type = TEXT) @Pkg(label = "Table", default_value_type = STRING)
            @NotEmpty String table,
            @Idx(index = "3", type = TEXT) @Pkg(label = "Sys_id", default_value_type = STRING)
            @NotEmpty String sys_id,
            @Idx(index = "4", type = DICTIONARY)
            @Pkg(label = "Values to return in dictionary",
                    description = "The keys contained in this input Dictionary variable will define the keys of the " +
                            "output Dictionary variable. The values of this input should match a ServiceNow response " +
                            "key. The ServiceNow keys can be found in the API documentation for ServiceNow. E.G. input " +
                            "Dictionary key: description, value: short_description - this implies that the output Dictionary" +
                            " will contain a key of 'description' and the associated value will be the ServiceNow response value for " +
                            "key 'short_description'.")
                    //Header of the entry form
                    Map<String, StringValue> values
    ) throws IOException, ParseException {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        String response = "";
        JSONObject result;
        try {
            response = ServiceNowActions.getRecord(url, token, table, sys_id, values);
            Object obj = new JSONParser().parse(response);
            JSONObject json_resp = (JSONObject) obj;
            result = (JSONObject) json_resp.get("result");
        } catch(Exception e){
            throw new BotCommandException("ServiceNow didn't respond with a record. Exception caught " + e);
        }

        Map<String, Value> ResMap = new LinkedHashMap();

        if(values.size()>0){
            for (Map.Entry<String,StringValue> entry : values.entrySet()){
                try {
                    ResMap.put(entry.getKey(), new StringValue(result.get(String.valueOf(entry.getValue())).toString()));
                } catch (Exception e){
                    throw new BotCommandException("The ServiceNow record returned doesn't contain one of the keys entered in the values. Please check the values entered. " + e);
                }
            }
        }

        return new DictionaryValue(ResMap);
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
