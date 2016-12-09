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

import varcode.java.lang._code;
import varcode.java.lang._fields;
import varcode.java.lang._fields._field;
import varcode.java.lang._imports;
import varcode.java.lang._methods;
import varcode.java.lang._methods._method;

/**
 * code that explicitly expresses its required compile-time dependencies:
 * <UL>
 *  <LI>...on <B>import class(es)</B>
 *  <LI>...on the existence/declaration of <B>field(s)</B>
 *  <LI>...on the presence of method(s)
 * </UL>
 * 
 * @author M. Eric DeFazio edefazio@varcode.io
 */
public class _portableCode 
{
    /* this is the _code to be transposed */
    private final _code _code;
    
    // the main method _m (above) can express some "external dependencies"
    // on some other things (imports, methods, fields)
    private final _methods _requiredMethods = new _methods();
    private final _fields _requiredFields = new _fields();
    private final _imports _requiredImports = new _imports();
    
    public _portableCode( _code code )
    {
        this._code = code;
    }
    
    public _portableCode addRequiredMethods( _method... methods )
    {
        this._requiredMethods.addMethods( methods );
        return this;
    }
    
    public _portableCode addRequiredImports( Object...imports )
    {
        this._requiredImports.addImports( imports );
        return this;
    }
    
    public _portableCode addRequiredFields( _field... fields )
    {
        this._requiredFields.addFields( fields );
        return this;
    }
    
    public _code getCode()
    {
        return this._code;
    }
    
    public _methods getRequiredMethods()
    {
        return this._requiredMethods;
    }
    
    public _fields getRequiredFields()
    {
        return this._requiredFields;
    }
}
