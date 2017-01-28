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

import junit.framework.TestCase;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class TestPackageDefine
    extends TestCase
{
    private static void assertClassNotFound( String className )
    {
        try
        {
            Class c = Class.forName( className );
            fail("expected ClassNotFoundException");
        }
        catch( ClassNotFoundException cnfe )
        {
            //it's in the AdHocClassLoader, not visible "globally" yet
        }
    }
    
    /**
     * When creating Classes in new Packages
     * @throws ClassNotFoundException 
     */
    public void testCreatePackage() throws ClassNotFoundException
    {
        //verify that this fiel DOESNT EXIST
        assertClassNotFound( "ex.io.C" );
        
        _class _c = _class.of( "package ex.io", "C" );
        AdHocClassLoader adHoc = AdHoc.compile( _c );
        
        //it DOESNT EXIST globally (it's local to the AdHocClassLoader)
        assertClassNotFound( "ex.io.C" );
        
        AdHocClassPublisher.publishToParent( adHoc );
        
        ClassLoader cl = adHoc.getParent();
        Class c = Class.forName( "ex.io.C" );
        Package createdPackage = c.getPackage();
    }
    
}
