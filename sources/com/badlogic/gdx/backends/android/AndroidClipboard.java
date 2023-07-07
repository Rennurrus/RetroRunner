package com.badlogic.gdx.backends.android;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.text.ClipboardManager;
import com.badlogic.gdx.utils.Clipboard;

public class AndroidClipboard implements Clipboard {
    private ClipboardManager clipboard;
    private android.content.ClipboardManager honeycombClipboard;

    public AndroidClipboard(Context context) {
        if (Build.VERSION.SDK_INT < 11) {
            this.clipboard = (ClipboardManager) context.getSystemService("clipboard");
        } else {
            this.honeycombClipboard = (android.content.ClipboardManager) context.getSystemService("clipboard");
        }
    }

    public String getContents() {
        CharSequence text;
        if (Build.VERSION.SDK_INT >= 11) {
            ClipData clip = this.honeycombClipboard.getPrimaryClip();
            if (clip == null || (text = clip.getItemAt(0).getText()) == null) {
                return null;
            }
            return text.toString();
        } else if (this.clipboard.getText() == null) {
            return null;
        } else {
            return this.clipboard.getText().toString();
        }
    }

    public void setContents(String contents) {
        if (Build.VERSION.SDK_INT < 11) {
            this.clipboard.setText(contents);
            return;
        }
        this.honeycombClipboard.setPrimaryClip(ClipData.newPlainText(contents, contents));
    }
}
