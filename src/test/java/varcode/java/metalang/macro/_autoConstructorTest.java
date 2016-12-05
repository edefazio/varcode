/*
 * Copyright 2016 Eric.
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
package varcode.java.metalang.macro;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.java.metalang._constructors._constructor;
import varcode.java.metalang._enum;
import varcode.java.metalang._parameters._parameter;

/**
 *
 * @author Eric
 */
public class _autoConstructorTest 
    extends TestCase
{
    public void testAutoCtorNoFields()
    {
        _class _c = _class.of("MyBean");
        _c.constructor(_autoConstructor.of( _c ) );
        assertEquals( 1, _c.getConstructors().count() );
        _constructor _ctor = _c.getConstructors().getAt(  0 );
        assertEquals( 0, _ctor.getParameters().count());
        assertEquals( "MyBean", _ctor.getSignature().getClassName() );        
    }
    
    public void testAutoEnumCtorNoFields()
    {
        _enum _e = _enum.of("enum MyEnum");
        _e.constructor( _autoConstructor.of( _e ) );
        assertEquals( 1, _e.getConstructors().count() );
        _constructor _ctor = _e.getConstructors().getAt(  0 );
        assertEquals( 0, _ctor.getParameters().count());
        assertEquals( "MyEnum", _ctor.getSignature().getClassName() );        
    }
    
    public void testAutoCtorNoFinalFields()
    {
        _class _c = _class.of("MyBean")
            .field("public int a;");
        
        _c.constructor(_autoConstructor.of( _c ) );
        assertEquals( 1, _c.getConstructors().count() );
        _constructor _ctor = _c.getConstructors().getAt(  0 );
        assertEquals( 0, _ctor.getParameters().count());
        assertEquals( "MyBean", _ctor.getSignature().getClassName() );        
    }
    
    public void testAutoEnumCtorNoFinalFields()
    {
        _enum _e = _enum.of("enum MyBean")
            .field("public int a;");
        
        _e.constructor( _autoConstructor.of( _e ) );
        assertEquals( 1, _e.getConstructors().count() );
        _constructor _ctor = _e.getConstructors().getAt(  0 );
        assertEquals( 0, _ctor.getParameters().count());
        assertEquals( "MyBean", _ctor.getSignature().getClassName() );        
    }
    
    public void testAutoCtorFinalFieldWithInit()
    {
        _class _c = _class.of("MyBean")
            .field("public final int a = 100;");
        
        //NOTE: the field a is final, BUT it has already been initialized
        // so we 
        _c.constructor(_autoConstructor.of( _c ) );
        assertEquals( 1, _c.getConstructors().count() );
        _constructor _ctor = _c.getConstructors().getAt(  0 );
        assertEquals( 0, _ctor.getParameters().count());
        assertEquals( "MyBean", _ctor.getSignature().getClassName() );        
    }
        
    public void testAutoEnumCtorFinalFieldWithInit()
    {
        _enum _e = _enum.of( "enum MyEnum" )
            .field("public final int a = 100;");
        
        //NOTE: the field a is final, BUT it has already been initialized
        // so we 
        _e.constructor( _autoConstructor.of( _e ) );
        assertEquals( 1, _e.getConstructors().count() );
        _constructor _ctor = _e.getConstructors().getAt(  0 );
        assertEquals( 0, _ctor.getParameters().count());
        assertEquals( "MyEnum", _ctor.getSignature().getClassName() );        
    }
        
    public void testAutoCtorSingleConstructorField()
    {
        _class _c = _class.of( "MyBean" )
            .field( "public int a;") 
            .field( "private final int b = 100;")
            .field("private final int c;");
 
        _c.constructor( _autoConstructor.of( _c ) );
         assertEquals( 1, _c.getConstructors().count() );
        _constructor _ctor = _c.getConstructors().getAt(  0 );
        assertEquals( 1, _ctor.getParameters().count());
        _parameter _p = _ctor.getParameters().getAt( 0 );
        
        assertEquals( "c", _p.getName());
        assertEquals( "int", _p.getType());
        
        assertEquals( "MyBean", _ctor.getSignature().getClassName() );       
        
        _c.instance( 5 ); //create an instance setting final int c == 5
    }
    
    public void testAutoEnumCtorSingleConstructorField()
    {
        _enum _e = _enum.of( "enum MyBean" )
            .field( "public int a;") 
            .field( "private final int b = 100;")
            .field( "private final int c;")
            .value( "VALUE", 100 );
            
 
        _e.constructor( _autoConstructor.of( _e ) );
         assertEquals( 1, _e.getConstructors().count() );
        _constructor _ctor = _e.getConstructors().getAt(  0 );
        assertEquals( 1, _ctor.getParameters().count());
        _parameter _p = _ctor.getParameters().getAt( 0 );
        
        
        assertEquals( "c", _p.getName());
        assertEquals( "int", _p.getType());
        
        assertEquals( "MyBean", _ctor.getSignature().getClassName() );       
        
        Class eclass = _e.loadClass();
        Object[] consts = eclass.getEnumConstants();
        assertEquals( 1, consts.length );
        
        //assertNotNull( _Java.getFieldValue( eclass, "VALUE" ) );
        
        //_c.instance( 5 ); //create an instance setting final int c == 5
    }
}
