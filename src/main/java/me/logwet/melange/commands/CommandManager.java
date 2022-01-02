package me.logwet.melange.commands;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import lombok.SneakyThrows;
import me.logwet.melange.commands.commands.Command;
import me.logwet.melange.commands.commands.divine.AddCommand;
import me.logwet.melange.commands.commands.divine.RemoveCommand;
import me.logwet.melange.commands.commands.divine.coordinate.FossilCommand;
import me.logwet.melange.commands.commands.divine.direction.PortalCommand;
import me.logwet.melange.commands.commands.divine.placeholder.PlaceholderDivineCommand;
import me.logwet.melange.commands.parsing.ParseCache;
import me.logwet.melange.commands.provider.MelangeCommandProvider;
import me.logwet.melange.commands.provider.TwitchCommandProvider;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.commands.source.MelangeCommandSource;
import me.logwet.melange.config.Config;
import org.apache.commons.lang3.concurrent.CallableBackgroundInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

public class CommandManager implements AutoCloseable {
    public static final ImmutableList<Command> COMMANDS;
    protected static final Logger LOGGER = (Logger) LoggerFactory.getLogger(CommandManager.class);
    private static CallableBackgroundInitializer<CommandManager> initializer;

    static {
        COMMANDS =
                ImmutableList.of(
                        new PlaceholderDivineCommand(),
                        new FossilCommand(),
                        new PortalCommand(),
                        new AddCommand(),
                        new RemoveCommand());
    }

    public final MelangeCommandProvider melange;
    public final TwitchCommandProvider twitch;

    @Getter private final CommandDispatcher<CommandSource> dispatcher;
    private final ParseCache parser;

    private CommandManager(String prefix) {
        long start = System.currentTimeMillis();

        dispatcher = new CommandDispatcher<>();
        parser = new ParseCache(dispatcher);

        register(dispatcher);

        melange = new MelangeCommandProvider(this);
        twitch = new TwitchCommandProvider(prefix, this);

        long end = System.currentTimeMillis();

        LOGGER.info("Loaded commands: " + this);
        LOGGER.info("Initialized CommandManager in " + (end - start) + "ms");
    }

    private CommandManager() {
        this((String) Config.getProperty("command_prefix"));
    }

    public static void initialize() {
        initializer = new CallableBackgroundInitializer<>(CommandManager::new);
        initializer.start();
    }

    @Nullable
    public static CommandManager getUnchecked() {
        try {
            return initializer.get();
        } catch (ConcurrentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CommandManager get() {
        return Objects.requireNonNull(getUnchecked());
    }

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        for (Command command : COMMANDS) {
            //noinspection unchecked
            command.register(dispatcher);
        }
    }

    public ParseResults<CommandSource> parse(StringReader stringReader, CommandSource source) {
        ParseResults<CommandSource> parseResults = this.parser.parse(stringReader, source);
        System.out.println(parseResults.getContext().getRootNode().getName());
        return parseResults;
    }

    @SneakyThrows
    public Suggestions getSuggestions(StringReader stringReader, CommandSource source) {
        try {
            return dispatcher.getCompletionSuggestions(parse(stringReader, source)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Suggestions.empty().get();
    }

    public int execute(StringReader stringReader, CommandSource source) {
        try {
            return this.dispatcher.execute(parse(stringReader, source));
        } catch (CommandSyntaxException e) {
            LOGGER.error("Unable to parse command " + stringReader.getString(), e);
            source.sendError(
                    "Unable to parse command " + stringReader.getString() + ", " + e.getMessage());
        }

        return -1;
    }

    @Override
    public String toString() {
        return Arrays.toString(
                dispatcher.getAllUsage(dispatcher.getRoot(), new MelangeCommandSource(), false));
    }

    @Override
    public void close() {
        if (Objects.nonNull(melange)) {
            melange.close();
        }

        if (Objects.nonNull(twitch)) {
            twitch.close();
        }
    }
}
