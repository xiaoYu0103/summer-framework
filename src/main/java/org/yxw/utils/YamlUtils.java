package org.yxw.utils;


import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yxw.io.NoImplicitResolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class YamlUtils {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadYaml(String filePath) {
        LoaderOptions loaderOptions = new LoaderOptions();
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        NoImplicitResolver resolver = new NoImplicitResolver();
        Yaml yaml = new Yaml(new Constructor(loaderOptions), representer, dumperOptions, loaderOptions, resolver);
        return ClassPathUtils.readInputStream(filePath, (input) ->{
            return (Map<String, Object>) yaml.load(input);
        });
    }

    public static Map<String, Object> loadYamlAsPlainMap(String filePath) {
        Map<String, Object> map = loadYaml(filePath);
        Map<String, Object> plain = new LinkedHashMap<>();
        convertToPlainMap(map, plain, "");
        return plain;
    }

    private static void convertToPlainMap(Map<String, Object> source, Map<String, Object> plain, String prefix) {
        for (String key : source.keySet()){
            Object value = source.get(key);
            if (value instanceof Map){
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                convertToPlainMap(subMap,  plain, prefix + key + ".");
            } else if (value instanceof List){
                plain.put(prefix + key, value);
            } else {
                plain.put(prefix + key, value.toString());
            }
        }
    }

}
