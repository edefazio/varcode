/*
 * Copyright 2015 M. Eric DeFazio.
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
package varcode.markup.mark;

import java.util.HashSet;
import java.util.Set;

import varcode.buffer.TranslateBuffer;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.markup.MarkupException;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasVars;
import varcode.markup.mark.Mark.IsNamed;
import varcode.markup.mark.Mark.MayBeRequired;

/**
 * Adds Code (bound to a given var name). 
 * Optionally provide a default in case the var name resolves to null.
 */
//
//example      : "{+count[1, 3, 5, 7, 9]+}"
//Prefix       : "{+"
//Postfix      : "+}"
//VarName      : "name"
//Required     : false


//example      : "{+count[1, 3, 5, 7, 9]*+}"
//    Prefix   : "{+"
//    Postfix  : "+}"
//    VarName  : "name"
//    Required : true


//example      : "{+count[1, 3, 5, 7, 9]|1+}"
//Prefix   : "{+"
//Postfix  : "+}"
//VarName  : "name"
//Required : false
//Default  : 1


//example      : "{+vowel['a', 'e', 'i', 'o', 'u']+}*/"


public class AddVarOneOf
    extends Mark
	implements IsNamed, BlankFiller, HasVars, MayBeRequired 
{	
	private final String varName;
	
	//I should check this is a valid array
	private final Object[] array;
	
	private final String arrayDescription;
	
	
	private final String defaultValue;
	
	private final boolean isRequired;

	
	public AddVarOneOf( 
	    String text, 
	    int lineNumber,
	    String varName, 
	    Object[] array,
	    String arrayDescription,
	    boolean isRequired,
	    String defaultValue )
	{
	    super( text, lineNumber );
	    if( isRequired && defaultValue != null )
	    {
	    	throw new MarkupException("AddVarOneOf cannot be BOTH required and have a default \""+ defaultValue+"\"" );
	    }
	    this.varName = varName;
	    this.defaultValue = defaultValue;
	    this.array = array;
	    this.arrayDescription = arrayDescription;
	    this.isRequired = isRequired;
	}

	public String getVarName()
	{
		return varName; 
	}

	public Object derive( VarContext context ) 
	{
		Object resolved = 
			context.getVarResolver().resolveVar( context, varName );
		
		if ( resolved == null )
		{
		    if( isRequired )
            {
		    	throw new NullVar( varName, text, lineNumber );                
            }
		    //resolved = defaultValue;
		    return defaultValue;
		}
		boolean found = false;
		for( int i = 0; i < array.length; i++ )
		{
			if( resolved.equals( array[ i ] ) )
			{
				found = true;
				break;
			}
		}
		if( !found )
		{
			for( int i = 0; i < array.length; i++ )
			{
				
			}
			throw new VarBindException( 
				"Count not bind value \"" + resolved + " to \"" + varName + "\" for mark " 
			    + N + text + N + "on line ["+lineNumber+"] must be one of " + N + arrayDescription  );
		}
		
		return resolved;	
	}

	public void fill( VarContext context, TranslateBuffer buffer )
	{
	    buffer.append( derive( context ) );
	}
	
	public boolean isRequired()
	{
	    return isRequired;
	}
	
    public String getDefault()
    {
        return this.defaultValue;
    }
    
    public Object[] getArray()
    {
    	return this.array;
    }
    
    public String getArrayDescription()
    {
    	return this.arrayDescription;
    }
    
    public void collectVarNames( Set<String>varNames, VarContext context )
    {
       varNames.add( varName );
    }
}	