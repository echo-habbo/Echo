package net.h4bbo.echo.server.commands;

import net.h4bbo.echo.common.util.extensions.CollectionUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHandler {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public CommandHandler() {
        register(new Command() {
            public String name() { return "/exit"; }
            public String description() { return "Exit the application"; }
            public void execute(List<String> args) {
                System.out.println("Exiting...");
                System.exit(0);
            }
        });

        register(new Command() {
            public String name() { return "/echo"; }
            public String description() { return "Echoes back the provided arguments"; }
            public void execute(List<String> args) {
                System.out.println(String.join(" ", args));
            }
        });

        // Register /help last so it can access allCommands
        register(new Command() {
            public String name() { return "/help"; }
            public String description() { return "Show this help menu. Usage: /help [page]"; }
            public void execute(List<String> args) {
                int cmdsPerPage = 5;

                List<Command> cmds = allCommands();
                List<List<Command>> pages = CollectionUtil.paginate(cmds, cmdsPerPage);

                int totalPages = pages.size();
                int page = 1;

                if (!args.isEmpty()) {
                    try { page = Math.max(1, Integer.parseInt(args.getFirst())); }
                    catch (NumberFormatException ignored) {}
                }

                if (page > totalPages) {
                    System.out.println("No such help page. Max page: " + totalPages);
                    return;
                }

                System.out.printf("-- Help (Page %d/%d) --%n", page, totalPages);

                for (Command c : pages.get(page - 1)) {
                    System.out.printf("%-10s : %s%n", c.name(), c.description());
                }

                System.out.println("------------------------");

                if (totalPages > 1) {
                    System.out.println("Type /help <page> for more.");
                }
            }
        });
    }

    public void register(Command cmd) {
        commands.put(cmd.name().toLowerCase(), cmd);
    }

    public void execute(String line) {
        if (line == null || line.isBlank()) return;
        String[] split = line.trim().split("\\s+");
        String cmdName = split[0].toLowerCase();
        List<String> args = split.length > 1 ? Arrays.asList(Arrays.copyOfRange(split, 1, split.length)) : List.of();
        Command cmd = commands.get(cmdName);
        if (cmd != null) {
            cmd.execute(args);
        } else {
            System.out.println("Unknown command. Try /help");
        }
    }

    public List<Command> allCommands() {
        return commands.values().stream().sorted(Comparator.comparing(Command::name)).toList();
    }
}