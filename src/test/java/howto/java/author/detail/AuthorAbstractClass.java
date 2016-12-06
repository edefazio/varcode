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
import varcode.java.metalang._class;
import varcode.java.metalang._javadoc;

/**
 *
 * @author Eric
 */
public class AuthorAbstractClass 
    extends TestCase
{
    /**
     * Verify I can create and load an abstract class
     */
    public void testAbstract()
    {
        _class _abs = _class.of( "howto.java.author.detail", 
            "public abstract class MyAbstClass" )
            .method( _javadoc.of( "This is a method javadoc" ) , 
                "public void myMethod()",
                "System.out.println( \"Called MyMethod\" );" );
        _abs.loadClass();
        
    }
}