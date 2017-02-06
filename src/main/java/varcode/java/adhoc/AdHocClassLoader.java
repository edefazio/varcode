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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileObject;
import varcode.java.ClassNameQualified;
import varcode.java.lang.ClassName;

/**
 * {@link ClassLoader}  built for loading Class files in an AdHoc manner (classes 
 * compiled via Javac at Runtime) that maintains a cache Map of 
 * {@link AdHocClassFile}s by name and delegates to the parent {@link ClassLoader} 
 * when resolving classes by name.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AdHocClassLoader
    extends ClassLoader
{   
    /** Maps the class name to the AdHocJavaClass */
    private final Map<String, AdHocClassFile> classNameToAdHocClass;
    
    /** Packages defined in an AdHoc Manner for housing the AdHocClasses */
    private final Set<Package>adHocPackages = new HashSet<Package>();
    
    /** 
     * IF we are creating a Class in a "new" package 
     * (that does not exist in the parentClassLoader)
     * we will need to create a new Package, this entity will
     * provided properties in the creation of the package
     * 
     * {@link #definePackage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.net.URL) }
     */ 
    private final PackagePropertyDefiner pckgDefine;
    
    
    /**
     * All Ad hoc packages that were created
     * 
     * @return a Set of Packages that were created for housing adHocFiles
     */
    public Set<Package> allAdHocPackages()
    {
        return adHocPackages;
    }
    
    public AdHocClassLoader()
    {
        this( Thread.currentThread().getContextClassLoader() );
    }
    
    public AdHocClassLoader( ClassLoader parent ) 
    {
        this( parent, DefaultPackagePropertyDefiner.INSTANCE );
    }

    public AdHocClassLoader ( ClassLoader parent, PackagePropertyDefiner pkgDefine )
    {
        super( parent );
        classNameToAdHocClass = new HashMap<String, AdHocClassFile>();
        this.pckgDefine = pkgDefine;
    }
    
    /** 
     * Adds the AdHocClassFile to the {@code classNameToAdHocClass} Map
     * @param adHocClassFile 
     */
    public void load( AdHocClassFile adHocClassFile ) 
    {
        classNameToAdHocClass.put( 
            adHocClassFile.getName(), adHocClassFile );
    }

    /** 
     * String className to {@link AdHocClassFile} Mapping for classes loaded 
     * in Memory
     * @return the Map of classname to {@link AdHocClassFile}
     */
    public Map<String, AdHocClassFile>classMap()
    {
    	return classNameToAdHocClass;
    }
    
    /** 
     * Returns a Collection of all the AdHocClasses
     * @return all AddHocClassFiles
     */
    public Collection<AdHocClassFile> allAdHocClassFiles()
    {
        return this.classNameToAdHocClass.values();
    }
    
    /**
     * Loads the class by name (in this classLoader <B>or the parent classLoader</B>) 
     * (Unlike classByName(), throws an UNCHECKED VarException and not a 
     * CheckedException if the class is not found)
     * 
     * @param qualifiedClassName the fully qualified class name to resolve 
     * (i.e. "ex.varcode.MyAuthored")
     * @return the class (Or null if the class is not found in this classLoader 
     * or the parent ClassLoader
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    public Class<?> findClass( String qualifiedClassName ) 
        throws ClassNotFoundException 
    {
        //Why am I checking this first??? -- BECAUSE overhead of loading
        Class loadedClass = this.findLoadedClass( qualifiedClassName );
        if( loadedClass != null )
        {
            return loadedClass;
        }
    	AdHocClassFile adHocClass = classNameToAdHocClass.get( qualifiedClassName );
    	if( adHocClass == null ) 
    	{
            return super.findClass( qualifiedClassName );
    	}    	
    	
        //** OK FINAL STEP BEFORE WE DEFINE THE CLASS,        
        // we need to ensure we create a Package to "house" the class
        //it might already exist
        String[] pkgClassName = ClassName.extractPackageAndClassName(qualifiedClassName );
        
        Package pkg = this.getPackage( pkgClassName[ 0 ] );
        if( pkg == null )
        {   //define the package BEFORE declaring the class
            
            //System.out.println( "Creating a Package \"" + pkgClassName[0] 
            //    + "\" in AdHocClassLoader" );
            this.definePackage( 
                pkgClassName[ 0 ], //package name
                this.pckgDefine.getSpecTitle(), 
                this.pckgDefine.getSpecVersion(),
                this.pckgDefine.getSpecVendor(),
                this.pckgDefine.getImplTitle(),
                this.pckgDefine.getImplVersion(),
                this.pckgDefine.getImplVendor(),
                this.pckgDefine.getSealBase() );
        }
            
        //NOW define the class (AFTER we made sure the package has been defined)    
        byte[] byteCode = adHocClass.toByteArray();
    	return defineClass(qualifiedClassName, byteCode, 0, byteCode.length );    	
    }
    
    
    /**
     * Finds and returns an {@link AdHocClassFile} 
     * 
     * @param model any ClassNameQualified:
     * <UL>
     *  <LI>{@link varcode.java.model._class} 
     *  <LI>{@link varcode.java.model._enum} 
     *  <LI>{@link varcode.java.model._interface}
     *  <LI>{@link varcode.java.model._annotationType} 
     *  <LI>{@link AdHocJavaFile}
     * </UL>
     * @return the AdHocClassFile containing the compiled class bytecode
     */
    public AdHocClassFile findClassFile( ClassNameQualified model )
    {
        return findClassFile( model.getQualifiedName() );
    }
    
    /**
     * Returns the AdHocClassFile (containing the bytecodes for the class)
     * by its fully qualified name.  
     * 
     * NOTE: this ONLY finds and returns Classes that were loaded
     * in the AdHocClassLoader, NOT the parent ClassLoader
     * 
     * @param qualifiedClassName the qualified name of the class
     * @return the AdHocClassFile
     * @throws AdHocException if there is a problem finding/loading the class
     */
    public AdHocClassFile findClassFile( String qualifiedClassName )
    {
        AdHocClassFile adHocClassFile = 
            classNameToAdHocClass.get( qualifiedClassName );
        if( adHocClassFile == null )
        {
            throw new AdHocException(
                "Can't to find AdHocClassFile for \"" + qualifiedClassName + "\"" );
        }
        return adHocClassFile;
    }
    
    /**
     * Finds the first class loaded within THIS classLoader that has this
     * simple name (i.e. no .'s )
 DO:
 Class c = findClassBySimpleName( "Map" ); 
 
 DON'T: 
 Class c = findClassBySimpleName( "java.util.Map" );
 
 NOTE, this method Does NOT check the parent classLoader for this class
 NOR does it differentiate between two classes that have the same SimpleName 
 but are in different packages, so USE AT YOUR OWN RISK.
     * 
     * @param simpleName the simple name of the class
     * @return the Class with this name or null if it is not found
     * @throws AdHocException if there is no class found with this simple name
     */
    public Class<?> findClassBySimpleName( String simpleName )
        throws AdHocException
    {
        String[] classNames = 
            this.classNameToAdHocClass.keySet().toArray( new String[ 0 ] );
        
        for( int i = 0; i < classNames.length; i++ )
        {
            String cn = classNames[ i ];
            int start = cn.lastIndexOf( '.' );
            if( start < 0 )
            {
                start = 0;
            }
            else
            {
                start ++;
            }
            cn = cn.substring( start );
            if( cn.equals( simpleName ) )
            {
                try
                {
                    return findClass( classNames[ i ] );
                }
                catch( ClassNotFoundException cnfe )
                {
                    throw new AdHocException(
                        "Unable to find class by simple name " + simpleName , cnfe );
                }
            }
        }
        throw new AdHocException(
            "Unable to find class by simple name " + simpleName );
    }
        
    /**
     * Allows you to return the Class associated with an {@link ClassNameQualified}
     * ... examples of {@link ClassNameQualified}s are:
     * <UL>
     *   <LI> _class            adHocClassLoader.findClass( _c );
   <LI> _interface        adHocClassLoader.findClass( _i );
   <LI> _enum             adHocClassLoader.findClass( _e ); 
   <LI> _annotationType   adHocClassLoader.findClass( _a );
   <LI> AdHocJavaFile     adHocClassLoader.findClass( adHocJavaFile );
   <LI> AdHocClassFile    adHocClassLoader.findClass( adHocClassFile );
 </UL>  
     *   
     * @param classNameQualified any entity that is class name qualified
     * @return the Class 
     * @throws AdHocException if a Class for the entity cannot be found in the 
     * classLoader
     */
    public Class<?> findClass( ClassNameQualified classNameQualified )
        throws AdHocException
    {
        return classByName( classNameQualified.getQualifiedName() );
    }
    
    
    /**
     * This DOES NOT recurse
     * @param packageName
     * @return 
     */
    public List<JavaFileObject> adHocClassFilesByPackage( String packageName )
    {
        //System.out.println( "**** GETTING AD HOC FILES BY  *** \""+ packageName+"\"" );
        //if(packageName == null || packageName.length() == 0 )
        //{
        //    return null; //I dunno
        //}
        List<JavaFileObject>classFilesByPackage = new ArrayList<JavaFileObject>();
        String[] fullClassNames = this.classNameToAdHocClass.keySet().toArray( new String[ 0 ] );
        
        for( int i = 0; i < fullClassNames.length; i++ )
        {
            //System.out.println( "/////CHECKING "+ fullClassNames[ i ] );
            if( packageName == null || packageName.length() == 0 )
            {
                if( fullClassNames[ i ].indexOf( "." ) < 0 )
                {
                    classFilesByPackage.add(  
                        this.classNameToAdHocClass.get( fullClassNames[ i ] ) ); 
                }
            }
            else if( fullClassNames[ i ].startsWith( packageName ) 
                &&
                !( fullClassNames[ i ].substring( packageName.length() + 1 ).contains( "." ) ) )
            {
                //System.out.println( " +++ FOUND " +fullClassNames[ i ] );
                classFilesByPackage.add(  
                    this.classNameToAdHocClass.get( fullClassNames[ i ] ) );                
            }
        }
        return classFilesByPackage;        
    }
    
    /**
     * A convenient method use in leu of {@code findClass()} for loading a class
     * by name (which throws a AdHocException (RuntimeException) if the 
     * classLoader cannot resolve a class 
     * (verses a ClassNotFound CheckedException).  
     * 
     * NOTE: searches AdHocClassFiles that match the name first, then
     * check the Parent ClassLoader for the Class.
     * 
     * @param qualifiedClassName the name of the class to resolve
     * @return the Class
     * @throws AdHocException (wrapping a Checked RuntimeException) 
     * if the class cannot be found
     */
    public Class<?> classByName( String qualifiedClassName )
        throws AdHocException
    {
        try 
        {
            return AdHocClassLoader.this.findClass(qualifiedClassName );
        }
        catch( ClassNotFoundException ex ) 
        {
            throw new AdHocException(
                "Could not resolve class \"" + qualifiedClassName + "\"", ex);
        }
    }
        
    /**
     * Remove ALL adHoc Classes from this Class Loader
     * 
     */
    public void unloadAll()
    {
        this.classNameToAdHocClass.clear();
    }    
    
    @Override
    public String toString()
    {
        return "AdHocClassLoader@" + Integer.toHexString( this.hashCode() ) + System.lineSeparator() + 
            "  child of " + this.getParent().toString() + System.lineSeparator() +
            "    " + this.allAdHocClassFiles();
    }
        
    /**
     * Copied from original method on ClassLoader
     * 
     * Defines a package by name in this <tt>ClassLoader</tt>.  This allows
     * class loaders to define the packages for their classes. Packages must
     * be created before the class is defined, and package names must be
     * unique within a class loader and cannot be redefined or changed once
     * created.
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
    @Override
    public Package definePackage(
        String name, String specTitle,String specVersion, String specVendor,
        String implTitle, String implVersion, String implVendor, URL sealBase)
        throws IllegalArgumentException
    {
        Package pkg = super.definePackage( 
            name, specTitle, specVersion, specVendor,
            implTitle, implVersion, implVendor, sealBase );        
        
        this.adHocPackages.add( pkg );
        return pkg;        
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
