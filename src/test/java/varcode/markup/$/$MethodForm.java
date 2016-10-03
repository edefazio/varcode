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
package varcode.markup.$;

import varcode.markup.$ml.$Parse;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.Model;


/**
 *
 * @author eric
 */
public class $MethodForm
    extends $Parse
{
    protected String methodName;
    
    public $MethodForm( String methodName )
    {
        this.methodName = methodName;
        
    }
    
    public $MethodForm( )
    {
        this.methodName = null;
    } 

    @Override
    public Model bindIn(VarContext context)
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Model replace(String target, String replacement)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String author(Directive... directives)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
