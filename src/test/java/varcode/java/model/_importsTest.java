/*
 * Copyright 2017 Eric.
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
package varcode.java.model;

import junit.framework.TestCase;
import java.util.*;
import varcode.java.Java;

/**
 *
 * @author Eric
 */
public class _importsTest
    extends TestCase
{
    public void testImports()
    {
        _class _c = Java._classFrom( _importsTest.class );
        System.out.println( _c.getImports() );
        assertTrue( _c.getImports().contains( "java.util.*" ) );
        
    }
}
