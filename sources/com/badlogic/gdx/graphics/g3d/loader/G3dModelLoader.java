package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.twi.game.BuildConfig;
import java.util.Iterator;

public class G3dModelLoader extends ModelLoader<ModelLoader.ModelParameters> {
    public static final short VERSION_HI = 0;
    public static final short VERSION_LO = 1;
    protected final BaseJsonReader reader;
    protected final Quaternion tempQ;

    public G3dModelLoader(BaseJsonReader reader2) {
        this(reader2, (FileHandleResolver) null);
    }

    public G3dModelLoader(BaseJsonReader reader2, FileHandleResolver resolver) {
        super(resolver);
        this.tempQ = new Quaternion();
        this.reader = reader2;
    }

    public ModelData loadModelData(FileHandle fileHandle, ModelLoader.ModelParameters parameters) {
        return parseModel(fileHandle);
    }

    public ModelData parseModel(FileHandle handle) {
        JsonValue json = this.reader.parse(handle);
        ModelData model = new ModelData();
        JsonValue version = json.require("version");
        model.version[0] = version.getShort(0);
        model.version[1] = version.getShort(1);
        if (model.version[0] == 0 && model.version[1] == 1) {
            model.id = json.getString("id", BuildConfig.FLAVOR);
            parseMeshes(model, json);
            parseMaterials(model, json, handle.parent().path());
            parseNodes(model, json);
            parseAnimations(model, json);
            return model;
        }
        throw new GdxRuntimeException("Model version not supported");
    }

    /* access modifiers changed from: protected */
    public void parseMeshes(ModelData model, JsonValue json) {
        ModelData modelData = model;
        JsonValue meshes = json.get("meshes");
        if (meshes != null) {
            modelData.meshes.ensureCapacity(meshes.size);
            JsonValue mesh = meshes.child;
            while (mesh != null) {
                ModelMesh jsonMesh = new ModelMesh();
                jsonMesh.id = mesh.getString("id", BuildConfig.FLAVOR);
                jsonMesh.attributes = parseAttributes(mesh.require("attributes"));
                jsonMesh.vertices = mesh.require("vertices").asFloatArray();
                JsonValue meshParts = mesh.require("parts");
                Array<ModelMeshPart> parts = new Array<>();
                JsonValue meshPart = meshParts.child;
                while (meshPart != null) {
                    ModelMeshPart jsonPart = new ModelMeshPart();
                    String partId = meshPart.getString("id", (String) null);
                    if (partId != null) {
                        Iterator<ModelMeshPart> it = parts.iterator();
                        while (it.hasNext()) {
                            JsonValue meshes2 = meshes;
                            if (!it.next().id.equals(partId)) {
                                meshes = meshes2;
                            } else {
                                throw new GdxRuntimeException("Mesh part with id '" + partId + "' already in defined");
                            }
                        }
                        JsonValue meshes3 = meshes;
                        jsonPart.id = partId;
                        String type = meshPart.getString("type", (String) null);
                        if (type != null) {
                            jsonPart.primitiveType = parseType(type);
                            jsonPart.indices = meshPart.require("indices").asShortArray();
                            parts.add(jsonPart);
                            meshPart = meshPart.next;
                            meshes = meshes3;
                        } else {
                            throw new GdxRuntimeException("No primitive type given for mesh part '" + partId + "'");
                        }
                    } else {
                        throw new GdxRuntimeException("Not id given for mesh part");
                    }
                }
                jsonMesh.parts = (ModelMeshPart[]) parts.toArray(ModelMeshPart.class);
                modelData.meshes.add(jsonMesh);
                mesh = mesh.next;
                meshes = meshes;
            }
            return;
        }
    }

    /* access modifiers changed from: protected */
    public int parseType(String type) {
        if (type.equals("TRIANGLES")) {
            return 4;
        }
        if (type.equals("LINES")) {
            return 1;
        }
        if (type.equals("POINTS")) {
            return 0;
        }
        if (type.equals("TRIANGLE_STRIP")) {
            return 5;
        }
        if (type.equals("LINE_STRIP")) {
            return 3;
        }
        throw new GdxRuntimeException("Unknown primitive type '" + type + "', should be one of triangle, trianglestrip, line, linestrip, lineloop or point");
    }

    /* access modifiers changed from: protected */
    public VertexAttribute[] parseAttributes(JsonValue attributes) {
        Array<VertexAttribute> vertexAttributes = new Array<>();
        int unit = 0;
        int blendWeightCount = 0;
        for (JsonValue value = attributes.child; value != null; value = value.next) {
            String attr = value.asString();
            if (attr.equals("POSITION")) {
                vertexAttributes.add(VertexAttribute.Position());
            } else if (attr.equals("NORMAL")) {
                vertexAttributes.add(VertexAttribute.Normal());
            } else if (attr.equals("COLOR")) {
                vertexAttributes.add(VertexAttribute.ColorUnpacked());
            } else if (attr.equals("COLORPACKED")) {
                vertexAttributes.add(VertexAttribute.ColorPacked());
            } else if (attr.equals("TANGENT")) {
                vertexAttributes.add(VertexAttribute.Tangent());
            } else if (attr.equals("BINORMAL")) {
                vertexAttributes.add(VertexAttribute.Binormal());
            } else if (attr.startsWith("TEXCOORD")) {
                vertexAttributes.add(VertexAttribute.TexCoords(unit));
                unit++;
            } else if (attr.startsWith("BLENDWEIGHT")) {
                vertexAttributes.add(VertexAttribute.BoneWeight(blendWeightCount));
                blendWeightCount++;
            } else {
                throw new GdxRuntimeException("Unknown vertex attribute '" + attr + "', should be one of position, normal, uv, tangent or binormal");
            }
        }
        return (VertexAttribute[]) vertexAttributes.toArray(VertexAttribute.class);
    }

    /* access modifiers changed from: protected */
    public void parseMaterials(ModelData model, JsonValue json, String materialDir) {
        G3dModelLoader g3dModelLoader = this;
        ModelData modelData = model;
        String str = materialDir;
        JsonValue materials = json.get("materials");
        if (materials == null) {
            JsonValue jsonValue = materials;
            return;
        }
        modelData.materials.ensureCapacity(materials.size);
        JsonValue material = materials.child;
        while (material != null) {
            ModelMaterial jsonMaterial = new ModelMaterial();
            String str2 = "id";
            String id = material.getString(str2, (String) null);
            if (id != null) {
                jsonMaterial.id = id;
                JsonValue diffuse = material.get("diffuse");
                if (diffuse != null) {
                    jsonMaterial.diffuse = g3dModelLoader.parseColor(diffuse);
                }
                JsonValue ambient = material.get("ambient");
                if (ambient != null) {
                    jsonMaterial.ambient = g3dModelLoader.parseColor(ambient);
                }
                JsonValue emissive = material.get("emissive");
                if (emissive != null) {
                    jsonMaterial.emissive = g3dModelLoader.parseColor(emissive);
                }
                JsonValue specular = material.get("specular");
                if (specular != null) {
                    jsonMaterial.specular = g3dModelLoader.parseColor(specular);
                }
                JsonValue reflection = material.get("reflection");
                if (reflection != null) {
                    jsonMaterial.reflection = g3dModelLoader.parseColor(reflection);
                }
                jsonMaterial.shininess = material.getFloat(FloatAttribute.ShininessAlias, 0.0f);
                jsonMaterial.opacity = material.getFloat("opacity", 1.0f);
                JsonValue textures = material.get("textures");
                if (textures != null) {
                    JsonValue texture = textures.child;
                    while (texture != null) {
                        ModelTexture jsonTexture = new ModelTexture();
                        JsonValue materials2 = materials;
                        String textureId = texture.getString(str2, (String) null);
                        if (textureId != null) {
                            ModelTexture jsonTexture2 = jsonTexture;
                            jsonTexture2.id = textureId;
                            String str3 = textureId;
                            String str4 = str2;
                            String fileName = texture.getString("filename", (String) null);
                            if (fileName != null) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(str);
                                String id2 = id;
                                String id3 = "/";
                                if (materialDir.length() == 0 || str.endsWith(id3)) {
                                    id3 = BuildConfig.FLAVOR;
                                }
                                sb.append(id3);
                                sb.append(fileName);
                                jsonTexture2.fileName = sb.toString();
                                jsonTexture2.uvTranslation = g3dModelLoader.readVector2(texture.get("uvTranslation"), 0.0f, 0.0f);
                                jsonTexture2.uvScaling = g3dModelLoader.readVector2(texture.get("uvScaling"), 1.0f, 1.0f);
                                String textureType = texture.getString("type", (String) null);
                                if (textureType != null) {
                                    jsonTexture2.usage = g3dModelLoader.parseTextureUsage(textureType);
                                    if (jsonMaterial.textures == null) {
                                        jsonMaterial.textures = new Array<>();
                                    }
                                    jsonMaterial.textures.add(jsonTexture2);
                                    texture = texture.next;
                                    JsonValue jsonValue2 = json;
                                    materials = materials2;
                                    str2 = str4;
                                    id = id2;
                                } else {
                                    throw new GdxRuntimeException("Texture needs type.");
                                }
                            } else {
                                throw new GdxRuntimeException("Texture needs filename.");
                            }
                        } else {
                            ModelTexture modelTexture = jsonTexture;
                            String str5 = textureId;
                            throw new GdxRuntimeException("Texture has no id.");
                        }
                    }
                    continue;
                }
                String str6 = id;
                modelData.materials.add(jsonMaterial);
                material = material.next;
                g3dModelLoader = this;
                JsonValue jsonValue3 = json;
                materials = materials;
            } else {
                String str7 = id;
                throw new GdxRuntimeException("Material needs an id.");
            }
        }
    }

    /* access modifiers changed from: protected */
    public int parseTextureUsage(String value) {
        if (value.equalsIgnoreCase("AMBIENT")) {
            return 4;
        }
        if (value.equalsIgnoreCase("BUMP")) {
            return 8;
        }
        if (value.equalsIgnoreCase("DIFFUSE")) {
            return 2;
        }
        if (value.equalsIgnoreCase("EMISSIVE")) {
            return 3;
        }
        if (value.equalsIgnoreCase("NONE")) {
            return 1;
        }
        if (value.equalsIgnoreCase("NORMAL")) {
            return 7;
        }
        if (value.equalsIgnoreCase("REFLECTION")) {
            return 10;
        }
        if (value.equalsIgnoreCase("SHININESS")) {
            return 6;
        }
        if (value.equalsIgnoreCase("SPECULAR")) {
            return 5;
        }
        if (value.equalsIgnoreCase("TRANSPARENCY")) {
            return 9;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public Color parseColor(JsonValue colorArray) {
        if (colorArray.size >= 3) {
            return new Color(colorArray.getFloat(0), colorArray.getFloat(1), colorArray.getFloat(2), 1.0f);
        }
        throw new GdxRuntimeException("Expected Color values <> than three.");
    }

    /* access modifiers changed from: protected */
    public Vector2 readVector2(JsonValue vectorArray, float x, float y) {
        if (vectorArray == null) {
            return new Vector2(x, y);
        }
        if (vectorArray.size == 2) {
            return new Vector2(vectorArray.getFloat(0), vectorArray.getFloat(1));
        }
        throw new GdxRuntimeException("Expected Vector2 values <> than two.");
    }

    /* access modifiers changed from: protected */
    public Array<ModelNode> parseNodes(ModelData model, JsonValue json) {
        JsonValue nodes = json.get("nodes");
        if (nodes != null) {
            model.nodes.ensureCapacity(nodes.size);
            for (JsonValue node = nodes.child; node != null; node = node.next) {
                model.nodes.add(parseNodesRecursively(node));
            }
        }
        return model.nodes;
    }

    /* access modifiers changed from: protected */
    public ModelNode parseNodesRecursively(JsonValue json) {
        Vector3 vector3;
        Quaternion quaternion;
        String str;
        String id;
        String str2;
        JsonValue rotation;
        JsonValue materials;
        JsonValue scale;
        String id2;
        JsonValue bones;
        String str3;
        String str4;
        G3dModelLoader g3dModelLoader = this;
        JsonValue jsonValue = json;
        ModelNode jsonNode = new ModelNode();
        String id3 = jsonValue.getString("id", (String) null);
        if (id3 != null) {
            jsonNode.id = id3;
            String str5 = "translation";
            JsonValue translation = jsonValue.get(str5);
            if (translation == null || translation.size == 3) {
                if (translation == null) {
                    vector3 = null;
                } else {
                    vector3 = new Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2));
                }
                jsonNode.translation = vector3;
                String str6 = "rotation";
                JsonValue rotation2 = jsonValue.get(str6);
                if (rotation2 == null || rotation2.size == 4) {
                    if (rotation2 == null) {
                        quaternion = null;
                    } else {
                        quaternion = new Quaternion(rotation2.getFloat(0), rotation2.getFloat(1), rotation2.getFloat(2), rotation2.getFloat(3));
                    }
                    jsonNode.rotation = quaternion;
                    JsonValue scale2 = jsonValue.get("scale");
                    if (scale2 == null || scale2.size == 3) {
                        jsonNode.scale = scale2 == null ? null : new Vector3(scale2.getFloat(0), scale2.getFloat(1), scale2.getFloat(2));
                        String meshId = jsonValue.getString("mesh", (String) null);
                        if (meshId != null) {
                            jsonNode.meshId = meshId;
                        }
                        JsonValue materials2 = jsonValue.get("parts");
                        if (materials2 != null) {
                            jsonNode.parts = new ModelNodePart[materials2.size];
                            int i = 0;
                            JsonValue material = materials2.child;
                            while (material != null) {
                                ModelNodePart nodePart = new ModelNodePart();
                                JsonValue translation2 = translation;
                                String meshPartId = material.getString("meshpartid", (String) null);
                                String meshId2 = meshId;
                                String materialId = material.getString("materialid", (String) null);
                                if (meshPartId == null || materialId == null) {
                                    String str7 = materialId;
                                    JsonValue jsonValue2 = scale2;
                                    JsonValue jsonValue3 = materials2;
                                    String str8 = meshPartId;
                                    JsonValue jsonValue4 = rotation2;
                                    throw new GdxRuntimeException("Node " + id3 + " part is missing meshPartId or materialId");
                                }
                                nodePart.materialId = materialId;
                                nodePart.meshPartId = meshPartId;
                                JsonValue bones2 = material.get("bones");
                                if (bones2 != null) {
                                    String str9 = materialId;
                                    scale = scale2;
                                    materials = materials2;
                                    String str10 = meshPartId;
                                    rotation = rotation2;
                                    nodePart.bones = new ArrayMap<>(true, bones2.size, String.class, Matrix4.class);
                                    int j = 0;
                                    JsonValue bone = bones2.child;
                                    while (bone != null) {
                                        String nodeId = bone.getString("node", (String) null);
                                        if (nodeId != null) {
                                            Matrix4 transform = new Matrix4();
                                            JsonValue val = bone.get(str5);
                                            if (val != null) {
                                                str3 = str5;
                                                bones = bones2;
                                                if (val.size >= 3) {
                                                    id2 = id3;
                                                    transform.translate(val.getFloat(0), val.getFloat(1), val.getFloat(2));
                                                } else {
                                                    id2 = id3;
                                                }
                                            } else {
                                                id2 = id3;
                                                str3 = str5;
                                                bones = bones2;
                                            }
                                            JsonValue val2 = bone.get(str6);
                                            if (val2 == null || val2.size < 4) {
                                                str4 = str6;
                                            } else {
                                                str4 = str6;
                                                transform.rotate(g3dModelLoader.tempQ.set(val2.getFloat(0), val2.getFloat(1), val2.getFloat(2), val2.getFloat(3)));
                                            }
                                            JsonValue val3 = bone.get("scale");
                                            if (val3 != null) {
                                                if (val3.size >= 3) {
                                                    transform.scale(val3.getFloat(0), val3.getFloat(1), val3.getFloat(2));
                                                    nodePart.bones.put(nodeId, transform);
                                                    bone = bone.next;
                                                    j++;
                                                    g3dModelLoader = this;
                                                    JsonValue jsonValue5 = json;
                                                    str5 = str3;
                                                    bones2 = bones;
                                                    id3 = id2;
                                                    str6 = str4;
                                                }
                                            }
                                            nodePart.bones.put(nodeId, transform);
                                            bone = bone.next;
                                            j++;
                                            g3dModelLoader = this;
                                            JsonValue jsonValue52 = json;
                                            str5 = str3;
                                            bones2 = bones;
                                            id3 = id2;
                                            str6 = str4;
                                        } else {
                                            JsonValue jsonValue6 = bones2;
                                            throw new GdxRuntimeException("Bone node ID missing");
                                        }
                                    }
                                    id = id3;
                                    str2 = str5;
                                    JsonValue jsonValue7 = bones2;
                                    str = str6;
                                } else {
                                    id = id3;
                                    str2 = str5;
                                    JsonValue jsonValue8 = bones2;
                                    String str11 = materialId;
                                    scale = scale2;
                                    materials = materials2;
                                    String str12 = meshPartId;
                                    str = str6;
                                    rotation = rotation2;
                                }
                                jsonNode.parts[i] = nodePart;
                                material = material.next;
                                i++;
                                g3dModelLoader = this;
                                JsonValue jsonValue9 = json;
                                translation = translation2;
                                meshId = meshId2;
                                scale2 = scale;
                                materials2 = materials;
                                rotation2 = rotation;
                                str5 = str2;
                                id3 = id;
                                str6 = str;
                            }
                            JsonValue jsonValue10 = translation;
                            String str13 = meshId;
                            JsonValue jsonValue11 = scale2;
                            JsonValue jsonValue12 = materials2;
                            JsonValue jsonValue13 = rotation2;
                        } else {
                            JsonValue jsonValue14 = translation;
                            String str14 = meshId;
                            JsonValue jsonValue15 = scale2;
                            JsonValue jsonValue16 = materials2;
                            JsonValue jsonValue17 = rotation2;
                        }
                        JsonValue children = json.get("children");
                        if (children != null) {
                            jsonNode.children = new ModelNode[children.size];
                            int i2 = 0;
                            JsonValue child = children.child;
                            while (child != null) {
                                jsonNode.children[i2] = parseNodesRecursively(child);
                                child = child.next;
                                i2++;
                            }
                        }
                        return jsonNode;
                    }
                    throw new GdxRuntimeException("Node scale incomplete");
                }
                throw new GdxRuntimeException("Node rotation incomplete");
            }
            throw new GdxRuntimeException("Node translation incomplete");
        }
        G3dModelLoader g3dModelLoader2 = g3dModelLoader;
        String str15 = id3;
        throw new GdxRuntimeException("Node id missing.");
    }

    /* access modifiers changed from: protected */
    public void parseAnimations(ModelData model, JsonValue json) {
        JsonValue animations;
        ModelAnimation animation;
        JsonValue nodes;
        JsonValue animations2;
        ModelAnimation animation2;
        JsonValue nodes2;
        JsonValue animations3;
        JsonValue keyframes;
        ModelData modelData = model;
        JsonValue animations4 = json.get("animations");
        if (animations4 != null) {
            modelData.animations.ensureCapacity(animations4.size);
            JsonValue anim = animations4.child;
            while (anim != null) {
                JsonValue nodes3 = anim.get("bones");
                if (nodes3 == null) {
                    animations = animations4;
                } else {
                    ModelAnimation animation3 = new ModelAnimation();
                    modelData.animations.add(animation3);
                    animation3.nodeAnimations.ensureCapacity(nodes3.size);
                    animation3.id = anim.getString("id");
                    JsonValue node = nodes3.child;
                    while (node != null) {
                        ModelNodeAnimation nodeAnim = new ModelNodeAnimation();
                        animation3.nodeAnimations.add(nodeAnim);
                        nodeAnim.nodeId = node.getString("boneId");
                        JsonValue keyframes2 = node.get("keyframes");
                        float f = 1000.0f;
                        float f2 = 0.0f;
                        int i = 3;
                        if (keyframes2 == null || !keyframes2.isArray()) {
                            animations2 = animations4;
                            nodes = nodes3;
                            animation = animation3;
                            JsonValue jsonValue = keyframes2;
                            JsonValue translationKF = node.get("translation");
                            if (translationKF != null && translationKF.isArray()) {
                                nodeAnim.translation = new Array<>();
                                nodeAnim.translation.ensureCapacity(translationKF.size);
                                for (JsonValue keyframe = translationKF.child; keyframe != null; keyframe = keyframe.next) {
                                    ModelNodeKeyframe<Vector3> kf = new ModelNodeKeyframe<>();
                                    nodeAnim.translation.add(kf);
                                    kf.keytime = keyframe.getFloat("keytime", 0.0f) / 1000.0f;
                                    JsonValue translation = keyframe.get("value");
                                    if (translation != null && translation.size >= 3) {
                                        kf.value = new Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2));
                                    }
                                }
                            }
                            JsonValue rotationKF = node.get("rotation");
                            if (rotationKF != null && rotationKF.isArray()) {
                                nodeAnim.rotation = new Array<>();
                                nodeAnim.rotation.ensureCapacity(rotationKF.size);
                                for (JsonValue keyframe2 = rotationKF.child; keyframe2 != null; keyframe2 = keyframe2.next) {
                                    ModelNodeKeyframe<Quaternion> kf2 = new ModelNodeKeyframe<>();
                                    nodeAnim.rotation.add(kf2);
                                    kf2.keytime = keyframe2.getFloat("keytime", 0.0f) / 1000.0f;
                                    JsonValue rotation = keyframe2.get("value");
                                    if (rotation != null && rotation.size >= 4) {
                                        kf2.value = new Quaternion(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3));
                                    }
                                }
                            }
                            JsonValue scalingKF = node.get("scaling");
                            if (scalingKF != null && scalingKF.isArray()) {
                                nodeAnim.scaling = new Array<>();
                                nodeAnim.scaling.ensureCapacity(scalingKF.size);
                                for (JsonValue keyframe3 = scalingKF.child; keyframe3 != null; keyframe3 = keyframe3.next) {
                                    ModelNodeKeyframe<Vector3> kf3 = new ModelNodeKeyframe<>();
                                    nodeAnim.scaling.add(kf3);
                                    kf3.keytime = keyframe3.getFloat("keytime", 0.0f) / 1000.0f;
                                    JsonValue scaling = keyframe3.get("value");
                                    if (scaling != null && scaling.size >= 3) {
                                        kf3.value = new Vector3(scaling.getFloat(0), scaling.getFloat(1), scaling.getFloat(2));
                                    }
                                }
                            }
                        } else {
                            JsonValue keyframe4 = keyframes2.child;
                            while (keyframe4 != null) {
                                float keytime = keyframe4.getFloat("keytime", f2) / f;
                                JsonValue translation2 = keyframe4.get("translation");
                                if (translation2 == null || translation2.size != i) {
                                    animations3 = animations4;
                                    nodes2 = nodes3;
                                    animation2 = animation3;
                                } else {
                                    if (nodeAnim.translation == null) {
                                        nodeAnim.translation = new Array<>();
                                    }
                                    ModelNodeKeyframe<Vector3> tkf = new ModelNodeKeyframe<>();
                                    tkf.keytime = keytime;
                                    animations3 = animations4;
                                    nodes2 = nodes3;
                                    animation2 = animation3;
                                    tkf.value = new Vector3(translation2.getFloat(0), translation2.getFloat(1), translation2.getFloat(2));
                                    nodeAnim.translation.add(tkf);
                                }
                                JsonValue rotation2 = keyframe4.get("rotation");
                                if (rotation2 == null || rotation2.size != 4) {
                                    keyframes = keyframes2;
                                    JsonValue jsonValue2 = translation2;
                                } else {
                                    if (nodeAnim.rotation == null) {
                                        nodeAnim.rotation = new Array<>();
                                    }
                                    ModelNodeKeyframe<Quaternion> rkf = new ModelNodeKeyframe<>();
                                    rkf.keytime = keytime;
                                    keyframes = keyframes2;
                                    JsonValue jsonValue3 = translation2;
                                    rkf.value = new Quaternion(rotation2.getFloat(0), rotation2.getFloat(1), rotation2.getFloat(2), rotation2.getFloat(3));
                                    nodeAnim.rotation.add(rkf);
                                }
                                JsonValue scale = keyframe4.get("scale");
                                if (scale != null && scale.size == 3) {
                                    if (nodeAnim.scaling == null) {
                                        nodeAnim.scaling = new Array<>();
                                    }
                                    ModelNodeKeyframe<Vector3> skf = new ModelNodeKeyframe<>();
                                    skf.keytime = keytime;
                                    skf.value = new Vector3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
                                    nodeAnim.scaling.add(skf);
                                }
                                keyframe4 = keyframe4.next;
                                ModelData modelData2 = model;
                                JsonValue jsonValue4 = json;
                                animations4 = animations3;
                                nodes3 = nodes2;
                                animation3 = animation2;
                                keyframes2 = keyframes;
                                i = 3;
                                f = 1000.0f;
                                f2 = 0.0f;
                            }
                            animations2 = animations4;
                            nodes = nodes3;
                            animation = animation3;
                            JsonValue jsonValue5 = keyframes2;
                        }
                        node = node.next;
                        ModelData modelData3 = model;
                        JsonValue jsonValue6 = json;
                        animations4 = animations2;
                        nodes3 = nodes;
                        animation3 = animation;
                    }
                    animations = animations4;
                    JsonValue jsonValue7 = nodes3;
                    ModelAnimation modelAnimation = animation3;
                }
                anim = anim.next;
                modelData = model;
                JsonValue jsonValue8 = json;
                animations4 = animations;
            }
        }
    }
}
