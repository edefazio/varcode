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
package tutorial.varcode.chap5.mixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.langmodel._class;
import varcode.java.langmodel._code;
import varcode.java.langmodel._component;
import varcode.java.langmodel._enum;
import varcode.java.langmodel._fields;
import varcode.java.langmodel._fields._field;
import varcode.java.langmodel._imports;
import varcode.java.langmodel._interface;
import varcode.java.langmodel._methods;
import varcode.java.langmodel._methods._method;
import varcode.java.langmodel._nesteds;
import varcode.java.load.langmodel._JavaLoader;

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
     * @param component a (_class, _enum, _interface)
     */
    public static void addImport( _component component )
    {        
        _imports imports = component.getImports();
        imports.addImports( Logger.class, LoggerFactory.class );        
    }
    
    /**
     * conditionally add a static final LOG field to the class
     * if a LOG static field if it doesn't exist, create one
     * @param component a (_class, _enum, _interface)
     */
    public static void addLOGField( _component component )
    {
        _fields fs = component.getFields();
        _field f = fs.getByName( "LOG" );
        if( f == null )
        {
            fs.addFields( 
                _field.of(
                "public static final Logger LOG = LoggerFactory.getLogger( " 
                    + component.getName() + ".class );" ) );
        }
    }
    
    public static _component addMethodLogging( _component component )
    {
        return addMethodLogging( component, true );
    }
    
    /**
     * 
     * @param component the component to add method logging to
     * @return the modified component
     */
    public static _component addMethodLogging( 
        _component component, boolean applyToAllNestedComponents )
    {   
        if( applyToAllNestedComponents )
        {
            _nesteds nests = component.getNesteds();    
            for( int i = 0; i < nests.count();i++ )
            {
                addMethodLogging( nests.getAt( i ), true );
            }
        }
        _methods methods = component.getMethods();
                
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
            addImport( component );
            addLOGField(  component );
        }
        return component;
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
        _class n = _JavaLoader._Class.from( 
            AddForNestedLogger.NestedClass.class );
        
        System.out.println( n );
        
        //_class c = _Load.INSTANCE._classOf( SomeDumbClass.class );
        _class c = _JavaLoader._Class.from( SomeDumbClass.class );
        addMethodLogging( c );
        System.out.println( c );
        
        //_interface i = _Load.INSTANCE._interfaceOf( NoLoggerToAdd.class );
        _interface i = _JavaLoader._Interface.from( NoLoggerToAdd.class );
        System.out.println( i );
        
        addMethodLogging( i );        
        System.out.println( i );

        //_enum e = _Load.INSTANCE._enumOf( AddForNestedLogger.class ) ;
        _enum e = _JavaLoader._Enum.from( AddForNestedLogger.class );
        addMethodLogging( e );
        System.out.println( e );        
    }
}
