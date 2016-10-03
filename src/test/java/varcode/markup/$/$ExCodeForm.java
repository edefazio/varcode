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
import varcode.context.VarContext;
import varcode.dom.Dom;
import varcode.java.code._methods._method;

/**
 *
 * @author eric
 */
class $ExCodeForm
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
    
    public static final $ExCodeForm CODEFORM = new $ExCodeForm();
    
    public static void main( String[] args )
    {
        //System.out.println( CODEFORM.getCode() );
        Dom d = CODEFORM.getDom();
        //System.out.println( f );
        //String derived = 
        //    f.derive( VarContext.of( "type", int.class, "fieldName", "count" ) ); 
        
        //System.out.println( derived );
        
        _method m = _method.of("public int getCountAt( int index )",
            CODEFORM );
        
        System.out.println( m );
        m.bindIn( VarContext.of("type", int.class, "fieldName", "count" ) ); 
        System.out.println( m );
    }
}
