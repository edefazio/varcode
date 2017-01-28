package example;

import java.util.UUID;

public interface ExInterface 
{
    public static final String ID = UUID.randomUUID().toString();
    
    /** Method Comment */
    public int aMethod( String param, int count );
}
