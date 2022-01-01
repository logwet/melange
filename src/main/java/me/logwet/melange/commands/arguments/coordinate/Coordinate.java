package me.logwet.melange.commands.arguments.coordinate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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

    enum Type {
        XYZ(7),
        XY(6),
        XZ(5),
        X(4),
        YZ(3),
        Y(2),
        Z(1),
        NONE(0);

        private static final ImmutableMap<Integer, Type> MAP;

        static {
            Builder<Integer, Type> builder = ImmutableMap.builder();

            for (Type type : Type.values()) {
                builder.put(type.id, type);
            }

            MAP = builder.build();
        }

        @Getter private final int id;

        Type(int id) {
            this.id = id;
        }

        public static Type from(int id) {
            return MAP.get(id);
        }

        public static Type from(@Nullable Integer x, @Nullable Integer y, @Nullable Integer z) {
            int t = 0;
            if (Objects.nonNull(x)) {
                t ^= 4;
            }
            if (Objects.nonNull(y)) {
                t ^= 2;
            }
            if (Objects.nonNull(z)) {
                t ^= 1;
            }
            return from(t);
        }

        public int getNumValues() {
            return Integer.bitCount(id);
        }

        public boolean[] getDescriptor() {
            boolean[] descriptor = new boolean[3];

            for (int i = 0; i < 3; i++) {
                descriptor[i] = ((id >> i) & 1) != 0;
            }

            return descriptor;
        }
    }
}
