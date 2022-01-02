package me.logwet.melange.commands.arguments.coordinate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class Coordinate {
    @Nullable private final Integer x;
    @Nullable private final Integer y;
    @Nullable private final Integer z;

    @NotNull private final Type type;

    private Coordinate(
            @Nullable Integer x, @Nullable Integer y, @Nullable Integer z, @NotNull Type type) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.type = type;
    }

    private Coordinate(@Nullable Integer x, @Nullable Integer y, @Nullable Integer z) {
        this(x, y, z, Type.from(x, y, z));
    }

    public static Coordinate from(@Nullable Integer x, @Nullable Integer y, @Nullable Integer z) {
        return new Coordinate(x, y, z);
    }

    public static Coordinate fromXYZ(int x, int y, int z) {
        return new Coordinate(x, y, z, Type.XYZ);
    }

    public static Coordinate fromXZ(int x, int z) {
        return new Coordinate(x, null, z, Type.XZ);
    }

    public static Coordinate fromX(int x) {
        return new Coordinate(x, null, null, Type.X);
    }

    public static Coordinate fromY(int y) {
        return new Coordinate(null, y, null, Type.Y);
    }

    public static Coordinate fromZ(int z) {
        return new Coordinate(null, null, z, Type.Z);
    }

    public static Coordinate from(int[] array) {
        return new Coordinate(array[0], array[1], array[2]);
    }

    public static Coordinate from(int[] array, Type type) {
        return new Coordinate(array[0], array[1], array[2], type);
    }

    public enum Type {
        XYZ(new boolean[] {true, true, true}),
        XY(new boolean[] {true, true, false}),
        XZ(new boolean[] {true, false, true}),
        X(new boolean[] {true, false, false}),
        YZ(new boolean[] {false, true, true}),
        Y(new boolean[] {false, true, false}),
        Z(new boolean[] {false, false, true}),
        NONE(new boolean[] {false, false, false});

        private static final ImmutableMap<Integer, Type> MAP;

        static {
            Builder<Integer, Type> builder = ImmutableMap.builder();

            for (Type type : Type.values()) {
                builder.put(Arrays.hashCode(type.descriptor), type);
            }

            MAP = builder.build();
        }

        @Getter private final int numValues;
        @Getter private final boolean[] descriptor;

        Type(boolean[] descriptor) {
            int n = 0;
            for (boolean b : descriptor) {
                if (b) {
                    n++;
                }
            }
            this.numValues = n;
            this.descriptor = descriptor;
        }

        public static Type from(boolean[] descriptor) {
            return MAP.get(Arrays.hashCode(descriptor));
        }

        public static Type from(@Nullable Integer x, @Nullable Integer y, @Nullable Integer z) {
            return from(new boolean[] {Objects.nonNull(x), Objects.nonNull(y), Objects.nonNull(z)});
        }

        @Override
        public String toString() {
            List<String> values = new ArrayList<>();
            if (descriptor[0]) {
                values.add("x");
            }
            if (descriptor[1]) {
                values.add("y");
            }
            if (descriptor[2]) {
                values.add("z");
            }
            return String.join(" ", values);
        }
    }
}
