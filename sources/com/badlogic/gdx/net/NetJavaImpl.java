package com.badlogic.gdx.net;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StreamUtils;
import com.twi.game.BuildConfig;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NetJavaImpl {
    final ObjectMap<Net.HttpRequest, HttpURLConnection> connections;
    private final ExecutorService executorService;
    final ObjectMap<Net.HttpRequest, Net.HttpResponseListener> listeners;

    static class HttpClientResponse implements Net.HttpResponse {
        private final HttpURLConnection connection;
        private HttpStatus status;

        public HttpClientResponse(HttpURLConnection connection2) throws IOException {
            this.connection = connection2;
            try {
                this.status = new HttpStatus(connection2.getResponseCode());
            } catch (IOException e) {
                this.status = new HttpStatus(-1);
            }
        }

        public byte[] getResult() {
            InputStream input = getInputStream();
            if (input == null) {
                return StreamUtils.EMPTY_BYTES;
            }
            try {
                return StreamUtils.copyStreamToByteArray(input, this.connection.getContentLength());
            } catch (IOException e) {
                return StreamUtils.EMPTY_BYTES;
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        public String getResultAsString() {
            InputStream input = getInputStream();
            if (input == null) {
                return BuildConfig.FLAVOR;
            }
            try {
                return StreamUtils.copyStreamToString(input, this.connection.getContentLength(), "UTF8");
            } catch (IOException e) {
                return BuildConfig.FLAVOR;
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }

        public InputStream getResultAsStream() {
            return getInputStream();
        }

        public HttpStatus getStatus() {
            return this.status;
        }

        public String getHeader(String name) {
            return this.connection.getHeaderField(name);
        }

        public Map<String, List<String>> getHeaders() {
            return this.connection.getHeaderFields();
        }

        private InputStream getInputStream() {
            try {
                return this.connection.getInputStream();
            } catch (IOException e) {
                return this.connection.getErrorStream();
            }
        }
    }

    public NetJavaImpl() {
        this(Integer.MAX_VALUE);
    }

    public NetJavaImpl(int maxThreads) {
        this.executorService = new ThreadPoolExecutor(0, maxThreads, 60, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "NetThread");
                thread.setDaemon(true);
                return thread;
            }
        });
        this.connections = new ObjectMap<>();
        this.listeners = new ObjectMap<>();
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x00a9 A[Catch:{ Exception -> 0x00df, all -> 0x00e8 }, LOOP:0: B:24:0x00a3->B:26:0x00a9, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendHttpRequest(com.badlogic.gdx.Net.HttpRequest r13, com.badlogic.gdx.Net.HttpResponseListener r14) {
        /*
            r12 = this;
            java.lang.String r0 = ""
            java.lang.String r1 = r13.getUrl()
            if (r1 != 0) goto L_0x0013
            com.badlogic.gdx.utils.GdxRuntimeException r0 = new com.badlogic.gdx.utils.GdxRuntimeException
            java.lang.String r1 = "can't process a HTTP request without URL set"
            r0.<init>((java.lang.String) r1)
            r14.failed(r0)
            return
        L_0x0013:
            java.lang.String r1 = r13.getMethod()     // Catch:{ Exception -> 0x00df }
            java.lang.String r2 = "GET"
            boolean r2 = r1.equalsIgnoreCase(r2)     // Catch:{ Exception -> 0x00df }
            if (r2 == 0) goto L_0x0057
            r2 = r0
            java.lang.String r3 = r13.getContent()     // Catch:{ Exception -> 0x00df }
            if (r3 == 0) goto L_0x003e
            boolean r0 = r0.equals(r3)     // Catch:{ Exception -> 0x00df }
            if (r0 != 0) goto L_0x003e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00df }
            r0.<init>()     // Catch:{ Exception -> 0x00df }
            java.lang.String r4 = "?"
            r0.append(r4)     // Catch:{ Exception -> 0x00df }
            r0.append(r3)     // Catch:{ Exception -> 0x00df }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x00df }
            r2 = r0
        L_0x003e:
            java.net.URL r0 = new java.net.URL     // Catch:{ Exception -> 0x00df }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00df }
            r4.<init>()     // Catch:{ Exception -> 0x00df }
            java.lang.String r5 = r13.getUrl()     // Catch:{ Exception -> 0x00df }
            r4.append(r5)     // Catch:{ Exception -> 0x00df }
            r4.append(r2)     // Catch:{ Exception -> 0x00df }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00df }
            r0.<init>(r4)     // Catch:{ Exception -> 0x00df }
            goto L_0x0060
        L_0x0057:
            java.net.URL r0 = new java.net.URL     // Catch:{ Exception -> 0x00df }
            java.lang.String r2 = r13.getUrl()     // Catch:{ Exception -> 0x00df }
            r0.<init>(r2)     // Catch:{ Exception -> 0x00df }
        L_0x0060:
            java.net.URLConnection r2 = r0.openConnection()     // Catch:{ Exception -> 0x00df }
            java.net.HttpURLConnection r2 = (java.net.HttpURLConnection) r2     // Catch:{ Exception -> 0x00df }
            java.lang.String r3 = "POST"
            boolean r3 = r1.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x00df }
            r4 = 1
            if (r3 != 0) goto L_0x0082
            java.lang.String r3 = "PUT"
            boolean r3 = r1.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x00df }
            if (r3 != 0) goto L_0x0082
            java.lang.String r3 = "PATCH"
            boolean r3 = r1.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x00df }
            if (r3 == 0) goto L_0x0080
            goto L_0x0082
        L_0x0080:
            r3 = 0
            goto L_0x0083
        L_0x0082:
            r3 = 1
        L_0x0083:
            r9 = r3
            r2.setDoOutput(r9)     // Catch:{ Exception -> 0x00df }
            r2.setDoInput(r4)     // Catch:{ Exception -> 0x00df }
            r2.setRequestMethod(r1)     // Catch:{ Exception -> 0x00df }
            boolean r3 = r13.getFollowRedirects()     // Catch:{ Exception -> 0x00df }
            java.net.HttpURLConnection.setFollowRedirects(r3)     // Catch:{ Exception -> 0x00df }
            r12.putIntoConnectionsAndListeners(r13, r14, r2)     // Catch:{ Exception -> 0x00df }
            java.util.Map r3 = r13.getHeaders()     // Catch:{ Exception -> 0x00df }
            java.util.Set r3 = r3.entrySet()     // Catch:{ Exception -> 0x00df }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ Exception -> 0x00df }
        L_0x00a3:
            boolean r4 = r3.hasNext()     // Catch:{ Exception -> 0x00df }
            if (r4 == 0) goto L_0x00bf
            java.lang.Object r4 = r3.next()     // Catch:{ Exception -> 0x00df }
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4     // Catch:{ Exception -> 0x00df }
            java.lang.Object r5 = r4.getKey()     // Catch:{ Exception -> 0x00df }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x00df }
            java.lang.Object r6 = r4.getValue()     // Catch:{ Exception -> 0x00df }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x00df }
            r2.addRequestProperty(r5, r6)     // Catch:{ Exception -> 0x00df }
            goto L_0x00a3
        L_0x00bf:
            int r3 = r13.getTimeOut()     // Catch:{ Exception -> 0x00df }
            r2.setConnectTimeout(r3)     // Catch:{ Exception -> 0x00df }
            int r3 = r13.getTimeOut()     // Catch:{ Exception -> 0x00df }
            r2.setReadTimeout(r3)     // Catch:{ Exception -> 0x00df }
            java.util.concurrent.ExecutorService r10 = r12.executorService     // Catch:{ Exception -> 0x00df }
            com.badlogic.gdx.net.NetJavaImpl$2 r11 = new com.badlogic.gdx.net.NetJavaImpl$2     // Catch:{ Exception -> 0x00df }
            r3 = r11
            r4 = r12
            r5 = r9
            r6 = r13
            r7 = r2
            r8 = r14
            r3.<init>(r5, r6, r7, r8)     // Catch:{ Exception -> 0x00df }
            r10.submit(r11)     // Catch:{ Exception -> 0x00df }
            return
        L_0x00df:
            r0 = move-exception
            r14.failed(r0)     // Catch:{ all -> 0x00e8 }
            r12.removeFromConnectionsAndListeners(r13)
            return
        L_0x00e8:
            r1 = move-exception
            r12.removeFromConnectionsAndListeners(r13)
            goto L_0x00ee
        L_0x00ed:
            throw r1
        L_0x00ee:
            goto L_0x00ed
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.net.NetJavaImpl.sendHttpRequest(com.badlogic.gdx.Net$HttpRequest, com.badlogic.gdx.Net$HttpResponseListener):void");
    }

    public void cancelHttpRequest(Net.HttpRequest httpRequest) {
        Net.HttpResponseListener httpResponseListener = getFromListeners(httpRequest);
        if (httpResponseListener != null) {
            httpResponseListener.cancelled();
            removeFromConnectionsAndListeners(httpRequest);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void removeFromConnectionsAndListeners(Net.HttpRequest httpRequest) {
        this.connections.remove(httpRequest);
        this.listeners.remove(httpRequest);
    }

    /* access modifiers changed from: package-private */
    public synchronized void putIntoConnectionsAndListeners(Net.HttpRequest httpRequest, Net.HttpResponseListener httpResponseListener, HttpURLConnection connection) {
        this.connections.put(httpRequest, connection);
        this.listeners.put(httpRequest, httpResponseListener);
    }

    /* access modifiers changed from: package-private */
    public synchronized Net.HttpResponseListener getFromListeners(Net.HttpRequest httpRequest) {
        return this.listeners.get(httpRequest);
    }
}
