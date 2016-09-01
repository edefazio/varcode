package varcode.doc.lib.java;

import varcode.Lang;
import varcode.context.VarBindings;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.lib.Library;

/**
 * Scripts designed specifically for Tailoring Java code
 * (i.e. Validation Scripts for ClassNames, PackageNames, etc.)
 * Source Forms for Classes, Enums, packages, imports, etc.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaLib
	implements Library, Directive.PreProcessor
	
{
	INSTANCE;

	public void load( VarContext context ) 
	{
		loadAtScope( context, VarScope.LIBRARY );
	}
	
	public void loadAtScope( VarContext varContext, VarScope varScope ) 
	{
		VarBindings bindings = varContext.getOrCreateBindings( varScope );
		
		bindings.put( getName() + "." + getVersion(), this );
		
		
		bindings.put( "validateClassName", ValidateClassName.INSTANCE );
		bindings.put( "!className", ValidateClassName.INSTANCE );
		
		bindings.put( "validateType", ValidateTypeName.INSTANCE );
		bindings.put( "validateTypeName", ValidateTypeName.INSTANCE );
		bindings.put( "!typeName", ValidateTypeName.INSTANCE );
		bindings.put( "!type", ValidateTypeName.INSTANCE );
		
		bindings.put( "validateIdentifierName", ValidateIdentifierName.INSTANCE );
		bindings.put( "validateIdentifier", ValidateIdentifierName.INSTANCE );		
		bindings.put( "!identifierName", ValidateIdentifierName.INSTANCE );
		
		bindings.put( "validatePackageName", ValidatePackageName.INSTANCE );		
		bindings.put( "!packageName", ValidatePackageName.INSTANCE );				
	}

	
	public String getName() 
	{
		return "JavaLib";
	}

	public String toString()
	{
		return "Load Java Library Directive";
	}
	
	public String getVersion() 
	{
		return "0.1";
	}

	public void preProcess( DocState tailorState ) 
	{
		tailorState.getContext().set( "lang", Lang.JAVA, VarScope.STATIC );
		load( tailorState.getContext() );
	}

}
