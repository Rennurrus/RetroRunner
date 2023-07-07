package com.badlogic.gdx.utils;

import com.twi.game.BuildConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SharedLibraryLoader {
    public static String abi = (System.getProperty("sun.arch.abi") != null ? System.getProperty("sun.arch.abi") : BuildConfig.FLAVOR);
    public static boolean is64Bit;
    public static boolean isARM = System.getProperty("os.arch").startsWith("arm");
    public static boolean isAndroid;
    public static boolean isIos;
    public static boolean isLinux;
    public static boolean isMac;
    public static boolean isWindows;
    private static final HashSet<String> loadedLibraries = new HashSet<>();
    private String nativesJar;

    static {
        isWindows = System.getProperty("os.name").contains("Windows");
        isLinux = System.getProperty("os.name").contains("Linux");
        isMac = System.getProperty("os.name").contains("Mac");
        isIos = false;
        isAndroid = false;
        is64Bit = System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64");
        boolean isMOEiOS = "iOS".equals(System.getProperty("moe.platform.name"));
        String vm = System.getProperty("java.runtime.name");
        if (vm != null && vm.contains("Android Runtime")) {
            isAndroid = true;
            isWindows = false;
            isLinux = false;
            isMac = false;
            is64Bit = false;
        }
        if (isMOEiOS || (!isAndroid && !isWindows && !isLinux && !isMac)) {
            isIos = true;
            isAndroid = false;
            isWindows = false;
            isLinux = false;
            isMac = false;
            is64Bit = false;
        }
    }

    public SharedLibraryLoader() {
    }

    public SharedLibraryLoader(String nativesJar2) {
        this.nativesJar = nativesJar2;
    }

    public String crc(InputStream input) {
        if (input != null) {
            CRC32 crc = new CRC32();
            byte[] buffer = new byte[StreamUtils.DEFAULT_BUFFER_SIZE];
            while (true) {
                try {
                    int length = input.read(buffer);
                    if (length == -1) {
                        break;
                    }
                    crc.update(buffer, 0, length);
                } catch (Exception e) {
                } catch (Throwable th) {
                    StreamUtils.closeQuietly(input);
                    throw th;
                }
            }
            StreamUtils.closeQuietly(input);
            return Long.toString(crc.getValue(), 16);
        }
        throw new IllegalArgumentException("input cannot be null.");
    }

    public String mapLibraryName(String libraryName) {
        String str;
        if (isWindows) {
            StringBuilder sb = new StringBuilder();
            sb.append(libraryName);
            sb.append(is64Bit ? "64.dll" : ".dll");
            return sb.toString();
        } else if (isLinux) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("lib");
            sb2.append(libraryName);
            if (isARM) {
                str = "arm" + abi;
            } else {
                str = BuildConfig.FLAVOR;
            }
            sb2.append(str);
            sb2.append(is64Bit ? "64.so" : ".so");
            return sb2.toString();
        } else if (!isMac) {
            return libraryName;
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("lib");
            sb3.append(libraryName);
            sb3.append(is64Bit ? "64.dylib" : ".dylib");
            return sb3.toString();
        }
    }

    public void load(String libraryName) {
        if (!isIos) {
            synchronized (SharedLibraryLoader.class) {
                if (!isLoaded(libraryName)) {
                    String platformName = mapLibraryName(libraryName);
                    try {
                        if (isAndroid) {
                            System.loadLibrary(platformName);
                        } else {
                            loadFile(platformName);
                        }
                        setLoaded(libraryName);
                    } catch (Throwable ex) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Couldn't load shared library '");
                        sb.append(platformName);
                        sb.append("' for target: ");
                        sb.append(System.getProperty("os.name"));
                        sb.append(is64Bit ? ", 64-bit" : ", 32-bit");
                        throw new GdxRuntimeException(sb.toString(), ex);
                    }
                }
            }
        }
    }

    private InputStream readFile(String path) {
        String str = this.nativesJar;
        if (str == null) {
            InputStream input = SharedLibraryLoader.class.getResourceAsStream("/" + path);
            if (input != null) {
                return input;
            }
            throw new GdxRuntimeException("Unable to read file for extraction: " + path);
        }
        try {
            ZipFile file = new ZipFile(str);
            ZipEntry entry = file.getEntry(path);
            if (entry != null) {
                return file.getInputStream(entry);
            }
            throw new GdxRuntimeException("Couldn't find '" + path + "' in JAR: " + this.nativesJar);
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading '" + path + "' in JAR: " + this.nativesJar, ex);
        }
    }

    public File extractFile(String sourcePath, String dirName) throws IOException {
        try {
            String sourceCrc = crc(readFile(sourcePath));
            if (dirName == null) {
                dirName = sourceCrc;
            }
            File extractedFile = getExtractedFile(dirName, new File(sourcePath).getName());
            if (extractedFile == null) {
                extractedFile = getExtractedFile(UUID.randomUUID().toString(), new File(sourcePath).getName());
                if (extractedFile == null) {
                    throw new GdxRuntimeException("Unable to find writable path to extract file. Is the user home directory writable?");
                }
            }
            return extractFile(sourcePath, sourceCrc, extractedFile);
        } catch (RuntimeException ex) {
            File file = new File(System.getProperty("java.library.path"), sourcePath);
            if (file.exists()) {
                return file;
            }
            throw ex;
        }
    }

    public void extractFileTo(String sourcePath, File dir) throws IOException {
        extractFile(sourcePath, crc(readFile(sourcePath)), new File(dir, new File(sourcePath).getName()));
    }

    private File getExtractedFile(String dirName, String fileName) {
        File idealFile = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + dirName, fileName);
        if (canWrite(idealFile)) {
            return idealFile;
        }
        try {
            File file = File.createTempFile(dirName, (String) null);
            if (file.delete()) {
                File file2 = new File(file, fileName);
                if (canWrite(file2)) {
                    return file2;
                }
            }
        } catch (IOException e) {
        }
        File file3 = new File(System.getProperty("user.home") + "/.libgdx/" + dirName, fileName);
        if (canWrite(file3)) {
            return file3;
        }
        File file4 = new File(".temp/" + dirName, fileName);
        if (canWrite(file4)) {
            return file4;
        }
        if (System.getenv("APP_SANDBOX_CONTAINER_ID") != null) {
            return idealFile;
        }
        return null;
    }

    private boolean canWrite(File file) {
        File testFile;
        File parent = file.getParentFile();
        if (!file.exists()) {
            parent.mkdirs();
            if (!parent.isDirectory()) {
                return false;
            }
            testFile = file;
        } else if (!file.canWrite() || !canExecute(file)) {
            return false;
        } else {
            testFile = new File(parent, UUID.randomUUID().toString());
        }
        try {
            new FileOutputStream(testFile).close();
            if (!canExecute(testFile)) {
                return false;
            }
            testFile.delete();
            return true;
        } catch (Throwable th) {
            return false;
        } finally {
            testFile.delete();
        }
    }

    private boolean canExecute(File file) {
        try {
            Method canExecute = File.class.getMethod("canExecute", new Class[0]);
            if (((Boolean) canExecute.invoke(file, new Object[0])).booleanValue()) {
                return true;
            }
            File.class.getMethod("setExecutable", new Class[]{Boolean.TYPE, Boolean.TYPE}).invoke(file, new Object[]{true, false});
            return ((Boolean) canExecute.invoke(file, new Object[0])).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    private File extractFile(String sourcePath, String sourceCrc, File extractedFile) throws IOException {
        String extractedCrc = null;
        if (extractedFile.exists()) {
            try {
                extractedCrc = crc(new FileInputStream(extractedFile));
            } catch (FileNotFoundException e) {
            }
        }
        if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
            InputStream input = null;
            FileOutputStream output = null;
            try {
                input = readFile(sourcePath);
                extractedFile.getParentFile().mkdirs();
                output = new FileOutputStream(extractedFile);
                byte[] buffer = new byte[StreamUtils.DEFAULT_BUFFER_SIZE];
                while (true) {
                    int length = input.read(buffer);
                    if (length == -1) {
                        break;
                    }
                    output.write(buffer, 0, length);
                }
                StreamUtils.closeQuietly(input);
                StreamUtils.closeQuietly(output);
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error extracting file: " + sourcePath + "\nTo: " + extractedFile.getAbsolutePath(), ex);
            } catch (Throwable th) {
                StreamUtils.closeQuietly(input);
                StreamUtils.closeQuietly(output);
                throw th;
            }
        }
        return extractedFile;
    }

    private void loadFile(String sourcePath) {
        String sourceCrc = crc(readFile(sourcePath));
        String fileName = new File(sourcePath).getName();
        Throwable ex = loadFile(sourcePath, sourceCrc, new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + sourceCrc, fileName));
        if (ex != null) {
            try {
                File file = File.createTempFile(sourceCrc, (String) null);
                if (file.delete() && loadFile(sourcePath, sourceCrc, file) == null) {
                    return;
                }
            } catch (Throwable th) {
            }
            if (loadFile(sourcePath, sourceCrc, new File(System.getProperty("user.home") + "/.libgdx/" + sourceCrc, fileName)) != null) {
                if (loadFile(sourcePath, sourceCrc, new File(".temp/" + sourceCrc, fileName)) != null) {
                    File file2 = new File(System.getProperty("java.library.path"), sourcePath);
                    if (file2.exists()) {
                        System.load(file2.getAbsolutePath());
                        return;
                    }
                    throw new GdxRuntimeException(ex);
                }
            }
        }
    }

    private Throwable loadFile(String sourcePath, String sourceCrc, File extractedFile) {
        try {
            System.load(extractFile(sourcePath, sourceCrc, extractedFile).getAbsolutePath());
            return null;
        } catch (Throwable ex) {
            return ex;
        }
    }

    public static synchronized void setLoaded(String libraryName) {
        synchronized (SharedLibraryLoader.class) {
            loadedLibraries.add(libraryName);
        }
    }

    public static synchronized boolean isLoaded(String libraryName) {
        boolean contains;
        synchronized (SharedLibraryLoader.class) {
            contains = loadedLibraries.contains(libraryName);
        }
        return contains;
    }
}
