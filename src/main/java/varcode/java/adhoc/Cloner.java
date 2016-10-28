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
package varcode.java.adhoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import varcode.VarException;

/**
 * Deep Copy Clones an object by Serializing it as a byte array and 
 * then de-serializing it.
 * 
 * NOTE the underlying class MUST BE SERIALIZABLE
 * 
 * @see _auto_externalizable
 * @author M. Eric DeFazio  eric@varcode.io
 */
public class Cloner
{
    public static Object clone( Object prototype )
    {
        if( prototype.getClass().getClassLoader() instanceof AdHocClassLoader )
        {
            return clone( 
                (AdHocClassLoader)prototype.getClass().getClassLoader(), 
                prototype );
        }
        ObjectOutputStream oos = null;
        try 
        {
            //serialize the instance to bytes    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( baos );
            oos.writeObject( prototype );
            
            //deserialize from bytes        
            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );                
            ObjectInputStream ois = new ObjectInputStream( bais );
            
            return ois.readObject();
            
        }
        catch( IOException ex ) 
        {
            throw new VarException( "IOException Unable to clone object", ex );
        }
        catch( ClassNotFoundException ex )
        {
            throw new VarException( "ClassNotFoundException Unable to clone object", ex );
        }
    }
    
    /**
     * If you want to clone an object that is not loaded in the main classloader
     * (or that contains an object in an AdHoc ClassLoader) then you can 
     * use this clone variant
     * 
     * @param adHocClassLoader a child adHocClassloader containing additional 
     * classes not on the 
     * @param toClone the object to clone
     * @return  the object
     */
    public static Object clone( AdHocClassLoader adHocClassLoader, Object toClone )
    {
        ObjectOutputStream oos = null;
        try 
        {
            //serialize the instance to bytes    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( baos );
            oos.writeObject( toClone );
            
            //deserialize from bytes        
            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );                
            AdHocObjectInputStream ois = 
                new AdHocObjectInputStream( adHocClassLoader, bais );
            
            return ois.readObject();            
        }
        catch( IOException ex ) 
        {
            throw new VarException( "IOException cloning object", ex );
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new VarException( "ClassNotFoundException cloning object", cnfe );
        }
    }    
}
