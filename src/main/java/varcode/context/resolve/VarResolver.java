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
package varcode.context.resolve;

import varcode.context.Context;

/**
 * Resolves Variables
 * 
 * Knows how to resolve the data values by name
 */
public interface VarResolver
    extends Resolve
{
    /**
     * @param context the context to resolve the Var
     * @param varName the name of the var to resolve
     * @return the var or null
     */
    public Object resolveVar( Context context, String varName );

    /**
     * Resolves the Var "through"
     */
    public enum SmartVarResolver
        implements VarResolver
    {
        INSTANCE;

        /**
         * Tries to resolve the object, returning null if the Var is not bound
         * (as EITHER:
         * <UL>
         * <LI>a var in the ( Javascript ) Expression engine
         * <LI>a value within the {@code ScopeBindings}
         * </UL>
         */
        @Override
        public Object resolveVar( Context context, String varName )
        {
            if( varName == null || varName.trim().length() == 0 )
            {
                return null;
            }

            Object var = context.get( varName );
            if( var != null )
            {
                return var;
            }

            String systemProp = System.getProperty( varName );

            //TODO ? Check ThreadLocal
            return systemProp;
        }
    }

}
