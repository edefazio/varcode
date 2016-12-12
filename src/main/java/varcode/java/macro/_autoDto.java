/*
 * Copyright 2016 M. Eric DeFazio.
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
package varcode.java.macro;

import java.lang.reflect.Modifier;
import varcode.java.JavaNaming;
import varcode.java.lang._class;
import varcode.java.lang._constructors._constructor;
import varcode.java.lang._fields._field;
import varcode.java.lang._modifiers;
import varcode.java.lang._package;

/**
 * Creates an _class that is a SteroTypical DTO (Data Transfer Object)
 * 
 * Data Transfer Objects are similar to "structs" in Java
 * they have private fields that can be accessed (read, updated)
 * using getters and setters.
 * 
 * Final Fields (that have no init) MUST be passed in on the constructor i.e.
 * <UL>
 *   <LI>"private final int a;" <-- this is a final field with no init 
 *    (and a value for a must be passed in in constructor)
 *   <LI>"private final int b = 100;" <-- this is a fienal field with an init
 * </UL>  
 * Final fields have only a getter (cant be set)
 * 
 */
public class _autoDto
    implements JavaMacro.StereoType
{
    /** 
     * we "hide" the creation of the underlying class, and provide a simpler
     * StereoType API
     */
    private final _class theClass;
    
    /**
     * Create a dto at the full pathName, add the appropriate fields
     * and build the approriate getters and setters:<PRE>
     * 
     *  _auto_dto.of("ex.varcode.dto.MyDto", "public int a");</PRE>
     * 
     * @param fullClassName the full class name ("ex.mypackage.MyClass")
     * @param fields the field definitions of the Dto (i.e. "private int count")
     * @return the pre-built _autoDto
     */    
    public static _autoDto of( String fullClassName, String... fields )
    {
        String[] packageAndClassName = 
            JavaNaming.ClassName.extractPackageAndClassName( fullClassName );
        
        _autoDto ad = 
            new _autoDto( packageAndClassName[ 0 ], packageAndClassName[ 1 ] );
        ad.properties(fields );
        return ad;        
    }
    
    /**
     * Build and return a _class that is a Dto with the following 
     * properties
     * @param fullClassName the fully qualified class name("ex.mypackage.MyClass")
     * @param fields the definitions of the fields on the class (of which 
     * getter/setters will be generated for)
     * @return the _class
     */
    public static _class _classOf( String fullClassName, String...fields )
    {
        return of(fullClassName, fields ).as_class();
    }
    
    /**
     * _auto_dto myDto = 
     *     _auto_dto.of( "ex.varcode.dto.MyDto" )
     *         .field( "public String name" )
     *         .field( "public Map<String,Integer> nameToCount")
     * 
     * 
     * @param packageName
     * @param className 
     */
    public _autoDto( String packageName, String className )
    {
       this.theClass = _class.of( _package.of( packageName ), 
            "public class "+className );        
    }
    
    /**
     * Add class imports to the DTO
     * @param clazz classes to import
     * @return this
     */
    public _autoDto imports( Class... clazz )
    {
        this.theClass.imports( (Object[]) clazz );
        return this;
    }
    
    
    public _autoDto properties( String...properties )
    {
        for( int i = 0; i < properties.length; i++ )
        {
            property( properties[ i ] );
        }
        return this;
    }
    /** 
     * Adds a field with class and name, and getter and setter 
     * (also imports the class if need be)
     * i.e.
     * <PRE>
     * _auto_dto myDto = _auto_dto.of( "MyDto" );
     * myDto.field( BigDecimal.class, "value" );
     *  // creates: 
     * 
     * //adds the import
     * import java.math.BigDecimal;
     * 
     * public class MyDto
     * {
     *    //adds the field
     *    <B>private BigDecimal value;
     * 
     *    //adds the getter
     *    public BigDecimal getValue()
     *    {
     *        return this.value;
     *    }
     *    //adds the setter
     *    public void setValue( BigDecimal value )
     *    {
     *        this.value = value;
     *    }</B>
     * }
     * </PRE>
     * @param clazz the class type of the property
     * @param name the name of the property
     * @return this (modified)
     */
    public _autoDto property( Class clazz, String name )
    {
        this.theClass.imports( clazz ); //import the clazz if necessary
        _field f = new _field( 
            _modifiers.of( "private" ), clazz.getCanonicalName(), name ); 
        this.theClass.getFields().addFields( f );  
        
        this.theClass.method( _autoMethodGetter.of( f ) ); 
        this.theClass.method( _autoMethodSetter.of( f ) ); 
        return this;        
    }
    
    /**
     * Creates a field along with getter and setter<PRE>
     * 
     * property( "private final String name;" );
     * 
     * @param fieldDef
     * @return this
     */
    public _autoDto property( String fieldDef )
    {
        //first add the field
        _field f = _field.of( fieldDef );
        this.theClass.field( f );
        
        this.theClass.method( _autoMethodGetter.of( f ) ); 
        if( ! f.getModifiers().contains( Modifier.FINAL ) ) 
        {
            this.theClass.method( _autoMethodSetter.of( f ) ); 
        }
        return this;        
    }
    
    /**
     * Constructs and returns a new "clone" dto _class
     * based on the current state of the _dto
     * (NOTE: the clone is Mutable, but changes to the
     * _class will not be reflected in the _dto)
     * 
     * the (constructor) for the dto is lazily constructed
     * based on the state of the properties of this _class
     * 
     * @return a constructed clone of the internal _class
     */
    public _class as_class()
    {
        _constructor constructor = _autoConstructor.of( theClass );
        
        _class dtoClass = _class.cloneOf( this.theClass );
        
        dtoClass.constructor( constructor );
        return dtoClass;
    }    
}
