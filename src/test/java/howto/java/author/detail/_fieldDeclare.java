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

import junit.framework.TestCase;
import varcode.java.metalang._annotations;
import varcode.java.metalang._annotations._annotation;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._javadoc;

/**
 *
 * @author Eric
 */
public class _fieldDeclare
    extends TestCase
{
    public void test_fieldDeclareStrings()
    {
        _field _f = of( 
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
        _field _f = of( 
            _javadoc.of( "comment" ), 
            _annotation.of( "@Deprecated" ), 
            "public int count = 1;" ); 
        
        assertEquals( "count", _f.getName() );
        assertEquals( "int", _f.getType() );
        assertEquals( " = 1", _f.getInit().toString() );
        
        assertTrue( 
            _f.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue( 
            _f.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );
        
    }
    
    private static class FieldParams
    {
        _javadoc javadoc; // starts with /* ends with */
        _annotations annots = new _annotations(); //starts with @
        String fieldSignature;
    }
    
    public static _field of( Object...components )
    {
        FieldParams cd = new FieldParams();
        for( int i = 0; i < components.length; i++ )
        {
            if( components[ i ] instanceof String )
            {
                fromString( cd, (String)components[ i ] );
            }            
            else if( components[ i ] instanceof _javadoc )
            {
                cd.javadoc = (_javadoc)components[ i ];
            }
            else if( components[ i ] instanceof _annotation )
            {
                cd.annots.add( (_annotation)components[ i ] );
            }
            else if( components[ i ] instanceof _annotations )
            {
                cd.annots = (_annotations)components[ i ];
            }            
        }
        _field _f = _field.of( cd.fieldSignature );
        for( int i = 0; i < cd.annots.count(); i++ )
        {
            _f.annotate( cd.annots.getAt( i ) );
        }
        if( cd.javadoc != null && !cd.javadoc.isEmpty())
        {
            _f.javadoc( cd.javadoc.getComment() );
        }        
        return _f;
    }
    
    private static void fromString( FieldParams cd, String component )
    {
        if( component.startsWith( "/**" ))
        {
            cd.javadoc = _javadoc.of( component.substring(3, component.length() -2 ) );            
        }
        else if( component.startsWith( "/*" ))
        {
            cd.javadoc = _javadoc.of( component.substring(2, component.length() -2 ) );            
        }
        else if( component.startsWith( "@" ) )
        {
            cd.annots.add( _annotation.of( component ) );
        }        
        else
        {
            cd.fieldSignature =  (String)component;             
        }        
    }
}
