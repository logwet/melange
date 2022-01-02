package me.logwet.melange.divine.provider.feature;

import java.util.Arrays;
import me.logwet.melange.divine.filter.angle.RangeAngleFilter;
import me.logwet.melange.divine.filter.distance.SliceDistanceFilter;
import me.logwet.melange.divine.provider.StrongholdProvider;

public class PlaceholderFeatureProvider extends StrongholdProvider {
    public PlaceholderFeatureProvider() {
        super(
                Arrays.asList(
                        new RangeAngleFilter(0, 0.5),
                        new RangeAngleFilter(0.8, 0.9),
                        new RangeAngleFilter(1.0, 2.55),
                        new RangeAngleFilter(2.66, 2.8),
                        new RangeAngleFilter(2.9, 3.8),
                        new RangeAngleFilter(4.2, 5.3),
                        new RangeAngleFilter(5.4, 5.5),
                        new SliceDistanceFilter(4, 1)));

        //                Arrays.asList(
        //                        new RangeAngleFilter(0, 0.6),
        //                        new RangeAngleFilter(0.7, 1.2),
        //                        new RangeAngleFilter(1.3, 1.7),
        //                        new RangeAngleFilter(1.8, 2.1),
        //                        new RangeAngleFilter(2.2, 2.4),
        //                        new RangeAngleFilter(2.5, 2.6),
        //                        new SliceDistanceFilter(4, 1)));

        //        super(Arrays.asList(new SliceDistanceFilter(4, 2)));
    }
}
