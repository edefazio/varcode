/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.adhoc;

import junit.framework.TestCase;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassFile;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.model._class;
import varcode.java.model.auto._autoToString;

/**
 *
 * @author Eric
 */
public class ClassLoader_LoadAdHocClass
    extends TestCase
{
    public void testAdHoc()
    {
        AdHocClassLoader adHocCL = AdHoc.compile(_autoToString.to( _class.of( "A")
            .field("public int count = 1;")
            .field("public String name = \"Eric\";") ) );
        
        AdHocClassFile c = adHocCL.getAdHocClassFileByName( "A" ); 
            
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        
    }
    
}
