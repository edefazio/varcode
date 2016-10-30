/*
 * Copyright 2016 eric.
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
package varcode.java.model.edit;

import java.util.List;
import varcode.VarException;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.java.model._nest;
import varcode.java.model._nest.component;
import varcode.java.model._parameters;

public class ExpectArgument
        implements MethodEditor
{
    String argName;
    String type;

    public ExpectArgument(String type, String argName)
    {
        this.type = type;
        this.argName = argName;
    }

    public _nest.component edit( component component, _method method )
    {
        verifyArgument( method.getSignature().getParameters(), type, argName );
        return component;
    }

    public static boolean verifyArgument(
            _parameters params, String type, String name )
    {
        List<_parameters._parameter> paramList = params.getParameters();
        for( int i = 0; i < paramList.size(); i++ )  
        {
            if( paramList.get(i).getName().equals(name) ) 
            {
                return paramList.get(i).getType().equals(type);
            }
        }
        throw new VarException(
            "Expected argument " + type + " " + name + " not found in " + params );
    }
}
