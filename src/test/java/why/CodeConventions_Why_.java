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
package why;

import java.util.Map;
import java.util.UUID;
import varcode.java.model._args;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._interface;
import varcode.java.model._javadoc;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.java.model._modifiers;
import varcode.java.model._modifiers._modifier;
import varcode.java.model._nests;
import varcode.java.model._parameters;
import varcode.java.model._parameters._parameter;
import varcode.java.model._var;

/**
 * 
 * <BLOCKQUOTE>"_" = "meta language model of..."</BLOCKQUOTE>
 * 
 * 
 * the "_" prefix is shorthand/ translated to the convention of 
 * a Discussion on why we use the "_" prefix to mean 
_c ( _class )
_e ( _enum )
_i ( _interface )
_f ( _field )        _fs ( _fields ) 
_m ( _method )       _ms ( _methods )
_a ( _arg )          _as ( _args )
_n ( _nest )         _ns ( _nesteds )
_p ( _parameter )    _ps ( _parameters 


_mods (_modifiers MLM)
_imp ( imports MLM)

_code

_ex ( _extends MLM )

_ann ( annotation MLM)
_ctor( constructor MLMs)
_ctors ( multiple constructor MLMS)
_p (ionmst ariable convention
 * @author Eric
 */
public class CodeConventions_Why_ 
{
    _class _c = _class.of( "public class A" );
    _interface _i = _interface.of( "public interface I" );
    _enum _e = _enum.of( "public enum E" );
    _field _f = _field.of( "int f;" ); //the value of variable f, the (literal) string f or the Mo
    _fields _fs = _fields.of( _f, _field.of( "String name;" ) );
    _method _m = _method.of( "public void meth()" );
    _methods _ms = _methods.of( _m, _method.of( "void doNothing()" ) );
    _var _v = _var.of( String.class, "name" );
    
    _modifier _mod = _modifier.FINAL;
    _modifiers _mods = _modifiers.of( "public" );
    
    _parameter _p = _parameter.of( "String", "name" );    
    _parameters _ps = _parameters.of( _p, _parameter.of("int", "count" ) );
    
    _javadoc _jd = _javadoc.of( "one line javadoc" );
    _constructor _ctor = _constructor.of( "public MyClass()" );
    _code _cd = _code.of( "/*One line of code , a comment */" );
    _nests _ns = _nests.of( _c, _i );
    
        
    _constructors _ctors = _constructors.of( _ctor, 
        _constructor.of( "public MyClass( String s )", "this.s = s;" ) );
    
    _imports _imps = _imports.of( Map.class, UUID.class );
    
    _args _as = _args.of( "1", "A" );
    
}
