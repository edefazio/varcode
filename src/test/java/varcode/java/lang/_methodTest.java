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

import varcode.java.lang._methods;
import varcode.java.lang._annotations;
import varcode.java.lang._javadoc;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Eric
 */
public class _methodTest 
    extends TestCase
{
        public void testDeclare_methodStrings()
    {
        _methods._method _m = _methods._method.of( 
            "/*comment*/", 
            "@Deprecated", 
            "public abstract int countIt()" ); 
        
        assertTrue( _m.getModifiers().containsAll( "public", "abstract" ) );
        
        assertEquals( "countIt", _m.getName() );
        assertEquals( "int", _m.getReturnType() );
        
        assertTrue( 
            _m.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _m.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );
        
    }
    
    public void testDeclare_method()
    {
        _methods._method _m = _methods._method.of( 
            _javadoc.of( "comment" ), 
            _annotations._annotation.of( "@Deprecated" ), 
            "public abstract int countIt()"  ); 
        
        assertEquals( "countIt", _m.getName() );
        assertEquals( "int", _m.getReturnType() );
        
        assertTrue( 
            _m.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _m.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );
        
    }
    
    public void testDeclare_methodWithBody()
    {
        _methods._method _m = _methods._method.of( 
            "/*comment*/", 
            "@Deprecated", 
            "public static final int countIt()",
            "return 100;" ); 
        
        assertTrue( _m.getModifiers().containsAll( "public", "static", "final" ) );
        
        assertEquals( "countIt", _m.getName() );
        assertEquals( "int", _m.getReturnType() );

        
        System.out.println( "\"" + _m.getBody().author() + "\"" );
        assertEquals( "return 100;", _m.getBody().author() );  
        
        /*
        assertEquals( "{" + System.lineSeparator()+
                      "    return 100;" + System.lineSeparator() +
                      "}" , _m.getBody().author() );        
        */
        assertTrue( 
            _m.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _m.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );

    }
    
    public void testDeclare_methodWithMultiLineBody()
    {
        _methods._method _m = _methods._method.of( 
            "/*comment*/", 
            "@Deprecated", 
            "public static final String getUUID()",
            "String s = UUID.randomUUID().toString();",
            "return prefix + s;" ); 
        
        assertTrue( _m.getModifiers().containsAll( "public", "static", "final" ) );
        
        assertEquals( "getUUID", _m.getName() );
        assertEquals( "String", _m.getReturnType() );
        
        assertEquals( "String s = UUID.randomUUID().toString();" + System.lineSeparator() +
                      "return prefix + s;", _m.getBody().toString() );        
        /*
        assertEquals( "{" + System.lineSeparator()+
                      "    String s = UUID.randomUUID().toString();" + System.lineSeparator() +
                      "    return prefix + s;" + System.lineSeparator() +
                      "}" , _m.getBody().toString() );        
        */
        assertTrue( 
            _m.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _m.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );

    }    
}
