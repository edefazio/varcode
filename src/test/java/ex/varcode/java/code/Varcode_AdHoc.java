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
package ex.varcode.java.code;

import java.util.UUID;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java._Java;
import varcode.java.langmodel._class;

public class Varcode_AdHoc
    extends TestCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( Varcode_AdHoc.class );
    
    public static void main( String... args )
    {
        _class c = _class.of( "AdHoc" )
            .imports( UUID.class )
            .method( "public String createId",
                "return UUID.randomUUID().toString();" );
        
        System.out.println( c );        
        
        String id = (String) _Java.invoke(
            c.instance(), 
            "createId" ); 
        
        System.out.println( id );
    }
    
    public void testIt()
    {
        main( );
    }
}
