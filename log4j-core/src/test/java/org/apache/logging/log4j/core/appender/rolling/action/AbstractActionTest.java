package org.apache.logging.log4j.core.appender.rolling.action;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.junit.StatusLoggerRule;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusLogger;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractActionTest {

    @Rule
    public StatusLoggerRule statusLogger = new StatusLoggerRule(Level.WARN);

    // Test for LOG4J2-2658
    @Test
    public void testExceptionsAreLoggedToStatusLogger() {
        StatusLogger statusLogger = StatusLogger.getLogger();
        statusLogger.clear();
        new TestAction().run();
        List<StatusData> statusDataList = statusLogger.getStatusData();
        assertEquals(1, statusDataList.size());
        StatusData statusData = statusDataList.get(0);
        assertEquals(Level.WARN, statusData.getLevel());
        String formattedMessage = statusData.getFormattedStatus();
        assertTrue(formattedMessage, formattedMessage.contains("Exception reported by action 'class org.apache."
                + "logging.log4j.core.appender.rolling.action.AbstractActionTest$TestAction' java.io.IOException: "
                + "failed" + System.lineSeparator()
                + "\tat org.apache.logging.log4j.core.appender.rolling.action.AbstractActionTest"
                + "$TestAction.execute(AbstractActionTest.java:"));
    }

    @Test
    public void testRuntimeExceptionsAreLoggedToStatusLogger() {
        StatusLogger statusLogger = StatusLogger.getLogger();
        statusLogger.clear();
        new AbstractAction() {
            @Override
            public boolean execute() {
                throw new IllegalStateException();
            }
        }.run();
        List<StatusData> statusDataList = statusLogger.getStatusData();
        assertEquals(1, statusDataList.size());
        StatusData statusData = statusDataList.get(0);
        assertEquals(Level.WARN, statusData.getLevel());
        String formattedMessage = statusData.getFormattedStatus();
        assertTrue(formattedMessage.contains("Exception reported by action"));
    }

    @Test
    public void testErrorsAreLoggedToStatusLogger() {
        StatusLogger statusLogger = StatusLogger.getLogger();
        statusLogger.clear();
        new AbstractAction() {
            @Override
            public boolean execute() {
                throw new AssertionError();
            }
        }.run();
        List<StatusData> statusDataList = statusLogger.getStatusData();
        assertEquals(1, statusDataList.size());
        StatusData statusData = statusDataList.get(0);
        assertEquals(Level.WARN, statusData.getLevel());
        String formattedMessage = statusData.getFormattedStatus();
        assertTrue(formattedMessage.contains("Exception reported by action"));
    }

    private static final class TestAction extends AbstractAction {
        @Override
        public boolean execute() throws IOException {
            throw new IOException("failed");
        }
    }
}
