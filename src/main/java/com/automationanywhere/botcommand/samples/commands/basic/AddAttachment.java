package com.automationanywhere.botcommand.samples.commands.basic;

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
        name = "AddAttachment",
        label = "Add Attachment",
        node_label = "Add an attachment to a record in session {{sessionName}}",
        group_label = "Attachments",
        description = "Adds an attachment to the specified record",
        icon = "snow.svg",
        comment = true ,
        //background_color =  "#293E40",
        return_label = "Assign output to a string",
        return_type = STRING,
        return_description = "Returns sys_id of the attachment in a string variable")

public class AddAttachment {
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
            @Idx(index = "4", type = FILE) @Pkg(label = "File to upload as attachment")
            @NotEmpty String filePath
    ) throws ParseException {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        String response = "";
        String errorMessage = "";
        JSONObject result;

        response = ServiceNowActions.addAttachment(url, table, token, sys_id, filePath);
        Object obj = new JSONParser().parse(response);
        JSONObject json_resp = (JSONObject) obj;
        if (json_resp.containsKey("error")) {
            JSONObject errorObject =  (JSONObject) json_resp.get("error");
            errorMessage = errorObject.get("message").toString() + ", details: " + errorObject.get("detail").toString();
            throw new BotCommandException("ServiceNow did not find the record at the specific sys_id. " + errorMessage);
        }
        result = (JSONObject) json_resp.get("result");

        return new StringValue(result.get("sys_id").toString());
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
