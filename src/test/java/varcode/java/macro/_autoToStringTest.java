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
package varcode.java.macro;

import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._fields;

/**
 *
 * @author eric
 */
public class _autoToStringTest
    extends TestCase
{
    
    public void test_portableMethod()
    {
        _class _c = _class.of( "ex.varcode", "public class MyClass",
            _fields._field.of( "public int count;" ) );
        
        _portableMethod _pm = 
            _autoToString.of ( _c.getFields() );
        
        //System.out.println( _pm.getMethod() );
        //System.out.println( _pm.getRequiredImports() );
        
        System.out.println( _c );
        
        _c = _Port.portForce( _pm, _c );
        
        System.out.println( _c );        
    }   
    
    public void testNoFieldsToString()
    {
        _autoDto a = _autoDto.of( "ex.varcode.NoFields" );
        _class toStrung = _autoToString.to( a.as_class() );
        assertEquals( "ex.varcode.NoFields" + System.lineSeparator(), 
            toStrung.instance( ).toString() );
    }
    
    public void testPrimitiveToString()
    {
        _autoDto a = _autoDto.of("ex.varcode.Primitives",
            "int a = 1;",
            "long b = 2;",
            "float f = 3.0f;");
        
        _class toStrung = _autoToString.to( a.as_class() );
        
        assertEquals( 
            "ex.varcode.Primitives" + System.lineSeparator()  +
            "    a = 1" + System.lineSeparator() + 
            "    b = 2" + System.lineSeparator() +
            "    f = 3.0", toStrung.instance().toString().trim() );        
    }
    
    public void test3Fields()
    {
        _autoDto a = _autoDto.of("ex.varcode.MyDto",
            "public int count = 1;",
            "public String name = \"Default\";",
            "public int[] values = new int[]{1,2,3,4}");
        
        _class toStrung = _autoToString.to( a.as_class() );
        
        System.out.println( toStrung );
        
        Object inst = toStrung.instance( );
        System.out.println ( inst );            
    }
    
}
