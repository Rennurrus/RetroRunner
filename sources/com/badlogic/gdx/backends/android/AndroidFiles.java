package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.IOException;

public class AndroidFiles implements Files {
    protected final AssetManager assets;
    private ZipResourceFile expansionFile = null;
    protected final String localpath;
    protected final String sdcard = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/");

    public AndroidFiles(AssetManager assets2) {
        this.assets = assets2;
        this.localpath = this.sdcard;
    }

    public AndroidFiles(AssetManager assets2, String localpath2) {
        String str;
        this.assets = assets2;
        if (localpath2.endsWith("/")) {
            str = localpath2;
        } else {
            str = localpath2 + "/";
        }
        this.localpath = str;
    }

    public FileHandle getFileHandle(String path, Files.FileType type) {
        FileHandle handle = new AndroidFileHandle(type == Files.FileType.Internal ? this.assets : null, path, type);
        if (this.expansionFile == null || type != Files.FileType.Internal) {
            return handle;
        }
        return getZipFileHandleIfExists(handle, path);
    }

    private FileHandle getZipFileHandleIfExists(FileHandle handle, String path) {
        try {
            this.assets.open(path).close();
            return handle;
        } catch (Exception e) {
            FileHandle zipHandle = new AndroidZipFileHandle(path);
            if (zipHandle.isDirectory() && !zipHandle.exists()) {
                return handle;
            }
            return zipHandle;
        }
    }

    public FileHandle classpath(String path) {
        return new AndroidFileHandle((AssetManager) null, path, Files.FileType.Classpath);
    }

    public FileHandle internal(String path) {
        FileHandle handle = new AndroidFileHandle(this.assets, path, Files.FileType.Internal);
        if (this.expansionFile != null) {
            return getZipFileHandleIfExists(handle, path);
        }
        return handle;
    }

    public FileHandle external(String path) {
        return new AndroidFileHandle((AssetManager) null, path, Files.FileType.External);
    }

    public FileHandle absolute(String path) {
        return new AndroidFileHandle((AssetManager) null, path, Files.FileType.Absolute);
    }

    public FileHandle local(String path) {
        return new AndroidFileHandle((AssetManager) null, path, Files.FileType.Local);
    }

    public String getExternalStoragePath() {
        return this.sdcard;
    }

    public boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public String getLocalStoragePath() {
        return this.localpath;
    }

    public boolean isLocalStorageAvailable() {
        return true;
    }

    public boolean setAPKExpansion(int mainVersion, int patchVersion) {
        Context context;
        try {
            if (Gdx.app instanceof Activity) {
                context = ((Activity) Gdx.app).getBaseContext();
            } else if (Gdx.app instanceof Fragment) {
                context = ((Fragment) Gdx.app).getActivity().getBaseContext();
            } else {
                throw new GdxRuntimeException("APK expansion not supported for application type");
            }
            this.expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, mainVersion, patchVersion);
            return this.expansionFile != null;
        } catch (IOException e) {
            throw new GdxRuntimeException("APK expansion main version " + mainVersion + " or patch version " + patchVersion + " couldn't be opened!");
        }
    }

    public ZipResourceFile getExpansionFile() {
        return this.expansionFile;
    }
}
