package varcode.form;

import junit.framework.TestCase;
import varcode.form.SeriesFormatter.AfterEach;
import varcode.form.SeriesFormatter.BetweenTwo;

public class SeparateFormsTest
    extends TestCase
{
    public static final String T = "    ";
    
    public static final String N = System.lineSeparator();
    
    public void testAlwaysAfter()
    {
        AfterEach aa = new AfterEach( "! " );
        
        //verify that if NONE exist, we dont use After
        String together = aa.format( new String[0] );
        assertEquals( "", together );
        
        together = aa.format( new String[]{"A"} );
        assertEquals( "A! ", together );
        
        together = aa.format( new String[]{"A", "B"} );
        assertEquals( "A! B! ", together );
        
        together = aa.format( new String[]{"A", "B", "C", "D"} );
        assertEquals( "A! B! C! D! ", together );                
    }
    
    public void testAlwaysAfterLineBreak()
    {
        AfterEach aa = new AfterEach( ";" + N );
        
        
        String together = aa.format( new String[]{"A", "B", "C", "D"} );
        assertEquals( "A;" + N + 
                      "B;" + N +
                      "C;" + N + 
                      "D;" + N, 
                      together ); 
    }
    
    /**
     * Sometimes separators ONLY APPEAR BETWEEN form instances
     */
    public void testOnlyBetween()
    {
        BetweenTwo ob = new BetweenTwo(", ");
        String together = null;
        
        together = ob.format( new String[0] );
        assertEquals( "", together );
        
        together = ob.format( new String[] {"A"} );
        assertEquals( "A", together );
        
        together = ob.format( new String[]{"A", "B", "C", "D"} );
        assertEquals( "A, B, C, D", together );
    }
}
