package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Vector2 implements Serializable, Vector<Vector2> {
    public static final Vector2 X = new Vector2(1.0f, 0.0f);
    public static final Vector2 Y = new Vector2(0.0f, 1.0f);
    public static final Vector2 Zero = new Vector2(0.0f, 0.0f);
    private static final long serialVersionUID = 913902788239530931L;
    public float x;
    public float y;

    public Vector2() {
    }

    public Vector2(float x2, float y2) {
        this.x = x2;
        this.y = y2;
    }

    public Vector2(Vector2 v) {
        set(v);
    }

    public Vector2 cpy() {
        return new Vector2(this);
    }

    public static float len(float x2, float y2) {
        return (float) Math.sqrt((double) ((x2 * x2) + (y2 * y2)));
    }

    public float len() {
        float f = this.x;
        float f2 = this.y;
        return (float) Math.sqrt((double) ((f * f) + (f2 * f2)));
    }

    public static float len2(float x2, float y2) {
        return (x2 * x2) + (y2 * y2);
    }

    public float len2() {
        float f = this.x;
        float f2 = this.y;
        return (f * f) + (f2 * f2);
    }

    public Vector2 set(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public Vector2 set(float x2, float y2) {
        this.x = x2;
        this.y = y2;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public Vector2 sub(float x2, float y2) {
        this.x -= x2;
        this.y -= y2;
        return this;
    }

    public Vector2 nor() {
        float len = len();
        if (len != 0.0f) {
            this.x /= len;
            this.y /= len;
        }
        return this;
    }

    public Vector2 add(Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public Vector2 add(float x2, float y2) {
        this.x += x2;
        this.y += y2;
        return this;
    }

    public static float dot(float x1, float y1, float x2, float y2) {
        return (x1 * x2) + (y1 * y2);
    }

    public float dot(Vector2 v) {
        return (this.x * v.x) + (this.y * v.y);
    }

    public float dot(float ox, float oy) {
        return (this.x * ox) + (this.y * oy);
    }

    public Vector2 scl(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2 scl(float x2, float y2) {
        this.x *= x2;
        this.y *= y2;
        return this;
    }

    public Vector2 scl(Vector2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }

    public Vector2 mulAdd(Vector2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    public Vector2 mulAdd(Vector2 vec, Vector2 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        return this;
    }

    public static float dst(float x1, float y1, float x2, float y2) {
        float x_d = x2 - x1;
        float y_d = y2 - y1;
        return (float) Math.sqrt((double) ((x_d * x_d) + (y_d * y_d)));
    }

    public float dst(Vector2 v) {
        float x_d = v.x - this.x;
        float y_d = v.y - this.y;
        return (float) Math.sqrt((double) ((x_d * x_d) + (y_d * y_d)));
    }

    public float dst(float x2, float y2) {
        float x_d = x2 - this.x;
        float y_d = y2 - this.y;
        return (float) Math.sqrt((double) ((x_d * x_d) + (y_d * y_d)));
    }

    public static float dst2(float x1, float y1, float x2, float y2) {
        float x_d = x2 - x1;
        float y_d = y2 - y1;
        return (x_d * x_d) + (y_d * y_d);
    }

    public float dst2(Vector2 v) {
        float x_d = v.x - this.x;
        float y_d = v.y - this.y;
        return (x_d * x_d) + (y_d * y_d);
    }

    public float dst2(float x2, float y2) {
        float x_d = x2 - this.x;
        float y_d = y2 - this.y;
        return (x_d * x_d) + (y_d * y_d);
    }

    public Vector2 limit(float limit) {
        return limit2(limit * limit);
    }

    public Vector2 limit2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            return scl((float) Math.sqrt((double) (limit2 / len2)));
        }
        return this;
    }

    public Vector2 clamp(float min, float max) {
        float len2 = len2();
        if (len2 == 0.0f) {
            return this;
        }
        float max2 = max * max;
        if (len2 > max2) {
            return scl((float) Math.sqrt((double) (max2 / len2)));
        }
        float min2 = min * min;
        if (len2 < min2) {
            return scl((float) Math.sqrt((double) (min2 / len2)));
        }
        return this;
    }

    public Vector2 setLength(float len) {
        return setLength2(len * len);
    }

    public Vector2 setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0.0f || oldLen2 == len2) ? this : scl((float) Math.sqrt((double) (len2 / oldLen2)));
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public Vector2 fromString(String v) {
        int s = v.indexOf(44, 1);
        if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                return set(Float.parseFloat(v.substring(1, s)), Float.parseFloat(v.substring(s + 1, v.length() - 1)));
            } catch (NumberFormatException e) {
            }
        }
        throw new GdxRuntimeException("Malformed Vector2: " + v);
    }

    public Vector2 mul(Matrix3 mat) {
        this.x = (this.x * mat.val[0]) + (this.y * mat.val[3]) + mat.val[6];
        this.y = (this.x * mat.val[1]) + (this.y * mat.val[4]) + mat.val[7];
        return this;
    }

    public float crs(Vector2 v) {
        return (this.x * v.y) - (this.y * v.x);
    }

    public float crs(float x2, float y2) {
        return (this.x * y2) - (this.y * x2);
    }

    public float angle() {
        float angle = ((float) Math.atan2((double) this.y, (double) this.x)) * 57.295776f;
        if (angle < 0.0f) {
            return angle + 360.0f;
        }
        return angle;
    }

    public float angle(Vector2 reference) {
        return ((float) Math.atan2((double) crs(reference), (double) dot(reference))) * 57.295776f;
    }

    public float angleRad() {
        return (float) Math.atan2((double) this.y, (double) this.x);
    }

    public float angleRad(Vector2 reference) {
        return (float) Math.atan2((double) crs(reference), (double) dot(reference));
    }

    public Vector2 setAngle(float degrees) {
        return setAngleRad(0.017453292f * degrees);
    }

    public Vector2 setAngleRad(float radians) {
        set(len(), 0.0f);
        rotateRad(radians);
        return this;
    }

    public Vector2 rotate(float degrees) {
        return rotateRad(0.017453292f * degrees);
    }

    public Vector2 rotateAround(Vector2 reference, float degrees) {
        return sub(reference).rotate(degrees).add(reference);
    }

    public Vector2 rotateRad(float radians) {
        float cos = (float) Math.cos((double) radians);
        float sin = (float) Math.sin((double) radians);
        float f = this.x;
        float f2 = this.y;
        this.x = (f * cos) - (f2 * sin);
        this.y = (f * sin) + (f2 * cos);
        return this;
    }

    public Vector2 rotateAroundRad(Vector2 reference, float radians) {
        return sub(reference).rotateRad(radians).add(reference);
    }

    public Vector2 rotate90(int dir) {
        float x2 = this.x;
        if (dir >= 0) {
            this.x = -this.y;
            this.y = x2;
        } else {
            this.x = this.y;
            this.y = -x2;
        }
        return this;
    }

    public Vector2 lerp(Vector2 target, float alpha) {
        float invAlpha = 1.0f - alpha;
        this.x = (this.x * invAlpha) + (target.x * alpha);
        this.y = (this.y * invAlpha) + (target.y * alpha);
        return this;
    }

    public Vector2 interpolate(Vector2 target, float alpha, Interpolation interpolation) {
        return lerp(target, interpolation.apply(alpha));
    }

    public Vector2 setToRandomDirection() {
        float theta = MathUtils.random(0.0f, 6.2831855f);
        return set(MathUtils.cos(theta), MathUtils.sin(theta));
    }

    public int hashCode() {
        return (((1 * 31) + NumberUtils.floatToIntBits(this.x)) * 31) + NumberUtils.floatToIntBits(this.y);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vector2 other = (Vector2) obj;
        if (NumberUtils.floatToIntBits(this.x) == NumberUtils.floatToIntBits(other.x) && NumberUtils.floatToIntBits(this.y) == NumberUtils.floatToIntBits(other.y)) {
            return true;
        }
        return false;
    }

    public boolean epsilonEquals(Vector2 other, float epsilon) {
        if (other != null && Math.abs(other.x - this.x) <= epsilon && Math.abs(other.y - this.y) <= epsilon) {
            return true;
        }
        return false;
    }

    public boolean epsilonEquals(float x2, float y2, float epsilon) {
        if (Math.abs(x2 - this.x) <= epsilon && Math.abs(y2 - this.y) <= epsilon) {
            return true;
        }
        return false;
    }

    public boolean epsilonEquals(Vector2 other) {
        return epsilonEquals(other, 1.0E-6f);
    }

    public boolean epsilonEquals(float x2, float y2) {
        return epsilonEquals(x2, y2, 1.0E-6f);
    }

    public boolean isUnit() {
        return isUnit(1.0E-9f);
    }

    public boolean isUnit(float margin) {
        return Math.abs(len2() - 1.0f) < margin;
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f;
    }

    public boolean isZero(float margin) {
        return len2() < margin;
    }

    public boolean isOnLine(Vector2 other) {
        return MathUtils.isZero((this.x * other.y) - (this.y * other.x));
    }

    public boolean isOnLine(Vector2 other, float epsilon) {
        return MathUtils.isZero((this.x * other.y) - (this.y * other.x), epsilon);
    }

    public boolean isCollinear(Vector2 other, float epsilon) {
        return isOnLine(other, epsilon) && dot(other) > 0.0f;
    }

    public boolean isCollinear(Vector2 other) {
        return isOnLine(other) && dot(other) > 0.0f;
    }

    public boolean isCollinearOpposite(Vector2 other, float epsilon) {
        return isOnLine(other, epsilon) && dot(other) < 0.0f;
    }

    public boolean isCollinearOpposite(Vector2 other) {
        return isOnLine(other) && dot(other) < 0.0f;
    }

    public boolean isPerpendicular(Vector2 vector) {
        return MathUtils.isZero(dot(vector));
    }

    public boolean isPerpendicular(Vector2 vector, float epsilon) {
        return MathUtils.isZero(dot(vector), epsilon);
    }

    public boolean hasSameDirection(Vector2 vector) {
        return dot(vector) > 0.0f;
    }

    public boolean hasOppositeDirection(Vector2 vector) {
        return dot(vector) < 0.0f;
    }

    public Vector2 setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        return this;
    }
}
