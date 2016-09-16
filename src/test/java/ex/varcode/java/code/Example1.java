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
package ex.varcode.java.code;

import java.util.Random;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.code._class;

/**
 *
 * @author eric
 */
public class Example1
    extends TestCase
{
    public void testHello()
    {
        _class hello = _class.of("HelloWorld")
            .method( "public static final void main(String[] args)",
            "System.out.println(\"Hello World !\");");
        
        System.out.println( hello ); 
    }
    
    public void testCallGetId()
    {
        //"author", compile, load, and instantiate a new instance
        Object authored = 
            _class.of( "CallInstance" )
                .imports( UUID.class )
                .method( "public static String getId",
                    "return UUID.randomUUID().toString();" )
                .instance();        
        
        System.out.println( authored );
        //invoke a method on the 
        System.out.println( Java.invoke( authored, "getId" ) );
    }
    
    public void testSimple()
    {
        _class myId = _class.of("Authored")
            .imports( Random.class )
            .field("public static final int ID = 100;")
            .field("private static final Random RANDOM = new Random();")    
            .method("public static final int getId()",
                "return ID;")
            .method("public static int RandomInt()",
                "return RANDOM.nextInt();");
                
        System.out.println( myId );
        
        Object inst = myId.instance( );
        assertEquals( 100, Java.invoke( inst, "getId" ) );
        
        System.out.println( _class.of( "EZClass" ) );
        
        Object ez = _class.of( "EZClass" ).instance( );
        ez = _class.of( "public class EZClass" ).instance( );
        
        assertEquals( ez.getClass().getName(), "EZClass");
        
        Class myClass = _class.of( "public class MyClass" )
            .staticBlock(
                "System.out.println(\"In Static block\");" )
            .loadClass();
        
        assertEquals( "MyClass", myClass.getName() );
    }
}
