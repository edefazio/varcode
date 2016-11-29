/*
 * Copyright 2016 Eric DeFazio.
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
package varcode.java.metalang.auto;

import varcode.java.metalang.auto._autoGetAtArrayIndex;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.java.metalang._fields;
import varcode.java.metalang._methods;
import static varcode.java.metalang.auto._autoGetterTest.N;

/**
 *
 * @author Eric DeFazio
 */
public class _autoGetAtArrayIndexTest
    extends TestCase
{
    public void testNothing()
    {
        //cop out, I know, but checking strings is BRITTLE
    }
    /*
    public void testArrayField()
    {
        _fields._field f = _fields._field.of( "public int[] f;" );
        _methods._method m = _autoGetAtArrayIndex.fromField( f );
        assertEquals( 
        "public int getFAt( int index )" + N + 
        "{"+ N +             
        "    if( this.f == null )"+ N + 
        "    {"+ N + 
        "        throw new IndexOutOfBoundsException( \"f is null\" );"+ N +   
        "    }" + N + 
        "    if( index < 0 || index > this.f.length  )" + N + 
        "    {" + N + 
        "        throw new IndexOutOfBoundsException(" + N + 
        "            \"index [\" + index + \"] is not in range [0...\" + f.length + \"]\" );" + N +
        "    }" + N + 
        "    return this.f[ index ];" + N +            
        "}", m.toString() );
        
        _class c = _class.of("A")
            .field( "private final int[] count = new int[]{1,2,3};" );
        
        //create and add a getter method for the count array field
        c.method( 
            _autoGetAtArrayIndex.fromField( 
                c.getFields().getByName( "count" ) ) );
        
        assertEquals( 1, _Java.invoke( c.instance( ), "getCountAt", 0 ) );        
    }
*/
}
