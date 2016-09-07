package varcode.java;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.context.Var;
import varcode.context.VarBindings;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.Directive;
import varcode.doc.lib.java.JavaLib;
import varcode.markup.Markup;
import varcode.script.VarScript;

/** Maybe this class is (ITSELF) a Directive? */
public enum JavaMarkup 
{
	INSTANCE;
	
    private static final Logger LOG = 
        LoggerFactory.getLogger( JavaCase.class );
    
	/**
     * Simplifies Marking up a Class that intends on being tailored.
     * 
     * Specifically:
     * <UL>
     *  <LI>sets the LANG to Java and adds all the java-related scripts and for use by Marks
     *  <LI>adds REQUIRED {@code ReplaceWithVar} Marks on the class name
     *  <LI>adds REQUIRED {@code ReplaceWithVar} Marks on the package name
     *  <LI>condenses multiple blank lines to a single blank line (in Post-Process)
     *  <LI>removes import statements for any used varcode classes or junit classes
     * </UL>
	 */
	public static Directive[] MARKUP_DIRECTIVES = new Directive[] {
		JavaLib.INSTANCE,   //load java library (for validation, etc.)	
		//Markup.className(), // add Mark around the ClassName
		//Markup.packageName(), // add Mark around the packageName
		Markup.condenseMultipleBlankLines(), //
		Markup.removeAllLinesWith( 
			"import varcode", 
			"import junit" )	
	};
	
    /** The name of the */
    public static final String MARKUP_CLASS_VAR_NAME = "markup.class";
    
	/**
     * When Tailoring from a Class, populate the VarContext
     * with static Fields of the class that are public & static &
     * <UL>
     *   <LI>{@code VarScript}s
     *   <LI>{@code Var}s (or {@code Var}[]) 
     *   <LI>{@code VarBindings}
     *   <LI>{@code Directive}s (or {@code Directive}[])
     *</UL>    
     * 	
     * @param markupClass
     * @param context the context to bind static bindings to
     * @return a List of Directives that are from static fields
     */
    public static List<Directive> bindStaticFields( 
    	Class<?> markupClass, VarContext context )
    {
    	VarBindings staticBindings = context.getOrCreateBindings( VarScope.STATIC );
    	staticBindings.put( MARKUP_CLASS_VAR_NAME, markupClass );
    	
    	List<Directive> directives = new ArrayList<Directive>();
        
    	Field[] fields = markupClass.getFields();
        for( int i = 0; i < fields.length; i++ )
        {
        	LOG.trace( "LOOKING THROUGH [" + fields.length + "] fields for tailor components" );
        	if( Modifier.isStatic( fields[ i ].getModifiers() ) 
        		&& Modifier.isPublic( fields[ i ].getModifiers() ) )
        	{
        		Class<?> fieldType = fields[ i ].getType();
        		if( fieldType.isAssignableFrom( VarBindings.class ) )
        		{   //add VarBindings at Static scope        			
        			staticBindings.merge(
        					(VarBindings)Java.getStaticFieldValue( fields[ i ] ) );
        			LOG.trace( "Adding static filed bindings " +
    					Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isAssignableFrom( Var.class ) )
        		{   //Add Vars at "Static" scope
        			staticBindings.put( 
        				(Var)Java.getStaticFieldValue( fields[ i ] ) );
        			LOG.trace( "Adding static Var " +
        				Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isAssignableFrom( VarScript.class ) )
        		{   //Add Vars at "Static" scope
        			staticBindings.put( fields[ i ].getName(),  
        				(VarScript)Java.getStaticFieldValue( fields[ i ] ) );
        			LOG.trace( "Adding static VarScript \"" +fields[ i ].getName()+"\" "
        				+(VarScript)Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isAssignableFrom( Directive.class ) )
        		{
        			directives.add( 
        				(Directive)Java.getStaticFieldValue( fields[ i ] ) );
        			LOG.trace( "Adding Directive \"" +fields[ i ].getName()+"\" "
            			+ (Directive)Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isArray() ) 
        		{	//arrays of Directives and Vars
        			if( fieldType.getComponentType().isAssignableFrom( Directive.class ) )
                	{
        				Directive[] dirs = 
        					(Directive[])Java.getStaticFieldValue( fields[ i ] );
        				directives.addAll( Arrays.asList(dirs) );
        				if( LOG.isTraceEnabled() )
        				{
        					for( int j = 0; j < dirs.length; j++ )
        					{
        						LOG.trace( "Added static Directive [" + j +"]"+ dirs[ j ]);
        					}
        				}
        			}
        			if( fieldType.getComponentType().isAssignableFrom( Var.class ) )
                	{
        				Var[] vars = (Var[])Java.getStaticFieldValue( fields[ i ] );
        				for( int j = 0; j < vars.length; j++ )
        				{
        					staticBindings.put( vars[ i ] );
        				}
        			} 
        		}    		
        	}
        	
        }
        return directives;
    }
}
