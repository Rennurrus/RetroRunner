package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public class ScrollPane extends WidgetGroup {
    float amountX;
    float amountY;
    float areaHeight;
    float areaWidth;
    boolean cancelTouchFocus;
    private boolean clamp;
    boolean disableX;
    boolean disableY;
    int draggingPointer;
    float fadeAlpha;
    float fadeAlphaSeconds;
    float fadeDelay;
    float fadeDelaySeconds;
    boolean fadeScrollBars;
    boolean flickScroll;
    private ActorGestureListener flickScrollListener;
    float flingTime;
    float flingTimer;
    private boolean forceScrollX;
    private boolean forceScrollY;
    final Rectangle hKnobBounds;
    final Rectangle hScrollBounds;
    boolean hScrollOnBottom;
    final Vector2 lastPoint;
    float maxX;
    float maxY;
    private float overscrollDistance;
    private float overscrollSpeedMax;
    private float overscrollSpeedMin;
    private boolean overscrollX;
    private boolean overscrollY;
    boolean scrollBarTouch;
    boolean scrollX;
    boolean scrollY;
    private boolean scrollbarsOnTop;
    boolean smoothScrolling;
    private ScrollPaneStyle style;
    boolean touchScrollH;
    boolean touchScrollV;
    final Rectangle vKnobBounds;
    final Rectangle vScrollBounds;
    boolean vScrollOnRight;
    private boolean variableSizeKnobs;
    float velocityX;
    float velocityY;
    float visualAmountX;
    float visualAmountY;
    private Actor widget;
    private final Rectangle widgetAreaBounds;
    private final Rectangle widgetCullingArea;

    public ScrollPane(Actor widget2) {
        this(widget2, new ScrollPaneStyle());
    }

    public ScrollPane(Actor widget2, Skin skin) {
        this(widget2, (ScrollPaneStyle) skin.get(ScrollPaneStyle.class));
    }

    public ScrollPane(Actor widget2, Skin skin, String styleName) {
        this(widget2, (ScrollPaneStyle) skin.get(styleName, ScrollPaneStyle.class));
    }

    public ScrollPane(Actor widget2, ScrollPaneStyle style2) {
        this.hScrollBounds = new Rectangle();
        this.vScrollBounds = new Rectangle();
        this.hKnobBounds = new Rectangle();
        this.vKnobBounds = new Rectangle();
        this.widgetAreaBounds = new Rectangle();
        this.widgetCullingArea = new Rectangle();
        this.vScrollOnRight = true;
        this.hScrollOnBottom = true;
        this.lastPoint = new Vector2();
        this.fadeScrollBars = true;
        this.smoothScrolling = true;
        this.scrollBarTouch = true;
        this.fadeAlphaSeconds = 1.0f;
        this.fadeDelaySeconds = 1.0f;
        this.cancelTouchFocus = true;
        this.flickScroll = true;
        this.overscrollX = true;
        this.overscrollY = true;
        this.flingTime = 1.0f;
        this.overscrollDistance = 50.0f;
        this.overscrollSpeedMin = 30.0f;
        this.overscrollSpeedMax = 200.0f;
        this.clamp = true;
        this.variableSizeKnobs = true;
        this.draggingPointer = -1;
        if (style2 != null) {
            this.style = style2;
            setActor(widget2);
            setSize(150.0f, 150.0f);
            addCaptureListener(new InputListener() {
                private float handlePosition;

                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    int i = -1;
                    if (ScrollPane.this.draggingPointer != -1) {
                        return false;
                    }
                    if (pointer == 0 && button != 0) {
                        return false;
                    }
                    if (ScrollPane.this.getStage() != null) {
                        ScrollPane.this.getStage().setScrollFocus(ScrollPane.this);
                    }
                    if (!ScrollPane.this.flickScroll) {
                        ScrollPane.this.setScrollbarsVisible(true);
                    }
                    if (ScrollPane.this.fadeAlpha == 0.0f) {
                        return false;
                    }
                    if (ScrollPane.this.scrollBarTouch && ScrollPane.this.scrollX && ScrollPane.this.hScrollBounds.contains(x, y)) {
                        event.stop();
                        ScrollPane.this.setScrollbarsVisible(true);
                        if (ScrollPane.this.hKnobBounds.contains(x, y)) {
                            ScrollPane.this.lastPoint.set(x, y);
                            this.handlePosition = ScrollPane.this.hKnobBounds.x;
                            ScrollPane scrollPane = ScrollPane.this;
                            scrollPane.touchScrollH = true;
                            scrollPane.draggingPointer = pointer;
                            return true;
                        }
                        ScrollPane scrollPane2 = ScrollPane.this;
                        float f = scrollPane2.amountX;
                        float f2 = ScrollPane.this.areaWidth;
                        if (x >= ScrollPane.this.hKnobBounds.x) {
                            i = 1;
                        }
                        scrollPane2.setScrollX(f + (f2 * ((float) i)));
                        return true;
                    } else if (!ScrollPane.this.scrollBarTouch || !ScrollPane.this.scrollY || !ScrollPane.this.vScrollBounds.contains(x, y)) {
                        return false;
                    } else {
                        event.stop();
                        ScrollPane.this.setScrollbarsVisible(true);
                        if (ScrollPane.this.vKnobBounds.contains(x, y)) {
                            ScrollPane.this.lastPoint.set(x, y);
                            this.handlePosition = ScrollPane.this.vKnobBounds.y;
                            ScrollPane scrollPane3 = ScrollPane.this;
                            scrollPane3.touchScrollV = true;
                            scrollPane3.draggingPointer = pointer;
                            return true;
                        }
                        ScrollPane scrollPane4 = ScrollPane.this;
                        float f3 = scrollPane4.amountY;
                        float f4 = ScrollPane.this.areaHeight;
                        if (y < ScrollPane.this.vKnobBounds.y) {
                            i = 1;
                        }
                        scrollPane4.setScrollY(f3 + (f4 * ((float) i)));
                        return true;
                    }
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (pointer == ScrollPane.this.draggingPointer) {
                        ScrollPane.this.cancel();
                    }
                }

                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    if (pointer == ScrollPane.this.draggingPointer) {
                        if (ScrollPane.this.touchScrollH) {
                            float scrollH = this.handlePosition + (x - ScrollPane.this.lastPoint.x);
                            this.handlePosition = scrollH;
                            float scrollH2 = Math.min((ScrollPane.this.hScrollBounds.x + ScrollPane.this.hScrollBounds.width) - ScrollPane.this.hKnobBounds.width, Math.max(ScrollPane.this.hScrollBounds.x, scrollH));
                            float total = ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width;
                            if (total != 0.0f) {
                                ScrollPane scrollPane = ScrollPane.this;
                                scrollPane.setScrollPercentX((scrollH2 - scrollPane.hScrollBounds.x) / total);
                            }
                            ScrollPane.this.lastPoint.set(x, y);
                        } else if (ScrollPane.this.touchScrollV) {
                            float scrollV = this.handlePosition + (y - ScrollPane.this.lastPoint.y);
                            this.handlePosition = scrollV;
                            float scrollV2 = Math.min((ScrollPane.this.vScrollBounds.y + ScrollPane.this.vScrollBounds.height) - ScrollPane.this.vKnobBounds.height, Math.max(ScrollPane.this.vScrollBounds.y, scrollV));
                            float total2 = ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height;
                            if (total2 != 0.0f) {
                                ScrollPane scrollPane2 = ScrollPane.this;
                                scrollPane2.setScrollPercentY(1.0f - ((scrollV2 - scrollPane2.vScrollBounds.y) / total2));
                            }
                            ScrollPane.this.lastPoint.set(x, y);
                        }
                    }
                }

                public boolean mouseMoved(InputEvent event, float x, float y) {
                    if (ScrollPane.this.flickScroll) {
                        return false;
                    }
                    ScrollPane.this.setScrollbarsVisible(true);
                    return false;
                }
            });
            this.flickScrollListener = new ActorGestureListener() {
                public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                    ScrollPane.this.setScrollbarsVisible(true);
                    ScrollPane.this.amountX -= deltaX;
                    ScrollPane.this.amountY += deltaY;
                    ScrollPane.this.clamp();
                    if (!ScrollPane.this.cancelTouchFocus) {
                        return;
                    }
                    if ((ScrollPane.this.scrollX && deltaX != 0.0f) || (ScrollPane.this.scrollY && deltaY != 0.0f)) {
                        ScrollPane.this.cancelTouchFocus();
                    }
                }

                public void fling(InputEvent event, float x, float y, int button) {
                    if (Math.abs(x) > 150.0f && ScrollPane.this.scrollX) {
                        ScrollPane scrollPane = ScrollPane.this;
                        scrollPane.flingTimer = scrollPane.flingTime;
                        ScrollPane scrollPane2 = ScrollPane.this;
                        scrollPane2.velocityX = x;
                        if (scrollPane2.cancelTouchFocus) {
                            ScrollPane.this.cancelTouchFocus();
                        }
                    }
                    if (Math.abs(y) > 150.0f && ScrollPane.this.scrollY) {
                        ScrollPane scrollPane3 = ScrollPane.this;
                        scrollPane3.flingTimer = scrollPane3.flingTime;
                        ScrollPane scrollPane4 = ScrollPane.this;
                        scrollPane4.velocityY = -y;
                        if (scrollPane4.cancelTouchFocus) {
                            ScrollPane.this.cancelTouchFocus();
                        }
                    }
                }

                public boolean handle(Event event) {
                    if (super.handle(event)) {
                        if (((InputEvent) event).getType() != InputEvent.Type.touchDown) {
                            return true;
                        }
                        ScrollPane.this.flingTimer = 0.0f;
                        return true;
                    } else if (!(event instanceof InputEvent) || !((InputEvent) event).isTouchFocusCancel()) {
                        return false;
                    } else {
                        ScrollPane.this.cancel();
                        return false;
                    }
                }
            };
            addListener(this.flickScrollListener);
            addListener(new InputListener() {
                public boolean scrolled(InputEvent event, float x, float y, int amount) {
                    ScrollPane.this.setScrollbarsVisible(true);
                    if (ScrollPane.this.scrollY) {
                        ScrollPane scrollPane = ScrollPane.this;
                        scrollPane.setScrollY(scrollPane.amountY + (ScrollPane.this.getMouseWheelY() * ((float) amount)));
                    } else if (!ScrollPane.this.scrollX) {
                        return false;
                    } else {
                        ScrollPane scrollPane2 = ScrollPane.this;
                        scrollPane2.setScrollX(scrollPane2.amountX + (ScrollPane.this.getMouseWheelX() * ((float) amount)));
                    }
                    return true;
                }
            });
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public void setScrollbarsVisible(boolean visible) {
        if (visible) {
            this.fadeAlpha = this.fadeAlphaSeconds;
            this.fadeDelay = this.fadeDelaySeconds;
            return;
        }
        this.fadeAlpha = 0.0f;
        this.fadeDelay = 0.0f;
    }

    public void cancelTouchFocus() {
        Stage stage = getStage();
        if (stage != null) {
            stage.cancelTouchFocusExcept(this.flickScrollListener, this);
        }
    }

    public void cancel() {
        this.draggingPointer = -1;
        this.touchScrollH = false;
        this.touchScrollV = false;
        this.flickScrollListener.getGestureDetector().cancel();
    }

    /* access modifiers changed from: package-private */
    public void clamp() {
        float f;
        float f2;
        if (this.clamp) {
            if (this.overscrollX) {
                float f3 = this.amountX;
                float f4 = this.overscrollDistance;
                f = MathUtils.clamp(f3, -f4, this.maxX + f4);
            } else {
                f = MathUtils.clamp(this.amountX, 0.0f, this.maxX);
            }
            scrollX(f);
            if (this.overscrollY) {
                float f5 = this.amountY;
                float f6 = this.overscrollDistance;
                f2 = MathUtils.clamp(f5, -f6, this.maxY + f6);
            } else {
                f2 = MathUtils.clamp(this.amountY, 0.0f, this.maxY);
            }
            scrollY(f2);
        }
    }

    public void setStyle(ScrollPaneStyle style2) {
        if (style2 != null) {
            this.style = style2;
            invalidateHierarchy();
            return;
        }
        throw new IllegalArgumentException("style cannot be null.");
    }

    public ScrollPaneStyle getStyle() {
        return this.style;
    }

    public void act(float delta) {
        Stage stage;
        super.act(delta);
        boolean panning = this.flickScrollListener.getGestureDetector().isPanning();
        boolean animating = false;
        float f = this.fadeAlpha;
        if (f > 0.0f && this.fadeScrollBars && !panning && !this.touchScrollH && !this.touchScrollV) {
            this.fadeDelay -= delta;
            if (this.fadeDelay <= 0.0f) {
                this.fadeAlpha = Math.max(0.0f, f - delta);
            }
            animating = true;
        }
        if (this.flingTimer > 0.0f) {
            setScrollbarsVisible(true);
            float alpha = this.flingTimer / this.flingTime;
            this.amountX -= (this.velocityX * alpha) * delta;
            this.amountY -= (this.velocityY * alpha) * delta;
            clamp();
            if (this.amountX == (-this.overscrollDistance)) {
                this.velocityX = 0.0f;
            }
            if (this.amountX >= this.maxX + this.overscrollDistance) {
                this.velocityX = 0.0f;
            }
            if (this.amountY == (-this.overscrollDistance)) {
                this.velocityY = 0.0f;
            }
            if (this.amountY >= this.maxY + this.overscrollDistance) {
                this.velocityY = 0.0f;
            }
            this.flingTimer -= delta;
            if (this.flingTimer <= 0.0f) {
                this.velocityX = 0.0f;
                this.velocityY = 0.0f;
            }
            animating = true;
        }
        if (!this.smoothScrolling || this.flingTimer > 0.0f || panning || ((this.touchScrollH && (!this.scrollX || this.maxX / (this.hScrollBounds.width - this.hKnobBounds.width) <= this.areaWidth * 0.1f)) || (this.touchScrollV && (!this.scrollY || this.maxY / (this.vScrollBounds.height - this.vKnobBounds.height) <= this.areaHeight * 0.1f)))) {
            float f2 = this.visualAmountX;
            float f3 = this.amountX;
            if (f2 != f3) {
                visualScrollX(f3);
            }
            float f4 = this.visualAmountY;
            float f5 = this.amountY;
            if (f4 != f5) {
                visualScrollY(f5);
            }
        } else {
            float f6 = this.visualAmountX;
            float f7 = this.amountX;
            if (f6 != f7) {
                if (f6 < f7) {
                    visualScrollX(Math.min(f7, f6 + Math.max(delta * 200.0f, (f7 - f6) * 7.0f * delta)));
                } else {
                    visualScrollX(Math.max(f7, f6 - Math.max(delta * 200.0f, ((f6 - f7) * 7.0f) * delta)));
                }
                animating = true;
            }
            float f8 = this.visualAmountY;
            float f9 = this.amountY;
            if (f8 != f9) {
                if (f8 < f9) {
                    visualScrollY(Math.min(f9, f8 + Math.max(200.0f * delta, (f9 - f8) * 7.0f * delta)));
                } else {
                    visualScrollY(Math.max(f9, f8 - Math.max(200.0f * delta, ((f8 - f9) * 7.0f) * delta)));
                }
                animating = true;
            }
        }
        if (!panning) {
            if (this.overscrollX && this.scrollX) {
                float f10 = this.amountX;
                if (f10 < 0.0f) {
                    setScrollbarsVisible(true);
                    float f11 = this.amountX;
                    float f12 = this.overscrollSpeedMin;
                    this.amountX = f11 + ((f12 + (((this.overscrollSpeedMax - f12) * (-f11)) / this.overscrollDistance)) * delta);
                    if (this.amountX > 0.0f) {
                        scrollX(0.0f);
                    }
                    animating = true;
                } else if (f10 > this.maxX) {
                    setScrollbarsVisible(true);
                    float f13 = this.amountX;
                    float f14 = this.overscrollSpeedMin;
                    float f15 = this.maxX;
                    this.amountX = f13 - ((f14 + (((this.overscrollSpeedMax - f14) * (-(f15 - f13))) / this.overscrollDistance)) * delta);
                    if (this.amountX < f15) {
                        scrollX(f15);
                    }
                    animating = true;
                }
            }
            if (this.overscrollY && this.scrollY) {
                float f16 = this.amountY;
                if (f16 < 0.0f) {
                    setScrollbarsVisible(true);
                    float f17 = this.amountY;
                    float f18 = this.overscrollSpeedMin;
                    this.amountY = f17 + ((f18 + (((this.overscrollSpeedMax - f18) * (-f17)) / this.overscrollDistance)) * delta);
                    if (this.amountY > 0.0f) {
                        scrollY(0.0f);
                    }
                    animating = true;
                } else if (f16 > this.maxY) {
                    setScrollbarsVisible(true);
                    float f19 = this.amountY;
                    float f20 = this.overscrollSpeedMin;
                    float f21 = this.maxY;
                    this.amountY = f19 - ((f20 + (((this.overscrollSpeedMax - f20) * (-(f21 - f19))) / this.overscrollDistance)) * delta);
                    if (this.amountY < f21) {
                        scrollY(f21);
                    }
                    animating = true;
                }
            }
        }
        if (animating && (stage = getStage()) != null && stage.getActionsRequestRendering()) {
            Gdx.graphics.requestRendering();
        }
    }

    public void layout() {
        float widgetWidth;
        float widgetHeight;
        float boundsY;
        float boundsX;
        Drawable bg = this.style.background;
        Drawable hScrollKnob = this.style.hScrollKnob;
        Drawable vScrollKnob = this.style.vScrollKnob;
        float bgLeftWidth = 0.0f;
        float bgRightWidth = 0.0f;
        float bgTopHeight = 0.0f;
        float bgBottomHeight = 0.0f;
        if (bg != null) {
            bgLeftWidth = bg.getLeftWidth();
            bgRightWidth = bg.getRightWidth();
            bgTopHeight = bg.getTopHeight();
            bgBottomHeight = bg.getBottomHeight();
        }
        float width = getWidth();
        float height = getHeight();
        float scrollbarHeight = 0.0f;
        if (hScrollKnob != null) {
            scrollbarHeight = hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
        }
        float scrollbarWidth = 0.0f;
        if (vScrollKnob != null) {
            scrollbarWidth = vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
        }
        this.areaWidth = (width - bgLeftWidth) - bgRightWidth;
        this.areaHeight = (height - bgTopHeight) - bgBottomHeight;
        Actor actor = this.widget;
        if (actor != null) {
            if (actor instanceof Layout) {
                Layout layout = (Layout) actor;
                widgetWidth = layout.getPrefWidth();
                widgetHeight = layout.getPrefHeight();
            } else {
                widgetWidth = actor.getWidth();
                widgetHeight = this.widget.getHeight();
            }
            this.scrollX = this.forceScrollX || (widgetWidth > this.areaWidth && !this.disableX);
            this.scrollY = this.forceScrollY || (widgetHeight > this.areaHeight && !this.disableY);
            boolean fade = this.fadeScrollBars;
            if (!fade) {
                if (this.scrollY) {
                    this.areaWidth -= scrollbarWidth;
                    if (!this.scrollX && widgetWidth > this.areaWidth && !this.disableX) {
                        this.scrollX = true;
                    }
                }
                if (this.scrollX) {
                    this.areaHeight -= scrollbarHeight;
                    if (!this.scrollY && widgetHeight > this.areaHeight && !this.disableY) {
                        this.scrollY = true;
                        this.areaWidth -= scrollbarWidth;
                    }
                }
            }
            Drawable drawable = bg;
            float bgRightWidth2 = bgRightWidth;
            this.widgetAreaBounds.set(bgLeftWidth, bgBottomHeight, this.areaWidth, this.areaHeight);
            if (fade) {
                if (this.scrollX && this.scrollY) {
                    this.areaHeight -= scrollbarHeight;
                    this.areaWidth -= scrollbarWidth;
                }
            } else if (this.scrollbarsOnTop) {
                if (this.scrollX) {
                    this.widgetAreaBounds.height += scrollbarHeight;
                }
                if (this.scrollY) {
                    this.widgetAreaBounds.width += scrollbarWidth;
                }
            } else {
                if (this.scrollX && this.hScrollOnBottom) {
                    this.widgetAreaBounds.y += scrollbarHeight;
                }
                if (this.scrollY && !this.vScrollOnRight) {
                    this.widgetAreaBounds.x += scrollbarWidth;
                }
            }
            float widgetWidth2 = this.disableX ? this.areaWidth : Math.max(this.areaWidth, widgetWidth);
            float widgetHeight2 = this.disableY ? this.areaHeight : Math.max(this.areaHeight, widgetHeight);
            this.maxX = widgetWidth2 - this.areaWidth;
            this.maxY = widgetHeight2 - this.areaHeight;
            if (fade && this.scrollX && this.scrollY) {
                this.maxY -= scrollbarHeight;
                this.maxX -= scrollbarWidth;
            }
            scrollX(MathUtils.clamp(this.amountX, 0.0f, this.maxX));
            scrollY(MathUtils.clamp(this.amountY, 0.0f, this.maxY));
            if (!this.scrollX) {
                float f = scrollbarWidth;
            } else if (hScrollKnob != null) {
                float hScrollHeight = this.style.hScroll != null ? this.style.hScroll.getMinHeight() : hScrollKnob.getMinHeight();
                float boundsX2 = this.vScrollOnRight ? bgLeftWidth : bgLeftWidth + scrollbarWidth;
                float f2 = scrollbarHeight;
                float f3 = scrollbarWidth;
                this.hScrollBounds.set(boundsX2, this.hScrollOnBottom ? bgBottomHeight : (height - bgTopHeight) - hScrollHeight, this.areaWidth, hScrollHeight);
                if (this.variableSizeKnobs) {
                    float f4 = hScrollHeight;
                    float f5 = boundsX2;
                    this.hKnobBounds.width = Math.max(hScrollKnob.getMinWidth(), (float) ((int) ((this.hScrollBounds.width * this.areaWidth) / widgetWidth2)));
                } else {
                    float f6 = boundsX2;
                    this.hKnobBounds.width = hScrollKnob.getMinWidth();
                }
                if (this.hKnobBounds.width > widgetWidth2) {
                    this.hKnobBounds.width = 0.0f;
                }
                this.hKnobBounds.height = hScrollKnob.getMinHeight();
                this.hKnobBounds.x = this.hScrollBounds.x + ((float) ((int) ((this.hScrollBounds.width - this.hKnobBounds.width) * getScrollPercentX())));
                this.hKnobBounds.y = this.hScrollBounds.y;
            } else {
                float f7 = scrollbarWidth;
                this.hScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                this.hKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
            }
            if (!this.scrollY) {
                float f8 = bgTopHeight;
            } else if (vScrollKnob != null) {
                float vScrollWidth = this.style.vScroll != null ? this.style.vScroll.getMinWidth() : vScrollKnob.getMinWidth();
                if (this.hScrollOnBottom) {
                    boundsY = (height - bgTopHeight) - this.areaHeight;
                } else {
                    boundsY = bgBottomHeight;
                }
                if (this.vScrollOnRight) {
                    boundsX = (width - bgRightWidth2) - vScrollWidth;
                } else {
                    boundsX = bgLeftWidth;
                }
                this.vScrollBounds.set(boundsX, boundsY, vScrollWidth, this.areaHeight);
                this.vKnobBounds.width = vScrollKnob.getMinWidth();
                if (this.variableSizeKnobs) {
                    Drawable drawable2 = hScrollKnob;
                    float f9 = bgTopHeight;
                    this.vKnobBounds.height = Math.max(vScrollKnob.getMinHeight(), (float) ((int) ((this.vScrollBounds.height * this.areaHeight) / widgetHeight2)));
                } else {
                    float f10 = bgTopHeight;
                    this.vKnobBounds.height = vScrollKnob.getMinHeight();
                }
                if (this.vKnobBounds.height > widgetHeight2) {
                    this.vKnobBounds.height = 0.0f;
                }
                if (this.vScrollOnRight) {
                    this.vKnobBounds.x = (width - bgRightWidth2) - vScrollKnob.getMinWidth();
                } else {
                    this.vKnobBounds.x = bgLeftWidth;
                }
                this.vKnobBounds.y = this.vScrollBounds.y + ((float) ((int) ((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - getScrollPercentY()))));
            } else {
                float f11 = bgTopHeight;
                this.vScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                this.vKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
            }
            updateWidgetPosition();
            Actor actor2 = this.widget;
            if (actor2 instanceof Layout) {
                actor2.setSize(widgetWidth2, widgetHeight2);
                ((Layout) this.widget).validate();
            }
        }
    }

    private void updateWidgetPosition() {
        float y;
        float y2 = this.widgetAreaBounds.y;
        if (!this.scrollY) {
            y = y2 - ((float) ((int) this.maxY));
        } else {
            y = y2 - ((float) ((int) (this.maxY - this.visualAmountY)));
        }
        float x = this.widgetAreaBounds.x;
        if (this.scrollX) {
            x -= (float) ((int) this.visualAmountX);
        }
        if (!this.fadeScrollBars && this.scrollbarsOnTop) {
            if (this.scrollX && this.hScrollOnBottom) {
                float scrollbarHeight = 0.0f;
                if (this.style.hScrollKnob != null) {
                    scrollbarHeight = this.style.hScrollKnob.getMinHeight();
                }
                if (this.style.hScroll != null) {
                    scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
                }
                y += scrollbarHeight;
            }
            if (this.scrollY && !this.vScrollOnRight) {
                float scrollbarWidth = 0.0f;
                if (this.style.hScrollKnob != null) {
                    scrollbarWidth = this.style.hScrollKnob.getMinWidth();
                }
                if (this.style.hScroll != null) {
                    scrollbarWidth = Math.max(scrollbarWidth, this.style.hScroll.getMinWidth());
                }
                x += scrollbarWidth;
            }
        }
        this.widget.setPosition(x, y);
        if (this.widget instanceof Cullable) {
            this.widgetCullingArea.x = this.widgetAreaBounds.x - x;
            this.widgetCullingArea.y = this.widgetAreaBounds.y - y;
            this.widgetCullingArea.width = this.widgetAreaBounds.width;
            this.widgetCullingArea.height = this.widgetAreaBounds.height;
            ((Cullable) this.widget).setCullingArea(this.widgetCullingArea);
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        if (this.widget != null) {
            validate();
            applyTransform(batch, computeTransform());
            if (this.scrollX) {
                this.hKnobBounds.x = this.hScrollBounds.x + ((float) ((int) ((this.hScrollBounds.width - this.hKnobBounds.width) * getVisualScrollPercentX())));
            }
            if (this.scrollY) {
                this.vKnobBounds.y = this.vScrollBounds.y + ((float) ((int) ((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - getVisualScrollPercentY()))));
            }
            updateWidgetPosition();
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            if (this.style.background != null) {
                this.style.background.draw(batch, 0.0f, 0.0f, getWidth(), getHeight());
            }
            batch.flush();
            if (clipBegin(this.widgetAreaBounds.x, this.widgetAreaBounds.y, this.widgetAreaBounds.width, this.widgetAreaBounds.height)) {
                drawChildren(batch, parentAlpha);
                batch.flush();
                clipEnd();
            }
            float alpha = color.a * parentAlpha;
            if (this.fadeScrollBars) {
                alpha *= Interpolation.fade.apply(this.fadeAlpha / this.fadeAlphaSeconds);
            }
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            drawScrollBars(batch, color.r, color.g, color.b, alpha);
            resetTransform(batch);
        }
    }

    /* access modifiers changed from: protected */
    public void drawScrollBars(Batch batch, float r, float g, float b, float a) {
        if (a > 0.0f) {
            batch.setColor(r, g, b, a);
            boolean z = true;
            boolean x = this.scrollX && this.hKnobBounds.width > 0.0f;
            if (!this.scrollY || this.vKnobBounds.height <= 0.0f) {
                z = false;
            }
            boolean y = z;
            if (x && y && this.style.corner != null) {
                this.style.corner.draw(batch, this.hScrollBounds.x + this.hScrollBounds.width, this.hScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.y);
            }
            if (x) {
                if (this.style.hScroll != null) {
                    this.style.hScroll.draw(batch, this.hScrollBounds.x, this.hScrollBounds.y, this.hScrollBounds.width, this.hScrollBounds.height);
                }
                if (this.style.hScrollKnob != null) {
                    this.style.hScrollKnob.draw(batch, this.hKnobBounds.x, this.hKnobBounds.y, this.hKnobBounds.width, this.hKnobBounds.height);
                }
            }
            if (y) {
                if (this.style.vScroll != null) {
                    this.style.vScroll.draw(batch, this.vScrollBounds.x, this.vScrollBounds.y, this.vScrollBounds.width, this.vScrollBounds.height);
                }
                if (this.style.vScrollKnob != null) {
                    this.style.vScrollKnob.draw(batch, this.vKnobBounds.x, this.vKnobBounds.y, this.vKnobBounds.width, this.vKnobBounds.height);
                }
            }
        }
    }

    public void fling(float flingTime2, float velocityX2, float velocityY2) {
        this.flingTimer = flingTime2;
        this.velocityX = velocityX2;
        this.velocityY = velocityY2;
    }

    public float getPrefWidth() {
        float width = 0.0f;
        Actor actor = this.widget;
        if (actor instanceof Layout) {
            validate();
            width = ((Layout) this.widget).getPrefWidth();
        } else if (actor != null) {
            width = actor.getWidth();
        }
        if (this.style.background != null) {
            width += this.style.background.getLeftWidth() + this.style.background.getRightWidth();
        }
        if (!this.scrollY) {
            return width;
        }
        float scrollbarWidth = 0.0f;
        if (this.style.vScrollKnob != null) {
            scrollbarWidth = this.style.vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
        }
        return width + scrollbarWidth;
    }

    public float getPrefHeight() {
        float height = 0.0f;
        Actor actor = this.widget;
        if (actor instanceof Layout) {
            validate();
            height = ((Layout) this.widget).getPrefHeight();
        } else if (actor != null) {
            height = actor.getHeight();
        }
        if (this.style.background != null) {
            height += this.style.background.getTopHeight() + this.style.background.getBottomHeight();
        }
        if (!this.scrollX) {
            return height;
        }
        float scrollbarHeight = 0.0f;
        if (this.style.hScrollKnob != null) {
            scrollbarHeight = this.style.hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
        }
        return height + scrollbarHeight;
    }

    public float getMinWidth() {
        return 0.0f;
    }

    public float getMinHeight() {
        return 0.0f;
    }

    public void setActor(Actor actor) {
        Actor actor2 = this.widget;
        if (actor2 != this) {
            if (actor2 != null) {
                super.removeActor(actor2);
            }
            this.widget = actor;
            Actor actor3 = this.widget;
            if (actor3 != null) {
                super.addActor(actor3);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("widget cannot be the ScrollPane.");
    }

    public Actor getActor() {
        return this.widget;
    }

    public void setWidget(Actor actor) {
        setActor(actor);
    }

    public Actor getWidget() {
        return this.widget;
    }

    public void addActor(Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }

    public void addActorAt(int index, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }

    public void addActorBefore(Actor actorBefore, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }

    public void addActorAfter(Actor actorAfter, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }

    public boolean removeActor(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        } else if (actor != this.widget) {
            return false;
        } else {
            setActor((Actor) null);
            return true;
        }
    }

    public boolean removeActor(Actor actor, boolean unfocus) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        } else if (actor != this.widget) {
            return false;
        } else {
            this.widget = null;
            return super.removeActor(actor, unfocus);
        }
    }

    public Actor hit(float x, float y, boolean touchable) {
        if (x < 0.0f || x >= getWidth() || y < 0.0f || y >= getHeight()) {
            return null;
        }
        if (touchable && getTouchable() == Touchable.enabled && isVisible()) {
            if (this.scrollX && this.touchScrollH && this.hScrollBounds.contains(x, y)) {
                return this;
            }
            if (this.scrollY && this.touchScrollV && this.vScrollBounds.contains(x, y)) {
                return this;
            }
        }
        return super.hit(x, y, touchable);
    }

    /* access modifiers changed from: protected */
    public void scrollX(float pixelsX) {
        this.amountX = pixelsX;
    }

    /* access modifiers changed from: protected */
    public void scrollY(float pixelsY) {
        this.amountY = pixelsY;
    }

    /* access modifiers changed from: protected */
    public void visualScrollX(float pixelsX) {
        this.visualAmountX = pixelsX;
    }

    /* access modifiers changed from: protected */
    public void visualScrollY(float pixelsY) {
        this.visualAmountY = pixelsY;
    }

    /* access modifiers changed from: protected */
    public float getMouseWheelX() {
        float f = this.areaWidth;
        return Math.min(f, Math.max(0.9f * f, this.maxX * 0.1f) / 4.0f);
    }

    /* access modifiers changed from: protected */
    public float getMouseWheelY() {
        float f = this.areaHeight;
        return Math.min(f, Math.max(0.9f * f, this.maxY * 0.1f) / 4.0f);
    }

    public void setScrollX(float pixels) {
        scrollX(MathUtils.clamp(pixels, 0.0f, this.maxX));
    }

    public float getScrollX() {
        return this.amountX;
    }

    public void setScrollY(float pixels) {
        scrollY(MathUtils.clamp(pixels, 0.0f, this.maxY));
    }

    public float getScrollY() {
        return this.amountY;
    }

    public void updateVisualScroll() {
        this.visualAmountX = this.amountX;
        this.visualAmountY = this.amountY;
    }

    public float getVisualScrollX() {
        if (!this.scrollX) {
            return 0.0f;
        }
        return this.visualAmountX;
    }

    public float getVisualScrollY() {
        if (!this.scrollY) {
            return 0.0f;
        }
        return this.visualAmountY;
    }

    public float getVisualScrollPercentX() {
        float f = this.maxX;
        if (f == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.visualAmountX / f, 0.0f, 1.0f);
    }

    public float getVisualScrollPercentY() {
        float f = this.maxY;
        if (f == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.visualAmountY / f, 0.0f, 1.0f);
    }

    public float getScrollPercentX() {
        float f = this.maxX;
        if (f == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.amountX / f, 0.0f, 1.0f);
    }

    public void setScrollPercentX(float percentX) {
        scrollX(this.maxX * MathUtils.clamp(percentX, 0.0f, 1.0f));
    }

    public float getScrollPercentY() {
        float f = this.maxY;
        if (f == 0.0f) {
            return 0.0f;
        }
        return MathUtils.clamp(this.amountY / f, 0.0f, 1.0f);
    }

    public void setScrollPercentY(float percentY) {
        scrollY(this.maxY * MathUtils.clamp(percentY, 0.0f, 1.0f));
    }

    public void setFlickScroll(boolean flickScroll2) {
        if (this.flickScroll != flickScroll2) {
            this.flickScroll = flickScroll2;
            if (flickScroll2) {
                addListener(this.flickScrollListener);
            } else {
                removeListener(this.flickScrollListener);
            }
            invalidate();
        }
    }

    public void setFlickScrollTapSquareSize(float halfTapSquareSize) {
        this.flickScrollListener.getGestureDetector().setTapSquareSize(halfTapSquareSize);
    }

    public void scrollTo(float x, float y, float width, float height) {
        scrollTo(x, y, width, height, false, false);
    }

    public void scrollTo(float x, float y, float width, float height, boolean centerHorizontal, boolean centerVertical) {
        float amountX2;
        float amountY2;
        validate();
        float amountX3 = this.amountX;
        if (centerHorizontal) {
            amountX2 = (x - (this.areaWidth / 2.0f)) + (width / 2.0f);
        } else {
            float f = this.areaWidth;
            if (x + width > amountX3 + f) {
                amountX3 = (x + width) - f;
            }
            amountX2 = amountX3;
            if (x < amountX2) {
                amountX2 = x;
            }
        }
        scrollX(MathUtils.clamp(amountX2, 0.0f, this.maxX));
        float amountY3 = this.amountY;
        if (centerVertical) {
            amountY2 = ((this.maxY - y) + (this.areaHeight / 2.0f)) - (height / 2.0f);
        } else {
            float f2 = this.maxY;
            float f3 = this.areaHeight;
            if (amountY3 > ((f2 - y) - height) + f3) {
                amountY3 = ((f2 - y) - height) + f3;
            }
            amountY2 = amountY3;
            float amountY4 = this.maxY;
            if (amountY2 < amountY4 - y) {
                amountY2 = amountY4 - y;
            }
        }
        scrollY(MathUtils.clamp(amountY2, 0.0f, this.maxY));
    }

    public float getMaxX() {
        return this.maxX;
    }

    public float getMaxY() {
        return this.maxY;
    }

    public float getScrollBarHeight() {
        if (!this.scrollX) {
            return 0.0f;
        }
        float height = 0.0f;
        if (this.style.hScrollKnob != null) {
            height = this.style.hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            return Math.max(height, this.style.hScroll.getMinHeight());
        }
        return height;
    }

    public float getScrollBarWidth() {
        if (!this.scrollY) {
            return 0.0f;
        }
        float width = 0.0f;
        if (this.style.vScrollKnob != null) {
            width = this.style.vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            return Math.max(width, this.style.vScroll.getMinWidth());
        }
        return width;
    }

    public float getScrollWidth() {
        return this.areaWidth;
    }

    public float getScrollHeight() {
        return this.areaHeight;
    }

    public boolean isScrollX() {
        return this.scrollX;
    }

    public boolean isScrollY() {
        return this.scrollY;
    }

    public void setScrollingDisabled(boolean x, boolean y) {
        this.disableX = x;
        this.disableY = y;
        invalidate();
    }

    public boolean isScrollingDisabledX() {
        return this.disableX;
    }

    public boolean isScrollingDisabledY() {
        return this.disableY;
    }

    public boolean isLeftEdge() {
        return !this.scrollX || this.amountX <= 0.0f;
    }

    public boolean isRightEdge() {
        return !this.scrollX || this.amountX >= this.maxX;
    }

    public boolean isTopEdge() {
        return !this.scrollY || this.amountY <= 0.0f;
    }

    public boolean isBottomEdge() {
        return !this.scrollY || this.amountY >= this.maxY;
    }

    public boolean isDragging() {
        return this.draggingPointer != -1;
    }

    public boolean isPanning() {
        return this.flickScrollListener.getGestureDetector().isPanning();
    }

    public boolean isFlinging() {
        return this.flingTimer > 0.0f;
    }

    public void setVelocityX(float velocityX2) {
        this.velocityX = velocityX2;
    }

    public float getVelocityX() {
        return this.velocityX;
    }

    public void setVelocityY(float velocityY2) {
        this.velocityY = velocityY2;
    }

    public float getVelocityY() {
        return this.velocityY;
    }

    public void setOverscroll(boolean overscrollX2, boolean overscrollY2) {
        this.overscrollX = overscrollX2;
        this.overscrollY = overscrollY2;
    }

    public void setupOverscroll(float distance, float speedMin, float speedMax) {
        this.overscrollDistance = distance;
        this.overscrollSpeedMin = speedMin;
        this.overscrollSpeedMax = speedMax;
    }

    public float getOverscrollDistance() {
        return this.overscrollDistance;
    }

    public void setForceScroll(boolean x, boolean y) {
        this.forceScrollX = x;
        this.forceScrollY = y;
    }

    public boolean isForceScrollX() {
        return this.forceScrollX;
    }

    public boolean isForceScrollY() {
        return this.forceScrollY;
    }

    public void setFlingTime(float flingTime2) {
        this.flingTime = flingTime2;
    }

    public void setClamp(boolean clamp2) {
        this.clamp = clamp2;
    }

    public void setScrollBarPositions(boolean bottom, boolean right) {
        this.hScrollOnBottom = bottom;
        this.vScrollOnRight = right;
    }

    public void setFadeScrollBars(boolean fadeScrollBars2) {
        if (this.fadeScrollBars != fadeScrollBars2) {
            this.fadeScrollBars = fadeScrollBars2;
            if (!fadeScrollBars2) {
                this.fadeAlpha = this.fadeAlphaSeconds;
            }
            invalidate();
        }
    }

    public void setupFadeScrollBars(float fadeAlphaSeconds2, float fadeDelaySeconds2) {
        this.fadeAlphaSeconds = fadeAlphaSeconds2;
        this.fadeDelaySeconds = fadeDelaySeconds2;
    }

    public boolean getFadeScrollBars() {
        return this.fadeScrollBars;
    }

    public void setScrollBarTouch(boolean scrollBarTouch2) {
        this.scrollBarTouch = scrollBarTouch2;
    }

    public void setSmoothScrolling(boolean smoothScrolling2) {
        this.smoothScrolling = smoothScrolling2;
    }

    public void setScrollbarsOnTop(boolean scrollbarsOnTop2) {
        this.scrollbarsOnTop = scrollbarsOnTop2;
        invalidate();
    }

    public boolean getVariableSizeKnobs() {
        return this.variableSizeKnobs;
    }

    public void setVariableSizeKnobs(boolean variableSizeKnobs2) {
        this.variableSizeKnobs = variableSizeKnobs2;
    }

    public void setCancelTouchFocus(boolean cancelTouchFocus2) {
        this.cancelTouchFocus = cancelTouchFocus2;
    }

    public void drawDebug(ShapeRenderer shapes) {
        drawDebugBounds(shapes);
        applyTransform(shapes, computeTransform());
        if (clipBegin(this.widgetAreaBounds.x, this.widgetAreaBounds.y, this.widgetAreaBounds.width, this.widgetAreaBounds.height)) {
            drawDebugChildren(shapes);
            shapes.flush();
            clipEnd();
        }
        resetTransform(shapes);
    }

    public static class ScrollPaneStyle {
        public Drawable background;
        public Drawable corner;
        public Drawable hScroll;
        public Drawable hScrollKnob;
        public Drawable vScroll;
        public Drawable vScrollKnob;

        public ScrollPaneStyle() {
        }

        public ScrollPaneStyle(Drawable background2, Drawable hScroll2, Drawable hScrollKnob2, Drawable vScroll2, Drawable vScrollKnob2) {
            this.background = background2;
            this.hScroll = hScroll2;
            this.hScrollKnob = hScrollKnob2;
            this.vScroll = vScroll2;
            this.vScrollKnob = vScrollKnob2;
        }

        public ScrollPaneStyle(ScrollPaneStyle style) {
            this.background = style.background;
            this.corner = style.corner;
            this.hScroll = style.hScroll;
            this.hScrollKnob = style.hScrollKnob;
            this.vScroll = style.vScroll;
            this.vScrollKnob = style.vScrollKnob;
        }
    }
}
