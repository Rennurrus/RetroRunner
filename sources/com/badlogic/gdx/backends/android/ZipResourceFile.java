package com.badlogic.gdx.backends.android;

import android.content.res.AssetFileDescriptor;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.twi.game.BuildConfig;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceFile {
    static final boolean LOGV = false;
    static final String LOG_TAG = "zipro";
    static final int kCDECRC = 16;
    static final int kCDECommentLen = 32;
    static final int kCDECompLen = 20;
    static final int kCDEExtraLen = 30;
    static final int kCDELen = 46;
    static final int kCDELocalOffset = 42;
    static final int kCDEMethod = 10;
    static final int kCDEModWhen = 12;
    static final int kCDENameLen = 28;
    static final int kCDESignature = 33639248;
    static final int kCDEUncompLen = 24;
    static final int kCompressDeflated = 8;
    static final int kCompressStored = 0;
    static final int kEOCDFileOffset = 16;
    static final int kEOCDLen = 22;
    static final int kEOCDNumEntries = 8;
    static final int kEOCDSignature = 101010256;
    static final int kEOCDSize = 12;
    static final int kLFHExtraLen = 28;
    static final int kLFHLen = 30;
    static final int kLFHNameLen = 26;
    static final int kLFHSignature = 67324752;
    static final int kMaxCommentLen = 65535;
    static final int kMaxEOCDSearch = 65557;
    static final int kZipEntryAdj = 10000;
    private HashMap<String, ZipEntryRO> mHashMap = new HashMap<>();
    ByteBuffer mLEByteBuffer = ByteBuffer.allocate(4);
    public HashMap<File, ZipFile> mZipFiles = new HashMap<>();

    private static int swapEndian(int i) {
        return ((i & 255) << 24) + ((65280 & i) << 8) + ((16711680 & i) >>> 8) + ((i >>> 24) & 255);
    }

    private static int swapEndian(short i) {
        return ((i & 255) << 8) | ((65280 & i) >>> 8);
    }

    public static final class ZipEntryRO {
        public long mCRC32;
        public long mCompressedLength;
        public final File mFile;
        public final String mFileName;
        public long mLocalHdrOffset;
        public int mMethod;
        public long mOffset = -1;
        public long mUncompressedLength;
        public long mWhenModified;
        public final String mZipFileName;

        public ZipEntryRO(String zipFileName, File file, String fileName) {
            this.mFileName = fileName;
            this.mZipFileName = zipFileName;
            this.mFile = file;
        }

        public void setOffsetFromFile(RandomAccessFile f, ByteBuffer buf) throws IOException {
            long localHdrOffset = this.mLocalHdrOffset;
            try {
                f.seek(localHdrOffset);
                f.readFully(buf.array());
                if (buf.getInt(0) == ZipResourceFile.kLFHSignature) {
                    this.mOffset = 30 + localHdrOffset + ((long) (buf.getShort(26) & ZipResourceFile.kMaxCommentLen)) + ((long) (ZipResourceFile.kMaxCommentLen & buf.getShort(28)));
                    return;
                }
                Log.w(ZipResourceFile.LOG_TAG, "didn't find signature at start of lfh");
                throw new IOException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public long getOffset() {
            return this.mOffset;
        }

        public boolean isUncompressed() {
            return this.mMethod == 0;
        }

        public AssetFileDescriptor getAssetFileDescriptor() {
            if (this.mMethod != 0) {
                return null;
            }
            try {
                return new AssetFileDescriptor(ParcelFileDescriptor.open(this.mFile, 268435456), getOffset(), this.mUncompressedLength);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getZipFileName() {
            return this.mZipFileName;
        }

        public File getZipFile() {
            return this.mFile;
        }
    }

    public ZipResourceFile(String zipFileName) throws IOException {
        addPatchFile(zipFileName);
    }

    /* access modifiers changed from: package-private */
    public ZipEntryRO[] getEntriesAt(String path) {
        Vector<ZipEntryRO> zev = new Vector<>();
        Collection<ZipEntryRO> values = this.mHashMap.values();
        if (path == null) {
            path = BuildConfig.FLAVOR;
        }
        int length = path.length();
        for (ZipEntryRO ze : values) {
            if (ze.mFileName.startsWith(path) && -1 == ze.mFileName.indexOf(47, length)) {
                zev.add(ze);
            }
        }
        return (ZipEntryRO[]) zev.toArray(new ZipEntryRO[zev.size()]);
    }

    public ZipEntryRO[] getAllEntries() {
        Collection<ZipEntryRO> values = this.mHashMap.values();
        return (ZipEntryRO[]) values.toArray(new ZipEntryRO[values.size()]);
    }

    public AssetFileDescriptor getAssetFileDescriptor(String assetPath) {
        ZipEntryRO entry = this.mHashMap.get(assetPath);
        if (entry != null) {
            return entry.getAssetFileDescriptor();
        }
        return null;
    }

    public InputStream getInputStream(String assetPath) throws IOException {
        ZipEntryRO entry = this.mHashMap.get(assetPath);
        if (entry == null) {
            return null;
        }
        if (entry.isUncompressed()) {
            return entry.getAssetFileDescriptor().createInputStream();
        }
        ZipFile zf = this.mZipFiles.get(entry.getZipFile());
        if (zf == null) {
            zf = new ZipFile(entry.getZipFile(), 1);
            this.mZipFiles.put(entry.getZipFile(), zf);
        }
        ZipEntry zi = zf.getEntry(assetPath);
        if (zi != null) {
            return zf.getInputStream(zi);
        }
        return null;
    }

    private static int read4LE(RandomAccessFile f) throws EOFException, IOException {
        return swapEndian(f.readInt());
    }

    /* access modifiers changed from: package-private */
    public void addPatchFile(String zipFileName) throws IOException {
        String str = zipFileName;
        File file = new File(str);
        RandomAccessFile f = new RandomAccessFile(file, "r");
        long fileLength = f.length();
        if (fileLength >= 22) {
            long readAmount = 65557;
            if (65557 > fileLength) {
                readAmount = fileLength;
            }
            f.seek(0);
            int header = read4LE(f);
            if (header == kEOCDSignature) {
                File file2 = file;
                long j = fileLength;
                long j2 = readAmount;
                int i = header;
                Log.i(LOG_TAG, "Found Zip archive, but it looks empty");
                throw new IOException();
            } else if (header == kLFHSignature) {
                long searchStart = fileLength - readAmount;
                f.seek(searchStart);
                ByteBuffer bbuf = ByteBuffer.allocate((int) readAmount);
                byte[] buffer = bbuf.array();
                f.readFully(buffer);
                bbuf.order(ByteOrder.LITTLE_ENDIAN);
                int eocdIdx = buffer.length - 22;
                while (eocdIdx >= 0) {
                    if (buffer[eocdIdx] == 80) {
                        if (bbuf.getInt(eocdIdx) == kEOCDSignature) {
                            break;
                        }
                    }
                    eocdIdx--;
                }
                if (eocdIdx < 0) {
                    Log.d(LOG_TAG, "Zip: EOCD not found, " + str + " is not zip");
                }
                int numEntries = bbuf.getShort(eocdIdx + 8);
                long j3 = readAmount;
                long dirSize = ((long) bbuf.getInt(eocdIdx + 12)) & 4294967295L;
                long j4 = searchStart;
                long dirOffset = ((long) bbuf.getInt(eocdIdx + 16)) & 4294967295L;
                if (dirOffset + dirSize > fileLength) {
                    File file3 = file;
                    long j5 = fileLength;
                    int i2 = header;
                    int i3 = numEntries;
                    ByteBuffer byteBuffer = bbuf;
                    byte[] bArr = buffer;
                    int i4 = eocdIdx;
                    Log.w(LOG_TAG, "bad offsets (dir " + dirOffset + ", size " + dirSize + ", eocd " + eocdIdx + ")");
                    throw new IOException();
                } else if (numEntries != 0) {
                    long j6 = fileLength;
                    MappedByteBuffer directoryMap = f.getChannel().map(FileChannel.MapMode.READ_ONLY, dirOffset, dirSize);
                    directoryMap.order(ByteOrder.LITTLE_ENDIAN);
                    int i5 = header;
                    byte[] tempBuf = new byte[kMaxCommentLen];
                    ByteBuffer buf = ByteBuffer.allocate(30);
                    ByteBuffer byteBuffer2 = bbuf;
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    int i6 = 0;
                    byte[] bArr2 = buffer;
                    int currentOffset = 0;
                    while (i6 < numEntries) {
                        int numEntries2 = numEntries;
                        int eocdIdx2 = eocdIdx;
                        if (directoryMap.getInt(currentOffset) == kCDESignature) {
                            int fileNameLen = directoryMap.getShort(currentOffset + 28) & kMaxCommentLen;
                            long dirSize2 = dirSize;
                            int extraLen = directoryMap.getShort(currentOffset + 30) & kMaxCommentLen;
                            int commentLen = directoryMap.getShort(currentOffset + 32) & kMaxCommentLen;
                            directoryMap.position(currentOffset + 46);
                            directoryMap.get(tempBuf, 0, fileNameLen);
                            directoryMap.position(0);
                            long dirOffset2 = dirOffset;
                            String str2 = new String(tempBuf, 0, fileNameLen);
                            ZipEntryRO ze = new ZipEntryRO(str, file, str2);
                            ze.mMethod = directoryMap.getShort(currentOffset + 10) & kMaxCommentLen;
                            ze.mWhenModified = ((long) directoryMap.getInt(currentOffset + 12)) & 4294967295L;
                            ze.mCRC32 = directoryMap.getLong(currentOffset + 16) & 4294967295L;
                            ze.mCompressedLength = directoryMap.getLong(currentOffset + 20) & 4294967295L;
                            ze.mUncompressedLength = directoryMap.getLong(currentOffset + 24) & 4294967295L;
                            ze.mLocalHdrOffset = ((long) directoryMap.getInt(currentOffset + 42)) & 4294967295L;
                            buf.clear();
                            ze.setOffsetFromFile(f, buf);
                            this.mHashMap.put(str2, ze);
                            currentOffset += fileNameLen + 46 + extraLen + commentLen;
                            i6++;
                            str = zipFileName;
                            numEntries = numEntries2;
                            eocdIdx = eocdIdx2;
                            dirSize = dirSize2;
                            dirOffset = dirOffset2;
                            file = file;
                        } else {
                            File file4 = file;
                            long j7 = dirSize;
                            long j8 = dirOffset;
                            Log.w(LOG_TAG, "Missed a central dir sig (at " + currentOffset + ")");
                            throw new IOException();
                        }
                    }
                    File file5 = file;
                    long j9 = dirSize;
                    int i7 = numEntries;
                    long j10 = dirOffset;
                    int i8 = eocdIdx;
                } else {
                    File file6 = file;
                    long j11 = fileLength;
                    long j12 = dirSize;
                    int i9 = header;
                    int i10 = numEntries;
                    long j13 = dirOffset;
                    ByteBuffer byteBuffer3 = bbuf;
                    byte[] bArr3 = buffer;
                    int i11 = eocdIdx;
                    Log.w(LOG_TAG, "empty archive?");
                    throw new IOException();
                }
            } else {
                File file7 = file;
                long j14 = fileLength;
                long j15 = readAmount;
                int i12 = header;
                Log.v(LOG_TAG, "Not a Zip archive");
                throw new IOException();
            }
        } else {
            File file8 = file;
            long j16 = fileLength;
            f.close();
            throw new IOException();
        }
    }
}
