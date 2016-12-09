package howto.java.macro;

import varcode.java.lang._enum;
import varcode.java.macro._autoEnum;

/**
 * 
 * @author eric
 */
public class AutoEnum
{
    public void testAutoEnum()
    {
        _autoEnum _ae = new _autoEnum( "howto.java.macro", 
            "MyColorPalette" );
        _ae.property("private final String name;");
        _ae.property("private final int ARGB;");
        
        _ae.value("RED", "\"red\"", 0x00FF0000 );
        _ae.value("GREEEN", "\"green\"", 0x0000FF00 );
        _ae.value("BLUE", "\"blue\"", 0x000000FF );
        
        //we can get the underlying _enum model build from the _autoEnum
        _enum e = _ae.as_enum();
        
        System.out.println(_ae.author( ) );        
    }
}
