/*
 * Copyright 2016 eric.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.code.auto;

import java.util.List;
import varcode.VarException;
import varcode.java.code._fields._field;
import varcode.java.code._methods._method;
import varcode.markup.codeml.code._Method;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoGetter
{    
    /** "template" for the "getXXX()" method */
    private static class _Get
        extends _Method
    {
        public class $type$ {}
        public $type$ $fieldName$;
        
        public _method composeWith( Object $type$, String $fieldName$ )
        {
            return compose( "type", $type$, "fieldName", $fieldName$ );
        }
        
        /*$*/
        public $type$ get$FieldName$( )
        {
            return this.$fieldName$;
        }
        /*$*/
    }        

    /** "template" for the "getXXX()At( int index )" method (for arrays)*/
    private static class _GetAtArrayIndex
        extends _Method
    {
        class $elementType$ { }
        $elementType$[] $fieldName$;
        
        public _method composeWith( Object $elementType$, String $fieldName$ )
        {
            return compose( "elementType", $elementType$, "fieldName", $fieldName$ );
        }
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
    
    /** "template" for the "getXXX()At( int index )" method (for arrays)*/
    private static class _GetAtListIndex
        extends _Method
    {
        class $elementType$ 
        { 
            public int size() { return 0;} 
        }
        List<$elementType$> $fieldName$;
        
        public _method composeWith( Object $elementType$, String $fieldName$ )
        {
            return compose( "elementType", $elementType$, "fieldName", $fieldName$ );
        }
        
        /*$*/
        public $elementType$ get$FieldName$At( int index )
        {
            if( this.$fieldName$ == null )
            {
                throw new IndexOutOfBoundsException( "$fieldName$ is null" );
            }
            if( index < 0 || index > this.$fieldName$.size()  )
            {
                throw new IndexOutOfBoundsException(
                    "index [" + index + "] is not in range [0..." + $fieldName$.size() + "]" );
            }
            return this.$fieldName$.get( index );
        }
        /*$*/
    }        
    
    /** _Get is immutable, creates / returns methods via compose(...) */
    public static final _Get _GET = new _Get();
    
    /** _GetAtArrayIndex is immutable, creates/ returns methods via compose(...) */
    public static _GetAtArrayIndex _GET_AT_ARRAY_INDEX = new _GetAtArrayIndex();
    
    /** _GetAtArrayIndex is immutable, creates/ returns methods via compose(...) */
    public static _GetAtListIndex _GET_AT_LIST_INDEX = new _GetAtListIndex();
    
    /**
     * Creates and returns a _method based on a _field
     * @param field
     * @return 
     */
    public static _method of( _field field )
    {
        return of( field.getType(), field.getName() );
    }
    
    public static _method of( Object type, String fieldName )
    {
        return _GET.compose( "fieldName", fieldName, "type", type );        
    }
    
    /**
     * Creates "getXXXXAt()" _method for an element of a member field array
     * 
     * @param field the field to 
     * @return 
     */
    public static _method ofArrayIndex( _field field )
    {
        String type = field.getType();
        if(! type.endsWith( "]" )  )   
        {
            throw new VarException( 
                "Field Type \"" + field.getType() + "\" not array ( must end with [])" );
        }
        //probably wont work for 2d arrays
        String $type$ =  type.substring( 0, type.indexOf( "[" ) );            
        
        return ofArrayIndex( $type$, field.getName() );
    }
    
    public static _method ofArrayIndex( String $type$, String $fieldName$ )
    {
        return _GET_AT_ARRAY_INDEX.composeWith( $type$, $fieldName$ );
    }
}
