package com.mulesoft.agent.eventtracking.log;

import com.mulesoft.agent.AgentEnableOperationException;
import com.mulesoft.agent.domain.tracking.AgentTrackingNotification;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Ignore
public class EventTrackingLogInternalHandlerTest
{
    @Test
    public void test ()
            throws AgentEnableOperationException
    {
        EventTrackingLogInternalHandler handler = new EventTrackingLogInternalHandler();
        handler.pattern = System.getProperty("pattern");
        handler.fileName = System.getProperty("fileName");
        handler.filePattern = System.getProperty("filePattern");
        handler.bufferSize = Integer.parseInt(System.getProperty("bufferSize"));
        handler.inmediateFlush = Boolean.parseBoolean(System.getProperty("inmediateFlush"));
        handler.daysTrigger = Integer.parseInt(System.getProperty("daysTrigger"));
        handler.mbTrigger = Integer.parseInt(System.getProperty("mbTrigger"));
        handler.enabled = Boolean.parseBoolean(System.getProperty("enabled"));
        handler.dateFormatPattern = System.getProperty("dateFormatPattern");
        handler.postConfigurable();

        for (AgentTrackingNotification notification : createNotifications())
        {
            handler.handle(notification);
        }
    }

    private List<AgentTrackingNotification> createNotifications ()
    {
        List<AgentTrackingNotification> list = new ArrayList<AgentTrackingNotification>();
        for (int i = 0; i < 100000; i++)
        {
            list.add(new AgentTrackingNotification.TrackingNotificationBuilder()
                    .action("TEST " + i)
                    .annotations(new ArrayList<Annotation>())
                    .build());
        }
        return list;
    }
}
