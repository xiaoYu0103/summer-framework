package org.yxw.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

/*
* ResourceResolver 只负责扫描并列出所有文件，由客户端觉得是找出.class文件还是.properties文件
* */
public class ResourceResolver {
    Logger logger = LoggerFactory.getLogger(getClass());
    String basePackage;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    public <R> List<R> scan(Function<Resource, R> mapper) {
        String basePackagePath= this.basePackage.replace(".", "/");
        String path = basePackagePath;
        try {
            List<R> collector = new ArrayList<>();
            scan0(basePackagePath, path, collector, mapper);
            return collector;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }



    public ClassLoader getContextClassLoader(){
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }

    /*
    * 资源扫描的入口方法，负责从类路径中查找并处理特定包路径下的所有资源
    * */
    public <R> void scan0(String basePackage, String path, List<R> collector, Function<Resource, R> mapper) throws IOException, URISyntaxException {
        logger.debug("scan paht:{}", path);
        Enumeration<URL> enURL = getContextClassLoader().getResources(path);
        while (enURL.hasMoreElements()) {
            URL url = enURL.nextElement();
            URI uri = url.toURI();
            String uriString = removeTrailingSlash(uriToString(uri));
            String uriBaseStr = uriString.substring(0, uriString.length() - basePackage.length());
            if (uriBaseStr.startsWith("file:")){
                uriBaseStr = uriBaseStr.substring(5);
            }
            if (uriString.startsWith("jar:")) {
                scanFile(true, uriBaseStr, jarUriToPath(basePackage, uri), collector, mapper);
            } else {
                scanFile(false, uriBaseStr, Paths.get(uri), collector, mapper);
            }
        }
    }

    /*
    * 扫描文件系统中的文件资源，并将符合条件的资源收集起来。
    * */
    public <R> void scanFile(boolean isJar, String base, Path root, List<R> collector, Function<Resource, R> mapper) throws IOException {
        String baseDir = removeTrailingSlash(base);
        Files.walk(root).filter(Files::isRegularFile).forEach(file -> {
            Resource resource = null;
            if (isJar) {
                resource = new Resource(baseDir, removeLeadingSlash(file.toString()));
            } else {
                String path = file.toString();
                String name = removeLeadingSlash(path.substring(baseDir.length()));
                resource = new Resource("file:" + path, name);
            }
            logger.debug("found resource: {}", resource);
            R r = mapper.apply(resource);
            if (r != null) {
                collector.add(r);
            }
        });
    }

    public Path jarUriToPath(String basePackagePath, URI jarUri) throws IOException {
        return FileSystems.newFileSystem(jarUri, Collections.emptyMap()).getPath(basePackagePath);
    }

    public String uriToString(URI uri) throws IOException  {
        return URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8.toString());
    }

    public String removeLeadingSlash(String s) {
        if (s.startsWith("/") || s.startsWith("\\")) {
            s = s.substring(1);
        }
        return s;
    }

    public String removeTrailingSlash(String s) {
        if (s.endsWith("/") || s.endsWith("\\")) {
            s = s.substring(0,s.length()-1);
        }
        return s;
    }
}
