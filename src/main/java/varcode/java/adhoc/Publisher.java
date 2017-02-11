/*
 * Copyright 2017 Eric.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.adhoc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Publishes "AdHoc" Classes to more standard {@link ClassLoader}s (i.e. 
 * the SystemClassLoader) for the purpose of using "AdHoc" Classes in the 
 * same manner as classes loaded directly from of the class path at startup.
 * 
 * Normally Not a fan of "Util" classes, but in this case, we are doing something
 * "nonstandard" in order to have some behavior (load custom Class files into 
 * a (parent) ClassLoader that we don't necessarily "own") that is valuable
 * but not intrinsically supported by the JVM.
 * 
 * Therefore, I wanted to call this out separately from the AdHocClassLoader, 
 * etc. because it is completely OPTIONAL... But in many contexts it is the most 
 * holistically intuitive solution (instead of trying to maintain independent 
 * ClassLoaders along with AdHocClassLoaders especially when we consider looking
 * up classes and naming /namespace conflicts.
 * 
 * Most of this code was extracted from the 
 * <A HREF="https://github.com/OpenHFT/Java-Runtime-Compiler/blob/master/compiler/src/main/java/net/openhft/compiler/CompilerUtils.java">OpenHFT</A> 
 * project
 * 
 * Here I'm using the word "Publish" to mean "Make Generally Known"
 * 
 * I might want to "barrow" another method, (set ClassPath) from the above OpenHFT
 * code, because, well it'd be nice to: 
 * <OL>
 *  <LI>create a workspace with many AdHoc Java files
 *  <LI>compile the workspace 
 *  <LI>export the (compiled classes in the AdHocClassLoader) to a location
 *  <LI>update the ClassPath and include the newly minted jar
 *  <LI>run code this way 
 * </OL>
 * NOTE: if I do this I need to reload the Javac compiler tool to include the
 * updated classpath)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Publisher
{
     /** 
     * THIS is a reference to the method :
     * {@link java.lang.ClassLoader#defineClass( String, byte[], int, int ) }
     * 
     * which is "protected" and "final" by default.
     * 
     * we have to "hack" the JVM accessibility to make this method available
     * to us at runtime (so we can define Classes via bytecode into a ClassLoader
     * i.e. the SystemClassLoader that we dont "own")
     */
    private static final Method DEFINE_CLASS_METHOD;
    
        /** 
     * IF we are creating a Class in a "new" package 
     * (that does not exist in the parentClassLoader)
     * we will need to create a new Package, this entity will
     * provided properties in the creation of the package
     * 
     * {@link #definePackage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.net.URL) }
     */ 
    private final PackagePropertyDefiner pckgDefine = 
        DefaultPackagePropertyDefiner.INSTANCE;
    
    /**
     * This is a reference to the method :
     *  {@link java.lang.ClassLoader#definePackage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.net.URL) 
     * 
     * which is protected by default.
     * 
     * we have to "hack" the JVM accessibility to make this method available
     * to us at runtime (so we can define Packages into a ClassLoader
     * that we dont "own")
     */
    private static final Method DEFINE_PACKAGE_METHOD;
    
    static 
    {
        try 
        {
            DEFINE_CLASS_METHOD = 
                ClassLoader.class.getDeclaredMethod( "defineClass", 
                    String.class, //className
                    byte[].class, //class bytecode
                    int.class, //start index into the byte Array
                    int.class //end index into the byte Array
                );
            
            DEFINE_CLASS_METHOD.setAccessible( true );
            
            
            DEFINE_PACKAGE_METHOD = 
                ClassLoader.class.getDeclaredMethod( "definePackage", 
                    String.class, //name 
                    String.class, //specTitle,
                    String.class, // specVersion, 
                    String.class, // specVendor,
                    String.class, // implTitle, 
                    String.class, // implVersion, 
                    String.class, // implVendor, 
                    URL.class ); // sealBase))
            
            DEFINE_PACKAGE_METHOD.setAccessible( true );
        } 
        catch( NoSuchMethodException e ) 
        {
            throw new AssertionError( e );
        }
    }

    /**
     * "Promotes" all {@link JavaClassFile}s loaded in the 
     * {@link AdHocClassLoader} to the parent {@code ClassLoader}.
     *
     * Allows a Class that was defined in an AdHoc Manner to be "visible" to things like Class.forName(...), and Serialization.
     * NOTE: this does some "tricks" to make the publishClass method accessible
     * and within the existing "ProtectionDomain"
     * this allows us to circumvent having to either:
     *  <UL>
     *  <LI> Write classes to a Path already on the classpath then request
     *       the classloader to load the class
     *  <LI> Update the classpath to include the new path (this also requires
     *       a "non-standard" change to the ClassLoader to enable)
     * </UL>
     * 
     * Basically we define a class via some in-memory bytes and "push" them into
     * the parent {@link ClassLoader}
     * 
     * @param adHocClassLoader the AdHocClassLoader that is a Child of an existing
     * (parent) ClassLoader
     */
    public static void publishToParent( AdHocClassLoader adHocClassLoader )
    {
        JavaClassFile[] adHocClassFiles = 
            adHocClassLoader.allAdHocClassFiles().toArray(new JavaClassFile[ 0 ] );
        
        Package[] packages = 
            adHocClassLoader.allAdHocPackages().toArray( new Package[ 0 ] );
        
        ClassLoader parent = adHocClassLoader.getParent();
        //System.out.println( "Promoting Packages " );
        for( int i = 0; i < packages.length; i++ )
        {
            //System.out.println( "Publishing Package "+ packages[ i ] );
            Package p = publishPackage( parent, packages[ i ] );
            //System.out.println( p );
        }
        for( int i = 0; i < adHocClassFiles.length; i++ )
        {
            publishClass( parent, adHocClassFiles[ i ] );
        }
        //since we loaded the classes in the Parent class loader, 
        //we dont need to adHoc copies anymore, they will still "resolve"
        // through this
        adHocClassLoader.unloadAll();
    }

    /** 
     * Call the definePackage method on a given classLoader instance
     * (Probably the parent classLoader of the AdHocClassLoader)
     * 
     * @param classLoader the classLoader to publish the package
     * @param packageDef the definition of the package
     * @return the Package Object that was created
     * @throws AdHocException if an error occurs
     */
    public static Package publishPackage( 
        ClassLoader classLoader, Package packageDef )
        throws AdHocException
    {
        try
        {
            Package adHocPackage = (Package)DEFINE_PACKAGE_METHOD.invoke(
                classLoader, 
                packageDef.getName(),
                packageDef.getSpecificationTitle(),
                packageDef.getSpecificationVersion(),
                packageDef.getSpecificationVendor(),
                packageDef.getImplementationTitle(),
                packageDef.getImplementationVersion(),
                packageDef.getImplementationVendor(),
                null  ); // sealBase))
            
            //System.out.println( "SEALED " + adHocPackage.isSealed() );
            return adHocPackage;
            
        }
        catch( IllegalAccessException e ) 
        {
            throw new AdHocException( "Illegal Access defining package", e );
        } 
        catch( InvocationTargetException e ) 
        {
            //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
            throw new AdHocException( 
                "Invocation Target Exception definining package", e.getCause() );
        }
    }
    /**
     * Publishes a class (defined by a name and a byte[]) to the invoking 
     * Threads' {@link ClassLoader}
     * <BLOCKQUOTE>
     * NOTE: Code was "lifted" from OpenHFT's defineClass method
     * </BLOCKQUOTE>
     * 
     * @param className expected to load.
     * @param bytes     of the byte code.
     */
    public static void publishClass( String className, byte[] bytes ) 
    {
        publishClass(
            ClassLoader.getSystemClassLoader(), className, bytes);
    }

    /**
     * Defines an {@link JavaClassFile} in the {@code targetClassLoader}
     * @param targetClassLoader the class loader to define the class in
     * @param adHocClassFile the AdHocClassFile
     * @return the Class
     */
    public static Class publishClass( 
        ClassLoader targetClassLoader, JavaClassFile adHocClassFile )
    {
        return publishClass( 
            targetClassLoader, 
            adHocClassFile.getQualifiedName(), 
            adHocClassFile.toByteArray() );
    }
    
    /**
     * Publishes a Class (via byte[]) to a {@link ClassLoader}
 
 This was "lifted" from OpenHFT's publishClass() method 
 publishes a class to a specific {@link ClassLoader} (one that we don't
     * own)
     *
     * @param targetClassLoader to load the class into.
     * @param className   expected to load.
     * @param bytes       of the byte code.
     * @return 
     */
    public static Class publishClass(
        ClassLoader targetClassLoader, String className, byte[] bytes ) 
    {
        try 
        {
            return (Class)DEFINE_CLASS_METHOD.invoke( targetClassLoader, 
                className, 
                bytes, 
                0, 
                bytes.length );
        } 
        catch( IllegalAccessException e ) 
        {
            throw new AssertionError( e );
        } 
        catch( InvocationTargetException e ) 
        {
            //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
            throw new AssertionError( e.getCause() );
        }
    }
    
    public void definePackage( String packageName )
    {        
        //System.out.println( "Creating a Package \"" + packageName + "\" " );
        
        this.publishPackage( 
            packageName, //package name
            this.pckgDefine.getSpecTitle(), 
            this.pckgDefine.getSpecVersion(),
            this.pckgDefine.getSpecVendor(),
            this.pckgDefine.getImplTitle(),
            this.pckgDefine.getImplVersion(),
            this.pckgDefine.getImplVendor(),
            this.pckgDefine.getSealBase() );        
    }
    
     /**
     * Copied from original method on ClassLoader
     * 
     * Defines a package by name in this <tt>ClassLoader</tt>.  This allows
 class loaders to defineClass the packages for their classes. Packages must
 be created before the class is defined, and package names must be
 unique within a class loader and cannot be redefined or changed once
 created.
     *
     * @param  name
     *         The package name
     *
     * @param  specTitle
     *         The specification title
     *
     * @param  specVersion
     *         The specification version
     *
     * @param  specVendor
     *         The specification vendor
     *
     * @param  implTitle
     *         The implementation title
     *
     * @param  implVersion
     *         The implementation version
     *
     * @param  implVendor
     *         The implementation vendor
     *
     * @param  sealBase
     *         If not <tt>null</tt>, then this package is sealed with
     *         respect to the given code source {@link java.net.URL
     *         <tt>URL</tt>}  object.  Otherwise, the package is not sealed.
     *
     * @return  The newly defined <tt>Package</tt> object
     *
     * @throws  IllegalArgumentException
     *          If package name duplicates an existing package either in this
     *          class loader or one of its ancestors
     *
     * @since  1.2
     */
    public Package publishPackage(
        String name, String specTitle,String specVersion, String specVendor,
        String implTitle, String implVersion, String implVendor, URL sealBase)
        throws IllegalArgumentException
    {        
        try
        {
            Package pkg = publishPackage( 
                ClassLoader.getSystemClassLoader(),
                name, specTitle, specVersion, specVendor,
                implTitle, implVersion, implVendor, sealBase );        
        
            //System.out.println( "Defining Package " + pkg );            
            return pkg;        
        }
        catch( IllegalAccessException iae )
        {
            throw new AdHocException("illegal Access ", iae ); 
        }
        catch( InvocationTargetException ivt )
        {
            throw new AdHocException( "InvocationTarget Exception", ivt.getCause() );
        }
    }
    
    
    public static Package publishPackage( 
        ClassLoader targetClassLoader, String name, String specTitle, 
        String specVersion, String specVendor, String implTitle, 
        String implVersion, String implVendor, URL sealBase ) 
        throws IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException
    {
         return (Package)DEFINE_PACKAGE_METHOD.invoke( targetClassLoader, 
                name, 
                specTitle, 
                specVersion, 
                specVendor,
                implTitle, 
                implVersion, 
                implVendor, 
                sealBase ); 
    }
    
    /**
     * 
     */
    public interface PackagePropertyDefiner
    {
        public String getSpecTitle();
        
        public String getSpecVersion();
        
        public String getSpecVendor();
        
        public String getImplTitle();
        
        public String getImplVersion();
        
        public String getImplVendor();
        
        public URL getSealBase();
    }
    
    /**
     * This will create "DEFAULT" properties for a package when a Package
     * needs to be created
     * 
     * 
     */
    public enum DefaultPackagePropertyDefiner
        implements PackagePropertyDefiner
    {
        INSTANCE;
        
        @Override
        public String getSpecTitle()
        {
            return "AdHoc";
        }

        @Override
        public String getSpecVersion()
        {
            return "1.0";
        }

        @Override
        public String getSpecVendor()
        {
            return "varcode.java.adhoc";
        }

        @Override
        public String getImplTitle()
        {
            return "Ad Hoc Code";
        }

        @Override
        public String getImplVersion()
        {
            return "1.0";
        }

        @Override
        public String getImplVendor()
        {
            return "io.varcode";
        }

        @Override
        public URL getSealBase()
        {
            return null;
        }        
    }
    
}
