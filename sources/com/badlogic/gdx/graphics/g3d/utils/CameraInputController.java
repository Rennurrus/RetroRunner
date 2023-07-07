package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraInputController extends GestureDetector {
    public int activateKey;
    protected boolean activatePressed;
    public boolean alwaysScroll;
    public boolean autoUpdate;
    public int backwardKey;
    protected boolean backwardPressed;
    protected int button;
    public Camera camera;
    public int forwardButton;
    public int forwardKey;
    protected boolean forwardPressed;
    public boolean forwardTarget;
    protected final CameraGestureListener gestureListener;
    private boolean multiTouch;
    public float pinchZoomFactor;
    public float rotateAngle;
    public int rotateButton;
    public int rotateLeftKey;
    protected boolean rotateLeftPressed;
    public int rotateRightKey;
    protected boolean rotateRightPressed;
    public float scrollFactor;
    public boolean scrollTarget;
    private float startX;
    private float startY;
    public Vector3 target;
    private final Vector3 tmpV1;
    private final Vector3 tmpV2;
    private int touched;
    public int translateButton;
    public boolean translateTarget;
    public float translateUnits;

    protected static class CameraGestureListener extends GestureDetector.GestureAdapter {
        public CameraInputController controller;
        private float previousZoom;

        protected CameraGestureListener() {
        }

        public boolean touchDown(float x, float y, int pointer, int button) {
            this.previousZoom = 0.0f;
            return false;
        }

        public boolean tap(float x, float y, int count, int button) {
            return false;
        }

        public boolean longPress(float x, float y) {
            return false;
        }

        public boolean fling(float velocityX, float velocityY, int button) {
            return false;
        }

        public boolean pan(float x, float y, float deltaX, float deltaY) {
            return false;
        }

        public boolean zoom(float initialDistance, float distance) {
            float newZoom = distance - initialDistance;
            float amount = newZoom - this.previousZoom;
            this.previousZoom = newZoom;
            float w = (float) Gdx.graphics.getWidth();
            float h = (float) Gdx.graphics.getHeight();
            return this.controller.pinchZoom(amount / (w > h ? h : w));
        }

        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    }

    protected CameraInputController(CameraGestureListener gestureListener2, Camera camera2) {
        super(gestureListener2);
        this.rotateButton = 0;
        this.rotateAngle = 360.0f;
        this.translateButton = 1;
        this.translateUnits = 10.0f;
        this.forwardButton = 2;
        this.activateKey = 0;
        this.alwaysScroll = true;
        this.scrollFactor = -0.1f;
        this.pinchZoomFactor = 10.0f;
        this.autoUpdate = true;
        this.target = new Vector3();
        this.translateTarget = true;
        this.forwardTarget = true;
        this.scrollTarget = false;
        this.forwardKey = 51;
        this.backwardKey = 47;
        this.rotateRightKey = 29;
        this.rotateLeftKey = 32;
        this.button = -1;
        this.tmpV1 = new Vector3();
        this.tmpV2 = new Vector3();
        this.gestureListener = gestureListener2;
        this.gestureListener.controller = this;
        this.camera = camera2;
    }

    public CameraInputController(Camera camera2) {
        this(new CameraGestureListener(), camera2);
    }

    public void update() {
        if (this.rotateRightPressed || this.rotateLeftPressed || this.forwardPressed || this.backwardPressed) {
            float delta = Gdx.graphics.getDeltaTime();
            if (this.rotateRightPressed) {
                Camera camera2 = this.camera;
                camera2.rotate(camera2.up, (-delta) * this.rotateAngle);
            }
            if (this.rotateLeftPressed) {
                Camera camera3 = this.camera;
                camera3.rotate(camera3.up, this.rotateAngle * delta);
            }
            if (this.forwardPressed) {
                Camera camera4 = this.camera;
                camera4.translate(this.tmpV1.set(camera4.direction).scl(this.translateUnits * delta));
                if (this.forwardTarget) {
                    this.target.add(this.tmpV1);
                }
            }
            if (this.backwardPressed) {
                Camera camera5 = this.camera;
                camera5.translate(this.tmpV1.set(camera5.direction).scl((-delta) * this.translateUnits));
                if (this.forwardTarget) {
                    this.target.add(this.tmpV1);
                }
            }
            if (this.autoUpdate) {
                this.camera.update();
            }
        }
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button2) {
        this.touched |= 1 << pointer;
        this.multiTouch = !MathUtils.isPowerOfTwo(this.touched);
        if (this.multiTouch) {
            this.button = -1;
        } else if (this.button < 0 && (this.activateKey == 0 || this.activatePressed)) {
            this.startX = (float) screenX;
            this.startY = (float) screenY;
            this.button = button2;
        }
        if (super.touchDown(screenX, screenY, pointer, button2) || this.activateKey == 0 || this.activatePressed) {
            return true;
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button2) {
        this.touched &= (1 << pointer) ^ -1;
        this.multiTouch = !MathUtils.isPowerOfTwo(this.touched);
        if (button2 == this.button) {
            this.button = -1;
        }
        if (super.touchUp(screenX, screenY, pointer, button2) || this.activatePressed) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean process(float deltaX, float deltaY, int button2) {
        if (button2 == this.rotateButton) {
            this.tmpV1.set(this.camera.direction).crs(this.camera.up).y = 0.0f;
            this.camera.rotateAround(this.target, this.tmpV1.nor(), this.rotateAngle * deltaY);
            this.camera.rotateAround(this.target, Vector3.Y, (-this.rotateAngle) * deltaX);
        } else if (button2 == this.translateButton) {
            Camera camera2 = this.camera;
            camera2.translate(this.tmpV1.set(camera2.direction).crs(this.camera.up).nor().scl((-deltaX) * this.translateUnits));
            Camera camera3 = this.camera;
            camera3.translate(this.tmpV2.set(camera3.up).scl((-deltaY) * this.translateUnits));
            if (this.translateTarget) {
                this.target.add(this.tmpV1).add(this.tmpV2);
            }
        } else if (button2 == this.forwardButton) {
            Camera camera4 = this.camera;
            camera4.translate(this.tmpV1.set(camera4.direction).scl(this.translateUnits * deltaY));
            if (this.forwardTarget) {
                this.target.add(this.tmpV1);
            }
        }
        if (!this.autoUpdate) {
            return true;
        }
        this.camera.update();
        return true;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean result = super.touchDragged(screenX, screenY, pointer);
        if (result || this.button < 0) {
            return result;
        }
        float deltaX = (((float) screenX) - this.startX) / ((float) Gdx.graphics.getWidth());
        float deltaY = (this.startY - ((float) screenY)) / ((float) Gdx.graphics.getHeight());
        this.startX = (float) screenX;
        this.startY = (float) screenY;
        return process(deltaX, deltaY, this.button);
    }

    public boolean scrolled(int amount) {
        return zoom(((float) amount) * this.scrollFactor * this.translateUnits);
    }

    public boolean zoom(float amount) {
        if (!this.alwaysScroll && this.activateKey != 0 && !this.activatePressed) {
            return false;
        }
        Camera camera2 = this.camera;
        camera2.translate(this.tmpV1.set(camera2.direction).scl(amount));
        if (this.scrollTarget) {
            this.target.add(this.tmpV1);
        }
        if (!this.autoUpdate) {
            return true;
        }
        this.camera.update();
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean pinchZoom(float amount) {
        return zoom(this.pinchZoomFactor * amount);
    }

    public boolean keyDown(int keycode) {
        if (keycode == this.activateKey) {
            this.activatePressed = true;
        }
        if (keycode == this.forwardKey) {
            this.forwardPressed = true;
            return false;
        } else if (keycode == this.backwardKey) {
            this.backwardPressed = true;
            return false;
        } else if (keycode == this.rotateRightKey) {
            this.rotateRightPressed = true;
            return false;
        } else if (keycode != this.rotateLeftKey) {
            return false;
        } else {
            this.rotateLeftPressed = true;
            return false;
        }
    }

    public boolean keyUp(int keycode) {
        if (keycode == this.activateKey) {
            this.activatePressed = false;
            this.button = -1;
        }
        if (keycode == this.forwardKey) {
            this.forwardPressed = false;
        } else if (keycode == this.backwardKey) {
            this.backwardPressed = false;
        } else if (keycode == this.rotateRightKey) {
            this.rotateRightPressed = false;
        } else if (keycode == this.rotateLeftKey) {
            this.rotateLeftPressed = false;
        }
        return false;
    }
}
