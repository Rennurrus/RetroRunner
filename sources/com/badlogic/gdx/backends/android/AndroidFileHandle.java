package com.badlogic.gdx.backends.android;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.twi.game.BuildConfig;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class AndroidFileHandle extends FileHandle {
    private final AssetManager assets;

    AndroidFileHandle(AssetManager assets2, String fileName, Files.FileType type) {
        super(fileName.replace('\\', '/'), type);
        this.assets = assets2;
    }

    AndroidFileHandle(AssetManager assets2, File file, Files.FileType type) {
        super(file, type);
        this.assets = assets2;
    }

    public FileHandle child(String name) {
        String name2 = name.replace('\\', '/');
        if (this.file.getPath().length() == 0) {
            return new AndroidFileHandle(this.assets, new File(name2), this.type);
        }
        return new AndroidFileHandle(this.assets, new File(this.file, name2), this.type);
    }

    public FileHandle sibling(String name) {
        String name2 = name.replace('\\', '/');
        if (this.file.getPath().length() != 0) {
            return Gdx.files.getFileHandle(new File(this.file.getParent(), name2).getPath(), this.type);
        }
        throw new GdxRuntimeException("Cannot get the sibling of the root.");
    }

    public FileHandle parent() {
        File parent = this.file.getParentFile();
        if (parent == null) {
            if (this.type == Files.FileType.Absolute) {
                parent = new File("/");
            } else {
                parent = new File(BuildConfig.FLAVOR);
            }
        }
        return new AndroidFileHandle(this.assets, parent, this.type);
    }

    public InputStream read() {
        if (this.type != Files.FileType.Internal) {
            return super.read();
        }
        try {
            return this.assets.open(this.file.getPath());
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public ByteBuffer map(FileChannel.MapMode mode) {
        if (this.type != Files.FileType.Internal) {
            return super.map(mode);
        }
        FileInputStream input = null;
        try {
            AssetFileDescriptor fd = getAssetFileDescriptor();
            long startOffset = fd.getStartOffset();
            long declaredLength = fd.getDeclaredLength();
            input = new FileInputStream(fd.getFileDescriptor());
            ByteBuffer map = input.getChannel().map(mode, startOffset, declaredLength);
            map.order(ByteOrder.nativeOrder());
            StreamUtils.closeQuietly(input);
            return map;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error memory mapping file: " + this + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(input);
            throw th;
        }
    }

    public FileHandle[] list() {
        if (this.type != Files.FileType.Internal) {
            return super.list();
        }
        try {
            String[] relativePaths = this.assets.list(this.file.getPath());
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int n = handles.length;
            for (int i = 0; i < n; i++) {
                handles[i] = new AndroidFileHandle(this.assets, new File(this.file, relativePaths[i]), this.type);
            }
            return handles;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error listing children: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public FileHandle[] list(FileFilter filter) {
        if (this.type != Files.FileType.Internal) {
            return super.list(filter);
        }
        try {
            String[] relativePaths = this.assets.list(this.file.getPath());
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            int n = handles.length;
            for (int i = 0; i < n; i++) {
                FileHandle child = new AndroidFileHandle(this.assets, new File(this.file, relativePaths[i]), this.type);
                if (filter.accept(child.file())) {
                    handles[count] = child;
                    count++;
                }
            }
            if (count >= relativePaths.length) {
                return handles;
            }
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            return newHandles;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error listing children: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public FileHandle[] list(FilenameFilter filter) {
        if (this.type != Files.FileType.Internal) {
            return super.list(filter);
        }
        try {
            String[] relativePaths = this.assets.list(this.file.getPath());
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            int n = handles.length;
            for (int i = 0; i < n; i++) {
                String path = relativePaths[i];
                if (filter.accept(this.file, path)) {
                    handles[count] = new AndroidFileHandle(this.assets, new File(this.file, path), this.type);
                    count++;
                }
            }
            if (count >= relativePaths.length) {
                return handles;
            }
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            return newHandles;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error listing children: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public FileHandle[] list(String suffix) {
        if (this.type != Files.FileType.Internal) {
            return super.list(suffix);
        }
        try {
            String[] relativePaths = this.assets.list(this.file.getPath());
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            int n = handles.length;
            for (int i = 0; i < n; i++) {
                String path = relativePaths[i];
                if (path.endsWith(suffix)) {
                    handles[count] = new AndroidFileHandle(this.assets, new File(this.file, path), this.type);
                    count++;
                }
            }
            if (count >= relativePaths.length) {
                return handles;
            }
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            return newHandles;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error listing children: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public boolean isDirectory() {
        if (this.type != Files.FileType.Internal) {
            return super.isDirectory();
        }
        try {
            return this.assets.list(this.file.getPath()).length > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean exists() {
        if (this.type != Files.FileType.Internal) {
            return super.exists();
        }
        String fileName = this.file.getPath();
        try {
            this.assets.open(fileName).close();
            return true;
        } catch (Exception e) {
            try {
                if (this.assets.list(fileName).length > 0) {
                    return true;
                }
                return false;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public long length() {
        if (this.type == Files.FileType.Internal) {
            AssetFileDescriptor fileDescriptor = null;
            try {
                AssetFileDescriptor fileDescriptor2 = this.assets.openFd(this.file.getPath());
                long length = fileDescriptor2.getLength();
                if (fileDescriptor2 != null) {
                    try {
                        fileDescriptor2.close();
                    } catch (IOException e) {
                    }
                }
                return length;
            } catch (IOException e2) {
                if (fileDescriptor != null) {
                    try {
                        fileDescriptor.close();
                    } catch (IOException e3) {
                    }
                }
            } catch (Throwable th) {
                if (fileDescriptor != null) {
                    try {
                        fileDescriptor.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        }
        return super.length();
    }

    public long lastModified() {
        return super.lastModified();
    }

    public File file() {
        if (this.type == Files.FileType.Local) {
            return new File(Gdx.files.getLocalStoragePath(), this.file.getPath());
        }
        return super.file();
    }

    public AssetFileDescriptor getAssetFileDescriptor() throws IOException {
        AssetManager assetManager = this.assets;
        if (assetManager != null) {
            return assetManager.openFd(path());
        }
        return null;
    }
}
