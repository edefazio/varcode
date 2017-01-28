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

import varcode.VarException;
import varcode.context.Context;
import varcode.context.VarBindException;

import varcode.translate.TranslateBuffer;
import varcode.context.VarBindException.NullResult;
import varcode.markup.form.Form;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.MayBeRequired;
import varcode.markup.mark.Mark.Bind;

/**
 * A Form of Code (one or more java statements) that is <I>conditionally</I>
 * written to the tailored source.
 *
 * There are (2) variants:
 * <UL>
 * <LI><B>On name</B> will write the block to the tailored source if the name
 * resolves to a <B>non-null</B> value.<PRE>
 *  / *{{+{+dayFormat}-{+monthFormat}-{yearFormat} }}* /</PRE>
 *
 * <LI><B>On name equals</B> will write the block to the tailored source is the
 * name resolves to a value that is is <B>equal to a target value</B>.<PRE>
 *  / *{+?log=trace:
 *  LOG.trace( "Inside Loop : \"" + methodName + "\"" );
 *  }* /</PRE>
 * </UL>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
//CodeML Example
// " / *{{+:{+dayFormat}-{+monthFormat}-{yearFormat} }}* /"
public class AddForm
    extends Mark
    implements Bind, HasForm, MayBeRequired
{
    /**
     * form of code conditionally written when tailoring source
     */
    private final Form form;

    private final boolean isRequired;

    public AddForm( String text, int lineNumber, boolean isRequired, Form form )
    {
        super( text, lineNumber );
        this.isRequired = isRequired;
        this.form = form;
    }

    public AddForm(
        String text, int lineNumber, Form form )
    {
        this( text, lineNumber, false, form );
    }

    @Override
    public void bind( Context context, TranslateBuffer buffer )
    {
        buffer.append( derive( context ) );
    }

    /**
     * Derives the Form for the Mark given the context
     */
    @Override
    public String derive( Context context )
    {
        String result = null;
        try
        {
            result = form.author( context );
        }
        catch( VarException ve )
        {
            throw ve;
        }
        catch( Exception e )
        {
            throw new VarBindException(
                "Unable to derive Form for AddForm Mark: " + N + text + N
                + "\" on line [" + lineNumber + "]", e );
        }

        if( result == null || result.trim().length() == 0 )
        {
            if( isRequired )
            {
                throw new NullResult(
                    this.text, this.form.getText(), this.lineNumber );
            }
        }
        return result;
    }

    @Override
    public Form getForm()
    {
        return form;
    }

    @Override
    public boolean isRequired()
    {
        return isRequired;
    }
}
