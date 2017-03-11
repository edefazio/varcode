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
package varcode.java.model;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Eric
 */
public class _annTest
    extends TestCase
{
    
    public void testAttributesStringArray()
    {
        String[] ar = _ann._attributes.parseStringArray( "\"public static {+type+} {+name+};\"" );
        assertEquals( 1, ar.length );
        assertEquals( "public static {+type+} {+name+};", ar[ 0 ] );
            
        String[] arr = _ann._attributes.parseStringArray( "{\"A\"}" );
        assertEquals( 1, arr.length );
        assertEquals( "A", arr[0] );
        
        arr = _ann._attributes.parseStringArray( "{\"A\", \"B\"}" );
        System.out.println( arr[ 0 ] );
        assertEquals( 2, arr.length );
        assertEquals( "A", arr[0] );
        assertEquals( "B", arr[1] );        
        
        //now read the literal string array from the annotation model
        _fields._field _f = _fields._field.of( "@$({\"a\", \"name\"})", "public int a;" );        
        _ann _as = _f.getAnnotations().getOne( varcode.java.draft.$.class );
        
        String[] keyValues = _ann._attributes.parseStringArray( 
            _as.getAttributes().values.get( 0 ) );
        
        assertTrue( keyValues.length == 2 );
        assertEquals( "a", keyValues[ 0 ]);
        assertEquals( "name", keyValues[ 1 ]);
    }
    
    public void testNoAttrs()
    {
        _ann a = _ann.of("@a");
        assertEquals( "@a", a.author() );        
        assertEquals( "a", a.getName() );
        assertTrue( a.getAttributes().isEmpty() );
        assertEquals( 0, a.getAttributes().count() );        
    }
    
    public void testOneValueOnlyAttr()
    {
        _ann a = _ann.of( "@a(1)" );
        assertEquals( "@a(1)", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "1", a.getAttributes().author() );
        assertFalse( a.getAttributes().isEmpty() );
        assertEquals( 1, a.getAttributes().count() );
    }
    
    public void testOneKeyValueAttr()
    {
        _ann a = _ann.of("@a(key=1)");
        assertEquals( "@a(key = 1)", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "key = 1", a.getAttributes().author() );
        assertFalse( a.getAttributes().isEmpty() );
        assertEquals( 1, a.getAttributes().count() );
        assertEquals( "key", a.getAttributes().keys.get( 0 ) );
        assertEquals( "1", a.getAttributes().values.get( 0 ) );
    }
    
    public void testOneValueArrayAttr()
    {
        _ann a = _ann.of("@a({1,2,3})");
        assertEquals( "@a({ 1, 2, 3 })", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "{ 1, 2, 3 }", a.getAttributes().author() );
        assertFalse( a.getAttributes().isEmpty() );
        assertEquals( 1, a.getAttributes().count() );
        //assertEquals( "key", a.getAttributes().keys.get( 0 ) );
        assertEquals( "{ 1, 2, 3 }", a.getAttributes().values.get( 0 ) );
    }
     
    public void testMultiAttrWithArr()
    {
        _ann a = _ann.of( "@a(a = 1,b = { int.class, String.class })");
        assertEquals( "@a(a = 1, b = { int.class, String.class })", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "a = 1, b = { int.class, String.class }", a.getAttributes().author() );
        assertFalse( a.getAttributes().isEmpty() );
        assertEquals( 2, a.getAttributes().count() );
        
        assertEquals( "a", a.getAttributes().keys.get( 0 ) );
        assertEquals( "b", a.getAttributes().keys.get( 1 ) );
        assertEquals( "1", a.getAttributes().values.get( 0 ) );
        assertEquals( "{ int.class, String.class }", a.getAttributes().values.get( 1 ) );
        
    }
    
    public static final void main( String[] args )
    {
        _ann.of( "@Classes(\"value\")" );
        _ann.of( "@Classes({int.class, String.class})");        
        _ann.of( "@Classes(classes = {int.class, String.class})");        
        _ann.of( "@Classes(a=1)");
        _ann.of( "@ann(a=1,b=2)");
        _ann.of( "@ann(a=1,b=\"big giant monster\")");
        _ann.of( "@ann(a=1,b={1,2,3,4,5})");
        _ann.of( "@ann(a=1,b={int.class, String.class})");
    }
}
