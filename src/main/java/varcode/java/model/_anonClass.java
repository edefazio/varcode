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
package varcode.java.model;

import java.lang.reflect.Type;
import java.util.Objects;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.context.VarContext;
import varcode.java.lang.RefRenamer;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.java.model._Java.Authored;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 * meta model of an anonymous class
 * for example:
 * 
 * _anonClass _aStringComparator = _anonClass.of( "Comparator<String>" )
 *     .method("@Override", 
 *         "public int compare( String a, String b )",
 *         "return 
 * 
 * @author M. Eric DeFazio
 */
public class _anonClass
    implements _Java._facet, _Java, Authored
{
    //the type being implemented or extended
    private String implType;
    private _args args = new _args();
    private _fields fields = new _fields();
    private _methods methods = new _methods();

    public static final String N = System.lineSeparator();

    public String getImplType()
    {
        
        return implType;
    }
    
    public _args getArgs()
    {
        return this.args;
    }
    
    public _fields getFields()
    {
        return this.fields;
    }
    
    public _methods getMethods()
    {
        return this.methods;
    }
    public static _anonClass of( Class clazz )
    {
        return new _anonClass( clazz );
    }
    
    public static _anonClass of( String type )
    {
        return new _anonClass( type );
    }
    
    public static _anonClass of( Type type )
    {
        return new _anonClass( type );
    }
    
    public Template ANONYMOUS_INSTANCE = BindML.compile(
        "new {+implType*+}{+args+}" + N
        + "{" + N
        + "{{+?fields:{+$>(fields)+}" + N
        + "+}}"
        + "{{+?methods:{+$>(methods)+}" + N
        + "+}}" + N
        + "}" );

    public Context getContext()
    {
        return VarContext.of( 
            "implType", implType, 
            "args", this.args,
            "fields", this.fields,
            "methods", this.methods );
    }
    
    public _anonClass( _anonClass prototype )
    {
        args = _args.cloneOf( prototype.args );
        fields = _fields.cloneOf( prototype.fields );
        methods = _methods.cloneOf( prototype.methods );
    }
    
    public static _anonClass cloneOf( _anonClass prototype )
    {
        return new _anonClass( prototype );
    }
    
    public _anonClass( Type type )
    {
        this.implType = type.toString();
    }
    
    public _anonClass( Class type )
    {
        this.implType = type.getCanonicalName();
    }
    
    public _anonClass( String implType )
    {
        this.implType = implType;
    }
    
    public Template getTemplate()
    {
        return ANONYMOUS_INSTANCE;
    }
        
    public _anonClass args( Object...args )
    {
        this.args.addArguments( args );
        return this;
    }
    
    public _anonClass field( _field field )
    {
        this.fields.add( field );
        return this;
    }
    
    public _anonClass fields( String...fields )
    {
        this.fields.add( fields );
        return this;
    }
    
    public _anonClass method( _method method )
    {
        this.methods.add( method );
        return this;
    }
    
    public _anonClass method( Object...parts )
    {
        this.methods.add( 
            _method.of( parts ) );
        return this;
    }
    
    @Override
     public int hashCode()
    {
        return Objects.hash( implType, args, fields, methods );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
        {
            return true;
        }
        if( obj == null )
        {
            return false;
        }
        if( getClass() != obj.getClass() )
        {
            return false;
        }
        final _anonClass other = (_anonClass)obj;
        if( !Objects.equals( this.implType, other.implType ) )
        {
            return false;
        }
        if( !Objects.equals( this.args, other.args ) )
        {
            return false;
        }
        if( !Objects.equals( this.fields, other.fields ) )
        {
            return false;
        }
        if( !Objects.equals( this.methods, other.methods ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String author()
    {
       return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( getTemplate(), getContext(), directives );
    }

    @Override
    public String toString()
    {
        return author();
    }
    
    @Override
    public _anonClass replace( String target, String replacement )
    {
        this.args.replace( target, replacement );
        this.fields.replace( target, replacement );
        //this.genericTypeParam.replace( target, replacement );
        this.implType = RefRenamer.apply( this.implType, target, replacement );
        this.methods.replace( target, replacement );
        return this;
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

}
