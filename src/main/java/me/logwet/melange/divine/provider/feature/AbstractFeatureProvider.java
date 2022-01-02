package me.logwet.melange.divine.provider.feature;

import java.util.List;
import me.logwet.melange.divine.filter.DivineFilter;
import me.logwet.melange.divine.provider.StrongholdProvider;

public abstract class AbstractFeatureProvider extends StrongholdProvider {
    public AbstractFeatureProvider(List<DivineFilter> filters) {
        super(filters);
    }
}
