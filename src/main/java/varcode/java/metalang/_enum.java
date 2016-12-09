package varcode.java.metalang;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.Model;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.java.JavaCase;
import varcode.java.JavaCase.JavaCaseAuthor;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.metalang._constructors._constructor;
import varcode.java.metalang._enum._constants._constant;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._methods._method;
import varcode.markup.bindml.BindML;

/**
 * MetaLang model 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _enum
    implements JavaCaseAuthor, _javaComponent
{
    private _package enumPackage = new _package( "" ); 
    private _imports imports = new _imports();
    private _javadoc javadoc = new _javadoc();
    private _annotations annotations;
    private _signature signature;
    private _constructors constructors = new _constructors();
    private _staticBlock staticBlock = new _staticBlock( (Object[])null );
    private _fields fields = new _fields();
    private _constants constants = new _constants();
    private _methods methods = new _methods();
    private _nests nesteds = new _nests();
	
    public static final Dom ENUM = 
        BindML.compile( 
            "{+pckage+}" +
            "{{+?imports:{+imports+}" + N +"+}}" +
            "{+javadocComment+}" +
            "{+enumAnnotations+}" +        
            "{+enumSignature*+}" + N +
            "{" + N +
            "{+values+}" + N +
            "{{+?members:{+$>(members)+}" + N +
            "+}}" +
            "{{+?constructors:{+$>(constructors)+}" + N +
            "+}}" +
            "{{+?methods:{+$>(methods)+}" + N + 
            "+}}" +
            "{{+?nested:{+$>(nested)+}" + N + 
            "+}}" +
            "{{+?staticBlock:{+$>(staticBlock)+}" + N +
            "+}}" +
            "}" );
	
    public static _enum cloneOf(_enum prototype )
    {
        return new _enum( prototype );
    }
	
    public _enum add( _facet... facets )
    {
        for( int i = 0; i < facets.length; i++ )
        {
            add( facets[ i ] );
        }
        return this;
    }
    
    public _enum add( _facet facet )
    {
        if( facet instanceof _annotations._annotation )
        {
            if(this.annotations == null)
            {
                this.annotations = new _annotations();
            }
            this.annotations.add( facet );
            return this;
        }
        if( facet instanceof _constructor )
        {
            this.constructors.addConstructor( (_constructor)facet );
            return this;
        }
        if( facet instanceof _field )
        {
            this.fields.addFields( (_field)facet );
            return this;
        }
        if( facet instanceof _method )
        {
            this.methods.addMethod( (_method)facet );
            return this;
        }
        if( facet instanceof _modifiers._modifier )
        {
            this.getModifiers().set( (_modifiers._modifier)facet );
            return this;
        }        
        throw new ModelException(
            "Unsupported facet " + facet + " for _enum" );
        
    }
    
    public _modifiers getModifiers()
    {
        return this.getSignature().getModifiers();
    }
    
    public _enum( _enum prototype )
    {
        this.enumPackage = _package.cloneOf( prototype.enumPackage );
        this.imports = _imports.cloneOf( prototype.imports );
        this.javadoc = _javadoc.cloneOf( prototype.javadoc );
        this.annotations = _annotations.cloneOf( prototype.annotations );
        this.signature = _signature.cloneOf(prototype.signature  );
        this.constructors = _constructors.cloneOf( prototype.constructors );
        if( prototype.staticBlock!= null && !prototype.staticBlock.isEmpty() )
        {
            this.staticBlock = _staticBlock.of( prototype.staticBlock.getBody() );
        }
        else
        {
            this.staticBlock = new _staticBlock();
        }
        this.fields = _fields.cloneOf( prototype.fields );
        this.constants = _constants.cloneOf(prototype.constants );
        this.methods = _methods.cloneOf( prototype.methods );		
        this.nesteds = _nests.cloneOf(prototype.nesteds );
    }
    
    /** Creates and returns a clone of this component 
     * @return a deep clone of this component
     */
    public _enum clone()
    {
        return cloneOf( this );
    }
    
    @Override
    public _enum bind( VarContext context )
    {
        this.enumPackage.bind( context);
        this.imports.bind( context );
        this.javadoc.bind( context );
        this.annotations.bind( context );
        this.signature.bind( context );
        this.constructors.bind( context );
        this.staticBlock.bind( context );
        this.fields.bind( context );
        this.constants.bind( context );
        this.methods.bind( context );
        this.nesteds.bind( context );
        return this;
    }
    
    /** sets the name of the class, (and the constructors) and return */
    public _enum setName( String name )
    {
        this.getSignature().enumName = name;
        _constructors cs = this.getConstructors();
        for(int i=0; i< cs.count(); i++ )
        {
            cs.getAt( i ).setName( name );
        }
        return this;
    }
        
    @Override
    public String getName()
    {
        return this.signature.getName();
    }
    
    public String getPackageName()
    {
        if( this.enumPackage.isEmpty() ) 
        {
            return "";
        }    
        return this.enumPackage.getName();
    }
    
    public _annotations getAnnotations()
    {
        return this.annotations;
    }
    
    @Override
    public VarContext getContext()
    {
	String[] n = null;
	if( nesteds.count() > 0 )
	{
            //I need to go to each of the nested classes/ interfaces/ etc.
            // and read what thier imports are, then add these imports to my imports
            String[] nested = new String[ nesteds.count() ];
            for( int i = 0; i < nesteds.count(); i++ )
            {
		_javaComponent _comp = nesteds.components.get( i );
		VarContext vc = _comp.getContext();
                vc.getScopeBindings().remove( "pckage" );
		vc.getScopeBindings().remove( "imports" );
		nested[ i ] = Compose.asString( _comp.getDom(), vc );				
            }
            n = nested;			
	}
	_imports _imp = null;
	if( this.getImports().count() > 0 )
	{
            _imp = this.getImports();
	}
	_constructors _ctors = null;
	if( constructors.count() > 0 )
	{
            _ctors = constructors; 
	}
	_fields _fs = null;
	if( this.fields != null && this.fields.count() > 0 )
	{
            _fs = this.fields;
	}
	_methods _ms = null;
	if( this.methods != null && this.methods.count() > 0 )
	{
            _ms = this.methods;
	}			
	return VarContext.of(
            "pckage", this.enumPackage, 
            "imports", _imp, 
            "javadocComment", javadoc,
            "enumSignature", signature,
            "constructors", _ctors,
            "staticBlock", staticBlock,
            "members", _fs,
            "values", constants,
            "nested", n,
            "methods", _ms );
	}
	
    public Class loadClass( )
    {
        return toJavaCase().loadClass();
    }
    
    public _enum implement( Class...implementClass )
    {
        this.signature.implementsFrom.implement( implementClass );
        return this;
    }
    
    public _enum implement( String...implementClass )
    {
        this.signature.implementsFrom.implement( implementClass );
        return this;
    }
    
    public Class loadClass( AdHocClassLoader adHocClassLoader )
    {
        return toJavaCase().loadClass( adHocClassLoader );
    }
    
    @Override
	public JavaCase toJavaCase( Directive... directives) 
	{	
		return JavaCase.of(
			getFullyQualifiedClassName(),
			ENUM, 
			getContext(),
			directives );
	}
    
    @Override
	public JavaCase toJavaCase( VarContext context, Directive...directives ) 
	{
        String FullClassName = this.getFullyQualifiedClassName();
        Dom classNameDom = BindML.compile( FullClassName );
        
        String theClassName = Compose.asString( classNameDom, context ); 
        
        //first lets print out the structure and optional Marks
        String authored = this.author( directives );
        
        //now compile the marks and fill them in with the context
	return JavaCase.of(
            theClassName,
            BindML.compile( authored ), 
            context,
            directives );			
    }
	
    public _package getPackage()
    {
        return this.enumPackage;
    }
    
    public _javadoc getJavadoc()
    {
        return this.javadoc;
    }
    
    public _signature getSignature()
    {
        return this.signature;
    }
    
    public _constructors getConstructors()
    {
        return this.constructors;
    }
    
    public _staticBlock getStaticBlock()
    {
        return this.staticBlock;
    }
    
    @Override
    public _fields getFields()
    {
        return this.fields;
    }
    
    public _constants getValueConstructs()
    {
        return this.constants;
    }
    
    @Override
    public _methods getMethods()
    {
        return this.methods;
    }
    
    @Override
    public _nests getNesteds()
    {
        return this.nesteds;
    }
    
    @Override
    public int getNestedCount()
    {
        return this.nesteds.count();
    }
    
    /** 
     * Returns a String[] representing all class Names (for the top level
     * and all nested classes, enums, interfaces)
     * 
     * @return all Names for the 
     */
    @Override
    public List<String> getAllNestedClassNames( 
        List<String>nestedClassNames, String containerClassName )
    {         
        for( int i = 0; i < this.nesteds.count(); i++ )
        {
            _javaComponent nest = this.nesteds.getAt( i );
            String nestedClassName = nest.getName();
            String thisNestClassName = containerClassName + ".$" + nestedClassName;
            nestedClassNames.add(  thisNestClassName );
            for( int j = 0; j< nest.getNestedCount(); j++ )
            {
                nestedClassNames = 
                     nest.getAllNestedClassNames( 
                        nestedClassNames, thisNestClassName );
            }    
        }
        return nestedClassNames;
    }
    
    public _javaComponent getNestedByName( String name )
    {
        return this.nesteds.getByName( name ); 
    }
    
    @Override
    public _javaComponent getNestedAt( int index )
    {
        return this.nesteds.getAt(  index );
    }
    
    public String getFullyQualifiedClassName()
    {	
        if( this.enumPackage != null && ! this.enumPackage.isEmpty() )
        {
            return this.enumPackage.getName() + "." 
                + this.signature.getName();
        }
        else
        {
            return this.signature.getName();
        }
    }
	
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
        
    @Override
    public String author( Directive... directives ) 
    {
	return toJavaCase( directives ).toString();
    }

    @Override
    public String toString()
    {
	return author();
    }

    private static class EnumParams
    {
        _license license;
        _package pack;    /* package io.varcode....*/
        _imports imports = new _imports(); // "import Java.lang", Class
        _javadoc javadoc; // starts with /* ends with */
        _annotations annots = new _annotations(); //starts with @
        _enum._signature signature; //starts with 
        List<_facet> facets = new ArrayList<_facet>();        
    }
    
    public static _enum of( Object... components )
    {
        EnumParams eParams = new EnumParams();
        for( int i = 0; i < components.length; i++ )
        {
            if( components[ i ] instanceof String )
            {
                fromString(eParams, (String)components[ i ] );
            }
            else if( components[ i ] instanceof Class )
            {
                if( ((Class)components[i]).isAnnotation() )
                {
                    eParams.annots.add( components[ i ] );
                    eParams.imports.addImport( components[ i ] );
                }
                else
                {
                    eParams.imports.addImport( components[ i ] );
                }
            }
            else if( components[ i ] instanceof _license )
            {
                eParams.license = (_license)components[ i ];
            }
            else if( components[ i ] instanceof _package )
            {
                eParams.pack = (_package)components[ i ];
            }
            else if( components[ i ] instanceof _imports )
            {
                eParams.imports = (_imports)components[ i ];
            }
            else if( components[ i ] instanceof _javadoc )
            {
                eParams.javadoc = (_javadoc)components[ i ];
            }
            else if( components[ i ] instanceof _annotations )
            {
                eParams.annots = (_annotations)components[ i ];
            }
            else if( components[ i ] instanceof _class._signature )
            {
                eParams.signature = (_enum._signature)components[ i ];
            }
            else if( components[ i ] instanceof _facet )
            {
                eParams.facets.add( (_facet)components[ i ] );
            }
        }
        _enum _e = new _enum( eParams.signature );
        for( int i = 0; i < eParams.annots.count(); i++ )
        {
            _e.annotate(eParams.annots.getAt( i ) );
        }
        for( int i = 0; i < eParams.imports.count(); i++ )
        {
            _e.imports(eParams.imports.getImports().toArray( new Object[ 0 ] ) );
        }
        for( int i = 0; i < eParams.facets.size(); i++ )
        {
            _e.add(eParams.facets.get( i ) );
        }
        if( eParams.javadoc != null && !eParams.javadoc.isEmpty())
        {
            _e.javadoc(eParams.javadoc.getComment() );
        }
        /*
        if( cd.license != null ) //TODO fix
        {
            _e.codeLicense( cd.license.author() );
        }
        */
        if( eParams.pack != null && !eParams.pack.isEmpty() )
        {
            _e.packageName(eParams.pack.getName() );
        }
        return _e;
    }
    
    private static void fromString( EnumParams cd, String component )
    {
        if( component.startsWith( "/**" ))
        {
            cd.javadoc = _javadoc.of( component.substring(3, component.length() -2 ) );            
        }
        else if( component.startsWith( "/*" ))
        {
            cd.javadoc = _javadoc.of( component.substring(2, component.length() -2 ) );            
        }
        else if( component.startsWith( "package ") )
        {
            cd.pack = _package.of( component );
        }        
        else if( component.startsWith( "@" ) )
        {
            cd.annots.add( _annotations._annotation.of( component ) );
        }        
        else
        {
            String[] tokens = component.split( " " );
            if( tokens.length == 1 )
            {
                if( tokens[ 0 ].indexOf( "." ) > 0 )
                {
                    cd.pack = _package.of( tokens[ 0 ] );
                }
                else                
                {
                    cd.signature = _enum._signature.of( component ); 
                }
            } 
            else
            {
                cd.signature = _enum._signature.of( component ); 
            }
        }        
    }
    /*
    public static _enum of( String enumSignature )
    {
        _signature sig = _signature.of( enumSignature );
        _enum e = new _enum( sig );
        return e;
    }
	
    public static _enum of( Package pkg, String enumSignature )
    {
        _signature sig = _signature.of( enumSignature );
        _enum e = new _enum( sig );
        e.packageName( pkg.getName() );
        return e;
    }
	
    public static _enum of( String packageName, String enumSignature )
    {
        _signature sig = _signature.of( enumSignature );
        _enum e = new _enum( sig );
        e.packageName( packageName );
        return e;
    }
    
    public static _enum of( String javadoc, String packageName, String enumSignature )
    {
        _signature sig = _signature.of( enumSignature );
            _enum e = new _enum( sig );
            e.packageName( packageName );
            
        e.javadoc( javadoc );
            return e;
    }
    */
    
    public _enum( _signature signature )
    {
	this.signature = signature;
    }
	
    public _enum(
	_package enumPackage, 
	_imports imports, 
	_javadoc javadocComment, 
	_signature signature,		
	_fields members,
	_staticBlock staticBlock,
	_methods methods,
	_nests nested )
    {
	this.enumPackage = enumPackage;
	this.imports = imports;
	this.javadoc = javadocComment;
	this.signature = signature;
	this.staticBlock = staticBlock;
	this.fields = members;
	this.methods = methods;
	this.nesteds = nested;
    }
	
    public _enum packageName( String packageName )
    {
	return packageName( _package.of( packageName ) );
	}
	
	public _enum packageName( _package packageImpl )
	{
		this.enumPackage = packageImpl;
		return this;
	}
	

	/**
	 * <PRE>{@code 
	 * _class MyAClass = new _class("public A")
	 *     .addConstructor("public A( String name )", "this.name = name;");}</PRE>
	 *     
	 * @param constructorSignature
	 * @param body
	 * @return
	 */
	public _enum constructor( String constructorSignature, Object... body )
	{
		_constructors._constructor c = 
            new _constructors._constructor( constructorSignature )
                .body( body );
        if( c.getSignature().getModifiers().containsAny( 
                Modifier.PUBLIC, Modifier.PROTECTED ) )
        {
            throw new ModelException( 
                "Enum constructors cannot be public or protected" );
        }
		return constructor( c );
	}
	
	public _enum constructor( _constructor c )
	{
		constructors.addConstructor( c );
        if( c.getSignature().getModifiers().containsAny( 
                Modifier.PUBLIC, Modifier.PROTECTED ) )
        {
            throw new ModelException( 
                "Enum constructors cannot be public or protected" );
        }
		return this;
	}
	
	public _enum nest( _javaComponent component )
	{
		this.nesteds.add( component );
		return this;
	}
	
	public _enum method( String methodSignature, String... bodyLines )
	{
		return method( _method.of( (_javadoc)null, methodSignature, (Object[]) bodyLines ) );
	}

	public _enum method( _method m )
	{
		if( m.isAbstract() )
		{
			throw new ModelException(
				"Cannot add an abstract method " + N + m + N + " to an enum " );
		}
		this.methods.addMethod( m );
		return this;
	}
	
    public _enum importsStatic( Object... imports )
    {
        this.imports.addStaticImports( imports );
        return this;
    }
    
	public _enum imports( Object...imports )
	{
		this.imports.addImports( imports );
		return this;
	}
	
    @Override
    public _imports getImports()
    {
	for( int i = 0; i < nesteds.count(); i++ )
	{
            this.imports.merge( 
                nesteds.components.get( i ).getImports() );
	}
	return this.imports;
    }
	
    public _enum annotate( Object... annotations )
    {
        if( this.annotations == null )
        {
            this.annotations = new _annotations();
        }
        this.annotations.add( annotations );
        return this;
    }
    
    public _enum javadoc( String... comment )
    {
        this.javadoc = new _javadoc( comment );
        return this;    
    }
	
    
    public _enum constant( String name, Object...arguments )
    {
        return constant( _constant.of( name, arguments ) );
    }
	
    public _enum constant( _constant constants )
    {
	this.constants.addConstant( constants );
	return this;
    }
    
    public _enum constants( _constants constants )
    {
        for( int i = 0; i < constants.count(); i++ )
        {
            this.constant( constants.getAt( i ) );
        }
        return this;
    }
    
    public _enum field( _field field )
    {
        fields.addFields( field );
        return this;
    }
     public _enum field( _modifiers mods, String type, String name )
    {
        _field f = new _field( mods, type, name );
        return field( f );
    }
    
    public _enum field( int mods, String type, String name )
    {
        _field f = new _field( mods, type, name );
        return field( f );
    }
    
    public _enum field( int modifiers, String type, String name, String init )
    {
        return field( new _field( modifiers, type, name, init ) );        
    }
    
    
    /** 
     * adds a field to the enum<PRE> 
     * myEnum.field( "public final int value;" );
     * myEnum.field( "private String value = \"Butler\";" );
     * </PRE>
     * @param field field to add
     * @return this (modified)
     */
	public _enum field( String field )
	{
		fields.addFields(_fields._field.of( field ) );		
		return this;
	}
	
	public _enum staticBlock( Object... staticBlockCode )
	{
		if( this.staticBlock == null )
		{
			this.staticBlock = new _staticBlock();
		}
		this.staticBlock.addTailCode( staticBlockCode );
		return this;
	}
	
	public _enum fields( String...fields )
	{
            for( int i = 0; i < fields.length; i++ )
		{
			this.fields.addFields( _fields._field.of( fields[ i ] ) );
		}
		return this;
	}
	
	public _enum fields( _fields._field...fields )
	{
		this.fields.addFields( fields );
		return this;
	}

    public _enum setModifiers( _modifiers mods )
    {
        if( this.signature == null )
        {
            System.out.println( "WE got real problems" );
        }
        if( this.signature.modifiers == null )
        {
            this.signature.modifiers = new _modifiers();
        }
        this.signature.modifiers = mods;
        return this;
    }
	    
	/**
	 * When we construct enums each enum value
	 * 
	 * must call a constructor
	 * so for example:
	 * <PRE>
	 * enum Octal
	 * {
	 *    _0(), _1(), _2(), _3(), _4(), _5(), _6(), _7();
	 * }
	 * </PRE>
	 * 
	 * you might also call the constructors with parameters
	 * 
	 * <PRE>
	 * enum Octal
	 * {
	 *    _0(0, "zero"), _1(1, "one"), _2(2, "two"), _3(3, "three"), 
	 *    _4(4, "four"), _5(5, "five"), _6(6, "six"), _7(7, "seven");
	 * }
	 * </PRE>
	 * this abstractions contains the List and verifies that
	 *    
	 */
    public static class _constants
        implements JavaMetaLang
    {
        /** TODO, cant I just iterate through each time w/o having to keep this around??*/		
	private List<_constant> constants = 
            new ArrayList<_constant>();
			
	public _constants addConstant( _constant constant )
	{
            for( int i = 0; i < this.constants.size(); i++ )
            {
                if( this.constants.get( i ).name.equals( constant.name ) )
                {
                    throw new ModelException( 
                        "Enum already contains a constant with name \"" + constant.name +"\"" );
                }
            }
            this.constants.add( constant );
            return this;
	}
		
		//the form is always
		// Identifier1( parameters, separated, by commas ), 
		// Identifier2( parameters, separated, by commas ),  
		//   -or-
		// Ideitifier1,
		// Ideitifier2,
		
		//NOTE : must be unique names
		
		public static _constants cloneOf( _constants prototype ) 
		{
			_constants clone = new _constants();
			for( int i = 0; i < prototype.count(); i++ )
			{
				clone.addConstant( _constant.cloneOf(prototype.constants.get( i ) ) );
			}
			return clone;
		}

        public _constant getByName( String name )
        {
            for( int i = 0; i < this.constants.size(); i++ )
            {
                if( this.constants.get( i ).name.equals( name ) )
                {
                    return this.constants.get( i );
                }
            }
            return null;
        }
        public _constant getAt( int index )
        {
            if( index < count() )
            {
                return this.constants.get( index );
            }
            throw new ModelException(
                "Invalid value construct index [" + index + "]");
        }
            
        @Override
        public _constants bind( VarContext context )
        {
            for( int i = 0; i < this.constants.size(); i++ )
            {
                this.constants.get( i ).bind( context );
            }
            return this;
        }
                
        @Override
        public _constants replace( String target, String replacement )
        {
            for( int i = 0; i < this.constants.size(); i++ )
            {
                this.constants.get( i ).replace( target, replacement );
            }
            return this;
        }
        
        /**
         * Individual Enum value const constructor
         */
	public static class _constant
            implements JavaMetaLang
	{
            private String name;
            private _arguments args;
			
            /** So:
             * <PRE>
             * ERIC( 42, "Michael" ),
             * ^^^^  ^^^^^^^^^^^^^
             * name    arguments
             * 
             * ERIC,
             * ^^^^
             * name
             * </PRE>
             * 
             * @param name the name iof the enumValue
             * @param arguments the arguments passed into the enum constructor
             * @return the valueConstruct with name and arguments
             */
            public static _constant of( String name, Object... arguments )
            {
		return new _constant( name, _arguments.of( arguments ) );								
            }
			
            public static _constant cloneOf( _constant construct )
            {
		return new _constant( 
                    construct.name,
                    _arguments.cloneOf( construct.args ) );					
            }
            
            public _constant( String name, _arguments args )
            {
		this.name = name;
		this.args = args;
            }
			
            public Dom CONST_CONSTRUCT = BindML.compile(
		"{+name*+}{+args+}" );			
    
            @Override
            public _constant bind( VarContext context )
            {
                this.name = Compose.asString( BindML.compile( this.name ), context );
                this.args.bind( context );
                return this;
            }
            
            @Override
            public String author( )
            {
                return author( new Directive[ 0 ] );
            }
            
            @Override
            public String author( Directive... directives ) 
            {
                VarContext vc = VarContext.of( "name", name );
		if( args != null && args.count() > 0 )
                {
                    vc.set( "args", args );
		}
		return Compose.asString( CONST_CONSTRUCT, vc, directives );
            }		
			
            @Override
            public String toString()
            {
		return author(); 
            }
            
            @Override
            public _constant replace( String target, String replacement )
            {
                this.args = this.args.replace( target, replacement );
                this.name = this.name.replace( target, replacement );
                return this;
            }
	}

	public Dom CONST_CONSTRUCTORS = BindML.compile(
            "{{+:    {+valueConstructs+}," + N + "+}};" + N + N );
		
        @Override
        public String author( )
        {
            return author( new Directive[ 0 ] );
        }
        
        @Override
	public String author( Directive... directives ) 
	{
            return Compose.asString( CONST_CONSTRUCTORS, 
		VarContext.of( "valueConstructs", constants ),
		directives );
	}
		
	public int count()
	{
            return this.constants.size();
	}
		
        @Override
	public String toString()
	{
            return author();
	}
    }
	
    /**
     * Enum Constructor signature
     */
    public static class _signature
	implements JavaMetaLang
    {
	private String enumName = "";		
	private _implements implementsFrom = new _implements();
	private _modifiers modifiers = new _modifiers();
		
	public static final Dom ENUM_SIGNATURE = 
            BindML.compile( "{+modifiers+}enum {+enumName*+}{+implementsFrom+}" );

        @Override
        public String author( )
        {
            return author( new Directive[ 0 ] );
        }
        
        @Override
	public String author( Directive... directives ) 
	{
            return Compose.asString( ENUM_SIGNATURE, 
                VarContext.of(
                    "enumName", enumName,
                    "modifiers", modifiers,
                    "implementsFrom", implementsFrom ), 
                    directives );
	} 
        
        @Override
        public _signature bind( VarContext context )
        {
            this.enumName = Compose.asString(BindML.compile(this.enumName), context );
            this.implementsFrom.bind( context );
            this.modifiers.bind( context );
            return this;
        }
        
	public static _signature cloneOf( _signature prototype ) 
	{
            _signature s = new _signature();
            s.enumName = prototype.enumName + "";
            s.implementsFrom = _implements.cloneOf( prototype.implementsFrom );
            s.modifiers = _modifiers.cloneOf( prototype.modifiers );
            return s; 			
	}

        @Override
	public String toString()
	{
            return author();
	}

        @Override
        public _signature replace( String target, String replacement )
        {
            this.enumName = this.enumName.replace(target, replacement);
            this.implementsFrom = this.implementsFrom.replace( target, replacement );
            this.modifiers = this.modifiers.replace( target, replacement );
            return this;
        }
        
	public _modifiers getModifiers()
	{
            return this.modifiers;
	}
        
	public _implements getImplements()
	{
            return this.implementsFrom;
	}
		
	public String getName()
	{
            return this.enumName;
	}
        
        /**
         * finds the index of the target token 
         * @param tokens
         * @param target
         * @return 
         */
        private static int indexOf( String[] tokens, String target )
        {
            for( int i = 0; i < tokens.length; i++ )
            {
                if( tokens[ i ].equals( target ) )
                {
                    return i;
                }
            }
            return -1;
        }
        
        public static _signature of( String enumSignature )
	{
            _signature sig = new _signature();
		
            String[] tokens = enumSignature.split(" ");
		
            int enumTokenIndex = -1;
            int implementsTokenIndex = -1;
		
            if( indexOf( tokens, "enum" ) < 0 )
            {
                //infer they want a public class
                String[] preamble = new String[ tokens.length + 2 ];  
                preamble[0] = "public";
                preamble[1] = "enum";
                System.arraycopy( tokens, 0, preamble, 2, tokens.length );
                tokens = preamble;
            }
            if( tokens.length < 2 )
            {
		throw new ModelException( 
                    "enum signature must have at least (2) tokens \"enum enumName\" " );	
            } 		
            for( int i = 0; i < tokens.length; i++ )
            {
		if( tokens[ i ].equals( "enum" ) )
                {
                    enumTokenIndex = i;
		}			
		else if( tokens[ i ].equals( "implements" ) )
		{
                    implementsTokenIndex = i;
		}
            }
		
            if( ( enumTokenIndex < 0 ) || ( enumTokenIndex >= tokens.length -1 ) )
            {   //cant be 
		throw new ModelException( 
                    "enum token cant be not found or the last token for \"" 
                    + enumSignature + "\"" ); 
            }
            sig.enumName = tokens[ enumTokenIndex + 1 ];
		
            if( enumTokenIndex > 0 )
            {   //modifier provided
		String[] mods = new String[ enumTokenIndex ];
		System.arraycopy( tokens, 0, mods, 0, enumTokenIndex );
		sig.modifiers = _modifiers.of( mods );
		if( sig.modifiers.containsAny( 
                    Modifier.ABSTRACT, 
                    Modifier.FINAL, 
                    Modifier.NATIVE,
                    Modifier.STRICT,
                    Modifier.STATIC,
                    Modifier.PROTECTED,
                    Modifier.PRIVATE,
                    Modifier.SYNCHRONIZED, 
                    Modifier.TRANSIENT, 
                    Modifier.VOLATILE,
                    _modifiers._modifier.INTERFACE_DEFAULT.getBitValue() ) )
		{
                    throw new ModelException(
			"Invalid Modifier(s) for enum of \"" + enumSignature 
                        + "\" only public allowed" );
		}
            }		
            if( implementsTokenIndex > enumTokenIndex + 1 )
            {
		if( implementsTokenIndex == tokens.length -1 )
		{
                    throw new ModelException( 
                        "implements token cannot be the last token" );
                }
		int tokensLeft = tokens.length - ( implementsTokenIndex + 1 );
                String[] implementsTokens = new String[ tokensLeft ];
		System.arraycopy( 
                    tokens, implementsTokenIndex + 1, implementsTokens, 0, tokensLeft );
                List<String>normalImplementsTokens = new ArrayList<String>();
                for( int i = 0; i < implementsTokens.length; i++)
		{
                    if( implementsTokens[ i ].contains( "," ) )
                    {
			String[] splitTokens = implementsTokens[ i ].split( "," );
			for( int j = 0; j < splitTokens.length; j++ )
			{
                            String tok = splitTokens[ j ].trim();
                            if( tok.length() > 0 )
                            { 
				normalImplementsTokens.add( tok );
                            }
                        }
                    }
                }
		sig.implementsFrom = 
                    varcode.java.metalang._implements.of( 
                        normalImplementsTokens.toArray( new String[ 0 ] ) ); //className.of( implementsTokens );
            }
            return sig;		
	}

	public String getImplements( int index ) 
	{			
            return implementsFrom.getAt( index );
	}	
    }

    @Override
    public Dom getDom() 
    {
	return ENUM;
    }

    @Override
    public _enum replace( String target, String replacement  ) 
    {
        this.constructors = this.constructors.replace( target, replacement );
        this.enumPackage = this.enumPackage.replace( target, replacement);
        this.signature = this.signature.replace( target, replacement );
        return this;
    }
}
