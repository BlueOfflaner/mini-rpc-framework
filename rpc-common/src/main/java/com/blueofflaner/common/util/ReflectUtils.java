package com.blueofflaner.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectUtils {
    public ReflectUtils() {
    }

    public static String getStackTrace() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return stack[stack.length - 1].getClassName();
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');

        try {
            Enumeration dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

            while(true) {
                label67:
                while(dirs.hasMoreElements()) {
                    URL url = (URL)dirs.nextElement();
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                    } else if ("jar".equals(protocol)) {
                        try {
                            JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                            Enumeration entries = jar.entries();

                            while(true) {
                                JarEntry entry;
                                String name;
                                int idx;
                                do {
                                    do {
                                        if (!entries.hasMoreElements()) {
                                            continue label67;
                                        }

                                        entry = (JarEntry)entries.nextElement();
                                        name = entry.getName();
                                        if (name.charAt(0) == '/') {
                                            name = name.substring(1);
                                        }
                                    } while(!name.startsWith(packageDirName));

                                    idx = name.lastIndexOf(47);
                                    if (idx != -1) {
                                        packageName = name.substring(0, idx).replace('/', '.');
                                    }
                                } while(idx == -1 && !recursive);

                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);

                                    try {
                                        classes.add(Class.forName(packageName + '.' + className));
                                    } catch (ClassNotFoundException var14) {
                                        var14.printStackTrace();
                                    }
                                }
                            }
                        } catch (IOException var15) {
                            var15.printStackTrace();
                        }
                    }
                }

                return classes;
            }
        } catch (IOException var16) {
            var16.printStackTrace();
            return classes;
        }
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (dir.exists() && dir.isDirectory()) {
            File[] dirfiles = dir.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return recursive && file.isDirectory() || file.getName().endsWith(".class");
                }
            });
            File[] var6 = dirfiles;
            int var7 = dirfiles.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                File file = var6[var8];
                if (file.isDirectory()) {
                    findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
                } else {
                    String className = file.getName().substring(0, file.getName().length() - 6);

                    try {
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                    } catch (ClassNotFoundException var12) {
                        var12.printStackTrace();
                    }
                }
            }

        }
    }

    public static String getMainClassName() {
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        return stack[stack.length - 1].getClassName();
    }

    public static String getBasePackageName() {
        String mainClassName = getMainClassName();
        return mainClassName.substring(0, mainClassName.lastIndexOf("."));
    }

    public static Set<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotationClazz) {
        Set<Class<?>> classes = getClasses(packageName);
        Set<Class<?>> res = new HashSet();
        Iterator var4 = classes.iterator();

        while(var4.hasNext()) {
            Class<?> clazz = (Class)var4.next();
            if (clazz.isAnnotationPresent(annotationClazz)) {
                res.add(clazz);
            }
        }

        return res;
    }
}
