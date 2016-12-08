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
package varcode.java.metalang;

import varcode.java.metalang._code;
import varcode.java.metalang.cs._try;
import java.io.IOException;
import junit.framework.TestCase;
import static varcode.java.metalang._codeTest.N;

/**
 *
 * @author eric
 */
public class _tryTest
    extends TestCase
{
    
    public void test_tryCatchAndHandle()
    {
        _code c = new _code();
        _try t = _try.catchAndHandle(
            c, 
            IOException.class, 
            "System.out.println(\"Handling dat IO Exception\");");
        
        System.out.println( t );
        
        assertEquals( 
			"try" + N + 
		    "{" + N +
		    N+
		    "}" + N +
            "catch( java.io.IOException e )" + N +
            "{" + N +
            "    System.out.println(\"Handling dat IO Exception\");" + N +
            "}" + N 
            , t.toString() );
        
        
        //add another catch and handle to an existing _try
        t.catchAndHandle(
            "SimpleException", 
            "LOG.error(\"Got Simple Exception\");" );
        
        assertEquals( 
			"try" + N + 
		    "{" + N +
		    N+
		    "}" + N +
            "catch( java.io.IOException e )" + N +
            "{" + N +
            "    System.out.println(\"Handling dat IO Exception\");" + N +
            "}" + N +
            "catch( SimpleException e )" + N +
            "{" + N +
            "    LOG.error(\"Got Simple Exception\");" + N +
            "}" + N     
            , t.toString() );
        /*
        t.catchHandleException("SimpleException", 
            "LOG.error(\"Got Simple Exception\");");
        */
    }
    
    /*
	public void testTryCatch()
	{
		_code c = new _code( );
        /*
        _try t = _try.catchHandleException(
            c, 
            IOException.class, 
            "System.out.println(\"Handling dat IO Exception\");");
        
        t.catchHandleException("SimpleException", 
            "LOG.error(\"Got Simple Exception\");");
        
        
		c.catchHandleException(
			IOException.class, 
			"System.out.println(\"Handling dat IO Exception\");" );
		
		assertEquals( 
			"try" + N + 
		    "{" + N +
		    N+
		    "}" + N +
            "catch( java.io.IOException e )" + N +
            "{" + N +
            "    System.out.println(\"Handling dat IO Exception\");" + N +
            "}" + N 
            , c.toString() );
		
		c.catchHandleException(
            "SimpleException", 
            "LOG.error(\"Got Simple Exception\");");		
        
        assertEquals( 
			"try" + N + 
		    "{" + N +
		    N+
		    "}" + N +
            "catch( java.io.IOException e )" + N +
            "{" + N +
            "    System.out.println(\"Handling dat IO Exception\");" + N +
            "}" + N +
            "catch( SimpleException e )" + N +
            "{" + N +
            "    LOG.error(\"Got Simple Exception\");" + N +
            "}" + N     
            , c.toString() );
	}
	*/
    
    public void testTryWithResources()
    {
        _code c = new _code();
        _try t = _try.withResources( _code.of( 
            "BufferedReader br = new BufferedReader( new FileReader( path ) ) )"), c );
     
        assertEquals( 
			"try( BufferedReader br = new BufferedReader( new FileReader( path ) ) ) )" + N +
		    "{" + N +
            N + 
		    "}" + N , t.toString() );
        
        //add a finally Block to the try
        t.finallyBlock( "System.out.println(\"In Finally\");" );
        
        assertEquals( 
			"try( BufferedReader br = new BufferedReader( new FileReader( path ) ) ) )" + N +
		    "{" + N +
            N + 
		    "}" + N +
            "finally" + N +
            "{" + N +
            "    System.out.println(\"In Finally\");" + N +
            "}" + N , t.toString() );
        
        
    }
	/*
	public void testTryWithResources()
	{
		_code c = new _code();
        //_try t = _try.withResources(c,  "BufferedReader br = new BufferedReader( new FileReader( path ) ) )");
		c.tryWith(
            "BufferedReader br = new BufferedReader( new FileReader( path ) ) )");
		
		System.out.println( c );
		assertEquals( 
			"try( BufferedReader br = new BufferedReader( new FileReader( path ) ) ) )" + N +
		    "{" + N +
            N + 
		    "}" + N , c.toString() );				
	}
	*/
    /*
    public void testFinally2()
    {
        _code c = new _code();
		c.addTailCode("file.read();");
		c.finallyBlock( 
			"//do this at the end regardless" , 
			"System.out.println(\"DONE\");");
		System.out.println( c );
		assertEquals(
			"try" + N +
			"{" + N + 
            "    file.read();" + N +
            "}" + N + 
            "finally" + N +
            "{" + N +
            "    //do this at the end regardless" + N +
            "    System.out.println(\"DONE\");" + N +
            "}" + N,
            c.toString() );		
    }
*/
    
	public void testTryFinally()
	{
        //_try t = _try.finallyDo(c, "//do this at the end regardless" , "System.out.println(\"DONE\");"
		_code c = new _code();
		c.addTailCode("file.read();");
        
        _try t = _try.finallyBlock( c, 
            "//do this at the end regardless" , 
			"System.out.println(\"DONE\");");
		//c.finallyBlock( 
		//	);
		System.out.println( c );
		assertEquals(
			"try" + N +
			"{" + N + 
            "    file.read();" + N +
            "}" + N + 
            "finally" + N +
            "{" + N +
            "    //do this at the end regardless" + N +
            "    System.out.println(\"DONE\");" + N +
            "}" + N,
            t.toString() );		
	}
}
