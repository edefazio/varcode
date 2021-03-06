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

import varcode.java.macro._autoLogMethodParams;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._methods;

/**
 * This is 
 * @author Eric
 */
public class _autoLogMethodParams_Use 
     extends TestCase
{

    public static abstract class PrototypeClass
    {
        //NOTE: no LOGGER
        
        public static void noArgs()
        {
            System.out.println( "Hello, No Arg " );
        }        
        
        public static void onePrimitiveArg( int count )
        {
            System.out.println( "Hello, One Arg "+ count );
        }
        
        public static void twoArgs( int count, String name )
        {
            System.out.println( "Hello, Two Arg "+ count + ", " + name );
        }
        
        public static void arrayArg( int[] counts )
        {
            System.out.println( "Hello, Array Arg "+ counts );
        }
        
        public abstract void someAbstractMethod( String arg );
        
        public static void emptyMethod( String param )
        {
            
        }
    }
    
    public enum PrototypeEnum 
    {
        ;
            
        public static void oneArg( String arg )
        {
            System.out.println( arg );
        }        
    }
    
    
    public static void main( String[] args )
    {
        _class _p = Java._classFrom( PrototypeClass.class );
        _methods _ms = _p.getMethods();
        
        for( int i = 0; i < _ms.count(); i++ )
        {
            _autoLogMethodParams.forMethod( _p, _ms.getAt( i ) );
        }
        
        System.out.println( _p.author() );
        
        //ok, let me test it
        _p.setName( "Tailored" ); //change the name so no class conflicts
        _p.setModifiers( "public", "abstract" ); //get rid of static (make top level class)
        Class adHocClass = _p.loadClass(); //load the class (cant instanitate, abstract)
        Java.call( adHocClass, "onePrimitiveArg", 4 );
        Java.call( adHocClass, "twoArgs", 4, "Eric" );
        
        Java.call( adHocClass, "arrayArg", new int[] {1,2,3,4} );
        
        _enum _e = Java._enumFrom( PrototypeEnum.class );
        
        _autoLogMethodParams.forMethod( _e, "oneArg" );
        
        System.out.println( _e.author() );
        
        Class adHocEnumClass = _e.loadClass();
        
        Java.call( adHocEnumClass, "oneArg", "eric" );
        
    }    
}
