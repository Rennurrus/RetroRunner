package com.badlogic.gdx.backends.android;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.ZipResourceFile;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.twi.game.BuildConfig;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

public class AndroidZipFileHandle extends AndroidFileHandle {
    private ZipResourceFile expansionFile;
    private long fdLength;
    private boolean hasAssetFd;
    private String path;

    public AndroidZipFileHandle(String fileName) {
        super((AssetManager) null, fileName, Files.FileType.Internal);
        initialize();
    }

    public AndroidZipFileHandle(File file, Files.FileType type) {
        super((AssetManager) null, file, type);
        initialize();
    }

    private void initialize() {
        this.path = this.file.getPath().replace('\\', '/');
        this.expansionFile = ((AndroidFiles) Gdx.files).getExpansionFile();
        AssetFileDescriptor assetFd = this.expansionFile.getAssetFileDescriptor(getPath());
        if (assetFd != null) {
            this.hasAssetFd = true;
            this.fdLength = assetFd.getLength();
            try {
                assetFd.close();
            } catch (IOException e) {
            }
        } else {
            this.hasAssetFd = false;
        }
        if (isDirectory()) {
            this.path += "/";
        }
    }

    public AssetFileDescriptor getAssetFileDescriptor() throws IOException {
        return this.expansionFile.getAssetFileDescriptor(getPath());
    }

    private String getPath() {
        return this.path;
    }

    public InputStream read() {
        try {
            return this.expansionFile.getInputStream(getPath());
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this.file + " (ZipResourceFile)", ex);
        }
    }

    public FileHandle child(String name) {
        if (this.file.getPath().length() == 0) {
            return new AndroidZipFileHandle(new File(name), this.type);
        }
        return new AndroidZipFileHandle(new File(this.file, name), this.type);
    }

    public FileHandle sibling(String name) {
        if (this.file.getPath().length() != 0) {
            return Gdx.files.getFileHandle(new File(this.file.getParent(), name).getPath(), this.type);
        }
        throw new GdxRuntimeException("Cannot get the sibling of the root.");
    }

    public FileHandle parent() {
        File parent = this.file.getParentFile();
        if (parent == null) {
            parent = new File(BuildConfig.FLAVOR);
        }
        return new AndroidZipFileHandle(parent.getPath());
    }

    public FileHandle[] list() {
        ZipResourceFile.ZipEntryRO[] zipEntries = this.expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[(zipEntries.length - 1)];
        int count = 0;
        int n = zipEntries.length;
        for (int i = 0; i < n; i++) {
            if (zipEntries[i].mFileName.length() != getPath().length()) {
                handles[count] = new AndroidZipFileHandle(zipEntries[i].mFileName);
                count++;
            }
        }
        return handles;
    }

    public FileHandle[] list(FileFilter filter) {
        ZipResourceFile.ZipEntryRO[] zipEntries = this.expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[(zipEntries.length - 1)];
        int count = 0;
        int n = zipEntries.length;
        for (int i = 0; i < n; i++) {
            if (zipEntries[i].mFileName.length() != getPath().length()) {
                FileHandle child = new AndroidZipFileHandle(zipEntries[i].mFileName);
                if (filter.accept(child.file())) {
                    handles[count] = child;
                    count++;
                }
            }
        }
        if (count >= handles.length) {
            return handles;
        }
        FileHandle[] newHandles = new FileHandle[count];
        System.arraycopy(handles, 0, newHandles, 0, count);
        return newHandles;
    }

    public FileHandle[] list(FilenameFilter filter) {
        ZipResourceFile.ZipEntryRO[] zipEntries = this.expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[(zipEntries.length - 1)];
        int count = 0;
        int n = zipEntries.length;
        for (int i = 0; i < n; i++) {
            if (zipEntries[i].mFileName.length() != getPath().length()) {
                String path2 = zipEntries[i].mFileName;
                if (filter.accept(this.file, path2)) {
                    handles[count] = new AndroidZipFileHandle(path2);
                    count++;
                }
            }
        }
        if (count >= handles.length) {
            return handles;
        }
        FileHandle[] newHandles = new FileHandle[count];
        System.arraycopy(handles, 0, newHandles, 0, count);
        return newHandles;
    }

    public FileHandle[] list(String suffix) {
        ZipResourceFile.ZipEntryRO[] zipEntries = this.expansionFile.getEntriesAt(getPath());
        FileHandle[] handles = new FileHandle[(zipEntries.length - 1)];
        int count = 0;
        int n = zipEntries.length;
        for (int i = 0; i < n; i++) {
            if (zipEntries[i].mFileName.length() != getPath().length()) {
                String path2 = zipEntries[i].mFileName;
                if (path2.endsWith(suffix)) {
                    handles[count] = new AndroidZipFileHandle(path2);
                    count++;
                }
            }
        }
        if (count >= handles.length) {
            return handles;
        }
        FileHandle[] newHandles = new FileHandle[count];
        System.arraycopy(handles, 0, newHandles, 0, count);
        return newHandles;
    }

    public boolean isDirectory() {
        return !this.hasAssetFd;
    }

    public long length() {
        if (this.hasAssetFd) {
            return this.fdLength;
        }
        return 0;
    }

    public boolean exists() {
        return this.hasAssetFd || this.expansionFile.getEntriesAt(getPath()).length != 0;
    }
}
