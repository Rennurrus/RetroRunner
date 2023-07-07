package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Window extends Table {
    private static final int MOVE = 32;
    private static final Vector2 tmpPosition = new Vector2();
    private static final Vector2 tmpSize = new Vector2();
    protected boolean dragging;
    boolean drawTitleTable;
    protected int edge;
    boolean isModal;
    boolean isMovable;
    boolean isResizable;
    boolean keepWithinStage;
    int resizeBorder;
    private WindowStyle style;
    Label titleLabel;
    Table titleTable;

    public Window(String title, Skin skin) {
        this(title, (WindowStyle) skin.get(WindowStyle.class));
        setSkin(skin);
    }

    public Window(String title, Skin skin, String styleName) {
        this(title, (WindowStyle) skin.get(styleName, WindowStyle.class));
        setSkin(skin);
    }

    public Window(String title, WindowStyle style2) {
        this.isMovable = true;
        this.resizeBorder = 8;
        this.keepWithinStage = true;
        if (title != null) {
            setTouchable(Touchable.enabled);
            setClip(true);
            this.titleLabel = new Label((CharSequence) title, new Label.LabelStyle(style2.titleFont, style2.titleFontColor));
            this.titleLabel.setEllipsis(true);
            this.titleTable = new Table() {
                public void draw(Batch batch, float parentAlpha) {
                    if (Window.this.drawTitleTable) {
                        super.draw(batch, parentAlpha);
                    }
                }
            };
            this.titleTable.add(this.titleLabel).expandX().fillX().minWidth(0.0f);
            addActor(this.titleTable);
            setStyle(style2);
            setWidth(150.0f);
            setHeight(150.0f);
            addCaptureListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Window.this.toFront();
                    return false;
                }
            });
            addListener(new InputListener() {
                float lastX;
                float lastY;
                float startX;
                float startY;

                private void updateEdge(float x, float y) {
                    float border = ((float) Window.this.resizeBorder) / 2.0f;
                    float width = Window.this.getWidth();
                    float height = Window.this.getHeight();
                    float padTop = Window.this.getPadTop();
                    float padLeft = Window.this.getPadLeft();
                    float padBottom = Window.this.getPadBottom();
                    float left = padLeft;
                    float right = width - Window.this.getPadRight();
                    float bottom = padBottom;
                    Window window = Window.this;
                    window.edge = 0;
                    if (window.isResizable && x >= left - border && x <= right + border && y >= bottom - border) {
                        if (x < left + border) {
                            Window.this.edge |= 8;
                        }
                        if (x > right - border) {
                            Window.this.edge |= 16;
                        }
                        if (y < bottom + border) {
                            Window.this.edge |= 4;
                        }
                        if (Window.this.edge != 0) {
                            border += 25.0f;
                        }
                        if (x < left + border) {
                            Window.this.edge |= 8;
                        }
                        if (x > right - border) {
                            Window.this.edge |= 16;
                        }
                        if (y < bottom + border) {
                            Window.this.edge |= 4;
                        }
                    }
                    if (Window.this.isMovable && Window.this.edge == 0 && y <= height && y >= height - padTop && x >= left && x <= right) {
                        Window.this.edge = 32;
                    }
                }

                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == 0) {
                        updateEdge(x, y);
                        Window window = Window.this;
                        window.dragging = window.edge != 0;
                        this.startX = x;
                        this.startY = y;
                        this.lastX = x - Window.this.getWidth();
                        this.lastY = y - Window.this.getHeight();
                    }
                    if (Window.this.edge != 0 || Window.this.isModal) {
                        return true;
                    }
                    return false;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Window.this.dragging = false;
                }

                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    if (Window.this.dragging) {
                        float width = Window.this.getWidth();
                        float height = Window.this.getHeight();
                        float windowX = Window.this.getX();
                        float windowY = Window.this.getY();
                        float minWidth = Window.this.getMinWidth();
                        float maxWidth = Window.this.getMaxWidth();
                        float minHeight = Window.this.getMinHeight();
                        float maxHeight = Window.this.getMaxHeight();
                        Stage stage = Window.this.getStage();
                        boolean clampPosition = Window.this.keepWithinStage && stage != null && Window.this.getParent() == stage.getRoot();
                        if ((Window.this.edge & 32) != 0) {
                            windowX += x - this.startX;
                            windowY += y - this.startY;
                        }
                        if ((Window.this.edge & 8) != 0) {
                            float amountX = x - this.startX;
                            if (width - amountX < minWidth) {
                                amountX = -(minWidth - width);
                            }
                            if (clampPosition && windowX + amountX < 0.0f) {
                                amountX = -windowX;
                            }
                            width -= amountX;
                            windowX += amountX;
                        }
                        if ((Window.this.edge & 4) != 0) {
                            float amountY = y - this.startY;
                            if (height - amountY < minHeight) {
                                amountY = -(minHeight - height);
                            }
                            if (clampPosition && windowY + amountY < 0.0f) {
                                amountY = -windowY;
                            }
                            height -= amountY;
                            windowY += amountY;
                        }
                        if ((Window.this.edge & 16) != 0) {
                            float amountX2 = (x - this.lastX) - width;
                            if (width + amountX2 < minWidth) {
                                amountX2 = minWidth - width;
                            }
                            if (clampPosition && windowX + width + amountX2 > stage.getWidth()) {
                                amountX2 = (stage.getWidth() - windowX) - width;
                            }
                            width += amountX2;
                        }
                        if ((Window.this.edge & 2) != 0) {
                            float amountY2 = (y - this.lastY) - height;
                            if (height + amountY2 < minHeight) {
                                amountY2 = minHeight - height;
                            }
                            if (clampPosition && windowY + height + amountY2 > stage.getHeight()) {
                                amountY2 = (stage.getHeight() - windowY) - height;
                            }
                            height += amountY2;
                        }
                        Window.this.setBounds((float) Math.round(windowX), (float) Math.round(windowY), (float) Math.round(width), (float) Math.round(height));
                    }
                }

                public boolean mouseMoved(InputEvent event, float x, float y) {
                    updateEdge(x, y);
                    return Window.this.isModal;
                }

                public boolean scrolled(InputEvent event, float x, float y, int amount) {
                    return Window.this.isModal;
                }

                public boolean keyDown(InputEvent event, int keycode) {
                    return Window.this.isModal;
                }

                public boolean keyUp(InputEvent event, int keycode) {
                    return Window.this.isModal;
                }

                public boolean keyTyped(InputEvent event, char character) {
                    return Window.this.isModal;
                }
            });
            return;
        }
        throw new IllegalArgumentException("title cannot be null.");
    }

    public void setStyle(WindowStyle style2) {
        if (style2 != null) {
            this.style = style2;
            setBackground(style2.background);
            this.titleLabel.setStyle(new Label.LabelStyle(style2.titleFont, style2.titleFontColor));
            invalidateHierarchy();
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public WindowStyle getStyle() {
        return this.style;
    }

    public void keepWithinStage() {
        Stage stage;
        if (this.keepWithinStage && (stage = getStage()) != null) {
            Camera camera = stage.getCamera();
            if (camera instanceof OrthographicCamera) {
                OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
                float parentWidth = stage.getWidth();
                float parentHeight = stage.getHeight();
                if (getX(16) - camera.position.x > (parentWidth / 2.0f) / orthographicCamera.zoom) {
                    setPosition(camera.position.x + ((parentWidth / 2.0f) / orthographicCamera.zoom), getY(16), 16);
                }
                if (getX(8) - camera.position.x < ((-parentWidth) / 2.0f) / orthographicCamera.zoom) {
                    setPosition(camera.position.x - ((parentWidth / 2.0f) / orthographicCamera.zoom), getY(8), 8);
                }
                if (getY(2) - camera.position.y > (parentHeight / 2.0f) / orthographicCamera.zoom) {
                    setPosition(getX(2), camera.position.y + ((parentHeight / 2.0f) / orthographicCamera.zoom), 2);
                }
                if (getY(4) - camera.position.y < ((-parentHeight) / 2.0f) / orthographicCamera.zoom) {
                    setPosition(getX(4), camera.position.y - ((parentHeight / 2.0f) / orthographicCamera.zoom), 4);
                }
            } else if (getParent() == stage.getRoot()) {
                float parentWidth2 = stage.getWidth();
                float parentHeight2 = stage.getHeight();
                if (getX() < 0.0f) {
                    setX(0.0f);
                }
                if (getRight() > parentWidth2) {
                    setX(parentWidth2 - getWidth());
                }
                if (getY() < 0.0f) {
                    setY(0.0f);
                }
                if (getTop() > parentHeight2) {
                    setY(parentHeight2 - getHeight());
                }
            }
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        Stage stage = getStage();
        if (stage != null && stage.getKeyboardFocus() == null) {
            stage.setKeyboardFocus(this);
        }
        keepWithinStage();
        if (this.style.stageBackground != null) {
            stageToLocalCoordinates(tmpPosition.set(0.0f, 0.0f));
            stageToLocalCoordinates(tmpSize.set(stage.getWidth(), stage.getHeight()));
            drawStageBackground(batch, parentAlpha, getX() + tmpPosition.x, getY() + tmpPosition.y, getX() + tmpSize.x, getY() + tmpSize.y);
        }
        super.draw(batch, parentAlpha);
    }

    /* access modifiers changed from: protected */
    public void drawStageBackground(Batch batch, float parentAlpha, float x, float y, float width, float height) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        this.style.stageBackground.draw(batch, x, y, width, height);
    }

    /* access modifiers changed from: protected */
    public void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        super.drawBackground(batch, parentAlpha, x, y);
        this.titleTable.getColor().a = getColor().a;
        float padTop = getPadTop();
        float padLeft = getPadLeft();
        this.titleTable.setSize((getWidth() - padLeft) - getPadRight(), padTop);
        this.titleTable.setPosition(padLeft, getHeight() - padTop);
        this.drawTitleTable = true;
        this.titleTable.draw(batch, parentAlpha);
        this.drawTitleTable = false;
    }

    public Actor hit(float x, float y, boolean touchable) {
        if (!isVisible()) {
            return null;
        }
        Actor hit = super.hit(x, y, touchable);
        if (hit == null && this.isModal && (!touchable || getTouchable() == Touchable.enabled)) {
            return this;
        }
        float height = getHeight();
        if (hit != null && hit != this && y <= height && y >= height - getPadTop() && x >= 0.0f && x <= getWidth()) {
            Actor current = hit;
            while (current.getParent() != this) {
                current = current.getParent();
            }
            if (getCell(current) != null) {
                return this;
            }
        }
        return hit;
    }

    public boolean isMovable() {
        return this.isMovable;
    }

    public void setMovable(boolean isMovable2) {
        this.isMovable = isMovable2;
    }

    public boolean isModal() {
        return this.isModal;
    }

    public void setModal(boolean isModal2) {
        this.isModal = isModal2;
    }

    public void setKeepWithinStage(boolean keepWithinStage2) {
        this.keepWithinStage = keepWithinStage2;
    }

    public boolean isResizable() {
        return this.isResizable;
    }

    public void setResizable(boolean isResizable2) {
        this.isResizable = isResizable2;
    }

    public void setResizeBorder(int resizeBorder2) {
        this.resizeBorder = resizeBorder2;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public float getPrefWidth() {
        return Math.max(super.getPrefWidth(), this.titleTable.getPrefWidth() + getPadLeft() + getPadRight());
    }

    public Table getTitleTable() {
        return this.titleTable;
    }

    public Label getTitleLabel() {
        return this.titleLabel;
    }

    public static class WindowStyle {
        public Drawable background;
        public Drawable stageBackground;
        public BitmapFont titleFont;
        public Color titleFontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

        public WindowStyle() {
        }

        public WindowStyle(BitmapFont titleFont2, Color titleFontColor2, Drawable background2) {
            this.background = background2;
            this.titleFont = titleFont2;
            this.titleFontColor.set(titleFontColor2);
        }

        public WindowStyle(WindowStyle style) {
            this.background = style.background;
            this.titleFont = style.titleFont;
            this.titleFontColor = new Color(style.titleFontColor);
        }
    }
}
