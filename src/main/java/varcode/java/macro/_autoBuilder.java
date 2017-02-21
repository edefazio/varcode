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
package varcode.java.macro;

import varcode.java.model._class;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.java.model._parameters;
import varcode.java.model._parameters._parameter;
import varcode.translate.JavaTranslate;

/**
 * Creates a static inner "Builder" class for the model i.e.
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
 * </PRE> Useful for Initialized "Immutable" Data Classes that have many fields
 * initialized with the constructor. (The Builder class can avoid long argument
 * lists)
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum _autoBuilder
    implements _autoApply
{
    INSTANCE;

    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }

    /**
     *
     * EITHER _target has EXACTLY 1 constructor
     *
     * or NO constructor (i.e. a default constructors)
     *
     * @param _target the target _class to add the builder to
     * @return the modified _class, note: if no eligible fields are found, a
     * builder is NOT created
     */
    public static _class to( _class _target )
    {
        _class _builderClass = null;
        if( _target.getConstructors().count() == 1 )
        {   //create the builder class that will call the constructor on build();
            _builderClass
                = of( _target.getConstructors().getAt( 0 ) );
        }
        else
        {   //the builder will set each field on build();
            _builderClass = of( _target.getName(), _target.getFields() );
        }

        //the builder class has no fields, so dont add the builder
        if( _builderClass.getFields().count() > 0 )
        {
            //nest the builder class within the taregt class
            _target.nest( _builderClass );

            //create a method to create and return a new instance of the builder
            _target.method( "public static " + _builderClass.getName() + " builder()",
                "return new " + _builderClass.getName() + "();" );
        }
        return _target;
    }

    /**
     * This builder
     *
     * @param className
     * @param _fs
     * @return
     */
    public static _class of( String className, _fields _fs )
    {
        String builderClassName = className + "Builder";
        _class _builder = _class.of( "public static class " + builderClassName );

        //the build method within the Builder (returns an instance of the target class
        _method _buildMethod = _method.of(
            "public " + builderClassName + " build()",
            builderClassName + " beingBuilt = new " + builderClassName + "(  );" );

        for( int i = 0; i < _fs.count(); i++ )
        {
            _field _f = _fs.getAt( i );

            String name = _f.getName();

            //add a field to store and a method to update
            _builder.field( "private " + _f.getType() + " " + name );
            _builder.method(
                "public " + builderClassName + " " + name + "( " + _f.getType() + " " + name + " )",
                "this." + name + " = " + name + ";",
                "return this;" );
            _buildMethod.add( "beingBuilt." + name + " = " + name + ";" );
        }
        _buildMethod.add( "return beingBuilt;" );
        _builder.method( _buildMethod );

        return _builder;
    }

    /**
     * Creates and returns a "Builder" _class that populates the
     *
     * @param _ctor
     * @return
     */
    public static _class of( _constructor _ctor )
    {
        _parameters _ps = _ctor.getParameters();

        String builderClassName = _ctor.getName() + "Builder";
        _class _builder = _class.of( "public static class " + builderClassName );

        //the build method within the Builder (returns an instance of the target class
        _method _buildMethod = _method.of(
            "public " + _ctor.getName() + " build()",
            "return new " + _ctor.getName() + "( {+args+} );" );

        for( int i = 0; i < _ps.count(); i++ )
        {
            _parameter _p = _ps.getAt( i );

            String name = _p.getName();

            _builder.field( "private " + _p.getType() + " " + _p.getName() );
            _builder.method(
                "public " + builderClassName + " " + name + "( " + _p.getType() + " " + name + " )",
                "this." + name + " = " + name + ";",
                "return this;" );
        }
        _builder.add(
            _buildMethod.replace(
                "{+args+}",
                JavaTranslate.INSTANCE.translate( _ps.getNames().toArray( new String[ 0 ] ) )
            ) );

        return _builder;
    }
}
