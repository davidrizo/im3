package es.ua.dlsi.im3.core.utils;

import java.time.Duration;
import java.time.Instant;

/**
 * @autor drizo
 */
public class TimeUtils {
    public static String getTimeElapsed(Instant from, Instant to) {
        Duration t = Duration.between(from, to);
        return t.toString();
    }
}
