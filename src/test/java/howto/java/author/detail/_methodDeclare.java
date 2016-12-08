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
package howto.java.author.detail;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import varcode.java.metalang._annotations;
import varcode.java.metalang._annotations._annotation;
import varcode.java.metalang._javadoc;
import varcode.java.metalang._methods._method;

/**
 *
 * @author Eric
 */
public class _methodDeclare
    extends TestCase
{
    public void test_methodStrings()
    {
        _method _m = of( 
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
    
    public void test_methodDeclare()
    {
        _method _m = of( 
            _javadoc.of( "comment" ), 
            _annotation.of( "@Deprecated" ), 
            "public abstract int countIt()"  ); 
        
        assertEquals( "countIt", _m.getName() );
        assertEquals( "int", _m.getReturnType() );
        
        assertTrue( 
            _m.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _m.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );
        
    }
    
    public void testMethodWithBody()
    {
        _method _m = of( 
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
    
    public void testMethodWithMultiLineBody()
    {
        _method _m = of( 
            "/*comment*/", 
            "@Deprecated", 
            "public static final String getUUID()",
            "String s = UUID.randomUUID().toString()",
            "return prefix + s;" ); 
        
        assertTrue( _m.getModifiers().containsAll( "public", "static", "final" ) );
        
        assertEquals( "countIt", _m.getName() );
        assertEquals( "int", _m.getReturnType() );
        
        assertEquals( "{" + System.lineSeparator()+
                      "    return 100;" + System.lineSeparator() +
                      "}" , _m.getBody().toString() );        
        assertTrue( 
            _m.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _m.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );

    }
        
    private static class MethodParams
    {
        _javadoc javadoc; // starts with /* ends with */
        _annotations annots = new _annotations(); //starts with @
        String signature;
        List<Object> body = new ArrayList<Object>(); //anything AFTER signature is populated
    }
    
    private static void addBody( MethodParams mp, Object body )
    {
        if( body.getClass().isArray() )
        {            
            for( int i = 0; i < Array.getLength( body ); i++ )      
            {
                mp.body.add( Array.get( body, i ) );
            }
        }
        else if( body.getClass().isAssignableFrom( List.class ) )
        {
            List lBody = (List) body;
            mp.body.addAll( lBody );
        }
        else
        {
            mp.body.add( body );
        }        
    }
    
    public static _method of( Object...components )
    {
        MethodParams mp = new MethodParams();
        for( int i = 0; i < components.length; i++ )
        {
            if( mp.signature != null )
            {   //ANYTHING i pass in AFTER the signature
                // is assumed to be the BODY of the method
                addBody(mp, components[ i ] );
            }
            else if( components[ i ] instanceof String )
            {
                fromString(mp, (String)components[ i ] );
            }            
            else if( components[ i ] instanceof _javadoc )
            {
                mp.javadoc = (_javadoc)components[ i ];
            }
            else if( components[ i ] instanceof _annotation )
            {
                mp.annots.add( (_annotation)components[ i ] );
            }
            else if( components[ i ] instanceof _annotations )
            {
                mp.annots = (_annotations)components[ i ];
            }            
        }
        _method _f = _method.of( mp.signature );
        for( int i = 0; i < mp.annots.count(); i++ )
        {
            _f.annotate( mp.annots.getAt( i ) );
        }
        if( mp.javadoc != null && !mp.javadoc.isEmpty())
        {
            _f.javadoc(  mp.javadoc.getComment() );
        }        
        if( mp.body != null && !mp.body.isEmpty() )
        {
            _f.body( mp.body );
        }
        return _f;
    }
    
    private static void fromString( MethodParams mp, String component )
    {
        if( component.startsWith( "/**" ))
        {
            mp.javadoc = _javadoc.of( component.substring(3, component.length() -2 ) );            
        }
        else if( component.startsWith( "/*" ))
        {
            mp.javadoc = _javadoc.of( component.substring(2, component.length() -2 ) );            
        }
        else if( component.startsWith( "@" ) )
        {
            mp.annots.add( _annotation.of( component ) );
        }        
        else
        {
            mp.signature =  (String)component;             
        }        
    }
}
