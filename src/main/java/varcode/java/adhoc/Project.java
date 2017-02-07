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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import varcode.java.Java;
import varcode.java.model._Java;
import varcode.java.model._class;
import varcode.java.model.auto._autoTestSuite;

/**
 *
 * @author Eric
 */
public class Project
{   
    /** name of the project*/
    private String name;
    
    private static final String DATE_FORMAT = "yyyy_MM_dd_hh_mm_ss";
    
    private static final Class JUnitCore;
    static
    {
        try
        {
            JUnitCore = Class.forName( "org.junit.runner.JUnitCore" );
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new AdHocException( "Project requires JUNIT in classpath" );
        }
    }
    
    
    public final Map<String, _Java.FileModel> source = 
        new HashMap<String, _Java.FileModel>();
    
    public final Map<String, _Java.FileModel> test = 
        new HashMap<String, _Java.FileModel>();
    
    public static Project of( String name )
    {
        return new Project( name );
    }
    
    public Project( )
    {
        this( "AdHocProject_" + 
            new SimpleDateFormat( DATE_FORMAT ).format( new Date() ) );
    }
    
    public Project( String name )
    {
        this.name = name;
    }
    
    public Project add( _Java.FileModel... sourceModels )
    {
        for( int i = 0; i < sourceModels.length; i++ )
        {
            source.put( sourceModels[ i ].getQualifiedName(), sourceModels[ i ] );
        }
        return this;
    }

    public Project test( _Java.FileModel... testModels )
    {
        for( int i = 0; i < testModels.length; i++ )
        {
            test.put( testModels[ i ].getQualifiedName(), testModels[ i ] );
        }
        return this;
    }
    
    /** compiles the source only and returns the classes in an AdHocClassLoader */
    public AdHocClassLoader compile()
    {
        return AdHoc.compile( 
            this.source.values().toArray( new _Java.FileModel[ 0 ] ) );
    }
    
    /**
     * Compiles BOTH models and Tests
     * Builds a dynamic TestSuite based on all the Test classes
     * 
     * Runs the TestSuite
     * @return an Array of Classes with the (source classes) that were loaded
     * @throws AdHocException if there 
     */
    public Class[] build()
        throws AdHocException
    {
        List<JavaSourceFile> sourceFiles = new ArrayList<JavaSourceFile>();
        
        _Java.FileModel[] testModels = test.values().toArray( new _Java.FileModel[ 0 ] );
        
        for( int i = 0; i < testModels.length; i++ )
        {
            sourceFiles.add( testModels[ i ].toJavaFile( ) );
        }
        _class _testSuite = _autoTestSuite.from( testModels );
        
        //System.out.println( _testSuite );
        
        sourceFiles.add( _testSuite.toJavaFile( ) );
            
        _Java.FileModel[] srcModels = 
            source.values().toArray( new _Java.FileModel[ 0 ] );
        
        for( int i = 0; i < srcModels.length; i++ )
        {
            sourceFiles.add( srcModels[ i ].toJavaFile( ) );
        }
        
        //compile the source and tests
        AdHocClassLoader loader = AdHoc.compile( sourceFiles );
        
        //now run the TestSuite
        Class c = loader.findClass( _testSuite );
        
        Java.call( JUnitCore, "runClasses", (Object)new Class[]{c} );
        
        //.runClasses( howto.AllSuite.class );
        
        List<Class> classes = new ArrayList<Class>();
        for( int i = 0; i < srcModels.length; i++ )
        {
            classes.add( loader.findClass(  srcModels[i] )  );
        }
        return classes.toArray( new Class[ 0 ] );
    }
    
}
