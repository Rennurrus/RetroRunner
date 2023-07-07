package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.regex.Matcher;

public class PixmapPackerIO {

    public static class SaveParameters {
        public ImageFormat format = ImageFormat.PNG;
        public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;
        public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
        public boolean useIndexes;
    }

    public enum ImageFormat {
        CIM(".cim"),
        PNG(".png");
        
        private final String extension;

        public String getExtension() {
            return this.extension;
        }

        private ImageFormat(String extension2) {
            this.extension = extension2;
        }
    }

    public void save(FileHandle file, PixmapPacker packer) throws IOException {
        save(file, packer, new SaveParameters());
    }

    public void save(FileHandle file, PixmapPacker packer, SaveParameters parameters) throws IOException {
        String imageName;
        FileHandle fileHandle = file;
        PixmapPacker pixmapPacker = packer;
        SaveParameters saveParameters = parameters;
        Writer writer = fileHandle.writer(false);
        int index = 0;
        Iterator<PixmapPacker.Page> it = pixmapPacker.pages.iterator();
        while (it.hasNext()) {
            PixmapPacker.Page page = it.next();
            if (page.rects.size > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(file.nameWithoutExtension());
                sb.append("_");
                index++;
                sb.append(index);
                sb.append(saveParameters.format.getExtension());
                FileHandle pageFile = fileHandle.sibling(sb.toString());
                int i = AnonymousClass1.$SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat[saveParameters.format.ordinal()];
                int i2 = 1;
                if (i == 1) {
                    PixmapIO.writeCIM(pageFile, page.image);
                } else if (i == 2) {
                    PixmapIO.writePNG(pageFile, page.image);
                }
                writer.write("\n");
                writer.write(pageFile.name() + "\n");
                writer.write("size: " + page.image.getWidth() + "," + page.image.getHeight() + "\n");
                StringBuilder sb2 = new StringBuilder();
                sb2.append("format: ");
                sb2.append(pixmapPacker.pageFormat.name());
                sb2.append("\n");
                writer.write(sb2.toString());
                writer.write("filter: " + saveParameters.minFilter.name() + "," + saveParameters.magFilter.name() + "\n");
                writer.write("repeat: none\n");
                ObjectMap.Keys<String> it2 = page.rects.keys().iterator();
                while (it2.hasNext()) {
                    String name = (String) it2.next();
                    int imageIndex = -1;
                    String imageName2 = name;
                    if (saveParameters.useIndexes) {
                        imageName = imageName2;
                        Matcher matcher = PixmapPacker.indexPattern.matcher(imageName);
                        if (matcher.matches()) {
                            String imageName3 = matcher.group(i2);
                            imageIndex = Integer.parseInt(matcher.group(2));
                            imageName = imageName3;
                        }
                    } else {
                        imageName = imageName2;
                    }
                    writer.write(imageName + "\n");
                    PixmapPacker.PixmapPackerRectangle rect = page.rects.get(name);
                    writer.write("  rotate: false\n");
                    writer.write("  xy: " + ((int) rect.x) + "," + ((int) rect.y) + "\n");
                    writer.write("  size: " + ((int) rect.width) + "," + ((int) rect.height) + "\n");
                    if (rect.splits != null) {
                        writer.write("  split: " + rect.splits[0] + ", " + rect.splits[1] + ", " + rect.splits[2] + ", " + rect.splits[3] + "\n");
                        if (rect.pads != null) {
                            writer.write("  pad: " + rect.pads[0] + ", " + rect.pads[1] + ", " + rect.pads[2] + ", " + rect.pads[3] + "\n");
                        }
                    }
                    writer.write("  orig: " + rect.originalWidth + ", " + rect.originalHeight + "\n");
                    writer.write("  offset: " + rect.offsetX + ", " + ((int) ((((float) rect.originalHeight) - rect.height) - ((float) rect.offsetY))) + "\n");
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("  index: ");
                    sb3.append(imageIndex);
                    sb3.append("\n");
                    writer.write(sb3.toString());
                    FileHandle fileHandle2 = file;
                    PixmapPacker pixmapPacker2 = packer;
                    i2 = 1;
                }
            }
            fileHandle = file;
            pixmapPacker = packer;
        }
        writer.close();
    }

    /* renamed from: com.badlogic.gdx.graphics.g2d.PixmapPackerIO$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat = new int[ImageFormat.values().length];

        static {
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat[ImageFormat.CIM.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat[ImageFormat.PNG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
