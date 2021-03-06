package com.mulesoft.agent.common.internalhandler;

import java.io.Serializable;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.agent.exception.AgentEnableOperationException;
import com.mulesoft.agent.common.internalhandler.serializer.DefaultObjectMapperFactory;
import com.mulesoft.agent.configuration.Configurable;
import com.mulesoft.agent.configuration.PostConfigure;
import com.mulesoft.agent.configuration.Type;
import com.mulesoft.agent.handlers.InitializableInternalMessageHandler;
import com.mulesoft.agent.handlers.exception.InitializationException;
import com.mulesoft.agent.services.OnOffSwitch;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Abstract log internal handler.
 * @param <T> Message type.
 */
public abstract class AbstractLogInternalHandler<T> implements InitializableInternalMessageHandler<T>
{
    private static final Logger LOGGER = LogManager.getLogger(AbstractLogInternalHandler.class);
    public static final String MULE_HOME_PLACEHOLDER = "$MULE_HOME";
    public static final String PATTERN_LAYOUT = "%m%n";
    public static final int MB_CONVERSION_UNIT = 1024;

    private String className = this.getClass().getName();
    private String loggerName = className + "." + "logger";
    private String appenderName = className + "." + "appender";
    private String contextName = className + "." + "context";
    private Configuration logConfiguration;
    private LoggerConfig loggerConfig;
    private Appender appender;
    private LoggerContext logContext;
    private ObjectMapper objectMapper;

    org.apache.logging.log4j.core.Logger internalLogger;
    OnOffSwitch enabledSwitch;

    /**
     * <p>
     * Flag to identify if the Internal Handler is enabled or not.
     * Default: false
     * </p>
     */
    @Configurable(value = "false")
    private boolean enabled;

    /**
     * <p>
     * The buffer size in bytes.
     * Default: 262144 (256 * 1024)
     * </p>
     */
    @Configurable(value = "262144", type = Type.DYNAMIC)
    private int bufferSize;

    /**
     * <p>
     * When set to true - the default, each write will be followed by a flush.
     * This will guarantee the data is written to disk but could impact performance.
     * Default: true
     * </p>
     */
    @Configurable(value = "true", type = Type.DYNAMIC)
    private boolean immediateFlush;

    /**
     * <p>
     * Days to maintain on the current active log file before being rolled over to backup files.
     * Default: 1
     * </p>
     */
    @Configurable(value = "1", type = Type.DYNAMIC)
    private int daysTrigger;

    /**
     * <p>
     * Maximum size that the output file is allowed to reach before being rolled over to backup files.
     * Default: 100 (MB)
     * </p>
     */
    @Configurable(value = "100", type = Type.DYNAMIC)
    private int mbTrigger;

    /**
     * <p>
     * Date format used to format the timestamp.
     * Default: yyyy-MM-dd'T'HH:mm:ss.SZ
     * </p>
     */
    @Configurable(value = "yyyy-MM-dd'T'HH:mm:ss.SZ", type = Type.DYNAMIC)
    private String dateFormatPattern;

    protected abstract String getFileName();

    protected abstract String getFilePattern();

    protected ObjectMapper getObjectMapper()
    {
        return this.objectMapper;
    }

    public void enable(boolean state) throws AgentEnableOperationException
    {
        this.enabledSwitch.switchTo(state);
    }

    public boolean isEnabled()
    {
        return this.enabledSwitch.isEnabled();
    }

    @Override
    public boolean handle(T message)
    {
        if (this.isEnabled())
        {
            try
            {
                String serialized = this.objectMapper.writeValueAsString(message);

                this.internalLogger.info(serialized);
                return true;
            }
            catch (Exception e)
            {
                LOGGER.error("There was an error logging the object.", e);
                return false;
            }
        }
        return false;
    }

    @PostConfigure
    public void postConfigurable()
    {
        if (this.enabledSwitch == null)
        {
            this.enabledSwitch = OnOffSwitch.newNullSwitch(this.enabled);
        }
    }

    @Override
    public void initialize() throws InitializationException
    {
        LOGGER.debug("Configuring the Common Log Internal Handler...");

        // Check if we should disable the loggers
        if (this.logContext != null)
        {
            this.appender.stop();
            this.loggerConfig.removeAppender(appenderName);
            this.logConfiguration.removeLogger(loggerName);
        }

        try
        {
            this.logContext = new LoggerContext(contextName);
            this.logConfiguration = logContext.getConfiguration();

            Layout<? extends Serializable> layout = PatternLayout.newBuilder().withPattern(PATTERN_LAYOUT).
                withCharset(Charset.forName("UTF-8")).withAlwaysWriteExceptions(true).withNoConsoleNoAnsi(false).build();

            String dayTrigger = this.daysTrigger + "";
            String sizeTrigger = (this.mbTrigger * MB_CONVERSION_UNIT * MB_CONVERSION_UNIT) + "";
            TimeBasedTriggeringPolicy timeBasedTriggeringPolicy = TimeBasedTriggeringPolicy.createPolicy(dayTrigger, "true");
            SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = SizeBasedTriggeringPolicy.createPolicy(sizeTrigger);
            CompositeTriggeringPolicy policy = CompositeTriggeringPolicy.createPolicy(timeBasedTriggeringPolicy, sizeBasedTriggeringPolicy);

            String fileName = this.getFileName().replace(MULE_HOME_PLACEHOLDER, System.getProperty("mule.home"));
            String filePattern = this.getFilePattern().replace(MULE_HOME_PLACEHOLDER, System.getProperty("mule.home"));
            this.appender = RollingRandomAccessFileAppender.createAppender(fileName, filePattern, "true",
                    this.appenderName, this.immediateFlush + "", this.bufferSize + "",
                    policy, null, layout, null, "false", null, null, this.logConfiguration);

            this.appender.start();

            AppenderRef[] ref = new AppenderRef[]{};
            this.loggerConfig = LoggerConfig.createLogger("false", Level.INFO, this.loggerName, "false", ref,
                    null, this.logConfiguration, null);
            this.loggerConfig.addAppender(this.appender, null, null);
            this.logConfiguration.addLogger(this.loggerName, this.loggerConfig);

            this.internalLogger = this.logContext.getLogger(this.loggerName);
        }
        catch (Exception e)
        {
            throw new InitializationException("There was an error configuring the AbstractLogInternalHandler internal handler.", e);
        }

        this.objectMapper = new DefaultObjectMapperFactory(this.dateFormatPattern).create();

        LOGGER.debug("Successfully configured the Common Log Internal Handler.");
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    public void setImmediateFlush(boolean immediateFlush)
    {
        this.immediateFlush = immediateFlush;
    }

    public void setDaysTrigger(int daysTrigger)
    {
        this.daysTrigger = daysTrigger;
    }

    public void setMbTrigger(int mbTrigger)
    {
        this.mbTrigger = mbTrigger;
    }

    public void setDateFormatPattern(String dateFormatPattern)
    {
        this.dateFormatPattern = dateFormatPattern;
    }
}
