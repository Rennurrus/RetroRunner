package com.badlogic.gdx.math;

import java.io.Serializable;

public class Matrix4 implements Serializable {
    public static final int M00 = 0;
    public static final int M01 = 4;
    public static final int M02 = 8;
    public static final int M03 = 12;
    public static final int M10 = 1;
    public static final int M11 = 5;
    public static final int M12 = 9;
    public static final int M13 = 13;
    public static final int M20 = 2;
    public static final int M21 = 6;
    public static final int M22 = 10;
    public static final int M23 = 14;
    public static final int M30 = 3;
    public static final int M31 = 7;
    public static final int M32 = 11;
    public static final int M33 = 15;
    static final Vector3 l_vex = new Vector3();
    static final Vector3 l_vey = new Vector3();
    static final Vector3 l_vez = new Vector3();
    static Quaternion quat = new Quaternion();
    static Quaternion quat2 = new Quaternion();
    static final Vector3 right = new Vector3();
    private static final long serialVersionUID = -2717655254359579617L;
    private static final float[] tmp = new float[16];
    static final Vector3 tmpForward = new Vector3();
    static final Matrix4 tmpMat = new Matrix4();
    static final Vector3 tmpUp = new Vector3();
    static final Vector3 tmpVec = new Vector3();
    public final float[] val = new float[16];

    public static native float det(float[] fArr);

    public static native boolean inv(float[] fArr);

    public static native void mul(float[] fArr, float[] fArr2);

    public static native void mulVec(float[] fArr, float[] fArr2);

    public static native void mulVec(float[] fArr, float[] fArr2, int i, int i2, int i3);

    public static native void prj(float[] fArr, float[] fArr2);

    public static native void prj(float[] fArr, float[] fArr2, int i, int i2, int i3);

    public static native void rot(float[] fArr, float[] fArr2);

    public static native void rot(float[] fArr, float[] fArr2, int i, int i2, int i3);

    public Matrix4() {
        float[] fArr = this.val;
        fArr[0] = 1.0f;
        fArr[5] = 1.0f;
        fArr[10] = 1.0f;
        fArr[15] = 1.0f;
    }

    public Matrix4(Matrix4 matrix) {
        set(matrix);
    }

    public Matrix4(float[] values) {
        set(values);
    }

    public Matrix4(Quaternion quaternion) {
        set(quaternion);
    }

    public Matrix4(Vector3 position, Quaternion rotation, Vector3 scale) {
        set(position, rotation, scale);
    }

    public Matrix4 set(Matrix4 matrix) {
        return set(matrix.val);
    }

    public Matrix4 set(float[] values) {
        float[] fArr = this.val;
        System.arraycopy(values, 0, fArr, 0, fArr.length);
        return this;
    }

    public Matrix4 set(Quaternion quaternion) {
        return set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public Matrix4 set(float quaternionX, float quaternionY, float quaternionZ, float quaternionW) {
        return set(0.0f, 0.0f, 0.0f, quaternionX, quaternionY, quaternionZ, quaternionW);
    }

    public Matrix4 set(Vector3 position, Quaternion orientation) {
        return set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w);
    }

    public Matrix4 set(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW) {
        float xs = quaternionX * 2.0f;
        float ys = quaternionY * 2.0f;
        float zs = 2.0f * quaternionZ;
        float wx = quaternionW * xs;
        float wy = quaternionW * ys;
        float wz = quaternionW * zs;
        float xx = quaternionX * xs;
        float xy = quaternionX * ys;
        float xz = quaternionX * zs;
        float yy = quaternionY * ys;
        float yz = quaternionY * zs;
        float zz = quaternionZ * zs;
        float[] fArr = this.val;
        fArr[0] = 1.0f - (yy + zz);
        fArr[4] = xy - wz;
        fArr[8] = xz + wy;
        fArr[12] = translationX;
        fArr[1] = xy + wz;
        fArr[5] = 1.0f - (xx + zz);
        fArr[9] = yz - wx;
        fArr[13] = translationY;
        fArr[2] = xz - wy;
        fArr[6] = yz + wx;
        fArr[10] = 1.0f - (xx + yy);
        fArr[14] = translationZ;
        fArr[3] = 0.0f;
        fArr[7] = 0.0f;
        fArr[11] = 0.0f;
        fArr[15] = 1.0f;
        return this;
    }

    public Matrix4 set(Vector3 position, Quaternion orientation, Vector3 scale) {
        return set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w, scale.x, scale.y, scale.z);
    }

    public Matrix4 set(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW, float scaleX, float scaleY, float scaleZ) {
        float xs = quaternionX * 2.0f;
        float ys = quaternionY * 2.0f;
        float zs = 2.0f * quaternionZ;
        float wx = quaternionW * xs;
        float wy = quaternionW * ys;
        float wz = quaternionW * zs;
        float xx = quaternionX * xs;
        float xy = quaternionX * ys;
        float xz = quaternionX * zs;
        float yy = quaternionY * ys;
        float yz = quaternionY * zs;
        float zz = quaternionZ * zs;
        float[] fArr = this.val;
        fArr[0] = (1.0f - (yy + zz)) * scaleX;
        fArr[4] = (xy - wz) * scaleY;
        fArr[8] = (xz + wy) * scaleZ;
        fArr[12] = translationX;
        fArr[1] = (xy + wz) * scaleX;
        fArr[5] = (1.0f - (xx + zz)) * scaleY;
        fArr[9] = (yz - wx) * scaleZ;
        fArr[13] = translationY;
        fArr[2] = (xz - wy) * scaleX;
        fArr[6] = (yz + wx) * scaleY;
        fArr[10] = (1.0f - (xx + yy)) * scaleZ;
        fArr[14] = translationZ;
        fArr[3] = 0.0f;
        fArr[7] = 0.0f;
        fArr[11] = 0.0f;
        fArr[15] = 1.0f;
        return this;
    }

    public Matrix4 set(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
        this.val[0] = xAxis.x;
        this.val[4] = xAxis.y;
        this.val[8] = xAxis.z;
        this.val[1] = yAxis.x;
        this.val[5] = yAxis.y;
        this.val[9] = yAxis.z;
        this.val[2] = zAxis.x;
        this.val[6] = zAxis.y;
        this.val[10] = zAxis.z;
        this.val[12] = pos.x;
        this.val[13] = pos.y;
        this.val[14] = pos.z;
        float[] fArr = this.val;
        fArr[3] = 0.0f;
        fArr[7] = 0.0f;
        fArr[11] = 0.0f;
        fArr[15] = 1.0f;
        return this;
    }

    public Matrix4 cpy() {
        return new Matrix4(this);
    }

    public Matrix4 trn(Vector3 vector) {
        float[] fArr = this.val;
        fArr[12] = fArr[12] + vector.x;
        float[] fArr2 = this.val;
        fArr2[13] = fArr2[13] + vector.y;
        float[] fArr3 = this.val;
        fArr3[14] = fArr3[14] + vector.z;
        return this;
    }

    public Matrix4 trn(float x, float y, float z) {
        float[] fArr = this.val;
        fArr[12] = fArr[12] + x;
        fArr[13] = fArr[13] + y;
        fArr[14] = fArr[14] + z;
        return this;
    }

    public float[] getValues() {
        return this.val;
    }

    public Matrix4 mul(Matrix4 matrix) {
        mul(this.val, matrix.val);
        return this;
    }

    public Matrix4 mulLeft(Matrix4 matrix) {
        tmpMat.set(matrix);
        mul(tmpMat.val, this.val);
        return set(tmpMat);
    }

    public Matrix4 tra() {
        float[] fArr = tmp;
        float[] fArr2 = this.val;
        fArr[0] = fArr2[0];
        fArr[4] = fArr2[1];
        fArr[8] = fArr2[2];
        fArr[12] = fArr2[3];
        fArr[1] = fArr2[4];
        fArr[5] = fArr2[5];
        fArr[9] = fArr2[6];
        fArr[13] = fArr2[7];
        fArr[2] = fArr2[8];
        fArr[6] = fArr2[9];
        fArr[10] = fArr2[10];
        fArr[14] = fArr2[11];
        fArr[3] = fArr2[12];
        fArr[7] = fArr2[13];
        fArr[11] = fArr2[14];
        fArr[15] = fArr2[15];
        return set(fArr);
    }

    public Matrix4 idt() {
        float[] fArr = this.val;
        fArr[0] = 1.0f;
        fArr[4] = 0.0f;
        fArr[8] = 0.0f;
        fArr[12] = 0.0f;
        fArr[1] = 0.0f;
        fArr[5] = 1.0f;
        fArr[9] = 0.0f;
        fArr[13] = 0.0f;
        fArr[2] = 0.0f;
        fArr[6] = 0.0f;
        fArr[10] = 1.0f;
        fArr[14] = 0.0f;
        fArr[3] = 0.0f;
        fArr[7] = 0.0f;
        fArr[11] = 0.0f;
        fArr[15] = 1.0f;
        return this;
    }

    public Matrix4 inv() {
        float[] fArr = this.val;
        float l_det = (((((((((((((((((((((((((fArr[3] * fArr[6]) * fArr[9]) * fArr[12]) - (((fArr[2] * fArr[7]) * fArr[9]) * fArr[12])) - (((fArr[3] * fArr[5]) * fArr[10]) * fArr[12])) + (((fArr[1] * fArr[7]) * fArr[10]) * fArr[12])) + (((fArr[2] * fArr[5]) * fArr[11]) * fArr[12])) - (((fArr[1] * fArr[6]) * fArr[11]) * fArr[12])) - (((fArr[3] * fArr[6]) * fArr[8]) * fArr[13])) + (((fArr[2] * fArr[7]) * fArr[8]) * fArr[13])) + (((fArr[3] * fArr[4]) * fArr[10]) * fArr[13])) - (((fArr[0] * fArr[7]) * fArr[10]) * fArr[13])) - (((fArr[2] * fArr[4]) * fArr[11]) * fArr[13])) + (((fArr[0] * fArr[6]) * fArr[11]) * fArr[13])) + (((fArr[3] * fArr[5]) * fArr[8]) * fArr[14])) - (((fArr[1] * fArr[7]) * fArr[8]) * fArr[14])) - (((fArr[3] * fArr[4]) * fArr[9]) * fArr[14])) + (((fArr[0] * fArr[7]) * fArr[9]) * fArr[14])) + (((fArr[1] * fArr[4]) * fArr[11]) * fArr[14])) - (((fArr[0] * fArr[5]) * fArr[11]) * fArr[14])) - (((fArr[2] * fArr[5]) * fArr[8]) * fArr[15])) + (((fArr[1] * fArr[6]) * fArr[8]) * fArr[15])) + (((fArr[2] * fArr[4]) * fArr[9]) * fArr[15])) - (((fArr[0] * fArr[6]) * fArr[9]) * fArr[15])) - (((fArr[1] * fArr[4]) * fArr[10]) * fArr[15])) + (fArr[0] * fArr[5] * fArr[10] * fArr[15]);
        if (l_det != 0.0f) {
            float inv_det = 1.0f / l_det;
            float[] fArr2 = tmp;
            fArr2[0] = ((((((fArr[9] * fArr[14]) * fArr[7]) - ((fArr[13] * fArr[10]) * fArr[7])) + ((fArr[13] * fArr[6]) * fArr[11])) - ((fArr[5] * fArr[14]) * fArr[11])) - ((fArr[9] * fArr[6]) * fArr[15])) + (fArr[5] * fArr[10] * fArr[15]);
            fArr2[4] = ((((((fArr[12] * fArr[10]) * fArr[7]) - ((fArr[8] * fArr[14]) * fArr[7])) - ((fArr[12] * fArr[6]) * fArr[11])) + ((fArr[4] * fArr[14]) * fArr[11])) + ((fArr[8] * fArr[6]) * fArr[15])) - ((fArr[4] * fArr[10]) * fArr[15]);
            fArr2[8] = ((((((fArr[8] * fArr[13]) * fArr[7]) - ((fArr[12] * fArr[9]) * fArr[7])) + ((fArr[12] * fArr[5]) * fArr[11])) - ((fArr[4] * fArr[13]) * fArr[11])) - ((fArr[8] * fArr[5]) * fArr[15])) + (fArr[4] * fArr[9] * fArr[15]);
            fArr2[12] = ((((((fArr[12] * fArr[9]) * fArr[6]) - ((fArr[8] * fArr[13]) * fArr[6])) - ((fArr[12] * fArr[5]) * fArr[10])) + ((fArr[4] * fArr[13]) * fArr[10])) + ((fArr[8] * fArr[5]) * fArr[14])) - ((fArr[4] * fArr[9]) * fArr[14]);
            fArr2[1] = ((((((fArr[13] * fArr[10]) * fArr[3]) - ((fArr[9] * fArr[14]) * fArr[3])) - ((fArr[13] * fArr[2]) * fArr[11])) + ((fArr[1] * fArr[14]) * fArr[11])) + ((fArr[9] * fArr[2]) * fArr[15])) - ((fArr[1] * fArr[10]) * fArr[15]);
            fArr2[5] = ((((((fArr[8] * fArr[14]) * fArr[3]) - ((fArr[12] * fArr[10]) * fArr[3])) + ((fArr[12] * fArr[2]) * fArr[11])) - ((fArr[0] * fArr[14]) * fArr[11])) - ((fArr[8] * fArr[2]) * fArr[15])) + (fArr[0] * fArr[10] * fArr[15]);
            fArr2[9] = ((((((fArr[12] * fArr[9]) * fArr[3]) - ((fArr[8] * fArr[13]) * fArr[3])) - ((fArr[12] * fArr[1]) * fArr[11])) + ((fArr[0] * fArr[13]) * fArr[11])) + ((fArr[8] * fArr[1]) * fArr[15])) - ((fArr[0] * fArr[9]) * fArr[15]);
            fArr2[13] = ((((((fArr[8] * fArr[13]) * fArr[2]) - ((fArr[12] * fArr[9]) * fArr[2])) + ((fArr[12] * fArr[1]) * fArr[10])) - ((fArr[0] * fArr[13]) * fArr[10])) - ((fArr[8] * fArr[1]) * fArr[14])) + (fArr[0] * fArr[9] * fArr[14]);
            fArr2[2] = ((((((fArr[5] * fArr[14]) * fArr[3]) - ((fArr[13] * fArr[6]) * fArr[3])) + ((fArr[13] * fArr[2]) * fArr[7])) - ((fArr[1] * fArr[14]) * fArr[7])) - ((fArr[5] * fArr[2]) * fArr[15])) + (fArr[1] * fArr[6] * fArr[15]);
            fArr2[6] = ((((((fArr[12] * fArr[6]) * fArr[3]) - ((fArr[4] * fArr[14]) * fArr[3])) - ((fArr[12] * fArr[2]) * fArr[7])) + ((fArr[0] * fArr[14]) * fArr[7])) + ((fArr[4] * fArr[2]) * fArr[15])) - ((fArr[0] * fArr[6]) * fArr[15]);
            fArr2[10] = ((((((fArr[4] * fArr[13]) * fArr[3]) - ((fArr[12] * fArr[5]) * fArr[3])) + ((fArr[12] * fArr[1]) * fArr[7])) - ((fArr[0] * fArr[13]) * fArr[7])) - ((fArr[4] * fArr[1]) * fArr[15])) + (fArr[0] * fArr[5] * fArr[15]);
            fArr2[14] = ((((((fArr[12] * fArr[5]) * fArr[2]) - ((fArr[4] * fArr[13]) * fArr[2])) - ((fArr[12] * fArr[1]) * fArr[6])) + ((fArr[0] * fArr[13]) * fArr[6])) + ((fArr[4] * fArr[1]) * fArr[14])) - ((fArr[0] * fArr[5]) * fArr[14]);
            fArr2[3] = ((((((fArr[9] * fArr[6]) * fArr[3]) - ((fArr[5] * fArr[10]) * fArr[3])) - ((fArr[9] * fArr[2]) * fArr[7])) + ((fArr[1] * fArr[10]) * fArr[7])) + ((fArr[5] * fArr[2]) * fArr[11])) - ((fArr[1] * fArr[6]) * fArr[11]);
            fArr2[7] = ((((((fArr[4] * fArr[10]) * fArr[3]) - ((fArr[8] * fArr[6]) * fArr[3])) + ((fArr[8] * fArr[2]) * fArr[7])) - ((fArr[0] * fArr[10]) * fArr[7])) - ((fArr[4] * fArr[2]) * fArr[11])) + (fArr[0] * fArr[6] * fArr[11]);
            fArr2[11] = ((((((fArr[8] * fArr[5]) * fArr[3]) - ((fArr[4] * fArr[9]) * fArr[3])) - ((fArr[8] * fArr[1]) * fArr[7])) + ((fArr[0] * fArr[9]) * fArr[7])) + ((fArr[4] * fArr[1]) * fArr[11])) - ((fArr[0] * fArr[5]) * fArr[11]);
            fArr2[15] = ((((((fArr[4] * fArr[9]) * fArr[2]) - ((fArr[8] * fArr[5]) * fArr[2])) + ((fArr[8] * fArr[1]) * fArr[6])) - ((fArr[0] * fArr[9]) * fArr[6])) - ((fArr[4] * fArr[1]) * fArr[10])) + (fArr[0] * fArr[5] * fArr[10]);
            fArr[0] = fArr2[0] * inv_det;
            fArr[4] = fArr2[4] * inv_det;
            fArr[8] = fArr2[8] * inv_det;
            fArr[12] = fArr2[12] * inv_det;
            fArr[1] = fArr2[1] * inv_det;
            fArr[5] = fArr2[5] * inv_det;
            fArr[9] = fArr2[9] * inv_det;
            fArr[13] = fArr2[13] * inv_det;
            fArr[2] = fArr2[2] * inv_det;
            fArr[6] = fArr2[6] * inv_det;
            fArr[10] = fArr2[10] * inv_det;
            fArr[14] = fArr2[14] * inv_det;
            fArr[3] = fArr2[3] * inv_det;
            fArr[7] = fArr2[7] * inv_det;
            fArr[11] = fArr2[11] * inv_det;
            fArr[15] = fArr2[15] * inv_det;
            return this;
        }
        throw new RuntimeException("non-invertible matrix");
    }

    public float det() {
        float[] fArr = this.val;
        return (((((((((((((((((((((((((fArr[3] * fArr[6]) * fArr[9]) * fArr[12]) - (((fArr[2] * fArr[7]) * fArr[9]) * fArr[12])) - (((fArr[3] * fArr[5]) * fArr[10]) * fArr[12])) + (((fArr[1] * fArr[7]) * fArr[10]) * fArr[12])) + (((fArr[2] * fArr[5]) * fArr[11]) * fArr[12])) - (((fArr[1] * fArr[6]) * fArr[11]) * fArr[12])) - (((fArr[3] * fArr[6]) * fArr[8]) * fArr[13])) + (((fArr[2] * fArr[7]) * fArr[8]) * fArr[13])) + (((fArr[3] * fArr[4]) * fArr[10]) * fArr[13])) - (((fArr[0] * fArr[7]) * fArr[10]) * fArr[13])) - (((fArr[2] * fArr[4]) * fArr[11]) * fArr[13])) + (((fArr[0] * fArr[6]) * fArr[11]) * fArr[13])) + (((fArr[3] * fArr[5]) * fArr[8]) * fArr[14])) - (((fArr[1] * fArr[7]) * fArr[8]) * fArr[14])) - (((fArr[3] * fArr[4]) * fArr[9]) * fArr[14])) + (((fArr[0] * fArr[7]) * fArr[9]) * fArr[14])) + (((fArr[1] * fArr[4]) * fArr[11]) * fArr[14])) - (((fArr[0] * fArr[5]) * fArr[11]) * fArr[14])) - (((fArr[2] * fArr[5]) * fArr[8]) * fArr[15])) + (((fArr[1] * fArr[6]) * fArr[8]) * fArr[15])) + (((fArr[2] * fArr[4]) * fArr[9]) * fArr[15])) - (((fArr[0] * fArr[6]) * fArr[9]) * fArr[15])) - (((fArr[1] * fArr[4]) * fArr[10]) * fArr[15])) + (fArr[0] * fArr[5] * fArr[10] * fArr[15]);
    }

    public float det3x3() {
        float[] fArr = this.val;
        return ((((((fArr[0] * fArr[5]) * fArr[10]) + ((fArr[4] * fArr[9]) * fArr[2])) + ((fArr[8] * fArr[1]) * fArr[6])) - ((fArr[0] * fArr[9]) * fArr[6])) - ((fArr[4] * fArr[1]) * fArr[10])) - ((fArr[8] * fArr[5]) * fArr[2]);
    }

    public Matrix4 setToProjection(float near, float far, float fovy, float aspectRatio) {
        idt();
        double d = (double) fovy;
        Double.isNaN(d);
        float l_fd = (float) (1.0d / Math.tan((d * 0.017453292519943295d) / 2.0d));
        float[] fArr = this.val;
        fArr[0] = l_fd / aspectRatio;
        fArr[1] = 0.0f;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = 0.0f;
        fArr[5] = l_fd;
        fArr[6] = 0.0f;
        fArr[7] = 0.0f;
        fArr[8] = 0.0f;
        fArr[9] = 0.0f;
        fArr[10] = (far + near) / (near - far);
        fArr[11] = -1.0f;
        fArr[12] = 0.0f;
        fArr[13] = 0.0f;
        fArr[14] = ((2.0f * far) * near) / (near - far);
        fArr[15] = 0.0f;
        return this;
    }

    public Matrix4 setToProjection(float left, float right2, float bottom, float top, float near, float far) {
        float[] fArr = this.val;
        fArr[0] = (near * 2.0f) / (right2 - left);
        fArr[1] = 0.0f;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = 0.0f;
        fArr[5] = (near * 2.0f) / (top - bottom);
        fArr[6] = 0.0f;
        fArr[7] = 0.0f;
        fArr[8] = (right2 + left) / (right2 - left);
        fArr[9] = (top + bottom) / (top - bottom);
        fArr[10] = (far + near) / (near - far);
        fArr[11] = -1.0f;
        fArr[12] = 0.0f;
        fArr[13] = 0.0f;
        fArr[14] = ((2.0f * far) * near) / (near - far);
        fArr[15] = 0.0f;
        return this;
    }

    public Matrix4 setToOrtho2D(float x, float y, float width, float height) {
        setToOrtho(x, x + width, y, y + height, 0.0f, 1.0f);
        return this;
    }

    public Matrix4 setToOrtho2D(float x, float y, float width, float height, float near, float far) {
        setToOrtho(x, x + width, y, y + height, near, far);
        return this;
    }

    public Matrix4 setToOrtho(float left, float right2, float bottom, float top, float near, float far) {
        idt();
        float[] fArr = this.val;
        fArr[0] = 2.0f / (right2 - left);
        fArr[1] = 0.0f;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = 0.0f;
        fArr[5] = 2.0f / (top - bottom);
        fArr[6] = 0.0f;
        fArr[7] = 0.0f;
        fArr[8] = 0.0f;
        fArr[9] = 0.0f;
        fArr[10] = -2.0f / (far - near);
        fArr[11] = 0.0f;
        fArr[12] = (-(right2 + left)) / (right2 - left);
        fArr[13] = (-(top + bottom)) / (top - bottom);
        fArr[14] = (-(far + near)) / (far - near);
        fArr[15] = 1.0f;
        return this;
    }

    public Matrix4 setTranslation(Vector3 vector) {
        this.val[12] = vector.x;
        this.val[13] = vector.y;
        this.val[14] = vector.z;
        return this;
    }

    public Matrix4 setTranslation(float x, float y, float z) {
        float[] fArr = this.val;
        fArr[12] = x;
        fArr[13] = y;
        fArr[14] = z;
        return this;
    }

    public Matrix4 setToTranslation(Vector3 vector) {
        idt();
        this.val[12] = vector.x;
        this.val[13] = vector.y;
        this.val[14] = vector.z;
        return this;
    }

    public Matrix4 setToTranslation(float x, float y, float z) {
        idt();
        float[] fArr = this.val;
        fArr[12] = x;
        fArr[13] = y;
        fArr[14] = z;
        return this;
    }

    public Matrix4 setToTranslationAndScaling(Vector3 translation, Vector3 scaling) {
        idt();
        this.val[12] = translation.x;
        this.val[13] = translation.y;
        this.val[14] = translation.z;
        this.val[0] = scaling.x;
        this.val[5] = scaling.y;
        this.val[10] = scaling.z;
        return this;
    }

    public Matrix4 setToTranslationAndScaling(float translationX, float translationY, float translationZ, float scalingX, float scalingY, float scalingZ) {
        idt();
        float[] fArr = this.val;
        fArr[12] = translationX;
        fArr[13] = translationY;
        fArr[14] = translationZ;
        fArr[0] = scalingX;
        fArr[5] = scalingY;
        fArr[10] = scalingZ;
        return this;
    }

    public Matrix4 setToRotation(Vector3 axis, float degrees) {
        if (degrees != 0.0f) {
            return set(quat.set(axis, degrees));
        }
        idt();
        return this;
    }

    public Matrix4 setToRotationRad(Vector3 axis, float radians) {
        if (radians != 0.0f) {
            return set(quat.setFromAxisRad(axis, radians));
        }
        idt();
        return this;
    }

    public Matrix4 setToRotation(float axisX, float axisY, float axisZ, float degrees) {
        if (degrees != 0.0f) {
            return set(quat.setFromAxis(axisX, axisY, axisZ, degrees));
        }
        idt();
        return this;
    }

    public Matrix4 setToRotationRad(float axisX, float axisY, float axisZ, float radians) {
        if (radians != 0.0f) {
            return set(quat.setFromAxisRad(axisX, axisY, axisZ, radians));
        }
        idt();
        return this;
    }

    public Matrix4 setToRotation(Vector3 v1, Vector3 v2) {
        return set(quat.setFromCross(v1, v2));
    }

    public Matrix4 setToRotation(float x1, float y1, float z1, float x2, float y2, float z2) {
        return set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
    }

    public Matrix4 setFromEulerAngles(float yaw, float pitch, float roll) {
        quat.setEulerAngles(yaw, pitch, roll);
        return set(quat);
    }

    public Matrix4 setFromEulerAnglesRad(float yaw, float pitch, float roll) {
        quat.setEulerAnglesRad(yaw, pitch, roll);
        return set(quat);
    }

    public Matrix4 setToScaling(Vector3 vector) {
        idt();
        this.val[0] = vector.x;
        this.val[5] = vector.y;
        this.val[10] = vector.z;
        return this;
    }

    public Matrix4 setToScaling(float x, float y, float z) {
        idt();
        float[] fArr = this.val;
        fArr[0] = x;
        fArr[5] = y;
        fArr[10] = z;
        return this;
    }

    public Matrix4 setToLookAt(Vector3 direction, Vector3 up) {
        l_vez.set(direction).nor();
        l_vex.set(direction).nor();
        l_vex.crs(up).nor();
        l_vey.set(l_vex).crs(l_vez).nor();
        idt();
        this.val[0] = l_vex.x;
        this.val[4] = l_vex.y;
        this.val[8] = l_vex.z;
        this.val[1] = l_vey.x;
        this.val[5] = l_vey.y;
        this.val[9] = l_vey.z;
        this.val[2] = -l_vez.x;
        this.val[6] = -l_vez.y;
        this.val[10] = -l_vez.z;
        return this;
    }

    public Matrix4 setToLookAt(Vector3 position, Vector3 target, Vector3 up) {
        tmpVec.set(target).sub(position);
        setToLookAt(tmpVec, up);
        mul(tmpMat.setToTranslation(-position.x, -position.y, -position.z));
        return this;
    }

    public Matrix4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
        tmpForward.set(forward).nor();
        right.set(tmpForward).crs(up).nor();
        tmpUp.set(right).crs(tmpForward).nor();
        set(right, tmpUp, tmpForward.scl(-1.0f), position);
        return this;
    }

    public String toString() {
        return "[" + this.val[0] + "|" + this.val[4] + "|" + this.val[8] + "|" + this.val[12] + "]\n[" + this.val[1] + "|" + this.val[5] + "|" + this.val[9] + "|" + this.val[13] + "]\n[" + this.val[2] + "|" + this.val[6] + "|" + this.val[10] + "|" + this.val[14] + "]\n[" + this.val[3] + "|" + this.val[7] + "|" + this.val[11] + "|" + this.val[15] + "]\n";
    }

    public Matrix4 lerp(Matrix4 matrix, float alpha) {
        for (int i = 0; i < 16; i++) {
            float[] fArr = this.val;
            fArr[i] = (fArr[i] * (1.0f - alpha)) + (matrix.val[i] * alpha);
        }
        return this;
    }

    public Matrix4 avg(Matrix4 other, float w) {
        getScale(tmpVec);
        other.getScale(tmpForward);
        getRotation(quat);
        other.getRotation(quat2);
        getTranslation(tmpUp);
        other.getTranslation(right);
        setToScaling(tmpVec.scl(w).add(tmpForward.scl(1.0f - w)));
        rotate(quat.slerp(quat2, 1.0f - w));
        setTranslation(tmpUp.scl(w).add(right.scl(1.0f - w)));
        return this;
    }

    public Matrix4 avg(Matrix4[] t) {
        float w = 1.0f / ((float) t.length);
        tmpVec.set(t[0].getScale(tmpUp).scl(w));
        quat.set(t[0].getRotation(quat2).exp(w));
        tmpForward.set(t[0].getTranslation(tmpUp).scl(w));
        for (int i = 1; i < t.length; i++) {
            tmpVec.add(t[i].getScale(tmpUp).scl(w));
            quat.mul(t[i].getRotation(quat2).exp(w));
            tmpForward.add(t[i].getTranslation(tmpUp).scl(w));
        }
        quat.nor();
        setToScaling(tmpVec);
        rotate(quat);
        setTranslation(tmpForward);
        return this;
    }

    public Matrix4 avg(Matrix4[] t, float[] w) {
        tmpVec.set(t[0].getScale(tmpUp).scl(w[0]));
        quat.set(t[0].getRotation(quat2).exp(w[0]));
        tmpForward.set(t[0].getTranslation(tmpUp).scl(w[0]));
        for (int i = 1; i < t.length; i++) {
            tmpVec.add(t[i].getScale(tmpUp).scl(w[i]));
            quat.mul(t[i].getRotation(quat2).exp(w[i]));
            tmpForward.add(t[i].getTranslation(tmpUp).scl(w[i]));
        }
        quat.nor();
        setToScaling(tmpVec);
        rotate(quat);
        setTranslation(tmpForward);
        return this;
    }

    public Matrix4 set(Matrix3 mat) {
        this.val[0] = mat.val[0];
        this.val[1] = mat.val[1];
        this.val[2] = mat.val[2];
        float[] fArr = this.val;
        fArr[3] = 0.0f;
        fArr[4] = mat.val[3];
        this.val[5] = mat.val[4];
        this.val[6] = mat.val[5];
        float[] fArr2 = this.val;
        fArr2[7] = 0.0f;
        fArr2[8] = 0.0f;
        fArr2[9] = 0.0f;
        fArr2[10] = 1.0f;
        fArr2[11] = 0.0f;
        fArr2[12] = mat.val[6];
        this.val[13] = mat.val[7];
        float[] fArr3 = this.val;
        fArr3[14] = 0.0f;
        fArr3[15] = mat.val[8];
        return this;
    }

    public Matrix4 set(Affine2 affine) {
        this.val[0] = affine.m00;
        this.val[1] = affine.m10;
        float[] fArr = this.val;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = affine.m01;
        this.val[5] = affine.m11;
        float[] fArr2 = this.val;
        fArr2[6] = 0.0f;
        fArr2[7] = 0.0f;
        fArr2[8] = 0.0f;
        fArr2[9] = 0.0f;
        fArr2[10] = 1.0f;
        fArr2[11] = 0.0f;
        fArr2[12] = affine.m02;
        this.val[13] = affine.m12;
        float[] fArr3 = this.val;
        fArr3[14] = 0.0f;
        fArr3[15] = 1.0f;
        return this;
    }

    public Matrix4 setAsAffine(Affine2 affine) {
        this.val[0] = affine.m00;
        this.val[1] = affine.m10;
        this.val[4] = affine.m01;
        this.val[5] = affine.m11;
        this.val[12] = affine.m02;
        this.val[13] = affine.m12;
        return this;
    }

    public Matrix4 setAsAffine(Matrix4 mat) {
        float[] fArr = this.val;
        float[] fArr2 = mat.val;
        fArr[0] = fArr2[0];
        fArr[1] = fArr2[1];
        fArr[4] = fArr2[4];
        fArr[5] = fArr2[5];
        fArr[12] = fArr2[12];
        fArr[13] = fArr2[13];
        return this;
    }

    public Matrix4 scl(Vector3 scale) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * scale.x;
        float[] fArr2 = this.val;
        fArr2[5] = fArr2[5] * scale.y;
        float[] fArr3 = this.val;
        fArr3[10] = fArr3[10] * scale.z;
        return this;
    }

    public Matrix4 scl(float x, float y, float z) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * x;
        fArr[5] = fArr[5] * y;
        fArr[10] = fArr[10] * z;
        return this;
    }

    public Matrix4 scl(float scale) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * scale;
        fArr[5] = fArr[5] * scale;
        fArr[10] = fArr[10] * scale;
        return this;
    }

    public Vector3 getTranslation(Vector3 position) {
        float[] fArr = this.val;
        position.x = fArr[12];
        position.y = fArr[13];
        position.z = fArr[14];
        return position;
    }

    public Quaternion getRotation(Quaternion rotation, boolean normalizeAxes) {
        return rotation.setFromMatrix(normalizeAxes, this);
    }

    public Quaternion getRotation(Quaternion rotation) {
        return rotation.setFromMatrix(this);
    }

    public float getScaleXSquared() {
        float[] fArr = this.val;
        return (fArr[0] * fArr[0]) + (fArr[4] * fArr[4]) + (fArr[8] * fArr[8]);
    }

    public float getScaleYSquared() {
        float[] fArr = this.val;
        return (fArr[1] * fArr[1]) + (fArr[5] * fArr[5]) + (fArr[9] * fArr[9]);
    }

    public float getScaleZSquared() {
        float[] fArr = this.val;
        return (fArr[2] * fArr[2]) + (fArr[6] * fArr[6]) + (fArr[10] * fArr[10]);
    }

    public float getScaleX() {
        if (!MathUtils.isZero(this.val[4]) || !MathUtils.isZero(this.val[8])) {
            return (float) Math.sqrt((double) getScaleXSquared());
        }
        return Math.abs(this.val[0]);
    }

    public float getScaleY() {
        if (!MathUtils.isZero(this.val[1]) || !MathUtils.isZero(this.val[9])) {
            return (float) Math.sqrt((double) getScaleYSquared());
        }
        return Math.abs(this.val[5]);
    }

    public float getScaleZ() {
        if (!MathUtils.isZero(this.val[2]) || !MathUtils.isZero(this.val[6])) {
            return (float) Math.sqrt((double) getScaleZSquared());
        }
        return Math.abs(this.val[10]);
    }

    public Vector3 getScale(Vector3 scale) {
        return scale.set(getScaleX(), getScaleY(), getScaleZ());
    }

    public Matrix4 toNormalMatrix() {
        float[] fArr = this.val;
        fArr[12] = 0.0f;
        fArr[13] = 0.0f;
        fArr[14] = 0.0f;
        return inv().tra();
    }

    public Matrix4 translate(Vector3 translation) {
        return translate(translation.x, translation.y, translation.z);
    }

    public Matrix4 translate(float x, float y, float z) {
        float[] fArr = tmp;
        fArr[0] = 1.0f;
        fArr[4] = 0.0f;
        fArr[8] = 0.0f;
        fArr[12] = x;
        fArr[1] = 0.0f;
        fArr[5] = 1.0f;
        fArr[9] = 0.0f;
        fArr[13] = y;
        fArr[2] = 0.0f;
        fArr[6] = 0.0f;
        fArr[10] = 1.0f;
        fArr[14] = z;
        fArr[3] = 0.0f;
        fArr[7] = 0.0f;
        fArr[11] = 0.0f;
        fArr[15] = 1.0f;
        mul(this.val, fArr);
        return this;
    }

    public Matrix4 rotate(Vector3 axis, float degrees) {
        if (degrees == 0.0f) {
            return this;
        }
        quat.set(axis, degrees);
        return rotate(quat);
    }

    public Matrix4 rotateRad(Vector3 axis, float radians) {
        if (radians == 0.0f) {
            return this;
        }
        quat.setFromAxisRad(axis, radians);
        return rotate(quat);
    }

    public Matrix4 rotate(float axisX, float axisY, float axisZ, float degrees) {
        if (degrees == 0.0f) {
            return this;
        }
        quat.setFromAxis(axisX, axisY, axisZ, degrees);
        return rotate(quat);
    }

    public Matrix4 rotateRad(float axisX, float axisY, float axisZ, float radians) {
        if (radians == 0.0f) {
            return this;
        }
        quat.setFromAxisRad(axisX, axisY, axisZ, radians);
        return rotate(quat);
    }

    public Matrix4 rotate(Quaternion rotation) {
        rotation.toMatrix(tmp);
        mul(this.val, tmp);
        return this;
    }

    public Matrix4 rotate(Vector3 v1, Vector3 v2) {
        return rotate(quat.setFromCross(v1, v2));
    }

    public Matrix4 scale(float scaleX, float scaleY, float scaleZ) {
        float[] fArr = tmp;
        fArr[0] = scaleX;
        fArr[4] = 0.0f;
        fArr[8] = 0.0f;
        fArr[12] = 0.0f;
        fArr[1] = 0.0f;
        fArr[5] = scaleY;
        fArr[9] = 0.0f;
        fArr[13] = 0.0f;
        fArr[2] = 0.0f;
        fArr[6] = 0.0f;
        fArr[10] = scaleZ;
        fArr[14] = 0.0f;
        fArr[3] = 0.0f;
        fArr[7] = 0.0f;
        fArr[11] = 0.0f;
        fArr[15] = 1.0f;
        mul(this.val, fArr);
        return this;
    }

    public void extract4x3Matrix(float[] dst) {
        float[] fArr = this.val;
        dst[0] = fArr[0];
        dst[1] = fArr[1];
        dst[2] = fArr[2];
        dst[3] = fArr[4];
        dst[4] = fArr[5];
        dst[5] = fArr[6];
        dst[6] = fArr[8];
        dst[7] = fArr[9];
        dst[8] = fArr[10];
        dst[9] = fArr[12];
        dst[10] = fArr[13];
        dst[11] = fArr[14];
    }

    public boolean hasRotationOrScaling() {
        if (!MathUtils.isEqual(this.val[0], 1.0f) || !MathUtils.isEqual(this.val[5], 1.0f) || !MathUtils.isEqual(this.val[10], 1.0f) || !MathUtils.isZero(this.val[4]) || !MathUtils.isZero(this.val[8]) || !MathUtils.isZero(this.val[1]) || !MathUtils.isZero(this.val[9]) || !MathUtils.isZero(this.val[2]) || !MathUtils.isZero(this.val[6])) {
            return true;
        }
        return false;
    }
}
