/*
 * Copyright 2016 M. Eric DeFazio.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import varcode.java.lang.JavaMetaLang._facet;
import varcode.java.lang._annotations;
import varcode.java.lang._annotations._annotation;
import varcode.java.lang._class;
import varcode.java.lang._class._signature;
import varcode.java.lang._fields._field;
import varcode.java.lang._imports;
import varcode.java.lang._javadoc;
import varcode.java.lang._methods._method;
import varcode.java.lang._package;

/**
 *
 * @author Eric
 */
public class _classDeclare 
    extends TestCase
{
    public void testStringInit()
    {
        _class _c = of( "public class A" );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains("public" ) );        
        
        _c = of( "io.varcode", "public class A" );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _c.getPackageName() );
        
        _c = of( "/*comment*/", "public class A" );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains("public" ) );        
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        
        _c = of( "/*comment*/", "io.varcode", "public class A" );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _c.getPackageName() );
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        
        
        _c = of( "/*comment*/", "@Deprecated", "io.varcode", "public class A" );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _c.getPackageName() );
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _c.getAnnotations().getAt( 0 ).toString() );
        
        _c = of( "/*comment*/", "@Deprecated", "io.varcode", 
            "public abstract class A", 
            Map.class, Date.class );
        
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().containsAll( "public", "abstract" ) );        
        assertEquals( "io.varcode", _c.getPackageName() );
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _c.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _c.getImports().containsAll( Map.class, Date.class ) ); 
        
        _c = of( 
            "/**comment*/", 
            "@Deprecated", 
            "io.varcode", 
            "public abstract class A", 
            Map.class, Date.class,
            _field.of( "public static final int ID = 100;" )
                .javadoc( "comment" ),
            _method.of( "public abstract String doIt" )    
            );
        
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().containsAll( "public", "abstract" ) );        
        assertEquals( "io.varcode", _c.getPackageName() );
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _c.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _c.getImports().containsAll( Map.class, Date.class ) ); 
        assertEquals( 1, _c.getFields().count() );
        assertEquals( 1, _c.getMethods().count() );        
    }
    
    public void testJDocClass()
    {
         _class _c = of( 
            _package.of( "io.varcode" ),
            _imports.of( Map.class, Date.class ),
            _javadoc.of( "comment" ),             
            _annotation.of( "@Deprecated" ),            
            "public static class A"                         
            );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains( "public" ) );        
        assertEquals( "io.varcode", _c.getPackageName() );
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _c.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _c.getImports().containsAll( Map.class, Date.class ) );
    }
    
    
    /*
    public void testFullClassInit()
    {
        _class _c = of( 
            _license.of( _license.APACHE_2_0, "author", "M. Eric DeFazio" ),    
            _package.of( "howto.java.author.detail" ),
            _imports.of( TestCase.class ),
            _javadoc.of( "this is a javadoc" ),  // 
            _annotation.of( "@Deprecated" ),     // @Deprecated
            "public class _classInitialize extends TestCase"                    
            );
    }
*/
    
    private static class ClassParams
    {
        //_license license;
        _package pack;    /* package io.varcode....*/
        _imports imports = new _imports(); // "import Java.lang", Class
        _javadoc javadoc; // starts with /* ends with */
        _annotations annots = new _annotations(); //starts with @
        _class._signature signature; //starts with 
        List<_facet> facets = new ArrayList<_facet>();        
    }
    
    public static _class of( Object... components )
    {
        ClassParams cd = new ClassParams();
        for( int i = 0; i < components.length; i++ )
        {
            if( components[ i ] instanceof String )
            {
                fromString( cd, (String)components[ i ] );
            }
            else if( components[ i ] instanceof Class )
            {
                if( ((Class)components[i]).isAnnotation() )
                {
                    cd.annots.add( components[ i ] );
                    cd.imports.addImport( components[ i ] );
                }
                else
                {
                    cd.imports.addImport( components[ i ] );
                }
            }
            /*
            else if( components[ i ] instanceof _license )
            {
                cd.license = (_license)components[ i ];
            }
            */
            else if( components[ i ] instanceof _package )
            {
                cd.pack = (_package)components[ i ];
            }
            else if( components[ i ] instanceof _imports )
            {
                cd.imports = (_imports)components[ i ];
            }
            else if( components[ i ] instanceof _javadoc )
            {
                cd.javadoc = (_javadoc)components[ i ];
            }
            else if( components[ i ] instanceof _annotations )
            {
                cd.annots = (_annotations)components[ i ];
            }
            else if( components[ i ] instanceof _class._signature )
            {
                cd.signature = (_class._signature)components[ i ];
            }
            else if( components[ i ] instanceof _facet )
            {
                cd.facets.add( (_facet)components[ i ] );
            }
        }
        _class _c = new _class( cd.signature );
        for( int i = 0; i < cd.annots.count(); i++ )
        {
            _c.annotate( cd.annots.getAt( i ) );
        }
        for( int i = 0; i < cd.imports.count(); i++ )
        {
            _c.imports( cd.imports.getImports().toArray( new Object[ 0 ] ) );
        }
        for( int i = 0; i < cd.facets.size(); i++ )
        {
            _c.add( cd.facets.get( i ) );
        }
        if( cd.javadoc != null && !cd.javadoc.isEmpty())
        {
            _c.javadoc( cd.javadoc.getComment() );
        }
        /*
        if( cd.license != null ) //TODO fix
        {
            _c.codeLicense( cd.license.author() );
        }
        */
        if( cd.pack != null && !cd.pack.isEmpty() )
        {
            _c.packageName( cd.pack.getName() );
        }
        return _c;
    }
    
    private static void fromString( ClassParams cd, String component )
    {
        if( component.startsWith( "/**" ))
        {
            cd.javadoc = _javadoc.of( component.substring(3, component.length() -2 ) );            
        }
        else if( component.startsWith( "/*" ))
        {
            cd.javadoc = _javadoc.of( component.substring(2, component.length() -2 ) );            
        }
        else if( component.startsWith( "package ") )
        {
            cd.pack = _package.of( component );
        }        
        else if( component.startsWith( "@" ) )
        {
            cd.annots.add( _annotation.of( component ) );
        }        
        else
        {
            String[] tokens = component.split( " " );
            if( tokens.length == 1 )
            {
                if( tokens[ 0 ].indexOf( "." ) > 0 )
                {
                    cd.pack = _package.of( tokens[ 0 ] );
                }
            } 
            else
            {
                cd.signature = _signature.of( component ); 
            }
        }        
    }
    
}
