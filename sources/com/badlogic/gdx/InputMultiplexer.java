package com.badlogic.gdx;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

public class InputMultiplexer implements InputProcessor {
    private SnapshotArray<InputProcessor> processors = new SnapshotArray<>(4);

    public InputMultiplexer() {
    }

    public InputMultiplexer(InputProcessor... processors2) {
        this.processors.addAll((T[]) processors2);
    }

    public void addProcessor(int index, InputProcessor processor) {
        if (processor != null) {
            this.processors.insert(index, processor);
            return;
        }
        throw new NullPointerException("processor cannot be null");
    }

    public void removeProcessor(int index) {
        this.processors.removeIndex(index);
    }

    public void addProcessor(InputProcessor processor) {
        if (processor != null) {
            this.processors.add(processor);
            return;
        }
        throw new NullPointerException("processor cannot be null");
    }

    public void removeProcessor(InputProcessor processor) {
        this.processors.removeValue(processor, true);
    }

    public int size() {
        return this.processors.size;
    }

    public void clear() {
        this.processors.clear();
    }

    public void setProcessors(InputProcessor... processors2) {
        this.processors.clear();
        this.processors.addAll((T[]) processors2);
    }

    public void setProcessors(Array<InputProcessor> processors2) {
        this.processors.clear();
        this.processors.addAll(processors2);
    }

    public SnapshotArray<InputProcessor> getProcessors() {
        return this.processors;
    }

    /* JADX INFO: finally extract failed */
    public boolean keyDown(int keycode) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).keyDown(keycode)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean keyUp(int keycode) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).keyUp(keycode)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean keyTyped(char character) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).keyTyped(character)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).touchDown(screenX, screenY, pointer, button)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).touchUp(screenX, screenY, pointer, button)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).touchDragged(screenX, screenY, pointer)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean mouseMoved(int screenX, int screenY) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).mouseMoved(screenX, screenY)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean scrolled(int amount) {
        Object[] items = this.processors.begin();
        try {
            int n = this.processors.size;
            for (int i = 0; i < n; i++) {
                if (((InputProcessor) items[i]).scrolled(amount)) {
                    this.processors.end();
                    return true;
                }
            }
            this.processors.end();
            return false;
        } catch (Throwable th) {
            this.processors.end();
            throw th;
        }
    }
}
