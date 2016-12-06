package howto.java.load;

import java.util.UUID;

/**
 * Top Level Enum for testing the Load functionality
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum TopLevelEnum 
{
    A,B,C;
    
    public static final String ID = UUID.randomUUID().toString();
    
}
