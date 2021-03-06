package com.tacitknowledge.simulator.camel;

import org.apache.camel.impl.DefaultPackageScanClassResolver;
import org.apache.camel.spi.PackageScanFilter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * Camel needs to discover at runtime, all classes available in certain packages. Because the
 * Simulator is packaged using One-Jar, the default way Camel uses doesn't work.
 * <p/>
 * The structure of the Jar that One-Jar creates is like this:
 * <p/>
 * simulator-one-jar.jar
 * ---lib
 * ------dependecy1.jar
 * ------dependency2.jar
 * ------.......
 * ---main
 * ---... ....
 * <p/>
 * All Jar dependencies of the Simulator are put in the lib directory.
 * <p/>
 * This class overrides the default Camel discoverer to enable it to look for classes in the
 * structure that One-Jar created.
 *
 * @author Adrian Neaga
 */
public class OneJarPackageScanResolver extends DefaultPackageScanClassResolver {

    public static final String FILE_PREFIX = "file:/";

    /**
     * Overridden to handle the URLs returned by One-Jar custom class loader which looks like:
     * <p/>
     * jar:file:simulator-core.one-jar.jar!/lib/activemq-camel-5.3.0.jar!/org/apache/activemq/camel/converter/
     *
     * @inheritDoc
     */
    protected void find(PackageScanFilter test, String packageName, ClassLoader loader, Set<Class<?>> classes) {

        Enumeration<URL> urls;

        try {
            urls = getResources(loader, packageName);

            while (urls.hasMoreElements()) {
                String urlPath = ((URL) urls.nextElement()).getPath();
                try {
                    urlPath = URLDecoder.decode(urlPath, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    log.error(e);
                }

                boolean containsTwoJarsInPath = urlPath.indexOf("!") != urlPath.lastIndexOf("!");

                if (urlPath.startsWith("file:") && containsTwoJarsInPath) {
                    discoverPackageClassesInOneJarPackage(test, classes, urlPath);
                } else {
                    super.find(test, packageName, loader, classes);
                }
            }
        } catch (IOException ioe) {
            log.warn((new StringBuilder()).append("Cannot read package: ").append(packageName)
                    .toString(), ioe);
        }


    }


    /**
     * Camel needs to discover at runtime, all classes available in certain packages. Because the
     * Simulator is packaged using One-Jar, the default way Camel uses doesn't work.
     * <p/>
     * The structure of the Jar that One-Jar creates is like this:
     * <p/>
     * simulator-one-jar.jar ---lib ---main ---... ....
     * <p/>
     * All Jar dependencies of the Simulator are put in the lib directory.
     * <p/>
     * The Url param that comes in here looks like the following:
     * <p/>
     * jar:file:simulator-core.one-jar.jar!/lib/activemq-camel-5.3.0.jar!/org/apache/activemq/camel/converter/
     * <p/>
     * which tells us the name of the simulator jar as well as the name of the dependency jar.
     * <p/>
     * This method uses this path to provide Camel all the classes that it needs, by looking in
     * activemq-camel-5.3.0.jar which is inside the global Simulator jar.
     *
     * @param test
     * @param classes
     * @param url
     * @param urlPath
     */
    private void discoverPackageClassesInOneJarPackage(PackageScanFilter test, Set<Class<?>> classes,
                                                       String urlPath) {
        String topLevelJarPath = extractTopLevelPath(urlPath);

        String dependencyJarPath =
                urlPath.substring(urlPath.indexOf("lib/") + 4, urlPath.lastIndexOf(".jar") + 4);

        String packagePath = urlPath.substring(urlPath.lastIndexOf("!/") + 2);

        JarInputStream jarInput = null;

        try {
            //errors here on top level
            jarInput = new JarInputStream(new FileInputStream(topLevelJarPath));
            JarFile topLevelJar = new JarFile(topLevelJarPath);

            JarEntry topLevelJarEntry = null;
            while ((topLevelJarEntry = jarInput.getNextJarEntry()) != null) {
                if (topLevelJarEntry.getName().equals("lib/" + dependencyJarPath)) {
                    discoverInInternalJar(test, classes, packagePath, topLevelJar,
                            topLevelJarEntry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
        } finally {
            try {
                if (jarInput != null) {
                    jarInput.close();
                }
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    /**
     * Finds the top level Jar (one jar) from the urlPath.  This might be relative or canonical
     *
     * @param urlPath
     * @return
     */
    String extractTopLevelPath(String urlPath) {

        final String urlPathWithoutFile = urlPath.substring(FILE_PREFIX.length(), urlPath.indexOf("!"));
        log.info("Searching main one jar: " + urlPathWithoutFile);

        return urlPathWithoutFile;
    }

    private void discoverInInternalJar(PackageScanFilter test, Set<Class<?>> classes, String packagePath,
                                       JarFile topLevelJar, JarEntry topLevelJarEntry) {
        JarInputStream libJarInputStream = null;
        try {
            libJarInputStream = new JarInputStream(topLevelJar.getInputStream(topLevelJarEntry));
            JarEntry childJarEntry = null;

            while ((childJarEntry = libJarInputStream.getNextJarEntry()) != null) {
                String childJarEntryName = childJarEntry.getName();

                if (childJarEntryName.startsWith(packagePath) && childJarEntryName
                        .endsWith(".class")) {
                    addIfMatching(test, childJarEntryName, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
        } finally {
            if (libJarInputStream != null) {
                try {
                    libJarInputStream.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }
}
