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
package varcode.java.lang;

import varcode.java.metalang._code;
import varcode.java.lang.cs._forCount;
import junit.framework.TestCase;

/**
 *
 * @author eric
 */
public class _forCountTest
    extends TestCase
{
    public void testDirect()
    {
        _forCount f = new _forCount( 
            int.class, 
            "i", 
            0, 
            "<",  
            "100", 
            "i++", 
            _code.of("System.out.println(i);") );
        
        assertEquals(
            "for( int i = 0; i < 100; i++ )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(i);" + "\r\n" +
            "}", f.author( ) );                    
    }
    
    public void testUp()
    {
        _forCount f = _forCount.up( 
            100, 
            _code.of( "System.out.println(i);" ) );
        
        assertEquals(
            "for( int i = 0; i < 100; i++ )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(i);" + "\r\n" +
            "}", f.author( ) );                    
        
        f = _forCount.up( 
            "i",   
            100, 
            _code.of( "System.out.println(i);" ) );
        
        assertEquals(
            "for( int i = 0; i < 100; i++ )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(i);" + "\r\n" +
            "}", f.author( ) );
        
        f = _forCount.up( 
            "i",
            0,   
            100, 
            _code.of( "System.out.println(i);" ) );
        
        assertEquals(
            "for( int i = 0; i < 100; i++ )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(i);" + "\r\n" +
            "}", f.author( ) ); 
        
        f = _forCount.up( 
            "x",
            0,   
            100, 
            _code.of( "System.out.println(x);" ) );
        
        assertEquals(
            "for( int x = 0; x < 100; x++ )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(x);" + "\r\n" +
            "}", f.author( ) ); 
    }
    
    public void testDown()
    {
        _forCount f = _forCount.down( 100, _code.of( "System.out.println(i);" ) );
        
        assertEquals(
            "for( int i = 100; i >= 0; i-- )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(i);" + "\r\n" +
            "}", f.author( ) ); 
        
        f = _forCount.down( "x", 100, _code.of( "System.out.println(x);" ) );
        
        assertEquals(
            "for( int x = 100; x >= 0; x-- )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(x);" + "\r\n" +
            "}", f.author( ) ); 
        
        f = _forCount.down( "x", 100, 10, _code.of( "System.out.println(x);" ) );
        
        assertEquals(
            "for( int x = 100; x >= 10; x-- )" +"\r\n"+
            "{" + "\r\n" +
            "    System.out.println(x);" + "\r\n" +
            "}", f.author( ) );         
    }
    
}
