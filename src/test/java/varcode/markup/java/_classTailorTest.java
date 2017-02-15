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
package varcode.markup.java;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.context.Context;
import varcode.java.model._class;
import varcode.java.model._class._signature;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._methods._method;
import varcode.java.model._staticBlock;
import static varcode.markup.java._classTailor.*;

/**
 *
 * @author Eric
 */
public class _classTailorTest
    extends TestCase
{
    /**
     * Verify that copy works (for each element)
     */
    public void testCopy()
    {
        CopyClassSignature ccs = 
            new CopyClassSignature( 
                "public class MyClass extends BaseClass implements MyInterface" );
        _class _c = ccs.initClass( Context.EMPTY );
        
        assertEquals( "MyClass", _c.getName() );        
        assertEquals( "BaseClass", _c.getExtends().getAt( 0 ) );
        assertEquals( "MyInterface", _c.getImplements().getAt( 0 ) );
        
        CopyField cf = new CopyField(
            _field.of("public static final int a = 100;" ) );
        cf.transfer( _c, Context.EMPTY );
        _field _f = _c.getField( "a" );
        assertTrue( _f.getModifiers().containsAll( "public", "static", "final" ) );
        assertEquals( "int", _f.getType() );
        assertEquals( "100", _f.getInit().getCode() );
        
        CopyImports ci = 
            new CopyImports(_imports.of( UUID.class ));
        
        ci.transfer( _c, Context.EMPTY );
        assertTrue( _c.getImports().contains( UUID.class ) );
        
        
        CopyMethod cm = new CopyMethod(
            _method.of("public static void main(String[] args)",
                "System.out.println( \"Hi\");" ) );        
        cm.transfer( _c, Context.EMPTY );
        _method _m = _c.getMethod( "main" );        
        assertTrue( _m.getModifiers().containsAll( "public", "static") );
        
        CopyPackage cp = new CopyPackage("ex.mypackage");
        cp.transfer( _c, Context.EMPTY );        
        assertEquals( "ex.mypackage", _c.getPackageName() );
        
        CopyStaticBlock csb = new CopyStaticBlock(
            _staticBlock.of( "System.out.println( \"Hi from static block\");" ) );
        
        csb.transfer( _c, Context.EMPTY );
        assertNotNull( _c.getStaticBlock() );
        
    }
    
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
}
