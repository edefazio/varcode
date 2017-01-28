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

import varcode.java.load._JavaAstPort;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import varcode.ModelException;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.ast.JavaAst;
import varcode.java.lang.RefRenamer;
import varcode.java.model._generic._typeParams;
import varcode.java.model._parameters._parameter;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.markup.bindml.BindML;

/**
 * Model for building one or more constructors (belonging to a {@code _class} or
 * {@code_enum})
 *
 * Note: there are dependencies among all constructors (i.e. each must have a
 * unique signature).
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _constructors
    implements _Java, Countable, Authored
{
    private List<_constructor> constructors = new ArrayList<_constructor>();

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this );
    }

    public static _constructors of( _constructor... _ctorArray )
    {
        _constructors _ctors = new _constructors();
        for( int i = 0; i < _ctorArray.length; i++ )
        {
            _ctors.addConstructor( _ctorArray[ i ] );
        }
        return _ctors;
    }

    public List<_constructor> getList()
    {
        return this.constructors;
    }
    
    public _constructors remove( _constructor...toRemove )
    {
        return remove( Arrays.asList( toRemove ) ); 
    }
    
    public _constructors remove( List<_constructor> ctorsToRemove )
    {
        this.constructors.removeAll( ctorsToRemove );
        return this;
    }
    
    public _constructors( _constructors prototype )
    {
        for( int i = 0; i < prototype.count(); i++ )
        {
            addConstructor(
                _constructor.cloneOf( prototype.constructors.get( i ) ) );
        }
    }
    /**
     * Builds and atReturn a clone of the prototype constructors
     *
     * @param prototype baseline to build clone compile
     * @return a new _constructors based compile the prototype
     */
    public static _constructors cloneOf( _constructors prototype )
    {
        return new _constructors( prototype );
    }

    public _constructors()
    {
    }

    public List<_constructor> getConstructors()
    {
        return this.constructors;
    }

    public _constructor getAt( int index )
    {
        if( index >= 0 && index < count() )
        {
            return this.constructors.get( index );
        }
        throw new ModelException( "invalid index [" + index + "]" );
    }

    public static final Template CONSTRUCTORS = BindML.compile(
        "{{+?constructors:{+constructors+}" + N + "+}}" );

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public Context getContext()
    {
        return VarContext.of( "constructors", constructors );
    }
    
    @Override
    public Template getTemplate()
    {
        return CONSTRUCTORS;
    }
    
    @Override
    public String author( Directive... directives )
    {
        if( constructors.size() > 0 )
        {
            return Author.toString(
                CONSTRUCTORS,
                getContext(),
                directives );
        }
        return "";
    }

    @Override
    public String toString()
    {
        return author();
    }

    private void verifyNoConflicts( _constructor constructor )
    {
        for( int i = 0; i < this.constructors.size(); i++ )
        {
            if( constructors.get( i ).matchesExisting( constructor ) )
            {
                throw new ModelException(
                    "Error, adding constructor " + N + constructor.toString()
                    + N + "a constructor with the same signature found " + N
                    + constructors.get( i ).toString() );
            }
        }
    }

    public _constructors addConstructor( String signature, Object... bodyLines )
    {
        _constructor c = new _constructor( signature ).body( bodyLines );
        return addConstructor( c );
    }

    @Override
    public int count()
    {
        return constructors.size();
    }

    @Override
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
    public final _constructors addConstructor( _constructor constructor )
    {
        verifyNoConflicts( constructor );
        constructors.add( constructor );
        return this;
    }

    /**
     * Individual constructor model
     */
    public static class _constructor
        implements _facet, _Java, Annotated
    {
        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }


        public _constructor addParameter( String type, String name )
        {
            this.getSignature().params.add( type, name );
            return this;
        }
        
        /**
         * Add a _parameter to this _constructor
         * @param _p the parameter to add
         * @return the modified constructor
         */
        public _constructor addParameter( _parameter _p )
        {
            this.getSignature().params.add( _p );
            return this;
        }
        
        /**
         * Holds builder params to simplify construction / initialization (we
         * use the static of(...) "builder" method.)
         */
        private static class CtorParams
        {
            _javadoc javadoc; // starts with /* ends with */
            _annotations annots = new _annotations(); //starts with @
            String signature;
            List<Object> body = new ArrayList<Object>(); //anything AFTER signature is populated
        }

        private static void addBody( CtorParams ctorParams, Object body )
        {
            if( body.getClass().isArray() )
            {
                for( int i = 0; i < Array.getLength( body ); i++ )
                {
                    ctorParams.body.add( Array.get( body, i ) );
                }
            }
            else if( body.getClass().isAssignableFrom( List.class ) )
            {
                List lBody = (List)body;
                ctorParams.body.addAll( lBody );
            }
            else
            {
                ctorParams.body.add( body );
            }
        }

        /**
         * A flexible fluent API for allowing the client to create _methods i.e.<PRE>
         * public static final _method _m = _method.of(
         *     "/*comment* /",  //provide the javadoc comment for the method
         *     "@Deprecated",   //provide one or more annotations to the method
         *     "public static final String createId()", //the method signature
         *     "return UUID.randomUUID().toString();"   //the method body
         * );
         *
         * //the idea is to provide the ability to declare the _method
         * (metalanguage model) as succinctly as possible.
         *
         * @param parts the parts of a method
         * @return a _method
         */
        public static _constructor of( Object... parts )
        {
            CtorParams mp = new CtorParams();
            for( int i = 0; i < parts.length; i++ )
            {
                if( parts[ i ] == null )
                {
                    continue;
                }
                else if( mp.signature != null )
                {   //ANYTHING i pass in AFTER the signature
                    // is assumed to be the BODY of the method
                    addBody( mp, parts[ i ] );
                }
                else if( parts[ i ] instanceof String )
                {
                    partFromString( mp, (String)parts[ i ] );
                }
                else if( parts[ i ] instanceof _javadoc )
                {
                    mp.javadoc = (_javadoc)parts[ i ];
                }
                else if( parts[ i ] instanceof _annotations._annotation )
                {
                    mp.annots.add( (_annotations._annotation)parts[ i ] );
                }
                else if( parts[ i ] instanceof _annotations )
                {
                    mp.annots = (_annotations)parts[ i ];
                }
            }

            _constructor _ctor = new _constructor( mp.signature );
            for( int i = 0; i < mp.annots.count(); i++ )
            {
                _ctor.annotate( mp.annots.getAt( i ) );
            }
            if( mp.javadoc != null && !mp.javadoc.isEmpty() )
            {
                _ctor.javadoc( mp.javadoc.getComment() );
            }
            if( mp.body != null && !mp.body.isEmpty() )
            {
                _ctor.body( mp.body.toArray() );
            }
            return _ctor;
        }

        private static void partFromString( CtorParams mp, String component )
        {
            if( component.startsWith( "/**" ) )
            {
                mp.javadoc = _javadoc.of( component.substring( 3, component.length() - 2 ) );
            }
            else if( component.startsWith( "/*" ) )
            {
                mp.javadoc = _javadoc.of( component.substring( 2, component.length() - 2 ) );
            }
            else if( component.startsWith( "@" ) )
            {
                mp.annots.add( _annotations._annotation.of( component ) );
            }
            else
            {
                mp.signature = (String)component;
            }
        }

        public _modifiers getModifiers()
        {
            return this.constructorSig.getModifiers();
        }

        public _throws getThrows()
        {
            return this.constructorSig.getThrows();
        }

        public String getName()
        {
            return this.constructorSig.getClassName();
        }

        public _parameters getParameters()
        {
            return this.constructorSig.getParameters();
        }

        public static _constructor cloneOf( _constructor prototype )
        {
            return new _constructor( prototype );
        }

        private _annotations annotations;

        private _javadoc javadoc = new _javadoc();
        private _signature constructorSig;
        private _code body;

        public _constructor(
            _modifiers modifiers,
            _typeParams genericTypeParams,
            String name,
            _parameters params,
            _throws throwsExceptions )
        {
            this(
                new _signature(
                    modifiers,
                    genericTypeParams,
                    name,
                    params,
                    throwsExceptions ) );

        }

        public _constructor(
            _modifiers modifiers,
            String name,
            _parameters params,
            _throws throwsExceptions )
        {
            this( modifiers,
                new _generic._typeParams(),
                name,
                params,
                throwsExceptions );

        }

        public _constructor( _signature sig )
        {
            this.constructorSig = sig;
            this.annotations = new _annotations();
        }

        public _constructor( _constructor prototype )
        {
            this.constructorSig = _signature.cloneOf( prototype.constructorSig );
            this.annotations = _annotations.cloneOf( prototype.annotations );
            this.body = _code.cloneOf( prototype.body );
            this.javadoc = _javadoc.cloneOf( prototype.javadoc );            
        }
        
        public _constructor( String constructorSignature )
        {
            this.constructorSig = _signature.of( constructorSignature );
            this.annotations = new _annotations();
        }

        public _constructor annotate( Object... annotations )
        {
            this.annotations.add( annotations );
            return this;
        }

        public _constructor setSignature( _signature sig )
        {
            this.constructorSig = sig;
            return this;
        }

        public _constructor setName( String name )
        {
            this.constructorSig.className = name;
            return this;
        }

        public _signature getSignature()
        {
            return constructorSig;
        }

        public _constructor setJavadoc( _javadoc javadoc )
        {
            this.javadoc = javadoc;
            return this;
        }

        public _javadoc getJavadoc()
        {
            return this.javadoc;
        }

        public _constructor setAnnotations( _annotations annotations )
        {
            this.annotations = annotations;
            return this;
        }

        @Override
        public _annotations getAnnotations()
        {
            return this.annotations;
        }

        public _constructor setBody( String body )
        {
            return setBody( _code.of( body ) );
        }

        public _constructor setBody( _code body )
        {
            this.body = body;
            return this;
        }

        public _code getBody()
        {
            return this.body;
        }

        @Override
        public _constructor replace( String target, String replacement )
        {
            if( this.javadoc != null && !this.javadoc.isEmpty() )
            {
                this.javadoc.replace( target, replacement );
            }

            if( this.annotations != null && !this.annotations.isEmpty() )
            {
                this.annotations.replace( target, replacement );
            }

            this.body.replace( target, replacement );

            this.constructorSig.replace( target, replacement );

            return this;
        }

        public _constructor javadoc( _javadoc javadoc )
        {
            this.javadoc = javadoc;
            return this;
        }

        public _constructor javadoc( String comment )
        {
            this.javadoc = new _javadoc( comment );
            return this;
        }

        public _constructor body( Object... bodyLines )
        {
            if( bodyLines == null || bodyLines.length == 0 )
            {
                this.body = null;
            }
            this.body = _code.of( bodyLines );

            return this;
        }

        /**
         * Ask if the constructor matches an existing constructor (contains the
         * same number of parameters of the same type)
         *
         * NOTE: this is not comprehensive on the account of Polymorphism but
         * intends to catch the most flagrant constructors violations.
         *
         * @param _ctor
         * @return
         */
        public boolean matchesExisting( _constructor _ctor )
        {
            _constructor._signature sig = _ctor.constructorSig;

            if( sig.className.equals( this.constructorSig.className ) )
            {
                if( sig.params.count() == this.constructorSig.params.count() )
                {
                    for( int i = 0; i < sig.params.count(); i++ )
                    {
                        if( !sig.params.getAt( i ).getType().equals( this.constructorSig.params.getAt( i ).getType() ) )
                        {
                            return false;
                        }
                        //I could also try to check generic and extensions, but lets just do this
                    }
                    return true;
                }
            }
            return false;
        }

        public static final Template CONSTRUCTOR
            = BindML.compile(
                 "{+javadoc+}"
                + "{+annotations+}"
                + "{+signature*+}" + N
                + "{" + N
                + "{+$>(body)+}" + N
                + "}" );

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public String author( Directive... directives )
        {
            return Author.toString(
                CONSTRUCTOR,
                VarContext.of(
                    "javadoc", javadoc,
                    "annotations", this.annotations,
                    "signature", constructorSig,
                    "body", body ),
                directives );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( javadoc, this.annotations, constructorSig, body );
        }

        @Override
        public boolean equals( Object obj )
        {
            if( this == obj )
            {
                System.out.println ("SAME OBJ");
                return true;
            }
            if( obj == null )
            {
                System.out.println ("NUll OBJ");
                return false;
            }
            if( getClass() != obj.getClass() )
            {
                System.out.println ("DIff CLASS");
                return false;
            }
            final _constructor other = (_constructor)obj;
            if( !Objects.equals( this.annotations, other.annotations ) )
            {
                System.out.println ("DIFF ANN");
                return false;
            }
            if( !Objects.equals( this.javadoc, other.javadoc ) )
            {
                System.out.println ("DIFF JAVADOC");
                return false;
            }
            if( !Objects.equals( this.constructorSig, other.constructorSig ) )
            {
                System.out.println ("DIFF SIG");
                return false;
            }
            if( !Objects.equals( this.body, other.body ) )
            {
                System.out.println ("DIFF BODY");
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
         * constructor signature
         */
        public static class _signature
            implements _Java, Authored
        {
            private String className;
            private _modifiers modifiers = new _modifiers(); //public protected private
            private _typeParams genericTypeParams = new _typeParams();
            private _parameters params = new _parameters();
            private _throws throwsExceptions = new _throws();

            public _signature(
                _modifiers modifiers,
                _typeParams genericTypeParams,
                String className,
                _parameters params,
                _throws throwsExceptions )
            {
                this.modifiers = modifiers;
                this.genericTypeParams = genericTypeParams;
                this.className = className;
                this.params = params;
                this.throwsExceptions = throwsExceptions;
            }

            
            @Override
            public int hashCode()
            {
                return Objects.hash( 
                    this.className, this.genericTypeParams, this.modifiers, this.params, this.throwsExceptions);
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
                final _signature other = (_signature)obj;
                if( !Objects.equals( this.className, other.className ) )
                {
                    return false;
                }
                if( !Objects.equals( this.modifiers, other.modifiers ) )
                {
                    return false;
                }
                if( !Objects.equals( this.genericTypeParams, other.genericTypeParams ) )
                {
                    return false;
                }
                if( !Objects.equals( this.params, other.params ) )
                {
                    return false;
                }
                if( !Objects.equals( this.throwsExceptions, other.throwsExceptions ) )
                {
                    return false;
                }
                return true;
            }
    
            @Override
            public void visit( ModelVisitor visitor )
            {
                visitor.visit(this);
            }
        
            public String getClassName()
            {
                return this.className;
            }

            @Override
            public _signature replace( String target, String replacement )
            {
                this.className =
                    RefRenamer.apply( this.className, target, replacement );
                    //= this.className.replace( target, replacement );
                this.modifiers.replace( target, replacement );
                this.genericTypeParams.replace( target, replacement );
                this.params.replace( target, replacement );
                this.throwsExceptions.replace( target, replacement );
                return this;
            }

            public _typeParams getGenericTypeParameters()
            {
                return this.genericTypeParams;
            }

            public _parameters getParameters()
            {
                return this.params;
            }

            public _throws getThrows()
            {
                return this.throwsExceptions;
            }

            public _modifiers getModifiers()
            {
                return modifiers;
            }

            /**
             * parses the name out of the constructor signature
             *
             * @param ctorSignature the constructor signature as a String
             * @return the name (of the class, enum being constructed)
             */
            private static String parseNameFrom( String ctorSignature )
            {
                int openIndex = ctorSignature.indexOf( "(" );
                if( openIndex < 0 )
                {
                    throw new ModelException(
                        "constructor signature must contain '(' " );
                }
                String ctorCapped = ctorSignature.substring( 0, openIndex ).trim();
                //now backtrack until 
                String name = "";
                for( int nameStart = ctorCapped.length() - 1; nameStart >= 0; nameStart-- )
                {
                    char nextChar = ctorCapped.charAt( nameStart );
                    if( Character.isWhitespace( nextChar )
                        || ('>' == nextChar) )
                    {
                        return name;
                    }
                    name = nextChar + name;
                }
                return name;
            }

            //"public <E extends Enum> A( ... )"
            public static _constructor._signature of( String ctorSig )
            {
                //parse the name compile within the Constructor signature
                String name = parseNameFrom( ctorSig );

                //manufacture a class that contains this constructor (based on the name)
                //(so we can use the AST parser to figure out the tokens rather than
                // manually parsing ourseleves
                String sourceCode = "class " + name + System.lineSeparator()
                    + "{" + System.lineSeparator()
                    + ctorSig + System.lineSeparator()
                    + "    { }" + System.lineSeparator()
                    + "}";

                try
                {
                    CompilationUnit astRoot = JavaAst.astFrom( sourceCode );

                    ConstructorDeclaration astCtor
                        = (ConstructorDeclaration)astRoot.getTypes().get( 0 ).getMembers().get( 0 );

                    _signature _sig
                        = _JavaAstPort._constructorFromAST( 
                            astCtor ).getSignature();

                    if( _sig.getModifiers().containsAny(
                        Modifier.ABSTRACT, Modifier.FINAL, Modifier.NATIVE,
                        Modifier.STATIC, Modifier.STRICT, Modifier.SYNCHRONIZED,
                        Modifier.TRANSIENT, Modifier.VOLATILE ) )
                    {
                        throw new ModelException(
                            "Invalid Modifier(s) for constructor " + N
                            + ctorSig );
                    }
                    return _sig;
                }
                catch( ParseException pe )
                {
                    throw new ModelException(
                        "Unable to parse Constructor signature to AST from Java Source :"
                        + System.lineSeparator() + sourceCode, pe );
                }
            }

            public _signature (_signature prototype )
            {
                this.className = prototype.className;
                this.genericTypeParams = _typeParams.cloneOf( prototype.genericTypeParams );
                this.modifiers = _modifiers.cloneOf( prototype.modifiers );
                this.params = _parameters.cloneOf( prototype.params );
                this.throwsExceptions = _throws.cloneOf( prototype.throwsExceptions );
            }
            
            public static _signature cloneOf( _signature prototype )
            {
                return new _signature( prototype );                
            }

            public static final Template CONSTRUCTOR_SIGNATURE = BindML.compile( 
                "{+modifiers+}{+genericTypeParams+}{+className+}{+params+}{+throws+}" );

            @Override
            public Context getContext()
            {
                return VarContext.of(
                        "modifiers", modifiers,
                        "genericTypeParams", genericTypeParams,
                        "className", className,
                        "params", params,
                        "throws", throwsExceptions );
            }
            
            @Override
            public Template getTemplate()
            {
                return CONSTRUCTOR_SIGNATURE;
            }
            
            @Override
            public String author( Directive... directives )
            {
                return Author.toString( CONSTRUCTOR_SIGNATURE,
                    getContext(),
                    directives );
            }

            @Override
            public String author()
            {
                return author( new Directive[ 0 ] );
            }

            @Override
            public String toString()
            {
                return author();
            }
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( constructors );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
        {
            System.out.println ("SAME OBJ");
            return true;
        }
        if( obj == null )
        {
            System.out.println ("DIFF OBJ");
            return false;
        }
        if( getClass() != obj.getClass() )
        {
            System.out.println ("DIFF CLASS");
            return false;
        }
        final _constructors other = (_constructors)obj;
        if( !Objects.equals( this.constructors, other.constructors ) )
        {
            System.out.println("DIFF " + this.constructors + other.constructors );
            return false;
        }
        return true;
    }
        
    //called when the class/ enum is renamed (renames all of the constructors
    @Override
    public _constructors replace( String target, String replacement )
    {
        for( int i = 0; i < constructors.size(); i++ )
        {
            _constructor _ctors = constructors.get( i );
            
            constructors.set( i, _ctors.replace( target, replacement ) );
        }
        return this;
    }
    
}
