package varcode.java.lang.auto;

import varcode.VarException;
import varcode.java.lang._fields._field;
import varcode.java.lang._methods._method;
import varcode.java.load._JavaLoader;

/**
 * automatically creates a _method to get the element of
 * an array field at a given index.
 * 
 * @author Eric DeFazio
 */
public class _autoGetAtArrayIndex
{
     /**
     * Creates "getXXXXAt()" _method for an element of a member field array
     * 
     * @param field the field to 
     * @return 
     */
    public static _method fromField( _field field )
    {
        String type = field.getType();
        if(! type.endsWith( "]" )  )   
        {
            throw new VarException( 
                "Field Type \"" + field.getType() + 
                    "\" not array ( must end with [])" );
        }
        //probably wont work for 2d arrays
        String $type$ =  type.substring( 0, type.indexOf( "[" ) );            
        
        return fromField( $type$, field.getName() );
    }
    
    /** "template" for the "getXXX()At( int index )" method (for arrays) */
    private static class _GetAtArrayIndex       
    {
        class $elementType$ { }
        $elementType$[] $fieldName$;
        
        public $elementType$ get$FieldName$At( int index )
        {
            if( this.$fieldName$ == null )
            {
                throw new IndexOutOfBoundsException( "$fieldName$ is null" );
            }
            if( index < 0 || index > this.$fieldName$.length  )
            {
                throw new IndexOutOfBoundsException(
                    "index [" + index + "] is not in range [0..." 
                        + $fieldName$.length + "]" );
            }
            return this.$fieldName$[ index ];
        }        
    }     
    
    /** Load the prototype method once */
    private static final _method GetArrayAtIndexPrototypeMethod = 
        _JavaLoader._Class.from( _GetAtArrayIndex.class )
        .getMethodNamed( "get$FieldName$At" );
    
    private static String firstCaps( String fieldName )
    {
        return Character.toUpperCase( fieldName.charAt( 0 ) ) 
            + fieldName.substring( 1 );
    }
    
    public static _method fromField( String $type$, String $fieldName$ )
    {  
        //clone the prototype method
        _method m = _method.cloneOf( GetArrayAtIndexPrototypeMethod ); 
        
        //replace $elementType$ with  
        m.replace( "$elementType$", $type$ );
        m.replace( "$fieldName$", $fieldName$ );
        m.replace( "$FieldName$", firstCaps( $fieldName$ ) );
        return m;
        //return _GET_AT_ARRAY_INDEX.composeWith( $type$, $fieldName$ );
    }    
    
    public static void main(String[] args)
    {
        _method _m = fromField( "int", "x" );
        System.out.println( _m );
        
        _method _m2 = fromField( _field.of( "private String[] name;" ) );
        System.out.println( _m2 );
    }
}
