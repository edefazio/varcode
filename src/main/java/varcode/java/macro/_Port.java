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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Model.ModelException;
import varcode.java.lang.JavaMetaLang;
import varcode.java.lang.JavaMetaLang._model;
import varcode.java.lang._class;
import varcode.java.lang._fields._field;
import varcode.java.lang._methods._method;

/**
 * Ports a _facet to {@code _models} like  
 * @author Eric   
 */
public class _Port
     implements JavaMacro.Mutator
{
    public static final Logger LOG = 
        LoggerFactory.getLogger( _Port.class );
    
    public static final String N = System.lineSeparator();
    
    
    public static _class portForce( _portableMethod _pMethod, _class _c )
    {
        return (_class)portForce( _pMethod, (_model)_c );
    }
    
    
    
    /**
     * Hmm, should I have a portForce(...) (to overwrite)
     * and a port(...) to fail if I encounter an existing method?
     * 
     * This ports the portable method _pMethod to the _class _c
     * @param _pMethod the method to "port" to the _class
     * @param _c the class (receiver) of the method
     * @return the modified _class
     */
    public static _model portForce( _portableMethod _pMethod, _model _c )
    {
        //first, create a clone of the _class, so we dont have to roll back
        _model _clone = _c.clone();
        
        //now apply changes to the _clone, if we fail... don't mutate _c
        
        boolean changed = false;
        
        List<_method> _ms = _clone.getMethods().getByName(  
            _pMethod.getMethod().getName() );
        if( _ms != null )
        {
            for( int i = 0; i < _ms.size(); i++ )
            {
                _method _m  = _ms.get( i );
                if( _m.getParameters().count() == 
                    _pMethod.getMethod().getParameters().count() )
                {                
                //we could do a "more envolved" search, but for now
                //lets just do this
                //if( _m.getModifiers().contains( "abstract" ) )
                //{
                    //its an abstract method, lets replace it with
                    // a "real method"
                //}
                //lets delta the method
                    changed = true;
                    _m.getSignature().setModifiers( 
                        _pMethod.getMethod().getModifiers() );
                    _m.setBody( _pMethod.getMethod().getBody() );
                }    
            }
        }
        if( ! changed )
        {
            //add the method only if we didnt "change" a method
            _clone.getMethods().addMethod( _pMethod.getMethod ()); 
            //_clone.add(_pMethod.getMethod() );
        }
        
        //add imports (merge to current imports)
        
        _clone.getImports().addImports( _pMethod.getRequiredImports() );
        
        for( int i = 0; i < _pMethod.getRequiredFields().count(); i++ )
        {
            _field _f = _pMethod.getRequiredFields().getAt( i );
            if( _clone.getFields().canAddFieldName( 
                _f.getName() ) )
            {
                LOG.debug( "adding required field " + _f );
                _clone.getFields().addFields( _f );
            }
            else
            {
                LOG.debug( "class already has field named " + _f.getName() 
                    + " for requirecd field " + _f );
            }
        }         
        for( int i = 0; i < _pMethod.getRequiredMethods().count(); i++ )
        {
            //if the required method is abstract, make sure there is
            // a "SIMILAR ENOUGH" method (with same name and param Count)
            // 
            _method _rm = _pMethod.getRequiredMethods().getAt( i );
            if( _rm.isAbstract() )
            {   //THERE MUST BE A CORRESPONDING METHOD DEFINED in clone
                // IF NOT THROW EXCEPTION
                List<_method> candidates = _clone.getMethods().getByName(
                    _rm.getName() );
                
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
                        "Unable to port method :" + N + _pMethod.getMethod() + N 
                      + "...to _class :" + N + _c + N 
                      + "...missing implementation of required method " + N + 
                      _rm.getSignature() );
                }
            }
            else
            {
                //if they dont already HAVE this method, I need to add it
                List<_method> candidates = 
                    _clone.getMethods().getByName( _rm.getName() );
                
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
                    _clone.getMethods().addMethod( _rm );
                }
            }
        }        
        return _clone;
    }    
}
