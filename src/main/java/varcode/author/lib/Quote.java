/*
 * Copyright 2017 Eric.
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
package varcode.author.lib;

import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.context.VarScript;

/**
 * adds double quotes around a String
 * 
 * @author Eric
 */
public class Quote
    extends ApplyToOneOrMore
    implements VarScript
{
    public static final Quote INSTANCE = new Quote();
    
    /** use INSTANCE */
    private Quote()
    {        
    }
    
    @Override
    public Object applyToOne( Object object )
    {
        String s = object.toString();
        return "\""+s+"\"";
    }    

    @Override
    public Object eval( Context context, String input )
        throws VarBindException
    {
        return this.apply( context.resolveVar( input ) );
    }
}
