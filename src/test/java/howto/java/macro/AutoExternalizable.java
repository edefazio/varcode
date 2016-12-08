package howto.java.macro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;
import junit.framework.TestCase;
import varcode.java.adhoc.AdHocObjectInputStream;
import varcode.java.metalang._class;
import varcode.java.metalang.macro._autoDto;
import varcode.java.metalang.macro._autoExternalizable;
import varcode.java.metalang.cs._autoToString;

/**
 * Illustrates creating an AdHoc Class (and using _autoExternalizable),
 * Serializing it to a set of in memory bytes (using Externalizable.writeExternal)
 * Deserializing it from a set of bytes (using Externalizable.readExternal)
 * 
 * and verifying the values are unchanged
 * 
 * @author eric
 */
public class AutoExternalizable
    extends TestCase
{
    public static _autoDto _dto = _autoDto.of( 
        "howto.java.macro.MyDto" )
        .property( String.class, "name" )
        .property( Date.class, "date" )
        .property( "Map<String,Integer> nameToCount;" )
        .property( int[].class, "numbers" )    
        .imports( Map.class );
    
    public void testApplyMultipleMacros_SerDeser() 
        throws IOException, ClassNotFoundException
    {
        //create a toString() AND
        _class _extern = _autoToString.of( 
            _autoExternalizable.of( 
                _dto.as_class() ) ); 
        
        Object emptyInstance = _extern.instance( );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( emptyInstance ); //use externalizable method
        
        
        //gets the serialized bytes
        byte[] bytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
        
        AdHocObjectInputStream adHocInStream = 
            new AdHocObjectInputStream( emptyInstance.getClass(), bais );
            
        Object clone = adHocInStream.readObject();        
    }
}
