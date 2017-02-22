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
import varcode.java.model._class;
import varcode.java.model._imports;
import varcode.java.model._methods._method;

/**
 * API for dynamically compiled Java code from a String that is and callable 
 * at runtime.
 * <PRE>
  for example:
  //create a snippet that adds two numbers
    Snip add = Snip.of( "(int a,int b){return a + b;}" );
        //                     |-----------| |-----------|
        //                       parameters      body     
 
  int sum = (int)add.call( 2,3 ); 
 </PRE>
 
 NOTE: we can ALSO use VARARGS
 
 
 Snip noInputOrOutput = 
    Snip.of( "{ System.out.println( new Date()); }" );
 
 
 Snip onlyInput = 
    Snip.of( "(int a, int b){ System.out.println( a + b ); }" );
 
 Snip onlyOutput = 
    Snip.of( "String{ java.util.UUID.randomUUID().toString(); }" );
 
 Snip inputAndOutput = 
    Snip.of( "int(int a, int b){ return a + b; }" );
 
 Snip is defined more by what it DOESNT care about verses what it does
 care about.
 
 
 MOST LIKELY an Idempotent function (although it CAN use external resources)
 
 We DOESNT CARE about: 
 <UL>
 *  <LI>its "name" (really)
 *  <LI>the "container" class (package, className)
 *  <LI>comments, etc.
 *  <LI>the lifecycle state of the container (instance variables)
 * </UL>  
 
 The only thing the Snip API lets you do is:
 1) define  the code
 2) execute the code
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Snip
{
    public final Method method;
    public final Class clazz;
    
    /**
     * Evaluate and return the result of some code dynamically
     * 
     * example: 
     * assertEquals( 9, Snip.eval("5 + 4") );
     * 
     * @param code the code
     * @param inputParams input parameters
     * @return the result of the code
     */
    public static Object eval( String code, Object...inputParams )
    {
        if( !code.contains( "return" ) )
        {
            code = "return " + code;
        }
        SnippetTokens st = parse( code );
        st.returnType = "Object";
        Snip s = Snip.of( st );
        return s.call( inputParams );
    }
    
    public static Snip of( _method _m )
    {
        return new Snip( new _imports(), _m );        
    }
    
    public static Snip of( _imports _i, _method _m )
    {
        return new Snip( _i, _m );
    }
    
    public static Snip of( _imports imports, String declaration )
    {
        SnippetTokens sp = parse( declaration );
        sp.imports.add( imports );
        return of( sp );
    }
    
    public static Snip of( Class returnType, String declaration )
    {
        SnippetTokens sp = parse( declaration );
        sp.imports.add( returnType );
        sp.returnType = returnType.getCanonicalName();
        return of( sp );
    }
    
    
    public static Snip of( String snippetDeclaration )
    {
        SnippetTokens sp = parse( snippetDeclaration );
        return of( sp );
    }
    
    public static Snip of( SnippetTokens sp )
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
        return new Snip( _c );    
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
    
    public Snip(  _imports imports, _method _m )
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
    
    public Snip( _class _c )
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
    
    /** used internally to store Snip tokens */
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
