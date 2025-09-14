package net.h4bbo.echo.server;

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

        // Start the main emulator application
        Echo.boot();


    }
}