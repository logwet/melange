package me.logwet.melange.divine.provider;

import com.google.common.collect.ImmutableList;
import java.util.List;
import me.logwet.melange.divine.filter.DivineFilter;

public abstract class AbstractDivineProvider implements DivineProvider {
    protected List<DivineFilter> filters;

    public AbstractDivineProvider(List<DivineFilter> filters) {
        this.filters = filters;
    }

    protected void addFilter(DivineFilter filter) {
        filters.add(filter);
    }

    @Override
    public ImmutableList<DivineFilter> getFilters() {
        return ImmutableList.copyOf(filters);
    }
}
