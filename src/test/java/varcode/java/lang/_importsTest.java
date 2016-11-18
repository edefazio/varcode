/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.lang;

import varcode.java.lang._imports;
import java.util.Map;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _importsTest
    extends TestCase
{
    
    public void testReplace()
    {
        _imports i = new _imports();
        i.replace("A", "Z");
        assertEquals("", i.toString());
        i.addImport("A");
        assertEquals("import A;", i.toString().trim());
        i.replace("A", "Z");
        assertEquals("import Z;", i.toString().trim());
        
        i.addStaticImport(Map.class);
        assertEquals(
            "import Z;"+ System.lineSeparator()+
            "import static java.util.Map.*;", i.toString().trim() );
        
        i.replace("Map", "HashMap");
        
        assertEquals(
            "import Z;"+ System.lineSeparator()+
            "import static java.util.HashMap.*;", i.toString().trim() );
        
    }
    public void testNone()
    {
        _imports im = _imports.of();
        assertEquals( "", im.toString() );
        
        im.addImport(String.class);
        assertEquals( "", im.toString() );
    }

    public void testByClassAddOrString()
    {
        _imports im = _imports.of();
        im.addImport(Map.class);
        
        assertEquals( "import java.util.Map;", im.toString().trim() );
        
        im.addImport("java.util.HashMap");
        //both imports appear
        assertEquals( "import java.util.HashMap;"+ System.lineSeparator()
                +"import java.util.Map;", im.toString().trim() );        
    }
    
    public void testParameterized()
    {
        _imports im = _imports.of( "{+baseClass+}" );
        assertEquals("import {+baseClass+};", im.toString().trim());
        
        assertEquals("import ex.MyBaseClass;", im.bind( 
            VarContext.of("baseClass", "ex.MyBaseClass") ).author().trim() ); 
        
        im.addImport(Map.class);
        
        assertEquals(
            "import ex.MyBaseClass;"+ System.lineSeparator() +
            "import java.util.Map;",                       
            im.bind( VarContext.of("baseClass", "ex.MyBaseClass") ).author().trim() );         
    }      
}
