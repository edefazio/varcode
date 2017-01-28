package varcode.translate;

import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class PrimitiveTranslateTest
    extends TestCase
{
    public void testPrims()
    {
        assertEquals( "1.0f",  PrimitiveTranslate.translateFrom( 1.0f ) );
        assertEquals( "'c'",  PrimitiveTranslate.translateFrom( 'c' ) );
        assertEquals( "1.0d", PrimitiveTranslate.translateFrom( 1.0d ) );
        assertEquals( "1",  PrimitiveTranslate.translateFrom( 1 ) );
        assertEquals( "1L", PrimitiveTranslate.translateFrom( 1L ) );
        assertEquals( "(byte)1", PrimitiveTranslate.translateFrom( (byte)1 ) );
        assertEquals( "(short)1", PrimitiveTranslate.translateFrom( (short)1 ) );
        assertEquals( "(byte)1", PrimitiveTranslate.translateFrom( (byte)1 ) );        
        assertEquals( "String", PrimitiveTranslate.translateFrom( "String" ) );

        assertEquals( "1.0f",  PrimitiveTranslate.translateFrom( new Float( 1.0f ) ) );
        assertEquals( "'c'",  PrimitiveTranslate.translateFrom( new Character( 'c' ) ) );
        assertEquals( "1.0d", PrimitiveTranslate.translateFrom( new Double( 1.0d ) ) );
        assertEquals( "1",  PrimitiveTranslate.translateFrom( new Integer( 1 ) ) );
        assertEquals( "1L", PrimitiveTranslate.translateFrom( new Long( 1L) ) );
        assertEquals( "(byte)1", PrimitiveTranslate.translateFrom( new Byte( (byte)1 ) ) );
        assertEquals( "(short)1", PrimitiveTranslate.translateFrom( new Short( (short)1 ) ) );
        assertEquals( "(byte)1", PrimitiveTranslate.translateFrom( new Byte( (byte)1 ) ) );                
    }
    
}
