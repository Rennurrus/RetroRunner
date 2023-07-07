package com.badlogic.gdx;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class Version {
    public static final int MAJOR;
    public static final int MINOR;
    public static final int REVISION;
    public static final String VERSION = "1.9.10";

    static {
        try {
            String[] v = VERSION.split("\\.");
            int i = 0;
            MAJOR = v.length < 1 ? 0 : Integer.valueOf(v[0]).intValue();
            MINOR = v.length < 2 ? 0 : Integer.valueOf(v[1]).intValue();
            if (v.length >= 3) {
                i = Integer.valueOf(v[2]).intValue();
            }
            REVISION = i;
        } catch (Throwable t) {
            throw new GdxRuntimeException("Invalid version 1.9.10", t);
        }
    }

    public static boolean isHigher(int major, int minor, int revision) {
        return isHigherEqual(major, minor, revision + 1);
    }

    public static boolean isHigherEqual(int major, int minor, int revision) {
        int i = MAJOR;
        if (i == major) {
            int i2 = MINOR;
            if (i2 != minor) {
                if (i2 > minor) {
                    return true;
                }
                return false;
            } else if (REVISION >= revision) {
                return true;
            } else {
                return false;
            }
        } else if (i > major) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLower(int major, int minor, int revision) {
        return isLowerEqual(major, minor, revision - 1);
    }

    public static boolean isLowerEqual(int major, int minor, int revision) {
        int i = MAJOR;
        if (i == major) {
            int i2 = MINOR;
            if (i2 != minor) {
                if (i2 < minor) {
                    return true;
                }
                return false;
            } else if (REVISION <= revision) {
                return true;
            } else {
                return false;
            }
        } else if (i < major) {
            return true;
        } else {
            return false;
        }
    }
}
