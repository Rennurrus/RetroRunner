package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class ArraySelection<T> extends Selection<T> {
    private Array<T> array;
    private boolean rangeSelect = true;
    private T rangeStart;

    public ArraySelection(Array<T> array2) {
        this.array = array2;
    }

    public void choose(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        } else if (!this.isDisabled) {
            if (!this.rangeSelect || !this.multiple) {
                super.choose(item);
                return;
            }
            if (this.selected.size > 0 && UIUtils.shift()) {
                T t = this.rangeStart;
                int rangeStartIndex = t == null ? -1 : this.array.indexOf(t, false);
                if (rangeStartIndex != -1) {
                    T oldRangeStart = this.rangeStart;
                    snapshot();
                    int start = rangeStartIndex;
                    int end = this.array.indexOf(item, false);
                    if (start > end) {
                        int temp = end;
                        end = start;
                        start = temp;
                    }
                    if (UIUtils.ctrl() == 0) {
                        this.selected.clear(8);
                    }
                    for (int i = start; i <= end; i++) {
                        this.selected.add(this.array.get(i));
                    }
                    if (fireChangeEvent() != 0) {
                        revert();
                    } else {
                        changed();
                    }
                    this.rangeStart = oldRangeStart;
                    cleanup();
                    return;
                }
            }
            super.choose(item);
            this.rangeStart = item;
        }
    }

    /* access modifiers changed from: protected */
    public void changed() {
        this.rangeStart = null;
    }

    public boolean getRangeSelect() {
        return this.rangeSelect;
    }

    public void setRangeSelect(boolean rangeSelect2) {
        this.rangeSelect = rangeSelect2;
    }

    public void validate() {
        Array<T> array2 = this.array;
        if (array2.size == 0) {
            clear();
            return;
        }
        Iterator<T> iter = items().iterator();
        while (iter.hasNext()) {
            if (!array2.contains(iter.next(), false)) {
                iter.remove();
            }
        }
        if (this.required && this.selected.size == 0) {
            set(array2.first());
        }
    }
}
