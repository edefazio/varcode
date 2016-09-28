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

import varcode.doc.translate.TranslateBuffer;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.translate.JavaTranslate;
import varcode.java.JavaCase;
import varcode.java.JavaNaming;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.code._code;
import varcode.java.code._enum;
import varcode.java.code._enum._valueConstructs;
import varcode.java.code._enum._valueConstructs._valueConstruct;
import varcode.java.code._fields;
import varcode.java.code._fields._field;
import varcode.java.code._implements;
import varcode.java.code._imports;
import varcode.java.code._modifiers;

/**
 * Simplified _enum model where the enum is populated with _properties
 * 
 * This is a Lazy API Model "on-top" of an existing API model (_enum).
 * 
 * We collect properties and lazily build the _enum model
 
 * <PRE>
 * _autoEnum auto = 
 *     _autoEnum.of( "ex.varcode.e.MyEnum" )
 *     .property( int.class, "age")
 *     .value( "Eric", 42 );
 * 
 * //creates the following enum source:
 * 
 * package varcode.ex.e;
 * 
 * public enum MyEnum
 * {
 *    Eric( 42 );
 * 
 *    private final int age;
 * 
 *    private MyEnum( int age )
 *    {
 *        this.age = age;
 *    }
 * 
 *    public int getAge()
 *    {
 *        return this.age;
 *    }
 * }
 * </PRE>
 * 
 * @author M. Eric DeFazio eric@carcode.io
 */
public class _autoEnum
    implements JavaCase.JavaCaseAuthor    
{ 
    private final String packageName;
    private final String className;
    
    private final _imports imports = new _imports();
    private final _fields fields = new _fields();
    
    private final _implements implementFrom = new _implements();
    
    /**
     * Constructors for enum values, i.e. <PRE>
     * public enum OCTAL { <B>_0, _1, _2, _3, _4, _5, _6, _7;</B> };
     * public enum OCTAL { <B>_0(0), _1(1), _2(2), _3(3), _4(4), _5(5), _6(6), _7(7);</B> };
     * </PRE>
     * NOTE: could be empty (if using the singleton enum idiom)
     */
    private final _valueConstructs valueConstructs = new _valueConstructs();
    
    /**
     * creates and returns an enum at the package and of the nmame provided
     * _auto_enum myEnum = _auto_enum.of( "ex.varcode.e.MyEnum" );
     * assertEquals( myEnum.getName(), "MyEnum");
     * assertEquals( myEnum.getPackageName(), "ex.varcode.e");
     * 
     * @param fullClassName
     * @return 
     */
    public static _autoEnum of( String fullClassName )
    {
         String[] packageAndClassName = 
            JavaNaming.ClassName.extractPackageAndClassName( fullClassName );
        
        return new _autoEnum( 
            packageAndClassName[ 0 ], packageAndClassName[ 1 ] );
    }
    
    public static _autoEnum of( String packageName, String className )
    {
        return new _autoEnum( packageName, className );
    }
    
    public _autoEnum( String packageName, String className )
    {
        this.packageName = packageName;
        this.className = className;       
    }
    
    
    public String getName()
    {
        return this.className; 
    }
    
    public String getPackageName()
    {
        return this.packageName;
    }
    
    /** 
     * Adds class imports to the enum 
     * @param clazz classes to import
     * @return this
     */
    public _autoEnum imports( Class... clazz )
    {
        this.imports.addImports( (Object[])clazz );
        return this;
    }
    
    public _autoEnum implement( Class...implementsClass )
    {
        this.implementFrom.implement(implementsClass);
        return this;
    }
    
    public _autoEnum implement( String...implement )
    {
        this.implementFrom.implement( implement );
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
    public _autoEnum property( Class clazz, String name )
    {
        this.imports.addImport( clazz ); //import the clazz if necessary
        _field f = new _field( 
            _modifiers.of( "private" ), JavaTranslate.INSTANCE.translate( clazz ), name ); 
        this.fields.addFields( f );  
        
        
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
    public _autoEnum property( String fieldDefinition )
    {
        _field f = _field.of( fieldDefinition );
        this.fields.addFields( f );           
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
        //_fields fields = this.iEnum.getFields();
        //String[] fieldNames = fields.getFieldNames();
        String paramList = "";
        _code finalInitCode = new _code();
        
        //dynamically create me an _enum 
        _enum derived = 
            _enum.of( this.packageName, "public enum " + this.className ); 
        if( this.implementFrom != null)
        {
            for(int i=0; i< this.implementFrom.count(); i++ )
            {
                derived.implement( this.implementFrom.get( i ) );
            }
        }
        derived.imports( this.imports.getImports().toArray() );
        
        
        for( int i = 0; i < fields.count(); i++ )
        {                        
            _field f = fields.getAt( i );
            
            //add the field
            derived.fields( f ) ;
            
            //add a getter method for the field
            derived.method(
               "public final " + JavaTranslate.INSTANCE.translate( f.getType() ) 
                  + " get" + firstUpper( f.getName() ) + "()",
               "return this." + f.getName() +";");            
            
            //update the constructor code and constructor parameters
            if( i > 0 )
            {
                paramList += ", ";
            }
            paramList +=  JavaTranslate.INSTANCE.translate( f.getType() ) + " " + f.getName();
            finalInitCode.addTailCode( 
                "this." + f.getName() + " = " + f.getName() + ";" );                                 
        }
        
        String constructorSig = 
            "private " + this.className + "( " + paramList + " )";
        derived.constructor( constructorSig, finalInitCode );
        derived.values( this.valueConstructs );
        
        return derived;
    }
    
    
    public Class loadClass( AdHocClassLoader adHoc )
    {
        return toJavaCase( ).loadClass( adHoc ); 
    }
    
    public Class loadClass()
    {
        return toJavaCase( ).loadClass(); 
    }
    
    @Override
    public JavaCase toJavaCase( Directive... directives )
    {
        return toJavaCase( null, directives );
    }

    @Override
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

    public _autoEnum value( String name, Object...values )
    {
        this.valueConstructs.addEnumValue( _valueConstruct.of( name, values ) );
        return this;
    }

}
