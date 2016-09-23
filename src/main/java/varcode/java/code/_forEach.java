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
package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.doc.translate.JavaTranslate;
import varcode.doc.translate.TranslateBuffer;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _forEach
{
    /** 
     * OK its now apparent the Translator is being used 
     * without the buffer part, so I need to separate that out
     * in the future
     */
    static TranslateBuffer tb = new TranslateBuffer();    
    
    
    /**
     * 
     * @param collection the variable name of the collection or array
     * @param elementType the element type within the collection
     * @param elementName the name for each element
     * @return _forEach that contains a block
     */
    public static String _each( 
        String collection, Class elementType, String elementName )
    {
        return "for( " + JavaTranslate.INSTANCE.translate( elementType ) + " " +  elementName +" : "+ collection +" )";
    }
    
    
    public static void main(String[] args)
    {
        String[] strings = new String[]{ "a", "b" };
        for( String a : strings )
        {
            System.out.println( a );
        }        
        System.out.println( _each( "strings", String.class, "s" ) );
        System.out.println( _each( "list", int.class, "i" ) );
        
        List<Integer> list = new ArrayList<Integer>();
        for( Integer u : list )
        {
            System.out.println( u );
        }
    }
}
