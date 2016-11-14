package tutorial.varcode.chap2.markup;

import junit.framework.TestCase;
import varcode.java.code._methods;
import varcode.markup.codeml.code._Method;

/**
 *
 * @author Eric DeFazio
 */
public class _6_$Method$_TemplateModel
    extends TestCase
{    
    public static class $Method$Template
        extends _Method
    {
        /** Everything Between this exists to appease the compiler for the 
         * template*/
        class $elementType$ { }
        $elementType$[] $fieldName$;
        
        public _methods._method composeWith( Object $elementType$, String $fieldName$ )
        {
            return compose( "elementType", $elementType$, "fieldName", $fieldName$ );
        }
        /** **/
        /*$*/
        public $elementType$ get$FieldName$At( int index )
        {
            if( this.$fieldName$ == null )
            {
                throw new IndexOutOfBoundsException( "$fieldName$ is null" );
            }
            if( index < 0 || index > this.$fieldName$.length  )
            {
                throw new IndexOutOfBoundsException(
                    "index [" + index + "] is not in range [0..." + $fieldName$.length + "]" );
            }
            return this.$fieldName$[ index ];
        }
        /*$*/
    } 
}
