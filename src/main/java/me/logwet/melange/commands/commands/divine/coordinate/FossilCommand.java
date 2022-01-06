package me.logwet.melange.commands.commands.divine.coordinate;

import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.arguments.coordinate.Coordinate;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.feature.FossilFeatureProvider;

public class FossilCommand extends AbstractXCoordinateCommand<FossilFeatureProvider> {
    public FossilCommand() {
        super(FossilFeatureProvider.class, "fossil", "boner", "bone", "fsl");
    }

    @Override
    public int add(CommandContext<CommandSource> context) {
        Coordinate coordinate = context.getArgument("x", Coordinate.class);
        return 0;
    }
}
