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

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _annotationListTest
    extends TestCase
{
    public void testEmpty()
    {
        _annotations al = new _annotations();
        assertEquals( "", al.author( ) );                
        assertEquals( 0, al.count() );
        assertTrue( al.isEmpty() );
        assertEquals( 0, al.getAnnotations().size() );
    }
    
    public void testOne()
    {
        _annotations al = new _annotations();
        al.add( "@Path(\"book\")" );
        assertEquals("@Path(\"book\")" + "\r\n", al.author( ) );        
        
        assertEquals( 1, al.count() );
        assertFalse( al.isEmpty() );
        assertEquals( 1, al.getAnnotations().size() );
    }
    
    public void testMany()
    {
        _annotations al = new _annotations();
        al.add( "@Path(\"/{id}\")", "@GET");
        assertEquals(
            "@Path(\"/{id}\")" + "\r\n" +
            "@GET" + "\r\n", al.author( ) );     
        
        assertEquals( 2, al.count() );
        assertFalse( al.isEmpty() );
        assertEquals( 2, al.getAnnotations().size() );
    }
}
