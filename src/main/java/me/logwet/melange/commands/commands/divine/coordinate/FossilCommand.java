package me.logwet.melange.commands.commands.divine.coordinate;

import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.feature.FossilFeature;

public class FossilCommand extends AbstractXCoordinateCommand<FossilFeature> {
    public FossilCommand() {
        super(FossilFeature.class, "fossil", "boner", "bone", "fsl");
    }

    @Override
    public int add(CommandContext<CommandSource> context) {
        return 0;
    }
}
