package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcommand.samples.Utils.HTTPRequest;
import com.automationanywhere.botcommand.samples.Utils.ServiceNowActions;
import com.automationanywhere.commandsdk.annotations.*;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.RECORD;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

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

@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "New Incident",
        description = "Trigger when a new incident is created",
        icon = "snow.svg",
        name = "incidenttrigger",
        return_type = RECORD,
        return_name = "TriggerData",
        return_description = "Available keys: triggerType, opened_at, number, description, sys_id, priority")

public class IncidentTrigger {
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
            @Idx(index = "6", type = AttributeType.SELECT, options = {
                    @Idx.Option(index = "6.1", pkg = @Pkg(label = "1 - Critical", value = "1")),
                    @Idx.Option(index = "6.2", pkg = @Pkg(label = "2 - High", value = "2")),
                    @Idx.Option(index = "6.3", pkg = @Pkg(label = "3 - Moderate", value = "3")),
                    @Idx.Option(index = "6.4", pkg = @Pkg(label = "4 - Low", value = "4")),
                    @Idx.Option(index = "6.5", pkg = @Pkg(label = "5 - Planning", value = "5"))
            })
            @Pkg(label = "Filter on Priority Level", default_value_type = STRING, description = "An incident with priority " +
                    "level at or above input priority will initiate the trigger. " +
                    "i.e. If 5 is selected (lowest priority), all created incidents will initiate the trigger. Leaving " +
                    "this field blank will watch all incidents regardless of priority.", default_value = "5")
            @NotEmpty String priority,
            @Idx(index = "7", type = AttributeType.NUMBER)
            @Pkg(label = "Please provide the interval to trigger in seconds", default_value = "120", default_value_type = DataType.NUMBER,
            description = "Interval should not be less than 30 seconds.")
            @GreaterThan("29")
            @NumberInteger
            @NotEmpty
                    Double interval,
            @Idx(index = "8", type = AttributeType.NUMBER)
            @Pkg(label = "System clock buffer", default_value = "0", default_value_type = DataType.NUMBER,
                    description = "Intended to help accommodate slight differences in bot runner system clock vs ServiceNow timestamp")
            @NumberInteger
            @NotEmpty
                    Double buffer) {
        String ins_clientId = clientId.getInsecureString();
        String ins_clientSecret = clientSecret.getInsecureString();
        String ins_username = username.getInsecureString();
        String ins_password = password.getInsecureString();
        String response= "";
        String token = "";
        Integer intPriority = Integer.parseInt(priority);
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
                    lastRun = ZonedDateTime.now();
                    result = ServiceNowActions.triggerOnRecord(url, "incident", finalToken);
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
                JSONObject json_result = (JSONObject) obj;
                JSONArray resultArray = (JSONArray) json_result.get("result");
                JSONObject time = (JSONObject) resultArray.get(0);
                String opened_at = (String) time.get("opened_at");
                ZonedDateTime dt2 = ZonedDateTime.parse(opened_at,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC")));
                Integer incidentPriority = Integer.parseInt(time.get("priority").toString());
                String number = time.get("number").toString();
                String description = time.get("short_description").toString();
                String sys_id = time.get("sys_id").toString();
                if(dt2.isAfter(lastRun.minusSeconds(buffer.longValue())) && incidentPriority <= intPriority){
                    lastRun = dt2;
                    consumer.accept(getRecordValue(opened_at, number, description, sys_id, incidentPriority));
                    return;
                } //else { lastRun = ZonedDateTime.now(); }
            }
        };
        taskMap.put(this.triggerUid, timerTask);
        TIMER.schedule(timerTask, interval.longValue()*1000, interval.longValue()*1000);
    }

    private RecordValue getRecordValue(String time, String number, String description, String sys_id, Integer incidentPriority) {
        List<Schema> schemas = new LinkedList<>();
        List<Value> values = new LinkedList<>();
        schemas.add(new Schema("triggerType"));
        values.add(new StringValue("New Incident Created"));
        schemas.add(new Schema("opened_at"));
        values.add(new StringValue(time));
        schemas.add(new Schema("number"));
        values.add(new StringValue(number));
        schemas.add(new Schema("description"));
        values.add(new StringValue(description));
        schemas.add(new Schema("sys_id"));
        values.add(new StringValue(sys_id));
        schemas.add(new Schema("priority"));
        values.add(new NumberValue(incidentPriority));

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
