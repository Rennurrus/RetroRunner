package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;

public class TextureDescriptor<T extends GLTexture> implements Comparable<TextureDescriptor<T>> {
    public Texture.TextureFilter magFilter;
    public Texture.TextureFilter minFilter;
    public T texture;
    public Texture.TextureWrap uWrap;
    public Texture.TextureWrap vWrap;

    public TextureDescriptor(T texture2, Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2, Texture.TextureWrap uWrap2, Texture.TextureWrap vWrap2) {
        this.texture = null;
        set(texture2, minFilter2, magFilter2, uWrap2, vWrap2);
    }

    public TextureDescriptor(T texture2) {
        this(texture2, (Texture.TextureFilter) null, (Texture.TextureFilter) null, (Texture.TextureWrap) null, (Texture.TextureWrap) null);
    }

    public TextureDescriptor() {
        this.texture = null;
    }

    public void set(T texture2, Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2, Texture.TextureWrap uWrap2, Texture.TextureWrap vWrap2) {
        this.texture = texture2;
        this.minFilter = minFilter2;
        this.magFilter = magFilter2;
        this.uWrap = uWrap2;
        this.vWrap = vWrap2;
    }

    public <V extends T> void set(TextureDescriptor<V> other) {
        this.texture = other.texture;
        this.minFilter = other.minFilter;
        this.magFilter = other.magFilter;
        this.uWrap = other.uWrap;
        this.vWrap = other.vWrap;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextureDescriptor)) {
            return false;
        }
        TextureDescriptor<?> other = (TextureDescriptor) obj;
        if (other.texture == this.texture && other.minFilter == this.minFilter && other.magFilter == this.magFilter && other.uWrap == this.uWrap && other.vWrap == this.vWrap) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        T t = this.texture;
        int i = 0;
        long j = ((long) (t == null ? 0 : t.glTarget)) * 811;
        T t2 = this.texture;
        long result = (j + ((long) (t2 == null ? 0 : t2.getTextureObjectHandle()))) * 811;
        Texture.TextureFilter textureFilter = this.minFilter;
        long result2 = (result + ((long) (textureFilter == null ? 0 : textureFilter.getGLEnum()))) * 811;
        Texture.TextureFilter textureFilter2 = this.magFilter;
        long result3 = (result2 + ((long) (textureFilter2 == null ? 0 : textureFilter2.getGLEnum()))) * 811;
        Texture.TextureWrap textureWrap = this.uWrap;
        long gLEnum = 811 * (result3 + ((long) (textureWrap == null ? 0 : textureWrap.getGLEnum())));
        Texture.TextureWrap textureWrap2 = this.vWrap;
        if (textureWrap2 != null) {
            i = textureWrap2.getGLEnum();
        }
        long result4 = gLEnum + ((long) i);
        return (int) ((result4 >> 32) ^ result4);
    }

    public int compareTo(TextureDescriptor<T> o) {
        int i = 0;
        if (o == this) {
            return 0;
        }
        T t = this.texture;
        int t1 = t == null ? 0 : t.glTarget;
        T t2 = o.texture;
        int t22 = t2 == null ? 0 : t2.glTarget;
        if (t1 != t22) {
            return t1 - t22;
        }
        T t3 = this.texture;
        int h1 = t3 == null ? 0 : t3.getTextureObjectHandle();
        T t4 = o.texture;
        int h2 = t4 == null ? 0 : t4.getTextureObjectHandle();
        if (h1 != h2) {
            return h1 - h2;
        }
        Texture.TextureFilter textureFilter = this.minFilter;
        if (textureFilter != o.minFilter) {
            int gLEnum = textureFilter == null ? 0 : textureFilter.getGLEnum();
            Texture.TextureFilter textureFilter2 = o.minFilter;
            if (textureFilter2 != null) {
                i = textureFilter2.getGLEnum();
            }
            return gLEnum - i;
        }
        Texture.TextureFilter textureFilter3 = this.magFilter;
        if (textureFilter3 != o.magFilter) {
            int gLEnum2 = textureFilter3 == null ? 0 : textureFilter3.getGLEnum();
            Texture.TextureFilter textureFilter4 = o.magFilter;
            if (textureFilter4 != null) {
                i = textureFilter4.getGLEnum();
            }
            return gLEnum2 - i;
        }
        Texture.TextureWrap textureWrap = this.uWrap;
        if (textureWrap != o.uWrap) {
            int gLEnum3 = textureWrap == null ? 0 : textureWrap.getGLEnum();
            Texture.TextureWrap textureWrap2 = o.uWrap;
            if (textureWrap2 != null) {
                i = textureWrap2.getGLEnum();
            }
            return gLEnum3 - i;
        }
        Texture.TextureWrap textureWrap3 = this.vWrap;
        if (textureWrap3 == o.vWrap) {
            return 0;
        }
        int gLEnum4 = textureWrap3 == null ? 0 : textureWrap3.getGLEnum();
        Texture.TextureWrap textureWrap4 = o.vWrap;
        if (textureWrap4 != null) {
            i = textureWrap4.getGLEnum();
        }
        return gLEnum4 - i;
    }
}
