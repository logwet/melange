package me.logwet.melange.divine.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import lombok.EqualsAndHashCode;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.divine.filter.DivineFilter;
import me.logwet.melange.divine.filter.DivineFilter.Type;
import me.logwet.melange.divine.filter.angle.RangeAngleFilter;
import me.logwet.melange.divine.filter.distance.RangeDistanceFilter;

@EqualsAndHashCode
public abstract class AbstractDivineProvider implements DivineProvider {
    protected final ImmutableList<DivineFilter> filters;
    protected final List<DivineFilter> angleFilters;
    protected final List<DivineFilter> distanceFilters;

    protected AbstractDivineProvider(List<DivineFilter> filters) {
        Builder<DivineFilter> angleFilterBuilder = ImmutableList.builder();
        Builder<DivineFilter> distanceFilterBuilder = ImmutableList.builder();

        int numAngleFilters = 0;
        int numDistanceFilters = 0;

        for (DivineFilter divineFilter : filters) {
            if (divineFilter.getType() == Type.ANGLE) {
                angleFilterBuilder.add(divineFilter);
                numAngleFilters++;
            } else if (divineFilter.getType() == Type.DISTANCE) {
                distanceFilterBuilder.add(divineFilter);
                numDistanceFilters++;
            }
        }

        if (numAngleFilters == 0) {
            angleFilterBuilder.add(RangeAngleFilter.range(0D, 0D));
        }

        if (numDistanceFilters == 0) {
            distanceFilterBuilder.add(
                    RangeDistanceFilter.range(
                            MelangeConstants.LOWER_BOUND, MelangeConstants.UPPER_BOUND));
        }

        angleFilters = angleFilterBuilder.build();
        distanceFilters = distanceFilterBuilder.build();

        Builder<DivineFilter> builder = ImmutableList.builder();
        builder.addAll(angleFilters).addAll(distanceFilters);

        this.filters = builder.build();
    }

    @Override
    public ImmutableList<DivineFilter> getFilters() {
        return filters;
    }

    @Override
    public boolean test(double t, double r, int s) {
        int as = 0;
        int ds = 0;

        for (DivineFilter angleFilter : angleFilters) {
            if (angleFilter.test(t, s)) {
                as++;
            }
        }

        for (DivineFilter distanceFilter : distanceFilters) {
            if (distanceFilter.test(r, s)) {
                ds++;
            }
        }

        return as > 0 && ds > 0;
    }
}
