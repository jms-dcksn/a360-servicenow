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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "Incident Trigger",
        description = "Trigger when a new incident is created",
        icon = "todo.svg",
        text_color = "#7B848B",
        background_color =  "#a6a6a6",
        name = "incidenttrigger",
        return_type = RECORD,
        return_name = "TriggerData",
        return_description = "Available keys: triggerType, id")

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
            @Idx(index = "6", type = TEXT) @Pkg(label = "ServiceNow Instance Default Timezone", default_value = "America/Los_Angeles",
            description = "Enter the default timezone of your ServiceNow instance. " +
                    "For more information on timezones in ServiceNow, refer to ServiceNow documentation.", default_value_type = STRING) @NotEmpty String tz,
            @Idx(index = "7", type = AttributeType.NUMBER)
            @Pkg(label = "Please provide the interval to trigger in seconds", default_value = "120", default_value_type = DataType.NUMBER)
            @GreaterThan("0")
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
                String result = ServiceNowActions.triggerOnRecord(url, "incident", finalToken);
                Object obj = null;
                try {
                    obj = new JSONParser().parse(result);
                } catch (ParseException e) {
                    throw new BotCommandException("An unexpected response was received from ServiceNow. Please check your credentials and ensure your instance as awake. Exception message: " + e);
                }
                JSONObject json_result = (JSONObject) obj;
                String opened_at = (String) json_result.get("opened_at");
                ZonedDateTime dt2 = ZonedDateTime.parse(opened_at,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(tz)));
                if(dt2.isAfter(lastRun)){
                    consumer.accept(getRecordValue(opened_at));
                    return;
                }
                lastRun = ZonedDateTime.now();
            }
        };
        taskMap.put(this.triggerUid, timerTask);
        TIMER.schedule(timerTask, interval.longValue()*1000, interval.longValue()*1000);
    }

    private RecordValue getRecordValue(String time) {
        List<Schema> schemas = new LinkedList<>();
        List<Value> values = new LinkedList<>();
        schemas.add(new Schema("triggerType"));
        values.add(new StringValue("New Incident"));
        schemas.add(new Schema("opened_at"));
        values.add(new StringValue(time));

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
