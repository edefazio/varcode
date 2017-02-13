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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ReferenceType;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.translate.JavaTranslate;
import varcode.java.ast.JavaAst;
import varcode.java.naming.RefRenamer;
import varcode.java.model._generic._typeParams;
import varcode.java.model._parameters._parameter;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * Grouping of methods belonging to an entity (class, enum, interface)
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _methods
    implements _Java, Countable, Authored
{
    private List<_method> methods = new ArrayList<_method>();

    public static _methods of( _method... _meths )
    {
        _methods _ms = new _methods();
        _ms.add( _meths );
        return _ms;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( methods );
    }

    public List<_method> getList()
    {
        return methods;
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
        final _methods other = (_methods)obj;
        
        if( !Objects.equals( this.methods, other.methods ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public _method getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            String[] names = getNames();
            int idx = 0;
            for( int i = 0; i < names.length; i++ )
            {
                List<_method> byName = getByName( names[ i ] );
                if( (byName.size() + idx) > index )
                {
                    int offset = index - idx;
                    return byName.get( offset );
                }
                idx += byName.size();
            }
        }
        throw new ModelException(
            "unable to get method at [" + index + "], out of range" );
    }

    public _methods( _methods prototype )
    {
        if( prototype != null )
        {
            for( int i = 0; i < prototype.count(); i++ )
            {
                this.methods.add( _method.cloneOf(  prototype.getAt( i ) ) );
            }        
        }
    }
    
    public static _methods cloneOf( _methods prototype )
    {
        return new _methods( prototype );        
    }

    public _methods()
    {
    }

    public static final Template METHODS = BindML.compile(
        "{{+?staticMethods:" + N + "{+staticMethods+}+}}"
        + "{{+?nonStaticMethods:" + N + "{+nonStaticMethods+}+}}"
        + "{{+?abstractMethods:" + N + "{+abstractMethods+};+}}" );

    public String[] getNames()
    {
        Set<String> names = new HashSet<String>();
        for( int i = 0; i < this.methods.size(); i++ )
        {
            names.add( this.methods.get( i ).getName() );
        }
        return names.toArray( new String[ 0 ] );
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public Context getContext()
    {
        List<_method> nonStaticMethods = new ArrayList<_method>();
        List<_method> staticMethods = new ArrayList<_method>();
        List<_method._signature> abstractMethods = new ArrayList<_method._signature>();

        String[] methodNames = getNames(); //methodsByName.keySet().toArray( new String[ 0 ] );

        List<_method> oMethods = this.methods;
        for( int j = 0; j < oMethods.size(); j++ )
        {
            if( oMethods.get( j ).signature.modifiers.contains( Modifier.ABSTRACT ) )
            {
                abstractMethods.add( oMethods.get( j ).signature );
            }
            else if( oMethods.get( j ).signature.modifiers.contains( Modifier.STATIC ) )
            {
                staticMethods.add( oMethods.get( j ) );
            }
            else
            {
                nonStaticMethods.add( oMethods.get( j ) );
            }
        }
        return VarContext.of(
            "staticMethods", staticMethods,
            "nonStaticMethods", nonStaticMethods,
            "abstractMethods", abstractMethods );
    }
    
    @Override
    public Template getTemplate()
    {
        return METHODS;
    }
    
    @Override
    public String author( Directive... directives )
    {
        
        return Author.toString(
            METHODS,
            getContext(),
            directives );
    }

    @Override
    public String toString()
    {
        return author();
    }

    public boolean isEmpty()
    {
        return count() == 0;
    }

    public int count()
    {
        return this.methods.size();
    }

    public _methods add( _method method )
    {
        verifyAndAddMethod( method );
        return this;
    }

    public _methods add( String signature )
    {
        return _methods.this.add( signature, (Object[])null );
    }

    public _methods add( String signature, Object... body )
    {
        _method _m = _method.of( (_javadoc)null, signature, (Object[])body );
        verifyAndAddMethod( _m );
        return this;
    }

    /**
     * atReturn all of the methods by the name
     *
     * @param name the name of the method
     * @return all methods with this name
     */
    public List<_method> getByName( String name )
    {
        List<_method> byName = new ArrayList<_method>();

        for( int i = 0; i < this.methods.size(); i++ )
        {
            if( this.methods.get( i ).getName().equals( name ) )
            {
                byName.add( this.methods.get( i ) );
            }
        }
        return byName;
    }

    private void verifyAndAddMethod( _method m )
    {
        methods.add( m );
    }

    public _methods remove( List<_method> toRemoveMethods )
    {
        for( int i = 0; i < toRemoveMethods.size(); i++ )
        {
            remove( toRemoveMethods.get( i ) );
        }
        return this;
    }

    public _methods remove( _method _m )
    {
        this.methods.remove( _m );
        return this;
    }

    @Override
    public _methods replace( String target, String replacement )
    {
        //Map<String, List<_method>> replacedMethods
        //    = new HashMap<String, List<_method>>();

        for( int j = 0; j < methods.size(); j++ )
        {
            _method thisOne = methods.get( j );
            thisOne.replace( target, replacement );
        }
        return this;
    }

    public _methods add( _method... methods )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            _methods.this.add( methods[ i ] );
        }
        return this;
    }

    public _methods add( _methods methods )
    {
        String[] methodNames = methods.getNames();

        for( int i = 0; i < methodNames.length; i++ )
        {
            List<_method> byName = methods.getByName( methodNames[ i ] );
            for( int j = 0; j < byName.size(); j++ )
            {
                this.add( byName.get( i ) );
            }
        }
        return this;
    }

    /**
     * model of a method
     */
    public static class _method
        implements _Java, _facet, Authored, Annotated
    {
        public static final Template METHOD
            = BindML.compile(
                "{+javadoc+}"
                + "{+annotations+}"
                + "{+signature*+}" + N
                + "{" + N
                + "{+$>(methodBody)+}" + N
                + "}" );

        //abstract, native methods with no body
        public static final Template NO_BODY_METHOD
            = BindML.compile(
                "{+javadoc+}"
                + "{+annotations+}"
                + "{+signature*+};" + N );

        /**
         * Holds builder params to simplify construction / initialization (we
         * use the static of(...) "builder" method.)
         */
        private static class MethodParams
        {
            _javadoc javadoc; // starts with /* ends with */
            _anns annots = new _anns(); //starts with @
            String signature;
            List<Object> body = new ArrayList<Object>(); //anything AFTER signature is populated
        }

        public _method setModifiers( String... modifiers )
        {
            this.signature.getModifiers().set( modifiers );
            return this;
        }

        public _method setParameters( _parameters params )
        {
            this.signature.setParameters( params );
            return this;
        }
        
        public _method setParameters( String parameters )
        {
            this.signature.params = _parameters.of( parameters );
            return this;
        }

        public _parameter getParameter( int index )
        {
            if( this.signature.params.count() > index )
            {
                return this.signature.params.getAt( index );
            }
            throw new ModelException( "No parameter at index [" + index + "]" );
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit( this );
        }

        /**
         *
         * @param mp
         * @param body
         */
        private static void addBody( MethodParams mp, Object body )
        {
            if( body.getClass().isArray() )
            {
                for( int i = 0; i < Array.getLength( body ); i++ )
                {
                    mp.body.add( Array.get( body, i ) );
                }
            }
            else if( body.getClass().isAssignableFrom( List.class ) )
            {
                List lBody = (List)body;
                mp.body.addAll( lBody );
            }
            else
            {
                mp.body.add( body );
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
        public static _method of( Object... parts )
        {
            MethodParams mp = new MethodParams();
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
                else if( parts[ i ] instanceof _ann )
                {
                    mp.annots.add( (_ann)parts[ i ] );
                }
                else if( parts[ i ] instanceof _annotations )
                {
                    mp.annots = (_anns)parts[ i ];
                }
                else if( parts[ i ] instanceof Class )
                {
                    if( ((Class)parts[ i ]).isAnnotation() )
                    {
                        mp.annots.add( ((Class)parts[ i ]).getCanonicalName() );
                    }
                }
            }

            _method _f = new _method( mp.signature );
            for( int i = 0; i < mp.annots.count(); i++ )
            {
                _f.annotate( mp.annots.getAt( i ) );
            }
            if( mp.javadoc != null && !mp.javadoc.isEmpty() )
            {
                _f.javadoc( mp.javadoc.getComment() );
            }
            if( mp.body != null && !mp.body.isEmpty() )
            {
                _f.body( mp.body.toArray() );
            }
            else if( !mp.signature.endsWith( ";" )
                && !_f.getModifiers().contains( "abstract" ) )
            { //let me add a blank body if they didnt end the signature with a ;
                _f.body( _code.of( "" ) );
            }
            return _f;
        }

        private static void partFromString( MethodParams mp, String component )
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
                mp.annots.add( _ann.of( component ) );
            }
            else
            {
                mp.signature = (String)component;
            }
        }

        public String getName()
        {
            return this.signature.getName();
        }

        /**
         * Adds a parameter to this method
         */
        public _method addParameter( String type, String name )
        {
            this.signature.params.add( type, name );
            return this;
        }

        public _method setName( String name )
        {
            this.signature.setName( name );
            return this;
        }

        public _method setBody( _code body )
        {
            this.methodBody = body;
            return this;
        }

        public _method setBody( Object... body )
        {
            this.methodBody = _code.of( body );
            return this;
        }

        @Override
        public _anns getAnnotations()
        {
            return this.annotations;
        }

        @Override
        public _method annotate( _anns annotations )
        {
            this.annotations = annotations;
            return this;
        }
        
        public _javadoc getJavadoc()
        {
            return this.javadoc;
        }

        public static _method cloneOf( _method _proto )
        {
            return new _method( _proto );            
        }

        public _signature getSignature()
        {
            return signature;
        }

        private _javadoc javadoc;
        private _anns annotations;
        private _signature signature;
        private _code methodBody;

        public boolean isAbstract()
        {
            return this.signature.modifiers.containsAny( Modifier.ABSTRACT );
        }

        public _method( _signature sig )
        {
            this.signature = sig;
            this.annotations = new _anns();
            this.methodBody = new _code();
            this.javadoc = new _javadoc();
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( javadoc, signature, annotations, methodBody.author().trim() );
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
            final _method other = (_method)obj;
            if( !Objects.equals( this.javadoc, other.javadoc ) )
            {
                //System.out.println( "JAVADOC" );
                return false;
            }
            if( !Objects.equals( this.annotations, other.annotations ) )
            {
                //System.out.println( "ANNOTATIONS" );
                return false;
            }
            if( !Objects.equals( this.signature, other.signature ) )
            {
                //System.out.println( "SIGNATURE" );
                return false;
            }
            if( !Objects.equals( this.methodBody, other.methodBody ) )
            {
                //System.out.println( "BODY" );
                return false;
            }
            return true;
        }

        public _method( String methodSignature, Object... bodyLines )
        {
            this( _signature.of( methodSignature ) );
            this.methodBody = _code.of( bodyLines );
        }

        public _method( _method prototype )
        {
            this.annotations = _anns.cloneOf( prototype.annotations );
            this.javadoc = _javadoc.cloneOf( prototype.javadoc );
            this.methodBody = _code.cloneOf( prototype.methodBody );
            this.signature = _signature.cloneOf( prototype.signature );
        }
        
        public _code getBody()
        {
            return this.methodBody;
        }

        public _method annotate( _ann... annotations )
        {
            this.annotations.add( annotations );
            return this;
        }

        private _method body( _code body )
        {
            this.methodBody = body;
            return this;
        }

        /**
         * Add code to the end (tail) of the current body of code
         *
         * EXACTLY the same as method.addTailCode
         *
         * @param linesOfCode code to add
         * @return this
         */
        public _method add( Object... linesOfCode )
        {
            this.methodBody.addTailCode( linesOfCode );
            return this;
        }

        /**
         * Sets / replaces the code in the method body
         *
         * @param linesOfCode
         * @return
         */
        public _method body( Object... linesOfCode )
        {
            if( this.isAbstract() && methodBody != null
                && linesOfCode != null
                && linesOfCode.length > 0 )
            {
                throw new ModelException(
                    "Abstract methods : " + N + signature + N
                    + "cannot have a method body" );
            }
            this.methodBody = _code.of( linesOfCode );
            return this;
        }

        public _method javadoc( _javadoc javadoc )
        {
            this.javadoc = javadoc;
            return this;
        }

        public _method javadoc( String javadocComment )
        {
            this.javadoc = new _javadoc( javadocComment );
            return this;
        }

        @Override
        public String toString()
        {
            return author();
        }

        @Override
        public Context getContext()
        {
            return VarContext.of( "javadoc", javadoc,
                "annotations", annotations,
                "signature", signature,
                "methodBody", methodBody );
        }

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public Template getTemplate()
        {
            if( this.methodBody == null || this.methodBody.isEmpty() )
            {
                return NO_BODY_METHOD;
            }
            return METHOD;
        }
        
        
        @Override
        public String author( Directive... directives )
        {
            if( this.methodBody == null || this.methodBody.isEmpty() )
            {
                return Author.toString(
                    NO_BODY_METHOD,
                    getContext(),
                    directives );
            }
            return Author.toString( METHOD,
                getContext(),
                directives );
        }

        public String getReturnType()
        {
            return this.signature.getReturnType();
        }

        public _modifiers getModifiers()
        {
            return this.signature.getModifiers();
        }

        public _parameters getParameters()
        {
            return this.signature.getParameters();
        }

        public _throws getThrows()
        {
            return this.signature.getThrows();
        }

        public _method setReturnType( Class clazz )
        {
            this.signature.returnType = clazz.getCanonicalName();
            return this;
        }
        
        public _method setReturnType( String returnType )
        {
            this.signature.returnType = returnType;
            return this;
        }

        public static class _signature
            implements _Java, Authored
        {
            public _signature( _signature prototype )
            {
                this.genericTypeParams = _typeParams.cloneOf( prototype.genericTypeParams );
                this.methodName = prototype.methodName;
                this.modifiers = _modifiers.cloneOf( prototype.modifiers );
                this.params = _parameters.cloneOf( prototype.params );
                this.returnType = prototype.returnType;
                this.throwsExceptions = _throws.cloneOf( prototype.throwsExceptions );
            }
            public static _signature cloneOf( _signature prototype )
            {
                return new _signature( prototype );                
            }
            

            private _modifiers modifiers;
            private _typeParams genericTypeParams;
            private String returnType;
            private String methodName;
            private _parameters params;
            private _throws throwsExceptions;

            public _signature(
                _modifiers modifiers,
                _typeParams genericTypeParams,
                String returnType,
                String methodName,
                _parameters params,
                _throws throwsExceptions )
            {
                this.modifiers = modifiers;
                this.genericTypeParams = genericTypeParams;
                this.returnType = returnType;
                this.methodName = methodName;
                this.params = params;
                this.throwsExceptions = throwsExceptions;
            }

            @Override
            public void visit( ModelVisitor visitor )
            {
                visitor.visit( this );
            }

            public _signature setParameters( _parameters _ps )
            {
                this.params = _ps;
                return this;
            }
            public _signature setGenericTypeParams( _typeParams _tp )
            {
                this.genericTypeParams = _tp;
                return this;
            }

            @Override
            public int hashCode()
            {
                return Objects.hash( this.genericTypeParams, this.methodName,
                    this.modifiers, this.params, this.returnType,
                    this.throwsExceptions );
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
                if( !Objects.equals( this.returnType, other.returnType ) )
                {
                    return false;
                }
                if( !Objects.equals( this.methodName, other.methodName ) )
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

            public _typeParams getGenericTypeParams()
            {
                return this.genericTypeParams;
            }

            public _signature setName( String name )
            {
                this.methodName = name;
                return this;
            }

            public _signature setReturnType( Object returnType )
            {
                if( returnType == null )
                {
                    this.returnType = "void";
                }
                else
                {
                    this.returnType = JavaTranslate.INSTANCE.translate( returnType );
                }
                return this;
            }

            public _signature setModifiers( _modifiers mods )
            {
                this.modifiers = mods;
                return this;
            }

            public _signature setThrows( Object throwsException )
            {
                this.throwsExceptions = _throws.of( throwsException );
                return this;
            }

            public _signature setThrows( _throws throwsExceptions )
            {
                this.throwsExceptions = throwsExceptions;
                return this;
            }

            public _modifiers getModifiers()
            {
                return this.modifiers;
            }

            public String getReturnType()
            {
                return returnType;
            }

            @Override
            public _signature replace( String target, String replacement )
            {
                this.returnType
                    = RefRenamer.apply( this.returnType, target, replacement );
                //this.returnType.replace( target, replacement );
                this.params.replace( target, replacement );
                this.genericTypeParams.replace( target, replacement );
                this.modifiers.replace( target, replacement );
                this.methodName
                    = RefRenamer.apply( this.methodName, target, replacement );
                //this.methodName.replace( target, replacement );
                this.throwsExceptions.replace( target, replacement );
                return this;
            }

            public String getName()
            {
                return methodName;
            }

            public _parameters getParameters()
            {
                return params;
            }

            public _throws getThrows()
            {
                return this.throwsExceptions;
            }

            public static _method._signature of( String methodSignature )
            {
                //System.out.println ( "OF "+ methodSignature );

                //to parse, just make it a compilation unit by prepending
                if( methodSignature.endsWith( ";" ) )
                {
                    methodSignature = methodSignature.substring( 0, methodSignature.length() - 1 );
                }
                try
                {
                    CompilationUnit cu
                        = JavaAst.astFrom( "interface A { " + methodSignature + "{} }" );

                    //now just pull out the ONLY method
                    MethodDeclaration md
                        = JavaAst.findAllMethods( JavaAst.findTypeDeclaration( cu, "A" ) )[ 0 ];

                    List<TypeParameter> tps = md.getTypeParameters();

                    _typeParams _tp = new _typeParams();
                    for( int i = 0; i < tps.size(); i++ )
                    {
                        //System.out.println( "TYPE PARAMS " );
                        _tp.addParam( tps.get( i ).toString() );
                    }
                    _parameters _ps = new _parameters();
                    for( int i = 0; i < md.getParameters().size(); i++ )
                    {
                        Parameter astP = md.getParameters().get( i );

                        _parameter _p; // = new _parameter();
                        if( astP.isVarArgs() )
                        {
                            if( i < md.getParameters().size() - 1 )
                            {
                                throw new ModelException(
                                    "varArgs Argument \"" + astP.toString()
                                    + "\" not the last arg of method" );
                            }
                            _p = new _parameter( astP.getType().toString() + "...", astP.getName() );
                            //_ps.add(  );
                        }
                        else
                        {
                            _p = new _parameter( astP.getType().toString(), astP.getName() );
                            //_ps.add(  );
                        }
                        int modifiers = astP.getModifiers();
                        if( (modifiers & Modifier.FINAL) == Modifier.FINAL )
                        {
                            _p.setFinal();
                        }
                        List<AnnotationExpr> anns = astP.getAnnotations();
                        if( anns != null && anns.size() > 0 )
                        {
                            for( int j = 0; j < anns.size(); j++ )
                            {
                                AnnotationExpr astAnn = anns.get( j );
                                _p.annotate( astAnn.toString() );
                            }
                        }
                        _ps.add( _p );
                        //_parameter.of( astP.getType().toString(), astP.getName().toString() ) );
                    }

                    List<ReferenceType> astThrow = md.getThrows();
                    _throws _t = new _throws();
                    for( int i = 0; i < astThrow.size(); i++ )
                    {
                        _t.addThrows( astThrow.get( i ).toString() );
                    }

                    _modifiers mods = _modifiers.of( md.getModifiers() );
                    if( md.isDefault() )
                    {
                        mods.set( "default" );
                    }
                    if( mods.containsAny( Modifier.TRANSIENT, Modifier.VOLATILE ) )
                    {
                        throw new ModelException(
                            "Invalid Modifiers for method; (cannot be transient or volatile)" );
                    }
                    if( mods.containsAll( Modifier.ABSTRACT, Modifier.FINAL ) )
                    {
                        throw new ModelException(
                            "Invalid Modifiers for method; (cannot be BOTH abstract and final )" );
                    }
                    if( mods.containsAll( Modifier.ABSTRACT, Modifier.NATIVE ) )
                    {
                        throw new ModelException(
                            "Invalid Modifiers for method; (cannot be BOTH abstract and native )" );
                    }
                    if( mods.containsAll( Modifier.ABSTRACT, Modifier.PRIVATE ) )
                    {
                        throw new ModelException(
                            "Invalid Modifiers for method; (cannot be BOTH abstract and private )" );
                    }
                    if( mods.containsAll( Modifier.ABSTRACT, Modifier.STATIC ) )
                    {
                        throw new ModelException(
                            "Invalid Modifiers for method; (cannot be BOTH abstract and static )" );
                    }
                    if( mods.containsAll( Modifier.ABSTRACT, Modifier.STRICT ) )
                    {
                        throw new ModelException(
                            "Invalid Modifiers for method; (cannot be BOTH abstract and strictfp )" );
                    }
                    if( mods.containsAll( Modifier.ABSTRACT, Modifier.SYNCHRONIZED ) )
                    {
                        throw new ModelException(
                            "Invalid Modifiers for method; (cannot be BOTH abstract and synchronized )" );
                    }
                    _method._signature _ms = new _method._signature(
                        mods,
                        _tp, //generic Type parameters
                        md.getType().toString(), //return type
                        md.getName(),
                        _ps,
                        _t );

                    return _ms;
                }
                catch( ParseException pe )
                {
                    throw new ModelException(
                        "Unable to parse method signature \"" + methodSignature
                        + "\"", pe );
                }
            }

            
            /**
             * Does this method signature "match" this other method signature?
             *
             * @param sig the signature
             * @return true if the signatures match (meaning these two methods
             * cannot exist on the same (enum, class)
             */
            public boolean matchesSignature( _signature sig )
            {
                if( sig.methodName.equals( this.methodName ) )
                {
                    if( sig.params.count() == this.params.count() )
                    {
                        for( int i = 0; i < sig.params.count(); i++ )
                        {
                            //System.out.println( "TYPE 1:"+sig.params.getAt( i ).toString() );
                            //System.out.println( "TYPE 2:"+this.params.getAt( i ).toString() );
                            if( !sig.params.getAt( i ).getType().equals(
                                this.params.getAt( i ).getType() ) )
                            {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                }
                return false;
            }

            public static final Template METHOD_SIGNATURE
                = BindML.compile(
                    "{+modifiers+}{+genericTypeParams+}{+returnType+} {+methodName+}{+params+}{+throws+}" );

            @Override
            public String author()
            {
                return author( new Directive[ 0 ] );
            }

            @Override
            public Context getContext()
            {
                return VarContext.of(
                    "modifiers", modifiers,
                    "genericTypeParams", this.genericTypeParams,
                    "returnType", returnType,
                    "methodName", methodName,
                    "params", params,
                    "throws", throwsExceptions );
            }
            
            @Override
            public Template getTemplate()
            {
                return METHOD_SIGNATURE;
            }
                
            @Override
            public String author( Directive... directives )
            {
                return Author.toString( METHOD_SIGNATURE,
                    getContext(),
                    directives );
            }

            @Override
            public String toString()
            {
                return author();
            }
        } //end of signature

        /**
         * searches through the contents to find target and replaces with
         * replacement
         */
        @Override
        public _method replace( String target, String replacement )
        {
            this.javadoc.replace( target, replacement );
            this.annotations.replace( target, replacement );
            this.methodBody.replace( target, replacement );
            this.signature.replace( target, replacement );
            return this;
        }
    }
}
