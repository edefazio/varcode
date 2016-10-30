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

import varcode.java.model._imports;
import varcode.java.model._methods;
import varcode.java.model._nest.component;


public class AddMethods
    implements ComponentEditor
{
    private _methods methods;
    private _imports imports;
    
    public AddMethods( _methods methods )
    {
        this.methods = methods;
    }
    public AddMethods( _methods methods, _imports imports )
    {
        this.methods = methods;
        this.imports = imports;
    }
    
    @Override
    public component edit( component component )
    {
        component.getImports().addImports( imports );
        component.getMethods().addMethods( methods );
        return component;
    }            
}
