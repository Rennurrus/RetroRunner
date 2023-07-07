package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;

public class ButtonGroup<T extends Button> {
    private final Array<T> buttons = new Array<>();
    private Array<T> checkedButtons = new Array<>(1);
    private T lastChecked;
    private int maxCheckCount = 1;
    private int minCheckCount = 1;
    private boolean uncheckLast = true;

    public ButtonGroup() {
    }

    public ButtonGroup(T... buttons2) {
        add(buttons2);
        this.minCheckCount = 1;
    }

    public void add(T button) {
        if (button != null) {
            button.buttonGroup = null;
            boolean shouldCheck = button.isChecked() || this.buttons.size < this.minCheckCount;
            button.setChecked(false);
            button.buttonGroup = this;
            this.buttons.add(button);
            button.setChecked(shouldCheck);
            return;
        }
        throw new IllegalArgumentException("button cannot be null.");
    }

    public void add(T... buttons2) {
        if (buttons2 != null) {
            for (T add : buttons2) {
                add(add);
            }
            return;
        }
        throw new IllegalArgumentException("buttons cannot be null.");
    }

    public void remove(T button) {
        if (button != null) {
            button.buttonGroup = null;
            this.buttons.removeValue(button, true);
            this.checkedButtons.removeValue(button, true);
            return;
        }
        throw new IllegalArgumentException("button cannot be null.");
    }

    public void remove(T... buttons2) {
        if (buttons2 != null) {
            for (T remove : buttons2) {
                remove(remove);
            }
            return;
        }
        throw new IllegalArgumentException("buttons cannot be null.");
    }

    public void clear() {
        this.buttons.clear();
        this.checkedButtons.clear();
    }

    public void setChecked(String text) {
        if (text != null) {
            int i = 0;
            int n = this.buttons.size;
            while (i < n) {
                T button = (Button) this.buttons.get(i);
                if (!(button instanceof TextButton) || !text.contentEquals(((TextButton) button).getText())) {
                    i++;
                } else {
                    button.setChecked(true);
                    return;
                }
            }
            return;
        }
        throw new IllegalArgumentException("text cannot be null.");
    }

    /* access modifiers changed from: protected */
    public boolean canCheck(T button, boolean newState) {
        if (button.isChecked == newState) {
            return false;
        }
        if (newState) {
            if (this.maxCheckCount != -1 && this.checkedButtons.size >= this.maxCheckCount) {
                if (!this.uncheckLast) {
                    return false;
                }
                int old = this.minCheckCount;
                this.minCheckCount = 0;
                this.lastChecked.setChecked(false);
                this.minCheckCount = old;
            }
            this.checkedButtons.add(button);
            this.lastChecked = button;
        } else if (this.checkedButtons.size <= this.minCheckCount) {
            return false;
        } else {
            this.checkedButtons.removeValue(button, true);
        }
        return true;
    }

    public void uncheckAll() {
        int old = this.minCheckCount;
        this.minCheckCount = 0;
        int n = this.buttons.size;
        for (int i = 0; i < n; i++) {
            ((Button) this.buttons.get(i)).setChecked(false);
        }
        this.minCheckCount = old;
    }

    public T getChecked() {
        if (this.checkedButtons.size > 0) {
            return (Button) this.checkedButtons.get(0);
        }
        return null;
    }

    public int getCheckedIndex() {
        if (this.checkedButtons.size > 0) {
            return this.buttons.indexOf(this.checkedButtons.get(0), true);
        }
        return -1;
    }

    public Array<T> getAllChecked() {
        return this.checkedButtons;
    }

    public Array<T> getButtons() {
        return this.buttons;
    }

    public void setMinCheckCount(int minCheckCount2) {
        this.minCheckCount = minCheckCount2;
    }

    public void setMaxCheckCount(int maxCheckCount2) {
        if (maxCheckCount2 == 0) {
            maxCheckCount2 = -1;
        }
        this.maxCheckCount = maxCheckCount2;
    }

    public void setUncheckLast(boolean uncheckLast2) {
        this.uncheckLast = uncheckLast2;
    }
}
