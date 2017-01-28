/*
 * Copyright 2017 M. Eric DeFazio.
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
 * Normally Not a fan of "Util" methods, but in this case, we are doing something
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
 * NOTE: if I dio this I need to reload the Javac compiler tool to include the
 * updated classpath)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ClassLoaderUtils
{
    /** 
     * THIS is a reference to the method :
     * {@link java.lang.ClassLoader#defineClass( String, byte[], int, int ) }
     * 
     * which is "protected" and "final" by default.
     * 
     * we have to "hack" the JVM accessibility to make this method available
     * to us at runtime (so we can define Classes via bytecode)
     */
    private static final Method DEFINE_CLASS_METHOD;
    
    private static final Method DEFINE_PACKAGE_METHOD;
    
    static 
    {
        try 
        {
            DEFINE_CLASS_METHOD = 
                ClassLoader.class.getDeclaredMethod(
                    "defineClass", 
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
     * "Promotes" AdHocClassFiles loaded in the {@link AdHocClassLoader} to the 
     * AdHocClassLoader's Parent {@code ClassLoader}.
     * 
     * NOTE: this does some "tricks" to make the defineClass method accessible
     * and within the existing "ProtectionDomain"
     * 
     * this allows us to circumvent having to either:
     * <UL>
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
    public static void promoteAdHocClassesToParentClassLoader( 
        AdHocClassLoader adHocClassLoader )
    {
        AdHocClassFile[] adHocClassFiles = 
            adHocClassLoader.getAllAdHocClassFiles().toArray( 
                new AdHocClassFile[ 0 ] );
        
        Package[] packages = 
            adHocClassLoader.getAllAdHocPackages().toArray( new Package[ 0 ] );
        
        ClassLoader parent = adHocClassLoader.getParent();
        //System.out.println( "Promoting Packages " );
        for( int i = 0; i < packages.length; i++ )
        {
            System.out.println( "Promoting Package "+ packages[ i ] );
            Package p = definePackage( parent, packages[ i ] );
            System.out.println( p );
        }
        for( int i = 0; i < adHocClassFiles.length; i++ )
        {
            defineClass( parent, adHocClassFiles[ i ] );
        }
        //since we loaded the classes in the Parent class loader, 
        //we dont need to adHoc copies anymore, they will still "resolve"
        // through this
        adHocClassLoader.unloadAllAdHocClasses();
    }

    /** 
     * Call the definePackage method on a given classLoader instance
     * (Probably the parent classLoader of the AdHocClassLoader)
     * 
     * @param classLoader
     * @param packageDef
     * @return 
     */
    public static Package definePackage( ClassLoader classLoader, Package packageDef )
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
            
            System.out.println( "SEALED " + adHocPackage.isSealed() );
            return adHocPackage;
            
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
    /**
     * This was "lifted" from OpenHFT
     * Define a class for byte code.
     *
     * @param className expected to load.
     * @param bytes     of the byte code.
     */
    public static void defineClass( String className, byte[] bytes ) 
    {
        defineClass(
            Thread.currentThread().getContextClassLoader(), className, bytes);
    }

    /**
     * Defines an {@link AdHocClassFile} in the {@code targetClassLoader}
     * @param targetClassLoader the class loader to define the class in
     * @param adHocClassFile the AdHocClassFile
     * @return the Class
     */
    public static Class defineClass( 
        ClassLoader targetClassLoader, AdHocClassFile adHocClassFile )
    {
        return defineClass( 
            targetClassLoader, 
            adHocClassFile.getQualifiedName(), 
            adHocClassFile.toByteArray() );
    }
    
    /**
     * This was "lifted" from OpenHFT
     * Define a class for byte code.
     *
     * @param classLoader to load the class into.
     * @param className   expected to load.
     * @param bytes       of the byte code.
     */
    public static Class defineClass(
        ClassLoader classLoader, String className, byte[] bytes) 
    {
        try 
        {
            return (Class)DEFINE_CLASS_METHOD.invoke(
                classLoader, 
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
}
