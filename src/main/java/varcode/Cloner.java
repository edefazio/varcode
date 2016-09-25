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
package varcode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import varcode.VarException;

/**
 * Clones an object by Serializing it as a byte array and then deserializaing
 * it.
 * 
 * NOTE the underlying class MUST BE SERIALIZABLE
 * 
 * @author eric
 */
public class Cloner
{
    public static Object clone( Object prototype )
    {
        ObjectOutputStream oos = null;
        try 
        {
            
            //compile and load the authored class
            //Class myBeanClass = MyBean.loadClass();            
            //create a new instance
            //Object instance = Java.instance( myBeanClass, d );

            //serialize the instance to bytes    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( baos );
            oos.writeObject( prototype );
            
            //deserialize from bytes        
            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );                
            ObjectInputStream ois = new ObjectInputStream( bais );
            
            return ois.readObject();
            
        }
        catch( Exception ex ) 
        {
            throw new VarException( "Unable to clone object" );
        }
    }
    
}
