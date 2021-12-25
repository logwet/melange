package me.logwet.melange.divine.provider.feature;

import java.util.Arrays;
import me.logwet.melange.divine.filter.angle.RangeAngleFilter;
import me.logwet.melange.divine.filter.distance.SliceDistanceFilter;
import me.logwet.melange.divine.provider.StrongholdProvider;

public class PlaceholderFeature extends StrongholdProvider {
    public PlaceholderFeature() {
        super(Arrays.asList(new RangeAngleFilter(0.1, 0.5), new SliceDistanceFilter(4, 2)));
    }
}
