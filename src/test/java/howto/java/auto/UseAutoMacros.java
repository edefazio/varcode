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
package howto.java.auto;

import java.util.Date;
import junit.framework.TestCase;
import junit.framework.TestResult;
import varcode.java.model._class;
import varcode.java.model.auto._auto;
import static varcode.java.model.auto._auto.*;
import varcode.java.model.auto._autoApply._autoMacro;
import howto.java.adhoc.AdHocTest;

/**
 * Illustrates how auto programming can DRAMATICALLY reduce the effort needed
 * to generate new AdHoc classes
 * 
 * @author Eric
 */
public class UseAutoMacros
    extends TestCase
{
    /**
     * The easiest way to use a macro
     */
    public void testUsePrebuildMacro()
    {
        _class _c = 
            _class.of( "package ex;", "A" ).imports( Date.class )
                .fields( "final int a;", "final String b;", "final Date c;" );
        
        macro.IMMUTABLE_DATA_CLASS.apply( _c );
         
        /** This is just verifying the functionality added **/
        assertNotNull( _c.getMethod( "builder" ) ); //macro creates builder method
        assertNotNull( _c.getMethod( "getA" ) );    //macro created getA method
        assertNotNull( _c.getMethod( "getB" ) );    //macro created getB method
        assertNotNull( _c.getMethod( "getC" ) );    //macro created getC method
        assertNotNull( _c.getMethod( "toString" )); //macro created toString method
        assertNotNull( _c.getMethod( "equals" ) );  //macro created equals method
        assertNotNull( _c.getMethod( "hashCode" ) );//macro created hashCode method
        
        
        _class _builder = (_class)_c.getNest( "ABuilder" );
        assertNotNull( _builder ); //macro created a builder class
        assertNotNull( _builder.getMethod( "a") ); //macrobuilder  created a method
        assertNotNull( _builder.getMethod( "b") ); //macrobuilder  created a method
        assertNotNull( _builder.getMethod( "c") ); //macrobuilder  created a method
        assertNotNull( _builder.getMethod( "build") ); //macrobuilder  created a method
        
        AdHocTest.of( _c, 
            "Date d = new Date();",
            "A constr = new A(100, \"eric\", d);",
            "A built = A.builder().a(100).b(\"eric\").c(d).build();",            
            "assertEquals( constr, built );"
            //"assertFalse( constr.equals( built ) );"
            ).run() ;
       
       
       /*
        _class _c = _class.of( "public class CSTest extends TestCase" )
            .imports(TestCase.class, Date.class)
            .method( "public void testThing" )
        CodeSpace cs = CodeSpace.of( _c ).extend( TestCase.class )
            .code( 
            "Date d = new Date();",
            "A constructed = new A(100, \"eric\", d);",
            "A built = A.builder().a(100).b(\"eric\").c(d).build();",
            "assertEquals( constructed, built );" );
        _class theClass = cs.buildSpaceClass();
        
        System.out.println( theClass );
        cs.eval( );
        */
        //
        //Object space = _c.space( 100, "eric", new Date() );
        
    }
    
    public void testUseExistingAutoMacros()
    {
        _class _c = _class.of( "C" ).imports( Date.class )
            .fields( "private final int a", "String b", "Date c" );
        
        assertEquals( 0, _c.getMethods().count() );
        assertEquals( 3, _c.getFields().count() );
        
        //ImmutableDataClass
        
        _autoMacro _macro = 
            _auto.macro( 
                CONSTRUCTOR, 
                GETTERS, 
                SETTERS_FLUENT, 
                TO_STRING, 
                EQUALS, 
                HASHCODE );
        
        System.out.println( _c );
        
        //apply the _macro to the class
        _macro.apply( _c );
        
        //now look at the class
        System.out.println( _c );        
    }
}
