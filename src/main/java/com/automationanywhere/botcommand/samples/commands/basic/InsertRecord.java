package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.SNOWServer;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListAddButtonLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEmptyLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEntryUnique;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListLabel;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.DataType;
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
        name = "InsertRecord",
        label = "Create Record",
        node_label = "Create a ServiceNow record in session {{sessionName}}",
        group_label = "Records",
        description = "Creates a record to the specified table",
        icon = "snow.svg",
        comment = true ,
        //background_color =  "#293E40",
        return_label = "Assign output to a string",
        return_type = STRING,
        return_description = "Returns sys_id in a string variable")

public class InsertRecord {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public StringValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,
            @Idx(index = "2", type = TEXT) @Pkg(label = "Table", default_value_type = STRING)
            @NotEmpty String table,
            @Idx(index = "3", type = DICTIONARY)
            @Pkg(label = "Values to include in record", description = "e.g. key: short_description, value: my computer crashed")
            @NotEmpty Map<String, StringValue> values
    ) {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        String response = "";
        JSONObject result;
        try {
            response = ServiceNowActions.insertRecord(url, table, token, values);
            Object obj = new JSONParser().parse(response);
            JSONObject json_resp = (JSONObject) obj;
            result = (JSONObject) json_resp.get("result");
        } catch(Exception e){
            throw new BotCommandException("ServiceNow didn't accept the record. Exception caught " + e);
        }
        return new StringValue(result.get("sys_id").toString());
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
