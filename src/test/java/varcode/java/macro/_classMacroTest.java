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
package varcode.java.macro;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.macro.Macro.*;

/**
 *
 * @author Eric
 */
@imports( remove={"varcode.java", "junit"}, add={"java.util.Map", "java.util.UUID", "{+addImports+}"} )
public class _classMacroTest
    extends TestCase
{   
    public void testExpandImports()
    {   //load this class as a Macro, 
        _class _c = _classMacro.of( _classMacroTest.class ).expand( ); 
        
        assertTrue( _c.getImports().contains( "java.util.Map" ) );
        assertTrue( _c.getImports().contains( "java.util.UUID" ) );
        
        //"author" the imports ot a Struing and verify we DONT have any 
        // imports that are
        String authored = _c.getImports().author();
        
        //verify there are no varcode.java imports
        assertTrue( !authored.contains( "varcode.java" ) );
        assertTrue( !authored.contains( "junit" ) );
        
        _c = _classMacro.of( _classMacroTest.class ).expand( "addImports", "java.util.HashSet" ); 
        assertTrue( _c.getImports().contains( "java.util.HashSet" ) );
        
    }
        
    public static class AddImports{}
    
    
    
    @sig("public class {+Name+}")
    public static class ClassSig{ }
    
    public void testClassSig()
    {
        _class _c = _classMacro.of( ClassSig.class ).expand( "Name", "MyClass" );
        assertEquals( "MyClass", _c.getName() ); 
    }
    
    @fields("public static {+type+} {+name+};")
    public static class ClassFields{}
    
    public void testClassFields()
    {
        _class _c = _classMacro.of( ClassFields.class )
            .expand( "type", int.class, "name", "blah" );
        System.out.println( _c );
        assertEquals( "int", _c.getField("blah").getType() );
        
        //create multiple fields
        _c = _classMacro.of( ClassFields.class )
            .expand( "type", new Class[]{int.class, float.class}, 
                     "name", new String[]{"x", "y"} );
        
        assertEquals( "int", _c.getField("x").getType() );
        assertEquals( "float", _c.getField("y").getType() );
    }
    
    @fields({"public int {+name+}Dim;", "private static String id;"})    
    public static class ClassFieldsMultiple
    { }
    
    public void testClassFieldM()
    {
        _class _c = _classMacro.of( ClassFieldsMultiple.class )
            .expand( "name", "TheName" );
        
        assertEquals( "int",_c.getField( "TheNameDim" ).getType() );
        assertEquals( "String",_c.getField( "id" ).getType() );
    }
    

        
    
    public static class AField
    {
        @$({"x", "name"})
        public int x;
    }
    
    public void testSimple()
    {
        _class _q = 
            _classMacro.of(AField.class ).expand( "name", "theName" );
        assertEquals( "AField", _q.getName() );
        assertEquals( "int", _q.getField( "theName" ).getType() );
    }
    public void testAField()
    {
        _classMacro _cm = _classMacro.of(Java._classFrom(AField.class ) );
        _class _c = _cm.expand( "name", "y" );
        assertEquals( "int", _c.getField("y").getType() );
        
        //expand a single field macro to mutliple fields (3) fields (x, y, and z)
        _c = _cm.expand( "name", new String[]{"x", "y", "z"} );
        assertEquals( "int", _c.getField("x").getType() );
        assertEquals( "int", _c.getField("y").getType() );
        assertEquals( "int", _c.getField("z").getType() );        
    }
    /*
    public void testField()
    {
        _classMacro.parseField( _field f );
    }
    */
    /*
    public static void main(String[] args)
    {
        _class source = _class.of( "public class A" );
        _method _m = _method.of("public void doIt()", "System.out.println( \"Hey\");"); 
        CopyMethod fcm = new CopyMethod( _m );
        
        _class _tailored = new _class( _signature.cloneOf(  source.getSignature() ) );
        fcm.transfer( _tailored, null );
        
        
        CopyPackage fp = new CopyPackage( "" );
        fp.transfer(_tailored, null );
        
        source.packageName("ex.mypkg;");
        fp.transfer(  _tailored, null );
        
        
        System.out.println( _tailored );
    }
    */
}
