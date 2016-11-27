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
package varcode.java.langmodel.auto;

import varcode.java.langmodel.cs._autoToString;
import varcode.java.langmodel.auto._autoDto;
import junit.framework.TestCase;
import varcode.java.langmodel._class;

/**
 *
 * @author eric
 */
public class _autoToStringTest
    extends TestCase
{
    
    public void testNoFieldsToString()
    {
        _autoDto a = _autoDto.of("ex.varcode.NoFields");
        _class toStrung = _autoToString.of( a.toClassModel() );
        assertEquals( "ex.varcode.NoFields"+ System.lineSeparator(), 
            toStrung.instance( ).toString() );
    }
    
    public void testPrimitiveToString()
    {
        _autoDto a = _autoDto.of("ex.varcode.Primitives")
            .property("int a = 1;")
            .property("long b = 2;")    
            .property("float f = 3.0f;");
        
        _class toStrung = _autoToString.of( a.toClassModel() );
        
        assertEquals( 
            "ex.varcode.Primitives" + System.lineSeparator()  +
            "    a = 1" + System.lineSeparator() + 
            "    b = 2" + System.lineSeparator() +
            "    f = 3.0", toStrung.instance().toString().trim() );        
    }
    
    public static void main( String[] args )
    {
        _autoDto a = _autoDto.of("ex.varcode.MyDto")
            .property( "public int count = 1;" )
            .property( "public String name = \"Default\";" )
            .property( "public int[] values = new int[]{1,2,3,4}");
        
        _class toStrung = _autoToString.of( a.toClassModel() );
        
        System.out.println( toStrung );
        
        Object inst = toStrung.instance( );
        System.out.println ( inst );            
    }
}
