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

import varcode.java.macro.Macro;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.context.VarBindException.NullVar;
import varcode.java.model._class;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields;
import varcode.java.model._imports;
import varcode.java.model._methods;
import varcode.java.model._staticBlock;

/**
 *
 * @author Eric
 */
public class MacroCopyTest
    extends TestCase
{
 
    public void testTailorClassSignature()
    {
        Macro.ExpandClassSignature tcs = 
            new Macro.ExpandClassSignature( "public class A" );
        
        _class _c = tcs.initClass( Context.EMPTY );
        
        assertEquals( "A", _c.getName( ) );
        
        tcs = new Macro.ExpandClassSignature( 
            "public class {+className*+} {{+?extends:\n" +
            "    extends {+extends+}+}}" );
        
        try
        {
            tcs.initClass( Context.EMPTY );
            fail("expected exception");
        }
        catch( NullVar e )
        {
            //            System.out.println("Expected" + e );
        }
        
        _c = tcs.initClass( "className", "MyClass" );
        
        assertEquals( _c.getName(), "MyClass" );
        
    }
    public void testTailorPackage()
    {
        _class _c = _class.of("A");
        
        Macro.ExpandPackage tp = new Macro.ExpandPackage( "package my.pkg;" );
        
        tp.expandTo( _c, Context.EMPTY );
        
        assertEquals( "my.pkg", _c.getPackageName() );
    }
    
    public void testTailorImports()
    {
        _class _c = _class.of( "A" );
        
        //transfer nothing (add none remove none)
        Macro.ExpandImports tti = 
            new Macro.ExpandImports( new _imports(), null, null );
        tti.expandTo( _c, Context.EMPTY );
        
        assertEquals( 0, _c.getImports().count() );
        
        //transferImports existing
        tti = 
            new Macro.ExpandImports( _imports.of( UUID.class ), null, null );
        tti.expandTo( _c, Context.EMPTY );
        
        assertEquals( 1, _c.getImports().count() );
        assertTrue( _c.getImports().contains(UUID.class) );
        
        //transferImports Add
        tti = 
            new Macro.ExpandImports( new _imports(), null, new String[]{"java.util.Map"} );
        tti.expandTo( _c, Context.EMPTY );
        
        //verify that we transferred yet ANOTHER
        assertEquals( 2, _c.getImports().count() );
        assertTrue( _c.getImports().contains( Map.class) );
    }
    
    public void testTailorImports2()
    {
        //VERIFY that I process the REMOVE FIRST, then the ADD
        //referesh
        _class _c = _class.of("A");
        
        //remove the UUID import, then ADD the Map import
        Macro.ExpandImports tti = 
            new Macro.ExpandImports( _imports.of( UUID.class ), 
                new String[]{"java.util"}, 
                new String[]{"java.util.Map"} );
        tti.expandTo( _c, Context.EMPTY );
        
        assertEquals( 1, _c.getImports().count() );
        assertTrue( _c.getImports().contains( Map.class ) ); 
        
        _c = _class.of( "A" );
        
        tti = new Macro.ExpandImports( _imports.of( UUID.class ), 
            new String[]{"java.util"}, 
            new String[]{"{+baseClass*+}"} );
        
        try
        {
            tti.expandTo( _c, Context.EMPTY );
            fail( "expected NullVar" );
        }
        catch( VarBindException.NullVar nv )
        {
            //expected
        }
        
        tti.expandTo( _c, "baseClass", AbstractMap.class  );
        
        assertEquals( 1, _c.getImports().count() );
        assertTrue( _c.getImports().contains( AbstractMap.class ) );                 
    }
    
    public void testTailor()
    {
        _class _c = _class.of("A");
        Macro.ExpandField tf = 
            Macro.ExpandField.of("public int a;");
        
        tf.expandTo( _c, Context.EMPTY );
        
        assertNotNull( _c.getField( "a" ) );
        
        _c = _class.of("A");
        tf = Macro.ExpandField.of("public {+type+} {+name+};");
        
        //this should create 0 new fields
        tf.expandTo( _c, Context.EMPTY );
        assertEquals( 0, _c.getFields().count() );
        
        //transfer a single field
        tf.expandTo( _c, "type", int.class, "name", "x" );
        assertNotNull( _c.getField("x" ) );
        
        _c = _class.of("A");
        
        //Tailor/Transfer MUTLIPLE fields
        tf.expandTo( _c, 
            "type", new Object[]{int.class, String.class}, 
            "name", new String[]{"y", "name"} );
        assertEquals( "int", _c.getField("y" ).getType() );
        assertEquals( "java.lang.String", _c.getField("name" ).getType() );        
    }
    
    
    public void testTailorCtor()
    {
        _constructor _ctor = _constructor.of( "public MyClass( String name)",
                "this.name = name;" );
        //signature
        Macro.ExpandConstructor tc = Macro.ExpandConstructor.ofSignature(
            _ctor, 
            "public StaticClass( String myName)" );
        _class _c = _class.of("A");
        tc.expandTo( _c, Context.EMPTY );
        
        assertEquals("StaticClass", _c.getConstructor( 0 ).getName() );
        assertEquals( "MyName", _c.getConstructor( 0 ).getParameters().getAt( 0 ).getName() );
        
        
        _c = _class.of("A");
        //body
        tc = Macro.ExpandConstructor.ofBody(
            _ctor, 
            "this.theName = name;\n" +
            "System.out.println( theName );" );
        
        tc.expandTo( _c, Context.EMPTY );
        
        assertEquals("MyClass", _c.getConstructor( 0 ).getName() );
        assertEquals( "name", _c.getConstructor( 0 ).getParameters().getAt( 0 ).getName() );
        
        _c.getConstructor( 0 ).getBody();
        
    }
    
    /**
     * Verify that copy works (for each element)
     */
    public void testCopy()
    {
        Macro.CopyClassSignature ccs = 
            new Macro.CopyClassSignature( 
                "public class MyClass extends BaseClass implements MyInterface" );
        _class _c = ccs.initClass( Context.EMPTY );
        
        assertEquals( "MyClass", _c.getName() );        
        assertEquals( "BaseClass", _c.getExtends().getAt( 0 ) );
        assertEquals( "MyInterface", _c.getImplements().getAt( 0 ) );
        
        Macro.CopyField cf = new Macro.CopyField(
            _fields._field.of("public static final int a = 100;" ) );
        cf.expandTo( _c, Context.EMPTY );
        _fields._field _f = _c.getField( "a" );
        assertTrue( _f.getModifiers().containsAll( "public", "static", "final" ) );
        assertEquals( "int", _f.getType() );
        assertEquals( "100", _f.getInit().getCode() );
        
        Macro.CopyImports ci = 
            new Macro.CopyImports(_imports.of( UUID.class ));
        
        ci.expandTo( _c, Context.EMPTY );
        assertTrue( _c.getImports().contains( UUID.class ) );
        
        
        Macro.CopyMethod cm = new Macro.CopyMethod(
            _methods._method.of("public static void main(String[] args)",
                "System.out.println( \"Hi\");" ) );        
        cm.expandTo( _c, Context.EMPTY );
        _methods._method _m = _c.getMethod( "main" );        
        assertTrue( _m.getModifiers().containsAll( "public", "static") );
        
        Macro.CopyPackage cp = new Macro.CopyPackage("ex.mypackage");
        cp.expandTo( _c, Context.EMPTY );        
        assertEquals( "ex.mypackage", _c.getPackageName() );
        
        Macro.CopyStaticBlock csb = new Macro.CopyStaticBlock(
            _staticBlock.of( "System.out.println( \"Hi from static block\");" ) );
        
        csb.expandTo( _c, Context.EMPTY );
        assertNotNull( _c.getStaticBlock() );
        
    }
    
}
