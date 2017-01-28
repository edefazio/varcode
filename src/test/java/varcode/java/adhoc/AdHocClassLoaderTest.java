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

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import junit.framework.TestCase;
import varcode.VarException;

/**
 *
 * @author eric
 */
public class AdHocClassLoaderTest
    extends TestCase
{
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
