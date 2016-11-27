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
package varcode.java.langmodel.auto;

import java.util.ArrayList;
import java.util.List;
import varcode.doc.Compose;
import varcode.doc.Dom;
import varcode.java.langmodel._class;
import varcode.java.langmodel._code;
import varcode.java.langmodel._fields;
import varcode.java.langmodel._fields._field;
import varcode.java.langmodel._methods._method;
import varcode.markup.bindml.BindML;

/**
 * Automatically builds and returns Setter {@code _method} i.e.<PRE>
 * 
 * _field f = _field.of("public int count");
 * _method m = _autoSetter.of( f );
 *  // ... were m = 
 * public void setCount( int count )
 * {
 *     this.count = count;
 * }
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoSetter
{
    /** composes the signature for the setter*/
    public static final Dom SIGNATURE = BindML.compile( 
        "public void set{+$^(fieldName)*+}( {+type*+} {+fieldName*+} )" );
    
    /** composes the body of the setter */
    public static final Dom BODY = BindML.compile(
        "this.{+fieldName*+} = {+fieldName*+};" );
    
    public static _method of( _field f )
    {
        if( f.getModifiers().contains( "final" ) )
        {   //no setter methods for final fields
            return null;
        }
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
        return _method.of(Compose.asString( SIGNATURE, "fieldName", fieldName, "type", type ),
            _code.of(Compose.asString( BODY, "fieldName", fieldName ) ) );
    }
    
    /**
     * Creates a Fluent style Setter 
     * <PRE>
     * public MyBean setCount( int count )
     * {
     *     this.count = count;
     *     return this;
     * }
     * </PRE>
     * ...that has the set method return the containing class
     * so you can string together set methods like this
     * <PRE>
     * MyBean b = new MyBean().setA("1").setB("2").setC("3");
     * 
     * //instead of this:
     * 
     * MyBean b = new MyBean();
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
            return _method.of(Compose.asString( 
                    SIGNATURE, "className", className, "fieldName", fieldName, "type", type ),
                _code.of( Compose.asString( BODY, "fieldName", fieldName ) ) );
        }
        
        //creates fluent Setter methods for all non-final fields on theClass
        public static _class of( _class theClass )
        {
            _class withSetters = _class.cloneOf( theClass ); 
            _fields fields = theClass.getFields();
            for( int i = 0; i < fields.count(); i++ )
            {
                if( fields.getAt( i ).getModifiers().contains( "final" ) )
                {
                    continue;
                }
                withSetters.method( 
                    of( theClass.getName(), fields.getAt(i).getName(), fields.getAt(i).getType() ));
            }
            
            return withSetters;
        }
    }
}
