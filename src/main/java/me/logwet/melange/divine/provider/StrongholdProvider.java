package me.logwet.melange.divine.provider;

import java.util.List;
import me.logwet.melange.divine.filter.DivineFilter;

public abstract class StrongholdProvider extends AbstractDivineProvider {
    public StrongholdProvider(List<DivineFilter> filters) {
        super(filters);
    }
}
