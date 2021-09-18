package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.SNOWServer;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.FILE;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 * @author James Dickson
 *
 */
@BotCommand
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "GetAttachment",
        label = "Get Attachment",
        node_label = "Downloads an attachment to a record in session {{sessionName}}",
        group_label = "Attachments",
        description = "Downloads an attachment to the specified record",
        icon = "snow.svg",
        comment = true
        )

public class GetAttachment {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public void action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,
            @Idx(index = "3", type = TEXT) @Pkg(label = "Sys_Id", default_value_type = STRING, description = "Enter sys_id for the attachment")
            @NotEmpty String sys_id,
            @Idx(index = "4", type = FILE) @Pkg(label = "Location and name to save attachment", description = "e.g. C:/Users/../Pictures/incident-screenshot.png")
            @NotEmpty String filePath
    ) throws ParseException, IOException {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        ServiceNowActions.downloadAttachment(url, token, sys_id, filePath);
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
