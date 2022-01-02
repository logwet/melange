package me.logwet.melange.divine.provider;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.logwet.melange.divine.filter.DivineFilter;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractDivineProvider that = (AbstractDivineProvider) o;
        return Objects.equal(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(filters);
    }
}
