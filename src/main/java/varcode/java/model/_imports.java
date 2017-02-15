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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.naming.RefRenamer;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.java.model._Java._facet;
import varcode.markup.bindml.BindML;
import varcode.ModelException;
import varcode.java.naming.ClassNameQualified;

/**
 * Handles imports
 *
 * "*" imports? static imports? inner static class imports? array SomeClass[]?
 * generic input
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _imports      
    implements _Java, _facet, Countable, Authored
{
    public _imports( _imports prototype )
    {
        if( prototype != null )
        {
            this.importClasses.addAll( prototype.importClasses );
            this.staticImports.addAll( prototype.staticImports );
        }
    }
    
    public _imports()
    {        
    }
    
    /**
     * Create and return a mutable clone given the imports
     *
     * @param prototype the prototype imports
     * @return a mutable clone
     */
    public static _imports cloneOf( _imports prototype )
    {
        return new _imports( prototype );
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    /**
     * Matches and returns all imports (static or otherwise) that
     * contain the target text
     * 
     * @param targetText the target text
     * @return a List of import Strings matching the targetText
     */
    public List<String> match( String targetText )
    {
        List<String> matches = new ArrayList<String>();
        String[] importArr = this.importClasses.toArray( new String[0]);
        for( int i = 0; i < importArr.length; i++ )
        {
            if( importArr[ i ].contains( targetText ) )
            {
                matches.add( importArr[ i ] );
            }
        }
        String[] importStaticArr = this.staticImports.toArray( new String[0]);
        for( int i = 0; i < importStaticArr.length; i++ )
        {
            if( importStaticArr[ i ].contains( targetText ) )
            {
                matches.add( importStaticArr[ i ] );
            }
        }
        return matches;        
    }
    
    public _imports remove( List<String> toRemove )
    {
        for( int i=0; i< toRemove.size(); i++ )
        {
            this.importClasses.remove( toRemove.get( i ) );
            this.staticImports.remove( toRemove.get( i ) );
        }
        return this;
    }
    
    public _imports remove( String...toRemove )
    {
        for( int i=0; i< toRemove.length; i++ )
        {
            this.importClasses.remove( toRemove[ i ] );
            this.staticImports.remove( toRemove[ i ] );
        }
        return this;
    }
    
    public boolean containsAll( Class... imports )
    {
        for( int i = 0; i < imports.length; i++ )
        {
            if( !this.importClasses.contains( imports[ i ].getCanonicalName() ) )
            {
                return false;
            }
        }
        return true;
    }

    public boolean containsAll( String... imports )
    {
        for( int i = 0; i < imports.length; i++ )
        {
            if( !this.importClasses.contains( imports[ i ] ) )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( importClasses, staticImports );
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
        final _imports other = (_imports)obj;
        if( !Objects.equals( this.staticImports, other.staticImports ) )
        {
            return false;
        }
        if( !Objects.equals( this.importClasses, other.importClasses ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public _imports replace( String target, String replacement )
    {
        Set<String> replaced = new TreeSet<String>();
        String[] classes = this.importClasses.toArray( new String[ 0 ] );
        for( int i = 0; i < classes.length; i++ )
        {
            //replaced.add( classes[ i ].replace( target, replacement ) );
            replaced.add( RefRenamer.apply( classes[ i ], target, replacement ) );
        }
        this.importClasses = replaced;

        Set<String> replacedStatic = new TreeSet<String>();
        classes = this.staticImports.toArray( new String[ 0 ] );
        for( int i = 0; i < classes.length; i++ )
        {
            replacedStatic.add(
                RefRenamer.apply( classes[ i ], target, replacement ) );
            //replacedStatic.add( classes[ i ].replace( target, replacement ) );
        }
        this.staticImports = replacedStatic;
        return this;
    }

    public static _imports of( Object... imports )
    {
        _imports im = new _imports();
        return im.add( imports );
    }

    public static Template IMPORTS = BindML.compile(
        "{{+:import {+imports*+};" + N
        + "+}}"
        + "{{+:import static {+staticImports+};" + N
        + "+}}" + N );

    private Set<String> importClasses = new TreeSet<String>();

    private Set<String> staticImports = new TreeSet<String>();

    @Override
    public int count()
    {
        return importClasses.size() + staticImports.size();
    }

    @Override
    public boolean isEmpty()
    {
        return count() == 0;
    }

    public String getAt( int index )
    {
        if( index > count() - 1 || index < 0 )
        {
            throw new ModelException(" invalid index ["+ index + "]" );
        }
        if( index < staticImports.size() )
        {
            return this.staticImports.toArray( new String[ 0 ] )[index ];            
        }
        return this.importClasses.toArray( new String[ 0 ] )[ index - staticImports.size() ];
    }
    
    public boolean contains( Class clazz )
    {
        return this.importClasses.contains( clazz.getCanonicalName() );
    }

    public boolean contains( String s )
    {
        return importClasses.contains( s ) || staticImports.contains( s );
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public Context getContext()
    {
        return VarContext.of(
                "imports", this.importClasses,
                "staticImports", this.staticImports );
    }
    
    @Override
    public Template getTemplate()
    {
        return IMPORTS;
    }
    
    @Override
    public String author( Directive... directives )
    {
        return Author.toString(
            IMPORTS,
            getContext(), directives );
    }

    /*
    public _imports addImports( _imports imports )
    {
        for( int i = 0; i < imports.count(); i++ )
	{
            addImports( imports.getImports().toArray() );
	}
	return this;
    }
     */
    public _imports add( Object... imports )
    {
        for( int i = 0; i < imports.length; i++ )
        {
            addImport( imports[ i ] );
        }
        return this;
    }

    public _imports add( _imports imports )
    {
        this.importClasses.addAll( imports.importClasses );
        this.staticImports.addAll( imports.staticImports );
        return this;
    }

    public _imports addStaticImports( Object... staticImports )
    {
        for( int i = 0; i < staticImports.length; i++ )
        {
            addStaticImport( staticImports[ i ] );
        }
        return this;
    }

    public _imports addStaticImport( Object importStatic )
    {
        return addImport( staticImports, importStatic, true );
    }

    public _imports addImport( Object importClass )
    {
        return addImport( importClasses, importClass, false );
    }

    public Set<String> getStaticImports()
    {
        return this.staticImports;
    }

    public Set<String> getImports()
    {
        return this.importClasses;
    }

    // we dont need to import these Strings */
    public static final String[] PRIMITIVES = new String[]
    {
        "boolean", "byte", "char", "double", "float", "int", "long", "short"
    };

    public static final Set<Object> EXCLUDE_IMPORTS
        = new HashSet<Object>();

    static
    {
        Object[] exclude =
        {
            String.class, String.class.getName(),
            Boolean.class, Boolean.class.getName(),
            Byte.class, Byte.class.getName(),
            Character.class, Character.class.getName(),
            Double.class, Double.class.getName(),
            Float.class, Float.class.getName(),
            Integer.class, Integer.class.getName(),
            Long.class, Long.class.getName(),
            Short.class, Short.class.getName(),
            Class.class, Class.class.getName(),
            ClassNotFoundException.class, ClassNotFoundException.class.getName(),
            "void",
        };
        EXCLUDE_IMPORTS.addAll( Arrays.asList( exclude ) );
    }

    private _imports addImport(
        Set<String> imports, Object importClass, boolean isStatic )
    {
        if( importClass == null )
        {
            return this;
        }
        if( importClass instanceof _imports )
        {
            this.importClasses.addAll(
                (((_imports)importClass).importClasses) );
            this.staticImports.addAll(
                (((_imports)importClass).staticImports) );
            return this;
        }
        if( importClass instanceof List )
        {
            List l = (List)importClass;
            for( int i = 0; i< l.size(); i++ )
            {
                addImport( imports, l.get( i ), isStatic );
            }
            return this;
        }
        if( importClass instanceof Class )
        {
            Class<?> clazz = (Class<?>)importClass;
            if( clazz.isArray() )
            {
                return addImport(
                    imports, clazz.getComponentType(), isStatic );
            }
            if( !clazz.isPrimitive()
                && !EXCLUDE_IMPORTS.contains( clazz ) )
            {   //dont need to add primitives or java.lang classes
                if( isStatic )
                {  // they want us to model a class (statically) 
                    //imports.add( clazz.getCanonicalName() + ".*" );
                    this.staticImports.add( clazz.getCanonicalName() + ".*" );
                }
                else
                {
                    imports.add( clazz.getCanonicalName() );
                }
            }
        }
        else if( importClass instanceof String )
        {
            String s = (String)importClass;

            if( s.startsWith( "import " ) ) 
            {
                s = s.substring( 7 );
            }
            if( Arrays.binarySearch( PRIMITIVES, s ) < 0
                //&& !s.startsWith( "java.lang" ) 
                && !EXCLUDE_IMPORTS.contains( s )
                && !s.equals( "class" ) )
            {
                if( isStatic )
                {
                    //if( s.endsWith( "*" ))
                    staticImports.add( s );
                }
                else
                {
                    imports.add( s );
                }
            }
        }
        else if( importClass instanceof ClassNameQualified )
        {
            _JavaFileModel fm = (_JavaFileModel)importClass;
            imports.add( ((_JavaFileModel)importClass).getQualifiedName() );
        }
        else if( importClass.getClass().isArray() )
        {
            Object[] arr = (Object[])importClass;
            for( int i = 0; i < arr.length; i++ )
            {
                addImport( imports, arr[ i ], isStatic );
            }
        }
        else
        {
            throw new ModelException(
                "Cant handle import " + importClass );
        }
        return this;
    }

    @Override
    public String toString()
    {
        return author();
    }

    public void merge( _imports toMerge )
    {
        this.importClasses.addAll( toMerge.importClasses );
        this.staticImports.addAll( toMerge.staticImports );
    }
}
