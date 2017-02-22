/*
 * Copyright 2016 eric.
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

import example.ExClass;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;

/**
 *
 * @author eric
 */
public class AdHocClassLoaderTest
    extends TestCase
{
    //what happens if we try to ADHoc load a class that
    // has already been loaded in it's parent ClassLoader??
    public void testLoadClassAlreadyLoaded()
    {
        //we will load ExClass here in the parent classLoader
        ExClass ex = new ExClass();
        ex.name = "Eric";
        
        //we can create a _class in the        
        _class _c = Java._classFrom( ExClass.class );
        
        //lets modify the model of ExClass
        _c.constructor( "public ExClass( String name )", "this.name = name;" );
        
        //this will load ExClass in an AdHoc Class Loader
        Object instance = _c.instance( "Eric" );
        System.out.println ( instance.getClass().getClassLoader() );
        String name = (String)Java.get( instance, "name" );
        assertEquals( "Eric", name );
               
        assertEquals( ex.getClass().getName(), instance.getClass().getName() );
        
        //the canonical name IS the same
        assertEquals( ex.getClass().getCanonicalName(), instance.getClass().getCanonicalName() );
        
        //the package NAMES ARE equal, the "actual" packages are different
        assertEquals( ex.getClass().getPackage().getName(), 
            instance.getClass().getPackage().getName() );
        
        //classLoaders are NOT the same
        assertNotSame( 
            ex.getClass().getClassLoader(), instance.getClass().getClassLoader() );
        
        //they are not Equal
        assertTrue( !ex.equals( instance ) );
        
        //NOTE: keep in mind, you MAY have and USE (2) definitions of classes 
        //that have the same canonical name (one in an AdHocClassLoader and one in its'
        // parent classLoader...  This is FINE, but you just can't try to 
        // "Publish" the AdHoc Class to the parent ClassLoader
        try
        {
            Publisher.publishToParent( (AdHocClassLoader) instance.getClass().getClassLoader() );
            fail("expected failure trying to redefine an existing class that has already been loaded");
        }
        catch( AdHocException e )
        {
            System.out.println( e );
            //OK, HERE is where we have problems, since the Parent ClassLoader
            //Already has a Class and Package declared... we EXPECT this to fail
        }
        
    }
    
    public void testEmpty()
    {
        AdHocClassLoader ahcl = new AdHocClassLoader();
        try
        {
            Class c = ahcl.findClass( "NotFound" );
            fail( "expected exception for classNotFound " );
        }
        catch( ClassNotFoundException ve )
        {
            //expected
        }
        try 
        {
            ahcl.loadClass( "NotFound" );
            fail( "expected CHECKED exception for classNotFound " );
        }
        catch( ClassNotFoundException ex ) 
        {
            //expected
        }
        //verify we don't have anything loaded in the AdHocClassMap    
        assertEquals( 0, ahcl.classMap().size() );
        
        ClassLoader parent = ahcl.getParent();
        if( parent instanceof URLClassLoader )
        {
            //these are the urls where
            URLClassLoader urlCl = (URLClassLoader)parent;
            URL[] urls = urlCl.getURLs();
            for(int i=0; i< urls.length; i++)
            {
                System.out.println( urls[ i ] );
            }
        }
    }
    
    public void testGetResource()
    {
        AdHocClassLoader ahcl = new AdHocClassLoader();
        URL url = ahcl.getResource( "NotFound" );
        assertEquals( null, url );
        
        InputStream is = ahcl.getResourceAsStream( "NotFound" );
        assertEquals( null, is );
        
    }
}
