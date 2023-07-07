package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

public abstract class Value {
    public static Value maxHeight = new Value() {
        public float get(Actor context) {
            if (context instanceof Layout) {
                return ((Layout) context).getMaxHeight();
            }
            if (context == null) {
                return 0.0f;
            }
            return context.getHeight();
        }
    };
    public static Value maxWidth = new Value() {
        public float get(Actor context) {
            if (context instanceof Layout) {
                return ((Layout) context).getMaxWidth();
            }
            if (context == null) {
                return 0.0f;
            }
            return context.getWidth();
        }
    };
    public static Value minHeight = new Value() {
        public float get(Actor context) {
            if (context instanceof Layout) {
                return ((Layout) context).getMinHeight();
            }
            if (context == null) {
                return 0.0f;
            }
            return context.getHeight();
        }
    };
    public static Value minWidth = new Value() {
        public float get(Actor context) {
            if (context instanceof Layout) {
                return ((Layout) context).getMinWidth();
            }
            if (context == null) {
                return 0.0f;
            }
            return context.getWidth();
        }
    };
    public static Value prefHeight = new Value() {
        public float get(Actor context) {
            if (context instanceof Layout) {
                return ((Layout) context).getPrefHeight();
            }
            if (context == null) {
                return 0.0f;
            }
            return context.getHeight();
        }
    };
    public static Value prefWidth = new Value() {
        public float get(Actor context) {
            if (context instanceof Layout) {
                return ((Layout) context).getPrefWidth();
            }
            if (context == null) {
                return 0.0f;
            }
            return context.getWidth();
        }
    };
    public static final Fixed zero = new Fixed(0.0f);

    public abstract float get(Actor actor);

    public float get() {
        return get((Actor) null);
    }

    public static class Fixed extends Value {
        static final Fixed[] cache = new Fixed[111];
        private final float value;

        public Fixed(float value2) {
            this.value = value2;
        }

        public float get(Actor context) {
            return this.value;
        }

        public String toString() {
            return Float.toString(this.value);
        }

        public static Fixed valueOf(float value2) {
            if (value2 == 0.0f) {
                return zero;
            }
            if (value2 < -10.0f || value2 > 100.0f || value2 != ((float) ((int) value2))) {
                return new Fixed(value2);
            }
            Fixed[] fixedArr = cache;
            Fixed fixed = fixedArr[((int) value2) + 10];
            if (fixed != null) {
                return fixed;
            }
            Fixed fixed2 = new Fixed(value2);
            Fixed fixed3 = fixed2;
            fixedArr[((int) value2) + 10] = fixed2;
            return fixed3;
        }
    }

    public static Value percentWidth(final float percent) {
        return new Value() {
            public float get(Actor actor) {
                return actor.getWidth() * percent;
            }
        };
    }

    public static Value percentHeight(final float percent) {
        return new Value() {
            public float get(Actor actor) {
                return actor.getHeight() * percent;
            }
        };
    }

    public static Value percentWidth(final float percent, final Actor actor) {
        if (actor != null) {
            return new Value() {
                public float get(Actor context) {
                    return actor.getWidth() * percent;
                }
            };
        }
        throw new IllegalArgumentException("actor cannot be null.");
    }

    public static Value percentHeight(final float percent, final Actor actor) {
        if (actor != null) {
            return new Value() {
                public float get(Actor context) {
                    return actor.getHeight() * percent;
                }
            };
        }
        throw new IllegalArgumentException("actor cannot be null.");
    }
}
