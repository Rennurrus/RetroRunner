package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.JsonValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonReader implements BaseJsonReader {
    private static final byte[] _json_actions = init__json_actions_0();
    private static final byte[] _json_eof_actions = init__json_eof_actions_0();
    private static final short[] _json_index_offsets = init__json_index_offsets_0();
    private static final byte[] _json_indicies = init__json_indicies_0();
    private static final short[] _json_key_offsets = init__json_key_offsets_0();
    private static final byte[] _json_range_lengths = init__json_range_lengths_0();
    private static final byte[] _json_single_lengths = init__json_single_lengths_0();
    private static final byte[] _json_trans_actions = init__json_trans_actions_0();
    private static final char[] _json_trans_keys = init__json_trans_keys_0();
    private static final byte[] _json_trans_targs = init__json_trans_targs_0();
    static final int json_en_array = 23;
    static final int json_en_main = 1;
    static final int json_en_object = 5;
    static final int json_error = 0;
    static final int json_first_final = 35;
    static final int json_start = 1;
    private JsonValue current;
    private final Array<JsonValue> elements = new Array<>(8);
    private final Array<JsonValue> lastChild = new Array<>(8);
    private JsonValue root;

    public JsonValue parse(String json) {
        char[] data = json.toCharArray();
        return parse(data, 0, data.length);
    }

    public JsonValue parse(Reader reader) {
        try {
            char[] data = new char[GL20.GL_STENCIL_BUFFER_BIT];
            int offset = 0;
            while (true) {
                int length = reader.read(data, offset, data.length - offset);
                if (length == -1) {
                    JsonValue parse = parse(data, 0, offset);
                    StreamUtils.closeQuietly(reader);
                    return parse;
                } else if (length == 0) {
                    char[] newData = new char[(data.length * 2)];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    data = newData;
                } else {
                    offset += length;
                }
            }
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(reader);
            throw th;
        }
    }

    public JsonValue parse(InputStream input) {
        try {
            JsonValue parse = parse((Reader) new InputStreamReader(input, "UTF-8"));
            StreamUtils.closeQuietly(input);
            return parse;
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        } catch (Throwable th) {
            StreamUtils.closeQuietly(input);
            throw th;
        }
    }

    public JsonValue parse(FileHandle file) {
        try {
            return parse(file.reader("UTF-8"));
        } catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v0, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v15, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v1, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v2, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v18, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v43, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v19, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v20, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v12, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v20, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v22, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v14, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v15, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v23, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v16, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v17, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v24, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v19, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v26, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v27, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v20, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v21, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v28, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v29, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v22, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v37, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v23, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v30, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v24, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v31, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v33, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v25, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v34, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v26, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v35, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v27, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v37, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v28, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v39, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v29, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v43, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v42, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v30, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v48, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v43, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v31, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v56, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v44, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v32, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v57, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v148, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v95, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v71, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v98, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v51, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v151, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v61, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v62, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v85, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v88, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v89, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v54, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v91, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v93, resolved type: int[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v94, resolved type: com.badlogic.gdx.utils.Array} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v152, resolved type: short} */
    /* JADX WARNING: type inference failed for: r13v21 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r0v58, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r0v6, types: [byte] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x06bb A[SYNTHETIC, Splitter:B:376:0x06bb] */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x075a  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0774  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0784  */
    /* JADX WARNING: Removed duplicated region for block: B:530:0x09f4  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0a49  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0169 A[SYNTHETIC, Splitter:B:58:0x0169] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.badlogic.gdx.utils.JsonValue parse(char[] r38, int r39, int r40) {
        /*
            r37 = this;
            r1 = r37
            r2 = r38
            r0 = r39
            r3 = r40
            r4 = r3
            r5 = 0
            r6 = 4
            int[] r7 = new int[r6]
            r8 = 0
            com.badlogic.gdx.utils.Array r9 = new com.badlogic.gdx.utils.Array
            r10 = 8
            r9.<init>((int) r10)
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            if (r14 == 0) goto L_0x0021
            java.io.PrintStream r15 = java.lang.System.out
            r15.println()
        L_0x0021:
            r15 = 1
            r5 = 0
            r16 = 0
            r17 = 0
            r18 = r12
            r12 = r10
            r10 = r8
            r8 = r5
            r5 = r17
            r17 = r11
            r11 = r7
            r7 = r0
        L_0x0032:
            java.lang.String r6 = "name: "
            r20 = r10
            java.lang.String r10 = "true"
            r21 = r12
            java.lang.String r12 = "double: "
            r22 = r13
            java.lang.String r13 = "boolean: "
            r23 = r12
            java.lang.String r12 = "="
            r24 = r12
            r12 = 1
            if (r5 == 0) goto L_0x0096
            if (r5 == r12) goto L_0x0094
            r12 = 2
            if (r5 == r12) goto L_0x007a
            r12 = 4
            if (r5 == r12) goto L_0x0063
            r33 = r3
            r32 = r4
            r27 = r5
            r31 = r8
            r24 = r9
            r23 = r11
            r10 = r20
            r12 = r21
            goto L_0x09cc
        L_0x0063:
            r32 = r4
            r27 = r5
            r31 = r8
            r35 = r10
            r4 = r13
            r10 = r20
            r12 = r21
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x0798
        L_0x007a:
            r12 = 4
            r33 = r3
            r32 = r4
            r27 = r5
            r31 = r8
            r35 = r10
            r4 = r13
            r10 = r20
            r12 = r21
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x0772
        L_0x0094:
            r12 = 4
            goto L_0x00ad
        L_0x0096:
            r12 = 4
            if (r7 != r3) goto L_0x00a2
            r5 = 4
            r10 = r20
            r12 = r21
            r13 = r22
            r6 = 4
            goto L_0x0032
        L_0x00a2:
            if (r15 != 0) goto L_0x00ad
            r5 = 5
            r10 = r20
            r12 = r21
            r13 = r22
            r6 = 4
            goto L_0x0032
        L_0x00ad:
            short[] r0 = _json_key_offsets     // Catch:{ RuntimeException -> 0x09d0 }
            short r0 = r0[r15]     // Catch:{ RuntimeException -> 0x09d0 }
            short[] r19 = _json_index_offsets     // Catch:{ RuntimeException -> 0x09d0 }
            short r19 = r19[r15]     // Catch:{ RuntimeException -> 0x09d0 }
            r16 = r19
            byte[] r19 = _json_single_lengths     // Catch:{ RuntimeException -> 0x09d0 }
            byte r19 = r19[r15]     // Catch:{ RuntimeException -> 0x09d0 }
            if (r19 <= 0) goto L_0x010d
            r27 = r0
            int r28 = r0 + r19
            r26 = 1
            int r28 = r28 + -1
            r12 = r28
            r36 = r27
            r27 = r5
            r5 = r36
        L_0x00cd:
            if (r12 >= r5) goto L_0x00d4
            int r0 = r0 + r19
            int r16 = r16 + r19
            goto L_0x010f
        L_0x00d4:
            int r28 = r12 - r5
            r26 = 1
            int r28 = r28 >> 1
            int r28 = r5 + r28
            r29 = r5
            char r5 = r2[r7]     // Catch:{ RuntimeException -> 0x0100 }
            char[] r30 = _json_trans_keys     // Catch:{ RuntimeException -> 0x0100 }
            r31 = r12
            char r12 = r30[r28]     // Catch:{ RuntimeException -> 0x0100 }
            if (r5 >= r12) goto L_0x00ed
            int r12 = r28 + -1
            r5 = r29
            goto L_0x00cd
        L_0x00ed:
            char r5 = r2[r7]     // Catch:{ RuntimeException -> 0x0100 }
            char[] r12 = _json_trans_keys     // Catch:{ RuntimeException -> 0x0100 }
            char r12 = r12[r28]     // Catch:{ RuntimeException -> 0x0100 }
            if (r5 <= r12) goto L_0x00fa
            int r5 = r28 + 1
            r12 = r31
            goto L_0x00cd
        L_0x00fa:
            int r5 = r28 - r0
            int r16 = r16 + r5
            r5 = r0
            goto L_0x0158
        L_0x0100:
            r0 = move-exception
            r33 = r3
            r32 = r4
            r24 = r9
            r10 = r20
            r12 = r21
            goto L_0x09df
        L_0x010d:
            r27 = r5
        L_0x010f:
            byte[] r5 = _json_range_lengths     // Catch:{ RuntimeException -> 0x09d0 }
            byte r5 = r5[r15]     // Catch:{ RuntimeException -> 0x09d0 }
            r19 = r5
            if (r19 <= 0) goto L_0x0157
            r5 = r0
            int r12 = r19 << 1
            int r12 = r12 + r0
            r25 = 2
            int r12 = r12 + -2
        L_0x011f:
            if (r12 >= r5) goto L_0x0125
            int r16 = r16 + r19
            r5 = r0
            goto L_0x0158
        L_0x0125:
            int r28 = r12 - r5
            r26 = 1
            int r28 = r28 >> 1
            r28 = r28 & -2
            int r28 = r5 + r28
            r29 = r5
            char r5 = r2[r7]     // Catch:{ RuntimeException -> 0x0100 }
            char[] r30 = _json_trans_keys     // Catch:{ RuntimeException -> 0x0100 }
            r31 = r12
            char r12 = r30[r28]     // Catch:{ RuntimeException -> 0x0100 }
            if (r5 >= r12) goto L_0x0140
            int r12 = r28 + -2
            r5 = r29
            goto L_0x011f
        L_0x0140:
            char r5 = r2[r7]     // Catch:{ RuntimeException -> 0x0100 }
            char[] r12 = _json_trans_keys     // Catch:{ RuntimeException -> 0x0100 }
            int r30 = r28 + 1
            char r12 = r12[r30]     // Catch:{ RuntimeException -> 0x0100 }
            if (r5 <= r12) goto L_0x014f
            int r5 = r28 + 2
            r12 = r31
            goto L_0x011f
        L_0x014f:
            int r5 = r28 - r0
            r12 = 1
            int r5 = r5 >> r12
            int r16 = r16 + r5
            r5 = r0
            goto L_0x0158
        L_0x0157:
            r5 = r0
        L_0x0158:
            byte[] r0 = _json_indicies     // Catch:{ RuntimeException -> 0x09d0 }
            byte r0 = r0[r16]     // Catch:{ RuntimeException -> 0x09d0 }
            r16 = r0
            byte[] r0 = _json_trans_targs     // Catch:{ RuntimeException -> 0x09d0 }
            byte r0 = r0[r16]     // Catch:{ RuntimeException -> 0x09d0 }
            r12 = r0
            byte[] r0 = _json_trans_actions     // Catch:{ RuntimeException -> 0x09d0 }
            byte r0 = r0[r16]     // Catch:{ RuntimeException -> 0x09d0 }
            if (r0 == 0) goto L_0x075a
            byte[] r0 = _json_trans_actions     // Catch:{ RuntimeException -> 0x0749 }
            byte r0 = r0[r16]     // Catch:{ RuntimeException -> 0x0749 }
            byte[] r15 = _json_actions     // Catch:{ RuntimeException -> 0x0749 }
            int r28 = r0 + 1
            byte r0 = r15[r0]     // Catch:{ RuntimeException -> 0x0749 }
            r15 = r20
        L_0x0175:
            int r20 = r0 + -1
            if (r0 <= 0) goto L_0x0731
            byte[] r0 = _json_actions     // Catch:{ RuntimeException -> 0x0721 }
            int r29 = r28 + 1
            byte r0 = r0[r28]     // Catch:{ RuntimeException -> 0x0721 }
            r30 = r5
            switch(r0) {
                case 0: goto L_0x06f4;
                case 1: goto L_0x04de;
                case 2: goto L_0x047e;
                case 3: goto L_0x045c;
                case 4: goto L_0x040c;
                case 5: goto L_0x03ea;
                case 6: goto L_0x033f;
                case 7: goto L_0x01e7;
                case 8: goto L_0x0197;
                default: goto L_0x0184;
            }
        L_0x0184:
            r33 = r3
            r32 = r4
            r31 = r8
            r35 = r10
            r4 = r13
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x0708
        L_0x0197:
            if (r14 == 0) goto L_0x01a0
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x01f1 }
            java.lang.String r5 = "quotedChars"
            r0.println(r5)     // Catch:{ RuntimeException -> 0x01f1 }
        L_0x01a0:
            int r7 = r7 + 1
            r5 = r7
            r0 = 0
            r15 = r0
        L_0x01a5:
            char r0 = r2[r7]     // Catch:{ RuntimeException -> 0x01d9 }
            r28 = r5
            r5 = 34
            if (r0 == r5) goto L_0x01c0
            r5 = 92
            if (r0 == r5) goto L_0x01b2
            goto L_0x01b6
        L_0x01b2:
            r15 = 1
            int r7 = r7 + 1
        L_0x01b6:
            r5 = 1
            int r7 = r7 + r5
            if (r7 != r4) goto L_0x01bd
            r21 = r15
            goto L_0x01c2
        L_0x01bd:
            r5 = r28
            goto L_0x01a5
        L_0x01c0:
            r21 = r15
        L_0x01c2:
            int r7 = r7 + -1
            r33 = r3
            r32 = r4
            r31 = r8
            r35 = r10
            r4 = r13
            r13 = r23
            r15 = r28
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x0708
        L_0x01d9:
            r0 = move-exception
            r28 = r5
            r33 = r3
            r32 = r4
            r24 = r9
            r12 = r15
            r10 = r28
            goto L_0x09df
        L_0x01e7:
            if (r14 == 0) goto L_0x01fd
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x01f1 }
            java.lang.String r5 = "unquotedChars"
            r0.println(r5)     // Catch:{ RuntimeException -> 0x01f1 }
            goto L_0x01fd
        L_0x01f1:
            r0 = move-exception
            r33 = r3
            r32 = r4
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x01fd:
            r5 = r7
            r0 = 0
            r18 = 1
            if (r17 == 0) goto L_0x028f
            r15 = r0
        L_0x0204:
            char r0 = r2[r7]     // Catch:{ RuntimeException -> 0x0281 }
            r33 = r5
            r5 = 10
            if (r0 == r5) goto L_0x027c
            r5 = 13
            if (r0 == r5) goto L_0x027c
            r5 = 47
            if (r0 == r5) goto L_0x0220
            r5 = 58
            if (r0 == r5) goto L_0x027c
            r5 = 92
            if (r0 == r5) goto L_0x021d
            goto L_0x0232
        L_0x021d:
            r0 = 1
            r15 = r0
            goto L_0x0232
        L_0x0220:
            int r0 = r7 + 1
            if (r0 != r4) goto L_0x0225
            goto L_0x0232
        L_0x0225:
            int r0 = r7 + 1
            char r0 = r2[r0]     // Catch:{ RuntimeException -> 0x0270 }
            r5 = 47
            if (r0 == r5) goto L_0x027d
            r5 = 42
            if (r0 != r5) goto L_0x0232
            goto L_0x027d
        L_0x0232:
            if (r14 == 0) goto L_0x0263
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x0254 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x0254 }
            r5.<init>()     // Catch:{ RuntimeException -> 0x0254 }
            r21 = r15
            java.lang.String r15 = "unquotedChar (name): '"
            r5.append(r15)     // Catch:{ RuntimeException -> 0x0326 }
            char r15 = r2[r7]     // Catch:{ RuntimeException -> 0x0326 }
            r5.append(r15)     // Catch:{ RuntimeException -> 0x0326 }
            java.lang.String r15 = "'"
            r5.append(r15)     // Catch:{ RuntimeException -> 0x0326 }
            java.lang.String r5 = r5.toString()     // Catch:{ RuntimeException -> 0x0326 }
            r0.println(r5)     // Catch:{ RuntimeException -> 0x0326 }
            goto L_0x0265
        L_0x0254:
            r0 = move-exception
            r21 = r15
            r32 = r4
            r24 = r9
            r12 = r21
            r10 = r33
            r33 = r3
            goto L_0x09df
        L_0x0263:
            r21 = r15
        L_0x0265:
            int r7 = r7 + 1
            if (r7 != r4) goto L_0x026b
            goto L_0x0304
        L_0x026b:
            r15 = r21
            r5 = r33
            goto L_0x0204
        L_0x0270:
            r0 = move-exception
            r32 = r4
            r24 = r9
            r12 = r15
            r10 = r33
            r33 = r3
            goto L_0x09df
        L_0x027c:
        L_0x027d:
            r21 = r15
            goto L_0x0304
        L_0x0281:
            r0 = move-exception
            r33 = r5
            r32 = r4
            r24 = r9
            r12 = r15
            r10 = r33
            r33 = r3
            goto L_0x09df
        L_0x028f:
            r33 = r5
            r5 = r0
        L_0x0292:
            char r0 = r2[r7]     // Catch:{ RuntimeException -> 0x0333 }
            r15 = 10
            if (r0 == r15) goto L_0x0301
            r15 = 13
            if (r0 == r15) goto L_0x0301
            r15 = 44
            if (r0 == r15) goto L_0x0301
            r15 = 47
            if (r0 == r15) goto L_0x02b4
            r15 = 125(0x7d, float:1.75E-43)
            if (r0 == r15) goto L_0x0301
            r15 = 92
            if (r0 == r15) goto L_0x02b1
            r15 = 93
            if (r0 == r15) goto L_0x0301
            goto L_0x02c6
        L_0x02b1:
            r0 = 1
            r5 = r0
            goto L_0x02c6
        L_0x02b4:
            int r0 = r7 + 1
            if (r0 != r4) goto L_0x02b9
            goto L_0x02c6
        L_0x02b9:
            int r0 = r7 + 1
            char r0 = r2[r0]     // Catch:{ RuntimeException -> 0x0333 }
            r15 = 47
            if (r0 == r15) goto L_0x0302
            r15 = 42
            if (r0 != r15) goto L_0x02c6
            goto L_0x0302
        L_0x02c6:
            if (r14 == 0) goto L_0x02f7
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x02e8 }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x02e8 }
            r15.<init>()     // Catch:{ RuntimeException -> 0x02e8 }
            r21 = r5
            java.lang.String r5 = "unquotedChar (value): '"
            r15.append(r5)     // Catch:{ RuntimeException -> 0x0326 }
            char r5 = r2[r7]     // Catch:{ RuntimeException -> 0x0326 }
            r15.append(r5)     // Catch:{ RuntimeException -> 0x0326 }
            java.lang.String r5 = "'"
            r15.append(r5)     // Catch:{ RuntimeException -> 0x0326 }
            java.lang.String r5 = r15.toString()     // Catch:{ RuntimeException -> 0x0326 }
            r0.println(r5)     // Catch:{ RuntimeException -> 0x0326 }
            goto L_0x02f9
        L_0x02e8:
            r0 = move-exception
            r21 = r5
            r32 = r4
            r24 = r9
            r12 = r21
            r10 = r33
            r33 = r3
            goto L_0x09df
        L_0x02f7:
            r21 = r5
        L_0x02f9:
            int r7 = r7 + 1
            if (r7 != r4) goto L_0x02fe
            goto L_0x0304
        L_0x02fe:
            r5 = r21
            goto L_0x0292
        L_0x0301:
        L_0x0302:
            r21 = r5
        L_0x0304:
            int r7 = r7 + -1
        L_0x0306:
            char r0 = r2[r7]     // Catch:{ RuntimeException -> 0x0326 }
            boolean r0 = java.lang.Character.isSpace(r0)     // Catch:{ RuntimeException -> 0x0326 }
            if (r0 == 0) goto L_0x0311
            int r7 = r7 + -1
            goto L_0x0306
        L_0x0311:
            r32 = r4
            r31 = r8
            r35 = r10
            r4 = r13
            r13 = r23
            r15 = r33
            r33 = r3
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x0708
        L_0x0326:
            r0 = move-exception
            r32 = r4
            r24 = r9
            r12 = r21
            r10 = r33
            r33 = r3
            goto L_0x09df
        L_0x0333:
            r0 = move-exception
            r32 = r4
            r12 = r5
            r24 = r9
            r10 = r33
            r33 = r3
            goto L_0x09df
        L_0x033f:
            int r0 = r7 + -1
            int r5 = r7 + 1
            char r7 = r2[r7]     // Catch:{ RuntimeException -> 0x03da }
            r31 = r5
            r5 = 47
            if (r7 != r5) goto L_0x036d
            r7 = r31
        L_0x034d:
            if (r7 == r4) goto L_0x0368
            char r5 = r2[r7]     // Catch:{ RuntimeException -> 0x035c }
            r33 = r3
            r3 = 10
            if (r5 == r3) goto L_0x036a
            int r7 = r7 + 1
            r3 = r33
            goto L_0x034d
        L_0x035c:
            r0 = move-exception
            r33 = r3
            r32 = r4
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x0368:
            r33 = r3
        L_0x036a:
            int r7 = r7 + -1
            goto L_0x0398
        L_0x036d:
            r33 = r3
            r7 = r31
        L_0x0371:
            int r3 = r7 + 1
            if (r3 >= r4) goto L_0x0389
            char r3 = r2[r7]     // Catch:{ RuntimeException -> 0x037f }
            r5 = 42
            if (r3 != r5) goto L_0x037c
            goto L_0x038b
        L_0x037c:
            r5 = 47
            goto L_0x0393
        L_0x037f:
            r0 = move-exception
            r32 = r4
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x0389:
            r5 = 42
        L_0x038b:
            int r3 = r7 + 1
            char r3 = r2[r3]     // Catch:{ RuntimeException -> 0x03d0 }
            r5 = 47
            if (r3 == r5) goto L_0x0396
        L_0x0393:
            int r7 = r7 + 1
            goto L_0x0371
        L_0x0396:
            int r7 = r7 + 1
        L_0x0398:
            if (r14 == 0) goto L_0x03bc
            java.io.PrintStream r3 = java.lang.System.out     // Catch:{ RuntimeException -> 0x03d0 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x03d0 }
            r5.<init>()     // Catch:{ RuntimeException -> 0x03d0 }
            r32 = r4
            java.lang.String r4 = "comment "
            r5.append(r4)     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r4 = new java.lang.String     // Catch:{ RuntimeException -> 0x04d6 }
            r34 = r13
            int r13 = r7 - r0
            r4.<init>(r2, r0, r13)     // Catch:{ RuntimeException -> 0x04d6 }
            r5.append(r4)     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r4 = r5.toString()     // Catch:{ RuntimeException -> 0x04d6 }
            r3.println(r4)     // Catch:{ RuntimeException -> 0x04d6 }
            goto L_0x03c0
        L_0x03bc:
            r32 = r4
            r34 = r13
        L_0x03c0:
            r31 = r8
            r35 = r10
            r13 = r23
            r4 = r34
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x0708
        L_0x03d0:
            r0 = move-exception
            r32 = r4
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x03da:
            r0 = move-exception
            r31 = r5
            r33 = r3
            r32 = r4
            r24 = r9
            r10 = r15
            r12 = r21
            r7 = r31
            goto L_0x09df
        L_0x03ea:
            r33 = r3
            r32 = r4
            if (r14 == 0) goto L_0x03f7
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r3 = "endArray"
            r0.println(r3)     // Catch:{ RuntimeException -> 0x04d6 }
        L_0x03f7:
            r37.pop()     // Catch:{ RuntimeException -> 0x04d6 }
            int r8 = r8 + -1
            r0 = r11[r8]     // Catch:{ RuntimeException -> 0x04d6 }
            r5 = 2
            r10 = r15
            r12 = r21
            r13 = r22
            r4 = r32
            r3 = r33
            r6 = 4
            r15 = r0
            goto L_0x0032
        L_0x040c:
            r33 = r3
            r32 = r4
            int r0 = r9.size     // Catch:{ RuntimeException -> 0x04d6 }
            if (r0 <= 0) goto L_0x041b
            java.lang.Object r0 = r9.pop()     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ RuntimeException -> 0x04d6 }
            goto L_0x041c
        L_0x041b:
            r0 = 0
        L_0x041c:
            if (r14 == 0) goto L_0x0434
            java.io.PrintStream r3 = java.lang.System.out     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x04d6 }
            r4.<init>()     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r5 = "startArray: "
            r4.append(r5)     // Catch:{ RuntimeException -> 0x04d6 }
            r4.append(r0)     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r4 = r4.toString()     // Catch:{ RuntimeException -> 0x04d6 }
            r3.println(r4)     // Catch:{ RuntimeException -> 0x04d6 }
        L_0x0434:
            r1.startArray(r0)     // Catch:{ RuntimeException -> 0x04d6 }
            int r3 = r11.length     // Catch:{ RuntimeException -> 0x04d6 }
            if (r8 != r3) goto L_0x0447
            int r3 = r11.length     // Catch:{ RuntimeException -> 0x04d6 }
            r4 = 2
            int r3 = r3 * 2
            int[] r3 = new int[r3]     // Catch:{ RuntimeException -> 0x04d6 }
            int r4 = r11.length     // Catch:{ RuntimeException -> 0x04d6 }
            r5 = 0
            java.lang.System.arraycopy(r11, r5, r3, r5, r4)     // Catch:{ RuntimeException -> 0x04d6 }
            r4 = r3
            r11 = r4
        L_0x0447:
            int r3 = r8 + 1
            r11[r8] = r12     // Catch:{ RuntimeException -> 0x04cd }
            r4 = 23
            r5 = 2
            r8 = r3
            r10 = r15
            r12 = r21
            r13 = r22
            r3 = r33
            r6 = 4
            r15 = r4
            r4 = r32
            goto L_0x0032
        L_0x045c:
            r33 = r3
            r32 = r4
            if (r14 == 0) goto L_0x0469
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r3 = "endObject"
            r0.println(r3)     // Catch:{ RuntimeException -> 0x04d6 }
        L_0x0469:
            r37.pop()     // Catch:{ RuntimeException -> 0x04d6 }
            int r8 = r8 + -1
            r0 = r11[r8]     // Catch:{ RuntimeException -> 0x04d6 }
            r5 = 2
            r10 = r15
            r12 = r21
            r13 = r22
            r4 = r32
            r3 = r33
            r6 = 4
            r15 = r0
            goto L_0x0032
        L_0x047e:
            r33 = r3
            r32 = r4
            int r0 = r9.size     // Catch:{ RuntimeException -> 0x04d6 }
            if (r0 <= 0) goto L_0x048d
            java.lang.Object r0 = r9.pop()     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ RuntimeException -> 0x04d6 }
            goto L_0x048e
        L_0x048d:
            r0 = 0
        L_0x048e:
            if (r14 == 0) goto L_0x04a6
            java.io.PrintStream r3 = java.lang.System.out     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x04d6 }
            r4.<init>()     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r5 = "startObject: "
            r4.append(r5)     // Catch:{ RuntimeException -> 0x04d6 }
            r4.append(r0)     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r4 = r4.toString()     // Catch:{ RuntimeException -> 0x04d6 }
            r3.println(r4)     // Catch:{ RuntimeException -> 0x04d6 }
        L_0x04a6:
            r1.startObject(r0)     // Catch:{ RuntimeException -> 0x04d6 }
            int r3 = r11.length     // Catch:{ RuntimeException -> 0x04d6 }
            if (r8 != r3) goto L_0x04b9
            int r3 = r11.length     // Catch:{ RuntimeException -> 0x04d6 }
            r4 = 2
            int r3 = r3 * 2
            int[] r3 = new int[r3]     // Catch:{ RuntimeException -> 0x04d6 }
            int r4 = r11.length     // Catch:{ RuntimeException -> 0x04d6 }
            r5 = 0
            java.lang.System.arraycopy(r11, r5, r3, r5, r4)     // Catch:{ RuntimeException -> 0x04d6 }
            r4 = r3
            r11 = r4
        L_0x04b9:
            int r3 = r8 + 1
            r11[r8] = r12     // Catch:{ RuntimeException -> 0x04cd }
            r4 = 5
            r5 = 2
            r8 = r3
            r10 = r15
            r12 = r21
            r13 = r22
            r3 = r33
            r6 = 4
            r15 = r4
            r4 = r32
            goto L_0x0032
        L_0x04cd:
            r0 = move-exception
            r8 = r3
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x04d6:
            r0 = move-exception
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x04de:
            r33 = r3
            r32 = r4
            r34 = r13
            r4 = 2
            java.lang.String r0 = new java.lang.String     // Catch:{ RuntimeException -> 0x06e8 }
            int r3 = r7 - r15
            r0.<init>(r2, r15, r3)     // Catch:{ RuntimeException -> 0x06e8 }
            if (r21 == 0) goto L_0x04f4
            java.lang.String r3 = r1.unescape(r0)     // Catch:{ RuntimeException -> 0x04d6 }
            r0 = r3
            goto L_0x04f5
        L_0x04f4:
            r3 = r0
        L_0x04f5:
            if (r17 == 0) goto L_0x0522
            r17 = 0
            if (r14 == 0) goto L_0x050f
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x04d6 }
            r5.<init>()     // Catch:{ RuntimeException -> 0x04d6 }
            r5.append(r6)     // Catch:{ RuntimeException -> 0x04d6 }
            r5.append(r3)     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r5 = r5.toString()     // Catch:{ RuntimeException -> 0x04d6 }
            r0.println(r5)     // Catch:{ RuntimeException -> 0x04d6 }
        L_0x050f:
            r9.add(r3)     // Catch:{ RuntimeException -> 0x04d6 }
            r31 = r8
            r35 = r10
            r13 = r23
            r4 = r34
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x06da
        L_0x0522:
            int r0 = r9.size     // Catch:{ RuntimeException -> 0x06e8 }
            if (r0 <= 0) goto L_0x052d
            java.lang.Object r0 = r9.pop()     // Catch:{ RuntimeException -> 0x04d6 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ RuntimeException -> 0x04d6 }
            goto L_0x052e
        L_0x052d:
            r0 = 0
        L_0x052e:
            r5 = r0
            if (r18 == 0) goto L_0x06ab
            boolean r0 = r3.equals(r10)     // Catch:{ RuntimeException -> 0x06e8 }
            if (r0 == 0) goto L_0x057f
            if (r14 == 0) goto L_0x0561
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x0557 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x0557 }
            r13.<init>()     // Catch:{ RuntimeException -> 0x0557 }
            r4 = r34
            r13.append(r4)     // Catch:{ RuntimeException -> 0x0557 }
            r13.append(r5)     // Catch:{ RuntimeException -> 0x0557 }
            r31 = r8
            java.lang.String r8 = "=true"
            r13.append(r8)     // Catch:{ RuntimeException -> 0x0575 }
            java.lang.String r8 = r13.toString()     // Catch:{ RuntimeException -> 0x0575 }
            r0.println(r8)     // Catch:{ RuntimeException -> 0x0575 }
            goto L_0x0565
        L_0x0557:
            r0 = move-exception
            r31 = r8
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x0561:
            r31 = r8
            r4 = r34
        L_0x0565:
            r8 = 1
            r1.bool(r5, r8)     // Catch:{ RuntimeException -> 0x0575 }
            r35 = r10
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x06da
        L_0x0575:
            r0 = move-exception
            r24 = r9
            r10 = r15
            r12 = r21
            r8 = r31
            goto L_0x09df
        L_0x057f:
            r31 = r8
            r4 = r34
            java.lang.String r0 = "false"
            boolean r0 = r3.equals(r0)     // Catch:{ RuntimeException -> 0x069f }
            if (r0 == 0) goto L_0x05b6
            if (r14 == 0) goto L_0x05a6
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x0575 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x0575 }
            r8.<init>()     // Catch:{ RuntimeException -> 0x0575 }
            r8.append(r4)     // Catch:{ RuntimeException -> 0x0575 }
            r8.append(r5)     // Catch:{ RuntimeException -> 0x0575 }
            java.lang.String r13 = "=false"
            r8.append(r13)     // Catch:{ RuntimeException -> 0x0575 }
            java.lang.String r8 = r8.toString()     // Catch:{ RuntimeException -> 0x0575 }
            r0.println(r8)     // Catch:{ RuntimeException -> 0x0575 }
        L_0x05a6:
            r8 = 0
            r1.bool(r5, r8)     // Catch:{ RuntimeException -> 0x0575 }
            r35 = r10
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x06da
        L_0x05b6:
            java.lang.String r0 = "null"
            boolean r0 = r3.equals(r0)     // Catch:{ RuntimeException -> 0x069f }
            if (r0 == 0) goto L_0x05ce
            r8 = 0
            r1.string(r5, r8)     // Catch:{ RuntimeException -> 0x0575 }
            r35 = r10
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x06da
        L_0x05ce:
            r0 = 0
            r8 = 1
            r13 = r15
        L_0x05d1:
            if (r13 >= r7) goto L_0x0601
            r28 = r0
            char r0 = r2[r13]     // Catch:{ RuntimeException -> 0x0575 }
            r34 = r8
            r8 = 43
            if (r0 == r8) goto L_0x05fa
            r8 = 69
            if (r0 == r8) goto L_0x05f7
            r8 = 101(0x65, float:1.42E-43)
            if (r0 == r8) goto L_0x05f7
            r8 = 45
            if (r0 == r8) goto L_0x05fa
            r8 = 46
            if (r0 == r8) goto L_0x05f7
            switch(r0) {
                case 48: goto L_0x05fa;
                case 49: goto L_0x05fa;
                case 50: goto L_0x05fa;
                case 51: goto L_0x05fa;
                case 52: goto L_0x05fa;
                case 53: goto L_0x05fa;
                case 54: goto L_0x05fa;
                case 55: goto L_0x05fa;
                case 56: goto L_0x05fa;
                case 57: goto L_0x05fa;
                default: goto L_0x05f0;
            }
        L_0x05f0:
            r0 = 0
            r8 = 0
            r28 = r0
            r34 = r8
            goto L_0x0605
        L_0x05f7:
            r0 = 1
            r8 = 0
            goto L_0x05fe
        L_0x05fa:
            r0 = r28
            r8 = r34
        L_0x05fe:
            int r13 = r13 + 1
            goto L_0x05d1
        L_0x0601:
            r28 = r0
            r34 = r8
        L_0x0605:
            if (r28 == 0) goto L_0x0669
            if (r14 == 0) goto L_0x0654
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ NumberFormatException -> 0x0648 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0648 }
            r8.<init>()     // Catch:{ NumberFormatException -> 0x0648 }
            r13 = r23
            r8.append(r13)     // Catch:{ NumberFormatException -> 0x0644 }
            r8.append(r5)     // Catch:{ NumberFormatException -> 0x0644 }
            r23 = r11
            r11 = r24
            r8.append(r11)     // Catch:{ NumberFormatException -> 0x063e, RuntimeException -> 0x0632 }
            r24 = r9
            r35 = r10
            double r9 = java.lang.Double.parseDouble(r3)     // Catch:{ NumberFormatException -> 0x0667 }
            r8.append(r9)     // Catch:{ NumberFormatException -> 0x0667 }
            java.lang.String r8 = r8.toString()     // Catch:{ NumberFormatException -> 0x0667 }
            r0.println(r8)     // Catch:{ NumberFormatException -> 0x0667 }
            goto L_0x065e
        L_0x0632:
            r0 = move-exception
            r24 = r9
            r10 = r15
            r12 = r21
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x063e:
            r0 = move-exception
            r24 = r9
            r35 = r10
            goto L_0x0668
        L_0x0644:
            r0 = move-exception
            r35 = r10
            goto L_0x064d
        L_0x0648:
            r0 = move-exception
            r35 = r10
            r13 = r23
        L_0x064d:
            r23 = r11
            r11 = r24
            r24 = r9
            goto L_0x0668
        L_0x0654:
            r35 = r10
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
        L_0x065e:
            double r8 = java.lang.Double.parseDouble(r3)     // Catch:{ NumberFormatException -> 0x0667 }
            r1.number((java.lang.String) r5, (double) r8, (java.lang.String) r3)     // Catch:{ NumberFormatException -> 0x0667 }
            goto L_0x06da
        L_0x0667:
            r0 = move-exception
        L_0x0668:
            goto L_0x06b9
        L_0x0669:
            r35 = r10
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            if (r34 == 0) goto L_0x06b9
            if (r14 == 0) goto L_0x0695
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x06de }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x06de }
            r8.<init>()     // Catch:{ RuntimeException -> 0x06de }
            r8.append(r13)     // Catch:{ RuntimeException -> 0x06de }
            r8.append(r5)     // Catch:{ RuntimeException -> 0x06de }
            r8.append(r11)     // Catch:{ RuntimeException -> 0x06de }
            double r9 = java.lang.Double.parseDouble(r3)     // Catch:{ RuntimeException -> 0x06de }
            r8.append(r9)     // Catch:{ RuntimeException -> 0x06de }
            java.lang.String r8 = r8.toString()     // Catch:{ RuntimeException -> 0x06de }
            r0.println(r8)     // Catch:{ RuntimeException -> 0x06de }
        L_0x0695:
            long r8 = java.lang.Long.parseLong(r3)     // Catch:{ NumberFormatException -> 0x069d }
            r1.number((java.lang.String) r5, (long) r8, (java.lang.String) r3)     // Catch:{ NumberFormatException -> 0x069d }
            goto L_0x06da
        L_0x069d:
            r0 = move-exception
            goto L_0x06b9
        L_0x069f:
            r0 = move-exception
            r23 = r11
            r24 = r9
            r10 = r15
            r12 = r21
            r8 = r31
            goto L_0x09df
        L_0x06ab:
            r31 = r8
            r35 = r10
            r13 = r23
            r4 = r34
            r23 = r11
            r11 = r24
            r24 = r9
        L_0x06b9:
            if (r14 == 0) goto L_0x06d7
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x06de }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x06de }
            r8.<init>()     // Catch:{ RuntimeException -> 0x06de }
            java.lang.String r9 = "string: "
            r8.append(r9)     // Catch:{ RuntimeException -> 0x06de }
            r8.append(r5)     // Catch:{ RuntimeException -> 0x06de }
            r8.append(r11)     // Catch:{ RuntimeException -> 0x06de }
            r8.append(r3)     // Catch:{ RuntimeException -> 0x06de }
            java.lang.String r8 = r8.toString()     // Catch:{ RuntimeException -> 0x06de }
            r0.println(r8)     // Catch:{ RuntimeException -> 0x06de }
        L_0x06d7:
            r1.string(r5, r3)     // Catch:{ RuntimeException -> 0x06de }
        L_0x06da:
            r18 = 0
            r15 = r7
            goto L_0x0708
        L_0x06de:
            r0 = move-exception
            r10 = r15
            r12 = r21
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x06e8:
            r0 = move-exception
            r31 = r8
            r23 = r11
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x06f4:
            r33 = r3
            r32 = r4
            r31 = r8
            r35 = r10
            r4 = r13
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            r17 = 1
        L_0x0708:
            r0 = r20
            r9 = r24
            r28 = r29
            r5 = r30
            r8 = r31
            r3 = r33
            r10 = r35
            r24 = r11
            r11 = r23
            r23 = r13
            r13 = r4
            r4 = r32
            goto L_0x0175
        L_0x0721:
            r0 = move-exception
            r31 = r8
            r23 = r11
            r33 = r3
            r32 = r4
            r24 = r9
            r10 = r15
            r12 = r21
            goto L_0x09df
        L_0x0731:
            r33 = r3
            r32 = r4
            r30 = r5
            r31 = r8
            r35 = r10
            r4 = r13
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            r10 = r15
            r15 = r12
            r12 = r21
            goto L_0x0772
        L_0x0749:
            r0 = move-exception
            r31 = r8
            r23 = r11
            r33 = r3
            r32 = r4
            r24 = r9
            r10 = r20
            r12 = r21
            goto L_0x09df
        L_0x075a:
            r33 = r3
            r32 = r4
            r30 = r5
            r31 = r8
            r35 = r10
            r4 = r13
            r13 = r23
            r23 = r11
            r11 = r24
            r24 = r9
            r15 = r12
            r10 = r20
            r12 = r21
        L_0x0772:
            if (r15 != 0) goto L_0x0784
            r5 = 5
            r13 = r22
            r11 = r23
            r9 = r24
            r8 = r31
            r4 = r32
            r3 = r33
            r6 = 4
            goto L_0x0032
        L_0x0784:
            int r7 = r7 + 1
            r3 = r33
            if (r7 == r3) goto L_0x0798
            r5 = 1
            r13 = r22
            r11 = r23
            r9 = r24
            r8 = r31
            r4 = r32
            r6 = 4
            goto L_0x0032
        L_0x0798:
            r5 = r32
            if (r7 != r5) goto L_0x09c8
            byte[] r0 = _json_eof_actions     // Catch:{ RuntimeException -> 0x09be }
            byte r0 = r0[r15]     // Catch:{ RuntimeException -> 0x09be }
            byte[] r8 = _json_actions     // Catch:{ RuntimeException -> 0x09be }
            int r9 = r0 + 1
            byte r0 = r8[r0]     // Catch:{ RuntimeException -> 0x09be }
        L_0x07a6:
            int r8 = r0 + -1
            if (r0 <= 0) goto L_0x09b7
            byte[] r0 = _json_actions     // Catch:{ RuntimeException -> 0x09be }
            int r19 = r9 + 1
            byte r0 = r0[r9]     // Catch:{ RuntimeException -> 0x09be }
            r9 = 1
            if (r0 == r9) goto L_0x07bf
            r33 = r3
            r34 = r4
            r32 = r5
            r25 = r6
            r20 = r8
            goto L_0x0997
        L_0x07bf:
            java.lang.String r0 = new java.lang.String     // Catch:{ RuntimeException -> 0x09be }
            int r9 = r7 - r10
            r0.<init>(r2, r10, r9)     // Catch:{ RuntimeException -> 0x09be }
            if (r12 == 0) goto L_0x07d9
            java.lang.String r9 = r1.unescape(r0)     // Catch:{ RuntimeException -> 0x07ce }
            r0 = r9
            goto L_0x07da
        L_0x07ce:
            r0 = move-exception
            r33 = r3
            r32 = r5
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x07d9:
            r9 = r0
        L_0x07da:
            if (r17 == 0) goto L_0x0829
            r17 = 0
            if (r14 == 0) goto L_0x080b
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x0800 }
            r32 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x07f7 }
            r5.<init>()     // Catch:{ RuntimeException -> 0x07f7 }
            r5.append(r6)     // Catch:{ RuntimeException -> 0x07f7 }
            r5.append(r9)     // Catch:{ RuntimeException -> 0x07f7 }
            java.lang.String r5 = r5.toString()     // Catch:{ RuntimeException -> 0x07f7 }
            r0.println(r5)     // Catch:{ RuntimeException -> 0x07f7 }
            goto L_0x080d
        L_0x07f7:
            r0 = move-exception
            r33 = r3
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x0800:
            r0 = move-exception
            r32 = r5
            r33 = r3
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x080b:
            r32 = r5
        L_0x080d:
            r5 = r24
            r5.add(r9)     // Catch:{ RuntimeException -> 0x081e }
            r33 = r3
            r34 = r4
            r24 = r5
            r25 = r6
            r20 = r8
            goto L_0x0994
        L_0x081e:
            r0 = move-exception
            r33 = r3
            r24 = r5
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x0829:
            r32 = r5
            r5 = r24
            int r0 = r5.size     // Catch:{ RuntimeException -> 0x09ad }
            if (r0 <= 0) goto L_0x0838
            java.lang.Object r0 = r5.pop()     // Catch:{ RuntimeException -> 0x081e }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ RuntimeException -> 0x081e }
            goto L_0x0839
        L_0x0838:
            r0 = 0
        L_0x0839:
            r20 = r0
            if (r18 == 0) goto L_0x0967
            r24 = r5
            r5 = r35
            boolean r0 = r9.equals(r5)     // Catch:{ RuntimeException -> 0x095e }
            if (r0 == 0) goto L_0x087d
            if (r14 == 0) goto L_0x086b
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x07f7 }
            r35 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x07f7 }
            r5.<init>()     // Catch:{ RuntimeException -> 0x07f7 }
            r5.append(r4)     // Catch:{ RuntimeException -> 0x07f7 }
            r25 = r6
            r6 = r20
            r5.append(r6)     // Catch:{ RuntimeException -> 0x07f7 }
            r20 = r8
            java.lang.String r8 = "=true"
            r5.append(r8)     // Catch:{ RuntimeException -> 0x07f7 }
            java.lang.String r5 = r5.toString()     // Catch:{ RuntimeException -> 0x07f7 }
            r0.println(r5)     // Catch:{ RuntimeException -> 0x07f7 }
            goto L_0x0873
        L_0x086b:
            r35 = r5
            r25 = r6
            r6 = r20
            r20 = r8
        L_0x0873:
            r5 = 1
            r1.bool(r6, r5)     // Catch:{ RuntimeException -> 0x07f7 }
            r33 = r3
            r34 = r4
            goto L_0x0994
        L_0x087d:
            r35 = r5
            r25 = r6
            r6 = r20
            r5 = 1
            r20 = r8
            java.lang.String r0 = "false"
            boolean r0 = r9.equals(r0)     // Catch:{ RuntimeException -> 0x095e }
            if (r0 == 0) goto L_0x08b3
            if (r14 == 0) goto L_0x08a9
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x07f7 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x07f7 }
            r8.<init>()     // Catch:{ RuntimeException -> 0x07f7 }
            r8.append(r4)     // Catch:{ RuntimeException -> 0x07f7 }
            r8.append(r6)     // Catch:{ RuntimeException -> 0x07f7 }
            java.lang.String r5 = "=false"
            r8.append(r5)     // Catch:{ RuntimeException -> 0x07f7 }
            java.lang.String r5 = r8.toString()     // Catch:{ RuntimeException -> 0x07f7 }
            r0.println(r5)     // Catch:{ RuntimeException -> 0x07f7 }
        L_0x08a9:
            r5 = 0
            r1.bool(r6, r5)     // Catch:{ RuntimeException -> 0x07f7 }
            r33 = r3
            r34 = r4
            goto L_0x0994
        L_0x08b3:
            java.lang.String r0 = "null"
            boolean r0 = r9.equals(r0)     // Catch:{ RuntimeException -> 0x095e }
            if (r0 == 0) goto L_0x08c5
            r5 = 0
            r1.string(r6, r5)     // Catch:{ RuntimeException -> 0x07f7 }
            r33 = r3
            r34 = r4
            goto L_0x0994
        L_0x08c5:
            r0 = 0
            r5 = 1
            r8 = r10
        L_0x08c8:
            if (r8 >= r7) goto L_0x08f7
            r21 = r0
            char r0 = r2[r8]     // Catch:{ RuntimeException -> 0x07f7 }
            r34 = r4
            r4 = 43
            if (r0 == r4) goto L_0x08f0
            r4 = 69
            if (r0 == r4) goto L_0x08ec
            r4 = 101(0x65, float:1.42E-43)
            if (r0 == r4) goto L_0x08ec
            r4 = 45
            if (r0 == r4) goto L_0x08f0
            r4 = 46
            if (r0 == r4) goto L_0x08ec
            switch(r0) {
                case 48: goto L_0x08f0;
                case 49: goto L_0x08f0;
                case 50: goto L_0x08f0;
                case 51: goto L_0x08f0;
                case 52: goto L_0x08f0;
                case 53: goto L_0x08f0;
                case 54: goto L_0x08f0;
                case 55: goto L_0x08f0;
                case 56: goto L_0x08f0;
                case 57: goto L_0x08f0;
                default: goto L_0x08e7;
            }
        L_0x08e7:
            r0 = 0
            r5 = 0
            r21 = r0
            goto L_0x08fb
        L_0x08ec:
            r0 = 1
            r4 = 0
            r5 = r4
            goto L_0x08f2
        L_0x08f0:
            r0 = r21
        L_0x08f2:
            int r8 = r8 + 1
            r4 = r34
            goto L_0x08c8
        L_0x08f7:
            r21 = r0
            r34 = r4
        L_0x08fb:
            if (r21 == 0) goto L_0x0930
            if (r14 == 0) goto L_0x0924
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ NumberFormatException -> 0x0920 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0920 }
            r4.<init>()     // Catch:{ NumberFormatException -> 0x0920 }
            r4.append(r13)     // Catch:{ NumberFormatException -> 0x0920 }
            r4.append(r6)     // Catch:{ NumberFormatException -> 0x0920 }
            r4.append(r11)     // Catch:{ NumberFormatException -> 0x0920 }
            r33 = r3
            double r2 = java.lang.Double.parseDouble(r9)     // Catch:{ NumberFormatException -> 0x092e }
            r4.append(r2)     // Catch:{ NumberFormatException -> 0x092e }
            java.lang.String r2 = r4.toString()     // Catch:{ NumberFormatException -> 0x092e }
            r0.println(r2)     // Catch:{ NumberFormatException -> 0x092e }
            goto L_0x0926
        L_0x0920:
            r0 = move-exception
            r33 = r3
            goto L_0x092f
        L_0x0924:
            r33 = r3
        L_0x0926:
            double r2 = java.lang.Double.parseDouble(r9)     // Catch:{ NumberFormatException -> 0x092e }
            r1.number((java.lang.String) r6, (double) r2, (java.lang.String) r9)     // Catch:{ NumberFormatException -> 0x092e }
            goto L_0x0994
        L_0x092e:
            r0 = move-exception
        L_0x092f:
            goto L_0x0973
        L_0x0930:
            r33 = r3
            if (r5 == 0) goto L_0x0973
            if (r14 == 0) goto L_0x0954
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x09a7 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x09a7 }
            r2.<init>()     // Catch:{ RuntimeException -> 0x09a7 }
            r2.append(r13)     // Catch:{ RuntimeException -> 0x09a7 }
            r2.append(r6)     // Catch:{ RuntimeException -> 0x09a7 }
            r2.append(r11)     // Catch:{ RuntimeException -> 0x09a7 }
            double r3 = java.lang.Double.parseDouble(r9)     // Catch:{ RuntimeException -> 0x09a7 }
            r2.append(r3)     // Catch:{ RuntimeException -> 0x09a7 }
            java.lang.String r2 = r2.toString()     // Catch:{ RuntimeException -> 0x09a7 }
            r0.println(r2)     // Catch:{ RuntimeException -> 0x09a7 }
        L_0x0954:
            long r2 = java.lang.Long.parseLong(r9)     // Catch:{ NumberFormatException -> 0x095c }
            r1.number((java.lang.String) r6, (long) r2, (java.lang.String) r9)     // Catch:{ NumberFormatException -> 0x095c }
            goto L_0x0994
        L_0x095c:
            r0 = move-exception
            goto L_0x0973
        L_0x095e:
            r0 = move-exception
            r33 = r3
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x0967:
            r33 = r3
            r34 = r4
            r24 = r5
            r25 = r6
            r6 = r20
            r20 = r8
        L_0x0973:
            if (r14 == 0) goto L_0x0991
            java.io.PrintStream r0 = java.lang.System.out     // Catch:{ RuntimeException -> 0x09a7 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x09a7 }
            r2.<init>()     // Catch:{ RuntimeException -> 0x09a7 }
            java.lang.String r3 = "string: "
            r2.append(r3)     // Catch:{ RuntimeException -> 0x09a7 }
            r2.append(r6)     // Catch:{ RuntimeException -> 0x09a7 }
            r2.append(r11)     // Catch:{ RuntimeException -> 0x09a7 }
            r2.append(r9)     // Catch:{ RuntimeException -> 0x09a7 }
            java.lang.String r2 = r2.toString()     // Catch:{ RuntimeException -> 0x09a7 }
            r0.println(r2)     // Catch:{ RuntimeException -> 0x09a7 }
        L_0x0991:
            r1.string(r6, r9)     // Catch:{ RuntimeException -> 0x09a7 }
        L_0x0994:
            r18 = 0
            r10 = r7
        L_0x0997:
            r2 = r38
            r9 = r19
            r0 = r20
            r6 = r25
            r5 = r32
            r3 = r33
            r4 = r34
            goto L_0x07a6
        L_0x09a7:
            r0 = move-exception
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x09ad:
            r0 = move-exception
            r33 = r3
            r24 = r5
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x09b7:
            r33 = r3
            r32 = r5
            r20 = r8
            goto L_0x09cc
        L_0x09be:
            r0 = move-exception
            r33 = r3
            r32 = r5
            r11 = r23
            r8 = r31
            goto L_0x09df
        L_0x09c8:
            r33 = r3
            r32 = r5
        L_0x09cc:
            r13 = r22
            goto L_0x09e4
        L_0x09d0:
            r0 = move-exception
            r33 = r3
            r32 = r4
            r31 = r8
            r24 = r9
            r23 = r11
            r10 = r20
            r12 = r21
        L_0x09df:
            r13 = r0
            r31 = r8
            r23 = r11
        L_0x09e4:
            com.badlogic.gdx.utils.JsonValue r0 = r1.root
            r2 = 0
            r1.root = r2
            r1.current = r2
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.JsonValue> r2 = r1.lastChild
            r2.clear()
            r2 = r33
            if (r7 >= r2) goto L_0x0a49
            r3 = 1
            r4 = 0
        L_0x09f6:
            if (r4 >= r7) goto L_0x0a05
            r5 = r38
            char r6 = r5[r4]
            r8 = 10
            if (r6 != r8) goto L_0x0a02
            int r3 = r3 + 1
        L_0x0a02:
            int r4 = r4 + 1
            goto L_0x09f6
        L_0x0a05:
            r5 = r38
            int r4 = r7 + -32
            r6 = 0
            int r4 = java.lang.Math.max(r6, r4)
            com.badlogic.gdx.utils.SerializationException r6 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Error parsing JSON on line "
            r8.append(r9)
            r8.append(r3)
            java.lang.String r9 = " near: "
            r8.append(r9)
            java.lang.String r9 = new java.lang.String
            int r11 = r7 - r4
            r9.<init>(r5, r4, r11)
            r8.append(r9)
            java.lang.String r9 = "*ERROR*"
            r8.append(r9)
            java.lang.String r9 = new java.lang.String
            r11 = 64
            int r15 = r2 - r7
            int r11 = java.lang.Math.min(r11, r15)
            r9.<init>(r5, r7, r11)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r6.<init>(r8, r13)
            throw r6
        L_0x0a49:
            r5 = r38
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.JsonValue> r3 = r1.elements
            int r3 = r3.size
            if (r3 == 0) goto L_0x0a76
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.JsonValue> r3 = r1.elements
            java.lang.Object r3 = r3.peek()
            com.badlogic.gdx.utils.JsonValue r3 = (com.badlogic.gdx.utils.JsonValue) r3
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.JsonValue> r4 = r1.elements
            r4.clear()
            if (r3 == 0) goto L_0x0a6e
            boolean r4 = r3.isObject()
            if (r4 == 0) goto L_0x0a6e
            com.badlogic.gdx.utils.SerializationException r4 = new com.badlogic.gdx.utils.SerializationException
            java.lang.String r6 = "Error parsing JSON, unmatched brace."
            r4.<init>((java.lang.String) r6)
            throw r4
        L_0x0a6e:
            com.badlogic.gdx.utils.SerializationException r4 = new com.badlogic.gdx.utils.SerializationException
            java.lang.String r6 = "Error parsing JSON, unmatched bracket."
            r4.<init>((java.lang.String) r6)
            throw r4
        L_0x0a76:
            if (r13 != 0) goto L_0x0a79
            return r0
        L_0x0a79:
            com.badlogic.gdx.utils.SerializationException r3 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "Error parsing JSON: "
            r4.append(r6)
            java.lang.String r6 = new java.lang.String
            r6.<init>(r5)
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4, r13)
            goto L_0x0a96
        L_0x0a95:
            throw r3
        L_0x0a96:
            goto L_0x0a95
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.JsonReader.parse(char[], int, int):com.badlogic.gdx.utils.JsonValue");
    }

    private static byte[] init__json_actions_0() {
        return new byte[]{0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 1, 8, 2, 0, 7, 2, 0, 8, 2, 1, 3, 2, 1, 5};
    }

    private static short[] init__json_key_offsets_0() {
        return new short[]{0, 0, 11, 13, 14, 16, 25, 31, 37, 39, 50, 57, 64, 73, 74, 83, 85, 87, 96, 98, 100, 101, 103, 105, 116, 123, 130, 141, 142, 153, 155, 157, 168, 170, 172, 174, 179, 184, 184};
    }

    private static char[] init__json_trans_keys_0() {
        return new char[]{13, ' ', '\"', ',', '/', ':', '[', ']', '{', 9, 10, '*', '/', '\"', '*', '/', 13, ' ', '\"', ',', '/', ':', '}', 9, 10, 13, ' ', '/', ':', 9, 10, 13, ' ', '/', ':', 9, 10, '*', '/', 13, ' ', '\"', ',', '/', ':', '[', ']', '{', 9, 10, 9, 10, 13, ' ', ',', '/', '}', 9, 10, 13, ' ', ',', '/', '}', 13, ' ', '\"', ',', '/', ':', '}', 9, 10, '\"', 13, ' ', '\"', ',', '/', ':', '}', 9, 10, '*', '/', '*', '/', 13, ' ', '\"', ',', '/', ':', '}', 9, 10, '*', '/', '*', '/', '\"', '*', '/', '*', '/', 13, ' ', '\"', ',', '/', ':', '[', ']', '{', 9, 10, 9, 10, 13, ' ', ',', '/', ']', 9, 10, 13, ' ', ',', '/', ']', 13, ' ', '\"', ',', '/', ':', '[', ']', '{', 9, 10, '\"', 13, ' ', '\"', ',', '/', ':', '[', ']', '{', 9, 10, '*', '/', '*', '/', 13, ' ', '\"', ',', '/', ':', '[', ']', '{', 9, 10, '*', '/', '*', '/', '*', '/', 13, ' ', '/', 9, 10, 13, ' ', '/', 9, 10, 0};
    }

    private static byte[] init__json_single_lengths_0() {
        return new byte[]{0, 9, 2, 1, 2, 7, 4, 4, 2, 9, 7, 7, 7, 1, 7, 2, 2, 7, 2, 2, 1, 2, 2, 9, 7, 7, 9, 1, 9, 2, 2, 9, 2, 2, 2, 3, 3, 0, 0};
    }

    private static byte[] init__json_range_lengths_0() {
        return new byte[]{0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0};
    }

    private static short[] init__json_index_offsets_0() {
        return new short[]{0, 0, 11, 14, 16, 19, 28, 34, 40, 43, 54, 62, 70, 79, 81, 90, 93, 96, 105, 108, 111, 113, 116, 119, 130, 138, 146, 157, 159, 170, 173, 176, 187, 190, 193, 196, 201, 206, 207};
    }

    private static byte[] init__json_indicies_0() {
        return new byte[]{1, 1, 2, 3, 4, 3, 5, 3, 6, 1, 0, 7, 7, 3, 8, 3, 9, 9, 3, 11, 11, 12, 13, 14, 3, 15, 11, 10, 16, 16, 17, 18, 16, 3, 19, 19, 20, 21, 19, 3, 22, 22, 3, 21, 21, 24, 3, 25, 3, 26, 3, 27, 21, 23, 28, 29, 29, 28, 30, 31, 32, 3, 33, 34, 34, 33, 13, 35, 15, 3, 34, 34, 12, 36, 37, 3, 15, 34, 10, 16, 3, 36, 36, 12, 3, 38, 3, 3, 36, 10, 39, 39, 3, 40, 40, 3, 13, 13, 12, 3, 41, 3, 15, 13, 10, 42, 42, 3, 43, 43, 3, 28, 3, 44, 44, 3, 45, 45, 3, 47, 47, 48, 49, 50, 3, 51, 52, 53, 47, 46, 54, 55, 55, 54, 56, 57, 58, 3, 59, 60, 60, 59, 49, 61, 52, 3, 60, 60, 48, 62, 63, 3, 51, 52, 53, 60, 46, 54, 3, 62, 62, 48, 3, 64, 3, 51, 3, 53, 62, 46, 65, 65, 3, 66, 66, 3, 49, 49, 48, 3, 67, 3, 51, 52, 53, 49, 46, 68, 68, 3, 69, 69, 3, 70, 70, 3, 8, 8, 71, 8, 3, 72, 72, 73, 72, 3, 3, 3, 0};
    }

    private static byte[] init__json_trans_targs_0() {
        return new byte[]{35, 1, 3, 0, 4, 36, 36, 36, 36, 1, 6, 5, 13, 17, 22, 37, 7, 8, 9, 7, 8, 9, 7, 10, 20, 21, 11, 11, 11, 12, 17, 19, 37, 11, 12, 19, 14, 16, 15, 14, 12, 18, 17, 11, 9, 5, 24, 23, 27, 31, 34, 25, 38, 25, 25, 26, 31, 33, 38, 25, 26, 33, 28, 30, 29, 28, 26, 32, 31, 25, 23, 2, 36, 2};
    }

    private static byte[] init__json_trans_actions_0() {
        return new byte[]{13, 0, 15, 0, 0, 7, 3, 11, 1, 11, 17, 0, 20, 0, 0, 5, 1, 1, 1, 0, 0, 0, 11, 13, 15, 0, 7, 3, 1, 1, 1, 1, 23, 0, 0, 0, 0, 0, 0, 11, 11, 0, 11, 11, 11, 11, 13, 0, 15, 0, 0, 7, 9, 3, 1, 1, 1, 1, 26, 0, 0, 0, 0, 0, 0, 11, 11, 0, 11, 11, 11, 1, 0, 0};
    }

    private static byte[] init__json_eof_actions_0() {
        return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0};
    }

    private void addChild(String name, JsonValue child) {
        child.setName(name);
        JsonValue jsonValue = this.current;
        if (jsonValue == null) {
            this.current = child;
            this.root = child;
        } else if (jsonValue.isArray() || this.current.isObject()) {
            JsonValue jsonValue2 = this.current;
            child.parent = jsonValue2;
            if (jsonValue2.size == 0) {
                this.current.child = child;
            } else {
                JsonValue last = this.lastChild.pop();
                last.next = child;
                child.prev = last;
            }
            this.lastChild.add(child);
            this.current.size++;
        } else {
            this.root = this.current;
        }
    }

    /* access modifiers changed from: protected */
    public void startObject(String name) {
        JsonValue value = new JsonValue(JsonValue.ValueType.object);
        if (this.current != null) {
            addChild(name, value);
        }
        this.elements.add(value);
        this.current = value;
    }

    /* access modifiers changed from: protected */
    public void startArray(String name) {
        JsonValue value = new JsonValue(JsonValue.ValueType.array);
        if (this.current != null) {
            addChild(name, value);
        }
        this.elements.add(value);
        this.current = value;
    }

    /* access modifiers changed from: protected */
    public void pop() {
        this.root = this.elements.pop();
        if (this.current.size > 0) {
            this.lastChild.pop();
        }
        this.current = this.elements.size > 0 ? this.elements.peek() : null;
    }

    /* access modifiers changed from: protected */
    public void string(String name, String value) {
        addChild(name, new JsonValue(value));
    }

    /* access modifiers changed from: protected */
    public void number(String name, double value, String stringValue) {
        addChild(name, new JsonValue(value, stringValue));
    }

    /* access modifiers changed from: protected */
    public void number(String name, long value, String stringValue) {
        addChild(name, new JsonValue(value, stringValue));
    }

    /* access modifiers changed from: protected */
    public void bool(String name, boolean value) {
        addChild(name, new JsonValue(value));
    }

    private String unescape(String value) {
        int length = value.length();
        StringBuilder buffer = new StringBuilder(length + 16);
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            char c = value.charAt(i);
            if (c != '\\') {
                buffer.append(c);
                i = i2;
            } else if (i2 == length) {
                break;
            } else {
                int i3 = i2 + 1;
                char c2 = value.charAt(i2);
                if (c2 == 'u') {
                    buffer.append(Character.toChars(Integer.parseInt(value.substring(i3, i3 + 4), 16)));
                    i = i3 + 4;
                } else {
                    if (!(c2 == '\"' || c2 == '/' || c2 == '\\')) {
                        if (c2 == 'b') {
                            c2 = 8;
                        } else if (c2 == 'f') {
                            c2 = 12;
                        } else if (c2 == 'n') {
                            c2 = 10;
                        } else if (c2 == 'r') {
                            c2 = 13;
                        } else if (c2 == 't') {
                            c2 = 9;
                        } else {
                            throw new SerializationException("Illegal escaped character: \\" + c2);
                        }
                    }
                    buffer.append(c2);
                    i = i3;
                }
            }
        }
        return buffer.toString();
    }
}
