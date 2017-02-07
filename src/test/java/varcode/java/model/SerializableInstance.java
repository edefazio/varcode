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
package varcode.java.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocObjectInputStream;
import varcode.java.adhoc.SourceFolder;
import varcode.java.model._class;
/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SerializableInstance
    extends TestCase
{
   
    public static _class _SerializableBean = 
       _class.of("public class MyBean implements Serializable")
            .imports( Serializable.class, Date.class )
            .field( "private final Date date;" )
            .constructor( "public MyBean( Date date )",
                "this.date = date;")
            .method( "public Date getDate()",
                "return this.date;" );
    
    public void testMyBeanSerializable()
    {
        try 
        {
            Date d = new Date();
            
            AdHocClassLoader classLoader = 
                AdHoc.compile( _SerializableBean );
            Class myBeanClass = classLoader.findClass( _SerializableBean );
                    //_SerializableBean.loadClass();            
            Object instance = Java.instance( myBeanClass, d );
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
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
            assertEquals( d, Java.call( deserialized, "getDate" ) );              
        }
        catch( IOException ex ) 
        {
            fail( "could not Serialiaze/ Deserialize" );
        }
        catch( ClassNotFoundException ex ) 
        {
            fail( "could not Serialiaze/ Deserialize" );
        }
    }   
}
