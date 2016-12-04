package howto.java.load;

import java.util.UUID;

/**
 * Top Level Interface for Testing
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface TopLevelInterface 
{
    /** Field comment */
    public static final String ID = UUID.randomUUID().toString();
    
    /** Method Comment */
    public int aMethod( String param, int count );
}
