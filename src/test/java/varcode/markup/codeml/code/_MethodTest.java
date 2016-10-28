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
package varcode.markup.codeml.code;

import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.doc.Dom;
import varcode.java.code._methods._method;
import varcode.java.code._modifiers;
import varcode.java.code._throws;

/**
 *  
 * @author eric
 */
public class _MethodTest
    extends TestCase
{
    private static class WithComment
        extends _Method
    {
        /*$*/
        /**
         * this is the comment 
         * MULTILINE
         * {+param+}
         */
        public void doSomething()
        {
            
        }
        /*$*/        
    }        
    
    public void testWithComment()
    {
        WithComment wc = new WithComment();
        _method m = wc.compose( );
        assertEquals( "public void doSomething(  )", m.getSignature().toString() );
        assertEquals( 
            "/**" + N + 
            " * this is the comment" + N + 
            " * MULTILINE" + N +         
            " */" + N, 
            m.getJavadoc().toString() );
        
        m = wc.compose( "param", "VALUE" );
        
        System.out.println( m.toString() );
        
        assertEquals( 
            "/**" + N + 
            " * this is the comment" + N +
            " * MULTILINE" + N +         
            " * VALUE" + N +        
            " */" + N, 
            m.getJavadoc().toString() );
    }
    
    private static class _Simple
        extends _Method
    {
        /*$*/
        public static int count()
        {
            return 5;
        }
        /*$*/
    }        
    
    public static String N = System.lineSeparator();
    
    public void testSimple()
    {
        _Simple s = new _Simple();
        
        Dom sigDom = s.getSignatureDom();
        assertEquals(
            "public static int count()", 
            sigDom.getMarkupText());
        
        Dom bodyDom = s.getBodyDom();
        assertEquals(
            "return 5;", 
            bodyDom.getMarkupText() );
        /*
        assertEquals(
            "public static int count()" + N + 
            "{" + N +
            "    return 5;" + N + 
            "}", s.getMarkup() );  
        */        
        _method m = s.compose();
        assertEquals( "public static int count(  )", m.getSignature().toString() );
        assertEquals( "return 5;", m.getBody().toString() );
        
      
    }
    
    public void testReadThenMutate()
    {
        _Simple s = new _Simple();
        
        //compose and return the _method as is
        _method m = s.compose();
        
        //now mutate the _method
        m.getSignature().setName( "newName" );
        
        assertEquals( "public static int newName(  )", m.getSignature().toString() );
        
        m.getSignature().setReturnType( String.class );
        
        assertEquals( "public static String newName(  )", m.getSignature().toString() );
        
        m.getSignature().setReturnType( "Object" );
        
        assertEquals( "public static Object newName(  )", m.getSignature().toString() );
        
        m.getSignature().setModifiers( _modifiers.of( "final" ) );
        
        assertEquals( "final Object newName(  )", m.getSignature().toString() );
        
        m.getSignature().setThrows( _throws.of( IOException.class ) );
        
        assertEquals( "final Object newName(  )" + "\r\n" +
            "    throws java.io.IOException", m.getSignature().toString() );
        
        m.getSignature().setThrows( "VarException" );
        
        assertEquals( "final Object newName(  )" + "\r\n" +
            "    throws VarException", m.getSignature().toString() );
        
        m.getBody().replace( "5", "\"jello\"" );
        
        assertEquals( 
            "return \"jello\";", m.getBody().toString() );        
    }
    
    private static class _BindOne
        extends _Method
    {        
        public _method composeWith( String comment )
        {
            return compose( "comment", comment );
        }
        
        /*$*/
        public static void getOne( )
        {
            // $comment$
        }
        /*$*/
    }        
    
    public void testBindOne()
    {
        _BindOne s = new _BindOne();
        
       // String markup = s.getMarkup();
        
        _method m = s.compose( "comment", "replace comment" ); 
        assertEquals( "public static void getOne(  )", m.getSignature().toString() );
        assertEquals( "// replace comment", m.getBody().toString() );
        
        
        //verify I can compose and I get the same result
        m = s.compose(
            VarContext.of( "comment", "replace comment" ) ); 
        assertEquals( "public static void getOne(  )", m.getSignature().toString() );
        assertEquals( "// replace comment", m.getBody().toString() );
        
        _method mcw = s.composeWith("replace comment");
        assertEquals( mcw.getSignature().toString(), m.getSignature().toString() );
        assertEquals( mcw.getBody().toString(), m.getBody().toString() );
        
    }
    
    
    public static class _BindReturnType
        extends _Method     
    {
        static class $returnType${} 
        
        public _method composeWith( Object $returnType$ )
        {
            return compose( "returnType", $returnType$ );
        }
        
        /*$*/
        public $returnType$ bindReturnType( int param )
        { 
            return null;
        }
        /*$*/
    }
    
    public void testBindReturnType()
    {
        _BindReturnType brt = new _BindReturnType();
        _method m = brt.compose( "returnType", int.class );
        assertEquals( "public int bindReturnType( int param )", m.getSignature().toString() );
    }
 
    
    public static class _BindParamType
        extends _Method
    {
        public static class $paramType$ {}
        
        public _method composeWith( Object $paramType$ )
        {
            return compose( "paramType", $paramType$ );
        }
        
        /*$*/
        public int bindParam( $paramType$ param )
        {
            return 4;
        }
        /*$*/
    }
    
    public void testBindParamType()
    {
        _BindParamType brt = new _BindParamType();
        _method m = brt.compose( "paramType", "HashMap<String,Integer>" );
        assertEquals( "public int bindParam( HashMap<String,Integer> param )", m.getSignature().toString() );
    }
    
    
    public static class _BindMethodName
        extends _Method
    {
        public _method composeWith( Object $methodName$ )
        {
            return compose( "methodName", $methodName$ );
        }
                
        /*$*/
        public void get$MethodName$()
        {
            
        }
        /*$*/
    }
    
    public void testBindMethodName()
    {
        _BindMethodName bmn = new _BindMethodName();
        _method m = bmn.compose( "methodName", "bumpCount" );
        String s = m.getSignature().toString();
        assertEquals( "public void getBumpCount(  )", s );
        _method mc = bmn.composeWith( "bumpCount" );
        assertEquals( mc.getSignature().toString(), s );
        
    }
    
    public static class _BindReturnTypeNameParamArg
        extends _Method
    {
        static class $returnType$ {}
        static class $param$ {}
        
        public _method composeWith( 
            Object $returnType$, String $name$, Object $param$, String $arg$ )
        {
            return compose(
                "returnType", $returnType$, "name", $name$, "param", $param$, "arg", $arg$ );
        }
        
        /*$*/
        public $returnType$ do$Name$( $param$ $arg$ )
        {
            return null;
        }
        /*$*/
    }
    
    public void testBindCodeML()
    {
        _BindReturnTypeNameParamArg bcml = new _BindReturnTypeNameParamArg();
        _method m = bcml.compose( 
            "returnType", int.class, 
            "name", "count", 
            "param", String.class, 
            "arg", "blah" );
        String s = m.getSignature().toString();
        assertEquals( "public int doCount( String blah )",  s);
        
        _method mcw = bcml.composeWith( int.class, "count", String.class, "blah" );
        assertEquals( s, mcw.getSignature().toString() );
    }
    
    public static class _BindParams
        extends _Method
    {
        public _method composeWith( Object[] types, String[] names )
        {
            return compose( "type", types, "name", names );
        }
        
        /*$*/
        public void doParams( /*{{+:{+type+} {+name+}, +}}*/ )
        {
            
        }
        /*$*/
    }
    
    public void testParams()
    {
        _BindParams bp = new _BindParams();
        
        _method m = bp.compose( );
        assertEquals( "public void doParams(  )", m.getSignature().toString() );
        
        m = bp.compose( 
            "type", int.class, 
            "name", "a" );
        
        assertEquals( "public void doParams( int a )", m.getSignature().toString() );
        
        m = bp.compose( 
            "type", new Object[] {int.class, Map.class}, 
            "name", new String[]{ "a", "b"} );
        
        assertEquals( "public void doParams( int a, java.util.Map b )", m.getSignature().toString() );
        
    }
    
    public static class _ThrowsXYZ
        extends _Method
    {
        public _method composeWith( Object thrownException )
        {
            return compose("someException", thrownException );
        }
        
        public class $someException$ extends RuntimeException {}
        
        /*$*/
        public static void doNothing() throws $someException$
        { 
            
        }
        /*$*/
    }
    
    public void testThrows()
    {
        System.out.println( "*******************************WTF ");
        _ThrowsXYZ t = new _ThrowsXYZ();
        System.out.println( "*******************************WTF ");
        
        _method m = t.compose( "someException", IOException.class );
        assertEquals( "public static void doNothing(  )" + "\r\n" +
            "    throws java.io.IOException", m.getSignature().toString() );
        
        
    }
    
    public static class _BindReturnTypeNameParamArgBody
        extends _Method
    {
        static class $returnType$ {}
        static class $param$ {}
        
        public _method composeWith( 
            Object $returnType$, String $name$, Object $param$, String $arg$ )
        {
            return compose(
                "returnType", $returnType$, "name", $name$, 
                "param", $param$, "arg", $arg$ );
                    
        }
        /*$*/
        public $returnType$ do$Name$( $param$ $arg$ )
        {
            System.out.println( $arg$ );
            System.out.println( $param$.class );
            System.out.println( $returnType$.class );
            return null;
        }
        /*$*/
    }
    
    public void testBindSignatureBody()
    {
        String N = "\r\n";
        
        _BindReturnTypeNameParamArgBody bcml = new _BindReturnTypeNameParamArgBody();
        _method m = bcml.compose( 
            "returnType", int.class, 
            "name", "count", 
            "param", String.class, 
            "arg", "blah" );
        
        
        assertEquals( "public int doCount( String blah )", m.getSignature().toString() );
        assertEquals( 
            "System.out.println( blah );" + N + 
            "System.out.println( String.class );" + N +
            "System.out.println( int.class );" + N + 
            "return null;", m.getBody().toString() );
    }
}
