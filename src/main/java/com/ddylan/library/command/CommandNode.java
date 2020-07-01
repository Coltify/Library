package com.ddylan.library.command;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.util.Color;
import com.ddylan.library.util.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.*;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommandNode {

    /**
     * Compact contstructor.
     *
     * @param name command name
     * @param permission required permission
     */
    public CommandNode(@NonNull String name, @NonNull String permission) {
        if (name == null)
            throw new NullPointerException("name");
        if (permission == null)
            throw new NullPointerException("permission");
        this.name = name;
        this.permission = permission;
    }

    public CommandNode(Class<?> owningClass) {
        this.owningClass = owningClass;
    }

    @NonNull
    private String name;
    private Set<String> aliases = new HashSet<>();
    @NonNull
    private String permission;
    private String description = "default command description";
    private boolean async = false, hidden = false;
    protected Method method;
    protected Class<?> owningClass;
    private List<String> validFlags = new ArrayList<>();
    private List<Data> parameters = new ArrayList<>();
    private Map<String, CommandNode> children = new HashMap<>();
    private CommandNode parent;
    private boolean logToConsole;

    /**
     * Register sub command to this tree.
     *
     * @param node sub command
     */
    public void registerCommand(CommandNode node) {
        node.setParent(this);
        children.put(node.getName(), node);
    }

    /**
     * Check for command existence.
     *
     * @param name command name
     * @return true if the command exists
     */
    public boolean hasCommand(String name) {
        return children.containsKey(name);
    }

    /**
     * Get command node {@link Object}
     *
     * @param name command name
     * @return command node object
     */
    public CommandNode getCommand(String name) {
        return children.get(name);
    }

    /**
     * Check for any command existence.
     *
     * @return true if any sub commands exist
     */
    public boolean hasCommands() {
        return children.size() > 0;
    }

    /**
     * Find command node {@link Object}
     * @param arguments
     * @return
     */
    public CommandNode findCommand(Arguments arguments) {
        if (arguments.getArguments().size() > 0) {
            String trySub = arguments.getArguments().get(0);
            if (hasCommand(trySub)) {
                arguments.getArguments().remove(0);
                CommandNode returnNode = getCommand(trySub);
                return returnNode.findCommand(arguments);
            }
        }
        return this;
    }

    /**
     * Check for valid flag on command.
     *
     * @param flag flag to be checked
     * @return if flag is valid
     */
    public boolean isValidFlag(String flag) {
        return validFlags.contains(flag) || validFlags.contains(flag.toLowerCase());
    }

    /**
     * Soft permission check.
     *
     * @param sender sender to be checked
     * @return true if sender has access to this node
     */
    public boolean canUse(CommandSender sender) {
        if (permission.isEmpty()) {
            return true;
        }

        switch (permission) {
            case "console":
                return sender instanceof ConsoleCommandSender;
            case "op":
                return sender.isOp();
            case "":
                return true;
        }
        return sender.hasPermission(permission);
    }

    /**
     * Get the command's {@link mkremins.fanciful.FancyMessage}
     * object for proper usage error text.
     *
     * @param realLabel command name
     * @return {@link FancyMessage} containing usage
     */
    public FancyMessage getUsage(String realLabel) {
        FancyMessage usage = (new FancyMessage("Usage: /" + realLabel)).color(ChatColor.RED);
        if (!Strings.isNullOrEmpty(getDescription()))
            usage.tooltip(Color.YELLOW + getDescription());
        List<FlagData> flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter(data -> data instanceof FlagData).map(data -> (FlagData)data).collect(Collectors.toList()));
        List<ParameterData> parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter(data -> data instanceof ParameterData).map(data -> (ParameterData)data).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty(getDescription()))
                usage.tooltip(Color.YELLOW + getDescription());
            for (FlagData data : flags) {
                String name = data.getNames().get(0);
                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED);
                    if (!Strings.isNullOrEmpty(getDescription()))
                        usage.tooltip(Color.YELLOW + getDescription());
                }
                flagFirst = false;
                usage.then("-" + name).color(ChatColor.AQUA);
                if (!Strings.isNullOrEmpty(data.getDescription()))
                    usage.tooltip(Color.GRAY + data.getDescription());
            }
            usage.then(") ").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty(getDescription()))
                usage.tooltip(Color.YELLOW + getDescription());
        }
        if (!parameters.isEmpty())
            for (int index = 0; index < parameters.size(); index++) {
                ParameterData data = parameters.get(index);
                boolean required = data.getDefaultValue().isEmpty();
                usage.then((required ? "<" : "[") + data.getName() + (data.isWildcard() ? "..." : "") + (required ? ">" : "]") + ((index != parameters.size() - 1) ? " " : "")).color(ChatColor.RED);
                if (!Strings.isNullOrEmpty(getDescription()))
                    usage.tooltip(Color.YELLOW + getDescription());
            }
        return usage;
    }

    /**
     * Get the command's {@link mkremins.fanciful.FancyMessage}
     * object for proper usage error text.
     *
     * @return {@link FancyMessage} containing usage
     */
    public FancyMessage getUsage() {
        FancyMessage usage = new FancyMessage("");
        List<FlagData> flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter(data -> data instanceof FlagData).map(data -> (FlagData)data).collect(Collectors.toList()));
        List<ParameterData> parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter(data -> data instanceof ParameterData).map(data -> (ParameterData)data).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);
            for (FlagData data : flags) {
                String name = data.getNames().get(0);
                if (!flagFirst)
                    usage.then(" | ").color(ChatColor.RED);
                flagFirst = false;
                usage.then("-" + name).color(ChatColor.AQUA);
                if (!Strings.isNullOrEmpty(data.getDescription()))
                    usage.tooltip(Color.GRAY + data.getDescription());
            }
            usage.then(") ").color(ChatColor.RED);
        }
        if (!parameters.isEmpty())
            for (int index = 0; index < parameters.size(); index++) {
                ParameterData data = parameters.get(index);
                boolean required = data.getDefaultValue().isEmpty();
                usage.then((required ? "<" : "[") + data.getName() + (data.isWildcard() ? "..." : "") + (required ? ">" : "]") + ((index != parameters.size() - 1) ? " " : "")).color(ChatColor.RED);
            }
        return usage;
    }

    /**
     * Invoke the command with arguments.
     *
     * @param sender command sender
     * @param arguments command arguments {@link Arguments}
     * @return true if command processed
     * @throws CommandException command exception
     */
    public boolean invoke(CommandSender sender, Arguments arguments) throws CommandException {
        if (method == null) {
            if (hasCommands()) {
                if (getSubCommands(sender, true).isEmpty()) {
                    if (isHidden()) {
                        sender.sendMessage("Unknown command. Type \"/help\" for help.");
                    } else {
                        sender.sendMessage(Color.translate("&cNo permission."));
                    }
                } else {
                    sender.sendMessage("Unknown command. Type \"/help\" for help.");
                }
            }
            return true;
        }
        List<Object> objects = new ArrayList(this.method.getParameterCount());
        objects.add(sender);
        int index = 0;
        for (Data unknownData : this.parameters) {
            if (unknownData instanceof FlagData) {
                FlagData flagData = (FlagData)unknownData;
                boolean value = flagData.isDefaultValue();
                for (String s : flagData.getNames()) {
                    if (arguments.hasFlag(s)) {
                        value = !value;
                        break;
                    }
                }
                objects.add(flagData.getMethodIndex(), Boolean.valueOf(value));
                continue;
            }
            if (unknownData instanceof ParameterData) {
                String argument;
                ParameterData parameterData = (ParameterData)unknownData;
                try {
                    argument = arguments.getArguments().get(index);
                } catch (Exception ex) {
                    if (!parameterData.getDefaultValue().isEmpty()) {
                        argument = parameterData.getDefaultValue();
                    } else {
                        return false;
                    }
                }
                if (parameterData.isWildcard() && (argument.isEmpty() || !argument.equals(parameterData.getDefaultValue())))
                    argument = arguments.join(index);
                ParameterType<?> type = LibraryPlugin.getInstance().getCommandHandler().getParameterType(parameterData.getType());
                if (parameterData.getParameterType() != null)
                    try {
                        type = parameterData.getParameterType().newInstance();
                    } catch (InstantiationException|IllegalAccessException e) {
                        e.printStackTrace();
                        throw new CommandException("Failed to create ParameterType instance: " + parameterData.getParameterType().getName(), e);
                    }
                if (type == null) {
                    Class<?> t = (parameterData.getParameterType() == null) ? parameterData.getType() : parameterData.getParameterType();
                    sender.sendMessage(Color.RED + "No parameter type found: " + t.getSimpleName());
                    return true;
                }
                Object result = type.transform(sender, argument);
                if (result == null)
                    return true;
                objects.add(parameterData.getMethodIndex(), result);
                index++;
            }
        }
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            this.method.invoke(null, objects.toArray());
            stopwatch.stop();
            int executionThreshold = LibraryPlugin.getInstance().getConfig().getInt("Command.TimeThreshold", 10);
            if (!this.async && this.logToConsole && stopwatch.elapsed() >= executionThreshold)
                LibraryPlugin.getInstance().getLogger().warning("Command '/" + getFullLabel() + "' took " + stopwatch.elapsed() + "ms!");
            return true;
        } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            throw new CommandException("An error occurred while executing the command", e);
        }
    }

    /**
     * Get sub commands of current node.
     *
     * @param sender command sender
     * @param print if true print sub commands
     * @return {@link List} of sub command labels
     */
    public List<String> getSubCommands(CommandSender sender, boolean print) {
        List<String> commands = new ArrayList<>();
        if (canUse(sender)) {
            String command = ((sender instanceof org.bukkit.entity.Player) ? "/" : "") + getFullLabel() + ((this.parameters != null) ? (" " + getUsage().toOldMessageFormat()) : "") + (!Strings.isNullOrEmpty(this.description) ? (Color.GRAY + " - " + getDescription()) : "");
            if (this.parent == null) {
                commands.add(command);
            } else if (this.parent.getName() != null &&
                    LibraryPlugin.getInstance().getCommandHandler().ROOT_NODE.getCommand(this.parent.getName()) != this.parent) {
                commands.add(command);
            }
            if (hasCommands())
                for (CommandNode n : getChildren().values())
                    commands.addAll(n.getSubCommands(sender, false));
        }
        if (!commands.isEmpty() && print) {
            sender.sendMessage(Color.BLUE + Color.STRIKE_THROUGH + StringUtils.repeat("-", 35));
            for (String command : commands)
                sender.sendMessage(Color.RED + command);
            sender.sendMessage(Color.BLUE + Color.STRIKE_THROUGH + StringUtils.repeat("-", 35));
        }
        return commands;
    }

    /**
     * Grab a list of this node's real aliases.
     *
     * @return set of real aliases
     */
    public Set<String> getRealAliases() {
        Set<String> aliases = getAliases();
        aliases.remove(getName());
        return aliases;
    }

    /**
     * Grab the full label of this node.
     *
     * @return full command label
     */
    public String getFullLabel() {
        List<String> labels = new ArrayList<>();
        CommandNode node = this;
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
        return builder.toString().trim();
    }

    /**
     * Don't know.
     *
     * @return help topic
     */
    public String getUsageForHelpTopic() {
        if (this.method != null && this.parameters != null)
            return "/" + getFullLabel() + " " + Color.translate(getUsage().toOldMessageFormat());
        return "";
    }
    
}
