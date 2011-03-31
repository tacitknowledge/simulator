/* Decompiled through IntelliJad */
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packfields(3) packimports(3) splitstr(64) lnc radix(10) lradix(10)
// Source File Name:   DefaultPackageScanClassResolver.java

package org.apache.camel.impl;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import org.apache.camel.impl.scan.AnnotatedWithAnyPackageScanFilter;
import org.apache.camel.impl.scan.AnnotatedWithPackageScanFilter;
import org.apache.camel.impl.scan.AssignableToPackageScanFilter;
import org.apache.camel.impl.scan.CompositePackageScanFilter;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.PackageScanFilter;
import org.apache.camel.util.IOHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Camel needs to discover at runtime, all classes available in certain packages. Because the
 * Simulator is packaged using One-Jar, the default way Camel uses doesn't work.
 *
 * The structure of the Jar that One-Jar creates is like this:
 *
 * simulator-one-jar.jar
 * ---lib
 * ------dependecy1.jar
 * ------dependency2.jar
 * ------....... 
 * ---main
 * ---... ....
 *
 * All Jar dependencies of the Simulator are put in the lib directory.
 *
 * This class overrides the default Camel discoverer to enable it to look for classes in the
 * structure that One-Jar created.
 *
 * @param test
 * @param classes
 * @param url
 * @param urlPath
 */
public class DefaultPackageScanClassResolver implements PackageScanClassResolver
{

    public DefaultPackageScanClassResolver()
            {
/*  55*/        acceptableSchemes = new String[0];
/*  59*/        try
                {
/*  59*/            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
/*  60*/            if(ccl != null)
                    {
/*  61*/                if(log.isTraceEnabled())
/*  62*/                    log.trace((new StringBuilder()).append("Adding ContextClassLoader from current thread: ").append(ccl).toString());
/*  64*/                classLoaders.add(ccl);
                    }
                }
/*  66*/        catch(Exception e)
                {
/*  68*/            log.warn((new StringBuilder()).append("Cannot add ContextClassLoader from current thread due ").append(e.getMessage()).append(". This exception will be ignored.").toString());
                }
/*  71*/        classLoaders.add(org.apache.camel.impl.DefaultPackageScanClassResolver.class.getClassLoader());
            }

            public void addClassLoader(ClassLoader classLoader)
            {
/*  75*/        classLoaders.add(classLoader);
            }

            public void addFilter(PackageScanFilter filter)
            {
/*  79*/        if(scanFilters == null)
/*  80*/            scanFilters = new LinkedHashSet();
/*  82*/        scanFilters.add(filter);
            }

            public void removeFilter(PackageScanFilter filter)
            {
/*  86*/        if(scanFilters != null)
/*  87*/            scanFilters.remove(filter);
            }

            public void setAcceptableSchemes(String schemes)
            {
/*  92*/        if(schemes != null)
/*  93*/            acceptableSchemes = schemes.split(";");
            }

            public boolean isAcceptableScheme(String urlPath)
            {
/*  98*/        if(urlPath != null)
                {
/*  99*/            String arr$[] = acceptableSchemes;
/*  99*/            int len$ = arr$.length;
/*  99*/            for(int i$ = 0; i$ < len$; i$++)
                    {
/*  99*/                String scheme = arr$[i$];
/* 100*/                if(urlPath.startsWith(scheme))
/* 101*/                    return true;
                    }

                }
/* 105*/        return false;
            }

            public Set getClassLoaders()
            {
/* 110*/        return Collections.unmodifiableSet(new LinkedHashSet(classLoaders));
            }

            public void setClassLoaders(Set classLoaders)
            {
/* 115*/        this.classLoaders.addAll(classLoaders);
            }

            public Set findAnnotated(Class annotation, String packageNames[])
            {
/* 120*/        if(packageNames == null)
/* 121*/            return Collections.EMPTY_SET;
/* 124*/        if(log.isDebugEnabled())
/* 125*/            log.debug((new StringBuilder()).append("Searching for annotations of ").append(annotation.getName()).append(" in packages: ").append(Arrays.asList(packageNames)).toString());
/* 128*/        PackageScanFilter test = getCompositeFilter(new AnnotatedWithPackageScanFilter(annotation, true));
/* 129*/        Set classes = new LinkedHashSet();
/* 130*/        String arr$[] = packageNames;
/* 130*/        int len$ = arr$.length;
/* 130*/        for(int i$ = 0; i$ < len$; i$++)
                {
/* 130*/            String pkg = arr$[i$];
/* 131*/            find(test, pkg, classes);
                }

/* 134*/        if(log.isDebugEnabled())
/* 135*/            log.debug((new StringBuilder()).append("Found: ").append(classes).toString());
/* 138*/        return classes;
            }

            public Set findAnnotated(Set annotations, String packageNames[])
            {
/* 143*/        if(packageNames == null)
/* 144*/            return Collections.EMPTY_SET;
/* 147*/        if(log.isDebugEnabled())
/* 148*/            log.debug((new StringBuilder()).append("Searching for annotations of ").append(annotations).append(" in packages: ").append(Arrays.asList(packageNames)).toString());
/* 151*/        PackageScanFilter test = getCompositeFilter(new AnnotatedWithAnyPackageScanFilter(annotations, true));
/* 152*/        Set classes = new LinkedHashSet();
/* 153*/        String arr$[] = packageNames;
/* 153*/        int len$ = arr$.length;
/* 153*/        for(int i$ = 0; i$ < len$; i$++)
                {
/* 153*/            String pkg = arr$[i$];
/* 154*/            find(test, pkg, classes);
                }

/* 157*/        if(log.isDebugEnabled())
/* 158*/            log.debug((new StringBuilder()).append("Found: ").append(classes).toString());
/* 161*/        return classes;
            }

            public  Set findImplementations(Class parent, String packageNames[])
            {
/* 166*/        if(packageNames == null)
/* 167*/            return Collections.EMPTY_SET;
/* 170*/        if(log.isDebugEnabled())
/* 171*/            log.debug((new StringBuilder()).append("Searching for implementations of ").append(parent.getName()).append(" in packages: ").append(Arrays.asList(packageNames)).toString());
/* 174*/        PackageScanFilter test = getCompositeFilter(new AssignableToPackageScanFilter(parent));
/* 175*/        Set classes = new LinkedHashSet();
/* 176*/        String arr$[] = packageNames;
/* 176*/        int len$ = arr$.length;
/* 176*/        for(int i$ = 0; i$ < len$; i$++)
                {
/* 176*/            String pkg = arr$[i$];
/* 177*/            find(test, pkg, classes);
                }

/* 180*/        if(log.isDebugEnabled())
/* 181*/            log.debug((new StringBuilder()).append("Found: ").append(classes).toString());
/* 184*/        return classes;
            }

            public Set findByFilter(PackageScanFilter filter, String packageNames[])
            {
/* 189*/        if(packageNames == null)
/* 190*/            return Collections.EMPTY_SET;
/* 193*/        Set classes = new LinkedHashSet();
/* 194*/        String arr$[] = packageNames;
/* 194*/        int len$ = arr$.length;
/* 194*/        for(int i$ = 0; i$ < len$; i$++)
                {
/* 194*/            String pkg = arr$[i$];
/* 195*/            find(filter, pkg, classes);
                }

/* 198*/        if(log.isDebugEnabled())
/* 199*/            log.debug((new StringBuilder()).append("Found: ").append(classes).toString());
/* 202*/        return classes;
            }

            protected void find(PackageScanFilter test, String packageName, Set classes)
            {
/* 206*/        packageName = packageName.replace('.', '/');
/* 208*/        Set set = getClassLoaders();
                ClassLoader classLoader;
/* 210*/        for(Iterator i$ = set.iterator(); i$.hasNext(); find(test, packageName, classLoader, classes))
/* 210*/            classLoader = (ClassLoader)i$.next();

            }

            protected void find(PackageScanFilter test, String packageName, ClassLoader loader, Set classes)
            {
//                System.out.println("The class loader is " + loader.getClass().getName());
//                System.out.println("Package name is " + packageName);

                

                Enumeration urls;
/* 216*/        if(log.isTraceEnabled())
/* 217*/            log.trace((new StringBuilder()).append("Searching for: ").append(test).append(" in package: ").append(packageName).append(" using classloader: ").append(loader.getClass().getName()).toString());
/* 223*/        try
                {
/* 223*/            urls = getResources(loader, packageName);
/* 224*/            if(!urls.hasMoreElements())
/* 225*/                log.trace("No URLs returned by classloader");
                }
/* 227*/        catch(IOException ioe)
                {
/* 228*/            log.warn((new StringBuilder()).append("Cannot read package: ").append(packageName).toString(), ioe);
/* 229*/            return;
                }

/* 232*/        while(urls.hasMoreElements())
            {
/* 233*/        URL url = null;
/* 235*/        url = (URL)urls.nextElement();


//                System.out.println("The URL is " + url);
/* 236*/        if(log.isTraceEnabled())
/* 237*/            log.trace((new StringBuilder()).append("URL from classloader: ").append(url).toString());
/* 240*/
                try
                {
                    url = customResourceLocator(url);
//                    System.out.println("The URL is " + url);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
/* 242*/        String urlPath = url.getFile();
//                System.out.println("urlPath = " + urlPath);
/* 243*/
                try
                {
                    urlPath = URLDecoder.decode(urlPath, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
/* 244*/        if(log.isTraceEnabled())
/* 245*/            log.trace((new StringBuilder()).append("Decoded urlPath: ").append(urlPath).append(" with protocol: ").append(url.getProtocol()).toString());
/* 249*/
                boolean containsTwoJarsInPath = urlPath.indexOf("!") != urlPath.lastIndexOf("!");
                if(urlPath.startsWith("file:") && containsTwoJarsInPath)
                {
                    discoverPackageClassesInOneJarPackage(test, classes, url, urlPath);
                }
                else
                {
                    if(urlPath.startsWith("file:"))
/* 261*/                urlPath = urlPath.substring(5);

    /* 266*/        if(url.toString().startsWith("bundle:") || urlPath.startsWith("bundle:"))
                    {
    /* 267*/            log.trace("It's a virtual osgi bundle, skipping");
    /* 268*/            continue;
                    }
    /* 272*/        try
                    {
    /* 272*/            if(urlPath.indexOf('!') > 0)
    /* 273*/                urlPath = urlPath.substring(0, urlPath.indexOf('!'));
    /* 276*/            if(log.isTraceEnabled())
    /* 277*/                log.trace((new StringBuilder()).append("Scanning for classes in [").append(urlPath).append("] matching criteria: ").append(test).toString());
    /* 280*/            File file = new File(urlPath);
    /* 281*/            if(file.isDirectory())
                        {
    /* 282*/                if(log.isTraceEnabled())
    /* 283*/                    log.trace((new StringBuilder()).append("Loading from directory using file: ").append(file).toString());
    /* 285*/                loadImplementationsInDirectory(test, packageName, file, classes);
                        } else
                        {
                            InputStream stream;
    /* 288*/                if(urlPath.startsWith("http:") || urlPath.startsWith("https:") || urlPath.startsWith("sonicfs:") || isAcceptableScheme(urlPath))
                            {
    /* 293*/                    if(log.isTraceEnabled())
    /* 294*/                        log.trace((new StringBuilder()).append("Loading from jar using url: ").append(urlPath).toString());
    /* 296*/                    URL urlStream = new URL(urlPath);
    /* 297*/                    URLConnection con = urlStream.openConnection();
    /* 299*/                    con.setUseCaches(false);
    /* 300*/                    stream = con.getInputStream();
                            } else
                            {
    /* 302*/                    if(log.isTraceEnabled())
    /* 303*/                        log.trace((new StringBuilder()).append("Loading from jar using file: ").append(file).toString());
    /* 305*/                    stream = new FileInputStream(file);
                            }
    /* 308*/                loadImplementationsInJar(test, packageName, stream, urlPath, classes);
                        }
                    }
    /* 310*/        catch(IOException e)
                    {
    /* 312*/            log.debug((new StringBuilder()).append("Cannot read entries in url: ").append(url).toString(), e);
                    }
    /* 314*/      }
                }

            }


    /**
     * Camel needs to discover at runtime, all classes available in certain packages. Because
     * the Simulator is packaged using One-Jar, the default way Camel uses doesn't work.
     *
     * The structure of the Jar that One-Jar creates is like this:
     *
     * simulator-one-jar.jar
     * ---lib
     * ---main
     * ---...
     * ....
     *
     * All Jar dependencies of the Simulator are put in the lib directory.
     *
     * The Url param that comes in here looks like the following:
     *
     *  jar:file:simulator-core.one-jar.jar!/lib/activemq-camel-5.3.0.jar!/org/apache/activemq/camel/converter/
     *
     * which tells us the name of the simulator jar as well as the name of the dependency jar.
     *
     * This method uses this path to provide Camel all the classes that it needs, by looking
     * in activemq-camel-5.3.0.jar which is inside the global Simulator jar.
     *
     * @param test
     * @param classes
     * @param url
     * @param urlPath
     */
    private void discoverPackageClassesInOneJarPackage(PackageScanFilter test, Set classes, URL url,
                                                       String urlPath)
    {
        System.out.println("Using One-Jar way to discover classes for " + urlPath);

        String topLevelJarPath = urlPath.substring(5, urlPath.indexOf("!"));

        String dependencyJarPath =
            urlPath.substring(urlPath.indexOf("lib/") + 4, urlPath.lastIndexOf(".jar") + 4);

        String packagePath = urlPath.substring(urlPath.lastIndexOf("!/") + 2);

        //System.out.println("libJarPath = " + libJarPath);
        JarInputStream jarInput = null;

        try
        {
            jarInput = new JarInputStream(new FileInputStream(topLevelJarPath));
            JarFile topLevelJar = new JarFile(topLevelJarPath);

            JarEntry topLevelJarEntry = null;
            while ((topLevelJarEntry = jarInput.getNextJarEntry()) != null)
            {
                if (topLevelJarEntry.getName().equals("lib/" + dependencyJarPath))
                {
                    discoverInInternalJar(test, classes, packagePath, topLevelJar,
                        topLevelJarEntry);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jarInput.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void discoverInInternalJar(PackageScanFilter test, Set classes, String packagePath,
                                       JarFile topLevelJar, JarEntry topLevelJarEntry)
    {
        JarInputStream libJarInputStream = null;
        try
        {
            libJarInputStream =
                new JarInputStream(topLevelJar.getInputStream(topLevelJarEntry));
            JarEntry childJarEntry = null;

            while ((childJarEntry = libJarInputStream.getNextJarEntry()) != null)
            {
                String childJarEntryName = childJarEntry.getName();

                if (childJarEntryName.startsWith(packagePath) && childJarEntryName
                    .endsWith(".class"))
                {
                    addIfMatching(test, childJarEntryName, classes);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(libJarInputStream != null)
            {
                try
                {
                    libJarInputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected URL customResourceLocator(URL url)
                throws IOException
            {
/* 320*/        return url;
            }

            protected Enumeration getResources(ClassLoader loader, String packageName)
                throws IOException
            {
/* 335*/        if(log.isTraceEnabled())
/* 336*/            log.trace((new StringBuilder()).append("Getting resource URL for package: ").append(packageName).append(" with classloader: ").append(loader).toString());
/* 341*/        if(!packageName.endsWith("/"))
/* 342*/            packageName = (new StringBuilder()).append(packageName).append("/").toString();
/* 344*/        return loader.getResources(packageName);
            }

            private PackageScanFilter getCompositeFilter(PackageScanFilter filter)
            {
/* 348*/        if(scanFilters != null)
                {
/* 349*/            CompositePackageScanFilter composite = new CompositePackageScanFilter(scanFilters);
/* 350*/            composite.addFilter(filter);
/* 351*/            return composite;
                } else
                {
/* 353*/            return filter;
                }
            }

            private void loadImplementationsInDirectory(PackageScanFilter test, String parent, File location, Set classes)
            {
/* 371*/        File files[] = location.listFiles();
/* 372*/        StringBuilder builder = null;
/* 374*/        File arr$[] = files;
/* 374*/        int len$ = arr$.length;
/* 374*/        for(int i$ = 0; i$ < len$; i$++)
                {
/* 374*/            File file = arr$[i$];
/* 375*/            builder = new StringBuilder(100);
/* 376*/            String name = file.getName();
/* 377*/            if(name == null)
/* 378*/                continue;
/* 378*/            name = name.trim();
/* 379*/            builder.append(parent).append("/").append(name);
/* 380*/            String packageOrClass = parent != null ? builder.toString() : name;
/* 382*/            if(file.isDirectory())
                    {
/* 383*/                loadImplementationsInDirectory(test, packageOrClass, file, classes);
/* 383*/                continue;
                    }
/* 384*/            if(name.endsWith(".class"))
/* 385*/                addIfMatching(test, packageOrClass, classes);
                }

            }

            private void loadImplementationsInJar(PackageScanFilter test, String parent, InputStream stream, String urlPath, Set classes)
            {
/* 403*/        JarInputStream jarStream = null;
/* 405*/        try
                {
/* 405*/            jarStream = new JarInputStream(stream);
/* 408*/            do
                    {
                        JarEntry entry;
/* 408*/                if((entry = jarStream.getNextJarEntry()) == null)
/* 409*/                    break;
/* 409*/                String name = entry.getName();
/* 410*/                if(name != null)
                        {
/* 411*/                    name = name.trim();
/* 412*/                    if(!entry.isDirectory() && name.startsWith(parent) && name.endsWith(".class"))
/* 413*/                        addIfMatching(test, name, classes);
                        }
                    } while(true);
                }
/* 417*/        catch(IOException ioe)
                {
/* 418*/            log.warn((new StringBuilder()).append("Cannot search jar file '").append(urlPath).append("' for classes matching criteria: ").append(test).append(" due to an IOException: ").append(ioe.getMessage()).toString(), ioe);
                }
/* 421*/        finally
                {
/* 421*/            IOHelper.close(jarStream, urlPath, log);
                }
            }

            protected void addIfMatching(PackageScanFilter test, String fqn, Set classes)
            {
/* 435*/        try
                {
/* 435*/            String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
/* 436*/            Set set = getClassLoaders();
/* 437*/            boolean found = false;
/* 438*/            Iterator i$ = set.iterator();
/* 438*/            do
                    {
/* 438*/                if(!i$.hasNext())
/* 438*/                    break;
/* 438*/                ClassLoader classLoader = (ClassLoader)i$.next();
/* 439*/                if(log.isTraceEnabled())
/* 440*/                    log.trace((new StringBuilder()).append("Testing for class ").append(externalName).append(" matches criteria [").append(test).append("] using classloader:").append(classLoader).toString());
/* 443*/                try
                        {
/* 443*/                    Class type = classLoader.loadClass(externalName);
/* 444*/                    if(log.isTraceEnabled())
/* 445*/                        log.trace((new StringBuilder()).append("Loaded the class: ").append(type).append(" in classloader: ").append(classLoader).toString());
/* 447*/                    if(test.matches(type))
                            {
/* 448*/                        if(log.isTraceEnabled())
/* 449*/                            log.trace((new StringBuilder()).append("Found class: ").append(type).append(" which matches the filter in classloader: ").append(classLoader).toString());
/* 451*/                        classes.add(type);
                            }
/* 453*/                    found = true;
/* 454*/                    break;
                        }
/* 455*/                catch(ClassNotFoundException e)
                        {
/* 456*/                    if(log.isTraceEnabled())
/* 457*/                        log.trace((new StringBuilder()).append("Cannot find class '").append(fqn).append("' in classloader: ").append(classLoader).append(". Reason: ").append(e).toString(), e);
                        }
/* 460*/                catch(NoClassDefFoundError e)
                        {
/* 461*/                    if(log.isTraceEnabled())
/* 462*/                        log.trace((new StringBuilder()).append("Cannot find the class definition '").append(fqn).append("' in classloader: ").append(classLoader).append(". Reason: ").append(e).toString(), e);
                        }
                    } while(true);
/* 467*/            if(!found && log.isDebugEnabled())
/* 470*/                log.debug((new StringBuilder()).append("Cannot find class '").append(fqn).append("' in any classloaders: ").append(set).toString());
                }
/* 473*/        catch(Exception e)
                {
/* 474*/            if(log.isWarnEnabled())
/* 475*/                log.warn((new StringBuilder()).append("Cannot examine class '").append(fqn).append("' due to a ").append(e.getClass().getName()).append(" with message: ").append(e.getMessage()).toString(), e);
                }
            }

            protected final transient Log log = LogFactory.getLog(getClass());
            private final Set classLoaders = new LinkedHashSet();
            private Set scanFilters;
            private String acceptableSchemes[];
}
