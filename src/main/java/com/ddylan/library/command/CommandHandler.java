package com.ddylan.library.command;

import com.ddylan.library.LibraryPlugin;
import com.ddylan.library.command.bukkit.ExtendedCommand;
import com.ddylan.library.command.bukkit.ExtendedCommandMap;
import com.ddylan.library.command.bukkit.ExtendedHelpTopic;
import com.ddylan.library.command.defaults.*;
import com.ddylan.library.command.parameter.*;
import com.ddylan.library.command.util.EasyClass;
import com.ddylan.library.util.ClassUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class CommandHandler {

    @Getter(AccessLevel.NONE) public static final CommandNode ROOT_NODE = new CommandNode();
    protected Map<Class<?>, ParameterType<?>> PARAMETER_TYPE_MAP = new HashMap<>();
    protected CommandMap commandMap;
    protected Map<String, Command> knownCommands;
    private CommandConfiguration configuration = new CommandConfiguration().setNoPermissionMessage("&cNo permission.").setHiddenMessage("Unknown command. Type \"/help\" for help.");

    public CommandHandler() {
        registerParameterType(Boolean.class, new BooleanParameterType());
        registerParameterType(Double.class, new DoubleParameterType());
        registerParameterType(Float.class, new FloatParameterType());
        registerParameterType(GameMode.class, new GameModeParameterType());
        registerParameterType(Integer.class, new IntegerParameterType());
        registerParameterType(Long.class, new LongParameterType());
        registerParameterType(Player.class, new PlayerParameterType());
        registerParameterType(UUID.class, new UUIDParameterType());
        registerParameterType(World.class, new WorldParameterType());
        registerParameterType(String.class, new StringParameterType());

        commandMap = getCommandMap();
        knownCommands = getKnownCommands();

        //  register command classes
        registerClass(BuildCommand.class);
        registerClass(EvalCommand.class);
        registerClass(VisibilityDebugCommand.class);
        registerClass(GamemodeCommand.class);
        registerClass(MoreCommand.class);

        (new BukkitRunnable() {
            public void run() {
                try {
                    swapCommandMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).runTaskLater(LibraryPlugin.getInstance(), 5L);
    }

    public void registerParameterType(Class<?> clazz, ParameterType<?> type) {
        PARAMETER_TYPE_MAP.put(clazz, type);
    }

    public ParameterType getParameterType(Class<?> clazz) {
        return PARAMETER_TYPE_MAP.get(clazz);
    }

    public CommandConfiguration getConfig() {
        return configuration;
    }

    public void setConfig(CommandConfiguration configuration) {
        LibraryPlugin.getInstance().getCommandHandler().configuration = configuration;
    }

    public void registerMethod(Method method) {
        method.setAccessible(true);
        Set<CommandNode> nodes = (new MethodProcessor()).process(method);
        if (nodes != null)
            nodes.forEach(node -> {
                if (node != null) {
                    ExtendedCommand command = new ExtendedCommand(node, JavaPlugin.getProvidingPlugin(method.getDeclaringClass()));
                    register(command);
                }
            });
    }

    protected void registerHelpTopic(CommandNode node, Set<String> aliases) {
        if (node.method != null)
            Bukkit.getHelpMap().addTopic(new ExtendedHelpTopic(node, aliases));
        if (node.hasCommands())
            node.getChildren().values().forEach(n -> registerHelpTopic(n, null));
    }

    private void register(ExtendedCommand command) {
        try {
            Map<String, Command> knownCommands = getKnownCommands();
            Iterator<Map.Entry<String, Command>> iterator = knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Command> entry = iterator.next();
                if (entry.getValue().getName().equalsIgnoreCase(command.getName())) {
                    entry.getValue().unregister(commandMap);
                    iterator.remove();
                }
            }
            for (String alias : command.getAliases())
                knownCommands.put(alias, command);
            command.register(commandMap);
            knownCommands.put(command.getName(), command);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void registerClass(Class<?> clazz) {
        for (Method method : clazz.getMethods())
            registerMethod(method);
    }

    public void unregisterClass(Class<?> clazz) {
        Map<String, Command> knownCommands = getKnownCommands();
        Iterator<Command> iterator = knownCommands.values().iterator();
        while (iterator.hasNext()) {
            Command command = iterator.next();
            if (!(command instanceof ExtendedCommand))
                continue;
            CommandNode node = ((ExtendedCommand)command).getNode();
            if (node.getOwningClass() == clazz) {
                command.unregister(commandMap);
                iterator.remove();
            }
        }
    }

    public void registerPackage(Plugin plugin, String packageName) {
        ClassUtils.getClassesInPackage(plugin, packageName).forEach(this::unregisterClass);
    }

    public void unregisterPackage(Plugin plugin, String packageName) {
        ClassUtils.getClassesInPackage(plugin, packageName).forEach(this::unregisterClass);
    }

    public void registerAll(Plugin plugin) {
        registerPackage(plugin, plugin.getClass().getPackage().getName());
    }

    public void unregisterAll(Plugin plugin) {
        unregisterPackage(plugin, plugin.getClass().getPackage().getName());
    }

    private void swapCommandMap() throws Exception {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        Object oldCommandMap = commandMapField.get(Bukkit.getServer());
        ExtendedCommandMap newCommandMap = new ExtendedCommandMap(Bukkit.getServer());
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & 0xFFFFFFEF);
        knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap));
        commandMapField.set(Bukkit.getServer(), newCommandMap);
    }

    protected CommandMap getCommandMap() {
        return (CommandMap)(new EasyClass<>(Bukkit.getServer())).getField("commandMap").get();
    }

    protected Map<String, Command> getKnownCommands() {
        return (Map<String, Command>)(new EasyClass<>(commandMap).getField("knownCommands").get());
    }

}
