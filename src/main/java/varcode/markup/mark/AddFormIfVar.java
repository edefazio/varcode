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

import java.util.Set;

import varcode.VarException;
import varcode.buffer.TranslateBuffer;
import varcode.context.VarContext;
import varcode.form.Form;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.HasVars;

/**
 * A Form of Code (one or more java statements) that is <I>conditionally</I> 
 * written to the tailored source.
 * 
 * There are (2) variants: 
 * <UL>
 *  <LI><B>On name</B> will write the block to the tailored source if the name
 *  resolves to a <B>non-null</B> value.<PRE> 
 *  / *{{+?log:
 *  import {+logFactory};
 *  import {+logger};
 *  }}* /</PRE>
 *  
 *  <LI><B>On name equals</B> will write the block to the tailored source is the
 *  name resolves to a value that is is <B>equal to a target value</B>.
 *  
 *  <PRE>/ *{{+?(log==trace): LOG.trace( "Inside {+methodName}() Loop"); }}* /</PRE>
 * </UL> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */

//BindML :
// {{+?log==true: LOG.debug("Resolved "+ {+vari+}.toString() +" for service call");+}}

//CodeML : 
/*{{+?log:
import {+logFactory};
import {+logger}; 
}}*/

public class AddFormIfVar
    extends Mark
    implements BlankFiller, HasForm, HasVars
{
    /** the name of the var*/
    private final String varName;
    
    /** the target value condition (for the code to be tailored) 
     * i.e. if a == targetValue (NOTE: OPTIONAL)*/
    private final String targetValue;
    
    /** form of code conditionally written when tailoring source */ 
    private final Form form;
   
    public AddFormIfVar( 
        String text, int lineNumber, String name, String targetValue, Form form )
    {
        super( name, lineNumber );
        this.varName = name;
        this.targetValue = targetValue;
        this.form = form;
    }
        
    public String getVarName()
    {
        return varName;
    }

    public String getTargetValue()
    {
    	return targetValue;
    }
    
    public void fill( VarContext context, TranslateBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
    public Object derive( VarContext context )
    {
        try
        {
            Object resolved = 
            	context.getVarResolver().resolveVar( context, varName );
            
            if( resolved == null )
            {        	
                return null;
            }
            if( targetValue == null )
            {               
                return form.derive( context );                   	
            }
            if ( resolved.equals( targetValue ) )
            {              
                return form.derive( context );            
            }
            return null;
        }
        catch( Exception e )
        {
            throw new VarException (
                "Unable to derive AddFormIfVar \"" + varName + "\" for mark " + N 
                + text + N + " on line [" + lineNumber + "]", e );
        }
    }
    
    public Form getForm()
    {
        return form;
    }    

    public void collectVarNames( Set<String>varNames, VarContext context )
    {
        form.collectVarNames( varNames, context );
    }
        
}
