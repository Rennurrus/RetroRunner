package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;

public class AnimationController extends BaseAnimationController {
    public boolean allowSameAnimation;
    protected final Pool<AnimationDesc> animationPool = new Pool<AnimationDesc>() {
        /* access modifiers changed from: protected */
        public AnimationDesc newObject() {
            return new AnimationDesc();
        }
    };
    public AnimationDesc current;
    public boolean inAction;
    private boolean justChangedAnimation = false;
    public boolean paused;
    public AnimationDesc previous;
    public AnimationDesc queued;
    public float queuedTransitionTime;
    public float transitionCurrentTime;
    public float transitionTargetTime;

    public interface AnimationListener {
        void onEnd(AnimationDesc animationDesc);

        void onLoop(AnimationDesc animationDesc);
    }

    public static class AnimationDesc {
        public Animation animation;
        public float duration;
        public AnimationListener listener;
        public int loopCount;
        public float offset;
        public float speed;
        public float time;

        protected AnimationDesc() {
        }

        /* access modifiers changed from: protected */
        public float update(float delta) {
            int loops;
            AnimationListener animationListener;
            if (this.loopCount == 0 || this.animation == null) {
                return delta;
            }
            float diff = this.speed * delta;
            float f = 0.0f;
            if (!MathUtils.isZero(this.duration)) {
                this.time += diff;
                loops = (int) Math.abs(this.time / this.duration);
                if (this.time < 0.0f) {
                    loops++;
                    while (true) {
                        float f2 = this.time;
                        if (f2 >= 0.0f) {
                            break;
                        }
                        this.time = f2 + this.duration;
                    }
                }
                this.time = Math.abs(this.time % this.duration);
            } else {
                loops = 1;
            }
            for (int i = 0; i < loops; i++) {
                int i2 = this.loopCount;
                if (i2 > 0) {
                    this.loopCount = i2 - 1;
                }
                if (!(this.loopCount == 0 || (animationListener = this.listener) == null)) {
                    animationListener.onLoop(this);
                }
                if (this.loopCount == 0) {
                    float f3 = this.duration;
                    float result = (((float) ((loops - 1) - i)) * f3) + (diff < 0.0f ? f3 - this.time : this.time);
                    if (diff >= 0.0f) {
                        f = this.duration;
                    }
                    this.time = f;
                    AnimationListener animationListener2 = this.listener;
                    if (animationListener2 != null) {
                        animationListener2.onEnd(this);
                    }
                    return result;
                }
            }
            return 0.0f;
        }
    }

    public AnimationController(ModelInstance target) {
        super(target);
    }

    private AnimationDesc obtain(Animation anim, float offset, float duration, int loopCount, float speed, AnimationListener listener) {
        if (anim == null) {
            return null;
        }
        AnimationDesc result = this.animationPool.obtain();
        result.animation = anim;
        result.listener = listener;
        result.loopCount = loopCount;
        result.speed = speed;
        result.offset = offset;
        float f = 0.0f;
        result.duration = duration < 0.0f ? anim.duration - offset : duration;
        if (speed < 0.0f) {
            f = result.duration;
        }
        result.time = f;
        return result;
    }

    private AnimationDesc obtain(String id, float offset, float duration, int loopCount, float speed, AnimationListener listener) {
        if (id == null) {
            return null;
        }
        Animation anim = this.target.getAnimation(id);
        if (anim != null) {
            return obtain(anim, offset, duration, loopCount, speed, listener);
        }
        throw new GdxRuntimeException("Unknown animation: " + id);
    }

    private AnimationDesc obtain(AnimationDesc anim) {
        return obtain(anim.animation, anim.offset, anim.duration, anim.loopCount, anim.speed, anim.listener);
    }

    public void update(float delta) {
        AnimationDesc animationDesc;
        if (!this.paused) {
            AnimationDesc animationDesc2 = this.previous;
            if (animationDesc2 != null) {
                float f = this.transitionCurrentTime + delta;
                this.transitionCurrentTime = f;
                if (f >= this.transitionTargetTime) {
                    removeAnimation(animationDesc2.animation);
                    this.justChangedAnimation = true;
                    this.animationPool.free(this.previous);
                    this.previous = null;
                }
            }
            if (this.justChangedAnimation) {
                this.target.calculateTransforms();
                this.justChangedAnimation = false;
            }
            AnimationDesc animationDesc3 = this.current;
            if (animationDesc3 != null && animationDesc3.loopCount != 0 && this.current.animation != null) {
                float remain = this.current.update(delta);
                if (remain == 0.0f || (animationDesc = this.queued) == null) {
                    AnimationDesc animationDesc4 = this.previous;
                    if (animationDesc4 != null) {
                        applyAnimations(animationDesc4.animation, this.previous.offset + this.previous.time, this.current.animation, this.current.offset + this.current.time, this.transitionCurrentTime / this.transitionTargetTime);
                        return;
                    }
                    applyAnimation(this.current.animation, this.current.offset + this.current.time);
                    return;
                }
                this.inAction = false;
                animate(animationDesc, this.queuedTransitionTime);
                this.queued = null;
                update(remain);
            }
        }
    }

    public AnimationDesc setAnimation(String id) {
        return setAnimation(id, 1, 1.0f, (AnimationListener) null);
    }

    public AnimationDesc setAnimation(String id, int loopCount) {
        return setAnimation(id, loopCount, 1.0f, (AnimationListener) null);
    }

    public AnimationDesc setAnimation(String id, AnimationListener listener) {
        return setAnimation(id, 1, 1.0f, listener);
    }

    public AnimationDesc setAnimation(String id, int loopCount, AnimationListener listener) {
        return setAnimation(id, loopCount, 1.0f, listener);
    }

    public AnimationDesc setAnimation(String id, int loopCount, float speed, AnimationListener listener) {
        return setAnimation(id, 0.0f, -1.0f, loopCount, speed, listener);
    }

    public AnimationDesc setAnimation(String id, float offset, float duration, int loopCount, float speed, AnimationListener listener) {
        return setAnimation(obtain(id, offset, duration, loopCount, speed, listener));
    }

    /* access modifiers changed from: protected */
    public AnimationDesc setAnimation(Animation anim, float offset, float duration, int loopCount, float speed, AnimationListener listener) {
        return setAnimation(obtain(anim, offset, duration, loopCount, speed, listener));
    }

    /* access modifiers changed from: protected */
    public AnimationDesc setAnimation(AnimationDesc anim) {
        AnimationDesc animationDesc = this.current;
        if (animationDesc == null) {
            this.current = anim;
        } else {
            if (this.allowSameAnimation || anim == null || animationDesc.animation != anim.animation) {
                removeAnimation(this.current.animation);
            } else {
                anim.time = this.current.time;
            }
            this.animationPool.free(this.current);
            this.current = anim;
        }
        this.justChangedAnimation = true;
        return anim;
    }

    public AnimationDesc animate(String id, float transitionTime) {
        return animate(id, 1, 1.0f, (AnimationListener) null, transitionTime);
    }

    public AnimationDesc animate(String id, AnimationListener listener, float transitionTime) {
        return animate(id, 1, 1.0f, listener, transitionTime);
    }

    public AnimationDesc animate(String id, int loopCount, AnimationListener listener, float transitionTime) {
        return animate(id, loopCount, 1.0f, listener, transitionTime);
    }

    public AnimationDesc animate(String id, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return animate(id, 0.0f, -1.0f, loopCount, speed, listener, transitionTime);
    }

    public AnimationDesc animate(String id, float offset, float duration, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return animate(obtain(id, offset, duration, loopCount, speed, listener), transitionTime);
    }

    /* access modifiers changed from: protected */
    public AnimationDesc animate(Animation anim, float offset, float duration, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return animate(obtain(anim, offset, duration, loopCount, speed, listener), transitionTime);
    }

    /* access modifiers changed from: protected */
    public AnimationDesc animate(AnimationDesc anim, float transitionTime) {
        AnimationDesc animationDesc = this.current;
        if (animationDesc == null) {
            this.current = anim;
        } else if (this.inAction) {
            queue(anim, transitionTime);
        } else if (this.allowSameAnimation || anim == null || animationDesc.animation != anim.animation) {
            AnimationDesc animationDesc2 = this.previous;
            if (animationDesc2 != null) {
                removeAnimation(animationDesc2.animation);
                this.animationPool.free(this.previous);
            }
            this.previous = this.current;
            this.current = anim;
            this.transitionCurrentTime = 0.0f;
            this.transitionTargetTime = transitionTime;
        } else {
            anim.time = this.current.time;
            this.animationPool.free(this.current);
            this.current = anim;
        }
        return anim;
    }

    public AnimationDesc queue(String id, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return queue(id, 0.0f, -1.0f, loopCount, speed, listener, transitionTime);
    }

    public AnimationDesc queue(String id, float offset, float duration, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return queue(obtain(id, offset, duration, loopCount, speed, listener), transitionTime);
    }

    /* access modifiers changed from: protected */
    public AnimationDesc queue(Animation anim, float offset, float duration, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return queue(obtain(anim, offset, duration, loopCount, speed, listener), transitionTime);
    }

    /* access modifiers changed from: protected */
    public AnimationDesc queue(AnimationDesc anim, float transitionTime) {
        AnimationDesc animationDesc = this.current;
        if (animationDesc == null || animationDesc.loopCount == 0) {
            animate(anim, transitionTime);
        } else {
            AnimationDesc animationDesc2 = this.queued;
            if (animationDesc2 != null) {
                this.animationPool.free(animationDesc2);
            }
            this.queued = anim;
            this.queuedTransitionTime = transitionTime;
            if (this.current.loopCount < 0) {
                this.current.loopCount = 1;
            }
        }
        return anim;
    }

    public AnimationDesc action(String id, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return action(id, 0.0f, -1.0f, loopCount, speed, listener, transitionTime);
    }

    public AnimationDesc action(String id, float offset, float duration, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return action(obtain(id, offset, duration, loopCount, speed, listener), transitionTime);
    }

    /* access modifiers changed from: protected */
    public AnimationDesc action(Animation anim, float offset, float duration, int loopCount, float speed, AnimationListener listener, float transitionTime) {
        return action(obtain(anim, offset, duration, loopCount, speed, listener), transitionTime);
    }

    /* access modifiers changed from: protected */
    public AnimationDesc action(AnimationDesc anim, float transitionTime) {
        if (anim.loopCount >= 0) {
            AnimationDesc animationDesc = this.current;
            if (animationDesc == null || animationDesc.loopCount == 0) {
                animate(anim, transitionTime);
            } else {
                AnimationDesc toQueue = this.inAction ? null : obtain(this.current);
                this.inAction = false;
                animate(anim, transitionTime);
                this.inAction = true;
                if (toQueue != null) {
                    queue(toQueue, transitionTime);
                }
            }
            return anim;
        }
        throw new GdxRuntimeException("An action cannot be continuous");
    }
}
