package tutorial.varcode.chapx.appendix;

import varcode.java.lang._enum;
import varcode.java.lang.auto._autoEnum;

/**
 *
 * @author eric
 */
public class Chap1_AutoEnum
{
    public void testAutoEnum()
    {
        _autoEnum ae = new _autoEnum( "tutorial.varcode.chap1", 
            "MyColorPalette" );
        ae.property("private final String name");
        ae.property("private final int ARGB");
        
        ae.value("RED", "\"red\"", 0x00FF0000 );
        ae.value("GREEEN", "\"green\"", 0x0000FF00 );
        ae.value("BLUE", "\"blue\"", 0x000000FF );
        
        //we can get the underlying _enum model build from the _autoEnum
        _enum e = ae.getEnum();
        
        System.out.println( ae.author( ) );        
    }
}
