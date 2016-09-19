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
package varcode.java.code;

import junit.framework.TestCase;
import varcode.java.code._annotate._annotation;
import varcode.java.code._annotate._attributes;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _annotationTest
    extends TestCase
{
    public void testAttributesEmpty()
    {
        _attributes at = _attributes.of();
        assertEquals( 0, at.count() );
        assertTrue( at.isEmpty() );       
        
        assertEquals( "", at.toString() );
    }
    
    public void testAttributesSingleValue()
    {
        _attributes at = _attributes.of( "value" );
        assertEquals( 1, at.count() );
        assertFalse( at.isEmpty() );        
        assertEquals( "\"value\"", at.toString() );
    }
    
    public void testAttributesOne()
    {
        _attributes at = _attributes.of( "name", "value" );
        assertEquals( 1, at.count() );
        assertFalse( at.isEmpty() );        
        assertEquals( "name = value", at.toString() );
    }
    
    public void testAttributesMany()
    {
        _attributes at = _attributes.of( "name", "\"value\"", "a", "1");
        assertEquals( 2, at.count() );
        assertFalse( at.isEmpty() );        
        assertEquals( "name = \"value\", a = 1", at.toString() );
    }
    
    public void testAnnotationAtts()
    {
        //test single attribute
        _annotation ann = _annotation.of( "Hey", "gabba" );
        assertEquals( "@Hey(\"gabba\") ", ann.toString() );
        
        //test name = value attribute
        ann = _annotation.of( "Hey", "gabba", "\"gabba\"" );
        assertEquals( "@Hey(gabba = \"gabba\") ", ann.toString() );
        
        //test multi-attribute
        ann = _annotation.of( "Hey", "gabba", "\"gabba\"", "oderous", "\"urungus\"" );
        assertEquals( "@Hey(gabba = \"gabba\", oderous = \"urungus\") ", ann.toString() );
        
        ann = _annotation.of( "@Hey", "gabba", "\"gabba\"", "oderous", "\"urungus\"" );
        assertEquals( "@Hey(gabba = \"gabba\", oderous = \"urungus\") ", ann.toString() );        
    }
    
    
    public void testEmpty()
    {
        _annotate al = new _annotate();
        assertEquals( "", al.author( ) );                
        assertEquals( 0, al.count() );
        assertTrue( al.isEmpty() );
        assertEquals( 0, al.getAnnotations().size() );
    }
    
    public void testOne()
    {
        _annotate al = new _annotate();
        al.add( "@Path(\"book\")" );
        assertEquals("@Path(\"book\")" + "\r\n", al.author( ) );        
        
        assertEquals( 1, al.count() );
        assertFalse( al.isEmpty() );
        assertEquals( 1, al.getAnnotations().size() );
    }
    
    public void testMany()
    {
        _annotate al = new _annotate();
        al.add( "@Path(\"/{id}\")", "@GET");
        assertEquals(
            "@Path(\"/{id}\")" + "\r\n" +
            "@GET" + "\r\n", al.author( ) );     
        
        assertEquals( 2, al.count() );
        assertFalse( al.isEmpty() );
        assertEquals( 2, al.getAnnotations().size() );
    }
}
