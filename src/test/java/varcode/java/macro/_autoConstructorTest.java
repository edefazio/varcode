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
package varcode.java.macro;

import junit.framework.TestCase;
import varcode.java.model._enum;

/**
 *
 * @author Eric
 */
public class _autoConstructorTest
    extends TestCase
{
    
    public void testEnumCtor()
    {
        _enum _e = _enum.of("MyE")
            .fields("private final String name;", "private final int count;");
        
        _auto.constructorTo( _e );
        
        System.out.println( _e );
    }
    
    
}
