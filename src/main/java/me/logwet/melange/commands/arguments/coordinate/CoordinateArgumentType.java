package me.logwet.melange.commands.arguments.coordinate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.logwet.melange.commands.arguments.coordinate.Coordinate.Type;

public class CoordinateArgumentType implements ArgumentType<Coordinate> {
    private static final SimpleCommandExceptionType NOT_COMPLETE =
            new SimpleCommandExceptionType(() -> "Coordinate Argument is Not Complete!");

    private static final List<Collection<String>> EXAMPLES =
            Arrays.asList(
                    Arrays.asList("0", "14", "-5"),
                    Arrays.asList("0 0", "2 53", "-70 64"),
                    Arrays.asList("0 0 0", "-5 67 42", "98 54 -18"));

    private final Type type;

    private CoordinateArgumentType(Type type) {
        this.type = type;
    }

    public static CoordinateArgumentType coordinate(Type type) {
        return new CoordinateArgumentType(type);
    }

    @Override
    public Coordinate parse(StringReader reader) throws CommandSyntaxException {
        List<Integer> values = new ArrayList<>();

        int k = reader.getCursor();
        for (int i = 0; i < type.getNumValues(); i++) {
            values.add(reader.readInt());
            if (!reader.canRead() || reader.peek() != ' ') {
                reader.setCursor(k);
                throw NOT_COMPLETE.createWithContext(reader);
            }
            if (i < type.getNumValues() - 1) {
                reader.skip();
            }
        }

        if (values.size() < type.getNumValues()) {
            throw NOT_COMPLETE.createWithContext(reader);
        }

        int[] components = new int[3];

        for (int i = 0; i < 3; i++) {
            if (type.getDescriptor()[i]) {
                components[i] = values.remove(0);
            }
        }

        return Coordinate.from(components);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
            CommandContext<S> context, SuggestionsBuilder builder) {
        StringBuilder msg = new StringBuilder();

        for (int i = 0; i < type.getNumValues(); i++) {
            msg.append("0");
            if (i < type.getNumValues() - 1) {
                msg.append(" ");
            }
        }

        builder.suggest(msg.toString());

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES.get(type.getNumValues() - 1);
    }
}
