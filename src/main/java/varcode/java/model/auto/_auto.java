/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.java.model.auto;

import varcode.java.naming.ClassName;
import varcode.java.model._class;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.java.model.auto._autoApply._autoMacro;

/**
 * API for applying "Automatic Programming" / Macros  to _class models.
 * 
 * The main purpose is to <B>cut down on boilerplate</B> and streamline
 * the creation of new _class models.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _auto
{   
    
    public static final _autoBuilder BUILDER = _autoBuilder.INSTANCE;
    public static final _autoConstructor CONSTRUCTOR = _autoConstructor.INSTANCE;
    public static final _autoEquals EQUALS = _autoEquals.INSTANCE;
    public static final _autoExternalizable EXTERNALIZABLE = _autoExternalizable.INSTANCE;
    
    public static final _autoGetters GETTERS = _autoGetters.INSTANCE;
    public static final _autoSetters SETTERS = _autoSetters.INSTANCE;
    public static final _autoSettersFluent SETTERS_FLUENT = _autoSettersFluent.INSTANCE;
    public static final _autoHashCode HASHCODE = _autoHashCode.INSTANCE;
    public static final _autoMethodJavadocs METHOD_JAVADOCS = _autoMethodJavadocs.INSTANCE;
    public static final _autoToString TO_STRING = _autoToString.INSTANCE;


    
    public static class macro
    {
        /**
         * A MACRO to apply to a _class to make it an 
         * Immutable Data Class
         */
        public static final _autoMacro IMMUTABLE_DATA_CLASS =         
            macro( CONSTRUCTOR, BUILDER, EQUALS, HASHCODE, GETTERS, TO_STRING );
        
        public static final _autoMacro DATA_CLASS =
            macro( GETTERS, SETTERS, EQUALS, HASHCODE, TO_STRING );
    }
    /**
     * Returns an instance of an _autoMacro 
     * (an ordered list of autoprogramming macros)
     * that can be applied to a _class
     * 
     * @param applyInOrder
     * @return 
     */
    public static _autoMacro macro( _autoApply...applyInOrder )
    {
        return new _autoMacro( applyInOrder );
    }
    
    
    public static _class defaultNoArgCtorTo( _class _c )
    {
        return _autoConstructor.defaultNoArgTo( _c );
    }

    public static void defaultNoArgCtor( _class _c )
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static To to( _class _c )
    {
        To t = new To( _c );
        return t;
    }
    
    public static class To
    {
        private _class _c;
        
        public To( _class _c )
        {
            this._c = _c;
        }
        
        public _class apply( _autoApply...applyInOrder )
        {
            for( int i = 0; i < applyInOrder.length; i++ )
            {
                System.out.println( "APPLYING " + applyInOrder[ i ] );
                _c = applyInOrder[ i ].apply( _c );
            }
            return _c;
        }
    }
    /**
     * _auto.dataClass("ex.varcode.auto.MyClass", "int a", "String name" );
     * will create a _class model:
     * 
 package ex.varcode.auto;
 
 public class MyClass
 {
     int a;
     String name;
     
     public int getA()
     {
         return a;
     }
 
     public String getName()
     {
        return name;
     }
 
     public void setA( int a )
     {
         this.a = a;
     }
 
     public void setName( String name )
     {
         this.name = name;
     }
 
     public boolean equals( Object o )
     { ... }
      
     public int hashCode()
     { ... }
      
     public String toString()
     { ... }
}
     * 
     * @param qualifiedName the fully qualified name of the class
     * @param fields Strings representing each of the fields
     * @return a _class in the appropriate package, with fields, 
     * and auto methods (getters, setters, hascode, equals, toString)
     * and the appropriate constructor
     */
    public static _class dataClass( String qualifiedName, String... fields )
    {        
        String[] pkgClass = ClassName.extractPackageAndClassName( qualifiedName );
        _class _c = _class.of( pkgClass[ 1 ] );
        if( pkgClass[ 0 ] != null )
        {
            _c.packageName( pkgClass[ 0 ] );
        }
        //add all the fields, AND all the getters and setters
        _c.properties( fields );
        
        equalsTo( _c );   //create an equals method
        hashCodeTo( _c ); //create a hashcode method
        constructorTo( _c ); //create a constructor
        
        return _c;
    }
    
    /**
     * Creates a static inner "Builder" class for the model
     * i.e.
     * <PRE>
     * //an immutable data class
     * _class _c = _class.of("ImmutableClass").import(Date.class)
     *     .field("final int a")
     *     .field("final String name")
     *     .field("final Date birthDate")
     * 
     * _auto.constructorTo( _c ); //create a constructor for all final fields
     * 
     * _auto.builderTo( _c ); //this will create a builder class
     * 
     * // ----- PRODUCES THE CODE BELOW-----/
     * import java.util.Date;
     * 
     * public class ImmutableClass
     * {
     *     final int a;
     *     final String name;
     *     final Date birthDate;
     *     
     *     // Auto generated constructor from _auto.constructorTo(...);
     *     public ImmutableClass( int a, String name, Date birthDate )
     *     {
     *         this.a = a;
     *         this.name = name;
     *         this.birthDate = birthDate;
     *     }
     *     
     *     //Auto generated builder accessor from _auto.builderTo(...);
     *     public static ImmutableClassBuilder builder()
     *     {
     *         return new ImmutableClassBuilder();
     *     }
     * 
     *     //Auto generated builder from _auto.builderTo(...)
     *     public static class ImmutableClassBuilder
     *     {
     *         private int a;
     *         private String name;
     *         private Date birthDate;
     *         
     *         public ImmutableClassBuilder a( int a )
     *         {
     *              this.a = a;
     *              return this;
     *         }
     *         public ImmutableClassBuidler name( String name )
     *         {
     *               this.name = name;
     *               return this;
     *         }
     * 
     *         public ImmutableClassBuilder birthDate( Date birthDate )
     *         {
     *              this.birthDate = birthDate;
     *              return this;
     *         }
     * 
     *         public ImmutableClass build()
     *         {
     *              return new ImmutableClass( a, name, birthDate );
     *         }
     *     }
     * }
     * 
     * </PRE>
     * Useful for Initialized "Immutable" Data Classes that have many fields 
     * initialized with the constructor. (The Builder class can avoid long 
     * argument lists)
     * 
     * @param _c the class to build the inner builder class for and accessor method
     * @return the modified _class with the builder integrated
     */
    public static _class builderTo( _class _c )
    {
        return _autoBuilder.to( _c );
    }
    
    public static _class hashCodeTo( _class _c )
    {
        return _autoHashCode.to( _c );
    }
    
    public static _method getter( _field _f )
    {
        return _autoGetters.of( _f );
    }
    
    public static _method getter( Object type, Object name )
    {
        return _autoGetters.of( type, name );
    }
   
    public static _class gettersTo( _class _c )
    {
    
        return _autoGetters.to( _c );
    }
    
    public static _method setter( _field _f )
    {
        return _autoSetters.of( _f );
    }
    
    public static _method setter( Object type, String name )
    {
        return _autoSetters.of( type, name );
    }
    
    public static _method setFluent( String className, Object type, String name )
    {
        return _autoSettersFluent.of( className, type, name );
    }
    
    public static _class setFluentTo( _class _c )
    {
        return _autoSettersFluent.to( _c );
    }
    
    public static _class constructorTo( _class _c )
    {
        return _c.add( constructor( _c ) );
    }
    
    /**
     * Builds and returns a _constructor that looks at the FINAL
     * UNINITIALIZED fields on the _class and creates parameters
     * and a constructor to ensure these fields are initialized
     * 
     * @param _c a class
     * @return a 
     */
    public static _constructor constructor( _class _c )
    {
        return _autoConstructor.of( _c );
    }
    
    public static _constructor constructor( _enum _e )
    {
        return _autoConstructor.of( _e );
    }
    
    public static _constructor constructor( String name, _fields _fields )
    {
        return _autoConstructor.of( name, _fields );
    }
     
    public static _class externalizeTo( _class _c )
    {
        return _autoExternalizable.to( _c );
    }

    public static _class equalsTo( _class _c )
    {
        return _c.add( _autoEquals.of( _c ) );
    }
    
    public static _method equals( _class _c )
    {
        return _autoEquals.of( _c );
    }
    
    public static _method equals( String className, _field...fields )
    {
        return _autoEquals.of( className, fields );
    }
    
    public static _method equals( String className, _fields fields )
    {
        return _autoEquals.of( className, fields );
    }

}
