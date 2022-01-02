package me.logwet.melange.commands.commands.alias;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.commands.commands.Command;
import me.logwet.melange.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AliasManager {
    public static final Map<String, List<String>> DEFAULT_ALIASES;
    public static final Map<String, List<String>> ALIASES;

    static {
        Builder<String, List<String>> builder = ImmutableMap.builder();

        for (Command command : CommandManager.COMMANDS) {
            if (command instanceof AbstractAliasCommand) {
                AbstractAliasCommand aliasedCommand = (AbstractAliasCommand) command;
                builder.put(
                        aliasedCommand.getRoot(),
                        ImmutableList.copyOf(aliasedCommand.getAliases()));
            }
        }

        DEFAULT_ALIASES = builder.build();

        ALIASES = new HashMap<>();
    }

    private AliasManager() {}

    @NotNull
    public static List<String> getDefault(String root) {
        return DEFAULT_ALIASES.get(root);
    }

    @Nullable
    public static List<String> getLocal(String root) {
        return ALIASES.get(root);
    }

    @NotNull
    public static List<String> getAliases(String root) {
        List<String> local = getLocal(root);
        if (Objects.nonNull(local)) {
            return local;
        }
        return getDefault(root);
    }

    public static void setAliases(String root, List<String> aliases) {
        ALIASES.put(root, aliases);
    }

    public static void clear() {
        ALIASES.clear();
    }

    public static Map<String, List<String>> getOverlap() {
        if (ALIASES.size() > 0) {
            Builder<String, List<String>> builder = ImmutableMap.builder();

            for (String root : DEFAULT_ALIASES.keySet()) {
                List<String> local = getLocal(root);
                if (Objects.nonNull(local)) {
                    builder.put(root, local);
                } else {
                    builder.put(root, getDefault(root));
                }
            }

            return builder.build();
        }

        return DEFAULT_ALIASES;
    }

    public static void save() {
        Config.setProperty("command_aliases", getOverlap());
    }

    public static void load() {
        //noinspection unchecked
        for (Entry<String, List<String>> entry :
                ((Map<String, List<String>>) Config.getProperty("command_aliases")).entrySet()) {
            if (!getDefault(entry.getKey()).equals(entry.getValue())) {
                setAliases(entry.getKey(), entry.getValue());
            }
        }
    }
}
