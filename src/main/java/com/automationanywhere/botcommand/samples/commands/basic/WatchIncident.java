package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.HTTPRequest;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThan;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NumberInteger;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.core.security.SecureString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.RECORD;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "Watch Incident",
        description = "Trigger when a specified incident is updated",
        icon = "snow.svg",
        name = "watchIncident",
        return_type = RECORD,
        return_name = "TriggerData",
        return_description = "Available keys: triggerType, updated_at, updated_by, description")

public class WatchIncident {
    private ZonedDateTime lastRun;
    // Map storing multiple tasks
    private static final Map<String, TimerTask> taskMap = new ConcurrentHashMap<>();
    private static final Timer TIMER = new Timer(true);

    @TriggerId
    private String triggerUid;
    @TriggerConsumer
    private Consumer consumer;

    /*
     * Starts the trigger.
     */
    @StartListen
    public void startTrigger(
            @Idx(index = "1", type = TEXT) @Pkg(label = "ServiceNow URL") @NotEmpty String url,
            @Idx(index = "2", type = CREDENTIAL) @Pkg(label = "Client ID") @NotEmpty SecureString clientId,
            @Idx(index = "3", type = CREDENTIAL) @Pkg(label = "Client Secret") @NotEmpty SecureString clientSecret,
            @Idx(index = "4", type = CREDENTIAL) @Pkg(label = "User Name") @NotEmpty SecureString username,
            @Idx(index = "5", type = CREDENTIAL) @Pkg(label = "Password") @NotEmpty SecureString password,
            @Idx(index = "6", type = TEXT) @Pkg(label = "Sys_id of incident to watch") @NotEmpty String sys_id,
            @Idx(index = "7", type = TEXT) @Pkg(label = "ServiceNow System Time Zone", default_value = "America/Los_Angeles",
                    default_value_type = STRING, description = "Refer to ServiceNow documentation " +
                    "(https://docs.servicenow.com/bundle/rome-platform-administration/page/administer/time/reference/r_TimeZones.html) " +
                    "for available time zones and the correct syntax. Enter time zone exactly as displayed" +
                    " in the documentation. e.g. For Pacific Time, enter 'America/Los_Angeles'") @NotEmpty String zone,
            @Idx(index = "8", type = AttributeType.NUMBER)
            @Pkg(label = "Please provide the interval to trigger in seconds", default_value = "120", default_value_type = DataType.NUMBER,
            description = "Interval should not be less than 30 seconds.")
            @GreaterThan("29")
            @NumberInteger
            @NotEmpty
                    Double interval) {

        lastRun = ZonedDateTime.now();

        String ins_clientId = clientId.getInsecureString();
        String ins_clientSecret = clientSecret.getInsecureString();
        String ins_username = username.getInsecureString();
        String ins_password = password.getInsecureString();
        String response= "";
        String token = "";

        try {
            response = HTTPRequest.oAuthMethod(url, ins_clientId, ins_clientSecret, ins_username, ins_password);
            Object obj = new JSONParser().parse(response);
            JSONObject details = (JSONObject) obj;
            token = (String) details.get("access_token");
            } catch (Exception e){
                throw new BotCommandException("The response from ServiceNow did not contain an access token. Please check your credentials and ensure your instance as awake. Exception message: " + e);
            }

        String finalToken = token;
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                String result = null;
                try {
                    result = ServiceNowActions.watchIncident(url, finalToken, sys_id);
                } catch (IOException e) {
                    throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
                } catch (ParseException e) {
                    throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
                }
                Object obj = null;
                try {
                    obj = new JSONParser().parse(result);
                } catch (ParseException e) {
                    throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
                }
                JSONObject json_obj = (JSONObject) obj;
                JSONObject json_result = (JSONObject) json_obj.get("result");
                String sys_updated_on = (String) json_result.get("sys_updated_on");
                ZonedDateTime dt2 = ZonedDateTime.parse(sys_updated_on,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(zone)));
                String description = json_result.get("short_description").toString();
                String updated_by = json_result.get("sys_updated_by").toString();
                String comments = json_result.get("comments").toString();
                String[] arrOfStr = comments.split("\\n");
                String comment = arrOfStr[1];
                if(dt2.isAfter(lastRun)){
                    lastRun = dt2;
                    consumer.accept(getRecordValue(sys_updated_on, updated_by, description, comment));
                    return;
                } //else { lastRun = ZonedDateTime.now(); }
            }
        };
        taskMap.put(this.triggerUid, timerTask);
        TIMER.schedule(timerTask, interval.longValue()*1000, interval.longValue()*1000);
    }

    private RecordValue getRecordValue(String time, String updated_by, String description, String comment) {
        List<Schema> schemas = new LinkedList<>();
        List<Value> values = new LinkedList<>();
        schemas.add(new Schema("triggerType"));
        values.add(new StringValue("Incident Updated"));
        schemas.add(new Schema("updated_at"));
        values.add(new StringValue(time));
        schemas.add(new Schema("updated_by"));
        values.add(new StringValue(updated_by));
        schemas.add(new Schema("description"));
        values.add(new StringValue(description));
        schemas.add(new Schema("comment"));
        values.add(new StringValue(comment));

        RecordValue recordValue = new RecordValue();
        recordValue.set(new Record(schemas,values));
        return recordValue;
    }

    /*
     * Cancel all the task and clear the map.
     */
    @StopAllTriggers
    public void stopAllTriggers() {
        taskMap.forEach((k, v) -> {
            if (v.cancel()) {
                taskMap.remove(k);
            }
        });
    }

    /*
     * Cancel the task and remove from map
     *
     * @param triggerUid
     */
    @StopListen
    public void stopListen(String triggerUid) {
        if (taskMap.get(triggerUid).cancel()) {
            taskMap.remove(triggerUid);
        }
    }

    public String getTriggerUid() {
        return triggerUid;
    }

    public void setTriggerUid(String triggerUid) {
        this.triggerUid = triggerUid;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }
}
