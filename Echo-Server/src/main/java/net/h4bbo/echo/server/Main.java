package net.h4bbo.echo.server;

import net.h4bbo.echo.server.commands.CommandHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        // Display application banner and build information
        System.out.println("ECHO EMULATOR");
        System.out.println("THE SHOCKWAVE HABBO HOTEL EMULATOR");
        System.out.println("COPYRIGHT (C) 2018-2025 BY QUACKSTER");
        System.out.println("FOR MORE DETAILS CHECK LEGAL.TXT");
        System.out.println();
        System.out.println("BUILD");
        System.out.println(" CORE: Autumn, Java 24");
        System.out.println(" CLIENT: R14+");
        System.out.println();

        // Start console input on virtual thread
        final var commandHandler = new CommandHandler();
        Thread.ofVirtual().start(() -> runConsole(commandHandler));

        // Simulate main server on the main thread
        Echo.boot();
    }

    private static void runConsole(CommandHandler consoleHandler) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                consoleHandler.execute(line);
            }
        } catch (Exception e) {
            System.err.println("Console error: " + e.getMessage());
        }
    }
}