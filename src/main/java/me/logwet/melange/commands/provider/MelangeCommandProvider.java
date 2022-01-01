package me.logwet.melange.commands.provider;

import com.mojang.brigadier.CommandDispatcher;
import me.logwet.melange.commands.source.CommandSource;

public class MelangeCommandProvider extends AbstractCommandProvider {
    public MelangeCommandProvider(CommandDispatcher<CommandSource> dispatcher) {
        super("", dispatcher);
    }

    @Override
    public Type getType() {
        return Type.MELANGE;
    }
}
