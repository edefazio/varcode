/*
 * Copyright 2016 eric.
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
package tutorial.chap1.java_metalang.detail;

import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import varcode.java.metalang._class;
import varcode.java.metalang._enum;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._modifiers;

/**
 *
 * @author eric
 */
public class Fields
    extends TestCase
{
    public void testBuildFields( )
    {
        _field _f = _field.of( "public String a;" );
        _field _f2 = new _field( Modifier.PUBLIC, "String", "b" );
        _field _f3 = new _field( _modifiers.of( Modifier.PUBLIC ), "String", "b" ); 
        _field _f4 = new _field( _modifiers.of( "public" ), "String", "b" ); 
        
        _class _c = _class.of( "public class A" )
            .field(_f )
            .field( _modifiers.of( "public", "final" ), "String", "b" )
            .field( Modifier.PUBLIC | Modifier.STATIC, "String", "c" );
        
        _enum _e = _enum.of("public enum E")
            .field(_f );
                
    }
}
