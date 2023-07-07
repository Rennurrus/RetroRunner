package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.twi.game.BuildConfig;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

public class I18NBundle {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Locale ROOT_LOCALE = new Locale(BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
    private static boolean exceptionOnMissingKey = true;
    private static boolean simpleFormatter = false;
    private TextFormatter formatter;
    private Locale locale;
    private I18NBundle parent;
    private ObjectMap<String, String> properties;

    public static boolean getSimpleFormatter() {
        return simpleFormatter;
    }

    public static void setSimpleFormatter(boolean enabled) {
        simpleFormatter = enabled;
    }

    public static boolean getExceptionOnMissingKey() {
        return exceptionOnMissingKey;
    }

    public static void setExceptionOnMissingKey(boolean enabled) {
        exceptionOnMissingKey = enabled;
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle) {
        return createBundleImpl(baseFileHandle, Locale.getDefault(), DEFAULT_ENCODING);
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle, Locale locale2) {
        return createBundleImpl(baseFileHandle, locale2, DEFAULT_ENCODING);
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle, String encoding) {
        return createBundleImpl(baseFileHandle, Locale.getDefault(), encoding);
    }

    public static I18NBundle createBundle(FileHandle baseFileHandle, Locale locale2, String encoding) {
        return createBundleImpl(baseFileHandle, locale2, encoding);
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:30:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.badlogic.gdx.utils.I18NBundle createBundleImpl(com.badlogic.gdx.files.FileHandle r9, java.util.Locale r10, java.lang.String r11) {
        /*
            if (r9 == 0) goto L_0x0084
            if (r10 == 0) goto L_0x0084
            if (r11 == 0) goto L_0x0084
            r0 = 0
            r1 = 0
            r2 = r10
        L_0x0009:
            java.util.List r3 = getCandidateLocales(r2)
            r4 = 0
            com.badlogic.gdx.utils.I18NBundle r0 = loadBundleChain(r9, r11, r3, r4, r1)
            if (r0 == 0) goto L_0x003e
            java.util.Locale r5 = r0.getLocale()
            java.util.Locale r6 = ROOT_LOCALE
            boolean r6 = r5.equals(r6)
            if (r6 == 0) goto L_0x0044
            boolean r7 = r5.equals(r10)
            if (r7 == 0) goto L_0x0027
            goto L_0x0044
        L_0x0027:
            int r7 = r3.size()
            r8 = 1
            if (r7 != r8) goto L_0x0039
            java.lang.Object r4 = r3.get(r4)
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0039
            goto L_0x0044
        L_0x0039:
            if (r6 == 0) goto L_0x003e
            if (r1 != 0) goto L_0x003e
            r1 = r0
        L_0x003e:
            java.util.Locale r2 = getFallbackLocale(r2)
            if (r2 != 0) goto L_0x0009
        L_0x0044:
            if (r0 != 0) goto L_0x0083
            if (r1 == 0) goto L_0x004a
            r0 = r1
            goto L_0x0083
        L_0x004a:
            java.util.MissingResourceException r3 = new java.util.MissingResourceException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Can't find bundle for base file handle "
            r4.append(r5)
            java.lang.String r5 = r9.path()
            r4.append(r5)
            java.lang.String r5 = ", locale "
            r4.append(r5)
            r4.append(r10)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r9)
            java.lang.String r6 = "_"
            r5.append(r6)
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = ""
            r3.<init>(r4, r5, r6)
            throw r3
        L_0x0083:
            return r0
        L_0x0084:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            r0.<init>()
            goto L_0x008b
        L_0x008a:
            throw r0
        L_0x008b:
            goto L_0x008a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.I18NBundle.createBundleImpl(com.badlogic.gdx.files.FileHandle, java.util.Locale, java.lang.String):com.badlogic.gdx.utils.I18NBundle");
    }

    private static List<Locale> getCandidateLocales(Locale locale2) {
        String language = locale2.getLanguage();
        String country = locale2.getCountry();
        String variant = locale2.getVariant();
        List<Locale> locales = new ArrayList<>(4);
        if (variant.length() > 0) {
            locales.add(locale2);
        }
        if (country.length() > 0) {
            locales.add(locales.isEmpty() ? locale2 : new Locale(language, country));
        }
        if (language.length() > 0) {
            locales.add(locales.isEmpty() ? locale2 : new Locale(language));
        }
        locales.add(ROOT_LOCALE);
        return locales;
    }

    private static Locale getFallbackLocale(Locale locale2) {
        Locale defaultLocale = Locale.getDefault();
        if (locale2.equals(defaultLocale)) {
            return null;
        }
        return defaultLocale;
    }

    private static I18NBundle loadBundleChain(FileHandle baseFileHandle, String encoding, List<Locale> candidateLocales, int candidateIndex, I18NBundle baseBundle) {
        Locale targetLocale = candidateLocales.get(candidateIndex);
        I18NBundle parent2 = null;
        if (candidateIndex != candidateLocales.size() - 1) {
            parent2 = loadBundleChain(baseFileHandle, encoding, candidateLocales, candidateIndex + 1, baseBundle);
        } else if (baseBundle != null && targetLocale.equals(ROOT_LOCALE)) {
            return baseBundle;
        }
        I18NBundle bundle = loadBundle(baseFileHandle, encoding, targetLocale);
        if (bundle == null) {
            return parent2;
        }
        bundle.parent = parent2;
        return bundle;
    }

    private static I18NBundle loadBundle(FileHandle baseFileHandle, String encoding, Locale targetLocale) {
        I18NBundle bundle = null;
        Reader reader = null;
        try {
            FileHandle fileHandle = toFileHandle(baseFileHandle, targetLocale);
            if (checkFileExistence(fileHandle)) {
                bundle = new I18NBundle();
                reader = fileHandle.reader(encoding);
                bundle.load(reader);
            }
            StreamUtils.closeQuietly(reader);
            if (bundle != null) {
                bundle.setLocale(targetLocale);
            }
            return bundle;
        } catch (IOException e) {
            throw new GdxRuntimeException((Throwable) e);
        } catch (Throwable th) {
            StreamUtils.closeQuietly((Closeable) null);
            throw th;
        }
    }

    private static boolean checkFileExistence(FileHandle fh) {
        try {
            fh.read().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void load(Reader reader) throws IOException {
        this.properties = new ObjectMap<>();
        PropertiesUtils.load(this.properties, reader);
    }

    private static FileHandle toFileHandle(FileHandle baseFileHandle, Locale locale2) {
        StringBuilder sb = new StringBuilder(baseFileHandle.name());
        if (!locale2.equals(ROOT_LOCALE)) {
            String language = locale2.getLanguage();
            String country = locale2.getCountry();
            String variant = locale2.getVariant();
            boolean emptyLanguage = BuildConfig.FLAVOR.equals(language);
            boolean emptyCountry = BuildConfig.FLAVOR.equals(country);
            boolean emptyVariant = BuildConfig.FLAVOR.equals(variant);
            if (!emptyLanguage || !emptyCountry || !emptyVariant) {
                sb.append('_');
                if (!emptyVariant) {
                    sb.append(language).append('_').append(country).append('_').append(variant);
                } else if (!emptyCountry) {
                    sb.append(language).append('_').append(country);
                } else {
                    sb.append(language);
                }
            }
        }
        return baseFileHandle.sibling(sb.append(".properties").toString());
    }

    public Locale getLocale() {
        return this.locale;
    }

    private void setLocale(Locale locale2) {
        this.locale = locale2;
        this.formatter = new TextFormatter(locale2, !simpleFormatter);
    }

    public final String get(String key) {
        String result = this.properties.get(key);
        if (result == null) {
            I18NBundle i18NBundle = this.parent;
            if (i18NBundle != null) {
                result = i18NBundle.get(key);
            }
            if (result == null) {
                if (!exceptionOnMissingKey) {
                    return "???" + key + "???";
                }
                throw new MissingResourceException("Can't find bundle key " + key, getClass().getName(), key);
            }
        }
        return result;
    }

    public String format(String key, Object... args) {
        return this.formatter.format(get(key), args);
    }

    public void debug(String placeholder) {
        ObjectMap.Keys<String> keys = this.properties.keys();
        if (keys != null) {
            ObjectMap.Keys<String> it = keys.iterator();
            while (it.hasNext()) {
                this.properties.put((String) it.next(), placeholder);
            }
        }
    }
}
