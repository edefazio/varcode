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
package tutorial.varcode.chapx.appendix;

import java.util.Random;
import java.util.UUID;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.java.metalang._fields;

/**
 *
 * @author Eric
 */
public class AuthorEtc 
    extends TestCase
{
     public void testHello()
    {
        _class hello = _class.of( "HelloWorld" )
            .method( "public static final void main(String[] args)",
            "System.out.println(\"Hello World !\");");
        
        System.out.println( hello ); 
    }
    
    public static _fields._field _F = _fields._field.of( "public final {+type+} {+name+}" );
 
    public static _class _Tuple = 
       _class.of("public class {+tupleName+} implements Serializable");
            
    
    public void testLazyBind()
    {
        _class myTuple = _class.cloneOf(_Tuple );
        myTuple.replace( "tupleName", "MyTuple" );
    }
    
     public static class MyBeanTest
       extends TestCase
    {
       
    }        
    public void testCallGetId()
    {
        //"author", compile, load, and instantiate a new instance
        Object authored = 
            _class.of( "AuthoredClass" )
                .imports( UUID.class )
                .method( "public String getId",
                    "return UUID.randomUUID().toString();" )
                .instance();                
        //invoke a method on the 
        System.out.println( _Java.invoke( authored, "getId" ) );
    }
    
    public void testSimple()
    {
        _class _myId = _class.of("Authored")
            .imports( Random.class )
            .field("public static final int ID = 100;")
            .field("private static final Random RANDOM = new Random();")    
            .method("public static final int getId()",
                "return ID;")
            .method("public static int RandomInt()",
                "return RANDOM.nextInt();");
                
        System.out.println( _myId );
        
        Object inst = _myId.instance( );
        assertEquals( 100, _Java.invoke( inst, "getId" ) );
        
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
