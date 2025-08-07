package org.yxw.io;

public final class Resource {
    private final String path;
    private final String name;

    Resource(String path, String name){
        this.path = path;
        this.name = name;
    }

    public String getPath(){
        return path;
    }

    public String getName(){
        return name;
    }

    public String toString() {
        return "path:" + path + ", name:" + name;
    }
}
