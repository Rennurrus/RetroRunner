package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.StreamUtils;
import com.twi.game.BuildConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ObjLoader extends ModelLoader<ObjLoaderParameters> {
    public static boolean logWarning = false;
    final Array<Group> groups;
    final FloatArray norms;
    final FloatArray uvs;
    final FloatArray verts;

    public static class ObjLoaderParameters extends ModelLoader.ModelParameters {
        public boolean flipV;

        public ObjLoaderParameters() {
        }

        public ObjLoaderParameters(boolean flipV2) {
            this.flipV = flipV2;
        }
    }

    public ObjLoader() {
        this((FileHandleResolver) null);
    }

    public ObjLoader(FileHandleResolver resolver) {
        super(resolver);
        this.verts = new FloatArray((int) HttpStatus.SC_MULTIPLE_CHOICES);
        this.norms = new FloatArray((int) HttpStatus.SC_MULTIPLE_CHOICES);
        this.uvs = new FloatArray((int) HttpStatus.SC_OK);
        this.groups = new Array<>(10);
    }

    public Model loadModel(FileHandle fileHandle, boolean flipV) {
        return loadModel(fileHandle, new ObjLoaderParameters(flipV));
    }

    public ModelData loadModelData(FileHandle file, ObjLoaderParameters parameters) {
        return loadModelData(file, parameters != null && parameters.flipV);
    }

    /* access modifiers changed from: protected */
    public ModelData loadModelData(FileHandle file, boolean flipV) {
        String line;
        String nodeId;
        String meshId;
        String str;
        String partId;
        if (logWarning) {
            Gdx.app.error("ObjLoader", "Wavefront (OBJ) is not fully supported, consult the documentation for more information");
        }
        MtlLoader mtl = new MtlLoader();
        String str2 = "default";
        Group activeGroup = new Group(str2);
        this.groups.add(activeGroup);
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), StreamUtils.DEFAULT_BUFFER_SIZE);
        int id = 0;
        while (true) {
            try {
                String readLine = reader.readLine();
                line = readLine;
                char c = 0;
                char c2 = 1;
                if (readLine != null) {
                    try {
                        String[] tokens = line.split("\\s+");
                        if (tokens.length < 1) {
                            break;
                        } else if (tokens[0].length() != 0) {
                            char charAt = tokens[0].toLowerCase().charAt(0);
                            char firstChar = charAt;
                            if (charAt != '#') {
                                char firstChar2 = firstChar;
                                if (firstChar2 == 'v') {
                                    if (tokens[0].length() == 1) {
                                        this.verts.add(Float.parseFloat(tokens[1]));
                                        this.verts.add(Float.parseFloat(tokens[2]));
                                        this.verts.add(Float.parseFloat(tokens[3]));
                                    } else if (tokens[0].charAt(1) == 'n') {
                                        this.norms.add(Float.parseFloat(tokens[1]));
                                        this.norms.add(Float.parseFloat(tokens[2]));
                                        this.norms.add(Float.parseFloat(tokens[3]));
                                    } else if (tokens[0].charAt(1) == 't') {
                                        this.uvs.add(Float.parseFloat(tokens[1]));
                                        this.uvs.add(flipV ? 1.0f - Float.parseFloat(tokens[2]) : Float.parseFloat(tokens[2]));
                                    }
                                } else if (firstChar2 == 'f') {
                                    Array<Integer> faces = activeGroup.faces;
                                    int i = 1;
                                    while (i < tokens.length - 2) {
                                        String[] parts = tokens[c2].split("/");
                                        faces.add(Integer.valueOf(getIndex(parts[c], this.verts.size)));
                                        if (parts.length > 2) {
                                            if (i == 1) {
                                                activeGroup.hasNorms = true;
                                            }
                                            faces.add(Integer.valueOf(getIndex(parts[2], this.norms.size)));
                                        }
                                        if (parts.length > 1 && parts[1].length() > 0) {
                                            if (i == 1) {
                                                activeGroup.hasUVs = true;
                                            }
                                            faces.add(Integer.valueOf(getIndex(parts[1], this.uvs.size)));
                                        }
                                        int i2 = i + 1;
                                        String[] parts2 = tokens[i2].split("/");
                                        faces.add(Integer.valueOf(getIndex(parts2[0], this.verts.size)));
                                        if (parts2.length > 2) {
                                            faces.add(Integer.valueOf(getIndex(parts2[2], this.norms.size)));
                                        }
                                        if (parts2.length > 1 && parts2[1].length() > 0) {
                                            faces.add(Integer.valueOf(getIndex(parts2[1], this.uvs.size)));
                                        }
                                        int i3 = i2 + 1;
                                        String[] parts3 = tokens[i3].split("/");
                                        faces.add(Integer.valueOf(getIndex(parts3[0], this.verts.size)));
                                        if (parts3.length > 2) {
                                            faces.add(Integer.valueOf(getIndex(parts3[2], this.norms.size)));
                                        }
                                        if (parts3.length > 1 && parts3[1].length() > 0) {
                                            faces.add(Integer.valueOf(getIndex(parts3[1], this.uvs.size)));
                                        }
                                        activeGroup.numFaces++;
                                        i = i3 - 1;
                                        c = 0;
                                        c2 = 1;
                                    }
                                } else {
                                    if (firstChar2 != 'o') {
                                        if (firstChar2 != 'g') {
                                            if (tokens[0].equals("mtllib")) {
                                                mtl.load(file.parent().child(tokens[1]));
                                            } else if (tokens[0].equals("usemtl")) {
                                                if (tokens.length == 1) {
                                                    activeGroup.materialName = str2;
                                                } else {
                                                    activeGroup.materialName = tokens[1].replace('.', '_');
                                                }
                                            }
                                        }
                                    }
                                    if (tokens.length > 1) {
                                        activeGroup = setActiveGroup(tokens[1]);
                                    } else {
                                        activeGroup = setActiveGroup(str2);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        Group group = activeGroup;
                        BufferedReader bufferedReader = reader;
                        return null;
                    }
                }
            } catch (IOException e2) {
                Group group2 = activeGroup;
                BufferedReader bufferedReader2 = reader;
                return null;
            }
        }
        reader.close();
        int i4 = 0;
        while (i4 < this.groups.size) {
            if (this.groups.get(i4).numFaces < 1) {
                this.groups.removeIndex(i4);
                i4--;
            }
            i4++;
        }
        if (this.groups.size < 1) {
            return null;
        }
        int numGroups = this.groups.size;
        ModelData data = new ModelData();
        int g = 0;
        while (g < numGroups) {
            Group group3 = this.groups.get(g);
            Array<Integer> faces2 = group3.faces;
            int numElements = faces2.size;
            int numFaces = group3.numFaces;
            boolean hasNorms = group3.hasNorms;
            boolean hasUVs = group3.hasUVs;
            int numGroups2 = numGroups;
            float[] finalVerts = new float[(numFaces * 3 * ((hasNorms ? 3 : 0) + 3 + (hasUVs ? 2 : 0)))];
            int i5 = 0;
            Group activeGroup2 = activeGroup;
            int uvIndex = 0;
            while (uvIndex < numElements) {
                BufferedReader reader2 = reader;
                int vi = uvIndex + 1;
                int vertIndex = faces2.get(uvIndex).intValue() * 3;
                int vi2 = i5 + 1;
                String line2 = line;
                int numElements2 = numElements;
                int numElements3 = vertIndex + 1;
                finalVerts[i5] = this.verts.get(vertIndex);
                int vi3 = vi2 + 1;
                int g2 = g;
                int vertIndex2 = numElements3 + 1;
                finalVerts[vi2] = this.verts.get(numElements3);
                int normIndex = vi3 + 1;
                finalVerts[vi3] = this.verts.get(vertIndex2);
                if (hasNorms) {
                    int i6 = vi + 1;
                    int normIndex2 = faces2.get(vi).intValue() * 3;
                    int vi4 = normIndex + 1;
                    int i7 = i6;
                    int i8 = vertIndex2;
                    int normIndex3 = normIndex2 + 1;
                    finalVerts[normIndex] = this.norms.get(normIndex2);
                    int vi5 = vi4 + 1;
                    finalVerts[vi4] = this.norms.get(normIndex3);
                    finalVerts[vi5] = this.norms.get(normIndex3 + 1);
                    normIndex = vi5 + 1;
                    vi = i7;
                }
                if (hasUVs) {
                    int i9 = vi + 1;
                    int uvIndex2 = faces2.get(vi).intValue() * 2;
                    int vi6 = normIndex + 1;
                    finalVerts[normIndex] = this.uvs.get(uvIndex2);
                    finalVerts[vi6] = this.uvs.get(uvIndex2 + 1);
                    uvIndex = i9;
                    i5 = vi6 + 1;
                } else {
                    uvIndex = vi;
                    i5 = normIndex;
                }
                reader = reader2;
                line = line2;
                numElements = numElements2;
                g = g2;
            }
            BufferedReader reader3 = reader;
            int g3 = g;
            String line3 = line;
            int i10 = numElements;
            int numIndices = numFaces * 3 >= 32767 ? 0 : numFaces * 3;
            short[] finalIndices = new short[numIndices];
            if (numIndices > 0) {
                for (int i11 = 0; i11 < numIndices; i11++) {
                    finalIndices[i11] = (short) i11;
                }
            }
            Array<VertexAttribute> attributes = new Array<>();
            int i12 = numIndices;
            Array<Integer> array = faces2;
            attributes.add(new VertexAttribute(1, 3, ShaderProgram.POSITION_ATTRIBUTE));
            if (hasNorms) {
                attributes.add(new VertexAttribute(8, 3, ShaderProgram.NORMAL_ATTRIBUTE));
            }
            if (hasUVs) {
                attributes.add(new VertexAttribute(16, 2, "a_texCoord0"));
            }
            int id2 = id + 1;
            String stringId = Integer.toString(id2);
            if (str2.equals(group3.name)) {
                nodeId = "node" + stringId;
            } else {
                nodeId = group3.name;
            }
            if (str2.equals(group3.name)) {
                meshId = "mesh" + stringId;
            } else {
                meshId = group3.name;
            }
            if (str2.equals(group3.name)) {
                StringBuilder sb = new StringBuilder();
                str = str2;
                sb.append("part");
                sb.append(stringId);
                partId = sb.toString();
            } else {
                str = str2;
                partId = group3.name;
            }
            ModelNode node = new ModelNode();
            node.id = nodeId;
            node.meshId = meshId;
            int id3 = id2;
            String str3 = stringId;
            node.scale = new Vector3(1.0f, 1.0f, 1.0f);
            node.translation = new Vector3();
            node.rotation = new Quaternion();
            ModelNodePart pm = new ModelNodePart();
            pm.meshPartId = partId;
            pm.materialId = group3.materialName;
            boolean z = hasUVs;
            node.parts = new ModelNodePart[]{pm};
            ModelMeshPart part = new ModelMeshPart();
            part.id = partId;
            part.indices = finalIndices;
            part.primitiveType = 4;
            ModelMesh mesh = new ModelMesh();
            mesh.id = meshId;
            String str4 = meshId;
            mesh.attributes = (VertexAttribute[]) attributes.toArray(VertexAttribute.class);
            mesh.vertices = finalVerts;
            float[] fArr = finalVerts;
            mesh.parts = new ModelMeshPart[]{part};
            data.nodes.add(node);
            data.meshes.add(mesh);
            data.materials.add(mtl.getMaterial(group3.materialName));
            g = g3 + 1;
            reader = reader3;
            numGroups = numGroups2;
            activeGroup = activeGroup2;
            line = line3;
            str2 = str;
            id = id3;
        }
        Group group4 = activeGroup;
        BufferedReader bufferedReader3 = reader;
        int i13 = g;
        String str5 = line;
        if (this.verts.size > 0) {
            this.verts.clear();
        }
        if (this.norms.size > 0) {
            this.norms.clear();
        }
        if (this.uvs.size > 0) {
            this.uvs.clear();
        }
        if (this.groups.size > 0) {
            this.groups.clear();
        }
        return data;
    }

    private Group setActiveGroup(String name) {
        Iterator<Group> it = this.groups.iterator();
        while (it.hasNext()) {
            Group group = it.next();
            if (group.name.equals(name)) {
                return group;
            }
        }
        Group group2 = new Group(name);
        this.groups.add(group2);
        return group2;
    }

    private int getIndex(String index, int size) {
        if (index == null || index.length() == 0) {
            return 0;
        }
        int idx = Integer.parseInt(index);
        if (idx < 0) {
            return size + idx;
        }
        return idx - 1;
    }

    private class Group {
        Array<Integer> faces = new Array<>((int) HttpStatus.SC_OK);
        boolean hasNorms;
        boolean hasUVs;
        Material mat = new Material(BuildConfig.FLAVOR);
        String materialName = "default";
        final String name;
        int numFaces = 0;

        Group(String name2) {
            this.name = name2;
        }
    }
}
