package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
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
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
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
        name = "GetRecords",
        label = "Get Multiple Records",
        node_label = "Get list of records from Servicenow in {{sessionName}}",
        group_label = "Records",
        description = "Gets a list of records from the specified table",
        icon = "snow.svg",
        comment = true ,
        //background_color =  "#293E40",
        return_label = "Assign output to a list of dictionary variables",
        return_type = DataType.LIST,
        return_description = "Specify dictionary keys in entry list")

public class GetRecords {
    @Sessions
    private Map<String, Object> sessionMap;

    @Idx(index = "3.3", type = TEXT, name="NAME")
    @Pkg(label = "Name", default_value_type = DataType.STRING)
    @NotEmpty
    private String name;

    @Idx(index = "3.4", type = TEXT, name="VALUE")
    @Pkg(label = "Value", default_value_type = DataType.STRING)
    private String value;

    @Execute
    public ListValue<DictionaryValue> action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,
            @Idx(index = "2", type = TEXT) @Pkg(label = "Table", default_value_type = STRING)
            @NotEmpty String table,
            @Idx(index = "3", type = ENTRYLIST, options = {
                    @Idx.Option(index = "3.1", pkg = @Pkg(title = "NAME", label = "Dictionary variable key for output")),
                    @Idx.Option(index = "3.2", pkg = @Pkg(title = "VALUE", label = "Servicenow response key")),
            })
            //Label you see at the top of the control
            @Pkg(label = "Values to return for each dictionary in list",
                    description = "e.g. name: incident description, value: short_description")
            //Header of the entry form
            @EntryListLabel(value = "Provide entry")
            //Button label which displays the entry form
            @EntryListAddButtonLabel(value = "Add entry")
            //Uniqueness rule for the column, this value is the TITLE of the column requiring uniqueness.
            @EntryListEntryUnique(value = "NAME")
            //Message to display in table when no entries are present.
            @EntryListEmptyLabel(value = "No values to return")
                    List<Value> values,
            @Idx(index = "4", type = AttributeType.NUMBER) @Pkg(label = "Limit", default_value_type = DataType.NUMBER,
                    description = "Limits the total number of records returned")
            Double limit
    ) throws IOException, ParseException {
        SNOWServer snowServer = (SNOWServer) this.sessionMap.get(sessionName);
        String token = snowServer.getToken();
        String url = snowServer.getURL();
        Integer iLimit = limit.intValue();
        String sLimit = iLimit.toString();
        String response = "";
        JSONArray resultArray;
        try {
            response = ServiceNowActions.getRecords(url, table, token, values, sLimit);
            Object obj = new JSONParser().parse(response);
            JSONObject json_resp = (JSONObject) obj;
            resultArray = (JSONArray) json_resp.get("result");
        } catch(Exception e){
            throw new BotCommandException("ServiceNow didn't respond with a record. Exception caught: " + e);
        }

        ListValue<DictionaryValue> returnValue = new ListValue<>();
        List<Value> dictionaryObjects = new ArrayList<>();

        for (int i=0; i<resultArray.size(); i++) {
            Map<String, Value> ResMap = new LinkedHashMap();
            JSONObject arrayElement = (JSONObject) resultArray.get(i);
            if (values != null && values.size() > 0) {
                for (Value element : values) {
                    Map<String, Value> customValuesMap = ((DictionaryValue) element).get();
                    String name = customValuesMap.containsKey("NAME") ? ((StringValue) customValuesMap.get("NAME")).get() : "";
                    String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue) customValuesMap.get("VALUE")).get();
                    try {
                        ResMap.put(name, new StringValue(arrayElement.get(value).toString()));
                    } catch (Exception e) {
                        throw new BotCommandException("The ServiceNow record returned doesn't contain one of the keys entered in the values. Please check the values entered. " + e);
                    }
                }
                dictionaryObjects.add(new DictionaryValue(ResMap));
            }
        }

        returnValue.set(dictionaryObjects);
        return returnValue;
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
