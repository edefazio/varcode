/*
 * Copyright 2016 M. Eric DeFazio eric@varcode.io
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
package varcode.java.lang;

import varcode.java.metalang._enum;
import varcode.java.metalang._class;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;

/**
 *
 * @author eric
 */
public class minTest
    extends TestCase
{
    public void testMin_class()
    {
        _class c = _class.of( "Min" );
        Class cl = c.toJavaCase( ).loadClass();
        assertEquals( "Min", cl.getCanonicalName() );
        assertTrue( Modifier.isPublic( cl.getModifiers() ) );
    }
    
    public void testDefault_class()
    {
        _class c = _class.of( "class DefaultAccessibility" );
        Class cl = c.toJavaCase( ).loadClass();
        assertEquals( "DefaultAccessibility", cl.getCanonicalName() );
        assertFalse( Modifier.isPublic( cl.getModifiers() ) );
    }
    
    public void testMin_enum()
    {
        _enum e = _enum.of( "EMin" );
        Class cl = e.toJavaCase( ).loadClass();
        assertEquals( "EMin", cl.getCanonicalName() );
        assertTrue( Modifier.isPublic( cl.getModifiers() ) );
    }
    
    public void testDefault_enum()
    {
        _enum c = _enum.of( "enum DefaultAccessibility" );
        Class cl = c.toJavaCase( ).loadClass();
        assertEquals( "DefaultAccessibility", cl.getCanonicalName() );
        assertFalse( Modifier.isPublic( cl.getModifiers() ) );
    }

}
