package com.badlogic.gdx.utils;

class ComparableTimSort {
    private static final boolean DEBUG = false;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private static final int MIN_GALLOP = 7;
    private static final int MIN_MERGE = 32;
    private Object[] a;
    private int minGallop;
    private final int[] runBase;
    private final int[] runLen;
    private int stackSize;
    private Object[] tmp;
    private int tmpCount;

    ComparableTimSort() {
        this.minGallop = 7;
        this.stackSize = 0;
        this.tmp = new Object[256];
        this.runBase = new int[40];
        this.runLen = new int[40];
    }

    public void doSort(Object[] a2, int lo, int hi) {
        this.stackSize = 0;
        rangeCheck(a2.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < 32) {
                binarySort(a2, lo, hi, lo + countRunAndMakeAscending(a2, lo, hi));
                return;
            }
            this.a = a2;
            this.tmpCount = 0;
            int minRun = minRunLength(nRemaining);
            do {
                int runLen2 = countRunAndMakeAscending(a2, lo, hi);
                if (runLen2 < minRun) {
                    int force = nRemaining <= minRun ? nRemaining : minRun;
                    binarySort(a2, lo, lo + force, lo + runLen2);
                    runLen2 = force;
                }
                pushRun(lo, runLen2);
                mergeCollapse();
                lo += runLen2;
                nRemaining -= runLen2;
            } while (nRemaining != 0);
            mergeForceCollapse();
            this.a = null;
            Object[] tmp2 = this.tmp;
            int n = this.tmpCount;
            for (int i = 0; i < n; i++) {
                tmp2[i] = null;
            }
        }
    }

    private ComparableTimSort(Object[] a2) {
        this.minGallop = 7;
        this.stackSize = 0;
        this.a = a2;
        int len = a2.length;
        this.tmp = new Object[(len < 512 ? len >>> 1 : 256)];
        int stackLen = len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40;
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    static void sort(Object[] a2) {
        sort(a2, 0, a2.length);
    }

    static void sort(Object[] a2, int lo, int hi) {
        rangeCheck(a2.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < 32) {
                binarySort(a2, lo, hi, lo + countRunAndMakeAscending(a2, lo, hi));
                return;
            }
            ComparableTimSort ts = new ComparableTimSort(a2);
            int minRun = minRunLength(nRemaining);
            do {
                int runLen2 = countRunAndMakeAscending(a2, lo, hi);
                if (runLen2 < minRun) {
                    int force = nRemaining <= minRun ? nRemaining : minRun;
                    binarySort(a2, lo, lo + force, lo + runLen2);
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

    private static void binarySort(Object[] a2, int lo, int hi, int start) {
        if (start == lo) {
            start++;
        }
        while (start < hi) {
            Comparable<Object> pivot = a2[start];
            int left = lo;
            int right = start;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (pivot.compareTo(a2[mid]) < 0) {
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

    private static int countRunAndMakeAscending(Object[] a2, int lo, int hi) {
        int runHi = lo + 1;
        if (runHi == hi) {
            return 1;
        }
        int runHi2 = runHi + 1;
        if (a2[runHi].compareTo(a2[lo]) < 0) {
            while (runHi2 < hi && a2[runHi2].compareTo(a2[runHi2 - 1]) < 0) {
                runHi2++;
            }
            reverseRange(a2, lo, runHi2);
        } else {
            while (runHi2 < hi && a2[runHi2].compareTo(a2[runHi2 - 1]) >= 0) {
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

    private void mergeCollapse() {
        while (true) {
            int i = this.stackSize;
            if (i > 1) {
                int n = i - 2;
                if (n > 0) {
                    int[] iArr = this.runLen;
                    if (iArr[n - 1] <= iArr[n] + iArr[n + 1]) {
                        if (iArr[n - 1] < iArr[n + 1]) {
                            n--;
                        }
                        mergeAt(n);
                    }
                }
                int[] iArr2 = this.runLen;
                if (iArr2[n] <= iArr2[n + 1]) {
                    mergeAt(n);
                } else {
                    return;
                }
            } else {
                return;
            }
        }
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
        int[] iArr = this.runBase;
        int base1 = iArr[i];
        int[] iArr2 = this.runLen;
        int len1 = iArr2[i];
        int base2 = iArr[i + 1];
        int len2 = iArr2[i + 1];
        iArr2[i] = len1 + len2;
        if (i == this.stackSize - 3) {
            iArr[i + 1] = iArr[i + 2];
            iArr2[i + 1] = iArr2[i + 2];
        }
        this.stackSize--;
        Object[] objArr = this.a;
        int k = gallopRight((Comparable) objArr[base2], objArr, base1, len1, 0);
        int base12 = base1 + k;
        int len12 = len1 - k;
        if (len12 != 0) {
            Object[] objArr2 = this.a;
            int len22 = gallopLeft((Comparable) objArr2[(base12 + len12) - 1], objArr2, base2, len2, len2 - 1);
            if (len22 != 0) {
                if (len12 <= len22) {
                    mergeLo(base12, len12, base2, len22);
                } else {
                    mergeHi(base12, len12, base2, len22);
                }
            }
        }
    }

    private static int gallopLeft(Comparable<Object> key, Object[] a2, int base, int len, int hint) {
        int ofs;
        int lastOfs;
        int lastOfs2 = 0;
        int ofs2 = 1;
        if (key.compareTo(a2[base + hint]) > 0) {
            int maxOfs = len - hint;
            while (ofs2 < maxOfs && key.compareTo(a2[base + hint + ofs2]) > 0) {
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
            while (ofs2 < maxOfs2 && key.compareTo(a2[(base + hint) - ofs2]) <= 0) {
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
            if (key.compareTo(a2[base + m]) > 0) {
                lastOfs3 = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static int gallopRight(Comparable<Object> key, Object[] a2, int base, int len, int hint) {
        int lastOfs;
        int ofs;
        int ofs2 = 1;
        int lastOfs2 = 0;
        if (key.compareTo(a2[base + hint]) < 0) {
            int maxOfs = hint + 1;
            while (ofs2 < maxOfs && key.compareTo(a2[(base + hint) - ofs2]) < 0) {
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
            while (ofs2 < maxOfs2 && key.compareTo(a2[base + hint + ofs2]) >= 0) {
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
            if (key.compareTo(a2[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs3 = m + 1;
            }
        }
        return ofs;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0070, code lost:
        r12 = gallopRight((java.lang.Comparable) r2[r10], r3, r6, r1, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0078, code lost:
        if (r12 == 0) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007a, code lost:
        java.lang.System.arraycopy(r3, r6, r2, r9, r12);
        r14 = r9 + r12;
        r6 = r6 + r12;
        r1 = r1 - r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0081, code lost:
        if (r1 > 1) goto L_0x0085;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0084, code lost:
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0085, code lost:
        r9 = r14 + 1;
        r15 = r10 + 1;
        r2[r14] = r2[r10];
        r7 = r7 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x008f, code lost:
        if (r7 != 0) goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0091, code lost:
        r14 = r9;
        r10 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0094, code lost:
        r13 = gallopLeft((java.lang.Comparable) r3[r6], r2, r15, r7, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x009c, code lost:
        if (r13 == 0) goto L_0x00a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009e, code lost:
        java.lang.System.arraycopy(r2, r15, r2, r9, r13);
        r14 = r9 + r13;
        r10 = r15 + r13;
        r7 = r7 - r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a6, code lost:
        if (r7 != 0) goto L_0x00ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a9, code lost:
        r14 = r9;
        r10 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ab, code lost:
        r9 = r14 + 1;
        r15 = r6 + 1;
        r2[r14] = r3[r6];
        r1 = r1 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b5, code lost:
        if (r1 != 1) goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00b7, code lost:
        r14 = r9;
        r6 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00da, code lost:
        r11 = r11 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00dd, code lost:
        if (r12 < 7) goto L_0x00e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00df, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00e1, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00e2, code lost:
        if (r13 < 7) goto L_0x00e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00e4, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00e6, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00e8, code lost:
        if ((r6 | r14) != false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00ea, code lost:
        if (r11 >= 0) goto L_0x00ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00ec, code lost:
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00f3, code lost:
        r6 = r15;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeLo(int r17, int r18, int r19, int r20) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            java.lang.Object[] r2 = r0.a
            java.lang.Object[] r3 = r0.ensureCapacity(r1)
            r4 = 0
            r5 = r17
            java.lang.System.arraycopy(r2, r5, r3, r4, r1)
            r6 = 0
            r7 = r19
            r8 = r17
            int r9 = r8 + 1
            int r10 = r7 + 1
            r7 = r2[r7]
            r2[r8] = r7
            int r7 = r20 + -1
            if (r7 != 0) goto L_0x0025
            java.lang.System.arraycopy(r3, r6, r2, r9, r1)
            return
        L_0x0025:
            r8 = 1
            if (r1 != r8) goto L_0x0032
            java.lang.System.arraycopy(r2, r10, r2, r9, r7)
            int r4 = r9 + r7
            r8 = r3[r6]
            r2[r4] = r8
            return
        L_0x0032:
            int r11 = r0.minGallop
        L_0x0034:
            r12 = 0
            r13 = 0
        L_0x0036:
            r14 = r2[r10]
            java.lang.Comparable r14 = (java.lang.Comparable) r14
            r15 = r3[r6]
            int r14 = r14.compareTo(r15)
            if (r14 >= 0) goto L_0x0058
            int r14 = r9 + 1
            int r15 = r10 + 1
            r10 = r2[r10]
            r2[r9] = r10
            int r13 = r13 + 1
            r9 = 0
            int r7 = r7 + -1
            if (r7 != 0) goto L_0x0054
            r10 = r15
            goto L_0x00b9
        L_0x0054:
            r12 = r9
            r9 = r14
            r10 = r15
            goto L_0x006c
        L_0x0058:
            int r14 = r9 + 1
            int r15 = r6 + 1
            r6 = r3[r6]
            r2[r9] = r6
            int r12 = r12 + 1
            r6 = 0
            int r1 = r1 + -1
            if (r1 != r8) goto L_0x0069
            r6 = r15
            goto L_0x00b9
        L_0x0069:
            r13 = r6
            r9 = r14
            r6 = r15
        L_0x006c:
            r14 = r12 | r13
            if (r14 < r11) goto L_0x00f6
        L_0x0070:
            r14 = r2[r10]
            java.lang.Comparable r14 = (java.lang.Comparable) r14
            int r12 = gallopRight(r14, r3, r6, r1, r4)
            if (r12 == 0) goto L_0x0084
            java.lang.System.arraycopy(r3, r6, r2, r9, r12)
            int r14 = r9 + r12
            int r6 = r6 + r12
            int r1 = r1 - r12
            if (r1 > r8) goto L_0x0085
            goto L_0x00b9
        L_0x0084:
            r14 = r9
        L_0x0085:
            int r9 = r14 + 1
            int r15 = r10 + 1
            r10 = r2[r10]
            r2[r14] = r10
            int r7 = r7 + -1
            if (r7 != 0) goto L_0x0094
            r14 = r9
            r10 = r15
            goto L_0x00b9
        L_0x0094:
            r10 = r3[r6]
            java.lang.Comparable r10 = (java.lang.Comparable) r10
            int r13 = gallopLeft(r10, r2, r15, r7, r4)
            if (r13 == 0) goto L_0x00a9
            java.lang.System.arraycopy(r2, r15, r2, r9, r13)
            int r14 = r9 + r13
            int r10 = r15 + r13
            int r7 = r7 - r13
            if (r7 != 0) goto L_0x00ab
            goto L_0x00b9
        L_0x00a9:
            r14 = r9
            r10 = r15
        L_0x00ab:
            int r9 = r14 + 1
            int r15 = r6 + 1
            r6 = r3[r6]
            r2[r14] = r6
            int r1 = r1 + -1
            if (r1 != r8) goto L_0x00da
            r14 = r9
            r6 = r15
        L_0x00b9:
            if (r11 >= r8) goto L_0x00bd
            r4 = 1
            goto L_0x00be
        L_0x00bd:
            r4 = r11
        L_0x00be:
            r0.minGallop = r4
            if (r1 != r8) goto L_0x00cc
            java.lang.System.arraycopy(r2, r10, r2, r14, r7)
            int r4 = r14 + r7
            r8 = r3[r6]
            r2[r4] = r8
            goto L_0x00d1
        L_0x00cc:
            if (r1 == 0) goto L_0x00d2
            java.lang.System.arraycopy(r3, r6, r2, r14, r1)
        L_0x00d1:
            return
        L_0x00d2:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r8 = "Comparison method violates its general contract!"
            r4.<init>(r8)
            throw r4
        L_0x00da:
            int r11 = r11 + -1
            r6 = 7
            if (r12 < r6) goto L_0x00e1
            r14 = 1
            goto L_0x00e2
        L_0x00e1:
            r14 = 0
        L_0x00e2:
            if (r13 < r6) goto L_0x00e6
            r6 = 1
            goto L_0x00e7
        L_0x00e6:
            r6 = 0
        L_0x00e7:
            r6 = r6 | r14
            if (r6 != 0) goto L_0x00f3
            if (r11 >= 0) goto L_0x00ed
            r11 = 0
        L_0x00ed:
            int r11 = r11 + 2
            r6 = r15
            goto L_0x0034
        L_0x00f3:
            r6 = r15
            goto L_0x0070
        L_0x00f6:
            goto L_0x0036
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.ComparableTimSort.mergeLo(int, int, int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0080, code lost:
        r13 = r7 - gallopRight((java.lang.Comparable) r5[r9], r4, r1, r7, r7 - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x008c, code lost:
        if (r13 == 0) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x008e, code lost:
        r6 = r11 - r13;
        r12 = r12 - r13;
        r7 = r7 - r13;
        java.lang.System.arraycopy(r4, r12 + 1, r4, r6 + 1, r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0099, code lost:
        if (r7 != 0) goto L_0x009d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x009b, code lost:
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009d, code lost:
        r11 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x009e, code lost:
        r6 = r11 - 1;
        r15 = r9 - 1;
        r4[r11] = r5[r9];
        r3 = r3 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a8, code lost:
        if (r3 != r8) goto L_0x00ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00aa, code lost:
        r8 = r10;
        r9 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ad, code lost:
        r14 = r3 - gallopLeft((java.lang.Comparable) r4[r12], r5, 0, r3, r3 - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ba, code lost:
        if (r14 == 0) goto L_0x00cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00bc, code lost:
        r6 = r6 - r14;
        r9 = r15 - r14;
        r3 = r3 - r14;
        java.lang.System.arraycopy(r5, r9 + 1, r4, r6 + 1, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c8, code lost:
        if (r3 > 1) goto L_0x00cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00ca, code lost:
        r8 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00cc, code lost:
        r9 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00cd, code lost:
        r11 = r6 - 1;
        r8 = r12 - 1;
        r4[r6] = r4[r12];
        r7 = r7 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00d7, code lost:
        if (r7 != 0) goto L_0x0107;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00d9, code lost:
        r12 = r8;
        r8 = r10;
        r6 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0107, code lost:
        r10 = r10 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x010c, code lost:
        if (r13 < 7) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x010e, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0111, code lost:
        r16 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0113, code lost:
        if (r14 < 7) goto L_0x0117;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0115, code lost:
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0117, code lost:
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x011a, code lost:
        if ((r16 | r12) != false) goto L_0x0126;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x011c, code lost:
        if (r10 >= 0) goto L_0x011f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x011e, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0126, code lost:
        r12 = r8;
        r8 = 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeHi(int r18, int r19, int r20, int r21) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r20
            r3 = r21
            java.lang.Object[] r4 = r0.a
            java.lang.Object[] r5 = r0.ensureCapacity(r3)
            r6 = 0
            java.lang.System.arraycopy(r4, r2, r5, r6, r3)
            int r7 = r1 + r19
            r8 = 1
            int r7 = r7 - r8
            int r9 = r3 + -1
            int r10 = r2 + r3
            int r10 = r10 - r8
            int r11 = r10 + -1
            int r12 = r7 + -1
            r7 = r4[r7]
            r4[r10] = r7
            int r7 = r19 + -1
            if (r7 != 0) goto L_0x002f
            int r8 = r3 + -1
            int r8 = r11 - r8
            java.lang.System.arraycopy(r5, r6, r4, r8, r3)
            return
        L_0x002f:
            if (r3 != r8) goto L_0x003f
            int r11 = r11 - r7
            int r12 = r12 - r7
            int r6 = r12 + 1
            int r8 = r11 + 1
            java.lang.System.arraycopy(r4, r6, r4, r8, r7)
            r6 = r5[r9]
            r4[r11] = r6
            return
        L_0x003f:
            int r10 = r0.minGallop
        L_0x0041:
            r13 = 0
            r14 = 0
        L_0x0043:
            r15 = r5[r9]
            java.lang.Comparable r15 = (java.lang.Comparable) r15
            r6 = r4[r12]
            int r6 = r15.compareTo(r6)
            if (r6 >= 0) goto L_0x0066
            int r6 = r11 + -1
            int r15 = r12 + -1
            r12 = r4[r12]
            r4[r11] = r12
            int r13 = r13 + 1
            r11 = 0
            int r7 = r7 + -1
            if (r7 != 0) goto L_0x0062
            r8 = r10
            r12 = r15
            goto L_0x00dc
        L_0x0062:
            r14 = r11
            r12 = r15
            r11 = r6
            goto L_0x007c
        L_0x0066:
            int r6 = r11 + -1
            int r15 = r9 + -1
            r9 = r5[r9]
            r4[r11] = r9
            int r14 = r14 + 1
            r9 = 0
            int r3 = r3 + -1
            if (r3 != r8) goto L_0x0079
            r8 = r10
            r9 = r15
            goto L_0x00dc
        L_0x0079:
            r11 = r6
            r13 = r9
            r9 = r15
        L_0x007c:
            r6 = r13 | r14
            if (r6 < r10) goto L_0x012a
        L_0x0080:
            r6 = r5[r9]
            java.lang.Comparable r6 = (java.lang.Comparable) r6
            int r15 = r7 + -1
            int r6 = gallopRight(r6, r4, r1, r7, r15)
            int r13 = r7 - r6
            if (r13 == 0) goto L_0x009e
            int r6 = r11 - r13
            int r12 = r12 - r13
            int r7 = r7 - r13
            int r11 = r12 + 1
            int r15 = r6 + 1
            java.lang.System.arraycopy(r4, r11, r4, r15, r13)
            if (r7 != 0) goto L_0x009d
            r8 = r10
            goto L_0x00dc
        L_0x009d:
            r11 = r6
        L_0x009e:
            int r6 = r11 + -1
            int r15 = r9 + -1
            r9 = r5[r9]
            r4[r11] = r9
            int r3 = r3 + -1
            if (r3 != r8) goto L_0x00ad
            r8 = r10
            r9 = r15
            goto L_0x00dc
        L_0x00ad:
            r9 = r4[r12]
            java.lang.Comparable r9 = (java.lang.Comparable) r9
            int r11 = r3 + -1
            r8 = 0
            int r9 = gallopLeft(r9, r5, r8, r3, r11)
            int r14 = r3 - r9
            if (r14 == 0) goto L_0x00cc
            int r6 = r6 - r14
            int r9 = r15 - r14
            int r3 = r3 - r14
            int r8 = r9 + 1
            int r11 = r6 + 1
            java.lang.System.arraycopy(r5, r8, r4, r11, r14)
            r8 = 1
            if (r3 > r8) goto L_0x00cd
            r8 = r10
            goto L_0x00dc
        L_0x00cc:
            r9 = r15
        L_0x00cd:
            int r11 = r6 + -1
            int r8 = r12 + -1
            r12 = r4[r12]
            r4[r6] = r12
            int r7 = r7 + -1
            if (r7 != 0) goto L_0x0107
            r12 = r8
            r8 = r10
            r6 = r11
        L_0x00dc:
            r15 = 1
            if (r8 >= r15) goto L_0x00e1
            r10 = 1
            goto L_0x00e2
        L_0x00e1:
            r10 = r8
        L_0x00e2:
            r0.minGallop = r10
            if (r3 != r15) goto L_0x00f4
            int r6 = r6 - r7
            int r12 = r12 - r7
            int r10 = r12 + 1
            int r11 = r6 + 1
            java.lang.System.arraycopy(r4, r10, r4, r11, r7)
            r10 = r5[r9]
            r4[r6] = r10
            goto L_0x00fe
        L_0x00f4:
            if (r3 == 0) goto L_0x00ff
            int r10 = r3 + -1
            int r10 = r6 - r10
            r11 = 0
            java.lang.System.arraycopy(r5, r11, r4, r10, r3)
        L_0x00fe:
            return
        L_0x00ff:
            java.lang.IllegalArgumentException r10 = new java.lang.IllegalArgumentException
            java.lang.String r11 = "Comparison method violates its general contract!"
            r10.<init>(r11)
            throw r10
        L_0x0107:
            r6 = 0
            r15 = 1
            int r10 = r10 + -1
            r12 = 7
            if (r13 < r12) goto L_0x0111
            r16 = 1
            goto L_0x0113
        L_0x0111:
            r16 = 0
        L_0x0113:
            if (r14 < r12) goto L_0x0117
            r12 = 1
            goto L_0x0118
        L_0x0117:
            r12 = 0
        L_0x0118:
            r12 = r16 | r12
            if (r12 != 0) goto L_0x0126
            if (r10 >= 0) goto L_0x011f
            r10 = 0
        L_0x011f:
            int r10 = r10 + 2
            r12 = r8
            r8 = 1
            goto L_0x0041
        L_0x0126:
            r12 = r8
            r8 = 1
            goto L_0x0080
        L_0x012a:
            r6 = 0
            r15 = 1
            r8 = 1
            goto L_0x0043
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.ComparableTimSort.mergeHi(int, int, int, int):void");
    }

    private Object[] ensureCapacity(int minCapacity) {
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
            this.tmp = new Object[newSize];
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
