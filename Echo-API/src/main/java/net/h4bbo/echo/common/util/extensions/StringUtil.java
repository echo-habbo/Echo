package net.h4bbo.echo.common.util.extensions;

import java.util.Arrays;

public class StringUtil {
    /**
     * Filter harmful injectable characters
     */
    public static String filterInput(String str, boolean filterNewline) {
        str = str.replace((char) 1, ' ');
        str = str.replace((char) 2, ' ');
        str = str.replace((char) 3, ' ');
        str = str.replace((char) 9, ' ');

        if (filterNewline) {
            str = str.replace((char) 10, ' ');
            str = str.replace((char) 13, ' ');
        }

        return str;
    }

    /**
     * Get if string is numeric
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Convert delimetered string to int array
     */
    public static int[] toIntArray(String value, char separator) {
        return Arrays.stream(value.split(String.valueOf(separator)))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    /**
     * Convert string to console output
     */
    public static String toConsoleOutput(String value) {
        StringBuilder consoleText = new StringBuilder(value);

        for (int i = 0; i < 13; i++) {
            consoleText.replace(consoleText.indexOf(String.valueOf((char) i)), consoleText.indexOf(String.valueOf((char) i)) + 1, "[" + i + "]");
        }

        return consoleText.toString();
    }
}