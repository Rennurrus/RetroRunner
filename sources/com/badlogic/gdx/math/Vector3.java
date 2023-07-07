package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Vector3 implements Serializable, Vector<Vector3> {
    public static final Vector3 X = new Vector3(1.0f, 0.0f, 0.0f);
    public static final Vector3 Y = new Vector3(0.0f, 1.0f, 0.0f);
    public static final Vector3 Z = new Vector3(0.0f, 0.0f, 1.0f);
    public static final Vector3 Zero = new Vector3(0.0f, 0.0f, 0.0f);
    private static final long serialVersionUID = 3840054589595372522L;
    private static final Matrix4 tmpMat = new Matrix4();
    public float x;
    public float y;
    public float z;

    public Vector3() {
    }

    public Vector3(float x2, float y2, float z2) {
        set(x2, y2, z2);
    }

    public Vector3(Vector3 vector) {
        set(vector);
    }

    public Vector3(float[] values) {
        set(values[0], values[1], values[2]);
    }

    public Vector3(Vector2 vector, float z2) {
        set(vector.x, vector.y, z2);
    }

    public Vector3 set(float x2, float y2, float z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        return this;
    }

    public Vector3 set(Vector3 vector) {
        return set(vector.x, vector.y, vector.z);
    }

    public Vector3 set(float[] values) {
        return set(values[0], values[1], values[2]);
    }

    public Vector3 set(Vector2 vector, float z2) {
        return set(vector.x, vector.y, z2);
    }

    public Vector3 setFromSpherical(float azimuthalAngle, float polarAngle) {
        float cosPolar = MathUtils.cos(polarAngle);
        float sinPolar = MathUtils.sin(polarAngle);
        return set(MathUtils.cos(azimuthalAngle) * sinPolar, MathUtils.sin(azimuthalAngle) * sinPolar, cosPolar);
    }

    public Vector3 setToRandomDirection() {
        return setFromSpherical(6.2831855f * MathUtils.random(), (float) Math.acos((double) ((2.0f * MathUtils.random()) - 1.0f)));
    }

    public Vector3 cpy() {
        return new Vector3(this);
    }

    public Vector3 add(Vector3 vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public Vector3 add(float x2, float y2, float z2) {
        return set(this.x + x2, this.y + y2, this.z + z2);
    }

    public Vector3 add(float values) {
        return set(this.x + values, this.y + values, this.z + values);
    }

    public Vector3 sub(Vector3 a_vec) {
        return sub(a_vec.x, a_vec.y, a_vec.z);
    }

    public Vector3 sub(float x2, float y2, float z2) {
        return set(this.x - x2, this.y - y2, this.z - z2);
    }

    public Vector3 sub(float value) {
        return set(this.x - value, this.y - value, this.z - value);
    }

    public Vector3 scl(float scalar) {
        return set(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3 scl(Vector3 other) {
        return set(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public Vector3 scl(float vx, float vy, float vz) {
        return set(this.x * vx, this.y * vy, this.z * vz);
    }

    public Vector3 mulAdd(Vector3 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        return this;
    }

    public Vector3 mulAdd(Vector3 vec, Vector3 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        return this;
    }

    public static float len(float x2, float y2, float z2) {
        return (float) Math.sqrt((double) ((x2 * x2) + (y2 * y2) + (z2 * z2)));
    }

    public float len() {
        float f = this.x;
        float f2 = this.y;
        float f3 = (f * f) + (f2 * f2);
        float f4 = this.z;
        return (float) Math.sqrt((double) (f3 + (f4 * f4)));
    }

    public static float len2(float x2, float y2, float z2) {
        return (x2 * x2) + (y2 * y2) + (z2 * z2);
    }

    public float len2() {
        float f = this.x;
        float f2 = this.y;
        float f3 = (f * f) + (f2 * f2);
        float f4 = this.z;
        return f3 + (f4 * f4);
    }

    public boolean idt(Vector3 vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z;
    }

    public static float dst(float x1, float y1, float z1, float x2, float y2, float z2) {
        float a = x2 - x1;
        float b = y2 - y1;
        float c = z2 - z1;
        return (float) Math.sqrt((double) ((a * a) + (b * b) + (c * c)));
    }

    public float dst(Vector3 vector) {
        float a = vector.x - this.x;
        float b = vector.y - this.y;
        float c = vector.z - this.z;
        return (float) Math.sqrt((double) ((a * a) + (b * b) + (c * c)));
    }

    public float dst(float x2, float y2, float z2) {
        float a = x2 - this.x;
        float b = y2 - this.y;
        float c = z2 - this.z;
        return (float) Math.sqrt((double) ((a * a) + (b * b) + (c * c)));
    }

    public static float dst2(float x1, float y1, float z1, float x2, float y2, float z2) {
        float a = x2 - x1;
        float b = y2 - y1;
        float c = z2 - z1;
        return (a * a) + (b * b) + (c * c);
    }

    public float dst2(Vector3 point) {
        float a = point.x - this.x;
        float b = point.y - this.y;
        float c = point.z - this.z;
        return (a * a) + (b * b) + (c * c);
    }

    public float dst2(float x2, float y2, float z2) {
        float a = x2 - this.x;
        float b = y2 - this.y;
        float c = z2 - this.z;
        return (a * a) + (b * b) + (c * c);
    }

    public Vector3 nor() {
        float len2 = len2();
        if (len2 == 0.0f || len2 == 1.0f) {
            return this;
        }
        return scl(1.0f / ((float) Math.sqrt((double) len2)));
    }

    public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (x1 * x2) + (y1 * y2) + (z1 * z2);
    }

    public float dot(Vector3 vector) {
        return (this.x * vector.x) + (this.y * vector.y) + (this.z * vector.z);
    }

    public float dot(float x2, float y2, float z2) {
        return (this.x * x2) + (this.y * y2) + (this.z * z2);
    }

    public Vector3 crs(Vector3 vector) {
        float f = this.y;
        float f2 = vector.z;
        float f3 = this.z;
        float f4 = vector.y;
        float f5 = (f * f2) - (f3 * f4);
        float f6 = vector.x;
        float f7 = this.x;
        return set(f5, (f3 * f6) - (f2 * f7), (f7 * f4) - (f * f6));
    }

    public Vector3 crs(float x2, float y2, float z2) {
        float f = this.y;
        float f2 = this.z;
        float f3 = (f * z2) - (f2 * y2);
        float f4 = this.x;
        return set(f3, (f2 * x2) - (f4 * z2), (f4 * y2) - (f * x2));
    }

    public Vector3 mul4x3(float[] matrix) {
        float f = this.x;
        float f2 = this.y;
        float f3 = (matrix[0] * f) + (matrix[3] * f2);
        float f4 = this.z;
        return set(f3 + (matrix[6] * f4) + matrix[9], (matrix[1] * f) + (matrix[4] * f2) + (matrix[7] * f4) + matrix[10], (f * matrix[2]) + (f2 * matrix[5]) + (f4 * matrix[8]) + matrix[11]);
    }

    public Vector3 mul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[0] * f) + (l_mat[4] * f2);
        float f4 = this.z;
        return set(f3 + (l_mat[8] * f4) + l_mat[12], (l_mat[1] * f) + (l_mat[5] * f2) + (l_mat[9] * f4) + l_mat[13], (f * l_mat[2]) + (f2 * l_mat[6]) + (f4 * l_mat[10]) + l_mat[14]);
    }

    public Vector3 traMul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[0] * f) + (l_mat[1] * f2);
        float f4 = this.z;
        return set(f3 + (l_mat[2] * f4) + l_mat[3], (l_mat[4] * f) + (l_mat[5] * f2) + (l_mat[6] * f4) + l_mat[7], (f * l_mat[8]) + (f2 * l_mat[9]) + (f4 * l_mat[10]) + l_mat[11]);
    }

    public Vector3 mul(Matrix3 matrix) {
        float[] l_mat = matrix.val;
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[0] * f) + (l_mat[3] * f2);
        float f4 = this.z;
        return set(f3 + (l_mat[6] * f4), (l_mat[1] * f) + (l_mat[4] * f2) + (l_mat[7] * f4), (f * l_mat[2]) + (f2 * l_mat[5]) + (f4 * l_mat[8]));
    }

    public Vector3 traMul(Matrix3 matrix) {
        float[] l_mat = matrix.val;
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[0] * f) + (l_mat[1] * f2);
        float f4 = this.z;
        return set(f3 + (l_mat[2] * f4), (l_mat[3] * f) + (l_mat[4] * f2) + (l_mat[5] * f4), (f * l_mat[6]) + (f2 * l_mat[7]) + (f4 * l_mat[8]));
    }

    public Vector3 mul(Quaternion quat) {
        return quat.transform(this);
    }

    public Vector3 prj(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[3] * f) + (l_mat[7] * f2);
        float f4 = this.z;
        float l_w = 1.0f / ((f3 + (l_mat[11] * f4)) + l_mat[15]);
        return set(((l_mat[0] * f) + (l_mat[4] * f2) + (l_mat[8] * f4) + l_mat[12]) * l_w, ((l_mat[1] * f) + (l_mat[5] * f2) + (l_mat[9] * f4) + l_mat[13]) * l_w, ((f * l_mat[2]) + (f2 * l_mat[6]) + (f4 * l_mat[10]) + l_mat[14]) * l_w);
    }

    public Vector3 rot(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[0] * f) + (l_mat[4] * f2);
        float f4 = this.z;
        return set(f3 + (l_mat[8] * f4), (l_mat[1] * f) + (l_mat[5] * f2) + (l_mat[9] * f4), (f * l_mat[2]) + (f2 * l_mat[6]) + (f4 * l_mat[10]));
    }

    public Vector3 unrotate(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[0] * f) + (l_mat[1] * f2);
        float f4 = this.z;
        return set(f3 + (l_mat[2] * f4), (l_mat[4] * f) + (l_mat[5] * f2) + (l_mat[6] * f4), (f * l_mat[8]) + (f2 * l_mat[9]) + (f4 * l_mat[10]));
    }

    public Vector3 untransform(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        this.x -= l_mat[12];
        this.y -= l_mat[12];
        this.z -= l_mat[12];
        float f = this.x;
        float f2 = this.y;
        float f3 = (l_mat[0] * f) + (l_mat[1] * f2);
        float f4 = this.z;
        return set(f3 + (l_mat[2] * f4), (l_mat[4] * f) + (l_mat[5] * f2) + (l_mat[6] * f4), (f * l_mat[8]) + (f2 * l_mat[9]) + (f4 * l_mat[10]));
    }

    public Vector3 rotate(float degrees, float axisX, float axisY, float axisZ) {
        return mul(tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
    }

    public Vector3 rotateRad(float radians, float axisX, float axisY, float axisZ) {
        return mul(tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
    }

    public Vector3 rotate(Vector3 axis, float degrees) {
        tmpMat.setToRotation(axis, degrees);
        return mul(tmpMat);
    }

    public Vector3 rotateRad(Vector3 axis, float radians) {
        tmpMat.setToRotationRad(axis, radians);
        return mul(tmpMat);
    }

    public boolean isUnit() {
        return isUnit(1.0E-9f);
    }

    public boolean isUnit(float margin) {
        return Math.abs(len2() - 1.0f) < margin;
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f;
    }

    public boolean isZero(float margin) {
        return len2() < margin;
    }

    public boolean isOnLine(Vector3 other, float epsilon) {
        float f = this.y;
        float f2 = other.z;
        float f3 = this.z;
        float f4 = other.y;
        float f5 = (f * f2) - (f3 * f4);
        float f6 = other.x;
        float f7 = this.x;
        return len2(f5, (f3 * f6) - (f2 * f7), (f7 * f4) - (f * f6)) <= epsilon;
    }

    public boolean isOnLine(Vector3 other) {
        float f = this.y;
        float f2 = other.z;
        float f3 = this.z;
        float f4 = other.y;
        float f5 = (f * f2) - (f3 * f4);
        float f6 = other.x;
        float f7 = this.x;
        return len2(f5, (f3 * f6) - (f2 * f7), (f7 * f4) - (f * f6)) <= 1.0E-6f;
    }

    public boolean isCollinear(Vector3 other, float epsilon) {
        return isOnLine(other, epsilon) && hasSameDirection(other);
    }

    public boolean isCollinear(Vector3 other) {
        return isOnLine(other) && hasSameDirection(other);
    }

    public boolean isCollinearOpposite(Vector3 other, float epsilon) {
        return isOnLine(other, epsilon) && hasOppositeDirection(other);
    }

    public boolean isCollinearOpposite(Vector3 other) {
        return isOnLine(other) && hasOppositeDirection(other);
    }

    public boolean isPerpendicular(Vector3 vector) {
        return MathUtils.isZero(dot(vector));
    }

    public boolean isPerpendicular(Vector3 vector, float epsilon) {
        return MathUtils.isZero(dot(vector), epsilon);
    }

    public boolean hasSameDirection(Vector3 vector) {
        return dot(vector) > 0.0f;
    }

    public boolean hasOppositeDirection(Vector3 vector) {
        return dot(vector) < 0.0f;
    }

    public Vector3 lerp(Vector3 target, float alpha) {
        float f = this.x;
        this.x = f + ((target.x - f) * alpha);
        float f2 = this.y;
        this.y = f2 + ((target.y - f2) * alpha);
        float f3 = this.z;
        this.z = f3 + ((target.z - f3) * alpha);
        return this;
    }

    public Vector3 interpolate(Vector3 target, float alpha, Interpolation interpolator) {
        return lerp(target, interpolator.apply(0.0f, 1.0f, alpha));
    }

    public Vector3 slerp(Vector3 target, float alpha) {
        float dot = dot(target);
        if (((double) dot) > 0.9995d || ((double) dot) < -0.9995d) {
            return lerp(target, alpha);
        }
        float theta = ((float) Math.acos((double) dot)) * alpha;
        float st = (float) Math.sin((double) theta);
        float tx = target.x - (this.x * dot);
        float ty = target.y - (this.y * dot);
        float tz = target.z - (this.z * dot);
        float l2 = (tx * tx) + (ty * ty) + (tz * tz);
        float f = 1.0f;
        if (l2 >= 1.0E-4f) {
            f = 1.0f / ((float) Math.sqrt((double) l2));
        }
        float dl = f * st;
        return scl((float) Math.cos((double) theta)).add(tx * dl, ty * dl, tz * dl).nor();
    }

    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }

    public Vector3 fromString(String v) {
        int s0 = v.indexOf(44, 1);
        int s1 = v.indexOf(44, s0 + 1);
        if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                return set(Float.parseFloat(v.substring(1, s0)), Float.parseFloat(v.substring(s0 + 1, s1)), Float.parseFloat(v.substring(s1 + 1, v.length() - 1)));
            } catch (NumberFormatException e) {
            }
        }
        throw new GdxRuntimeException("Malformed Vector3: " + v);
    }

    public Vector3 limit(float limit) {
        return limit2(limit * limit);
    }

    public Vector3 limit2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            scl((float) Math.sqrt((double) (limit2 / len2)));
        }
        return this;
    }

    public Vector3 setLength(float len) {
        return setLength2(len * len);
    }

    public Vector3 setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0.0f || oldLen2 == len2) ? this : scl((float) Math.sqrt((double) (len2 / oldLen2)));
    }

    public Vector3 clamp(float min, float max) {
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

    public int hashCode() {
        return (((((1 * 31) + NumberUtils.floatToIntBits(this.x)) * 31) + NumberUtils.floatToIntBits(this.y)) * 31) + NumberUtils.floatToIntBits(this.z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vector3 other = (Vector3) obj;
        if (NumberUtils.floatToIntBits(this.x) == NumberUtils.floatToIntBits(other.x) && NumberUtils.floatToIntBits(this.y) == NumberUtils.floatToIntBits(other.y) && NumberUtils.floatToIntBits(this.z) == NumberUtils.floatToIntBits(other.z)) {
            return true;
        }
        return false;
    }

    public boolean epsilonEquals(Vector3 other, float epsilon) {
        if (other != null && Math.abs(other.x - this.x) <= epsilon && Math.abs(other.y - this.y) <= epsilon && Math.abs(other.z - this.z) <= epsilon) {
            return true;
        }
        return false;
    }

    public boolean epsilonEquals(float x2, float y2, float z2, float epsilon) {
        if (Math.abs(x2 - this.x) <= epsilon && Math.abs(y2 - this.y) <= epsilon && Math.abs(z2 - this.z) <= epsilon) {
            return true;
        }
        return false;
    }

    public boolean epsilonEquals(Vector3 other) {
        return epsilonEquals(other, 1.0E-6f);
    }

    public boolean epsilonEquals(float x2, float y2, float z2) {
        return epsilonEquals(x2, y2, z2, 1.0E-6f);
    }

    public Vector3 setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        return this;
    }
}
