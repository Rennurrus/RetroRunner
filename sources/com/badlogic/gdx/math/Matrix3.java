package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public class Matrix3 implements Serializable {
    public static final int M00 = 0;
    public static final int M01 = 3;
    public static final int M02 = 6;
    public static final int M10 = 1;
    public static final int M11 = 4;
    public static final int M12 = 7;
    public static final int M20 = 2;
    public static final int M21 = 5;
    public static final int M22 = 8;
    private static final long serialVersionUID = 7907569533774959788L;
    private float[] tmp = new float[9];
    public float[] val = new float[9];

    public Matrix3() {
        idt();
    }

    public Matrix3(Matrix3 matrix) {
        set(matrix);
    }

    public Matrix3(float[] values) {
        set(values);
    }

    public Matrix3 idt() {
        float[] val2 = this.val;
        val2[0] = 1.0f;
        val2[1] = 0.0f;
        val2[2] = 0.0f;
        val2[3] = 0.0f;
        val2[4] = 1.0f;
        val2[5] = 0.0f;
        val2[6] = 0.0f;
        val2[7] = 0.0f;
        val2[8] = 1.0f;
        return this;
    }

    public Matrix3 mul(Matrix3 m) {
        float[] val2 = this.val;
        float f = val2[0];
        float[] fArr = m.val;
        float v00 = (f * fArr[0]) + (val2[3] * fArr[1]) + (val2[6] * fArr[2]);
        float v01 = (val2[0] * fArr[3]) + (val2[3] * fArr[4]) + (val2[6] * fArr[5]);
        float v02 = (val2[0] * fArr[6]) + (val2[3] * fArr[7]) + (val2[6] * fArr[8]);
        float v10 = (val2[1] * fArr[0]) + (val2[4] * fArr[1]) + (val2[7] * fArr[2]);
        float v11 = (val2[1] * fArr[3]) + (val2[4] * fArr[4]) + (val2[7] * fArr[5]);
        float v12 = (val2[1] * fArr[6]) + (val2[4] * fArr[7]) + (val2[7] * fArr[8]);
        float v20 = (val2[2] * fArr[0]) + (val2[5] * fArr[1]) + (val2[8] * fArr[2]);
        float v21 = (val2[2] * fArr[3]) + (val2[5] * fArr[4]) + (val2[8] * fArr[5]);
        val2[0] = v00;
        val2[1] = v10;
        val2[2] = v20;
        val2[3] = v01;
        val2[4] = v11;
        val2[5] = v21;
        val2[6] = v02;
        val2[7] = v12;
        val2[8] = (val2[2] * fArr[6]) + (val2[5] * fArr[7]) + (val2[8] * fArr[8]);
        return this;
    }

    public Matrix3 mulLeft(Matrix3 m) {
        float[] val2 = this.val;
        float[] fArr = m.val;
        float v00 = (fArr[0] * val2[0]) + (fArr[3] * val2[1]) + (fArr[6] * val2[2]);
        float v01 = (fArr[0] * val2[3]) + (fArr[3] * val2[4]) + (fArr[6] * val2[5]);
        float v02 = (fArr[0] * val2[6]) + (fArr[3] * val2[7]) + (fArr[6] * val2[8]);
        float v10 = (fArr[1] * val2[0]) + (fArr[4] * val2[1]) + (fArr[7] * val2[2]);
        float v11 = (fArr[1] * val2[3]) + (fArr[4] * val2[4]) + (fArr[7] * val2[5]);
        float v12 = (fArr[1] * val2[6]) + (fArr[4] * val2[7]) + (fArr[7] * val2[8]);
        float v20 = (fArr[2] * val2[0]) + (fArr[5] * val2[1]) + (fArr[8] * val2[2]);
        float v21 = (fArr[2] * val2[3]) + (fArr[5] * val2[4]) + (fArr[8] * val2[5]);
        val2[0] = v00;
        val2[1] = v10;
        val2[2] = v20;
        val2[3] = v01;
        val2[4] = v11;
        val2[5] = v21;
        val2[6] = v02;
        val2[7] = v12;
        val2[8] = (fArr[2] * val2[6]) + (fArr[5] * val2[7]) + (fArr[8] * val2[8]);
        return this;
    }

    public Matrix3 setToRotation(float degrees) {
        return setToRotationRad(0.017453292f * degrees);
    }

    public Matrix3 setToRotationRad(float radians) {
        float cos = (float) Math.cos((double) radians);
        float sin = (float) Math.sin((double) radians);
        float[] val2 = this.val;
        val2[0] = cos;
        val2[1] = sin;
        val2[2] = 0.0f;
        val2[3] = -sin;
        val2[4] = cos;
        val2[5] = 0.0f;
        val2[6] = 0.0f;
        val2[7] = 0.0f;
        val2[8] = 1.0f;
        return this;
    }

    public Matrix3 setToRotation(Vector3 axis, float degrees) {
        return setToRotation(axis, MathUtils.cosDeg(degrees), MathUtils.sinDeg(degrees));
    }

    public Matrix3 setToRotation(Vector3 axis, float cos, float sin) {
        float[] val2 = this.val;
        float oc = 1.0f - cos;
        val2[0] = (axis.x * oc * axis.x) + cos;
        val2[1] = ((axis.x * oc) * axis.y) - (axis.z * sin);
        val2[2] = (axis.z * oc * axis.x) + (axis.y * sin);
        val2[3] = (axis.x * oc * axis.y) + (axis.z * sin);
        val2[4] = (axis.y * oc * axis.y) + cos;
        val2[5] = ((axis.y * oc) * axis.z) - (axis.x * sin);
        val2[6] = ((axis.z * oc) * axis.x) - (axis.y * sin);
        val2[7] = (axis.y * oc * axis.z) + (axis.x * sin);
        val2[8] = (axis.z * oc * axis.z) + cos;
        return this;
    }

    public Matrix3 setToTranslation(float x, float y) {
        float[] val2 = this.val;
        val2[0] = 1.0f;
        val2[1] = 0.0f;
        val2[2] = 0.0f;
        val2[3] = 0.0f;
        val2[4] = 1.0f;
        val2[5] = 0.0f;
        val2[6] = x;
        val2[7] = y;
        val2[8] = 1.0f;
        return this;
    }

    public Matrix3 setToTranslation(Vector2 translation) {
        float[] val2 = this.val;
        val2[0] = 1.0f;
        val2[1] = 0.0f;
        val2[2] = 0.0f;
        val2[3] = 0.0f;
        val2[4] = 1.0f;
        val2[5] = 0.0f;
        val2[6] = translation.x;
        val2[7] = translation.y;
        val2[8] = 1.0f;
        return this;
    }

    public Matrix3 setToScaling(float scaleX, float scaleY) {
        float[] val2 = this.val;
        val2[0] = scaleX;
        val2[1] = 0.0f;
        val2[2] = 0.0f;
        val2[3] = 0.0f;
        val2[4] = scaleY;
        val2[5] = 0.0f;
        val2[6] = 0.0f;
        val2[7] = 0.0f;
        val2[8] = 1.0f;
        return this;
    }

    public Matrix3 setToScaling(Vector2 scale) {
        float[] val2 = this.val;
        val2[0] = scale.x;
        val2[1] = 0.0f;
        val2[2] = 0.0f;
        val2[3] = 0.0f;
        val2[4] = scale.y;
        val2[5] = 0.0f;
        val2[6] = 0.0f;
        val2[7] = 0.0f;
        val2[8] = 1.0f;
        return this;
    }

    public String toString() {
        float[] val2 = this.val;
        return "[" + val2[0] + "|" + val2[3] + "|" + val2[6] + "]\n[" + val2[1] + "|" + val2[4] + "|" + val2[7] + "]\n[" + val2[2] + "|" + val2[5] + "|" + val2[8] + "]";
    }

    public float det() {
        float[] val2 = this.val;
        return ((((((val2[0] * val2[4]) * val2[8]) + ((val2[3] * val2[7]) * val2[2])) + ((val2[6] * val2[1]) * val2[5])) - ((val2[0] * val2[7]) * val2[5])) - ((val2[3] * val2[1]) * val2[8])) - ((val2[6] * val2[4]) * val2[2]);
    }

    public Matrix3 inv() {
        float det = det();
        if (det != 0.0f) {
            float inv_det = 1.0f / det;
            float[] tmp2 = this.tmp;
            float[] val2 = this.val;
            tmp2[0] = (val2[4] * val2[8]) - (val2[5] * val2[7]);
            tmp2[1] = (val2[2] * val2[7]) - (val2[1] * val2[8]);
            tmp2[2] = (val2[1] * val2[5]) - (val2[2] * val2[4]);
            tmp2[3] = (val2[5] * val2[6]) - (val2[3] * val2[8]);
            tmp2[4] = (val2[0] * val2[8]) - (val2[2] * val2[6]);
            tmp2[5] = (val2[2] * val2[3]) - (val2[0] * val2[5]);
            tmp2[6] = (val2[3] * val2[7]) - (val2[4] * val2[6]);
            tmp2[7] = (val2[1] * val2[6]) - (val2[0] * val2[7]);
            tmp2[8] = (val2[0] * val2[4]) - (val2[1] * val2[3]);
            val2[0] = tmp2[0] * inv_det;
            val2[1] = tmp2[1] * inv_det;
            val2[2] = tmp2[2] * inv_det;
            val2[3] = tmp2[3] * inv_det;
            val2[4] = tmp2[4] * inv_det;
            val2[5] = tmp2[5] * inv_det;
            val2[6] = tmp2[6] * inv_det;
            val2[7] = tmp2[7] * inv_det;
            val2[8] = tmp2[8] * inv_det;
            return this;
        }
        throw new GdxRuntimeException("Can't invert a singular matrix");
    }

    public Matrix3 set(Matrix3 mat) {
        float[] fArr = mat.val;
        float[] fArr2 = this.val;
        System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
        return this;
    }

    public Matrix3 set(Affine2 affine) {
        float[] val2 = this.val;
        val2[0] = affine.m00;
        val2[1] = affine.m10;
        val2[2] = 0.0f;
        val2[3] = affine.m01;
        val2[4] = affine.m11;
        val2[5] = 0.0f;
        val2[6] = affine.m02;
        val2[7] = affine.m12;
        val2[8] = 1.0f;
        return this;
    }

    public Matrix3 set(Matrix4 mat) {
        float[] val2 = this.val;
        val2[0] = mat.val[0];
        val2[1] = mat.val[1];
        val2[2] = mat.val[2];
        val2[3] = mat.val[4];
        val2[4] = mat.val[5];
        val2[5] = mat.val[6];
        val2[6] = mat.val[8];
        val2[7] = mat.val[9];
        val2[8] = mat.val[10];
        return this;
    }

    public Matrix3 set(float[] values) {
        float[] fArr = this.val;
        System.arraycopy(values, 0, fArr, 0, fArr.length);
        return this;
    }

    public Matrix3 trn(Vector2 vector) {
        float[] fArr = this.val;
        fArr[6] = fArr[6] + vector.x;
        float[] fArr2 = this.val;
        fArr2[7] = fArr2[7] + vector.y;
        return this;
    }

    public Matrix3 trn(float x, float y) {
        float[] fArr = this.val;
        fArr[6] = fArr[6] + x;
        fArr[7] = fArr[7] + y;
        return this;
    }

    public Matrix3 trn(Vector3 vector) {
        float[] fArr = this.val;
        fArr[6] = fArr[6] + vector.x;
        float[] fArr2 = this.val;
        fArr2[7] = fArr2[7] + vector.y;
        return this;
    }

    public Matrix3 translate(float x, float y) {
        float[] val2 = this.val;
        float[] fArr = this.tmp;
        fArr[0] = 1.0f;
        fArr[1] = 0.0f;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = 1.0f;
        fArr[5] = 0.0f;
        fArr[6] = x;
        fArr[7] = y;
        fArr[8] = 1.0f;
        mul(val2, fArr);
        return this;
    }

    public Matrix3 translate(Vector2 translation) {
        float[] val2 = this.val;
        float[] fArr = this.tmp;
        fArr[0] = 1.0f;
        fArr[1] = 0.0f;
        fArr[2] = 0.0f;
        fArr[3] = 0.0f;
        fArr[4] = 1.0f;
        fArr[5] = 0.0f;
        fArr[6] = translation.x;
        this.tmp[7] = translation.y;
        float[] fArr2 = this.tmp;
        fArr2[8] = 1.0f;
        mul(val2, fArr2);
        return this;
    }

    public Matrix3 rotate(float degrees) {
        return rotateRad(0.017453292f * degrees);
    }

    public Matrix3 rotateRad(float radians) {
        if (radians == 0.0f) {
            return this;
        }
        float cos = (float) Math.cos((double) radians);
        float sin = (float) Math.sin((double) radians);
        float[] tmp2 = this.tmp;
        tmp2[0] = cos;
        tmp2[1] = sin;
        tmp2[2] = 0.0f;
        tmp2[3] = -sin;
        tmp2[4] = cos;
        tmp2[5] = 0.0f;
        tmp2[6] = 0.0f;
        tmp2[7] = 0.0f;
        tmp2[8] = 1.0f;
        mul(this.val, tmp2);
        return this;
    }

    public Matrix3 scale(float scaleX, float scaleY) {
        float[] tmp2 = this.tmp;
        tmp2[0] = scaleX;
        tmp2[1] = 0.0f;
        tmp2[2] = 0.0f;
        tmp2[3] = 0.0f;
        tmp2[4] = scaleY;
        tmp2[5] = 0.0f;
        tmp2[6] = 0.0f;
        tmp2[7] = 0.0f;
        tmp2[8] = 1.0f;
        mul(this.val, tmp2);
        return this;
    }

    public Matrix3 scale(Vector2 scale) {
        float[] tmp2 = this.tmp;
        tmp2[0] = scale.x;
        tmp2[1] = 0.0f;
        tmp2[2] = 0.0f;
        tmp2[3] = 0.0f;
        tmp2[4] = scale.y;
        tmp2[5] = 0.0f;
        tmp2[6] = 0.0f;
        tmp2[7] = 0.0f;
        tmp2[8] = 1.0f;
        mul(this.val, tmp2);
        return this;
    }

    public float[] getValues() {
        return this.val;
    }

    public Vector2 getTranslation(Vector2 position) {
        float[] fArr = this.val;
        position.x = fArr[6];
        position.y = fArr[7];
        return position;
    }

    public Vector2 getScale(Vector2 scale) {
        float[] val2 = this.val;
        scale.x = (float) Math.sqrt((double) ((val2[0] * val2[0]) + (val2[3] * val2[3])));
        scale.y = (float) Math.sqrt((double) ((val2[1] * val2[1]) + (val2[4] * val2[4])));
        return scale;
    }

    public float getRotation() {
        float[] fArr = this.val;
        return ((float) Math.atan2((double) fArr[1], (double) fArr[0])) * 57.295776f;
    }

    public float getRotationRad() {
        float[] fArr = this.val;
        return (float) Math.atan2((double) fArr[1], (double) fArr[0]);
    }

    public Matrix3 scl(float scale) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * scale;
        fArr[4] = fArr[4] * scale;
        return this;
    }

    public Matrix3 scl(Vector2 scale) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * scale.x;
        float[] fArr2 = this.val;
        fArr2[4] = fArr2[4] * scale.y;
        return this;
    }

    public Matrix3 scl(Vector3 scale) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * scale.x;
        float[] fArr2 = this.val;
        fArr2[4] = fArr2[4] * scale.y;
        return this;
    }

    public Matrix3 transpose() {
        float[] val2 = this.val;
        float v01 = val2[1];
        float v02 = val2[2];
        float v10 = val2[3];
        float v12 = val2[5];
        float v20 = val2[6];
        float v21 = val2[7];
        val2[3] = v01;
        val2[6] = v02;
        val2[1] = v10;
        val2[7] = v12;
        val2[2] = v20;
        val2[5] = v21;
        return this;
    }

    private static void mul(float[] mata, float[] matb) {
        float v00 = (mata[0] * matb[0]) + (mata[3] * matb[1]) + (mata[6] * matb[2]);
        float v01 = (mata[0] * matb[3]) + (mata[3] * matb[4]) + (mata[6] * matb[5]);
        float v02 = (mata[0] * matb[6]) + (mata[3] * matb[7]) + (mata[6] * matb[8]);
        float v10 = (mata[1] * matb[0]) + (mata[4] * matb[1]) + (mata[7] * matb[2]);
        float v11 = (mata[1] * matb[3]) + (mata[4] * matb[4]) + (mata[7] * matb[5]);
        float v12 = (mata[1] * matb[6]) + (mata[4] * matb[7]) + (mata[7] * matb[8]);
        float v20 = (mata[2] * matb[0]) + (mata[5] * matb[1]) + (mata[8] * matb[2]);
        float v21 = (mata[2] * matb[3]) + (mata[5] * matb[4]) + (mata[8] * matb[5]);
        mata[0] = v00;
        mata[1] = v10;
        mata[2] = v20;
        mata[3] = v01;
        mata[4] = v11;
        mata[5] = v21;
        mata[6] = v02;
        mata[7] = v12;
        mata[8] = (mata[2] * matb[6]) + (mata[5] * matb[7]) + (mata[8] * matb[8]);
    }
}
