package com.badlogic.gdx.utils;

import java.util.Arrays;
import java.util.Comparator;

class TimSort<T> {
    private static final boolean DEBUG = false;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private static final int MIN_GALLOP = 7;
    private static final int MIN_MERGE = 32;
    private T[] a;
    private Comparator<? super T> c;
    private int minGallop;
    private final int[] runBase;
    private final int[] runLen;
    private int stackSize;
    private T[] tmp;
    private int tmpCount;

    TimSort() {
        this.minGallop = 7;
        this.stackSize = 0;
        this.tmp = (Object[]) new Object[256];
        this.runBase = new int[40];
        this.runLen = new int[40];
    }

    public void doSort(T[] a2, Comparator<T> c2, int lo, int hi) {
        this.stackSize = 0;
        rangeCheck(a2.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < 32) {
                binarySort(a2, lo, hi, lo + countRunAndMakeAscending(a2, lo, hi, c2), c2);
                return;
            }
            this.a = a2;
            this.c = c2;
            this.tmpCount = 0;
            int minRun = minRunLength(nRemaining);
            do {
                int runLen2 = countRunAndMakeAscending(a2, lo, hi, c2);
                if (runLen2 < minRun) {
                    int force = nRemaining <= minRun ? nRemaining : minRun;
                    binarySort(a2, lo, lo + force, lo + runLen2, c2);
                    runLen2 = force;
                }
                pushRun(lo, runLen2);
                mergeCollapse();
                lo += runLen2;
                nRemaining -= runLen2;
            } while (nRemaining != 0);
            mergeForceCollapse();
            this.a = null;
            this.c = null;
            T[] tmp2 = this.tmp;
            int n = this.tmpCount;
            for (int i = 0; i < n; i++) {
                tmp2[i] = null;
            }
        }
    }

    private TimSort(T[] a2, Comparator<? super T> c2) {
        this.minGallop = 7;
        this.stackSize = 0;
        this.a = a2;
        this.c = c2;
        int len = a2.length;
        this.tmp = (Object[]) new Object[(len < 512 ? len >>> 1 : 256)];
        int stackLen = len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40;
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    static <T> void sort(T[] a2, Comparator<? super T> c2) {
        sort(a2, 0, a2.length, c2);
    }

    static <T> void sort(T[] a2, int lo, int hi, Comparator<? super T> c2) {
        if (c2 == null) {
            Arrays.sort(a2, lo, hi);
            return;
        }
        rangeCheck(a2.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < 32) {
                binarySort(a2, lo, hi, lo + countRunAndMakeAscending(a2, lo, hi, c2), c2);
                return;
            }
            TimSort<T> ts = new TimSort<>(a2, c2);
            int minRun = minRunLength(nRemaining);
            do {
                int runLen2 = countRunAndMakeAscending(a2, lo, hi, c2);
                if (runLen2 < minRun) {
                    int force = nRemaining <= minRun ? nRemaining : minRun;
                    binarySort(a2, lo, lo + force, lo + runLen2, c2);
                    runLen2 = force;
                }
                ts.pushRun(lo, runLen2);
                ts.mergeCollapse();
                lo += runLen2;
                nRemaining -= runLen2;
            } while (nRemaining != 0);
            ts.mergeForceCollapse();
        }
    }

    private static <T> void binarySort(T[] a2, int lo, int hi, int start, Comparator<? super T> c2) {
        if (start == lo) {
            start++;
        }
        while (start < hi) {
            T pivot = a2[start];
            int left = lo;
            int right = start;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (c2.compare(pivot, a2[mid]) < 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            int n = start - left;
            if (n != 1) {
                if (n != 2) {
                    System.arraycopy(a2, left, a2, left + 1, n);
                    a2[left] = pivot;
                    start++;
                } else {
                    a2[left + 2] = a2[left + 1];
                }
            }
            a2[left + 1] = a2[left];
            a2[left] = pivot;
            start++;
        }
    }

    private static <T> int countRunAndMakeAscending(T[] a2, int lo, int hi, Comparator<? super T> c2) {
        int runHi = lo + 1;
        if (runHi == hi) {
            return 1;
        }
        int runHi2 = runHi + 1;
        if (c2.compare(a2[runHi], a2[lo]) < 0) {
            while (runHi2 < hi && c2.compare(a2[runHi2], a2[runHi2 - 1]) < 0) {
                runHi2++;
            }
            reverseRange(a2, lo, runHi2);
        } else {
            while (runHi2 < hi && c2.compare(a2[runHi2], a2[runHi2 - 1]) >= 0) {
                runHi2++;
            }
        }
        return runHi2 - lo;
    }

    private static void reverseRange(Object[] a2, int hi, int hi2) {
        int hi3 = hi2 - 1;
        while (hi < hi3) {
            Object t = a2[hi];
            a2[hi] = a2[hi3];
            a2[hi3] = t;
            hi3--;
            hi++;
        }
    }

    private static int minRunLength(int n) {
        int r = 0;
        while (n >= 32) {
            r |= n & 1;
            n >>= 1;
        }
        return n + r;
    }

    private void pushRun(int runBase2, int runLen2) {
        int[] iArr = this.runBase;
        int i = this.stackSize;
        iArr[i] = runBase2;
        this.runLen[i] = runLen2;
        this.stackSize = i + 1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002a, code lost:
        r1 = r5.runLen;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0034, code lost:
        if (r1[r0 - 1] >= r1[r0 + 1]) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0036, code lost:
        r0 = r0 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0016, code lost:
        if (r1[r0 - 1] > (r1[r0] + r1[r0 + 1])) goto L_0x0018;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        if (r1[r0 - 2] <= (r1[r0] + r1[r0 - 1])) goto L_0x002a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeCollapse() {
        /*
            r5 = this;
        L_0x0000:
            int r0 = r5.stackSize
            r1 = 1
            if (r0 <= r1) goto L_0x0048
            int r0 = r0 + -2
            if (r0 < r1) goto L_0x0018
            int[] r1 = r5.runLen
            int r2 = r0 + -1
            r2 = r1[r2]
            r3 = r1[r0]
            int r4 = r0 + 1
            r1 = r1[r4]
            int r3 = r3 + r1
            if (r2 <= r3) goto L_0x002a
        L_0x0018:
            r1 = 2
            if (r0 < r1) goto L_0x0039
            int[] r1 = r5.runLen
            int r2 = r0 + -2
            r2 = r1[r2]
            r3 = r1[r0]
            int r4 = r0 + -1
            r1 = r1[r4]
            int r3 = r3 + r1
            if (r2 > r3) goto L_0x0039
        L_0x002a:
            int[] r1 = r5.runLen
            int r2 = r0 + -1
            r2 = r1[r2]
            int r3 = r0 + 1
            r1 = r1[r3]
            if (r2 >= r1) goto L_0x0044
            int r0 = r0 + -1
            goto L_0x0044
        L_0x0039:
            int[] r1 = r5.runLen
            r2 = r1[r0]
            int r3 = r0 + 1
            r1 = r1[r3]
            if (r2 <= r1) goto L_0x0044
            goto L_0x0048
        L_0x0044:
            r5.mergeAt(r0)
            goto L_0x0000
        L_0x0048:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.TimSort.mergeCollapse():void");
    }

    private void mergeForceCollapse() {
        while (true) {
            int i = this.stackSize;
            if (i > 1) {
                int n = i - 2;
                if (n > 0) {
                    int[] iArr = this.runLen;
                    if (iArr[n - 1] < iArr[n + 1]) {
                        n--;
                    }
                }
                mergeAt(n);
            } else {
                return;
            }
        }
    }

    private void mergeAt(int i) {
        int i2 = i;
        int[] iArr = this.runBase;
        int base1 = iArr[i2];
        int[] iArr2 = this.runLen;
        int len1 = iArr2[i2];
        int base2 = iArr[i2 + 1];
        int len2 = iArr2[i2 + 1];
        iArr2[i2] = len1 + len2;
        if (i2 == this.stackSize - 3) {
            iArr[i2 + 1] = iArr[i2 + 2];
            iArr2[i2 + 1] = iArr2[i2 + 2];
        }
        this.stackSize--;
        T[] tArr = this.a;
        int k = gallopRight(tArr[base2], tArr, base1, len1, 0, this.c);
        int base12 = base1 + k;
        int len12 = len1 - k;
        if (len12 != 0) {
            T[] tArr2 = this.a;
            int base22 = base2;
            int len22 = gallopLeft(tArr2[(base12 + len12) - 1], tArr2, base2, len2, len2 - 1, this.c);
            if (len22 != 0) {
                if (len12 <= len22) {
                    mergeLo(base12, len12, base22, len22);
                } else {
                    mergeHi(base12, len12, base22, len22);
                }
            }
        }
    }

    private static <T> int gallopLeft(T key, T[] a2, int base, int len, int hint, Comparator<? super T> c2) {
        int ofs;
        int lastOfs;
        int lastOfs2 = 0;
        int ofs2 = 1;
        if (c2.compare(key, a2[base + hint]) > 0) {
            int maxOfs = len - hint;
            while (ofs2 < maxOfs && c2.compare(key, a2[base + hint + ofs2]) > 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs;
                }
            }
            if (ofs2 > maxOfs) {
                ofs2 = maxOfs;
            }
            lastOfs = lastOfs2 + hint;
            ofs = ofs2 + hint;
        } else {
            int maxOfs2 = hint + 1;
            while (ofs2 < maxOfs2 && c2.compare(key, a2[(base + hint) - ofs2]) <= 0) {
                lastOfs2 = ofs2;
                int ofs3 = (ofs2 << 1) + 1;
                if (ofs3 <= 0) {
                    ofs3 = maxOfs2;
                }
            }
            if (ofs2 > maxOfs2) {
                ofs2 = maxOfs2;
            }
            int tmp2 = lastOfs2;
            lastOfs = hint - ofs2;
            ofs = hint - tmp2;
        }
        int lastOfs3 = lastOfs + 1;
        while (lastOfs3 < ofs) {
            int m = ((ofs - lastOfs3) >>> 1) + lastOfs3;
            if (c2.compare(key, a2[base + m]) > 0) {
                lastOfs3 = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static <T> int gallopRight(T key, T[] a2, int base, int len, int hint, Comparator<? super T> c2) {
        int lastOfs;
        int ofs;
        int ofs2 = 1;
        int lastOfs2 = 0;
        if (c2.compare(key, a2[base + hint]) < 0) {
            int maxOfs = hint + 1;
            while (ofs2 < maxOfs && c2.compare(key, a2[(base + hint) - ofs2]) < 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs;
                }
            }
            if (ofs2 > maxOfs) {
                ofs2 = maxOfs;
            }
            int tmp2 = lastOfs2;
            lastOfs = hint - ofs2;
            ofs = hint - tmp2;
        } else {
            int maxOfs2 = len - hint;
            while (ofs2 < maxOfs2 && c2.compare(key, a2[base + hint + ofs2]) >= 0) {
                lastOfs2 = ofs2;
                int ofs3 = (ofs2 << 1) + 1;
                if (ofs3 <= 0) {
                    ofs3 = maxOfs2;
                }
            }
            if (ofs2 > maxOfs2) {
                ofs2 = maxOfs2;
            }
            lastOfs = lastOfs2 + hint;
            ofs = ofs2 + hint;
        }
        int lastOfs3 = lastOfs + 1;
        while (lastOfs3 < ofs) {
            int m = ((ofs - lastOfs3) >>> 1) + lastOfs3;
            if (c2.compare(key, a2[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs3 = m + 1;
            }
        }
        return ofs;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0077, code lost:
        r16 = r3;
        r18 = r4;
        r9 = r6;
        r15 = r13;
        r17 = r14;
        r14 = r1;
        r13 = r2;
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0082, code lost:
        r11 = r6;
        r15 = gallopRight(r7[r9], r8, r13, r14, 0, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x008e, code lost:
        if (r15 == 0) goto L_0x00a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0090, code lost:
        java.lang.System.arraycopy(r8, r13, r7, r11, r15);
        r1 = r11 + r15;
        r2 = r13 + r15;
        r3 = r14 - r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009a, code lost:
        if (r3 > 1) goto L_0x00a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x009c, code lost:
        r14 = r3;
        r6 = r9;
        r3 = r16;
        r11 = r18;
        r10 = 1;
        r9 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a6, code lost:
        r11 = r1;
        r13 = r2;
        r14 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a9, code lost:
        r6 = r11 + 1;
        r5 = r9 + 1;
        r7[r11] = r7[r9];
        r9 = r16 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b3, code lost:
        if (r9 != 0) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b5, code lost:
        r3 = r9;
        r2 = r13;
        r11 = r18;
        r10 = 1;
        r9 = r6;
        r6 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00bd, code lost:
        r10 = r5;
        r11 = r6;
        r1 = gallopLeft(r8[r13], r7, r5, r9, 0, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00cb, code lost:
        if (r1 == 0) goto L_0x00e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00cd, code lost:
        java.lang.System.arraycopy(r7, r10, r7, r11, r1);
        r2 = r11 + r1;
        r6 = r10 + r1;
        r3 = r9 - r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00d6, code lost:
        if (r3 != 0) goto L_0x00de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d8, code lost:
        r9 = r2;
        r2 = r13;
        r11 = r18;
        r10 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00de, code lost:
        r11 = r2;
        r16 = r3;
        r9 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00e3, code lost:
        r16 = r9;
        r9 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00e6, code lost:
        r6 = r11 + 1;
        r2 = r13 + 1;
        r7[r11] = r8[r13];
        r14 = r14 - 1;
        r10 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00f1, code lost:
        if (r14 != 1) goto L_0x011d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00f3, code lost:
        r3 = r16;
        r11 = r18;
        r19 = r9;
        r9 = r6;
        r6 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x011d, code lost:
        r18 = r18 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0120, code lost:
        if (r15 < 7) goto L_0x0124;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0122, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0124, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0125, code lost:
        if (r1 < 7) goto L_0x0129;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0127, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0129, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x012b, code lost:
        if ((r3 | r4) != false) goto L_0x013f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x012d, code lost:
        if (r18 >= 0) goto L_0x0131;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x012f, code lost:
        r18 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x013f, code lost:
        r10 = r21;
        r17 = r1;
        r13 = r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeLo(int r21, int r22, int r23, int r24) {
        /*
            r20 = this;
            r0 = r20
            r1 = r22
            T[] r7 = r0.a
            java.lang.Object[] r8 = r0.ensureCapacity(r1)
            r9 = 0
            r10 = r21
            java.lang.System.arraycopy(r7, r10, r8, r9, r1)
            r2 = 0
            r3 = r23
            r4 = r21
            int r5 = r4 + 1
            int r6 = r3 + 1
            r3 = r7[r3]
            r7[r4] = r3
            int r3 = r24 + -1
            if (r3 != 0) goto L_0x0025
            java.lang.System.arraycopy(r8, r2, r7, r5, r1)
            return
        L_0x0025:
            r11 = 1
            if (r1 != r11) goto L_0x0032
            java.lang.System.arraycopy(r7, r6, r7, r5, r3)
            int r4 = r5 + r3
            r9 = r8[r2]
            r7[r4] = r9
            return
        L_0x0032:
            java.util.Comparator<? super T> r12 = r0.c
            int r4 = r0.minGallop
        L_0x0036:
            r13 = 0
            r14 = 0
        L_0x0038:
            r15 = r7[r6]
            r9 = r8[r2]
            int r9 = r12.compare(r15, r9)
            if (r9 >= 0) goto L_0x005b
            int r9 = r5 + 1
            int r15 = r6 + 1
            r6 = r7[r6]
            r7[r5] = r6
            int r14 = r14 + 1
            r5 = 0
            int r3 = r3 + -1
            if (r3 != 0) goto L_0x0057
            r14 = r1
            r11 = r4
            r6 = r15
            r10 = 1
            goto L_0x00fc
        L_0x0057:
            r13 = r5
            r5 = r9
            r6 = r15
            goto L_0x0073
        L_0x005b:
            int r9 = r5 + 1
            int r15 = r2 + 1
            r2 = r8[r2]
            r7[r5] = r2
            int r13 = r13 + 1
            r2 = 0
            int r1 = r1 + -1
            if (r1 != r11) goto L_0x0070
            r14 = r1
            r11 = r4
            r2 = r15
            r10 = 1
            goto L_0x00fc
        L_0x0070:
            r14 = r2
            r5 = r9
            r2 = r15
        L_0x0073:
            r9 = r13 | r14
            if (r9 < r4) goto L_0x0147
            r16 = r3
            r18 = r4
            r9 = r6
            r15 = r13
            r17 = r14
            r14 = r1
            r13 = r2
            r6 = r5
        L_0x0082:
            r1 = r7[r9]
            r5 = 0
            r2 = r8
            r3 = r13
            r4 = r14
            r11 = r6
            r6 = r12
            int r15 = gallopRight(r1, r2, r3, r4, r5, r6)
            if (r15 == 0) goto L_0x00a9
            java.lang.System.arraycopy(r8, r13, r7, r11, r15)
            int r1 = r11 + r15
            int r2 = r13 + r15
            int r3 = r14 - r15
            r4 = 1
            if (r3 > r4) goto L_0x00a6
            r14 = r3
            r6 = r9
            r3 = r16
            r11 = r18
            r10 = 1
            r9 = r1
            goto L_0x00fc
        L_0x00a6:
            r11 = r1
            r13 = r2
            r14 = r3
        L_0x00a9:
            int r6 = r11 + 1
            int r5 = r9 + 1
            r1 = r7[r9]
            r7[r11] = r1
            int r9 = r16 + -1
            if (r9 != 0) goto L_0x00bd
            r3 = r9
            r2 = r13
            r11 = r18
            r10 = 1
            r9 = r6
            r6 = r5
            goto L_0x00fc
        L_0x00bd:
            r1 = r8[r13]
            r11 = 0
            r2 = r7
            r3 = r5
            r4 = r9
            r10 = r5
            r5 = r11
            r11 = r6
            r6 = r12
            int r1 = gallopLeft(r1, r2, r3, r4, r5, r6)
            if (r1 == 0) goto L_0x00e3
            java.lang.System.arraycopy(r7, r10, r7, r11, r1)
            int r2 = r11 + r1
            int r6 = r10 + r1
            int r3 = r9 - r1
            if (r3 != 0) goto L_0x00de
            r9 = r2
            r2 = r13
            r11 = r18
            r10 = 1
            goto L_0x00fc
        L_0x00de:
            r11 = r2
            r16 = r3
            r9 = r6
            goto L_0x00e6
        L_0x00e3:
            r16 = r9
            r9 = r10
        L_0x00e6:
            int r6 = r11 + 1
            int r2 = r13 + 1
            r3 = r8[r13]
            r7[r11] = r3
            int r14 = r14 + -1
            r10 = 1
            if (r14 != r10) goto L_0x011d
            r3 = r16
            r11 = r18
            r19 = r9
            r9 = r6
            r6 = r19
        L_0x00fc:
            if (r11 >= r10) goto L_0x0100
            r1 = 1
            goto L_0x0101
        L_0x0100:
            r1 = r11
        L_0x0101:
            r0.minGallop = r1
            if (r14 != r10) goto L_0x010f
            java.lang.System.arraycopy(r7, r6, r7, r9, r3)
            int r1 = r9 + r3
            r4 = r8[r2]
            r7[r1] = r4
            goto L_0x0114
        L_0x010f:
            if (r14 == 0) goto L_0x0115
            java.lang.System.arraycopy(r8, r2, r7, r9, r14)
        L_0x0114:
            return
        L_0x0115:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.String r4 = "Comparison method violates its general contract!"
            r1.<init>(r4)
            throw r1
        L_0x011d:
            int r18 = r18 + -1
            r3 = 7
            if (r15 < r3) goto L_0x0124
            r4 = 1
            goto L_0x0125
        L_0x0124:
            r4 = 0
        L_0x0125:
            if (r1 < r3) goto L_0x0129
            r3 = 1
            goto L_0x012a
        L_0x0129:
            r3 = 0
        L_0x012a:
            r3 = r3 | r4
            if (r3 != 0) goto L_0x013f
            if (r18 >= 0) goto L_0x0131
            r18 = 0
        L_0x0131:
            int r4 = r18 + 2
            r10 = r21
            r5 = r6
            r6 = r9
            r1 = r14
            r3 = r16
            r9 = 0
            r11 = 1
            goto L_0x0036
        L_0x013f:
            r10 = r21
            r17 = r1
            r13 = r2
            r11 = 1
            goto L_0x0082
        L_0x0147:
            r10 = 1
            r10 = r21
            r9 = 0
            r11 = 1
            goto L_0x0038
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.TimSort.mergeLo(int, int, int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0080, code lost:
        r17 = r2;
        r10 = r3;
        r19 = r5;
        r15 = r6;
        r16 = r7;
        r18 = r14;
        r14 = r13;
        r13 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x008c, code lost:
        r14 = r10 - gallopRight(r9[r13], r8, r24, r10, r10 - 1, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x009b, code lost:
        if (r14 == 0) goto L_0x00b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x009d, code lost:
        r2 = r15 - r14;
        r7 = r16 - r14;
        r3 = r10 - r14;
        java.lang.System.arraycopy(r8, r7 + 1, r8, r2 + 1, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00aa, code lost:
        if (r3 != 0) goto L_0x00b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ac, code lost:
        r10 = r2;
        r16 = r7;
        r2 = r17;
        r5 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b5, code lost:
        r15 = r2;
        r10 = r3;
        r16 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00b9, code lost:
        r20 = r15 - 1;
        r21 = r13 - 1;
        r8[r15] = r9[r13];
        r13 = r17 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00c3, code lost:
        if (r13 != 1) goto L_0x00ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00c5, code lost:
        r3 = r10;
        r2 = r13;
        r5 = r19;
        r10 = r20;
        r13 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ce, code lost:
        r2 = r13 - gallopLeft(r8[r16], r9, 0, r13, r13 - 1, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00dc, code lost:
        if (r2 == 0) goto L_0x00fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00de, code lost:
        r3 = r20 - r2;
        r4 = r21 - r2;
        r5 = r13 - r2;
        java.lang.System.arraycopy(r9, r4 + 1, r8, r3 + 1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00eb, code lost:
        if (r5 > 1) goto L_0x00f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ed, code lost:
        r13 = r4;
        r2 = r5;
        r5 = r19;
        r22 = r10;
        r10 = r3;
        r3 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00f7, code lost:
        r13 = r4;
        r17 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00fb, code lost:
        r17 = r13;
        r3 = r20;
        r13 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0101, code lost:
        r15 = r3 - 1;
        r4 = r16 - 1;
        r8[r3] = r8[r16];
        r10 = r10 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x010b, code lost:
        if (r10 != 0) goto L_0x0140;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x010d, code lost:
        r16 = r4;
        r3 = r10;
        r10 = r15;
        r2 = r17;
        r5 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0140, code lost:
        r19 = r19 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0144, code lost:
        if (r14 < 7) goto L_0x0148;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0146, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0148, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0149, code lost:
        if (r2 < 7) goto L_0x014d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x014b, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x014d, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x014f, code lost:
        if ((r3 | r5) != false) goto L_0x0161;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0151, code lost:
        if (r19 >= 0) goto L_0x0155;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0153, code lost:
        r19 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0161, code lost:
        r18 = r2;
        r16 = r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeHi(int r24, int r25, int r26, int r27) {
        /*
            r23 = this;
            r0 = r23
            r1 = r26
            r2 = r27
            T[] r8 = r0.a
            java.lang.Object[] r9 = r0.ensureCapacity(r2)
            r10 = 0
            java.lang.System.arraycopy(r8, r1, r9, r10, r2)
            int r3 = r24 + r25
            r11 = 1
            int r3 = r3 - r11
            int r4 = r2 + -1
            int r5 = r1 + r2
            int r5 = r5 - r11
            int r6 = r5 + -1
            int r7 = r3 + -1
            r3 = r8[r3]
            r8[r5] = r3
            int r3 = r25 + -1
            if (r3 != 0) goto L_0x002d
            int r5 = r2 + -1
            int r5 = r6 - r5
            java.lang.System.arraycopy(r9, r10, r8, r5, r2)
            return
        L_0x002d:
            if (r2 != r11) goto L_0x003d
            int r6 = r6 - r3
            int r7 = r7 - r3
            int r5 = r7 + 1
            int r10 = r6 + 1
            java.lang.System.arraycopy(r8, r5, r8, r10, r3)
            r5 = r9[r4]
            r8[r6] = r5
            return
        L_0x003d:
            java.util.Comparator<? super T> r12 = r0.c
            int r5 = r0.minGallop
        L_0x0041:
            r13 = 0
            r14 = 0
        L_0x0043:
            r15 = r9[r4]
            r10 = r8[r7]
            int r10 = r12.compare(r15, r10)
            if (r10 >= 0) goto L_0x0065
            int r10 = r6 + -1
            int r15 = r7 + -1
            r7 = r8[r7]
            r8[r6] = r7
            int r13 = r13 + 1
            r6 = 0
            int r3 = r3 + -1
            if (r3 != 0) goto L_0x0061
            r13 = r4
            r16 = r15
            goto L_0x0115
        L_0x0061:
            r14 = r6
            r6 = r10
            r7 = r15
            goto L_0x007c
        L_0x0065:
            int r10 = r6 + -1
            int r15 = r4 + -1
            r4 = r9[r4]
            r8[r6] = r4
            int r14 = r14 + 1
            r4 = 0
            int r2 = r2 + -1
            if (r2 != r11) goto L_0x0079
            r16 = r7
            r13 = r15
            goto L_0x0115
        L_0x0079:
            r13 = r4
            r6 = r10
            r4 = r15
        L_0x007c:
            r10 = r13 | r14
            if (r10 < r5) goto L_0x0167
            r17 = r2
            r10 = r3
            r19 = r5
            r15 = r6
            r16 = r7
            r18 = r14
            r14 = r13
            r13 = r4
        L_0x008c:
            r2 = r9[r13]
            int r6 = r10 + -1
            r3 = r8
            r4 = r24
            r5 = r10
            r7 = r12
            int r2 = gallopRight(r2, r3, r4, r5, r6, r7)
            int r14 = r10 - r2
            if (r14 == 0) goto L_0x00b9
            int r2 = r15 - r14
            int r7 = r16 - r14
            int r3 = r10 - r14
            int r4 = r7 + 1
            int r5 = r2 + 1
            java.lang.System.arraycopy(r8, r4, r8, r5, r14)
            if (r3 != 0) goto L_0x00b5
            r10 = r2
            r16 = r7
            r2 = r17
            r5 = r19
            goto L_0x0115
        L_0x00b5:
            r15 = r2
            r10 = r3
            r16 = r7
        L_0x00b9:
            int r20 = r15 + -1
            int r21 = r13 + -1
            r2 = r9[r13]
            r8[r15] = r2
            int r13 = r17 + -1
            if (r13 != r11) goto L_0x00ce
            r3 = r10
            r2 = r13
            r5 = r19
            r10 = r20
            r13 = r21
            goto L_0x0115
        L_0x00ce:
            r2 = r8[r16]
            r4 = 0
            int r6 = r13 + -1
            r3 = r9
            r5 = r13
            r7 = r12
            int r2 = gallopLeft(r2, r3, r4, r5, r6, r7)
            int r2 = r13 - r2
            if (r2 == 0) goto L_0x00fb
            int r3 = r20 - r2
            int r4 = r21 - r2
            int r5 = r13 - r2
            int r6 = r4 + 1
            int r7 = r3 + 1
            java.lang.System.arraycopy(r9, r6, r8, r7, r2)
            if (r5 > r11) goto L_0x00f7
            r13 = r4
            r2 = r5
            r5 = r19
            r22 = r10
            r10 = r3
            r3 = r22
            goto L_0x0115
        L_0x00f7:
            r13 = r4
            r17 = r5
            goto L_0x0101
        L_0x00fb:
            r17 = r13
            r3 = r20
            r13 = r21
        L_0x0101:
            int r15 = r3 + -1
            int r4 = r16 + -1
            r5 = r8[r16]
            r8[r3] = r5
            int r10 = r10 + -1
            if (r10 != 0) goto L_0x0140
            r16 = r4
            r3 = r10
            r10 = r15
            r2 = r17
            r5 = r19
        L_0x0115:
            if (r5 >= r11) goto L_0x0119
            r4 = 1
            goto L_0x011a
        L_0x0119:
            r4 = r5
        L_0x011a:
            r0.minGallop = r4
            if (r2 != r11) goto L_0x012d
            int r10 = r10 - r3
            int r16 = r16 - r3
            int r4 = r16 + 1
            int r6 = r10 + 1
            java.lang.System.arraycopy(r8, r4, r8, r6, r3)
            r4 = r9[r13]
            r8[r10] = r4
            goto L_0x0137
        L_0x012d:
            if (r2 == 0) goto L_0x0138
            int r4 = r2 + -1
            int r4 = r10 - r4
            r6 = 0
            java.lang.System.arraycopy(r9, r6, r8, r4, r2)
        L_0x0137:
            return
        L_0x0138:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "Comparison method violates its general contract!"
            r4.<init>(r6)
            throw r4
        L_0x0140:
            r6 = 0
            int r19 = r19 + -1
            r3 = 7
            if (r14 < r3) goto L_0x0148
            r5 = 1
            goto L_0x0149
        L_0x0148:
            r5 = 0
        L_0x0149:
            if (r2 < r3) goto L_0x014d
            r3 = 1
            goto L_0x014e
        L_0x014d:
            r3 = 0
        L_0x014e:
            r3 = r3 | r5
            if (r3 != 0) goto L_0x0161
            if (r19 >= 0) goto L_0x0155
            r19 = 0
        L_0x0155:
            int r5 = r19 + 2
            r7 = r4
            r3 = r10
            r4 = r13
            r6 = r15
            r2 = r17
            r10 = 0
            goto L_0x0041
        L_0x0161:
            r18 = r2
            r16 = r4
            goto L_0x008c
        L_0x0167:
            r10 = 0
            goto L_0x0043
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.TimSort.mergeHi(int, int, int, int):void");
    }

    private T[] ensureCapacity(int minCapacity) {
        int newSize;
        this.tmpCount = Math.max(this.tmpCount, minCapacity);
        if (this.tmp.length < minCapacity) {
            int newSize2 = minCapacity;
            int newSize3 = newSize2 | (newSize2 >> 1);
            int newSize4 = newSize3 | (newSize3 >> 2);
            int newSize5 = newSize4 | (newSize4 >> 4);
            int newSize6 = newSize5 | (newSize5 >> 8);
            int newSize7 = (newSize6 | (newSize6 >> 16)) + 1;
            if (newSize7 < 0) {
                newSize = minCapacity;
            } else {
                newSize = Math.min(newSize7, this.a.length >>> 1);
            }
            this.tmp = (Object[]) new Object[newSize];
        }
        return this.tmp;
    }

    private static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        } else if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        } else if (toIndex > arrayLen) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }
}
