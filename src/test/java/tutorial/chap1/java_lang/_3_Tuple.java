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
package tutorial.chap1.java_lang;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.lang._class;
import varcode.java.lang._code;

/**
 *
 * @author Eric DeFazio
 */
public class _3_Tuple
    extends TestCase
{
    public static abstract class Tuple
    {
        protected static boolean isEquivalent( Object p1, Object p2 )
        {
            if( p1 == p2 )
            {
                return true;
            }
            if( p1 == null )
            {
                return p2 == null;
            }
            if( !( p2 == null ) )
            {
                return p1.equals( p2 );
            }
            return false;
        }
        
        
        public static void createFieldForElement( 
            int index, _class c, Object o )
        {
            if( o == null )
            {
                c.property( "final Object _" + index + " = null;" );
                return;
            }            
            if( o instanceof Integer )
            {
                createFieldForElement( index, c, (Integer)o );
                return;
            }
            throw new RuntimeException( "Cannot support "+o+" elements in tuples" );
        }
        
        public static void createFieldForElement( 
            int index, _class c, String s )
        {
            c.property( //todo make sure I escape the string
                "private final String _" + index + " = \"" + s + "\";" );            
        }
        
        public static void createFieldForElement( 
            int index, _class c, byte b )
        {
            c.property( "private final byte _" + index + " = (byte)" + b + ";" );
        }
        
        public static void createFieldForElement( 
            int index, _class c, Integer i )
        {
            c.property( "private final int _" + index + " = " + i + ";" );
        }
        
        public static void createFieldForElement( int index, _class c, int i )
        {
            c.property( "private final int _" + index + " = " + i + ";" );
        }
        
        public static void createFieldForElement( int index, _class c, float f )
        {
            c.property( "private final float _" + index + " = " + f + "f;" );
        }        
        
        public static Tuple of( Object... elements )
        {
            return (Tuple)modelOf( elements ).instance( );
        }
        
        public static _class modelOf( Object...elements )
        {
            //create all fields
            _class _c = new _class( 
                "public final class _" + UUID.randomUUID().toString().replace("-", "" ) );
            for( int i = 0; i < elements.length; i++ )
            {
                createFieldForElement( i, _c, elements[ i ] );
            }
            
            _c.extend( Tuple.class );
            //now I need to implement the count(),
            //contains() and isEquivalent()
            //methods
            _c.method( "public final int count()", "return " + elements.length + ";" );
            
            _code containsCode = new _code();
            containsCode.addTailCode( "return isEquivalent( _0, o )" );
            for( int i = 1; i < elements.length; i++ )
            {
                containsCode.addTailCode( 
                    "    | isEquivalent( _" + i +", o ) " );
            }
            containsCode.addTailCode( ";" );
            _c.method( "public final boolean contains( Object o )", containsCode );
                
            return _c;
            //return (Tuple)_c.instance( );
        }
        
        
        /**the number of elements in the tuple */
        public abstract int count();
        
        public abstract boolean contains( Object element );
//        
//        public boolean isEquivalent( Tuple t )
//        {   
//            return t.count() == count() && 
//                /*{{+: isEquivalent( _{+fieldIndex+}, t.getAt(+fieldIndex+} );
//                */true;
//                /*}}*/
//        }
        
    }
    
    public void testSimple( )
    {
        _class c = Tuple.modelOf( 1,2 );
        System.out.println( c ); 
        Object instance = c.instance( );
        
        Java.getFieldValue( instance, "_1" );
        
    }
}
