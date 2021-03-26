package keiproductfamily.Vector;

import java.util.Objects;

public class Vec3I{
    public final int x, y, z;
    public Vec3I(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "[" +
                x +
                ", " + y +
                ", " + z +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec3I vec3I = (Vec3I) o;
        return x == vec3I.x && y == vec3I.y && z == vec3I.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
