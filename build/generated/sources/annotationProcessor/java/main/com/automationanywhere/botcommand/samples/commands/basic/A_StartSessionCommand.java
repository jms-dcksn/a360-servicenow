package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.BotCommand;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.core.security.SecureString;
import java.lang.ClassCastException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class A_StartSessionCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(A_StartSessionCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.entrySet().stream().filter(en -> !Arrays.asList( new String[] {}).contains(en.getKey()) && en.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    A_StartSession command = new A_StartSession();
    HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
    if(parameters.containsKey("name") && parameters.get("name") != null && parameters.get("name").get() != null) {
      convertedParameters.put("name", parameters.get("name").get());
      if(convertedParameters.get("name") !=null && !(convertedParameters.get("name") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","name", "String", parameters.get("name").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("name") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","name"));
    }

    if(parameters.containsKey("url") && parameters.get("url") != null && parameters.get("url").get() != null) {
      convertedParameters.put("url", parameters.get("url").get());
      if(convertedParameters.get("url") !=null && !(convertedParameters.get("url") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","url", "String", parameters.get("url").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("url") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","url"));
    }

    if(parameters.containsKey("clientId") && parameters.get("clientId") != null && parameters.get("clientId").get() != null) {
      convertedParameters.put("clientId", parameters.get("clientId").get());
      if(convertedParameters.get("clientId") !=null && !(convertedParameters.get("clientId") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","clientId", "SecureString", parameters.get("clientId").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("clientId") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","clientId"));
    }

    if(parameters.containsKey("clientSecret") && parameters.get("clientSecret") != null && parameters.get("clientSecret").get() != null) {
      convertedParameters.put("clientSecret", parameters.get("clientSecret").get());
      if(convertedParameters.get("clientSecret") !=null && !(convertedParameters.get("clientSecret") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","clientSecret", "SecureString", parameters.get("clientSecret").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("clientSecret") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","clientSecret"));
    }

    if(parameters.containsKey("username") && parameters.get("username") != null && parameters.get("username").get() != null) {
      convertedParameters.put("username", parameters.get("username").get());
      if(convertedParameters.get("username") !=null && !(convertedParameters.get("username") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","username", "SecureString", parameters.get("username").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("username") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","username"));
    }

    if(parameters.containsKey("password") && parameters.get("password") != null && parameters.get("password").get() != null) {
      convertedParameters.put("password", parameters.get("password").get());
      if(convertedParameters.get("password") !=null && !(convertedParameters.get("password") instanceof SecureString)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","password", "SecureString", parameters.get("password").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("password") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","password"));
    }

    command.setSessionMap(sessionMap);
    try {
      command.execute((String)convertedParameters.get("name"),(String)convertedParameters.get("url"),(SecureString)convertedParameters.get("clientId"),(SecureString)convertedParameters.get("clientSecret"),(SecureString)convertedParameters.get("username"),(SecureString)convertedParameters.get("password"));Optional<Value> result = Optional.empty();
      return logger.traceExit(result);
    }
    catch (ClassCastException e) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.IllegalParameters","execute"));
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
