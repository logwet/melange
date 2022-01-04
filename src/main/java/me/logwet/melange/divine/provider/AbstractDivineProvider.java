package me.logwet.melange.divine.provider;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.logwet.melange.divine.filter.DivineFilter;

@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class AbstractDivineProvider implements DivineProvider {
    protected final List<DivineFilter> filters;

    protected void addFilter(DivineFilter filter) {
        filters.add(filter);
    }

    @Override
    public ImmutableList<DivineFilter> getFilters() {
        return ImmutableList.copyOf(filters);
    }
}
