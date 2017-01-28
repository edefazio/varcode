/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.load.complex;

import varcode.java.load.Load_class;
import static junit.framework.TestCase.assertEquals;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.load.DirectorySourceLoader;

/**
 *
 * @author Eric
 */
public class Load_JavaSource_CustomLoader
{
    public void test_classCustomSourceLoaderImpl()
    {
        _class _c = Java._classFrom( 
            DirectorySourceLoader.of( 
                System.getProperty( "user.dir" ) + "/src/test/java" ),
            Load_class.class );
        
        assertEquals( _c.getPackageName(), getClass().getPackage().getName() );
    }
}
