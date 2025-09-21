package net.h4bbo.echo.api.commands;

import java.util.List;

public interface Command {
    String name();
    String description();
    void execute(List<String> args);
}