package me.logwet.melange.commands.commands.divine.coordinate;

import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.arguments.coordinate.Coordinate;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.feature.FossilFeatureProvider;

public class FossilCommand extends AbstractMultiCoordinateCommand<FossilFeatureProvider> {
    public FossilCommand() {
        super(
                new Coordinate.Type[] {Coordinate.Type.X, Coordinate.Type.XZ},
                FossilFeatureProvider.class,
                "fossil",
                "boner",
                "bone",
                "fsl");
    }

    @Override
    protected int addWithCoordinateType(
            Coordinate.Type coordinateType, CommandContext<CommandSource> context) {
        Coordinate coordinate = context.getArgument(coordinateType.toString(), Coordinate.class);
        addAndWait(FossilFeatureProvider.build(coordinate));

        return 0;
    }
}
