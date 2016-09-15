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
package varcode.java.code.auto;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Workspace;
import varcode.java.code._enum;
import varcode.java.code._literal;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _auto_enumTest
    extends TestCase
{
    
    /**
     * This will create
     */
    public void testIncrementallyAdd()
    {
        _auto_enum auto = _auto_enum.of( "ex.varcode.e.MyEnum" )
            .property( int.class, "age")
            .value( "Eric", 42 );
        
        Class enumClass = auto.toJavaCase( ).loadClass();
        assertEquals(
            42,  Java.invoke( enumClass.getEnumConstants()[ 0 ], "getAge" ) );
        
        auto.value( "Blah", 22 );
        
        //create the class again with the new value
        enumClass = auto.toJavaCase( ).loadClass();
        
        assertEquals(
            22,  Java.invoke( enumClass.getEnumConstants()[ 1 ], "getAge" ) );
        
    }
    

    
    public void testEnumClone()
    {
        _auto_enum ae = _auto_enum.of( "MyEnum" );
        
        _enum e = ae.getEnum();
        e.field("public static final int ID = 100;");
        assertEquals( 1, e.getFields().count() );
        
        //updating the clone DOES NOT change the original _autoEnum
        assertEquals( 0, ae.getEnum().getFields().count() );
    }
    
    public void testAutoEnum()
    {
        _auto_enum ae = _auto_enum.of( "MyEnum" );
        
        assertEquals( "MyEnum", ae.getName() );
        assertEquals( "", ae.getPackageName() );
        
        Class enumClass = ae.toJavaCase( ).loadClass();
        
        
        assertEquals( "MyEnum", enumClass.getCanonicalName() );
        assertEquals( 0, enumClass.getEnumConstants().length );
        
        ae = _auto_enum.of( "ex.varcode.e.MyEnum" );
        
        assertEquals( "MyEnum", enumClass.getName() );
        assertEquals( "ex.varcode.e", ae.getPackageName() );
        
        enumClass = ae.toJavaCase( ).loadClass();
        
        assertEquals( "ex.varcode.e.MyEnum", enumClass.getCanonicalName() );
        assertEquals( 0, enumClass.getEnumConstants().length );
        
        //adds a property, 
        ae.property( String.class, "id" );
        ae.value( "BILL", _literal.of( "WILLIAM" ) );
        
        enumClass = ae.toJavaCase( ).loadClass();
        assertEquals( 1, enumClass.getEnumConstants().length );
        assertEquals( 
            "WILLIAM", Java.invoke( enumClass.getEnumConstants()[0], "getId" ) );
        
        ae = _auto_enum.of( "ex.varcode.e.MyEnum" );
        ae.property( "public final int count;" );
        ae.value( "_1", 1 );
        
        enumClass = ae.toJavaCase( ).loadClass();
        assertEquals( 1, enumClass.getEnumConstants().length );
        assertEquals( 
            1, Java.invoke( enumClass.getEnumConstants()[ 0 ], "getCount" ) );
        
    }
}
