package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.bot.service.TriggerException;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.core.security.SecureString;
import com.automationanywhere.toolchain.runtime.Trigger3;
import com.automationanywhere.toolchain.runtime.Trigger3ListenerContext;
import java.lang.ClassCastException;
import java.lang.Double;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IncidentTriggerTrigger implements Trigger3 {
  private static final Logger logger = LogManager.getLogger(IncidentTriggerTrigger.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  private static IncidentTriggerTrigger thisInstance = new IncidentTriggerTrigger();

  private final IncidentTrigger command = new IncidentTrigger();

  private IncidentTriggerTrigger() {
    super();
  }

  public static IncidentTriggerTrigger getInstance() {
    return thisInstance;
  }

  public Object clone() {
    return thisInstance;
  }

  public void startListen(Trigger3ListenerContext triggerListenerContext,
      Map<String, Value> parameters) throws TriggerException {
    logger.traceEntry(() -> parameters != null ? parameters.entrySet().stream().filter(en -> !Arrays.asList( new String[] {}).contains(en.getKey()) && en.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).toString() : null);
    HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
    if(parameters.containsKey("url") && parameters.get("url") != null && parameters.get("url").get() != null) {
      convertedParameters.put("url", parameters.get("url").get());
      if(convertedParameters.get("url") !=null && !(convertedParameters.get("url") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","url", "String", parameters.get("url").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("url") == null) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","url"));
    }

    if(parameters.containsKey("clientId") && parameters.get("clientId") != null && parameters.get("clientId").get() != null) {
      convertedParameters.put("clientId", parameters.get("clientId").get());
      if(convertedParameters.get("clientId") !=null && !(convertedParameters.get("clientId") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","clientId", "SecureString", parameters.get("clientId").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("clientId") == null) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","clientId"));
    }

    if(parameters.containsKey("clientSecret") && parameters.get("clientSecret") != null && parameters.get("clientSecret").get() != null) {
      convertedParameters.put("clientSecret", parameters.get("clientSecret").get());
      if(convertedParameters.get("clientSecret") !=null && !(convertedParameters.get("clientSecret") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","clientSecret", "SecureString", parameters.get("clientSecret").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("clientSecret") == null) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","clientSecret"));
    }

    if(parameters.containsKey("username") && parameters.get("username") != null && parameters.get("username").get() != null) {
      convertedParameters.put("username", parameters.get("username").get());
      if(convertedParameters.get("username") !=null && !(convertedParameters.get("username") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","username", "SecureString", parameters.get("username").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("username") == null) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","username"));
    }

    if(parameters.containsKey("password") && parameters.get("password") != null && parameters.get("password").get() != null) {
      convertedParameters.put("password", parameters.get("password").get());
      if(convertedParameters.get("password") !=null && !(convertedParameters.get("password") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","password", "SecureString", parameters.get("password").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("password") == null) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","password"));
    }

    if(parameters.containsKey("tz") && parameters.get("tz") != null && parameters.get("tz").get() != null) {
      convertedParameters.put("tz", parameters.get("tz").get());
      if(convertedParameters.get("tz") !=null && !(convertedParameters.get("tz") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","tz", "String", parameters.get("tz").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("tz") == null) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","tz"));
    }

    if(parameters.containsKey("interval") && parameters.get("interval") != null && parameters.get("interval").get() != null) {
      convertedParameters.put("interval", parameters.get("interval").get());
      if(convertedParameters.get("interval") !=null && !(convertedParameters.get("interval") instanceof Double)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","interval", "Double", parameters.get("interval").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("interval") == null) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","interval"));
    }
    if(convertedParameters.containsKey("interval")) {
      try {
        if(convertedParameters.get("interval") != null && !((double)convertedParameters.get("interval") > 0)) {
          throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.GreaterThan","interval", "0"));
        }
      }
      catch(ClassCastException e) {
        throw new TriggerException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","interval", "Number", convertedParameters.get("interval").getClass().getSimpleName()));
      }
      catch(NullPointerException e) {
        throw new TriggerException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","interval"));
      }
      if(convertedParameters.get("interval")!=null && !(convertedParameters.get("interval") instanceof Number)) {
        throw new TriggerException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","interval", "Number", convertedParameters.get("interval").getClass().getSimpleName()));
      }

    }
    command.setConsumer(triggerListenerContext.getEventCallback());
    command.setTriggerUid(triggerListenerContext.getTriggerUid());
    try {
      command.startTrigger((String)convertedParameters.get("url"),(SecureString)convertedParameters.get("clientId"),(SecureString)convertedParameters.get("clientSecret"),(SecureString)convertedParameters.get("username"),(SecureString)convertedParameters.get("password"),(String)convertedParameters.get("tz"),(Double)convertedParameters.get("interval"));}
    catch (ClassCastException e) {
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.IllegalParameters","startTrigger"));
    }
    catch (TriggerException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }

  public void stopListen(String triggerUid) throws TriggerException {
    try {
      command.stopListen(triggerUid);}
    catch (TriggerException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }

  public void stopAllTriggers() throws TriggerException {
    try {
      command.stopAllTriggers();}
    catch (TriggerException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new TriggerException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }
}
