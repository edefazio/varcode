package varcode.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.script.Bindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.doc.Directive;
import varcode.doc.lib.Library;
import varcode.context.eval.VarScript;

/** 
 * Maintains a set of hierarchal / Named  Scopes associated with Bindings: 
 * 
 * GLOBAL -> {name=value,a=b}  
 * ENGINE -> {engine=javascript}
 * INSTANCE -> {fieldName=aName}
 * 
 * @see VarScope
 */
public class ScopeBindings
    implements Bindings
{
	private static final Logger LOG = 
        LoggerFactory.getLogger( ScopeBindings.class );
	
    private final TreeMap<Integer, VarBindings> scopeToBindings; 

    /** All of the Supported scopes for the Bindings */
    public static final List<Integer>ALL_SCOPES 
        = VarScope.getAllScopeValues();
    
    public ScopeBindings( )
    {
        this( new TreeMap<Integer, VarBindings>() );
    }
    
    public ScopeBindings( TreeMap<Integer, VarBindings> scopeToBindings )
    {
        this.scopeToBindings = scopeToBindings;
    }

    /**
     * Merges the non-conflicting scopeBindings to this ScopeBindings
     * @param mergeScopeBindings
     */
    public void merge( ScopeBindings mergeScopeBindings )
    {
    	Integer[] mergeScopes = 
    		mergeScopeBindings.scopeToBindings.keySet().toArray( new Integer[ 0 ] );
    	for( int i = 0; i < mergeScopes.length; i++ )
    	{
    		VarBindings toMergeAtScope =
    			mergeScopeBindings.getBindings( mergeScopes[ i ] );
    		
    		VarBindings target = this.getOrCreateBindings( mergeScopes[ i ] );
    		
    		String[] mergeKeys = toMergeAtScope.keySet().toArray( new String[ 0 ] );
    		for( int m = 0; m < mergeKeys.length; m++ )
    		{   //ONLY merge if there is no conflicts
    			if( target.get( mergeKeys[ m ] ) == null )
    			{
    				target.put( mergeKeys[ m ], toMergeAtScope.get( mergeKeys[ m ] ) ); 
    			}
    		}
    	}    	
    }
    
    public void setBindings( VarBindings bindings, int scope )
    {
        if(! VarScope.isValidScope( scope ) )
        {
            throw new VarException(
                "Invalid Scope \"" + scope + "\" for bindings" );
        }
        VarBindings last = scopeToBindings.put( scope, bindings ); 

        if( last != null && LOG.isInfoEnabled() )
        {   
            LOG.info( "Replaced existing Bindings at scope " + scope + " " + last ); 
        }             
    }

    public VarBindings getOrCreateBindings( VarScope scope )    
    {
        return getOrCreateBindings( scope.getValue() );
    }
    
    public VarBindings getOrCreateBindings( int scope )    
    {
        VarBindings vb = scopeToBindings.get( scope ) ;
        if( vb != null )
        {
            return vb;
        }
        
        vb = new VarBindings();
        setBindings( vb,  scope );
        
        return vb;
    }
    
    public VarBindings getBindings( VarScope varScope )
    {
        return scopeToBindings.get( varScope.getValue() );
    }
    
    public VarBindings getBindings( int scope )
    {
        return scopeToBindings.get( scope );
    }
    
    public void put( Var var, VarScope varScope )
    {
    	put( var.getName(), var.getValue(), varScope );
    }

    public void put( String name, Object value, VarScope scope )
    {
        put( name, value, scope.getValue() );
    }
    
    public void put( String name, Object value, int scope )
    {
        VarBindings bindings = getBindings( scope );
        if( bindings == null )
        {   //bindings for this scope doesn't exist yet, create one and add 
            // it to the internal scopeBindings
            if( getBindings( scope ) != null && LOG.isDebugEnabled() )
            {   
                LOG.debug( "Created Bindings at scope " + scope );   
            }
            
            bindings = new VarBindings();
            scopeToBindings.put( scope, bindings );
        }
        bindings.put( name, value );        
    }

    public Object remove( String name, int scope )
    {
        Bindings bindings = getBindings( scope );
        if( bindings == null )
        {   
            return null;
        }
        return bindings.remove( name );
    }   

    /**
     * Scans through all Scope Bindings and returns the 
     * lowest scope that contains a value bound to var {@code name} 
     * 
     * @param varName the name of the var
     * @return int scope (or  
     */
    public int getScopeOf( String varName )
    {
    	//only seach non-null scopeBindings
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            Bindings bindingsForThisScope = scopeToBindings.get( scope );
            Object value = bindingsForThisScope.get( varName );
            if( value != null )
            {
                return scope;
            }
        }
        return -1;        
    }

    @Override
    public int size()
    {
        Integer[] scopes = 
            scopeToBindings.keySet().toArray( new Integer[ 0 ] );
        
        int count = 0;
        for( int i = 0; i < scopes.length; i++ )
        {
            VarBindings vb = scopeToBindings.get( scopes[ i ] );
            count += vb.size();
        }
        return count;
    }
    

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    @Override
    public boolean containsValue( Object value )
    {
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            Bindings bindingsForThisScope = scopeToBindings.get( scope );
            if( bindingsForThisScope != null)
            {    
                if( bindingsForThisScope.containsValue( value ) )
                {
                    return true;
                }                
            }
        }
        return false;        
    }

    @Override
    public void clear()
    {
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
        	
            int scope = scopeIterator.next();
            scopeToBindings.get( scope ).clear();
            if( getBindings( scope ) != null && LOG.isInfoEnabled() )
            { LOG.info( "Cleared all Bindings at scope " + scope ); }
        }
    }

    @Override
    public Set<String> keySet()
    {
        Set<String> superSet = new HashSet<String>();
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            superSet.addAll(  scopeToBindings.get( scope ).keySet() );        
        }
        return superSet;
    }

    @Override
    public Collection<Object> values()
    {
        List<Object> superList = new ArrayList<Object>();
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            superList.addAll(  scopeToBindings.get( scope ).keySet() );        
        }
        return superList;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet()
    {
        Set<java.util.Map.Entry<String, Object>>entrySet = 
              new HashSet<java.util.Map.Entry<String, Object>>();
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            entrySet.addAll( scopeToBindings.get( scope ).entrySet() );        
        }
        return entrySet;
    }

    @Override
    public Object put( String name, Object value )
    {
        //by default put on INSTANCE scope
        return getOrCreateBindings( VarScope.INSTANCE ).put( name, value );
    }

    @Override
    public void putAll( Map<? extends String, ? extends Object> toMerge )
    {
        getOrCreateBindings( VarScope.INSTANCE ).putAll( toMerge );
    }

    @Override
    public boolean containsKey( Object key )
    {
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            if ( scopeToBindings.get( scope ).containsKey( key ) )
            {
                return true;
            }
        }
        return false;
    }

    public Object get( String name, VarScope scope )
    {
        return get( name, scope.getValue() );
    }
    
    public Object get( String name, int scope )
    {
        Bindings bindings = getBindings( scope );
        if( bindings == null )
        {   
            return null;
        }
        return bindings.get( name );
    }

    @Override
    public Object get( Object key )
    {
        return get( (String)key );
    }

    /**
     * Starting with the "Lowest" scope, search each scope for an attribute
     * "name", and return it (or null if not found in any scope)
     * @param name the name to look for
     * @return the value of the attribute (or null if not found in any scope)
     */
    public Object get( String name )
    {
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            Bindings bindingsForThisScope = scopeToBindings.get( scope );
            if( bindingsForThisScope != null)
            {    
                Object value = bindingsForThisScope.get( name );
                if( value != null )
                {
                    return value;
                }
            }
        }
        return null;        
    }
    
    @Override
    public Object remove( Object key )
    {
        Object lastRemoved = null;
        Iterator<Integer> scopeIterator 
            = scopeToBindings.keySet().iterator();
        while ( scopeIterator.hasNext() )
        {
            int scope = scopeIterator.next();
            Object removed = scopeToBindings.get( scope ).remove( key );
            if( removed != null )
            {
                lastRemoved = removed;
            }
        }
        return lastRemoved;
    }
    
    public Directive getDirective( String name )
    {
        Object o = get( name );
        if( o != null )
        {
            if( o instanceof Directive )
            {
            	if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace( "   found Directive: \"" + o.toString()  + "\"" ); 
                }
                
                return (Directive) o;
            }       
            throw new VarException(
            	"Expected Directive for \"" + name + "\"; but was \"" + o + "" );
        }
        if( LOG.isDebugEnabled() ) 
        { 
            LOG.warn( "couldn't find Directive for \"" + name  + "\"" ); 
        }
        return null;
    }
    
    public VarScript getScript( String name )
    {
        Object o = get( name );
        if( o != null )
        {
            if( o instanceof VarScript )
            {
                return (VarScript) o;
            }       
            throw new VarException(
            	"Expected VarScript for \"" + name + "\"; but was \"" + o + "" );
        }
        return null;
    }
    
    /**
     * 
     * @param vb
     * @param sb
     */
    private static void bindingsToString( VarBindings vb, StringBuilder sb )
    {
    	StringBuilder libraries = new StringBuilder();
    	StringBuilder varScript = new StringBuilder();
    	StringBuilder tailorDirective = new StringBuilder();
    	StringBuilder varValues = new StringBuilder();
    	
    	Iterator<String> it = vb.keySet().iterator();
    	
    	for( int i = 0; i < vb.size(); i++ )
		{
    		String name = it.next();
    		Object value = vb.get( name );
    		if( value instanceof Library )
    		{
    			libraries.append( "    " );
    			libraries.append( name );
    			libraries.append( " : " );
    			libraries.append( value.getClass().getName() );
    			libraries.append( System.lineSeparator() );
    		}
    		else if( value instanceof Directive )
    		{
    			tailorDirective.append( "    " );
    			tailorDirective.append( name );
    			tailorDirective.append( " : " );
    			tailorDirective.append(value.getClass().getName() );
    			tailorDirective.append( System.lineSeparator() );
    		}
    		else if( value instanceof VarScript )
    		{
    			varScript.append( "    " );
    			varScript.append( name );
    			varScript.append( " : " );
    			varScript.append(value.getClass().getName() );
    			varScript.append( System.lineSeparator() );
    		}
    		else
    		{
    			varValues.append( "    " );
    			varValues.append( name );
    			varValues.append( " : " );
    			varValues.append( value );
    			varValues.append( System.lineSeparator() );
    		}    		
		}    	
    	if( libraries.length() > 0 )
    	{
    		sb.append( System.lineSeparator() );
    		sb.append( "    LIBRARIES---------------------------" );
    		sb.append( System.lineSeparator() );
    		sb.append( libraries.toString() );    		
    	}
    	if( tailorDirective.length() > 0 )
    	{
    		sb.append( System.lineSeparator() );
    		sb.append( "    TAILOR DIRECTIVES-------------------" );
    		sb.append( System.lineSeparator() );
    		sb.append( tailorDirective.toString() );    		
    	}
    	if( varScript.length() > 0 )
    	{
    		sb.append( System.lineSeparator() );
    		sb.append( "    SCRIPTS-----------------------------");
    		sb.append( System.lineSeparator() );
    		sb.append( varScript.toString() );    		
    	}
    	if( varValues.length() > 0 )
    	{
    		sb.append( System.lineSeparator() );
    		sb.append( "    VARS--------------------------------" );
    		sb.append( System.lineSeparator() );
    		sb.append( varValues.toString() );    		
    	}    	
    }
    
    @Override
    public String toString()
    {
    	StringBuilder sb = new StringBuilder();
    	Iterator<Integer> scopesIt = this.scopeToBindings.keySet().iterator();
    	
    	List<Integer>scopes = new ArrayList<Integer>();
    	while( scopesIt.hasNext() )
    	{
    		scopes.add( scopesIt.next() );
    	}
    	
    	Collections.sort( 
    		scopes, 
    		new Comparator<Integer>()
    	{

            @Override
			public int compare( Integer o1, Integer o2 ) 
			{
				return -1 * o1.compareTo( o2 );
			}
    		
    	});
    	for( int i = 0; i < scopes.size(); i++ )
    	{
    		int nextScope = scopes.get( i );
    		String scopeLabel = "" + nextScope;
    		VarScope vs = VarScope.fromScope( nextScope );
    		if( vs != null )
    		{
    			scopeLabel = vs.toString();    		
    		}
    		sb.append( System.lineSeparator() );
    		sb.append( " (" );
    		sb.append( scopeLabel );
    		sb.append( ") Bindings" );
    		sb.append( System.lineSeparator() );
    		VarBindings vb = this.scopeToBindings.get( nextScope );
    		bindingsToString( vb, sb );
    	}
    	return sb.toString();
    }
}
