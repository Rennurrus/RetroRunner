package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.twi.game.BuildConfig;

public class ResolutionFileResolver implements FileHandleResolver {
    protected final FileHandleResolver baseResolver;
    protected final Resolution[] descriptors;

    public static class Resolution {
        public final String folder;
        public final int portraitHeight;
        public final int portraitWidth;

        public Resolution(int portraitWidth2, int portraitHeight2, String folder2) {
            this.portraitWidth = portraitWidth2;
            this.portraitHeight = portraitHeight2;
            this.folder = folder2;
        }
    }

    public ResolutionFileResolver(FileHandleResolver baseResolver2, Resolution... descriptors2) {
        if (descriptors2.length != 0) {
            this.baseResolver = baseResolver2;
            this.descriptors = descriptors2;
            return;
        }
        throw new IllegalArgumentException("At least one Resolution needs to be supplied.");
    }

    public FileHandle resolve(String fileName) {
        Resolution bestResolution = choose(this.descriptors);
        FileHandle handle = this.baseResolver.resolve(resolve(new FileHandle(fileName), bestResolution.folder));
        if (!handle.exists()) {
            return this.baseResolver.resolve(fileName);
        }
        return handle;
    }

    /* access modifiers changed from: protected */
    public String resolve(FileHandle originalHandle, String suffix) {
        String parentString = BuildConfig.FLAVOR;
        FileHandle parent = originalHandle.parent();
        if (parent != null && !parent.name().equals(BuildConfig.FLAVOR)) {
            parentString = parent + "/";
        }
        return parentString + suffix + "/" + originalHandle.name();
    }

    public static Resolution choose(Resolution... descriptors2) {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        Resolution best = descriptors2[0];
        if (w < h) {
            int n = descriptors2.length;
            for (int i = 0; i < n; i++) {
                Resolution other = descriptors2[i];
                if (w >= other.portraitWidth && other.portraitWidth >= best.portraitWidth && h >= other.portraitHeight && other.portraitHeight >= best.portraitHeight) {
                    best = descriptors2[i];
                }
            }
        } else {
            int n2 = descriptors2.length;
            for (int i2 = 0; i2 < n2; i2++) {
                Resolution other2 = descriptors2[i2];
                if (w >= other2.portraitHeight && other2.portraitHeight >= best.portraitHeight && h >= other2.portraitWidth && other2.portraitWidth >= best.portraitWidth) {
                    best = descriptors2[i2];
                }
            }
        }
        return best;
    }
}
