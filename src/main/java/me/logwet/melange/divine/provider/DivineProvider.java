package me.logwet.melange.divine.provider;

import com.google.common.collect.ImmutableList;
import me.logwet.melange.divine.filter.DivineFilter;

public interface DivineProvider {
    ImmutableList<DivineFilter> getFilters();

    boolean test(double t, double r, int s);
}
