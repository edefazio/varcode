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
package varcode.author;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import varcode.VarException;
import varcode.context.Context;
import varcode.context.VarBindException;

/**
 * Adapts a Static Method call to the {@code Directive.PreProcessor} interface
 * so that we might call static methods as if they implemented {@code VarScript}
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class StaticMethodPreProcessAdapter
    implements PreProcessor
{
    private final Method method;

    private final Object[] params;

    public StaticMethodPreProcessAdapter( Method method, Object... params )
    {
        this.method = method;
        if( params.length == 0 )
        {
            this.params = null;
        }
        else
        {
            this.params = params;
        }
    }

    public Object eval( Context context )
        throws VarBindException
    {
        try
        {
            return method.invoke( null, params );
        }
        catch( IllegalAccessException e )
        {
            throw new VarBindException( e );
        }
        catch( IllegalArgumentException e )
        {
            throw new VarBindException( e );
        }
        catch( InvocationTargetException e )
        {
            if( e.getCause() instanceof VarException )
            {
                throw (VarException)e.getCause();
            }
            throw new VarBindException( e.getCause() );
        }
    }

    @Override
    public String toString()
    {
        return "Pre-Processor to {" + method.toString()+ "}";
    }

    @Override
    public void preProcess( AuthorState authorState )
    {
        eval( authorState.getContext() );
    }
}
