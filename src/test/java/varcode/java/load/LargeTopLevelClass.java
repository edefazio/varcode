package varcode.java.load;

import java.io.Serializable;
import static java.lang.System.*;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The purpose of this class is to push the boundaries and ensure
 * that all Facets of Reading in the AST / LangModel (_class) works
 * @author Eric DeFazio
 */
@Deprecated
public abstract class LargeTopLevelClass
    implements Serializable
{
    /** Field Comment */
    @Deprecated
    public static final Logger LOG = 
        LoggerFactory.getLogger( LargeTopLevelClass.class );
    
    static
    {
        //comment
        out.println( "In Static Block" );
        /*comment*/
    }
    
    /**
     * Method JavaDoc
     * @param map a map
     * @return a List of stuff
     */
    public abstract List<String> 
        someAbstractMethod( Map<String, Integer> map );
    
    public final void someMethod( String... varArgs )
    {
        for( int i = 0; i < varArgs.length; i++ )
        {
            //comment
            System.out.println( varArgs[i] );
            /*comment*/
        }
    }
    
    /** Comment */
    @Deprecated    
    public static void main( @Deprecated String[] args )
    {
        
    }
}
