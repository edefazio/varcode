package varcode.context;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptContext;

/**
 * Predefined "Scopes" for maintaining the Hierarchical Context State 
 * (the state of variables from GLOBAL to LOOP state) 
 * 
 * You can "Update" at your scope level, 
 * and "Read/Resolve" all above scope levels above you. 
 * 
 * Implications:
 * <UL>
 *   <LI>Changes made in one hierarchy DO NOT PROPEGATE UP to pollute the Scope Hierarchy
 *   <LI>Overrides can occur at the lowest part within the hierarchy 
 *   (TODO perhaps we introduce "FINAL" which means an error is thrown if 
 *   trying to bind / override a named script, var, form at a lower scope)
 * </UL>    
 * 
 * For instance when Tailoring an instance of a {@code VarCode}, the Context
 * (passed to a {@code MarkAction}) will be at {@code ContextScope} 
 * VARCODE_INSTANCE.  IF the {@code MarkAction} decides to update the 
 * Context (@see ScriptContext.setAttribute()) then the change will be "bound"
 * to the VARCODE_INSTANCE scope (it will be "readable" by other 
 * {@code MarkAction}s that are encountered AFTER    
 * 
 * Typically When resolving a {@code Form}, {@code VarScript} or {@code TailorDirective}
 * we start with the LOWEST/ or most "narrow" scope and work up the hierarchy:
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum VarScope
{   
    GLOBAL( 
        "GLOBAL", 
        "visible to all engines created by same ScriptEngineFactory", 
        ScriptContext.GLOBAL_SCOPE ), //200 
    
    CORE( 
        "CORE",
        "visible during the lifetime of a single ScriptEngine and a set "
       +"of attributes is maintained for each engine. (synonymous with"
       + "core \"lang\" features (i.e. java.lang.*)",
        ScriptContext.ENGINE_SCOPE ), //100    
    
    CORE_LIBRARY( 
        "CORE LIBRARY",
        "common constants, forms, and VarScripts used in many Markup "
       +"instances (synonymous with core libraries i.e. \"java.util.*\")",       
        ScriptContext.ENGINE_SCOPE / 2 ), //50

    METADATA(
        "METADATA",
        "common constants, forms, and scripts used within a Build Workspace "
       +"(Metadata)",
        ScriptContext.ENGINE_SCOPE / 3 ), //33
    
    LIBRARY(
       "LIBRARY",
        "common constants, forms, and scripts used in many VarCodes "
      + "instances, (when tailoring multiple projects within a workspace)",
        ScriptContext.ENGINE_SCOPE / 4 ), //25
    
    PROJECT( 
        "PROJECT",
        "common constants, forms, and scripts used in many VarCode "
       +"instances, (when tailoring multiple varcode sources for a project)"
       +"(synonymous with all import dependencies for a project)",       
        ScriptContext.ENGINE_SCOPE / 5 ), //20
    
    STATIC( 
        "STATIC",
        "constants, forms, and scripts for tailoring all VarCode instances "
       +"of a given varcode source (synonymous to a static field, or static "
       +"method on a Class)",
        ScriptContext.ENGINE_SCOPE / 10 ), //10
    
    INSTANCE( 
        "INSTANCE",
        "variables, constants, forms, and scripts for tailoring on a single "
      + "markup instance (synonymous to an instance field, or lambda on an "
      + "instance of an object)",
        ScriptContext.ENGINE_SCOPE / 20 ), //5
    
    METHOD( 
        "METHOD",
        "variables, constants, forms, and scripts used for tailoring within a"
      + "method call (synonymous to an parameter passed to a method) ",
        ScriptContext.ENGINE_SCOPE / 25 ), //4
    
    LOOP(
        "LOOP",
        "variables, constants, forms, and functions used for tailoring within a"
      + "method call (synonymous to an parameter passed to a method) ",
        ScriptContext.ENGINE_SCOPE / 50 ); //2
    
    private final String name;
    
    /**
     * A Description of the scope of context bindings 
     * (of fields, forms, and vars) their exposure and visibility 
     */
    private final String description;
    
    /**
     * scope precedence
     * <UL>
     *   <LI>lower values means more specific/smaller scope, 
     *       (LOOP = the lowest predefined scope for a context 
     *       that exists has its contents visible only during processing a 
     *       single loop iteration ) 
     *   <LI>higher values means more generic/larger scope
     *       (GLOBAL means all) 
     * </UL>  
     */
    private final int scope;
    
    private VarScope( String name, String description, int value )
    {
        this.name= name;
        this.description = description;
        this.scope = value;
    }

    public static List<Integer> getAllScopeValues()
    {
        List<Integer>allScopes = new ArrayList<Integer>();
        VarScope[] vals = VarScope.values();
        for( int i = 0; i < vals.length; i++ )
        {
        	allScopes.add( vals[ i ].scope );
        }        
        return allScopes;
    }
    
    public static boolean isValidScope( int scope )
    {
        for( int i = 0; i < VarScope.values().length; i++ )
        {
            if( VarScope.values()[ i ].scope == scope )
            {   
                return true;
            }            
        }
        return false;
    }
    
    public static VarScope fromScope( int scope )
    {
        for( int i = 0; i < VarScope.values().length; i++ )
        {
            if( VarScope.values()[ i ].scope == scope )
            {   
                return VarScope.values()[ i ];
            }            
        }
        return null;
    }
    
    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public int getValue()
    {
        return scope;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
