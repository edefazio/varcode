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
package tutorial.varcode.chapx.appendix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;
import junit.framework.TestCase;
import varcode.java.adhoc.AdHocObjectInputStream;
import varcode.java.lang._class;
import varcode.java.lang.auto._autoDto;
import varcode.java.lang.auto._autoExternalizable;
import varcode.java.model.cs._autoToString;

/**
 * Illustrates creating an AdHoc Class (and using _autoExternalizable),
 * Serializing it to a set of in memory bytes (using Externalizable.writeExternal)
 * Deserializing it from a set of bytes (using Externalizable.readExternal)
 * 
 * and verifying the values are unchanged
 * 
 * @author eric
 */
public class Chap2_AdHocExternalizable_Serialization
    extends TestCase
{
    public static _autoDto dto = _autoDto.of( 
        "tutorial.varcode.chapx.appendix.Exter" )
        .property( String.class, "name" )
        .property( Date.class, "date" )
        .property( "Map<String,Integer> nameToCount;" )
        .property( int[].class, "numbers" )    
        .imports( Map.class );
    
    public static _class _extern = 
        _autoToString.of( _autoExternalizable.of( dto.toClassModel() ) );    
    
    public void testSerDeser() 
        throws IOException, ClassNotFoundException
    {
        Object emptyInstance = _extern.instance( );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( emptyInstance );
        
        
        //gets the serialized bytes
        byte[] bytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
        
        AdHocObjectInputStream adHocInStream = 
            new AdHocObjectInputStream( emptyInstance.getClass(), bais );
            
        Object clone = adHocInStream.readObject();        
    }
}
