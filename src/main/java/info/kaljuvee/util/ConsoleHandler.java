package info.kaljuvee.util;

import java.util.logging.*;

/**
 * Helper class that directs logging to standard out (by default Java Logging prints to standard err).
 */
public class ConsoleHandler extends StreamHandler {

    public ConsoleHandler() {
        super(System.out, new SimpleFormatter());
    }
}
