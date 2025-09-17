package net.h4bbo.echo.server.commands;

import java.util.List;

public interface Command {
    String name();
    String description();
    void execute(List<String> args);
}