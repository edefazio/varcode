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

import varcode.doc.Compose;
import varcode.dom.Dom;
import varcode.java.Java;
import varcode.java.code._class;
import varcode.java.code._code;
import varcode.java.code._fields._field;
import varcode.java.code._if;
import varcode.java.code._methods._method;
import varcode.markup.bindml.BindML;

/**
 *
 * @author eric
 */
public class _autoGetter
{
    private static final String firstUpper( String s )
    {
        return Character.toUpperCase( s.charAt(0) ) + s.substring( 1 );
    }
        
    public static final Dom SIGNATURE = BindML.compile( 
        "public {+type+} get{+$^(fieldName)+}( )"
    );
    
    public static final Dom BODY = BindML.compile( 
        "return this.{+fieldName+};"
    );
          
    /**
     * Creates and returns a _method based on a _field
     * @param field
     * @return 
     */
    public static _method of( _field field )
    {
        return of( field.getName(), field.getType() );
    }
    
    public static _method of( String fieldName, Object type )
    {
        return _method.of(Compose.asString( SIGNATURE, "type", type, "fieldName", fieldName ),
            _code.of(Compose.asString( BODY, "fieldName", fieldName ) ) );     
             
            //"public " + field.getType() + " get" + firstUpper( field.getName() )+"()",
            //    "return this." + field.getName() +";" );    
    }
    
    /**
     * Creates a getAtIndex method for a field that is a array or collection
     * 
     * int[]
     * Set<String>[]
     * 
     * @param field
     * @return 
     */
    public static _method getAtArrayIndex( _field field )
    {
        String type = field.getType();
        if(! type.endsWith( "]" )  )   
        {
            
        }
        //probably wont work for 2d arrays
        String elementType =  type.substring( 0,  type.indexOf( "[" ));            
        
        
        //OK THIS IS EXACTLY WHY we Have BindML, because (its a mess)
        // OR EVEN BETTER, we use $ML
        //Exception e = IndexOutOfBoundsException.class;               
        /*
        return _method.of(
            "public " + elementType + " get" + firstUpper( field.getName() )+"At( int index )",
            _if.is( "this." + field.getName() + " == null", 
                "throw new IndexOutOfBoundsException( \"" + field.getName() + " is null\");" ),
            _if.is("this." + field.getName() + ".length <= index || index < 0",
                "throw new IndexOutOfBoundsException( \" index \"+ index + \" is not in range [0...\" + this." + field.getName() + ".length+\"]\");" ),
            "return this." + field.getName() + "[ index ];" );         
        */
        return _method.of(
            "public {+elementType+} get{+$^(fieldName)+}At( int index )",
            ARRAY_BODY );
    }
    
    public static final Dom ARRAY_SIGNATURE = BindML.compile( 
        "public {+elementType+} get{+$^(fieldName)+}At( int index )" );
            
    public static final $GetArrayAtIndex ARRAY_BODY = new $GetArrayAtIndex();
    
    private static class $GetArrayAtIndex
        extends varcode.markup.$ml.$CodeForm
    {
        class $elementType$ { }
        $elementType$[] $fieldName$;
        
        public $elementType$ get$FieldName$At( int index )
        {
            /*{$*/
            if( this.$fieldName$ == null )
            {
                throw new IndexOutOfBoundsException( "$fieldName$ is null " );   
            }
            if( index < 0 || index > this.$fieldName$.length  )
            {
                throw new IndexOutOfBoundsException( 
                    "index [" + index + "] is not in range [0..." + $fieldName$.length + "]" );
            }
            return this.$fieldName$[ index ];
            /*$}*/
        }
    }        
    
    final static String N = "\r\n";
    
    //write a method
    //<wrap it in tags>
    // parameterize fieldName, elementType
    //</wrap it in tags>
    
    public static Dom ARRAY_AT_SIGNATURE = BindML.compile(
        "public {+elementType+} get{+$^(fieldName)+}At( int index )"
    );
    
    public static Dom ARRAY_AT_BODY = BindML.compile(
        "if( this.{+fieldName+} == null )" + N +
        "{" + N +
        "    throw new IndexOutOfBoundsException( \"{+fieldName+} is null\" );" + N +
        "}" + N +
        "if( index < 0 || index > this.{+fieldName+}.length )" + N +
        "{" + N +    
        "    throw new IndexOutOfBoundsException( " + N + 
        "        \"index \"+ index + \" is not in range [0...\" + this.{+fieldName+}.length+\"]\");" + N +
        "}" + N +
        "return this.{+fieldName+}[ index ];" );        
    
    public static void main(String[] args)
    {
        _class c = _autoDto.of("ex.varcode.dto.ArrDto")
            .property("public int[] arr = new int[] {1,2,3,4,5};")
            .toClassModel();
        
        _method m = getAtArrayIndex( c.getFields().getAt( 0 ) );
        c.method( m );
        
        System.out.println( c );
        Object instance = c.instance( );
        System.out.println ( Java.invoke(instance, "getArrAt", 0 ) );
        
        
    }
    
}
