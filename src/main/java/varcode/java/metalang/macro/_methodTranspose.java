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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Model.ModelException;
import varcode.java.metalang._class;
import varcode.java.metalang._class;
import varcode.java.metalang._fields;
import varcode.java.metalang._fields;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._imports;
import varcode.java.metalang._imports;
import varcode.java.metalang._methods;
import varcode.java.metalang._methods;
import varcode.java.metalang._methods._method;
import varcode.java.metalang._methods._method._signature;

/**
 * Aggregates a method and all of it's dependencies 
 * (_imports, _fields, other _methods) to allow the method to be transposed 
 * to another _javaComponent (_class, _enum)
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
public class _methodTranspose 
{
    public static _methodTranspose of( _method method )
    {
        return new _methodTranspose( method );
    }
    
    public static final Logger LOG = 
        LoggerFactory.getLogger(_methodTranspose.class );
    
    //this is the method signature and body to be transposed (to some other
    // _class, _enum, _staticBlock)
    private final _method _m;
    
    // the main method _m (above) can express some "external dependencies"
    // on some other things (imports, methods, fields)
    private final _methods _requiredMethods = new _methods();
    private final _fields _requiredFields = new _fields();
    private final _imports _requiredImports = new _imports();
    
    public _methodTranspose( _method _m )
    {
        this._m = _m;
    }
    
    public _methodTranspose addRequiredMethods( _method... methods )
    {
        this._requiredMethods.addMethods( methods );
        return this;
    }
    
    public _methodTranspose addRequiredImports( Object...imports )
    {
        this._requiredImports.addImports( imports );
        return this;
    }
    
    public _methodTranspose addRequiredFields( _field... fields )
    {
        this._requiredFields.addFields( fields );
        return this;
    }
    
    public static final String N = System.lineSeparator();
    
    /**
     * 
     * @param _c
     * @return 
     */
    public _class transposeTo( _class _c )
    {
        //first, create a clone of the _class, so we dont have to roll back
        _class _clone = _c.clone();
        
        //now apply changes to the _clone, if we fail... don't mutate _c
        
        //add the method
        _clone.add( _m );
        
        //add imports (merge to current imports)
        _clone.add( _requiredImports );
        
        for( int i = 0; i < _requiredFields.count(); i++ )
        {
            _field _f = _requiredFields.getAt( i );
            if( _clone.getFields().canAddFieldName( 
                _f.getName() ) )
            {
                LOG.debug( "adding required field " + _f );
                _clone.add( _f );
            }
            else
            {
                LOG.debug( "class already has field named " + _f.getName() 
                    + " for requirecd field " + _f );
            }
        }
        
        for( int i = 0; i < _requiredMethods.count(); i++ )
        {
            //if the required method is abstract, make sure there is
            // a "SIMILAR ENOUGH" method (with same name and param Count)
            // 
            _method _rm = _requiredMethods.getAt( i );
            if( _rm.isAbstract() )
            {   //THERE MUST BE A CORRESPONDING METHOD DEFINED in clone
                // IF NOT THROW EXCEPTION
                List<_method> candidates = 
                    _clone.getMethodsByName(_rm.getName() );
                
                boolean foundImpl = false;
                
                for( int j = 0; j < candidates.size(); j++ )
                {   
                    if( candidates.get( j ).getSignature().matchesSignature( 
                        _rm.getSignature() ) )
                    {   //i found a method that matches the signature 
                        // of the required method, dont add
                        foundImpl = false;
                        break;
                    }
                }
                if( ! foundImpl )
                {
                    throw new ModelException(
                        "Unable to transpose method :" + N + _m + N 
                      + "...to _class :" + N + _c + N 
                      + "...missing implementation of required method " + N + 
                      _rm.getSignature() );
                }
            }
            else
            {
                //if they dont already HAVE this method, I need to add it
                List<_method> candidates = 
                    _clone.getMethodsByName(_rm.getName() );
                
                boolean needToAdd = true;
                
                for( int j = 0; j < candidates.size(); j++ )
                {   
                    if( candidates.get( j ).getSignature().matchesSignature( 
                        _rm.getSignature() ) )
                    {   //i found a method that matches the signature 
                        // of the required method, dont add
                        needToAdd = false;
                        break;
                    }
                }
                if( needToAdd )
                {
                    LOG.debug( "Adding method " +_rm.getSignature() + " to clone" );
                    _clone.method( _rm );
                }
            }
        }
        
        return _clone;
    }
}
