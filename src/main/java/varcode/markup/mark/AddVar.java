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
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasVars;
import varcode.markup.mark.Mark.IsNamed;
import varcode.markup.mark.Mark.MayBeRequired;

/**
 * Adds Code (bound to a given var name). 
 * Optionally provide a default in case the var name resolves to null.
 */
//
//example      : "{+name+}"
//    Prefix   : "{+"
//    Postfix  : "+}"
//    VarName  : "name"
//    Default  : ""
//    Required : false

// example      : "/*{+name+}*/"
//	   Prefix   : "/*{+"
//     Postfix  : "+}*/"
//     VarName  : "name"
//     Default  : ""
//     Required : false

// example      : "/*{+name|default+}*/"
//     Prefix   : "/*{+"
//     Postfix  : "+}*/"
//     VarName  : "name"
//     Default  : "default"
//     Required : false

// example      : "/*{+name*+}*/"
//     Prefix   : "/*{+"
//     Postfix  : "+}*/"
//     VarName  : "name"
//     Default  : "default"
//     Required : true 

public class AddVar
    extends Mark
	implements IsNamed, BlankFiller, HasVars, MayBeRequired 
{	
	private final String varName;
	
	private final String defaultValue;
	
	private final boolean isRequired;

	public AddVar( 
	    String text, 
	    int lineNumber,
	    String varName, 
	    boolean isRequired,
	    String defaultValue )
	{
	    super( text, lineNumber );
	    this.varName = varName;
	    this.defaultValue = defaultValue;
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
		    resolved = defaultValue;
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

    public void collectVarNames( Set<String>varNames, VarContext context )
    {
       varNames.add( varName );
    }
}	