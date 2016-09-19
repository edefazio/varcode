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
package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.Template;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/** List of One-Per-Line Annotations*/
public class _annotations
    extends Template.Base
{
    private List<Object> annList;
    
    public static final Dom ANNOTATION_LIST = 
        //BindML.compile( "{{+?code:{+code+}" + N + "+}}" );
        BindML.compile( "{{+:{+annotation+}" + N + "+}}" );
    
    public _annotations( )
    {
        this( new ArrayList<Object>() );
    }
    
    public _annotations( List<Object> annotations )
    {
        this.annList = new ArrayList<Object>();
    }
    
    public VarContext getContext()
    {
        return VarContext.of( "annotation", annList );
    }
        
    public _annotations add( Object...annotations )
    {
        for( int i = 0; i < annotations.length; i++ )
        {
            this.annList.add( annotations[ i ] );
        }
        return this;
    }
    
    @Override
    public _annotations replace( String target, String replacement )
    {
        List<Object> repList = new ArrayList<Object>();
        for( int i = 0; i < annList.size(); i++ )
        {
            Object o = annList.get( i );
            if( o instanceof String )
            {
                repList.add( ((String)o).replace( target, replacement ) );
            }
            else if (o instanceof Template.Base )
            {
                repList.add( ((Template.Base)o).replace( target, replacement ) );
            }
            else
            {
                repList.add( o.toString().replace( target, replacement ) );
            }
        }
        this.annList = repList;
        return this;
    }
    
    @Override
    public String author( Directive... directives )
    {
        if( ! isEmpty() )
        {
            return Author.code( ANNOTATION_LIST, getContext(), directives );
        }
        return "";
    }
        
    @Override
    public String toString()
    {
        return author();
    }
    
    public boolean isEmpty()
    {
        return this.annList.isEmpty();
    }
        
    public List<Object> getAnnotations()
    {
        return this.annList;
    }
    
    public int count()
    {
        return this.annList.size();
    }
    
    /**
     * <PRE>
     * @Entity(tableName = "vehicles")
     *         ^^^^^^^^^^^^^^^^^^^^^^
     * (1) annotation attribute
     * 
     * @Entity(tableName = "vehicles", primaryKey = "id")
     *         ^^^^^^^^^^^^^^^^^^^^^^  ^^^^^^^^^^^^^^^^^
     * (2) annotation attributes
     * </PRE>
     * 
     */
    public static class _attributes
        extends Template.Base
    {
        
        public static _attributes of( String... nameValues )
        {
            if( nameValues.length == 1 )
            {
                return new _attributes( null, nameValues[ 0 ] );
            }             
            return new _attributes( nameValues );
        }
        
        private List<String>names;
        private List<String>values;

        public _attributes()
        {            
            this.names = new ArrayList<String>();
            this.values = new ArrayList<String>();            
        }
        
        public int count()
        {
            return names.size();
        }
        
        public boolean isEmpty()
        {
            return count() == 0;
        }
        
        public _attributes( String... nameValues )
        {
            this.names = new ArrayList<String>();
            this.values = new ArrayList<String>();
            
            add( nameValues );            
        }
        
        public final _attributes add( String... nameValues )
        {
            if( nameValues.length % 2 != 0 )
            {
                throw new VarException(
                    "nameValues must be passed in as pairs");
            }
            for( int i = 0; i < (nameValues.length / 2); i++ )
            {
                this.names.add( nameValues[ i * 2 ] );
                this.values.add( nameValues[ (i * 2) + 1 ] );
            }
            return this;
        }
        
        @Override
        public _attributes replace( String target, String replacement )
        {
            for( int i = 0; i < names.size(); i++ )
            {
                names.set( i, names.get( i ).replace( target, replacement ) );
                values.set( i, values.get( i ).replace( target, replacement ) );
            }
            return this;
        }

        /** Single Annotation attribute value without name: 
         * @Table("value") as apposed to @Table(name = "value")
         */
        public static final Dom SINGLE_VALUE_ATTRIBUTE = 
            BindML.compile( "\"{+value*+}\"" );
            
        public static final Dom ATTRIBUTES = 
            BindML.compile( "{{+:{+name*+} = \"{+value*+}\", +}}" );
        
        @Override
        public String author( Directive... directives )
        {
            if( names.size() > 0 )
            {
                if( names.size() == 1 && names.get( 0 ) == null )
                {
                    return Author.code(
                        SINGLE_VALUE_ATTRIBUTE, 
                        VarContext.of( "value", values ), 
                        directives );
                }
                return Author.code(
                    ATTRIBUTES, 
                    VarContext.of( "name", names, "value", values ), 
                    directives );
            }
            return "";
        }
        
        public String toString()
        {
            return author();
        }        
    }
    
    public static class _annotation
        extends Template.Base
    {
        public static _annotation of( String...tokens )
        {
            if( tokens.length == 0 )
            {
                return new _annotation( null );
            }
            _annotation ann = new _annotation( tokens[ 0 ] );
            if( tokens.length > 1 )
            {
                String[] attributes = new String[ tokens.length -1];
                System.arraycopy(tokens, 1, attributes, 0, tokens.length - 1);
                ann.attributes( attributes );
            }
            return ann;
        }
        
        /** A String starting with @ */
        private String annotation;
        
        private _attributes attributes;
        
        public static final Dom ANNOTATION = 
            BindML.compile( "{{+?annotation:{+annotation+} +}}" );
        
        public static final Dom ANNOTATION_ATTRIBUTES = 
            BindML.compile( "{+annotation+}{{+?attributes:({+attributes+}) +}}" );
        
        public _annotation( String annotation )
        {
            if( annotation != null )
            {
                if( annotation.startsWith( "@" ) )
                {
                    this.annotation = annotation;
                }
                else
                {
                    this.annotation = "@" + annotation;
                }
            }
            
        }

        public boolean isEmpty()
        {
            return annotation == null;
        }
        
        public String getAnnotation()
        {
            return this.annotation;
        }
        
        @Override
        public _annotation replace(String target, String replacement)
        {
            if( this.annotation != null )
            {
                this.annotation = annotation.replace( target, replacement );
            }
            if( this.attributes != null )
            {
                this.attributes.replace( target, replacement );
            }
            return this;            
        }

        public _annotation attributes( String...attributes )
        {
            this.attributes = _attributes.of( attributes );
            return this;
        }
        
        @Override
        public String author( Directive... directives )
        {
            if( this.annotation == null )
            {
                return "";
            }
            if( this.attributes == null || this.attributes.isEmpty() )
            {
                return Author.code( ANNOTATION, 
                    VarContext.of( "annotation", annotation ), directives );
            }
            return Author.code( ANNOTATION_ATTRIBUTES, 
                    VarContext.of( "annotation", annotation, "attributes", attributes ), directives ); 
                    
        }        
        
        @Override
        public String toString()
        {
            if( this.isEmpty() )
            {
                return "";
            }
            return author( );
        }
    }
}        
