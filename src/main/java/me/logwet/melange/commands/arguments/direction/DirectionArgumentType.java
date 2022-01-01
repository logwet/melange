package me.logwet.melange.commands.arguments.direction;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DirectionArgumentType implements ArgumentType<Direction> {
    private static final SimpleCommandExceptionType EXPECTED_DIRECTION =
            new SimpleCommandExceptionType(() -> "Expected direction!");
    private static final SimpleCommandExceptionType INVALID_DIRECTION =
            new SimpleCommandExceptionType(() -> "Invalid direction!");

    private static final Collection<String> EXAMPLES =
            Arrays.asList("north", "east", "south", "west");

    private DirectionArgumentType() {}

    public static DirectionArgumentType direction() {
        return new DirectionArgumentType();
    }

    @Override
    public Direction parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();

        String value = reader.readString();
        if (value.isEmpty()) {
            throw EXPECTED_DIRECTION.createWithContext(reader);
        }

        Direction direction = Direction.fromString(value);
        if (Objects.isNull(direction)) {
            reader.setCursor(start);
            throw INVALID_DIRECTION.createWithContext(reader);
        }

        return direction;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
            CommandContext<S> context, SuggestionsBuilder builder) {
        String suggestion = Direction.getSuggestion(builder.getRemainingLowerCase());
        if (Objects.nonNull(suggestion)) {
            builder.suggest(suggestion);
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
