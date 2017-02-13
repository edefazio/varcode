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
import varcode.java.model._ann;

/**
 *
 * @author Eric
 */
public class _annTest
    extends TestCase
{
    
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
