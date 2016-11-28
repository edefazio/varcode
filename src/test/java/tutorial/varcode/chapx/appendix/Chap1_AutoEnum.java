package tutorial.varcode.chapx.appendix;

import varcode.java.langmodel._enum;
import varcode.java.langmodel.auto._autoEnum;

/**
 *
 * @author eric
 */
public class Chap1_AutoEnum
{
    public void testAutoEnum()
    {
        _autoEnum _ae = new _autoEnum( "tutorial.varcode.chap1", 
            "MyColorPalette" );
        _ae.property("private final String name");
        _ae.property("private final int ARGB");
        
        _ae.value("RED", "\"red\"", 0x00FF0000 );
        _ae.value("GREEEN", "\"green\"", 0x0000FF00 );
        _ae.value("BLUE", "\"blue\"", 0x000000FF );
        
        //we can get the underlying _enum model build from the _autoEnum
        _enum e = _ae.getEnum();
        
        System.out.println(_ae.author( ) );        
    }
}
