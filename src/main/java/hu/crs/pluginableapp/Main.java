package hu.crs.pluginableapp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    public static void main(String[] args) {
        var file = new File(".\\build\\libs");
        File[] jars = file.listFiles();

        if (jars == null || jars.length == 0) {
            throw new IllegalArgumentException();
        }

        Arrays.stream(Objects.requireNonNull(jars[0].listFiles()))
                .map(jar -> {
                    try {
                        return loadJar(jar.getAbsolutePath().replace(".\\", ""));
                    } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException();
                    }
                })
                .filter(Objects::nonNull)
                .forEach(plugin -> System.out.println(plugin.result()));
    }

    public static Plugin loadJar(String pathToJar) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);
            if (Plugin.class.isAssignableFrom(c)) {
                return (Plugin) c.getDeclaredConstructor().newInstance();
            }
        }
        return null;
    }
}
