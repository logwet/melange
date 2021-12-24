package me.logwet.melange.divine.filter.angle;

import java.util.Collections;
import java.util.List;
import me.logwet.melange.divine.filter.DivineFilter;

public interface AngleFilter extends DivineFilter {
    @Override
    default Type getType() {
        return Type.ANGLE;
    }

    @Override
    default List<Integer> getTargets() {
        return Collections.singletonList(0);
    }
}
