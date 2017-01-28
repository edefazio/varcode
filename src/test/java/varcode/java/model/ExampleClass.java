package varcode.java.model;

import java.util.UUID;

/** Javadoc */
@Deprecated
public class ExampleClass
{
    @Deprecated
    public static final int count = 1;

    @Deprecated
    public String getStuff( int count )
    {
        System.out.println( "Hi" );
        return "STUFF";
    }
    
    @Override
    @Deprecated
    public String toString()
    {
        return "TOSTRING";
    }
}
