package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.IntBuffer;

public final class DefaultTextureBinder implements TextureBinder {
    public static final int MAX_GLES_UNITS = 32;
    public static final int ROUNDROBIN = 0;
    public static final int WEIGHTED = 1;
    private int bindCount;
    private final int count;
    private int currentTexture;
    private final int method;
    private final int offset;
    private int reuseCount;
    private final int reuseWeight;
    private boolean reused;
    private final TextureDescriptor tempDesc;
    private final GLTexture[] textures;
    private final int[] weights;

    public DefaultTextureBinder(int method2) {
        this(method2, 0);
    }

    public DefaultTextureBinder(int method2, int offset2) {
        this(method2, offset2, -1);
    }

    public DefaultTextureBinder(int method2, int offset2, int count2) {
        this(method2, offset2, count2, 10);
    }

    public DefaultTextureBinder(int method2, int offset2, int count2, int reuseWeight2) {
        this.reuseCount = 0;
        this.bindCount = 0;
        this.tempDesc = new TextureDescriptor();
        this.currentTexture = 0;
        int max = Math.min(getMaxTextureUnits(), 32);
        count2 = count2 < 0 ? max - offset2 : count2;
        if (offset2 < 0 || count2 < 0 || offset2 + count2 > max || reuseWeight2 < 1) {
            throw new GdxRuntimeException("Illegal arguments");
        }
        this.method = method2;
        this.offset = offset2;
        this.count = count2;
        this.textures = new GLTexture[count2];
        this.reuseWeight = reuseWeight2;
        this.weights = method2 == 1 ? new int[count2] : null;
    }

    private static int getMaxTextureUnits() {
        IntBuffer buffer = BufferUtils.newIntBuffer(16);
        Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
        return buffer.get(0);
    }

    public void begin() {
        for (int i = 0; i < this.count; i++) {
            this.textures[i] = null;
            int[] iArr = this.weights;
            if (iArr != null) {
                iArr[i] = 0;
            }
        }
    }

    public void end() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    public final int bind(TextureDescriptor textureDesc) {
        return bindTexture(textureDesc, false);
    }

    public final int bind(GLTexture texture) {
        this.tempDesc.set(texture, (Texture.TextureFilter) null, (Texture.TextureFilter) null, (Texture.TextureWrap) null, (Texture.TextureWrap) null);
        return bindTexture(this.tempDesc, false);
    }

    private final int bindTexture(TextureDescriptor textureDesc, boolean rebind) {
        int result;
        GLTexture texture = textureDesc.texture;
        this.reused = false;
        int i = this.method;
        if (i == 0) {
            int result2 = this.offset;
            int bindTextureRoundRobin = bindTextureRoundRobin(texture);
            int i2 = bindTextureRoundRobin;
            result = result2 + bindTextureRoundRobin;
        } else if (i != 1) {
            return -1;
        } else {
            int i3 = this.offset;
            int bindTextureWeighted = bindTextureWeighted(texture);
            int i4 = bindTextureWeighted;
            result = i3 + bindTextureWeighted;
        }
        if (this.reused) {
            this.reuseCount++;
            if (rebind) {
                texture.bind(result);
            } else {
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + result);
            }
        } else {
            this.bindCount++;
        }
        texture.unsafeSetWrap(textureDesc.uWrap, textureDesc.vWrap);
        texture.unsafeSetFilter(textureDesc.minFilter, textureDesc.magFilter);
        return result;
    }

    private final int bindTextureRoundRobin(GLTexture texture) {
        int i = 0;
        while (true) {
            int i2 = this.count;
            if (i < i2) {
                int idx = (this.currentTexture + i) % i2;
                if (this.textures[idx] == texture) {
                    this.reused = true;
                    return idx;
                }
                i++;
            } else {
                this.currentTexture = (this.currentTexture + 1) % i2;
                GLTexture[] gLTextureArr = this.textures;
                int i3 = this.currentTexture;
                gLTextureArr[i3] = texture;
                texture.bind(this.offset + i3);
                return this.currentTexture;
            }
        }
    }

    private final int bindTextureWeighted(GLTexture texture) {
        int result = -1;
        int weight = this.weights[0];
        int windex = 0;
        for (int i = 0; i < this.count; i++) {
            if (this.textures[i] == texture) {
                result = i;
                int[] iArr = this.weights;
                iArr[i] = iArr[i] + this.reuseWeight;
            } else {
                int[] iArr2 = this.weights;
                if (iArr2[i] >= 0) {
                    int i2 = iArr2[i] - 1;
                    iArr2[i] = i2;
                    if (i2 >= weight) {
                    }
                }
                weight = this.weights[i];
                windex = i;
            }
        }
        if (result < 0) {
            this.textures[windex] = texture;
            this.weights[windex] = 100;
            int result2 = windex;
            texture.bind(this.offset + windex);
            return result2;
        }
        this.reused = true;
        return result;
    }

    public final int getBindCount() {
        return this.bindCount;
    }

    public final int getReuseCount() {
        return this.reuseCount;
    }

    public final void resetCounts() {
        this.reuseCount = 0;
        this.bindCount = 0;
    }
}
