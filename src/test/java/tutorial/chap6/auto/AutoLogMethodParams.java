package tutorial.chap6.auto;

import java.util.List;
import varcode.doc.translate.JavaTranslate;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._methods._method;
import varcode.java.metalang._parameters;
import varcode.java.metalang._javaComponent;

/**
 * Automate adding Logging for _method(s) within a _class
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AutoLogMethodParams 
{
    public static _javaComponent forMethod( _javaComponent _c, String methodName )
    {
        List<_method> methods = _c.getMethods().getByName( methodName );
        for( int i = 0; i < methods.size(); i++ )
        {
            forMethod( _c, methods.get( i ) );
        }
        return _c;
    }
    /**
     * NOTE: the assumption is that _m is a method of _c
     * 
     * @param _c the class MetaLang model
     * @param _m the method MetaLang model (assumed to be a method OF _c)
     * @return the modified _class
     */
    public static _javaComponent forMethod( _javaComponent _c, _method _m )
    {
        //dont attempt to do this for abstract or native methods
        if( _m.getModifiers().containsAny( "abstract", "native" ) || 
            _m.getBody().isEmpty() ) //or methods that are no-ops
        {
            return _c;
        }
        
        //get (or create) the LOG that will print out the method input
        _field _log = AutoGetOrCreateSLF4JLogger.getOrCreate( _c );
        
        //lets reuse varcodes Java doc Translate (to print parameter values
        //when they are things like arrays)
        
        _c.getImports().addImport( JavaTranslate.class );
        
        //adding the log statement to the beginging 
        _parameters _ps = _m.getParameters();
        if( _ps.count() == 0 )
        {   //no parameters, add a simple log statement and return
            _m.getBody().addHeadCode( 
                _log.getName() + ".debug( \"Calling no-arg method : " 
                    +_m.getName() + "\" ); " );
            return _c;
        }
        for( int i = _ps.count() -1; i >= 0; i-- )
        {
            //print each of the parameters separately in a LOG statement
            _m.getBody().addHeadCode( 
                _log.getName() + ".debug( \"[" + i + "] " 
                    + _ps.getAt( i ).getName() + ": \" + JavaTranslate.INSTANCE.translate(" 
                    + _ps.getAt( i ).getName() + ") );" );
        }
        return _c;
    }
    
}
