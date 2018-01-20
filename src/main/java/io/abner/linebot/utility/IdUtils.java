package io.abner.linebot.utility;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class IdUtils {

    public static Long generateId(){
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                        .withLocale(Locale.TAIWAN)
                        .withZone(ZoneId.systemDefault());
        return Long.parseLong(formatter.format(Instant.now()));
    }
}
