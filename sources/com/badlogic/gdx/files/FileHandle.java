package com.badlogic.gdx.files;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.twi.game.BuildConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class FileHandle {
    protected File file;
    protected Files.FileType type;

    protected FileHandle() {
    }

    public FileHandle(String fileName) {
        this.file = new File(fileName);
        this.type = Files.FileType.Absolute;
    }

    public FileHandle(File file2) {
        this.file = file2;
        this.type = Files.FileType.Absolute;
    }

    protected FileHandle(String fileName, Files.FileType type2) {
        this.type = type2;
        this.file = new File(fileName);
    }

    protected FileHandle(File file2, Files.FileType type2) {
        this.file = file2;
        this.type = type2;
    }

    public String path() {
        return this.file.getPath().replace('\\', '/');
    }

    public String name() {
        return this.file.getName();
    }

    public String extension() {
        String name = this.file.getName();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            return BuildConfig.FLAVOR;
        }
        return name.substring(dotIndex + 1);
    }

    public String nameWithoutExtension() {
        String name = this.file.getName();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            return name;
        }
        return name.substring(0, dotIndex);
    }

    public String pathWithoutExtension() {
        String path = this.file.getPath().replace('\\', '/');
        int dotIndex = path.lastIndexOf(46);
        if (dotIndex == -1) {
            return path;
        }
        return path.substring(0, dotIndex);
    }

    public Files.FileType type() {
        return this.type;
    }

    public File file() {
        if (this.type == Files.FileType.External) {
            return new File(Gdx.files.getExternalStoragePath(), this.file.getPath());
        }
        return this.file;
    }

    public InputStream read() {
        if (this.type == Files.FileType.Classpath || ((this.type == Files.FileType.Internal && !file().exists()) || (this.type == Files.FileType.Local && !file().exists()))) {
            InputStream input = FileHandle.class.getResourceAsStream("/" + this.file.getPath().replace('\\', '/'));
            if (input != null) {
                return input;
            }
            throw new GdxRuntimeException("File not found: " + this.file + " (" + this.type + ")");
        }
        try {
            return new FileInputStream(file());
        } catch (Exception ex) {
            if (file().isDirectory()) {
                throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
            }
            throw new GdxRuntimeException("Error reading file: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    public Reader reader() {
        return new InputStreamReader(read());
    }

    public Reader reader(String charset) {
        InputStream stream = read();
        try {
            return new InputStreamReader(stream, charset);
        } catch (UnsupportedEncodingException ex) {
            StreamUtils.closeQuietly(stream);
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
    }

    public BufferedReader reader(int bufferSize) {
        return new BufferedReader(new InputStreamReader(read()), bufferSize);
    }

    public BufferedReader reader(int bufferSize, String charset) {
        try {
            return new BufferedReader(new InputStreamReader(read(), charset), bufferSize);
        } catch (UnsupportedEncodingException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
    }

    public String readString() {
        return readString((String) null);
    }

    public String readString(String charset) {
        StringBuilder output = new StringBuilder(estimateLength());
        InputStreamReader reader = null;
        if (charset == null) {
            try {
                reader = new InputStreamReader(read());
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error reading layout file: " + this, ex);
            } catch (Throwable th) {
                StreamUtils.closeQuietly(reader);
                throw th;
            }
        } else {
            reader = new InputStreamReader(read(), charset);
        }
        char[] buffer = new char[256];
        while (true) {
            int length = reader.read(buffer);
            if (length == -1) {
                StreamUtils.closeQuietly(reader);
                return output.toString();
            }
            output.append(buffer, 0, length);
        }
    }

    public byte[] readBytes() {
        InputStream input = read();
        try {
            byte[] copyStreamToByteArray = StreamUtils.copyStreamToByteArray(input, estimateLength());
            StreamUtils.closeQuietly(input);
            return copyStreamToByteArray;
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(input);
            throw th;
        }
    }

    private int estimateLength() {
        int length = (int) length();
        return length != 0 ? length : GL20.GL_NEVER;
    }

    public int readBytes(byte[] bytes, int offset, int size) {
        InputStream input = read();
        int position = 0;
        while (true) {
            try {
                int count = input.read(bytes, offset + position, size - position);
                if (count <= 0) {
                    StreamUtils.closeQuietly(input);
                    return position - offset;
                }
                position += count;
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error reading file: " + this, ex);
            } catch (Throwable th) {
                StreamUtils.closeQuietly(input);
                throw th;
            }
        }
    }

    public ByteBuffer map() {
        return map(FileChannel.MapMode.READ_ONLY);
    }

    public ByteBuffer map(FileChannel.MapMode mode) {
        if (this.type != Files.FileType.Classpath) {
            try {
                RandomAccessFile raf = new RandomAccessFile(this.file, mode == FileChannel.MapMode.READ_ONLY ? "r" : "rw");
                ByteBuffer map = raf.getChannel().map(mode, 0, this.file.length());
                map.order(ByteOrder.nativeOrder());
                StreamUtils.closeQuietly(raf);
                return map;
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error memory mapping file: " + this + " (" + this.type + ")", ex);
            } catch (Throwable th) {
                StreamUtils.closeQuietly((Closeable) null);
                throw th;
            }
        } else {
            throw new GdxRuntimeException("Cannot map a classpath file: " + this);
        }
    }

    public OutputStream write(boolean append) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            parent().mkdirs();
            try {
                return new FileOutputStream(file(), append);
            } catch (Exception ex) {
                if (file().isDirectory()) {
                    throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
                }
                throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
            }
        } else {
            throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
        }
    }

    public OutputStream write(boolean append, int bufferSize) {
        return new BufferedOutputStream(write(append), bufferSize);
    }

    public void write(InputStream input, boolean append) {
        OutputStream output = null;
        try {
            output = write(append);
            StreamUtils.copyStream(input, output);
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error stream writing to file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
            throw th;
        }
    }

    public Writer writer(boolean append) {
        return writer(append, (String) null);
    }

    public Writer writer(boolean append, String charset) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            parent().mkdirs();
            try {
                FileOutputStream output = new FileOutputStream(file(), append);
                if (charset == null) {
                    return new OutputStreamWriter(output);
                }
                return new OutputStreamWriter(output, charset);
            } catch (IOException ex) {
                if (file().isDirectory()) {
                    throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
                }
                throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
            }
        } else {
            throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
        }
    }

    public void writeString(String string, boolean append) {
        writeString(string, append, (String) null);
    }

    public void writeString(String string, boolean append, String charset) {
        Writer writer = null;
        try {
            writer = writer(append, charset);
            writer.write(string);
            StreamUtils.closeQuietly(writer);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(writer);
            throw th;
        }
    }

    public void writeBytes(byte[] bytes, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes);
            StreamUtils.closeQuietly(output);
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(output);
            throw th;
        }
    }

    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes, offset, length);
            StreamUtils.closeQuietly(output);
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(output);
            throw th;
        }
    }

    public FileHandle[] list() {
        if (this.type != Files.FileType.Classpath) {
            String[] relativePaths = file().list();
            if (relativePaths == null) {
                return new FileHandle[0];
            }
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int n = relativePaths.length;
            for (int i = 0; i < n; i++) {
                handles[i] = child(relativePaths[i]);
            }
            return handles;
        }
        throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
    }

    public FileHandle[] list(FileFilter filter) {
        if (this.type != Files.FileType.Classpath) {
            String[] relativePaths = file().list();
            if (relativePaths == null) {
                return new FileHandle[0];
            }
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            for (String path : relativePaths) {
                FileHandle child = child(path);
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
        }
        throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
    }

    public FileHandle[] list(FilenameFilter filter) {
        if (this.type != Files.FileType.Classpath) {
            File file2 = file();
            String[] relativePaths = file2.list();
            if (relativePaths == null) {
                return new FileHandle[0];
            }
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            for (String path : relativePaths) {
                if (filter.accept(file2, path)) {
                    handles[count] = child(path);
                    count++;
                }
            }
            if (count >= relativePaths.length) {
                return handles;
            }
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            return newHandles;
        }
        throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
    }

    public FileHandle[] list(String suffix) {
        if (this.type != Files.FileType.Classpath) {
            String[] relativePaths = file().list();
            if (relativePaths == null) {
                return new FileHandle[0];
            }
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            for (String path : relativePaths) {
                if (path.endsWith(suffix)) {
                    handles[count] = child(path);
                    count++;
                }
            }
            if (count >= relativePaths.length) {
                return handles;
            }
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            return newHandles;
        }
        throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
    }

    public boolean isDirectory() {
        if (this.type == Files.FileType.Classpath) {
            return false;
        }
        return file().isDirectory();
    }

    public FileHandle child(String name) {
        if (this.file.getPath().length() == 0) {
            return new FileHandle(new File(name), this.type);
        }
        return new FileHandle(new File(this.file, name), this.type);
    }

    public FileHandle sibling(String name) {
        if (this.file.getPath().length() != 0) {
            return new FileHandle(new File(this.file.getParent(), name), this.type);
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
        return new FileHandle(parent, this.type);
    }

    public void mkdirs() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot mkdirs with a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            file().mkdirs();
        } else {
            throw new GdxRuntimeException("Cannot mkdirs with an internal file: " + this.file);
        }
    }

    /* renamed from: com.badlogic.gdx.files.FileHandle$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$Files$FileType = new int[Files.FileType.values().length];

        static {
            try {
                $SwitchMap$com$badlogic$gdx$Files$FileType[Files.FileType.Internal.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$Files$FileType[Files.FileType.Classpath.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$Files$FileType[Files.FileType.Absolute.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$Files$FileType[Files.FileType.External.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public boolean exists() {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$Files$FileType[this.type.ordinal()];
        if (i != 1) {
            if (i != 2) {
                return file().exists();
            }
        } else if (file().exists()) {
            return true;
        }
        if (FileHandle.class.getResource("/" + this.file.getPath().replace('\\', '/')) != null) {
            return true;
        }
        return false;
    }

    public boolean delete() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            return file().delete();
        } else {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
    }

    public boolean deleteDirectory() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            return deleteDirectory(file());
        } else {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
    }

    public void emptyDirectory() {
        emptyDirectory(false);
    }

    public void emptyDirectory(boolean preserveTree) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            emptyDirectory(file(), preserveTree);
        } else {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
    }

    public void copyTo(FileHandle dest) {
        if (!isDirectory()) {
            if (dest.isDirectory()) {
                dest = dest.child(name());
            }
            copyFile(this, dest);
            return;
        }
        if (!dest.exists()) {
            dest.mkdirs();
            if (!dest.isDirectory()) {
                throw new GdxRuntimeException("Destination directory cannot be created: " + dest);
            }
        } else if (!dest.isDirectory()) {
            throw new GdxRuntimeException("Destination exists but is not a directory: " + dest);
        }
        copyDirectory(this, dest.child(name()));
    }

    public void moveTo(FileHandle dest) {
        int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$Files$FileType[this.type.ordinal()];
        if (i == 1) {
            throw new GdxRuntimeException("Cannot move an internal file: " + this.file);
        } else if (i == 2) {
            throw new GdxRuntimeException("Cannot move a classpath file: " + this.file);
        } else if ((i != 3 && i != 4) || !file().renameTo(dest.file())) {
            copyTo(dest);
            delete();
            if (exists() && isDirectory()) {
                deleteDirectory();
            }
        }
    }

    /* JADX INFO: finally extract failed */
    public long length() {
        if (this.type != Files.FileType.Classpath && (this.type != Files.FileType.Internal || this.file.exists())) {
            return file().length();
        }
        InputStream input = read();
        try {
            long available = (long) input.available();
            StreamUtils.closeQuietly(input);
            return available;
        } catch (Exception e) {
            StreamUtils.closeQuietly(input);
            return 0;
        } catch (Throwable th) {
            StreamUtils.closeQuietly(input);
            throw th;
        }
    }

    public long lastModified() {
        return file().lastModified();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FileHandle)) {
            return false;
        }
        FileHandle other = (FileHandle) obj;
        if (this.type != other.type || !path().equals(other.path())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((1 * 37) + this.type.hashCode()) * 67) + path().hashCode();
    }

    public String toString() {
        return this.file.getPath().replace('\\', '/');
    }

    public static FileHandle tempFile(String prefix) {
        try {
            return new FileHandle(File.createTempFile(prefix, (String) null));
        } catch (IOException ex) {
            throw new GdxRuntimeException("Unable to create temp file.", ex);
        }
    }

    public static FileHandle tempDirectory(String prefix) {
        try {
            File file2 = File.createTempFile(prefix, (String) null);
            if (!file2.delete()) {
                throw new IOException("Unable to delete temp file: " + file2);
            } else if (file2.mkdir()) {
                return new FileHandle(file2);
            } else {
                throw new IOException("Unable to create temp directory: " + file2);
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Unable to create temp file.", ex);
        }
    }

    private static void emptyDirectory(File file2, boolean preserveTree) {
        File[] files;
        if (file2.exists() && (files = file2.listFiles()) != null) {
            int n = files.length;
            for (int i = 0; i < n; i++) {
                if (!files[i].isDirectory()) {
                    files[i].delete();
                } else if (preserveTree) {
                    emptyDirectory(files[i], true);
                } else {
                    deleteDirectory(files[i]);
                }
            }
        }
    }

    private static boolean deleteDirectory(File file2) {
        emptyDirectory(file2, false);
        return file2.delete();
    }

    private static void copyFile(FileHandle source, FileHandle dest) {
        try {
            dest.write(source.read(), false);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error copying source file: " + source.file + " (" + source.type + ")\nTo destination: " + dest.file + " (" + dest.type + ")", ex);
        }
    }

    private static void copyDirectory(FileHandle sourceDir, FileHandle destDir) {
        destDir.mkdirs();
        for (FileHandle srcFile : sourceDir.list()) {
            FileHandle destFile = destDir.child(srcFile.name());
            if (srcFile.isDirectory()) {
                copyDirectory(srcFile, destFile);
            } else {
                copyFile(srcFile, destFile);
            }
        }
    }
}
