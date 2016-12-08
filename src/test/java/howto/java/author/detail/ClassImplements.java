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
package howto.java.author.detail;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Workspace;
import varcode.java.metalang._class;
import varcode.java.metalang._interface;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ClassImplements 
    extends TestCase
{
    public interface MyInterface            
    {
        public String doThing( int v );
    }
    public void testClassImplements()
    {
        _class _c = _class.of( "public class MyImplementer implements MyInterface" )
            .imports(MyInterface.class)
            .method( "public String doThing( int v )",
                 "return \"AString\";" );
        
        Object inst = _c.instance();
        assertEquals( "AString", _Java.invoke( inst, "doThing", 0 ) ); 
    }
    
    public void testClassImplementsMultiple()
    {
        _interface _i = _interface.of( "howto.java.author.detail", 
               "public interface AnotherInterface" );
        
        _class _c = _class.of( "howto.java.author.detail", 
            "public class ImplementMultiple implements MyInterface, " )
            .imports( MyInterface.class, _i.getFullyQualifiedClassName() )
            .method( "public String doThing( int v )",
                 "return \"AString\";" );
        
        AdHocClassLoader cl = Workspace.compileNow( _i, _c );
        Object inst = _c.instance();
        assertEquals( "AString", _Java.invoke( inst, "doThing", 0 ) ); 
        Class[] ints = inst.getClass().getInterfaces();
        assertTrue( ints.length == 2 );        
    }
}
