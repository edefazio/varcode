package howto.java.load;

import java.util.UUID;

/**
 *
 * @author Eric
 */
public enum TopLevelEnum 
{
    A,B,C;
    
    public static final String ID = UUID.randomUUID().toString();
    
}
