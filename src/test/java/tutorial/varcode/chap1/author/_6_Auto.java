package tutorial.varcode.chap1.author;

import java.util.Date;
import junit.framework.TestCase;
import varcode.java.JavaCase;
import varcode.java.code._class;
import varcode.java.code.auto._autoDto;
import varcode.java.code.auto._autoEnum;
import varcode.java.code.auto._autoExternalizable;
import varcode.java.code.auto._autoToString;

/**
 *
 * @author Eric DeFazio
 */
public class _6_Auto
    extends TestCase
{
    public void testAutoToStringExternalizable()
    {
        _class person = 
            _autoDto.of( "tutorial.varcode.chap1.author.MyDto" )
            .property( String.class, "name" )
            .property( Date.class, "dob" )     
            .toClassModel();
        
        person = _autoToString.of( person );
        person = _autoExternalizable.of( person );
        
        System.out.println( person );
        System.out.println( person.instance( ) );        
    }
    
    public void testAutoEnum()
    {
        _autoEnum ae = new _autoEnum( "tutorial.varcode.chap1", 
            "MyColorPalette" );
        ae.property("private final String name");
        ae.property("private final int ARGB");
        
        ae.value("RED", "\"red\"", 0x00FF0000 );
        ae.value("GREEEN", "\"green\"", 0x0000FF00 );
        ae.value("BLUE", "\"blue\"", 0x000000FF );
        
        JavaCase jc = ae.toJavaCase( );
        
        jc.loadClass( );
        
    }
}
