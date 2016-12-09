/*
 * Copyright 2016 Eric DeFazio.
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
package tutorial.varcode.chapx.appendix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.metalang._class;
import varcode.java.metalang._code;
import varcode.java.metalang._enum;
import varcode.java.metalang._fields;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._imports;
import varcode.java.metalang._interface;
import varcode.java.metalang._methods;
import varcode.java.metalang._methods._method;
import varcode.java.metalang._nests;
import varcode.java.load.JavaMetaLangLoader;
import varcode.java.metalang.JavaMetaLang._model;

/**
 * Simple example for a "mixin" like metaprogram which takes
 * in a component
 * Load the model for a component (class),
 * then mutate all methods
 * @author Eric DeFazio
 */
public class _1_AddMethodLogging
{
    /**
     * add the appropriate Logger imports
     * @param _component a (_class, _enum, _interface)
     */
    public static void addImport( _model _component )
    {        
        _imports imports = _component.getImports();
        imports.addImports( Logger.class, LoggerFactory.class );        
    }
    
    /**
     * conditionally add a static final LOG field to the class
     * if a LOG static field if it doesn't exist, create one
     * @param _component a (_class, _enum, _interface)
     */
    public static void addLOGField( _model _component )
    {
        _fields _fs = _component.getFields();
        _field _f = _fs.getByName( "LOG" );
        if( _f == null )
        {
            _fs.addFields( _field.of( 
                "public static final Logger LOG = LoggerFactory.getLogger( " 
                + _component.getName() + ".class );" ) );
        }
    }
    
    public static _model addMethodLogging( _model _component )
    {
        return addMethodLogging(_component, true );
    }
    
    /**
     * 
     * @param _component the component to add method logging to
     * @return the modified component
     */
    public static _model addMethodLogging( 
        _model _component, boolean applyToAllNestedComponents )
    {   
        if( applyToAllNestedComponents )
        {
            _nests nests = _component.getNesteds();    
            for( int i = 0; i < nests.count();i++ )
            {
                addMethodLogging( nests.getAt( i ), true );
            }
        }
        _methods methods = _component.getMethods();
                
        boolean hasLoggedMethod = false;
        
        for( int i = 0; i < methods.count(); i++ )
        {
            _method m = methods.getAt( i );
            if( !m.getBody().isEmpty() )
            {
                hasLoggedMethod = true;
                
                _code body = m.getBody();
                
                body.addHeadCode( 
                    "LOG.trace( \"in method " + m.getName()+ "\" );" );            
            }
        }
        //ONLY add a LOG field and imports iff the component has at
        //least one method that can be logged
        if( hasLoggedMethod )
        {
            addImport( _component );
            addLOGField( _component );
        }
        return _component;
    }
    
    public static class SomeDumbClass
    {
        public void doIt()
        {
            System.out.println( " I Said Do it " );            
        }
        
        public String renewIt( String s )
        {
            System.out.println( "I said renew it" );
            return s + "100 ";
        }
    }
    
    public interface NoLoggerToAdd
    {
        String getName();
    }
    
    public enum AddForNestedLogger
    {
        ;
        public static class NestedClass
        {
            public void someMethod()
            {
                System.out.println( "Called someMethod" );
            }
        }
    }
    
    public static void main( String[] args )
    {
        //? Can I load a deep nested class?
        _class _n = JavaMetaLangLoader._Class.from( 
            AddForNestedLogger.NestedClass.class );
        
        System.out.println( _n );
        
        //_class c = _Load.INSTANCE._classOf( SomeDumbClass.class );
        _class _c = JavaMetaLangLoader._Class.from( SomeDumbClass.class );
        addMethodLogging( _c );
        System.out.println( _c );
        
        //_interface i = _Load.INSTANCE._interfaceOf( NoLoggerToAdd.class );
        _interface _i = JavaMetaLangLoader._Interface.from( NoLoggerToAdd.class );
        System.out.println(_i );
        
        addMethodLogging(_i );        
        System.out.println(_i );

        //_enum e = _Load.INSTANCE._enumOf( AddForNestedLogger.class ) ;
        _enum _e = JavaMetaLangLoader._Enum.from( AddForNestedLogger.class );
        addMethodLogging(_e );
        System.out.println(_e );        
    }
}
