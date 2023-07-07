package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.utils.Array;

public abstract class Attribute implements Comparable<Attribute> {
    private static final Array<String> types = new Array<>();
    public final long type;
    private final int typeBit;

    public abstract Attribute copy();

    public static final long getAttributeType(String alias) {
        for (int i = 0; i < types.size; i++) {
            if (types.get(i).compareTo(alias) == 0) {
                return 1 << i;
            }
        }
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:1:0x0001 A[LOOP:0: B:1:0x0001->B:6:0x0014, LOOP_START, PHI: r0 
      PHI: (r0v1 'idx' int) = (r0v0 'idx' int), (r0v3 'idx' int) binds: [B:0:0x0000, B:6:0x0014] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.String getAttributeAlias(long r7) {
        /*
            r0 = -1
        L_0x0001:
            r1 = 0
            int r3 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0017
            int r0 = r0 + 1
            r3 = 63
            if (r0 >= r3) goto L_0x0017
            long r3 = r7 >> r0
            r5 = 1
            long r3 = r3 & r5
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 != 0) goto L_0x0017
            goto L_0x0001
        L_0x0017:
            if (r0 < 0) goto L_0x0028
            com.badlogic.gdx.utils.Array<java.lang.String> r1 = types
            int r1 = r1.size
            if (r0 >= r1) goto L_0x0028
            com.badlogic.gdx.utils.Array<java.lang.String> r1 = types
            java.lang.Object r1 = r1.get(r0)
            java.lang.String r1 = (java.lang.String) r1
            goto L_0x0029
        L_0x0028:
            r1 = 0
        L_0x0029:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g3d.Attribute.getAttributeAlias(long):java.lang.String");
    }

    protected static final long register(String alias) {
        long result = getAttributeType(alias);
        if (result > 0) {
            return result;
        }
        types.add(alias);
        return 1 << (types.size - 1);
    }

    protected Attribute(long type2) {
        this.type = type2;
        this.typeBit = Long.numberOfTrailingZeros(type2);
    }

    /* access modifiers changed from: protected */
    public boolean equals(Attribute other) {
        return other.hashCode() == hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        Attribute other = (Attribute) obj;
        if (this.type != other.type) {
            return false;
        }
        return equals(other);
    }

    public String toString() {
        return getAttributeAlias(this.type);
    }

    public int hashCode() {
        return this.typeBit * 7489;
    }
}
