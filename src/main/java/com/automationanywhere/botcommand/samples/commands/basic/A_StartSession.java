/*
 * Copyright (c) 2020 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */

/**
 * @author James Dickson
 */
package com.automationanywhere.botcommand.samples.commands.basic;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.HTTPRequest;
import com.automationanywhere.botcommand.samples.Utils.SNOWServer;

import java.io.IOException;
import java.util.Map;

import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.core.security.SecureString;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author James Dickson
 */
@BotCommand
@CommandPkg(label = "Start Session",
		description = "Starts Session with OAuth Credentials",
		icon = "snow.svg",
		name = "startSnowSession",
		node_label = "Start Session {{sessionName}}",
		group_label="Admin",
		comment = true
		//text_color = "#7B848B" ,
		//background_color =  "#293E40"
		)
public class A_StartSession {

	@Sessions
	private Map<String, Object> sessionMap;

	@Execute
	public void execute(
			@Idx(index = "1", type = TEXT)
			@Pkg(label = "Start Session", default_value_type = STRING, default_value = "Default")
			@NotEmpty String name,
			@Idx(index = "2", type = TEXT) @Pkg(label = "ServiceNow URL", description = "e.g. https://{Your-Instance}.service-now.com/") @NotEmpty String url,
			@Idx(index = "3", type = CREDENTIAL) @Pkg(label = "Client ID") @NotEmpty SecureString clientId,
			@Idx(index = "4", type = CREDENTIAL) @Pkg(label = "Client Secret") @NotEmpty SecureString clientSecret,
			@Idx(index = "5", type = CREDENTIAL) @Pkg(label = "User Name") @NotEmpty SecureString username,
			@Idx(index = "6", type = CREDENTIAL) @Pkg(label = "Password") @NotEmpty SecureString password
	) throws ParseException, IOException {
		String ins_clientId = clientId.getInsecureString();
		String ins_clientSecret = clientSecret.getInsecureString();
		String ins_username = username.getInsecureString();
		String ins_password = password.getInsecureString();
		String response= "";
		String token = "";
		if (!sessionMap.containsKey(name))
			try {
				response = HTTPRequest.oAuthMethod(url, ins_clientId, ins_clientSecret, ins_username, ins_password);
				Object obj = new JSONParser().parse(response);
				JSONObject details = (JSONObject) obj;
				token = (String) details.get("access_token");
			} catch (Exception e){
				throw new BotCommandException("The response from ServiceNow did not contain an access token. Please check your credentials and ensure your instance as awake. Exception message: " + e);
			}
			SNOWServer snowServer = new SNOWServer(url, token);
			sessionMap.put(name, snowServer);
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

}
