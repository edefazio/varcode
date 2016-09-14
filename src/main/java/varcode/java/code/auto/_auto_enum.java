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

import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.java.JavaCase;
import varcode.java.JavaNaming;
import varcode.java.code._code;
import varcode.java.code._enum;
import varcode.java.code._fields;
import varcode.java.code._fields._field;
import varcode.java.code._modifiers;

/**
 * simplified _enum model where each enum value has _properties
 * 
 * each _property is 
 * created as a private final _field
 * a getter is created
 * a parameter is added to the constructor
 * 
 * @author eric
 */
public class _auto_enum
    implements JavaCase.JavaCaseAuthor    
{
    //this is the enum which stores the fields added
    private final _enum iEnum;
    
    /**
     * creates and returns an enum at the package and of the nmame provided
     * _auto_enum myEnum = _auto_enum.of( "ex.varcode.e.MyEnum" );
     * assertEquals( myEnum.getName(), "MyEnum");
     * assertEquals( myEnum.getPackageName(), "ex.varcode.e");
     * 
     * @param fullClassName
     * @return 
     */
    public static _auto_enum of( String fullClassName )
    {
         String[] packageAndClassName = 
            JavaNaming.ClassName.extractPackageAndClassName( fullClassName );
        
        return new _auto_enum( 
            packageAndClassName[ 0 ], packageAndClassName[ 1 ] );
    }
    
    public _auto_enum( String packageName, String className )
    {
        this.iEnum = _enum.of( packageName, "public enum " + className );          
    }
    
    
    public String getName()
    {
        return iEnum.getName();
    }
    
    public String getPackageName()
    {
        return iEnum.getPackageName();
    }
    
    /** 
     * Adds class imports to the enum 
     * @param clazz classes to import
     * @return this
     */
    public _auto_enum imports( Class... clazz )
    {
        this.iEnum.imports( (Object[]) clazz );
        return this;
    }
    
    private static final String firstUpper( String s )
    {
        return Character.toUpperCase( s.charAt( 0 ) ) + s.substring( 1 );
    }
    
    /** 
     * Adds: 
     * <UL>
     * <LI>a private final field with type class and name, 
     * <LI>a getter for the field
     * <LI>a parameter in the enum constructor
     * <LI>code in the enum constructor to set the field
     * <LI>(also imports the class if need be)
     * i.e. <PRE>
     * auto_enum myEnum = _autoEnum.of( "MyEnum" );
     * myEnum.property( BigDecimal.class, "value" );
     * 
     * // represents : 
     * 
     * //adds the import
     * import java.math.BigDecimal;
     * 
     * public enum MyEnum
     * {
     *    //adds the field
     *    <B>private final BigDecimal value;
     * 
     *    //adds a parameter to the constructor
     *    private enum MyEnum( BigDecimal value )
     *    {
     *         this.value = value;
     *    }
     * 
     *    //adds the getter
     *    public BigDecimal getValue()
     *    {
     *        return this.value;
     *    }
     *  </B>
     * }
     * </PRE>
     */
    public _auto_enum property( Class clazz, String name )
    {
        this.iEnum.imports( clazz ); //import the clazz if necessary
        _field f = new _field( 
            _modifiers.of( "private" ), clazz.getCanonicalName(), name ); 
        this.iEnum.getFields().addFields( f );  
        
         //add the getter
        this.iEnum.method(
            "public " + f.getType() + " get" + firstUpper( f.getName() ) + "()",
            "return this." + f.getName() +";");            
        return this;        
    }
    
    /** 
     * Adds: 
     * <UL>
     * <LI>a private final field with type class and name, 
     * <LI>a getter for the field
     * <LI>a parameter in the enum constructor
     * <LI>code in the enum constructor to set the field
     * <LI>(also imports the class if need be)
     * i.e. <PRE>
     * auto_enum myEnum = _autoEnum.of( "MyEnum" );
     * myEnum.property( "private final BigDecimal value;" );
     *  // creates: 
     * 
     * 
     * public enum MyEnum
     * {
     *    //adds the field
     *    <B>private final BigDecimal value;
     * 
     *    //adds a parameter to the constructor
     *    private enum MyEnum( BigDecimal value )
     *    {
     *         this.value = value;
     *    }
     * 
     *    //adds the getter
     *    public BigDecimal getValue()
     *    {
     *        return this.value;
     *    }
     *  </B>
     * }
     * </PRE>
     */
    public _auto_enum property( String fieldDefinition )
    {
        _field f = _field.of( fieldDefinition );
        this.iEnum.fields( f );
        
         //add the getter
        this.iEnum.method(
            "public " + f.getType() + " get" + firstUpper( f.getName() ) + "()",
            "return this." + f.getName() +";");             
        return this;
    }
    
    /**
     * Gets a clone of the internal Enum that is being built
     * (REMINDER: if you change things in the clone they will 
     * NOT be reflected in this _auto_enum)
     * 
     * @return a constructed deep clone of the enum
     */
    public _enum getEnum()
    {
        _fields fields = this.iEnum.getFields();
        String[] fieldNames = fields.getFieldNames();
        String paramList = "";
        _code finalInitCode = new _code();
            
        for( int i = 0; i < fieldNames.length; i++ )
        {
            _field f = fields.getByName( fieldNames[ i ] );
            if( i > 0 )
            {
                paramList += ", ";
            }
            paramList +=  f.getType() + " " + f.getName();
            finalInitCode.addTailCode( 
                "this." + f.getName() + " = " + f.getName() + ";" );                                 
        }
        _enum derived = _enum.from( this.iEnum );
        String constructorSig = 
            "private " + this.iEnum.getSignature().getName() + "( "+paramList+" )";
        derived.constructor( constructorSig, finalInitCode );
        return derived;
    }
    
    public JavaCase toJavaCase( Directive... directives )
    {
        return toJavaCase( null, directives );
    }

    public JavaCase toJavaCase( VarContext context, Directive... directives )
    {
        //build a clone of the enum (adding in any constructors)
        _enum derived = getEnum();  
        
        if( context == null )
        {
            return derived.toJavaCase( directives );
        }
        else
        {
            return derived.toJavaCase( context, directives );
        }        
    }    

    public _auto_enum value( String name, Object...values )
    {
        this.iEnum.value( name, values );
        return this;
    }
}
