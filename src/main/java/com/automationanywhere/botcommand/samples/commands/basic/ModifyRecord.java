package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.bot.service.Bot;
import com.automationanywhere.botcommand.data.Value;
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
        name = "ModifyRecord",
        label = "Modify a Record",
        node_label = "Modify a Servicenow record in session {{sessionName}}",
        group_label = "Records",
        description = "Modifies record attributes in the specified table",
        icon = "snow.svg",
        comment = true ,
        //background_color =  "#293E40",
        return_label = "Assign output to a string",
        return_type = STRING,
        return_description = "Returns sys_id in a string variable")

public class ModifyRecord {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public StringValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,
            @Idx(index = "2", type = TEXT) @Pkg(label = "Table", default_value_type = STRING)
            @NotEmpty String table,
            @Idx(index = "3", type = TEXT) @Pkg(label = "Sys_Id", default_value_type = STRING)
            @NotEmpty String sys_id,
            @Idx(index = "4", type = DICTIONARY)
            @Pkg(label = "Values to modify in record. Use this action to add updates to a record, such as comments.",
                    description = "e.g. dictionary key: comments, dictionary value: Please try to reboot your computer. " +
                            "Ensure the dictionary key matches a ServiceNow key from the Table API")
                    //Header of the entry form
                    Map<String, StringValue> values
    ) throws IOException, ParseException {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        String response = "";
        JSONObject result;
        try {
            response = ServiceNowActions.modifyRecord(url, "PUT", table, sys_id, token, values);
            Object obj = new JSONParser().parse(response);
            JSONObject json_resp = (JSONObject) obj;
            result = (JSONObject) json_resp.get("result");
        } catch (Exception e) {
            throw new BotCommandException("ServiceNow didn't respond with a record. Exception caught " + e);
        }

        return new StringValue(result.get("sys_id").toString());
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
