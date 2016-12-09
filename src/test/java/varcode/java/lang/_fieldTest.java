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
package varcode.java.lang;

import varcode.java.lang._annotations;
import varcode.java.lang._javadoc;
import varcode.java.lang._fields;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Eric
 */
public class _fieldTest 
    extends TestCase
{
    
    public void test_fieldDeclareStrings()
    {
        _fields._field _f = _fields._field.of( 
            "/*comment*/", "@Deprecated", "public int count = 1;" ); 
        
        assertEquals( "count", _f.getName() );
        assertEquals( "int", _f.getType() );
        assertEquals( " = 1", _f.getInit().toString() );
        
        assertTrue( _f.getModifiers().contains( "public" ) );
        assertTrue( 
            _f.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _f.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );
        
    }
    
    public void test_fieldDeclare()
    {
        _fields._field _f = _fields._field.of( 
            _javadoc.of( "comment" ), 
            _annotations._annotation.of( "@Deprecated" ), 
            "public int count = 1;" ); 
        
        assertEquals( "count", _f.getName() );
        assertEquals( "int", _f.getType() );
        assertEquals( " = 1", _f.getInit().toString() );
        
        assertTrue( 
            _f.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _f.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );        
    }    
}
