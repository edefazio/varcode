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
package varcode.markup.$;

import varcode.markup.$ml.$CodeForm;
import java.util.Set;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class $CodeFormTest
    extends TestCase
{
 
    private class $ACodeForm
        extends $CodeForm
    {   
        public class $type$ {}
        $type$[] $fieldName$;
    
        public $type$ get$FieldName$At( int index )
        {
            /*{$*/
            if( this.$fieldName$ == null )
            {
                throw new IndexOutOfBoundsException( "$fieldName$ is null" );
            }
            if( index < 0 || index > this.$fieldName$.length )
            {
                throw new IndexOutOfBoundsException(
                    "index [" + index + "] is not in [0..." + this.$fieldName$.length + "]" );
            }
            return this.$fieldName$[ index ];            
            /*$}*/        
        }
    }
    
    static String N = "\r\n";
    
    public void test$CodeForm()
    {
        $ACodeForm acf = new $ACodeForm();
        Set<String> vars = acf.getVars();
        assertTrue( vars.contains( "fieldName" ) );
        assertEquals( 1, vars.size() );
        $CodeForm cf = acf.bindIn( VarContext.of( "fieldName", "count" ) );
        String bound = cf.toString();
        
        assertEquals(            
            "if( this.count == null )" + N +
            "{" +  N +
            "    throw new IndexOutOfBoundsException( \"count is null\" );" + N +
            "}" + N + 
            "if( index < 0 || index > this.count.length )" + N +
            "{" + N + 
            "    throw new IndexOutOfBoundsException(" + N +  
            "        \"index [\" + index + \"] is not in [0...\" + this.count.length + \"]\" );" + N +
            "}" + N + 
            "return this.count[ index ];", bound);                
    }
}
