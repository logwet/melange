package me.logwet.melange.commands.parsing;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Objects;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import me.logwet.melange.commands.source.CommandSource;
import org.jetbrains.annotations.NotNull;

public class ParseCache {
    private final LoadingCache<ParseInputs, ParseResults<CommandSource>> cache;

    public ParseCache(CommandDispatcher<CommandSource> dispatcher) {
        cache = Caffeine.newBuilder().maximumSize(1024).build(new Loader(dispatcher));
    }

    public ParseResults<CommandSource> parse(ParseInputs key) {
        return cache.get(key);
    }

    public ParseResults<CommandSource> parse(String phrase, CommandSource source) {
        return parse(new ParseInputs(phrase, source));
    }

    private static class Loader implements CacheLoader<ParseInputs, ParseResults<CommandSource>> {
        private final CommandDispatcher<CommandSource> dispatcher;

        private Loader(CommandDispatcher<CommandSource> dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public @NotNull ParseResults<CommandSource> load(@NotNull ParseInputs key) {
            return dispatcher.parse(key.phrase, key.source);
        }
    }

    private static class ParseInputs {
        private final String phrase;
        private final CommandSource source;

        private ParseInputs(String phrase, CommandSource source) {
            this.phrase = phrase;
            this.source = source;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ParseInputs that = (ParseInputs) o;
            return Objects.equal(phrase, that.phrase) && Objects.equal(source, that.source);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(phrase, source);
        }
    }
}
