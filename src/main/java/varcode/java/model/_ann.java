/*
 * Copyright 2017 Eric.
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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import varcode.ModelException;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.context.VarContext;
import varcode.java.ast.FormatJavaCode_AllmanScanStyle;
import varcode.java.ast.JavaAst;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java._facet;
import varcode.java.naming.RefRenamer;
import varcode.markup.Template;
import varcode.markup.bindml.BindML; 
import varcode.translate.UnescapeJavaString;

/**
 * Revised Annotation instance
 * 
 * @author Eric
 */
public class _ann
    implements _Java, _facet, Authored
{
    public String name;    
    public _attributes attributes = new _attributes();
    
    public _ann()
    {        
    }
    
    public _ann( String name )
    {
        if( name.startsWith( "@" ) )
        {
            this.name = name.substring( 1 );
        }
        else
        {
            this.name = name;
        }
    }
    
    public _ann( _ann prototype )
    {
        this.name = prototype.name;
        this.attributes = new _attributes( prototype.attributes );
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getAttributeString( String key )
    {
        for(int i = 0; i< this.attributes.keys.size(); i++ )
        {
            if( this.attributes.keys.get( i ).equals( key ) )
            {
                return 
                    _attributes.parseStringArray( this.attributes.values.get( i ) )[ 0 ];
            }
        }
        return "";
    }
    
    public String[] getAttributeStringArray( String key )
    {
        for(int i = 0; i< this.attributes.keys.size(); i++ )
        {
            if( this.attributes.keys.get( i ).equals( key ) )
            {
                return _attributes.parseStringArray( this.attributes.values.get( i ) );
            }
        }
        return new String[ 0 ];
    }
    
    public String getLoneAttributeString()
    {
        if( this.attributes.values.size() == 0 )
        {
            return null;
        }
        if( this.attributes.values.size() == 1 )
        {
            return _attributes.parseStringArray( this.attributes.values.get( 0 ) )[ 0 ];
        }
        throw new ModelException(
            "More than one attribute in "+ this.author() );
    }
    
    //ASSUMES ONLY
    public String[] getLoneAttributeStringArray()
    {
        if( this.attributes.values.size() == 0 )
        {
            return null;
        }
        if( this.attributes.values.size() == 1 )
        {
            return _attributes.parseStringArray( this.attributes.values.get( 0 ) );
        }
        throw new ModelException(
            "More than one attribute in "+ this.author() );
    }
    public _attributes getAttributes()
    {
        return this.attributes;
    }
    
    public static _ann of( Class annotationClass )
    {
        if( annotationClass.isAnnotation() )
        {
            return of( "@" + annotationClass.getCanonicalName() );
        }
        throw new ModelException( 
            annotationClass + " is not an AnnotationClass " );
    }
    
    public static _ann of( String annotationDecl )
    {
        CompilationUnit astRoot = null;
        try
        {
            astRoot = 
                JavaAst.astFrom( annotationDecl + " class A{}" );
        }
        catch( ParseException pe )
        {
            throw new ModelException(
                "unable to parse annotation \""+ annotationDecl+"\"" );
        }
        AnnotationExpr astAnn = 
            astRoot.getTypes().get( 0 ).getAnnotations().get( 0 );
            
        
        //System.out.println( astAnn.getName() );
        List<Node> nodes = astAnn.getChildrenNodes(); //System.out.println(  );
        _ann _a = new _ann( nodes.get( 0 ).toString() );
        //_a.name = ;
        
        //System.out.println( _a );
        for( int i = 1; i < nodes.size(); i++ )
        {
            Node n = nodes.get( i );
            //System.out.println( "NODE " + i + " " + n );
            if( n instanceof MemberValuePair )
            {
                MemberValuePair mvp = (MemberValuePair)n;
                _a.addAttribute( mvp.getName(), mvp.getValue().toString() );                   
            }
            else
            {
                String s = n.toString().trim();
                if( s.length() > 0 )
                {
                    _a.addAttribute( n.toString() );                   
                }
            }
        }        
        return _a;
    }
    
    @Override
    public String toString()
    {
        return author();
    }

    
    public _ann addAttribute( String name, String value )
    {
        this.attributes.add( name, value);
        return this;
    }
    
    public _ann addAttribute( String value )
    {
        this.attributes.add( value );
        return this;
    }
    
    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    public static final Template ANNOTATION = BindML.compile("@{+name+}");
    
    public static final Template ANNOTATION_ATTRIBUTES = 
        BindML.compile("@{+name+}({+attributes+})" );    
    
    @Override
    public String author( Directive... directives )
    {
        return Author.toString( getTemplate(), getContext(), directives );
    }

    @Override
    public _ann replace( String target, String replacement )
    {
        this.name = RefRenamer.apply( name, target, replacement );
        this.attributes.replace( target, replacement );
        return this;
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        
    }

    @Override
    public Template getTemplate()
    {
        if( this.attributes.isEmpty() )
        {
            return ANNOTATION;
        }
        return ANNOTATION_ATTRIBUTES;
    }

    @Override
    public Context getContext()
    {
        return VarContext.of( "name", this.name, "attributes", this.attributes );
    }
    
    
    public static class _attributes
        implements _Java, _facet, Authored
    {
        public List<String> keys = new ArrayList<String>();
        public List<String>values = new ArrayList<String>();
        
        /**
         * gets the raw String value for a key
         * REMEMBER the RAW value might represent something other than a 
         * String (i.e. an int, a float, an int[], a String[])
         * 
         * @param key the key of the attribute
         * @return the Raw value associated with this key, or null
         */
        public String getRawValueForKey( String key )
        {
            for(int i=0; i< this.keys.size(); i++ )
            {
                if( keys.get( i ).equals( key ) )
                {
                    return values.get( i );
                }
            }
            return null;
        }
        
        /**
         * String[] Attributes within annotations can be signified as
         * one big String where internally the string can have 
         * quotes " ", 
         * braces '{' '}' 
         * commas ','
         * 
         * this method will parse the one big string representing the attribute
         * value to (as a String[])
         * <PRE>
         * for example
         * @fields("int a;")  //without braces {}
         * @fields({"int a;"}) //with braces{}
         * @fields({"int a;", "int b;", "int c;"}) //with braces
         * </PRE>
         * 
         * this method will "normalize" the String representation of the attribute
         * as a String array
         * 
         * @param array
         * @return 
         */
        public static String[] parseStringArray( String array )
        {
            try
            {
                array = array.trim();
                if( !array.startsWith( "{" ) )
                {
                    array = "{" + array + "}";
                }
                FieldDeclaration fd = 
                    JavaAst.astFieldFrom( "String s = " + array + ";" );
            
                Expression expr = fd.getVariables().get( 0 ).getInit();
            
                List<Node> nodes = expr.getChildrenNodes();
                String[] values = new String[nodes.size()];
                for( int i = 0; i < nodes.size(); i++ )
                {
                    String s = nodes.get( i ).toString();
                    s = UnescapeJavaString.unescapeJavaString( s );
                    values[ i ] = s.substring( 1, s.length() - 1 );
                }
                return values;
            }
            catch( ParseException e )
            {
                throw new ModelException(" bad array representation "+ array, e );
            }            
        }
        
        /** Single Annotation attribute value without name: 
        * @Table("value") as apposed to @Table(name = "value")
        */
        public static final Template SINGLE_VALUE_ATTRIBUTE =         
            BindML.compile( "{+value*+}" );
        
            
        public static final Template ATTRIBUTES = 
            BindML.compile( "{{+:{+name*+} = {+value*+}, +}}" );
    
        public _attributes()
        {            
        }
        
        public _attributes( _attributes prototype )
        {
            for( int i = 0; i < prototype.keys.size(); i++ )
            {
                this.keys.add( prototype.keys.get( i ) );
            }
            for( int i=0; i< prototype.values.size(); i++ )
            {
                this.values.add( prototype.values.get( i ) );
            }
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( this.keys, this.values );
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
            final _attributes other = (_attributes)obj;
            if( this.keys != other.keys && (this.keys == null || !this.keys.equals( other.keys )) )
            {
                return false;
            }
            if( this.values != other.values && (this.values == null || !this.values.equals( other.values )) )
            {
                return false;
            }
            return true;
        }
        
        @Override
        public _attributes replace( String target, String replacement )
        {
            for( int i = 0; i < this.keys.size(); i++ )
            {
                this.keys.set( i, RefRenamer.apply( this.keys.get( i ), target, replacement ) );
            }
            for(int i = 0; i < this.values.size(); i++ )
            {
                this.values.set( i, RefRenamer.apply( this.values.get( i ), target, replacement ) );
            }            
            return this;
        }
    
        public int count()
        {
            return Math.max( keys.size(), values.size() );
        }
        
        public boolean isEmpty()
        {
            return count() == 0;
        }
        
        public _attributes add( String valueOnly )
        {
            values.add( valueOnly );
            return this;
        }
        
        public _attributes add( String key, String value )
        {
            keys.add( key );
            values.add( value );
            return this;
        }
        
        @Override
        public String toString()
        {
            return author();
        }

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public String author( Directive... directives )
        {
            return Author.toString( getTemplate(),getContext(), directives );
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            
        }

        @Override
        public Template getTemplate()
        {
            if( isEmpty() )
            {
                return Template.EMPTY;
            }
            if( this.keys.size() < this.values.size() )
            {
                return SINGLE_VALUE_ATTRIBUTE;
            }
            return ATTRIBUTES;
        }

        @Override
        public Context getContext()
        {
            return VarContext.of( "name", this.keys, "value", this.values );
        }
    }
    
    
    public static final _ann SAFE_VARARGS = 
        _ann.of( SafeVarargs.class );
        
    public static final _ann FUNCTIONAL_INTERFACE = 
        _ann.of( FunctionalInterface.class );
        
    public static _ann DEPRECATED = 
        _ann.of( Deprecated.class );
        
    public static _ann OVERRIDE = 
        _ann.of( Override.class );
        
    public static _ann SUPPRESS_WARNINGS = 
        _ann.of( SuppressWarnings.class );
}
