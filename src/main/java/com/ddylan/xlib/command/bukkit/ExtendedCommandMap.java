package com.ddylan.xlib.command.bukkit;

import com.ddylan.xlib.command.CommandNode;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExtendedCommandMap extends SimpleCommandMap {

    public ExtendedCommandMap(Server server) {
        super(server);
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");
        int spaceIndex = cmdLine.indexOf(' ');
        if (spaceIndex == -1) {
            ArrayList<String> completions = new ArrayList<>();
            Map<String, Command> knownCommands = this.knownCommands;
            String prefix = (sender instanceof org.bukkit.entity.Player) ? "/" : "";
            for (Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                String name = commandEntry.getKey();
                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    Command command = commandEntry.getValue();
                    if (command instanceof ExtendedCommand) {
                        CommandNode executionNode = ((ExtendedCommand)command).node.getCommand(name);
                        if (executionNode == null)
                            executionNode = ((ExtendedCommand)command).node;
                        if (!executionNode.hasCommands()) {
                            CommandNode testNode = executionNode.getCommand(name);
                            if (testNode == null)
                                testNode = ((ExtendedCommand)command).node.getCommand(name);
                            if (testNode.canUse(sender))
                                completions.add(prefix + name);
                            continue;
                        }
                        if (executionNode.getSubCommands(sender, false).size() != 0)
                            completions.add(prefix + name);
                        continue;
                    }
                    if (!command.testPermissionSilent(sender))
                        continue;
                    completions.add(prefix + name);
                }
            }
            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        }
        String commandName = cmdLine.substring(0, spaceIndex);
        Command target = getCommand(commandName);
        if (target == null)
            return null;
        if (!target.testPermissionSilent(sender))
            return null;
        String argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length());
        String[] args = argLine.split(" ");
        try {
            List<String> completions = (target instanceof ExtendedCommand) ? ((ExtendedCommand)target).tabComplete(sender, cmdLine) : target.tabComplete(sender, commandName, args);
            if (completions != null)
                Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
        }
    }

}
