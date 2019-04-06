
package com.gedo.server.business.path;


import com.gedo.server.business.annotation.NettyHttpHandler;

/**
 * Created by Gedo on 2019/4/3.
 */
public class Path {
    private String method;
    private String uri;
    private boolean equal;

    public static Path build(NettyHttpHandler annotation) {
        return new Path(annotation);
    }

    public Path(NettyHttpHandler annotation) {
        method = annotation.method();
        String rootCtx = "/seq-server";
        uri = rootCtx + annotation.path();
        equal = annotation.equal();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEqual() {
        return equal;
    }

    public void setEqual(boolean equal) {
        this.equal = equal;
    }

    @Override
    public String toString() {
        return method.toUpperCase() + " " + uri.toUpperCase();
    }

    @Override
    public int hashCode() {
        return ("HTTP " + method.toUpperCase() + " " + uri.toUpperCase()).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Path) {
            Path path = (Path) object;
            return method.equalsIgnoreCase(path.method) && uri.equalsIgnoreCase(path.uri);
        }
        return false;
    }
}
