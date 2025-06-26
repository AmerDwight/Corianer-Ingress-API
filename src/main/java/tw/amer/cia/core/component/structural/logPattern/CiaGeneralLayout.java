package tw.amer.cia.core.component.structural.logPattern;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CiaGeneralLayout extends PatternLayout {
    private static final String BINDER_CLASS = "org.hibernate.type.descriptor.sql.BasicBinder";
    private static final String VARBINARY = "[VARBINARY]";

    @Override
    public String doLayout(ILoggingEvent event) {
        String message = super.doLayout(event);
        if (BINDER_CLASS.equals(event.getLoggerName()) &&
                event.getMessage().contains(VARBINARY)) {

            String originalMessage = event.getMessage();
            String baseMessage = originalMessage.substring(0, originalMessage.indexOf(" - "));
            String newMessage = baseMessage + " - [ content-in-byte-array ]";

            return message.replace(originalMessage, newMessage);
        }
        return message;
    }
}