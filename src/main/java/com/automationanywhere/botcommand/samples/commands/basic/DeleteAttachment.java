package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.SNOWServer;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
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
        name = "DeleteAttachment",
        label = "Delete Attachment",
        node_label = "Deletes an attachment in session {{sessionName}}",
        group_label = "Attachments",
        description = "Deletes an attachment to the specified record",
        icon = "snow.svg",
        comment = true
        )

public class DeleteAttachment {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public void action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,
            @Idx(index = "2", type = TEXT) @Pkg(label = "Sys_Id", default_value_type = STRING, description = "Enter sys_id for the attachment")
            @NotEmpty String sys_id
    ) throws ParseException, IOException {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        String result = ServiceNowActions.deleteAttachment(url, token, sys_id);
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
