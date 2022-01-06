package me.logwet.melange.divine.provider.feature;

import java.util.Collections;
import java.util.List;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.divine.filter.DivineFilter;
import me.logwet.melange.divine.filter.angle.RangeAngleFilter;

public abstract class DecoratorFeatureProvider extends AbstractFeatureProvider {
    public DecoratorFeatureProvider(long salt, int xLb, int xUb, int zLb, int zUb) {
        super(genFilters(salt, xLb, xUb, zLb, zUb));
    }

    protected static List<DivineFilter> genFilters(long salt, int xLb, int xUb, int zLb, int zUb) {
        double sixteenth = MelangeConstants.TWO_PI / 16;

        return Collections.singletonList(RangeAngleFilter.range(xLb * sixteenth, xUb * sixteenth));
    }
}
