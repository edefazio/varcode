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

/**
 *
 * @author Eric
 */
public class ClassExtends 
    extends TestCase
{
    public static class BaseClass
    {
        public static final String ID = "100";
    }
    
    public void testClassExtends()
    {
        _class _c = _class.of( "howto.java.author.detail",
            "public class MyClass extends BaseClass" )
            .imports( BaseClass.class );
        
        Object instance = _c.instance();
        //verify I can retrieve the field value from the base class
        assertEquals( "100", _Java.getFieldValue( instance, "ID" ) ); 
    }
    
    public void testBaseAndExtendsClass()
    {
        _class _abstract = _class.of( "howto.java.author.detail",
            "public abstract class AbstractBaseClass" )
            .method( "public abstract int getCount()" );
        _class _extendsClass = _class.of( "howto.java.author.detail",
            "public class ExtendsClass extends AbstractBaseClass" )
            .method( "public int getCount()", 
                "return 37;");
        
        AdHocClassLoader cl = 
            Workspace.compileNow( _abstract, _extendsClass );
        
        Class clazz = cl.find( _extendsClass );
        Object instance = _Java.instance( clazz );
        assertEquals( 37, _Java.invoke( instance, "getCount" ) );
        
    }
}
