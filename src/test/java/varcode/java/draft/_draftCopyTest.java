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
package varcode.java.draft;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.context.VarBindException.NullVar;
import varcode.java.draft.draftAction.ExpandConstructor;
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
public class _draftCopyTest
    extends TestCase
{
 
    public void testTailorClassSignature()
    {
        draftClass.ExpandClassSignature tcs = 
            new draftClass.ExpandClassSignature( "public class A" );
        
        _class _c = tcs.draftClass( Context.EMPTY );
        
        assertEquals( "A", _c.getName( ) );
        
        tcs = new draftClass.ExpandClassSignature( 
            "public class {+className*+} {{+?extends:\n" +
            "    extends {+extends+}+}}" );
        
        try
        {
            tcs.draftClass( Context.EMPTY );
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
        
        draftAction.ExpandPackage tp = new draftAction.ExpandPackage( "package my.pkg;" );
        
        tp.draftTo( _c, Context.EMPTY );
        
        assertEquals( "my.pkg", _c.getPackageName() );
    }
    
    public void testTailorImports()
    {
        _class _c = _class.of( "A" );
        
        //transfer nothing (add none remove none)
        draftAction.ExpandImports tti = 
            new draftAction.ExpandImports( new _imports(), null, null );
        tti.draftTo( _c, Context.EMPTY );
        
        assertEquals( 0, _c.getImports().count() );
        
        //transferImports existing
        tti = 
            new draftAction.ExpandImports( _imports.of( UUID.class ), null, null );
        tti.draftTo( _c, Context.EMPTY );
        
        assertEquals( 1, _c.getImports().count() );
        assertTrue( _c.getImports().contains(UUID.class) );
        
        //transferImports Add
        tti = 
            new draftAction.ExpandImports( new _imports(), null, new String[]{"java.util.Map"} );
        tti.draftTo( _c, Context.EMPTY );
        
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
        draftAction.ExpandImports tti = 
            new draftAction.ExpandImports( _imports.of( UUID.class ), 
                new String[]{"java.util"}, 
                new String[]{"java.util.Map"} );
        tti.draftTo( _c, Context.EMPTY );
        
        assertEquals( 1, _c.getImports().count() );
        assertTrue( _c.getImports().contains( Map.class ) ); 
        
        _c = _class.of( "A" );
        
        tti = new draftAction.ExpandImports( _imports.of( UUID.class ), 
            new String[]{"java.util"}, 
            new String[]{"{+baseClass*+}"} );
        
        try
        {
            tti.draftTo( _c, Context.EMPTY );
            fail( "expected NullVar" );
        }
        catch( VarBindException.NullVar nv )
        {
            //expected
        }
        
        tti.draftTo( _c, "baseClass", AbstractMap.class  );
        
        assertEquals( 1, _c.getImports().count() );
        assertTrue( _c.getImports().contains( AbstractMap.class ) );                 
    }
    
    public void testTailor()
    {
        _class _c = _class.of("A");
        draftAction.ExpandField tf = 
            draftAction.ExpandField.of("public int a;");
        
        tf.draftTo( _c, Context.EMPTY );
        
        assertNotNull( _c.getField( "a" ) );
        
        _c = _class.of("A");
        tf = draftAction.ExpandField.of("public {+type+} {+name+};");
        
        //this should create 0 new fields
        tf.draftTo( _c, Context.EMPTY );
        assertEquals( 0, _c.getFields().count() );
        
        //transfer a single field
        tf.draftTo( _c, "type", int.class, "name", "x" );
        assertNotNull( _c.getField("x" ) );
        
        _c = _class.of("A");
        
        //Tailor/Transfer MUTLIPLE fields
        tf.draftTo( _c, 
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
        draftAction.ExpandConstructor tc = draftAction.ExpandConstructor.ofSignature(
            _ctor, 
            "public StaticClass( String myName)" );
        _class _c = _class.of("A");
        tc.draftTo( _c, Context.EMPTY );
        
        assertEquals("StaticClass", _c.getConstructor( 0 ).getName() );
        assertEquals( "myName", _c.getConstructor( 0 ).getParameters().getAt( 0 ).getName() );
        
        
        _c = _class.of("A");
        //body
        tc = ExpandConstructor.ofBody(
            _ctor, 
            new String[]{"this.theName = name;",
            "System.out.println( theName );"} );
        
        tc.draftTo( _c, Context.EMPTY );
        
        assertEquals("MyClass", _c.getConstructor( 0 ).getName() );
        assertEquals( "name", _c.getConstructor( 0 ).getParameters().getAt( 0 ).getName() );
        
        _c.getConstructor( 0 ).getBody();
        
    }
    
    /**
     * Verify that copy works (for each element)
     */
    public void testCopy()
    {
        draftClass.CopyClassSignature ccs = 
            new draftClass.CopyClassSignature( 
                "public class MyClass extends BaseClass implements MyInterface" );
        _class _c = ccs.draftClass( Context.EMPTY );
        
        assertEquals( "MyClass", _c.getName() );        
        assertEquals( "BaseClass", _c.getExtends().getAt( 0 ) );
        assertEquals( "MyInterface", _c.getImplements().getAt( 0 ) );
        
        draftAction.CopyField cf = new draftAction.CopyField(
            _fields._field.of("public static final int a = 100;" ) );
        cf.draftTo( _c, Context.EMPTY );
        _fields._field _f = _c.getField( "a" );
        assertTrue( _f.getModifiers().containsAll( "public", "static", "final" ) );
        assertEquals( "int", _f.getType() );
        assertEquals( "100", _f.getInit().getCode() );
        
        draftAction.CopyImports ci = 
            new draftAction.CopyImports(_imports.of( UUID.class ));
        
        ci.draftTo( _c, Context.EMPTY );
        assertTrue( _c.getImports().contains( UUID.class ) );
        
        
        draftAction.CopyMethod cm = new draftAction.CopyMethod(
            _methods._method.of("public static void main(String[] args)",
                "System.out.println( \"Hi\");" ) );        
        cm.draftTo( _c, Context.EMPTY );
        _methods._method _m = _c.getMethod( "main" );        
        assertTrue( _m.getModifiers().containsAll( "public", "static") );
        
        draftAction.CopyPackage cp = new draftAction.CopyPackage("ex.mypackage");
        cp.draftTo( _c, Context.EMPTY );        
        assertEquals( "ex.mypackage", _c.getPackageName() );
        
        draftAction.CopyStaticBlock csb = new draftAction.CopyStaticBlock(
            _staticBlock.of( "System.out.println( \"Hi from static block\");" ) );
        
        csb.draftTo( _c, Context.EMPTY );
        assertNotNull( _c.getStaticBlock() );
        
    }
    
}
