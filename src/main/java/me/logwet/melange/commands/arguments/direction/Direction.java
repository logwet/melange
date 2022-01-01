package me.logwet.melange.commands.arguments.direction;

import com.google.common.collect.ImmutableMap;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public enum Direction {
    NORTH(Axis.Z, AxisDirection.NEGATIVE),
    SOUTH(Axis.Z, AxisDirection.POSITIVE),
    WEST(Axis.X, AxisDirection.NEGATIVE),
    EAST(Axis.X, AxisDirection.POSITIVE);

    private static final ImmutableMap<String, Direction> MAP;

    static {
        ImmutableMap.Builder<String, Direction> builder = ImmutableMap.builder();
        for (Direction direction : values()) {
            builder.put(direction.name().toLowerCase(Locale.ROOT), direction);
        }
        MAP = builder.build();
    }

    private final int[] data;

    Direction(Axis axis, AxisDirection axisDirection) {
        data = new int[2];
        data[axis.index] = axisDirection.sign;
    }

    public static Direction fromString(String name) {
        return MAP.get(name.toLowerCase(Locale.ROOT));
    }

    @Nullable
    public static String getSuggestion(String snippet) {
        for (String direction : MAP.keySet()) {
            if (direction.startsWith(snippet)) {
                return direction;
            }
        }

        return null;
    }

    public int getX() {
        return data[0];
    }

    public int getZ() {
        return data[1];
    }

    enum Axis {
        X(0),
        Z(1);

        private final int index;

        Axis(int index) {
            this.index = index;
        }
    }

    enum AxisDirection {
        POSITIVE(1),
        NEGATIVE(-1);

        private final int sign;

        AxisDirection(int sign) {
            this.sign = sign;
        }
    }
}
