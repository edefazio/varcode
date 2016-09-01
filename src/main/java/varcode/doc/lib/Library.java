package varcode.doc.lib;

import varcode.context.VarContext;
import varcode.context.VarScope;

/**
 * Similar to a "WildCard Import" "import.java.util.*;" in Java.
 * 
 * Each module is composed of:
 * <UL>
 *  <LI>Vars (name value pairs)
 *  <LI>{@code VarScript}s 
 *  <LI>{@code TailorDirective}s  
 * </UL>
 * These can be loaded into the {@code VarContext} 
 * at a specified {@code VarScope}.
 * 
 * Similar to an "import with wildcard" in Java:<BR>
 * "import java.util.*;"
 * 
 * <BLOCKQUOTE>
 * NOTE: you can MANUALLY import individual {@code VarScript}s
 * and {@code TailorDirective}s.
 * <PRE>
 * VarContext context = new VarContext();
 * 
 * //manually include a script
 * context.setAttribute( 
 *     "allCaps", 
 *     io.varcode.context.module.AllCap.INSTANCE, 
 *     VarScope.LIBRARY );
 * 
 * //manually include a Tailor Directive    
 * context.setAttribute(
 *     "-emptyLines", 
 *     io.varcode.context.module.RemoveEmptyLines.INSTANCE,
 *     VarScope.LIBRARY );	    	    
 * </PRE>
 * </BLOCKQUOTE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Library 
{
	/** 
	 * Each module has a unique name (for bookkeeping purposes) 
	 * 
	 * It's nice to have the OPTION of knowing and querying (AT RUNTIME)
	 * which modules and versions are loaded in a {@code VarContext}. 
	 */
	public String getName();
	
	/** Each module MAY have versions */
	public String getVersion();
	
	/**
	 * Load the Library at the appropriate Scope
	 * into the VarContext
	 * @param context the context to load the script libraries
	 */
	public void load( VarContext context );
	
	/**
	 * 
	 * @param context the context to load the modules into
	 * @param scope the scope to load the modules at
	 */
	public void loadAtScope( VarContext context, VarScope scope );
}
