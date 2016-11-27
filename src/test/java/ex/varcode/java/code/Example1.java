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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocObjectInputStream;
import varcode.java.langmodel._class;
import varcode.java.langmodel._fields;
import varcode.java.langmodel._fields._field;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
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
    
    public static _field f = _field.of( "public final {+type+} {+name+}" );
    
    public static _class Tuple = 
       _class.of("public class {+tupleName+} implements Serializable");
            
    
    public void testLazyBind()
    {
        _class myTuple = _class.cloneOf( Tuple );
        myTuple.replace( "tupleName", "MyTuple" );
    }
    
    public static _class MyBean = 
       _class.of("public class MyBean implements Serializable")
            .imports( Serializable.class, Date.class )
            .field("private final Date date;")
            .constructor("public MyBean( Date date )",
                "this.date = date;")
            .method("public Date getDate()",
                "return this.date;");
    
    public void testMyBeanSerializable()
    {
        ObjectOutputStream oos = null;
        try 
        {
            Date d = new Date();
            
            Class myBeanClass = MyBean.loadClass();            
            Object instance = _Java.instance(myBeanClass, d );
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( baos );
            oos.writeObject( instance );
                
            //These bytes represent the object
            byte[] bytes = baos.toByteArray();
                
            ByteArrayInputStream bais = new ByteArrayInputStream( bytes );                
            ObjectInputStream ois = 
                new AdHocObjectInputStream( 
                    (AdHocClassLoader)myBeanClass.getClassLoader(),
                    bais );
            Object deserialized = ois.readObject();
                
            //now verify the date (on the deserialized object is the same)
            assertEquals( d, _Java.invoke( deserialized, "getDate" ) );              
        }
        catch( Exception ex ) 
        {
            fail( "could not Serialiaze/ Deserialize" );
        }
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
