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
package varcode.java.code.auto;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.java.JavaCase;
import varcode.java.JavaCase.JavaCaseAuthor;
import varcode.java.JavaNaming;
import varcode.java.code._class;
import varcode.java.code._code;
import varcode.java.code._fields;
import varcode.java.code._fields._field;
import varcode.java.code._modifiers;

/**
 * Creates an ad-hoc dto (Data Transfer Object)
 * 
 * Data Transfer Objects are similar to "structs" in Java
 * they have private fields that can be accessed (read, updated)
 * using getters and setters.
 * 
 * Final Fields (that have no init)  MUST be passed in on the constructor i.e.
 * <UL>
 *   <LI>"private final int a;" <-- this is a final field with no init 
 *    (and a value for a must be passed in in constructor)
 *   <LI>"private final int b = 100;" <-- this is a fienal field with an init
 *</UL>  
 * Final fields have only a getter (cant be set)
 * 
 * NOTE: 
 */
public class _auto_dto
    implements JavaCaseAuthor
{
    private _class theClass;
    
    /**
     * Create a dto at the full pathName:<PRE>
     *  _auto_dto.of("ex.varcode.dto.MyDto");</PRE>
     * 
     * @param fullClassName
     * @return 
     */
    public static _auto_dto of( String fullClassName )
    {
         String[] packageAndClassName = 
            JavaNaming.ClassName.extractPackageAndClassName( fullClassName );
        
        return new _auto_dto( packageAndClassName[ 0 ], packageAndClassName[ 1 ] );
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
    public _auto_dto( String packageName, String className )
    {
       this.theClass = _class.of( packageName, "public class "+className );        
    }
    
    private static final String firstUpper( String s )
    {
        return Character.toUpperCase( s.charAt(0) ) + s.substring( 1 );
    }
    
    /**
     * Add class imports to the DTO
     * @param clazz classes to import
     * @return this
     */
    public _auto_dto imports( Class... clazz )
    {
        this.theClass.imports( clazz );
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
     */
    public _auto_dto property( Class clazz, String name )
    {
        this.theClass.imports( clazz ); //import the clazz if necessary
        _field f = new _field( 
            _modifiers.of("private"), clazz.getCanonicalName(), name ); 
        this.theClass.getFields().addFields( f );  
        
         //add the getter
        this.theClass.method(
            "public " + f.getType() + " get" + firstUpper( f.getName() )+"()",
            "return this." + f.getName() +";");    
        
        //add a setter        
        this.theClass.method(
            "public void set" + firstUpper( f.getName() ) + "( " 
                    + f.getType() + " " + f.getName()+" )",
            "this." + f.getName() + " = " + f.getName() + ";" );    
        
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
    public _auto_dto property( String fieldDef )
    {
        //first add the field
        _field f = _field.of( fieldDef );
        this.theClass.field( f );
        
        //add the getter
        this.theClass.method(
            "public "+f.getType() + " get" + firstUpper( f.getName() )+"()",
            "return this." + f.getName() + ";" );    
        
        //if the field is Final, if NOT add a setter        
        if( !f.getModifiers().contains( Modifier.FINAL ) )
        {
            this.theClass.method(
            "public void set" + firstUpper( f.getName() ) + "( " +f.getType()+" "+f.getName()+" )",
            "this." + f.getName() + " = " + f.getName() + ";" );    
        }        
        return this;        
    }
    
    /**
     * Constructs and returns a new "clone" dto class
     * based on the current state of the _dto
     * (NOTE: the clone is Mutable, but changes to the
     * _class will not be reflected in the _dto)
     * 
     * the (constructor) for the dto is lazily constructed
     * based on the state of the properties of this _class
     * 
     * @return a constructed clone of the internal _class
     */
    public _class getDtoClass()
    {
        //we need a constructor with all the uninitialized final fields
        //verify that all fields are either
        // nonfinal and they are final With an initializer
        List<_field>uninitializedFinalFields = new ArrayList<_field>();
        
        _fields fs = this.theClass.getFields();
        String[] fieldNames = this.theClass.getFields().getFieldNames();
        
        for( int i = 0; i < fieldNames.length; i++ )
        {
            _field f = fs.getByName( fieldNames[ i ] );
            if( f.getModifiers().contains( Modifier.FINAL ) && 
                    !f.hasInit() )
            {
                uninitializedFinalFields.add( f );
            }
        }
        
        String paramList = "";
        _code finalInitCode = new _code();
        
        for( int i = 0; i < uninitializedFinalFields.size(); i++ )
        {
            _field f = uninitializedFinalFields.get( i );
            if( i > 0 )
            {
                paramList += ", ";
            }
            paramList +=  f.getType() + " " + f.getName();
            
            finalInitCode.addTailCode( 
                "this." + f.getName() + " = " + f.getName()+";" );
        }
        _class dtoClass = _class.cloneOf( this.theClass );
            
        String constructorSig = 
            "public " + this.theClass.getSignature().getName() 
                + "( " + paramList + " )";
        
        dtoClass.constructor( constructorSig, finalInitCode );
        return dtoClass;
    }
    
    @Override
    public JavaCase toJavaCase( Directive... directives )
    {
        return toJavaCase( null, directives );
    }

    @Override
    public JavaCase toJavaCase( VarContext context, Directive... directives )
    {
        _class dtoClass = getDtoClass();
        if( context == null )
        {
            return dtoClass.toJavaCase( directives );
        }
        else
        {
            return dtoClass.toJavaCase( context, directives );
        }
    }    
}
