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

import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.markup.mark.Mark.Bind;
import varcode.markup.mark.Mark.HasVar;
import varcode.translate.TranslateBuffer;

/**
 * "$var$ 
 * 
 * AddVarDurable avd = 
 *     AddVarDurable( "var", "$var$", 100 );
 * 
 * String s = (String)avd.derive( VarContext.of() ); //s = "$var$
 * s = (String)avd.derive( VarContext.of( "var", "100") ); //s = 100;
 * 
 * An AddVar mark that is "durable" meaning instead of printing blank 
 * (empty string) if it is null, prints the Mark
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AddVarDurable
    extends Mark
    implements Bind, HasVar
{
    public String var;

    public AddVarDurable( String var, String text, int lineNumber )
    {
        super( text, lineNumber );
        this.var = var;
    }

    @Override
    public void bind( Context context, TranslateBuffer buffer )
    {
        Object val = context.get( var );
        if( val == null )
        {
            buffer.append( derive( context ) );
        }
    }

    @Override
    public Object derive( Context context )
        throws VarBindException
    {
        Object val = context.get( var );
        if( val == null )
        {
            return text;
        }
        return val;
    }

    @Override
    public String getVarName()
    {
        return var;
    }    
}
