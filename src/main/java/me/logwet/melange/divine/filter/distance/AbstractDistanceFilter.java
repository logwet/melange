package me.logwet.melange.divine.filter.distance;

import java.util.List;
import me.logwet.melange.divine.filter.AbstractDivineFilter;

public abstract class AbstractDistanceFilter extends AbstractDivineFilter
        implements DistanceFilter {
    protected final List<Integer> targets;

    public AbstractDistanceFilter(Policy policy, List<Integer> targets) {
        super(policy);
        this.targets = targets;
    }

    @Override
    public List<Integer> getTargets() {
        return targets;
    }
}
