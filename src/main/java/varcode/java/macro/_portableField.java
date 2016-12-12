/*
 * Copyright 2016 Eric.
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
package varcode.java.macro;

import varcode.java.lang._fields;
import varcode.java.lang._fields._field;
import varcode.java.lang._imports;
import varcode.java.lang._methods;
import varcode.java.lang._methods._method;
import varcode.java.macro.JavaMacro.Port;

/**
 *
 * @author M. Eric DeFazio erioc@varcode.io
 */
public class _portableField 
    implements Port
{
    private final _field _f;
    private final _fields _requiredFields = new _fields();
    private final _methods _requiredMethods = new _methods();
    private final _imports _requiredImports = new _imports();
    
    public _portableField( _field _f )
    {
        this._f = _f;
    }
    
    public _portableField addRequiredFields( String... fields )
    {
        this._requiredFields.addFields( _fields.of( fields ) );
        return this;
    }
    
    public _portableField addRequiredFields( _field... fields )
    {
        this._requiredFields.addFields( fields );
        return this;
    }
            
    public _portableField addRequiredMethods( _method... methods )
    {
        this._requiredMethods.addMethods( methods );
        return this;
    }    
    
    public _portableField addRequiredImports( Object... imports )
    {
        this._requiredImports.addImports( imports );
        return this;
    }     
}
