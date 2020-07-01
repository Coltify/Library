package com.ddylan.library.command.bukkit;

import com.ddylan.library.LibraryPlugin;
import com.google.common.collect.Lists;
import lombok.Getter;
import com.ddylan.library.command.*;
import com.ddylan.library.util.Color;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@Getter
public class ExtendedCommand extends Command implements PluginIdentifiableCommand {


    protected CommandNode node;
    private final JavaPlugin owningPlugin;

    public ExtendedCommand(CommandNode node, JavaPlugin plugin) {
        super(node.getName(), "", "/", Lists.newArrayList(node.getRealAliases()));
        this.node = node;
        this.owningPlugin = plugin;
    }

    public boolean execute(final CommandSender sender, String label, String[] args) {
        label = label.replace(this.owningPlugin.getName().toLowerCase() + ":", "");
        String[] newArgs = concat(label, args);
        final Arguments arguments = (new ArgumentProcessor()).process(newArgs);
        final CommandNode executionNode = this.node.findCommand(arguments);
        final String realLabel = getFullLabel(executionNode);
        if (executionNode.canUse(sender)) {
            if (executionNode.isAsync()) {
                (new BukkitRunnable() {
                    public void run() {
                        try {
                            if (!executionNode.invoke(sender, arguments))
                                executionNode.getUsage(realLabel).send(sender);
                        } catch (CommandException ex) {
                            executionNode.getUsage(realLabel).send(sender);
                            sender.sendMessage(Color.RED + "An error occurred while processing your command.");
                            if (sender.isOp())
                                ExtendedCommand.this.sendStackTrace(sender, (Exception)ex);
                        }
                    }
                }).runTaskAsynchronously((Plugin)this.owningPlugin);
            } else {
                try {
                    if (!executionNode.invoke(sender, arguments))
                        executionNode.getUsage(realLabel).send(sender);
                } catch (CommandException ex) {
                    executionNode.getUsage(realLabel).send(sender);
                    sender.sendMessage(Color.RED + "An error occurred while processing your command.");
                    if (sender.isOp())
                        sendStackTrace(sender, ex);
                }
            }
        } else if (executionNode.isHidden()) {
            sender.sendMessage(LibraryPlugin.getInstance().getCommandHandler().getConfig().hiddenMessage());
        } else {
            sender.sendMessage(LibraryPlugin.getInstance().getCommandHandler().getConfig().noPermissionMessage());
        }
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        String[] rawArgs = cmdLine.replace(this.owningPlugin.getName().toLowerCase() + ":", "").split(" ");
        if (rawArgs.length < 1) {
            if (!this.node.canUse(sender))
                return new ArrayList<>();
            return new ArrayList<>();
        }
        Arguments arguments = (new ArgumentProcessor()).process(rawArgs);
        CommandNode realNode = this.node.findCommand(arguments);
        if (!realNode.canUse(sender))
            return new ArrayList<>();;
        List<String> realArgs = arguments.getArguments();
        int currentIndex = realArgs.size() - 1;
        if (currentIndex < 0)
            currentIndex = 0;
        if (cmdLine.endsWith(" ") && realArgs.size() >= 1)
            currentIndex++;
        if (currentIndex < 0)
            return new ArrayList<>();;
        List<String> completions = new ArrayList<>();
        if (realNode.hasCommands()) {
            String name = (realArgs.size() == 0) ? "" : realArgs.get(realArgs.size() - 1);
            completions.addAll(realNode.getChildren().values().stream()
                    .filter(node -> (node.canUse(sender) && (StringUtils.startsWithIgnoreCase(node.getName(), name) || StringUtils.isEmpty(name))))

                    .map(CommandNode::getName).collect(Collectors.toList()));
            if (completions.size() > 0)
                return completions;
        }
        if (rawArgs[rawArgs.length - 1].equalsIgnoreCase(realNode.getName()) && !cmdLine.endsWith(" "))
            return new ArrayList<>();
        if (realNode.getValidFlags() != null && !realNode.getValidFlags().isEmpty()) {
            for (String flags : realNode.getValidFlags()) {
                String arg = rawArgs[rawArgs.length - 1];
                if ((Flag.FLAG_PATTERN.matcher(arg).matches() || arg.equals("-")) && (
                        StringUtils.startsWithIgnoreCase(flags, arg.substring(1)) || arg.equals("-")))
                    completions.add("-" + flags);
            }
            if (completions.size() > 0)
                return completions;
        }
        try {
            ParameterType<?> parameterType = null;
            ParameterData data = null;
            if (realNode.getParameters() != null) {
                List<ParameterData> params = realNode.getParameters().stream().filter(d -> d instanceof ParameterData).map(d -> (ParameterData)d).collect(Collectors.toList());
                int fixed = Math.max(0, currentIndex - 1);
                data = params.get(fixed);
                parameterType = LibraryPlugin.getInstance().getCommandHandler().getParameterType(data.getType());
                if (data.getParameterType() != null)
                    try {
                        parameterType = data.getParameterType().newInstance();
                    } catch (InstantiationException|IllegalAccessException e) {
                        e.printStackTrace();
                    }
            }
            if (parameterType != null) {
                if (currentIndex < realArgs.size() && realArgs.get(currentIndex).equalsIgnoreCase(realNode.getName())) {
                    realArgs.add("");
                    currentIndex++;
                }
                String argumentBeingCompleted = (currentIndex >= realArgs.size() || realArgs.size() == 0) ? "" : realArgs.get(currentIndex);
                List<String> suggested = parameterType.tabComplete((Player) sender, data.getTabCompleteFlags(), argumentBeingCompleted);
                completions.addAll(suggested.stream().filter(s -> StringUtils.startsWithIgnoreCase(s, argumentBeingCompleted))
                        .collect(Collectors.toList()));
            }
        } catch (Exception exception) {}
        return completions;
    }

    public Plugin getPlugin() {
        return this.owningPlugin;
    }

    private String[] concat(String label, String[] args) {
        String[] labelAsArray = { label };
        String[] newArgs = new String[args.length + 1];
        System.arraycopy(labelAsArray, 0, newArgs, 0, 1);
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }

    private String getFullLabel(CommandNode node) {
        List<String> labels = new ArrayList<>();
        while (node != null) {
            String name = node.getName();
            if (name != null)
                labels.add(name);
            node = node.getParent();
        }
        Collections.reverse(labels);
        labels.remove(0);
        StringBuilder builder = new StringBuilder();
        labels.forEach(s -> builder.append(s).append(' '));
        return builder.toString();
    }

    private void sendStackTrace(CommandSender sender, Exception exception) {
        String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);
        sender.sendMessage(Color.RED + "Message: " + rootCauseMessage);
        String cause = ExceptionUtils.getStackTrace(exception);
        StringTokenizer tokenizer = new StringTokenizer(cause);
        String exceptionType = "";
        String details = "";
        boolean parsingNeeded = false;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equalsIgnoreCase("Caused")) {
                tokenizer.nextToken();
                parsingNeeded = true;
                exceptionType = tokenizer.nextToken();
                continue;
            }
            if (token.equalsIgnoreCase("at") && parsingNeeded) {
                details = tokenizer.nextToken();
                break;
            }
        }
        sender.sendMessage(Color.RED + "Exception: " + exceptionType.replace(":", ""));
        sender.sendMessage(Color.RED + "Details:");
        sender.sendMessage(Color.RED + details);
    }

}
