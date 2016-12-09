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
package howto.java.load;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.java.metalang._interface;
import varcode.load.DirectorySourceLoader;

/**
 *
 * @author Eric
 */
public class Load_interface 
    extends TestCase
{

    /** Create _interface (metalang model) from a String, great for testing */
    public void test_interfaceFromString()
    {
        _interface _i = _Java._interfaceFrom(
            "public interface A { public static int count = 100; }" );
        
        assertEquals( "A", _i.getName() );
    }
    
    /** Load a _interface based on a runtime Java class */
    public void test_interfaceFromRuntimeClass()
    {
        _interface _i = _Java._interfaceFrom( TopLevelInterface.class );
        assertEquals( _i.getPackage().getName(), 
            TopLevelInterface.class.getPackage().getName() );
    }
    /** Load a _interface from runtime class using a custom SourceLoader */ 
    public void test_interfaceUsingCustomSourceLoader()
    {
        _interface _i = _Java._interfaceFrom( 
            DirectorySourceLoader.of( 
                System.getProperty( "user.dir" ) + "/src/test/java" ),
            TopLevelInterface.class );
        
        assertEquals( _i.getPackage().getName(), getClass().getPackage().getName() );
    }
    
    /** Interface Javadoc comment */
    public interface NestedInterface 
    {
        public int getCount();        
    }
    
    /** Test loading an _interface from a nested interface */
    public void testNested_interface()
    {
        _interface _i = _Java._interfaceFrom( NestedInterface.class );
        
        assertTrue( _i.getModifiers().containsAll( "public" ) );
        // NOTE: when you load a nested/inner class 
        // it retains the package and ALL imports from the top-level class
        assertEquals( _i.getPackage().getName(), getClass().getPackage().getName() );
        assertTrue( _i.getImports().containsAll( 
            TestCase.class, _Java.class, _class.class ) );
    }
}
