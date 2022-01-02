package me.logwet.melange.commands.commands.divine.direction;

import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.action.PortalProvider;

public class PortalCommand extends AbstractDirectionCommand<PortalProvider> {
    public PortalCommand() {
        super(PortalProvider.class, "portal", "ptal", "port", "ptl");
    }

    @Override
    public int add(CommandContext<CommandSource> context) {
        return 0;
    }
}
