/*
 * Copyright 2017 Eric.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License space distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.adhoc;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.adhoc.CodeSpace.Space;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class CodeSpaceTest
    extends TestCase
{    
    /** 
     * If we want to evaluate some code and return the result,
     * we can do this 
     * 
     * Sacrificing Cycles/memory 
     * during dev time / build time
     * 
     * to save cycles in prod
     * 
     * CodeSpace.eval("4+5");
     */
    public void testBake()
    {
        assertEquals( new Integer(9), CodeSpace.bake( "4 + 5" ) );        
        assertEquals( new Double(1.0), CodeSpace.bake( "1.0d * 1" ) ); 
    }
    
    
    public void testInitVars()
    {
        Space space = 
            CodeSpace.of( 
                "s = prefix + s + postfix;" )
            .init( "String s = \"\";", 
                "String prefix = \"prefix\";",
                "String postfix = \"postfix\";")
            .space();
        assertEquals( "prefixpostfix", space.eval( ).get( "s" ) );
        
        //ok, lets change prefix
        space.set( "prefix", "pre" );
        assertEquals( "preprefixpostfixpostfix", space.eval( ).get( "s" ) );
        
        space.get( "s" );
        
        
    }
    public void testCodeSpaceParamString()
    {
        Space space = CodeSpace.of(             
            "this.s += postfix;" ) 
            .extend( TestCase.class )
            .init( "String s = \"S\";" ) //alternatively .init("String s = \"S\";");
            .init( "String postfix= \"1\";" )
            .space();
        
        assertEquals("S", space.get( "s" ) );        
        
        
        assertEquals("S1", space.eval().get( "s" ) );          
        assertEquals("S11", space.eval().get( "s" ) );  
        
        space.set("s", "" );
        assertEquals("",  space.get( "s" ) );        
        assertEquals("1111111111", space.iterate( 10 ).get( "s" ) );        
        
        //now change the postfix
        space.set( "postfix", "0" );
        //now iterate 10 times
        assertEquals("11111111110000000000", space.iterate( 10 ).get( "s" ) );        
        
    }
    
    public void testCodeSpaceParam()
    {
        Space space = CodeSpace.of(
            "System.out.println( a );",
            "a = a + 5;").extend( TestCase.class )
            .init( "int a = 4;" ).space() ;
        
        space.eval();
        assertEquals( new Integer(9), space.get( "a" ) );
    }
    
    public void testModelInstance()
    {
        _class _c = _class.of("ex.id", "public class IDGen")
            .imports( UUID.class )
            .method( "public String genId()",
                "return UUID.randomUUID().toString();");
        Space space = CodeSpace.of( _c,             
            "String s = idg.genId();" )
            .init( "IDGen idg = new IDGen();" )
            .space();
        
        space.eval();
    }
    
}
