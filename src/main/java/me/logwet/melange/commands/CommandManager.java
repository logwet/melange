package me.logwet.melange.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import lombok.SneakyThrows;
import me.logwet.melange.commands.commands.Command;
import me.logwet.melange.commands.commands.divine.AddCommand;
import me.logwet.melange.commands.commands.divine.PlaceholderDivineCommand;
import me.logwet.melange.commands.commands.divine.RemoveCommand;
import me.logwet.melange.commands.parse.Parser;
import me.logwet.melange.commands.provider.MelangeCommandProvider;
import me.logwet.melange.commands.provider.TwitchCommandProvider;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.commands.source.CommandSource.Role;
import me.logwet.melange.commands.source.MelangeCommandSource;
import me.logwet.melange.commands.source.TwitchCommandSource;
import me.logwet.melange.config.Config;

public class CommandManager {
    public static final ImmutableList<Command> COMMANDS;

    static {
        COMMANDS =
                ImmutableList.of(
                        new PlaceholderDivineCommand(), new AddCommand(), new RemoveCommand());
    }

    public final MelangeCommandProvider melangeCommandProvider;
    public final TwitchCommandProvider twitchCommandProvider;

    @Getter private final CommandDispatcher<CommandSource> dispatcher;
    private final Parser parser;

    public CommandManager(String prefix) {
        long start = System.currentTimeMillis();

        dispatcher = new CommandDispatcher<>();
        parser = new Parser(dispatcher);

        register(dispatcher);

        melangeCommandProvider = new MelangeCommandProvider(dispatcher);
        twitchCommandProvider = new TwitchCommandProvider(prefix, dispatcher);

        long end = System.currentTimeMillis();

        System.out.println("Initialized CommandManager in " + (end - start) + "ms");
    }

    public CommandManager() {
        this((String) Config.getProperty("command_prefix"));
    }

    public void register(CommandDispatcher<CommandSource> dispatcher) {
        for (Command command : COMMANDS) {
            //noinspection unchecked
            command.register(dispatcher);
        }
    }

    public ParseResults<CommandSource> parse(String phrase, CommandSource source) {
        return this.parser.parse(phrase, source);
    }

    public ParseResults<CommandSource> parse(String phrase) {
        return parse(phrase, new MelangeCommandSource());
    }

    public ParseResults<CommandSource> parse(String phrase, String username, Role role) {
        return parse(phrase, new TwitchCommandSource(username, role));
    }

    @SneakyThrows
    public Suggestions getSuggestions(String phrase, CommandSource source) {
        try {
            return dispatcher.getCompletionSuggestions(parse(phrase, source)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Suggestions.empty().get();
    }

    public Suggestions getSuggestions(String phrase) {
        return getSuggestions(phrase, new MelangeCommandSource());
    }

    public Suggestions getSuggestions(String phrase, String username, Role role) {
        return getSuggestions(phrase, new TwitchCommandSource(username, role));
    }

    public int execute(String phrase, CommandSource source) {
        try {
            return this.dispatcher.execute(parse(phrase, source));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int execute(String phrase) {
        return execute(phrase, new MelangeCommandSource());
    }

    public int execute(String phrase, String username, Role role) {
        return execute(phrase, new TwitchCommandSource(username, role));
    }

    @Override
    public String toString() {
        return Arrays.toString(
                dispatcher.getAllUsage(dispatcher.getRoot(), new MelangeCommandSource(), false));
    }
}
