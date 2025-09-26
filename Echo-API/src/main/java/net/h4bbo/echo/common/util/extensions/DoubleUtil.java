package net.h4bbo.echo.common.util.extensions;

public class DoubleUtil {
    /**
     * Convert double for Habbo client
     */
    public static String toClientValue(double value) {
        return String.format("%.1f", value);
    }
}