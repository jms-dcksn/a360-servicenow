package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.BotCommand;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import java.lang.ClassCastException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UpdateRecordCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(UpdateRecordCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.entrySet().stream().filter(en -> !Arrays.asList( new String[] {}).contains(en.getKey()) && en.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    UpdateRecord command = new UpdateRecord();
    HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
    if(parameters.containsKey("sessionName") && parameters.get("sessionName") != null && parameters.get("sessionName").get() != null) {
      convertedParameters.put("sessionName", parameters.get("sessionName").get());
      if(convertedParameters.get("sessionName") !=null && !(convertedParameters.get("sessionName") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","sessionName", "String", parameters.get("sessionName").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("sessionName") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","sessionName"));
    }

    if(parameters.containsKey("table") && parameters.get("table") != null && parameters.get("table").get() != null) {
      convertedParameters.put("table", parameters.get("table").get());
      if(convertedParameters.get("table") !=null && !(convertedParameters.get("table") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","table", "String", parameters.get("table").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("table") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","table"));
    }

    if(parameters.containsKey("sys_id") && parameters.get("sys_id") != null && parameters.get("sys_id").get() != null) {
      convertedParameters.put("sys_id", parameters.get("sys_id").get());
      if(convertedParameters.get("sys_id") !=null && !(convertedParameters.get("sys_id") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","sys_id", "String", parameters.get("sys_id").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("sys_id") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","sys_id"));
    }

    if(parameters.containsKey("values") && parameters.get("values") != null && parameters.get("values").get() != null) {
      convertedParameters.put("values", parameters.get("values").get());
      if(convertedParameters.get("values") !=null && !(convertedParameters.get("values") instanceof List)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","values", "List", parameters.get("values").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.containsKey("values")) {
      try {
        List<Value> entryList = (List<Value>)convertedParameters.get("values");
        for(Value element:entryList)  {
          DictionaryValue dictionaryValue=(DictionaryValue)element;
          Map<String,Value> valueMap=dictionaryValue.get();
          HashSet<Value> uniqueValueSet=new HashSet() ;
          for (Map.Entry<String,Value> entry: valueMap.entrySet()) {
            if(entry.getKey().equals("NAME")  && uniqueValueSet.contains(entry.getValue()))  {
              throw new BotCommandException(MESSAGES_GENERIC.getString("entrylist.duplicate.rows.added","NAME"));
            }
            else if (entry.getKey().equals("NAME"))  {
              uniqueValueSet.add(entry.getValue());
            }
          }
        }
      }
      catch(ClassCastException e) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","values", "List<Value>",convertedParameters.get("values").getClass().getSimpleName()));
      }


      if(parameters.containsKey("NAME") && parameters.get("NAME") != null && parameters.get("NAME").get() != null) {
        convertedParameters.put("NAME", parameters.get("NAME").get());
        if(convertedParameters.get("NAME") !=null && !(convertedParameters.get("NAME") instanceof String)) {
          throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","NAME", "String", parameters.get("NAME").get().getClass().getSimpleName()));
        }
      }

      if(parameters.containsKey("VALUE") && parameters.get("VALUE") != null && parameters.get("VALUE").get() != null) {
        convertedParameters.put("VALUE", parameters.get("VALUE").get());
        if(convertedParameters.get("VALUE") !=null && !(convertedParameters.get("VALUE") instanceof String)) {
          throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","VALUE", "String", parameters.get("VALUE").get().getClass().getSimpleName()));
        }
      }


    }
    command.setSessionMap(sessionMap);
    try {
      Optional<Value> result =  Optional.ofNullable(command.action((String)convertedParameters.get("sessionName"),(String)convertedParameters.get("table"),(String)convertedParameters.get("sys_id"),(List<Value>)convertedParameters.get("values")));
      return logger.traceExit(result);
    }
    catch (ClassCastException e) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.IllegalParameters","action"));
    }
    catch (BotCommandException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }
}
