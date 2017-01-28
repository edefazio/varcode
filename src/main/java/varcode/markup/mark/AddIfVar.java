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
package varcode.markup.mark;

import java.lang.reflect.Array;
import java.util.Collection;
import varcode.translate.TranslateBuffer;
import varcode.context.Context;
import varcode.markup.mark.Mark.HasVar;
import varcode.markup.mark.Mark.Bind;

/**
 * If a var is bound in the {@code Context} (and non-null) write some static text 
 * to the document.
 * 
 * A Form of Code (one or more java statements) that is <I>conditionally</I> 
 * written to the tailored source.
 * 
 * There are (2) variants: 
 * <UL>
 *  <LI><B>On name</B> will write the block to the tailored source if the name
 *  resolves to a <B>non-null</B> value (AND FOR COLLECTIONS/ARRAYS, the length
 * /size must be > 0).<PRE> 
 *  / *{?log:
 *  import org.slf4j.LoggerFactory;
 *  import org.slf4j.Logger;
 *  }* /</PRE>
 *  
 *  <LI><B>On name equals</B> will write the block to the tailored source is the
 *  name resolves to a value that is is <B>equal to a target value</B>.<PRE>
 *  / *{?log==trace:
 *  LOG.trace( "Inside Loop : \"" + methodName + "\"" ); 
 *  }* /</PRE>
 * </UL> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
       /*{?log=trace:
       LOG.trace( "In Method " );
       }*/
public class AddIfVar
    extends Mark
    implements Bind, HasVar
{
    /** the var name in the context to test*/
    private final String varName;
    
    /** expected target value ...of the <B>value</B> associated with the varName */
    private final String targetValue;
    
    /** code/text conditionally written to the tailored source*/ 
    private final String conditionalText;
    
    public AddIfVar( 
        String text, 
        int lineNumber, 
        String name,
        String targetValue,
        String conditionalText )
    {
        super( text, lineNumber );
        this.varName = name;
        this.targetValue = targetValue;
        this.conditionalText = conditionalText;            
    }   

    @Override
    public String getVarName()
    {
        return varName;
    }

    public String getTargetValue()
    {
    	return targetValue;
    }
    
    public String getConditionalText()    
    {
        return conditionalText;
    }
    
    @Override
    public void bind( Context context, TranslateBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
    @Override
    public Object derive( Context context )
    {
        Object resolved = context.resolveVar( varName );
        if( resolved == null )
        {        	
            return null;
        }
        if( resolved.getClass().isArray() )
        {
            if( Array.getLength( resolved ) == 0 )
            {
                return null;
            }
        }
        else if( Collection.class.isAssignableFrom( resolved.getClass() ) )
        {
            Collection c = (Collection)resolved;
            if( c.isEmpty() )
            {
                return null;
            }
        }
        if( targetValue == null )
        {
            return conditionalText;                    	
        }        
        if ( resolved.toString().equals( targetValue ) )
        {
            return conditionalText;            
        }
        return null;
    }  
}
