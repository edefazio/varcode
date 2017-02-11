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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.naming.RefRenamer;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * Modes fields on _classes, _interfaces, _enums, _annotationTypes
 * 
 * members of( "int x;" ); members of( "String[] names" ); members.of( "public
 * static Map<Integer,String>map" ); members.of( "public static final String
 * name = new Member(\" \");" );
 *
 */
public class _fields
    implements _Java, Countable, Authored
{
    public static final Template FIELDS
        = BindML.compile(
            "{{+?staticFields:{+staticFields+}" + N
          + "+}}"
          + "{{+?instanceFields:{+instanceFields+}" + N
          + "+}}" );

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }

    public static _fields cloneOf( _fields prototype )
    {
        return new _fields( prototype );        
    }

    
    /**
     * the fields in order of addition
     */
    private final List<_field> fields = new ArrayList<_field>();

    public _fields()
    {
    }

    public _fields( _fields prototype )
    {
        if( prototype == null )
        {
            return;
        }
        for( int i = 0; i < prototype.count(); i++ )
        {
            this.fields.add(  _field.cloneOf( prototype.getAt( i ) ) ); 
        }
    }
    
    @Override
    public int count()
    {
        return fields.size();
    }

    @Override
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
    /**
     * atReturn the field at this index
     *
     * @param index
     * @return the field at the index
     */
    @Override
    public _field getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            return fields.get( index );
        }
        throw new ModelException( "invalid field index [" + index + "]" );
    }

    public _field[] asArray()
    {
        return this.fields.toArray( new _field[ 0 ] );
    }
    
    /**
     * atReturn the names of all the fields
     *
     * @return all field names
     */
    public String[] getFieldNames()
    {
        String[] fieldNames = new String[ this.fields.size() ];
        for( int i = 0; i < fieldNames.length; i++ )
        {
            fieldNames[ i ] = fields.get( i ).name;
        }
        return fieldNames;
    }

    /**
     * Verify there is no other field with this name
     *
     * @param fieldName name of a field to be added
     * @return true if the field can be added
     */
    public boolean canAddFieldName( String fieldName )
    {
        for( int i = 0; i < fields.size(); i++ )
        {
            if( this.fields.get( i ).name.equals( fieldName ) )
            {
                return false;
            }
        }
        return true;
    }

    public List<_field> getList()
    {
        return this.fields;
    }
   
    
    public _field getByName( String name )
    {
        for( int i = 0; i < this.fields.size(); i++ )
        {
            if( this.fields.get( i ).name.equals( name ) )
            {
                return this.fields.get( i );
            }
        }
        return null;
    }

    /**
     * Sets these modifiers to all fields
     * @param modifiers the modifiers to set
     * @return this
     */
    public _fields setModifiers( String...modifiers )
    {
        for( int i = 0; i< count(); i++ )
        {
            fields.get( i ).setModifiers( modifiers );
        }
        return this;
    }
    
    /**
     * Adds these modifiers to all fields
     * @param modifiers the modifiers to set
     * @return this
     */
    public _fields addModifiers( String... modifiers )
    {
        for( int i = 0; i< count(); i++ )
        {
            fields.get( i ).mods.set( modifiers );
        }
        return this;
    }
    public List<_field> getByType( String typeName )
    {
        List<_field> found = new ArrayList<_field>();
        for( int i = 0; i < this.fields.size(); i++ )
        {
            if( this.fields.get( i ).getType().equals( typeName ) )
            {
                found.add( this.fields.get( i ) );
            }
        }
        return found;
    }

    public _fields remove( _field..._fs )
    {
        return remove( Arrays.asList( _fs ) );
    }
    
    public _fields remove( _field _f )
    {
        fields.remove( _f );
        return this;
    }
    
    public _fields remove( List<_field> toRemove )
    {
        for(int i=0; i<toRemove.size(); i++ )
        {
            fields.remove( toRemove.get( i ) );
        }
        return this;
    }
    
    /**
     * replaces the target toString with the replacement String
     *
     * @param target
     * @param replacement
     * @return this field after modification
     */
    @Override
    public _fields replace( String target, String replacement )
    {
        for( int i = 0; i < fields.size(); i++ )
        {
            _field f = this.fields.get( i );
            f.replace( target, replacement );
        }
        return this;
    }

    public _fields add( _fields fields )
    {
        for( int i = 0; i < fields.count(); i++ )
        {
            System.out.println( "Adding " + fields.getAt( i ) );
            this.add( fields.getAt( i ) );
        }
        return this;
    }

    public _fields add( String...fields )
    {
        for( int i = 0; i < fields.length; i++ )
        {
            add( _field.of( fields[ i ] ) );
        }
        return this;
    }
    
    /**
     * Adds one or more _field to the _fields
     *
     * @param fields fields to add
     * @return this (modified)
     */
    public _fields add( _field... fields )
    {
        for( int i = 0; i < fields.length; i++ )
        {
            if( canAddFieldName( fields[ i ].name ) )
            {
                this.fields.add( fields[ i ] );
            }
            else
            {
                throw new ModelException(
                    "cannot add field with name \""
                    + fields[ i ].name
                    + "\" a field with the same name already exists" );
            }
        }
        return this;
    }

    /**
     * Create a new _fields of the member fields
     *
     * @param fields each _field to be included in the _fields
     * @return a new _fields including fields
     */
    public static _fields of( String... fields )
    {
        _fields _fs = new _fields();
        for( int i = 0; i < fields.length; i++ )
        {
            _fs.add( _field.of( fields[ i ] ) );
        }
        return _fs;
    }

    public static _fields of( _field... _fields )
    {
        _fields _fs = new _fields();
        _fs.add( _fields );
        return _fs;
    }

    /**
     * model for a field
     */
    public static class _field
        implements _Java, _facet, Authored
    {
        public static final Template FIELD = BindML.compile(
            "{+javadoc+}{+annotations+}{+modifiers+}{+type*+} {+name*+}{+init+};" );

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }

        public _field( _field prototype )
        {
            this.fieldAnnotations = _annotations.cloneOf(  prototype.fieldAnnotations );
            this.mods = _modifiers.cloneOf( prototype.mods );
            this.init = _init.cloneOf(  prototype.init );
            this.javadoc = _javadoc.cloneOf( prototype.javadoc );
            this.name = prototype.name;
            this.type = prototype.type;           
        }
        
        /**
         * Creates and atReturn a clone of the prototype field
         *
         * @param prototype
         * @return
         */
        public static _field cloneOf( _field prototype )
        {            
            return new _field( prototype );
        }

        /**
         * Updated the init, because the initialization COULD be many 
         * lines of code
         * @param initialization
         * @return 
         */
        public _field init( String... initialization )
        {
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< initialization.length; i++ )
            {
                if( i > 0 )
                {
                    sb.append( System.lineSeparator());
                }
                sb.append(initialization[ i ] );                
            }            
            return init( _init.of( sb.toString() ) );
        }
        
        /**
         * set the initialization for this field on declaration)
         * <PRE>
         * i.e.
         * public String name = "Eric";
         *                   ^^^^^^^^^
         * </PRE>
         *
         * @param initialization
         * @return this (modified)
         */
        public _field init( _init initialization )
        {
            this.init = initialization;
            return this;
        }

        public _field annotate( Object... annotations )
        {
            this.fieldAnnotations.add( annotations );
            return this;
        }

        public _annotations getAnnotations()
        {
            return this.fieldAnnotations;
        }

        public String getType()
        {
            return this.type;
        }

        public _modifiers getModifiers()
        {
            return this.mods;
        }

        //adds all of the modifiers to all the fields
        public _field addModifiers( String...modifiers )
        {
            this.mods.set( modifiers );
            return this;
        }
        
        public _field setModifiers( String... modifiers )
        {
            this.mods = _modifiers.of( modifiers );
            return this;
        }

        public _field setModifiers( int modifiers )
        {
            this.mods = _modifiers.of( modifiers );
            return this;
        }

        @Override
        public _field replace( String target, String replacement )
        {
            this.javadoc = javadoc.replace( target, replacement );
            this.init = 
                init.replace( target, replacement );
            this.fieldAnnotations = fieldAnnotations.replace( target, replacement );
            this.name = 
                RefRenamer.apply( this.name, target, replacement );
            this.type = 
                RefRenamer.apply( this.type, target, replacement );
            return this;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( javadoc, init, fieldAnnotations, name, mods, type);
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
            final _field other = (_field)obj;
            if( !Objects.equals( this.type, other.type ) )
            {
                System.out.println( "TYPE NOT EQUAL");
                return false;
            }
            if( !Objects.equals( this.name, other.name ) )
            {
                System.out.println( "NAME NOT EQUAL");
                return false;
            }
            if( !Objects.equals( this.fieldAnnotations, other.fieldAnnotations ) )
            {
                System.out.println( "ANNS NOT EQUAL");
                return false;
            }
            if( !Objects.equals( this.javadoc, other.javadoc ) )
            {
                System.out.println( "JAVADOC NOT EQUAL");
                return false;
            }
            if( !Objects.equals( this.mods, other.mods ) )
            {
                System.out.println( "MODS NOT EQUAL");
                return false;
            }
            if( !Objects.equals( this.init, other.init ) )
            {
                System.out.println( "INIT NOT EQUAL \""+ this.init.initCode + "\" \"" + other.init.initCode+"\"");
                return false;
            }
            return true;
        }
    
        public String getName()
        {
            return this.name;
        }

        public _javadoc getJavadoc()
        {
            return this.javadoc;
        }

        public _init getInit()
        {
            return this.init;
        }
        
        private static _field parseFieldDeclarationFrom( String fieldDeclaration )
        {
            if( fieldDeclaration.endsWith( ";" ) )
            {
                fieldDeclaration = fieldDeclaration.substring(
                    0, fieldDeclaration.length() - 1 );
            }
            int indexOfEquals = fieldDeclaration.indexOf( '=' );

            if( indexOfEquals > 0 )
            {   //there is an init AND a member 
                String member = fieldDeclaration.substring( 0, indexOfEquals );
                String init = fieldDeclaration.substring( indexOfEquals + 1 );
                _field f = parseField( member );
                return f.setInit( init );
            }
            _field f = parseField( fieldDeclaration );
            
            return f;
        }
     
        
        /**
         * DOES NOT Handle parsing Field Annotations (only field signatures)
         *
         * @param fieldDef the definition of the field (sans annotations)
         * @return a _field based on the field
         */
        private static _field parseField( String fieldDef )
        {
            String[] tokens = _var.normalizeTokens( fieldDef );
            if( tokens.length < 2 )
            {
                throw new ModelException(
                    "Expected at least (2) tokens for field <type> <name>" );
            }
            String name = tokens[ tokens.length - 1 ];

            String t = tokens[ tokens.length - 2 ];
            if( tokens.length > 2 )
            {
                String[] arr = new String[ tokens.length - 2 ];
                System.arraycopy( tokens, 0, arr, 0, arr.length );
                _modifiers mods = _modifiers.of( arr );
                if( mods.containsAny(
                    Modifier.ABSTRACT, Modifier.NATIVE, Modifier.SYNCHRONIZED,
                    Modifier.STRICT ) )
                {
                    throw new ModelException(
                        "field contains invalid modifier " + N + mods.toString() );
                }
                if( mods.containsAll( Modifier.FINAL, Modifier.VOLATILE ) )
                {
                    throw new ModelException(
                        "field cannot be BOTH final AND volatile" );
                }
                return new _field( mods, t, name );
            }
            return new _field( new _modifiers(), t, name );
        }

        @Override
        public Context getContext()
        {
            return VarContext.of(
                "javadoc", this.javadoc,
                "annotations", this.fieldAnnotations,
                "modifiers", this.mods,
                "type", this.type,
                "name", this.name,
                "init", this.init );
        }

        @Override
        public Template getTemplate()
        {
            return FIELD;
        }
        
        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public String author( Directive... directives )
        {
            return Author.toString( FIELD,
                getContext(),
                directives );
        }

        @Override
        public String toString()
        {
            return author();
        }

        private _annotations fieldAnnotations = new _annotations();
        private _javadoc javadoc;
        private _modifiers mods;
        private String type;
        private String name;
        private _init init;

        private static class FieldParams
        {
            _javadoc javadoc; // starts with /* ends with */
            _annotations annots = new _annotations(); //starts with @
            String fieldSignature;
        }

        public static _field of( Object... components )
        {
            FieldParams cd = new FieldParams();
            for( int i = 0; i < components.length; i++ )
            {
                if( components[ i ] instanceof String )
                {
                    fromString( cd, (String)components[ i ] );
                }
                else if( components[ i ] instanceof _javadoc )
                {
                    cd.javadoc = (_javadoc)components[ i ];
                }
                else if( components[ i ] instanceof _annotations._annotation )
                {
                    cd.annots.add( (_annotations._annotation)components[ i ] );
                }
                else if( components[ i ] instanceof _annotations )
                {
                    cd.annots = (_annotations)components[ i ];
                }
            }
            _field _f = parseFieldDeclarationFrom( cd.fieldSignature );
            //_field _f = _field.of( cd.fieldSignature );
            
            for( int i = 0; i < cd.annots.count(); i++ )
            {
                _f.annotate( cd.annots.getAt( i ) );
            }
            if( cd.javadoc != null && !cd.javadoc.isEmpty() )
            {
                _f.javadoc( cd.javadoc.getComment() );
            }
            return _f;
        }

        private static void fromString( FieldParams cd, String component )
        {
            if( component.startsWith( "/**" ) )
            {
                cd.javadoc = _javadoc.of(
                    component.substring( 3, component.length() - 2 ) );
            }
            else if( component.startsWith( "/*" ) )
            {
                cd.javadoc = _javadoc.of(
                    component.substring( 2, component.length() - 2 ) );
            }
            else if( component.startsWith( "@" ) )
            {
                cd.annots.add( _annotations._annotation.of( component ) );
            }
            else
            {
                cd.fieldSignature = (String)component;
            }
        }
        
        public _field( int modifiers, String type, String name, String init )
        {
            this( _modifiers.of( modifiers ), type, name, _init.of( init ) );
        }

        public _field( _modifiers modifiers, String type, String name )
        {
            this.mods = modifiers;
            this.type = type;
            this.name = name;
            this.javadoc = new _javadoc();
            this.init = new _init();
            this.fieldAnnotations = new _annotations();
        }

        public _field( _modifiers modifiers, String type, String name, _init init )
        {
            this.mods = modifiers;
            this.type = type;
            this.name = name;
            this.init = init;
            this.javadoc = new _javadoc();
            this.fieldAnnotations = new _annotations();
        }

        public _field setInit( String init )
        {
            this.init = new _init( init );
            return this;
        }

        public _field javadoc( _javadoc javadoc )
        {
            this.javadoc = javadoc;
            return this;
        }

        public _field javadoc( String javadoc )
        {
            this.javadoc = new _javadoc( javadoc );
            return this;
        }

        /**
         * does this field have an initial value
         *
         * @return true if the field has initialization, false otherwise
         */
        public boolean hasInit()
        {
            return init != null && !init.isEmpty();
        }

        public _field setType( String newType )
        {
            this.type = newType;
            return this;
        }

        public _field setName( String newName )
        {
            this.name = newName;
            return this;
        }
    }

    @Override
    public Context getContext()
    {
        List<_field> staticFields = new ArrayList<_field>();
        List<_field> instanceFields = new ArrayList<_field>();
        for( int i = 0; i < fields.size(); i++ )
        {
            _field mem = fields.get( i );
            if( mem.mods.containsAny( Modifier.STATIC ) )
            {
                staticFields.add( mem );
            }
            else
            {
                instanceFields.add( mem );
            }
        }

        return VarContext.of(
            "staticFields", staticFields,
            "instanceFields", instanceFields );
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public Template getTemplate()
    {
        return FIELDS;
    }
    
    @Override
    public String author( Directive... directives )
    {
        return Author.toString(
            FIELDS,
            getContext(),
            directives );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( fields );
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
        final _fields other = (_fields)obj;
        if( !Objects.equals( this.fields, other.fields ) )
        {
            return false;
        }
        return true;
    }
        
    @Override
    public String toString()
    {
        return author();
    }

    /**
     * Field Initialization
     * <PRE>
     * i.e. public int x = 100;
     *                   ^^^^^
     *      public static final String NAME = "Eric";
     *                                      ^^^^^^^^
     * </PRE>
     */
    public static class _init
        implements _Java, Authored
    {
        public static _init of( String code )
        {
            return new _init( code );
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }
        
        private String initCode;

        public _init()
        {
            this.initCode = "";
        }

        public _init( _init prototype )
        {            
            if( prototype != null )
            {
                this.initCode = prototype.initCode;
            }
        }
            
        public static _init cloneOf( _init prototype )
        {
            return new _init( prototype );
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash( this.initCode );
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
            final _init other = (_init)obj;
            if( !Objects.equals( this.initCode, other.initCode ) )
            {
                return false;
            }
            return true;
        }
        
        
        public boolean isEmpty()
        {
            return initCode == null || initCode.trim().length() == 0;
        }

        public _init( String code )
        {
            if( code == null )
            {
                this.initCode = "";
            }
            else if( code.trim().startsWith( "=" ) )
            {
                this.initCode = code.substring( code.indexOf( "=" ) + 1 ).trim();
            }
            else
            {
                this.initCode = code.trim();
            }
        }
        
        public static final Template INIT = BindML.compile( " = {+initCode*+}" );

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public String author( Directive... directives )
        {
            if( initCode != null && initCode.trim().length() > 0 )
            {
                return Author.toString(
                    INIT,
                    VarContext.of( "initCode", initCode ),
                    directives );
            }
            return "";
        }
        
        @Override
        public Context getContext()
        {
            return VarContext.of( "initCode", initCode );            
        }
        
        @Override
        public Template getTemplate()
        {
            if( initCode != null && initCode.trim().length() > 0 )
            {
                return INIT;
            }
            return Template.EMPTY;
        }

        @Override
        public String toString()
        {
            return author();
        }

        public String getCode()
        {
            return initCode;
        }

        @Override
        public _init replace( String target, String replacement )
        {
            if( this.initCode != null )
            {
                return new _init(
                    RefRenamer.apply( this.initCode, target, replacement ) );
            }
            return this;
        }
    }
}
