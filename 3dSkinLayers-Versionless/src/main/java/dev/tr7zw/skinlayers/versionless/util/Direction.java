package dev.tr7zw.skinlayers.versionless.util;

public enum Direction {
    DOWN(Axis.Y, 0, -1, 0), UP(Axis.Y, 0, 1, 0), NORTH(Axis.Z, 0, 0, -1), SOUTH(Axis.Z, 0, 0, 1),
    WEST(Axis.X, -1, 0, 0), EAST(Axis.X, 1, 0, 0);

    private static Direction[] opposite = new Direction[] { UP, DOWN, SOUTH, NORTH, EAST, WEST };

    private final Axis axis;
    private final int x, y, z;

    Direction(Axis axis, int x, int y, int z) {
        this.axis = axis;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Direction getOpposite() {
        return opposite[this.ordinal()];
    }

    public Axis getAxis() {
        return axis;
    }

    public int getStepX() {
        return x;
    }

    public int getStepY() {
        return y;
    }

    public int getStepZ() {
        return z;
    }

    public int getDirStep() {
        return x + y + z;
    }

    public enum Axis {
        X {
            public int choose(int i, int j, int k) {
                return i;
            }

            public double choose(double d, double e, double f) {
                return d;
            }
        },
        Y {
            public int choose(int i, int j, int k) {
                return j;
            }

            public double choose(double d, double e, double f) {
                return e;
            }
        },
        Z {
            public int choose(int i, int j, int k) {
                return k;
            }

            public double choose(double d, double e, double f) {
                return f;
            }
        };

        public static Axis[] VALUES = values();

        public abstract int choose(int param1Int1, int param1Int2, int param1Int3);

        public abstract double choose(double param1Double1, double param1Double2, double param1Double3);
    }

}
