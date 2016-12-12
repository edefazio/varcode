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

import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._methods;
import varcode.java.lang._methods._method;

/**
 *
 * @author Eric
 */
public class _autoJavadocTest 
    extends TestCase
{
    public void testJavadoc()
    {
        _class _c = _class.of( "ex.varcode", "public class A" );
        
        _autoJavadoc.to( _c );
        
        System.out.println( _c ); 
    }
    
    public void testJavadocMethods()
    {
        _class _c = _class.of( "ex.varcode", "public class A", 
            _method.of( "public void doIt()", "System.out.println( \"Do it\");" ) );
        
        _autoJavadoc.to( _c );
        
        System.out.println( _c ); 
    }
}
