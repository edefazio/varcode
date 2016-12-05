/*
 * Copyright 2016 M. Eric DeFazio.
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
package varcode.java.metalang.macro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.metalang._fields;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._imports;
import varcode.java.metalang._methods;
import varcode.java.metalang._methods._method;

/**
 * Create a "floating" method, that
 * contains a _method that is "portable" (can be moved from one abstraction :
 * _class, _enum to another _class, _enum) freely.
 * 
 * 
 * Aggregates a method and all of it's dependencies 
 * (_imports, _fields, other _methods) to allow the method to be transposed 
 * to another _javaComponent (_class, _enum)
 * 
  * NORMALLY using OO this type of thing is done via code sharing via: 
 * <UL>
 *   <LI>an inheritance relationship (where a class "Subclass" inherents a 
 * method from its super class hierarchy
 *   <LI>Delegation to another class.
 * </UL>
 * 
 * <U>Example</U>:<BR/>
 * 
 * <PRE>
 *  //here is a simple method definition
 * _method _m = _method.of( 
 *     "public static String getId()",
 *     "return prefix + UUID.randomUUID().toString();");
 * 
 * _methodTranspose _getId = _methodTranspose.of
 *    ( _m, 
 *      _fields.of( "Object prefix" ),  // requires a _field "prefix" to work
 *      _imports.of( UUID.class ) );    // requires the UUID.class to be import 
 * </PRE>
 * assuming we have 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _portableMethod 
{
    public static _portableMethod of( _method method )
    {
        return new _portableMethod( method );
    }
    
    
    //this is the method signature and body to be transposed (to some other
    // _class, _enum, _staticBlock)
    private final _method _m;
    
    // the main method _m (above) can express some "external dependencies"
    // on some other things (imports, methods, fields)
    private final _methods _requiredMethods = new _methods();
    private final _fields _requiredFields = new _fields();
    private final _imports _requiredImports = new _imports();
    
    public _portableMethod( _method _m )
    {
        this._m = _m;
    }
    
    public _method getMethod()
    {
        return _m;
    }
    
    public _methods getRequiredMethods()
    {
        return _requiredMethods;
    }
    
    public _fields getRequiredFields()
    {
        return _requiredFields;
    }
    
    public _imports getRequiredImports()
    {
        return _requiredImports;
    }
    
    public _portableMethod addRequiredMethods( _method... methods )
    {
        this._requiredMethods.addMethods( methods );
        return this;
    }
    
    public _portableMethod addRequiredImports( Object...imports )
    {
        this._requiredImports.addImports( imports );
        return this;
    }
    
    public _portableMethod addRequiredFields( _field... fields )
    {
        this._requiredFields.addFields( fields );
        return this;
    }     
}
