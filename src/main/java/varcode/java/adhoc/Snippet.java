/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.java.adhoc;

import java.lang.reflect.Method;
import varcode.java.JavaException;
import varcode.java.JavaReflection;
import varcode.java.adhoc.AdHocException;
import varcode.java.adhoc.JavacException;
import varcode.java.model._class;
import varcode.java.model._imports;
import varcode.java.model._methods._method;

/**
 * API for dynamically compiled Java code from a String that is and callable 
 * at runtime.
 * <PRE>
 *  for example:
 *  //create a snippet that adds two numbers
    Snippet add = Snippet.of( "(int a,int b){return a + b;}" );
        //                     |-----------| |-----------|
        //                       parameters      body     
 * 
 *  int sum = (int)add.call( 2,3 ); 
 * </PRE>
 * 
 * NOTE: we can ALSO use VARARGS
 * 
 * 
 * Snippet noInputOrOutput = 
 *    Snippet.of( "{ System.out.println( new Date()); }" );
 * 
 * 
 * Snippet onlyInput = 
 *    Snippet.of( "(int a, int b){ System.out.println( a + b ); }" );
 * 
 * Snippet onlyOutput = 
 *    Snippet.of( "String{ java.util.UUID.randomUUID().toString(); }" );
 * 
 * Snippet inputAndOutput = 
 *    Snippet.of( "int(int a, int b){ return a + b; }" );
 * 
 * Snippet is defined more by what it DOESNT care about verses what it does
 * care about.
 * 
 * 
 * MOST LIKELY an Idempotent function (although it CAN use external resources)
 * 
 * We DOESNT CARE about: 
 * <UL>
 *  <LI>its "name" (really)
 *  <LI>the "container" class (package, className)
 *  <LI>comments, etc.
 *  <LI>the lifecycle state of the container (instance variables)
 * </UL>  
 * 
 * The only thing the Snippet API lets you do is:
 * 1) define  the code
 * 2) execute the code
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Snippet
{
    public final Method method;
    public final Class clazz;
    
    public static Snippet of( _method _m )
    {
        return new Snippet( new _imports(), _m );        
    }
    
    public static Snippet of( _imports _i, _method _m )
    {
        return new Snippet( _i, _m );
    }
    
    public static Snippet of( _imports imports, String declaration )
    {
        SnippetTokens sp = parse( declaration );
        sp.imports.add( imports );
        return of( sp );
    }
    
    public static Snippet of( Class returnType, String declaration )
    {
        SnippetTokens sp = parse( declaration );
        sp.imports.add( returnType );
        sp.returnType = returnType.getCanonicalName();
        return of( sp );
    }
    
    
    public static Snippet of( String snippetDeclaration )
    {
        SnippetTokens sp = parse( snippetDeclaration );
        return of( sp );
    }
    
    public static Snippet of( SnippetTokens sp )
    {
        if( sp.returnType == null )
        {
            sp.returnType = "void"; 
        }
        if( sp.parameters == null )
        {
            sp.parameters = "()";
        }
        else
        {
            sp.parameters = "(" + sp.parameters + ")";
        }    
        _class _c = _class.of( "package varcode.snippet;", 
            "public class _S" + System.nanoTime() )
            .method( _method.of(
                "public static final " + sp.returnType + " snippet" + sp.parameters,
                sp.body ) );
        _c.imports( sp.imports );
        return new Snippet( _c );    
    }

    private static _class buildClass( _method _m )
    {
        _m.setModifiers( "public", "static", "final" );
        return _class.of( "package varcode.snippet;", 
            "public class _S" + System.nanoTime() )
            .method( _m );        
    }
    
    public static Method getSnippetMethod( Class clazz )
    {
        Method[] ms = clazz.getMethods();
        for(int i=0; i< ms.length; i++ )
        {
            if ( ms[ i ].getName().equals("snippet" ) )
            {
                return ms[ i ];
            }
        }
        throw new AdHocException( "could not find snippet method" );
    }
    
    public Snippet(  _imports imports, _method _m )
    {
        _m.setName( "snippet" );
        _class _c = buildClass( _m );
        _c.imports( imports );
        try
        {
            this.clazz = _c.loadClass(); 
            this.method = getSnippetMethod( this.clazz );
        }
        catch( JavaException je )
        {
            throw new JavacException( _c.author(), je );
        }        
    }
    
    public Snippet( _class _c )
    {
        
        try
        {
            this.clazz = _c.loadClass();
            this.method = getSnippetMethod( this.clazz ); //_c.getMethods().getAt( 0 );
        }
        catch( Exception e )
        {
            throw new AdHocException(
                "Unable to compile snippet class "+ System.lineSeparator() + 
                    _c, e );
        }
    }
  
    public Object call( Object...args )
    {
        return JavaReflection.invokeStatic( method, args );
    }
    
    /** used internally to store Snippet tokens */
    public static class SnippetTokens
    {        
        public _imports imports = new _imports();
        public String returnType; //optional
        public String parameters; //optional
        public String body; //required
    }
    
    /**
     * This will handle reading in snippets and break into parts
     * 
     * "{ System.out.println( new Date()); }"
     *   ---------------------------------- 
     * "(int a, int b){ System.out.println( a + b ); }"
     * "String{ java.util.UUID.randomUUID().toString(); }"
     * "int(int a, int b){ return a + b; }"
     * 
     * @param snippet
     * @return the snippet parts from the String
     */ 
    public static SnippetTokens parse( String snippet )
    {   
        snippet = snippet.trim();
        int openParamIndex = snippet.indexOf( "(" );
        
        
        int openBraceIndex = snippet.indexOf( "{" );
        //int closeBraceIndex = snippet.indexOf( "}" );
        
        if( openBraceIndex < 0 )
        {
            //the whole thing is a body
            SnippetTokens sp = new SnippetTokens();
            sp.body = snippet;
            return sp;
        }
        if( snippet.startsWith( "{" ) )
        {  //no parameters or return type (all body)
            SnippetTokens sp = new SnippetTokens();
            sp.body = snippet.substring( 1, snippet.length() - 1 );
            return sp;
        }
        if( snippet.startsWith( "(" ) )
        {   //parameters but no return type
            SnippetTokens sp = new SnippetTokens();
            int closeParamIndex = snippet.indexOf( ")" , openParamIndex );
            sp.parameters = snippet.substring( 1, closeParamIndex );
            sp.body = snippet.substring( openBraceIndex + 1, snippet.length() -1 );
            return sp;            
        }
        // -- here it could EITHER be: -- 
        //int{ return 100 + 5; } OR
        //int(int a, int b){ return a + b)        
        if( openParamIndex > 0 && openParamIndex < openBraceIndex )
        {   //this means We DO have input parameters
            //    int(int a, int b){ return a + b; }    
            SnippetTokens sp = new SnippetTokens();
            sp.returnType = snippet.substring( 0, openParamIndex ).trim();            
            int closeParamIndex = snippet.indexOf( ")" , openParamIndex );
            sp.parameters = snippet.substring( openParamIndex +1, closeParamIndex );
            sp.body = snippet.substring( openBraceIndex +1, snippet.length() - 1 );
            return sp;
        }
        else 
        {   //NO parameters, but a return type
            SnippetTokens sp = new SnippetTokens();
            sp.returnType = snippet.substring( 0, openBraceIndex ).trim();            
            sp.body = snippet.substring( openBraceIndex +1, snippet.length() - 1 );
            return sp;
        }
    }
}
