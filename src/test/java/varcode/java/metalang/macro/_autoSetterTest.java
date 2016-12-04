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
package varcode.java.metalang.macro;

import varcode.java.metalang.macro._autoSetter;
import junit.framework.TestCase;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._methods._method;

/**
 *
 * @author eric
 */
public class _autoSetterTest
    extends TestCase
{
    public static final String N = "\r\n";
    
    public void testField()
    {
        _field f = _field.of("public int count");
        _method m = _autoSetter.of( f );
        assertEquals( "setCount", m.getName() );
        assertTrue( m.getSignature().getModifiers().contains( "public" ) );
        assertEquals( 1, m.getSignature().getParameters().count() );
        assertEquals( "count" , m.getSignature().getParameters().getAt( 0 ).getName() );
        assertEquals( "int" , m.getSignature().getParameters().getAt( 0 ).getType() );
        assertEquals( "void", m.getSignature().getReturnType() );
        
        assertEquals(
        "public void setCount( int count )" +N + 
        "{" + N + 
        "    this.count = count;" + N + 
        "}", m.toString() );
    }
    
    public void testFluent()
    {
        _method m = _autoSetter.Fluent.of( "MyBean", "name", String.class );
        assertEquals( "setName", m.getName() );
        assertTrue( m.getSignature().getModifiers().contains( "public" ) );
        assertEquals( "MyBean", m.getSignature().getReturnType() );
        assertEquals( "name" , m.getSignature().getParameters().getAt( 0 ).getName() );
        assertEquals( "java.lang.String" , m.getSignature().getParameters().getAt( 0 ).getType() );
        assertEquals(1, m.getSignature().getParameters().count() );
        
        assertEquals(
        "public MyBean setName( java.lang.String name )" +N + 
        "{" + N + 
        "    this.name = name;" + N + 
        "    return this;" + N +         
        "}", m.toString() );
        
    }
}
