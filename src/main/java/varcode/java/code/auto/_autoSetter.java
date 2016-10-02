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

import varcode.doc.Author;
import varcode.dom.Dom;
import varcode.java.code._code;
import varcode.java.code._fields._field;
import varcode.java.code._methods._method;
import varcode.markup.bindml.BindML;

/**
 * Automatically builds and returns Setter {@code _method}
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoSetter
{
    /** composes the signature for the Setter method */
    public static final Dom SIGNATURE = BindML.compile( 
        "public void set{+$^(fieldName)*+}( {+type*+} {+fieldName*+} )" );
    
    
    public static final Dom BODY = BindML.compile(
        "this.{+fieldName*+} = {+fieldName*+};" );
    
    public static _method of( _field f )
    {
        return of( f.getName(), f.getType() );
    }
        

    /** 
     * Creates setter method for a member field with name fieldName of type
     * @param fieldName name of the field
     * @param type type of the field
     * @return the setter method
     */ 
    public static _method of( String fieldName, Object type )
    {
        return _method.of( 
            Author.code( SIGNATURE, "fieldName", fieldName, "type", type ),
            _code.of( Author.code( BODY, "fieldName", fieldName ) ) );
    }
    
    /**
     * Creates a Fluent style Setter that
     * has the set method return the containing class
     * so you can string together set methods like this
     * <PRE>
     * Bean b = new Bean().setA("1").setB("2").setC("3");
     * 
     * //instead of this:
     * 
     * Bean b = new Bean();
     * b.setA("1");
     * b.setB("2");
     * b.setC("3");
     * </PRE>
     */
    public enum Fluent
    {
        ;
         
        public static final Dom SIGNATURE = BindML.compile( 
            "public {+className+} set{+$^(fieldName)+}( {+type+} {+fieldName+} )" );
        
        public static final Dom BODY = BindML.compile(
            "this.{+fieldName+} = {+fieldName+};" + "\r\n" + 
            "return this;" );
        
        /**
         * Fluent-Style set method
         * @param className
         * @param f
         * @return 
         */
        public static _method of( String className, _field f )
        {
            return of( className, f.getName(), f.getType() );
        }
        
        /**
         * Fluent-Style set method
         * @param className the name of the class
         * @param fieldName the name of the field
         * @param type the type of the field to set
         * @return fluent setter _method given input
         */
        public static _method of( String className, String fieldName, Object type )
        {
            return _method.of( 
                Author.code( 
                    SIGNATURE, "className", className, "fieldName", fieldName, "type", type ),
                _code.of( Author.code( BODY, "fieldName", fieldName ) ) );
        }
    }
}
