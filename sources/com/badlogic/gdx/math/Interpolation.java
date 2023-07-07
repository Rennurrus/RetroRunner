package com.badlogic.gdx.math;

public abstract class Interpolation {
    public static final Bounce bounce = new Bounce(4);
    public static final BounceIn bounceIn = new BounceIn(4);
    public static final BounceOut bounceOut = new BounceOut(4);
    public static final Interpolation circle = new Interpolation() {
        public float apply(float a) {
            if (a <= 0.5f) {
                float a2 = a * 2.0f;
                return (1.0f - ((float) Math.sqrt((double) (1.0f - (a2 * a2))))) / 2.0f;
            }
            float a3 = (a - 1.0f) * 2.0f;
            return (((float) Math.sqrt((double) (1.0f - (a3 * a3)))) + 1.0f) / 2.0f;
        }
    };
    public static final Interpolation circleIn = new Interpolation() {
        public float apply(float a) {
            return 1.0f - ((float) Math.sqrt((double) (1.0f - (a * a))));
        }
    };
    public static final Interpolation circleOut = new Interpolation() {
        public float apply(float a) {
            float a2 = a - 1.0f;
            return (float) Math.sqrt((double) (1.0f - (a2 * a2)));
        }
    };
    public static final Elastic elastic = new Elastic(2.0f, 10.0f, 7, 1.0f);
    public static final ElasticIn elasticIn = new ElasticIn(2.0f, 10.0f, 6, 1.0f);
    public static final ElasticOut elasticOut = new ElasticOut(2.0f, 10.0f, 7, 1.0f);
    public static final Exp exp10 = new Exp(2.0f, 10.0f);
    public static final ExpIn exp10In = new ExpIn(2.0f, 10.0f);
    public static final ExpOut exp10Out = new ExpOut(2.0f, 10.0f);
    public static final Exp exp5 = new Exp(2.0f, 5.0f);
    public static final ExpIn exp5In = new ExpIn(2.0f, 5.0f);
    public static final ExpOut exp5Out = new ExpOut(2.0f, 5.0f);
    public static final Interpolation fade = smoother;
    public static final PowOut fastSlow = pow2Out;
    public static final Interpolation linear = new Interpolation() {
        public float apply(float a) {
            return a;
        }
    };
    public static final Pow pow2 = new Pow(2);
    public static final PowIn pow2In = new PowIn(2);
    public static final Interpolation pow2InInverse = new Interpolation() {
        public float apply(float a) {
            return (float) Math.sqrt((double) a);
        }
    };
    public static final PowOut pow2Out = new PowOut(2);
    public static final Interpolation pow2OutInverse = new Interpolation() {
        public float apply(float a) {
            return 1.0f - ((float) Math.sqrt((double) (-(a - 1.0f))));
        }
    };
    public static final Pow pow3 = new Pow(3);
    public static final PowIn pow3In = new PowIn(3);
    public static final Interpolation pow3InInverse = new Interpolation() {
        public float apply(float a) {
            return (float) Math.cbrt((double) a);
        }
    };
    public static final PowOut pow3Out = new PowOut(3);
    public static final Interpolation pow3OutInverse = new Interpolation() {
        public float apply(float a) {
            return 1.0f - ((float) Math.cbrt((double) (-(a - 1.0f))));
        }
    };
    public static final Pow pow4 = new Pow(4);
    public static final PowIn pow4In = new PowIn(4);
    public static final PowOut pow4Out = new PowOut(4);
    public static final Pow pow5 = new Pow(5);
    public static final PowIn pow5In = new PowIn(5);
    public static final PowOut pow5Out = new PowOut(5);
    public static final Interpolation sine = new Interpolation() {
        public float apply(float a) {
            return (1.0f - MathUtils.cos(3.1415927f * a)) / 2.0f;
        }
    };
    public static final Interpolation sineIn = new Interpolation() {
        public float apply(float a) {
            return 1.0f - MathUtils.cos((3.1415927f * a) / 2.0f);
        }
    };
    public static final Interpolation sineOut = new Interpolation() {
        public float apply(float a) {
            return MathUtils.sin((3.1415927f * a) / 2.0f);
        }
    };
    public static final PowIn slowFast = pow2In;
    public static final Interpolation smooth = new Interpolation() {
        public float apply(float a) {
            return a * a * (3.0f - (2.0f * a));
        }
    };
    public static final Interpolation smooth2 = new Interpolation() {
        public float apply(float a) {
            float a2 = a * a * (3.0f - (a * 2.0f));
            return a2 * a2 * (3.0f - (2.0f * a2));
        }
    };
    public static final Interpolation smoother = new Interpolation() {
        public float apply(float a) {
            return a * a * a * ((((6.0f * a) - 15.0f) * a) + 10.0f);
        }
    };
    public static final Swing swing = new Swing(1.5f);
    public static final SwingIn swingIn = new SwingIn(2.0f);
    public static final SwingOut swingOut = new SwingOut(2.0f);

    public abstract float apply(float f);

    public float apply(float start, float end, float a) {
        return ((end - start) * apply(a)) + start;
    }

    public static class Pow extends Interpolation {
        final int power;

        public Pow(int power2) {
            this.power = power2;
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                return ((float) Math.pow((double) (a * 2.0f), (double) this.power)) / 2.0f;
            }
            float pow = (float) Math.pow((double) ((a - 1.0f) * 2.0f), (double) this.power);
            int i = 2;
            if (this.power % 2 == 0) {
                i = -2;
            }
            return (pow / ((float) i)) + 1.0f;
        }
    }

    public static class PowIn extends Pow {
        public PowIn(int power) {
            super(power);
        }

        public float apply(float a) {
            return (float) Math.pow((double) a, (double) this.power);
        }
    }

    public static class PowOut extends Pow {
        public PowOut(int power) {
            super(power);
        }

        public float apply(float a) {
            return (((float) Math.pow((double) (a - 1.0f), (double) this.power)) * ((float) (this.power % 2 == 0 ? -1 : 1))) + 1.0f;
        }
    }

    public static class Exp extends Interpolation {
        final float min;
        final float power;
        final float scale = (1.0f / (1.0f - this.min));
        final float value;

        public Exp(float value2, float power2) {
            this.value = value2;
            this.power = power2;
            this.min = (float) Math.pow((double) value2, (double) (-power2));
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                return ((((float) Math.pow((double) this.value, (double) (this.power * ((a * 2.0f) - 1.0f)))) - this.min) * this.scale) / 2.0f;
            }
            return (2.0f - ((((float) Math.pow((double) this.value, (double) ((-this.power) * ((a * 2.0f) - 1.0f)))) - this.min) * this.scale)) / 2.0f;
        }
    }

    public static class ExpIn extends Exp {
        public ExpIn(float value, float power) {
            super(value, power);
        }

        public float apply(float a) {
            return (((float) Math.pow((double) this.value, (double) (this.power * (a - 1.0f)))) - this.min) * this.scale;
        }
    }

    public static class ExpOut extends Exp {
        public ExpOut(float value, float power) {
            super(value, power);
        }

        public float apply(float a) {
            return 1.0f - ((((float) Math.pow((double) this.value, (double) ((-this.power) * a))) - this.min) * this.scale);
        }
    }

    public static class Elastic extends Interpolation {
        final float bounces;
        final float power;
        final float scale;
        final float value;

        public Elastic(float value2, float power2, int bounces2, float scale2) {
            this.value = value2;
            this.power = power2;
            this.scale = scale2;
            this.bounces = ((float) bounces2) * 3.1415927f * ((float) (bounces2 % 2 == 0 ? 1 : -1));
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                float a2 = a * 2.0f;
                return ((((float) Math.pow((double) this.value, (double) (this.power * (a2 - 1.0f)))) * MathUtils.sin(this.bounces * a2)) * this.scale) / 2.0f;
            }
            float a3 = (1.0f - a) * 2.0f;
            return 1.0f - (((((float) Math.pow((double) this.value, (double) (this.power * (a3 - 1.0f)))) * MathUtils.sin(this.bounces * a3)) * this.scale) / 2.0f);
        }
    }

    public static class ElasticIn extends Elastic {
        public ElasticIn(float value, float power, int bounces, float scale) {
            super(value, power, bounces, scale);
        }

        public float apply(float a) {
            if (((double) a) >= 0.99d) {
                return 1.0f;
            }
            return ((float) Math.pow((double) this.value, (double) (this.power * (a - 1.0f)))) * MathUtils.sin(this.bounces * a) * this.scale;
        }
    }

    public static class ElasticOut extends Elastic {
        public ElasticOut(float value, float power, int bounces, float scale) {
            super(value, power, bounces, scale);
        }

        public float apply(float a) {
            if (a == 0.0f) {
                return 0.0f;
            }
            float a2 = 1.0f - a;
            return 1.0f - ((((float) Math.pow((double) this.value, (double) (this.power * (a2 - 1.0f)))) * MathUtils.sin(this.bounces * a2)) * this.scale);
        }
    }

    public static class Bounce extends BounceOut {
        public Bounce(float[] widths, float[] heights) {
            super(widths, heights);
        }

        public Bounce(int bounces) {
            super(bounces);
        }

        private float out(float a) {
            float test = (this.widths[0] / 2.0f) + a;
            if (test < this.widths[0]) {
                return (test / (this.widths[0] / 2.0f)) - 1.0f;
            }
            return super.apply(a);
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                return (1.0f - out(1.0f - (a * 2.0f))) / 2.0f;
            }
            return (out((a * 2.0f) - 1.0f) / 2.0f) + 0.5f;
        }
    }

    public static class BounceOut extends Interpolation {
        final float[] heights;
        final float[] widths;

        public BounceOut(float[] widths2, float[] heights2) {
            if (widths2.length == heights2.length) {
                this.widths = widths2;
                this.heights = heights2;
                return;
            }
            throw new IllegalArgumentException("Must be the same number of widths and heights.");
        }

        public BounceOut(int bounces) {
            if (bounces < 2 || bounces > 5) {
                throw new IllegalArgumentException("bounces cannot be < 2 or > 5: " + bounces);
            }
            this.widths = new float[bounces];
            this.heights = new float[bounces];
            float[] fArr = this.heights;
            fArr[0] = 1.0f;
            if (bounces == 2) {
                float[] fArr2 = this.widths;
                fArr2[0] = 0.6f;
                fArr2[1] = 0.4f;
                fArr[1] = 0.33f;
            } else if (bounces == 3) {
                float[] fArr3 = this.widths;
                fArr3[0] = 0.4f;
                fArr3[1] = 0.4f;
                fArr3[2] = 0.2f;
                fArr[1] = 0.33f;
                fArr[2] = 0.1f;
            } else if (bounces == 4) {
                float[] fArr4 = this.widths;
                fArr4[0] = 0.34f;
                fArr4[1] = 0.34f;
                fArr4[2] = 0.2f;
                fArr4[3] = 0.15f;
                fArr[1] = 0.26f;
                fArr[2] = 0.11f;
                fArr[3] = 0.03f;
            } else if (bounces == 5) {
                float[] fArr5 = this.widths;
                fArr5[0] = 0.3f;
                fArr5[1] = 0.3f;
                fArr5[2] = 0.2f;
                fArr5[3] = 0.1f;
                fArr5[4] = 0.1f;
                fArr[1] = 0.45f;
                fArr[2] = 0.3f;
                fArr[3] = 0.15f;
                fArr[4] = 0.06f;
            }
            float[] fArr6 = this.widths;
            fArr6[0] = fArr6[0] * 2.0f;
        }

        public float apply(float a) {
            if (a == 1.0f) {
                return 1.0f;
            }
            float[] fArr = this.widths;
            float a2 = a + (fArr[0] / 2.0f);
            float width = 0.0f;
            float height = 0.0f;
            int i = 0;
            int n = fArr.length;
            while (true) {
                if (i >= n) {
                    break;
                }
                width = this.widths[i];
                if (a2 <= width) {
                    height = this.heights[i];
                    break;
                }
                a2 -= width;
                i++;
            }
            float a3 = a2 / width;
            float z = (4.0f / width) * height * a3;
            return 1.0f - ((z - (z * a3)) * width);
        }
    }

    public static class BounceIn extends BounceOut {
        public BounceIn(float[] widths, float[] heights) {
            super(widths, heights);
        }

        public BounceIn(int bounces) {
            super(bounces);
        }

        public float apply(float a) {
            return 1.0f - super.apply(1.0f - a);
        }
    }

    public static class Swing extends Interpolation {
        private final float scale;

        public Swing(float scale2) {
            this.scale = 2.0f * scale2;
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                float a2 = a * 2.0f;
                float f = this.scale;
                return ((a2 * a2) * (((1.0f + f) * a2) - f)) / 2.0f;
            }
            float a3 = (a - 1.0f) * 2.0f;
            float f2 = this.scale;
            return (((a3 * a3) * (((f2 + 1.0f) * a3) + f2)) / 2.0f) + 1.0f;
        }
    }

    public static class SwingOut extends Interpolation {
        private final float scale;

        public SwingOut(float scale2) {
            this.scale = scale2;
        }

        public float apply(float a) {
            float a2 = a - 1.0f;
            float f = this.scale;
            return (a2 * a2 * (((f + 1.0f) * a2) + f)) + 1.0f;
        }
    }

    public static class SwingIn extends Interpolation {
        private final float scale;

        public SwingIn(float scale2) {
            this.scale = scale2;
        }

        public float apply(float a) {
            float f = this.scale;
            return a * a * (((1.0f + f) * a) - f);
        }
    }
}
