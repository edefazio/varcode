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
 *
 * @author Eric
 */
public class AdHocPackagePublisher
{    
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
