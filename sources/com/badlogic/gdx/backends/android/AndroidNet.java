package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.NetJavaImpl;
import com.badlogic.gdx.net.NetJavaServerSocketImpl;
import com.badlogic.gdx.net.NetJavaSocketImpl;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class AndroidNet implements Net {
    final AndroidApplicationBase app;
    NetJavaImpl netJavaImpl;

    public AndroidNet(AndroidApplicationBase app2, AndroidApplicationConfiguration configuration) {
        this.app = app2;
        this.netJavaImpl = new NetJavaImpl(configuration.maxNetThreads);
    }

    public void sendHttpRequest(Net.HttpRequest httpRequest, Net.HttpResponseListener httpResponseListener) {
        this.netJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
    }

    public void cancelHttpRequest(Net.HttpRequest httpRequest) {
        this.netJavaImpl.cancelHttpRequest(httpRequest);
    }

    public ServerSocket newServerSocket(Net.Protocol protocol, String hostname, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, hostname, port, hints);
    }

    public ServerSocket newServerSocket(Net.Protocol protocol, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, port, hints);
    }

    public Socket newClientSocket(Net.Protocol protocol, String host, int port, SocketHints hints) {
        return new NetJavaSocketImpl(protocol, host, port, hints);
    }

    public boolean openURI(String URI) {
        final Uri uri = Uri.parse(URI);
        if (this.app.getContext().getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", uri), 65536) == null) {
            return false;
        }
        this.app.runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                if (!(AndroidNet.this.app.getContext() instanceof Activity)) {
                    intent.addFlags(268435456);
                }
                AndroidNet.this.app.startActivity(intent);
            }
        });
        return true;
    }
}
