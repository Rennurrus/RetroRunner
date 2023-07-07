package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import java.util.Locale;

public class I18NBundleLoader extends AsynchronousAssetLoader<I18NBundle, I18NBundleParameter> {
    I18NBundle bundle;

    public I18NBundleLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public void loadAsync(AssetManager manager, String fileName, FileHandle file, I18NBundleParameter parameter) {
        String encoding;
        Locale locale;
        this.bundle = null;
        if (parameter == null) {
            locale = Locale.getDefault();
            encoding = null;
        } else {
            locale = parameter.locale == null ? Locale.getDefault() : parameter.locale;
            encoding = parameter.encoding;
        }
        if (encoding == null) {
            this.bundle = I18NBundle.createBundle(file, locale);
        } else {
            this.bundle = I18NBundle.createBundle(file, locale, encoding);
        }
    }

    public I18NBundle loadSync(AssetManager manager, String fileName, FileHandle file, I18NBundleParameter parameter) {
        I18NBundle bundle2 = this.bundle;
        this.bundle = null;
        return bundle2;
    }

    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, I18NBundleParameter parameter) {
        return null;
    }

    public static class I18NBundleParameter extends AssetLoaderParameters<I18NBundle> {
        public final String encoding;
        public final Locale locale;

        public I18NBundleParameter() {
            this((Locale) null, (String) null);
        }

        public I18NBundleParameter(Locale locale2) {
            this(locale2, (String) null);
        }

        public I18NBundleParameter(Locale locale2, String encoding2) {
            this.locale = locale2;
            this.encoding = encoding2;
        }
    }
}
