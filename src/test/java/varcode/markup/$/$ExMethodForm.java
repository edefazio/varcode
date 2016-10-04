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

import varcode.markup.$ml.$MethodForm;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class $ExMethodForm
    extends $MethodForm
{
    public class $type$ {}
    $type$[] $fieldName$;
    
    
    public $ExMethodForm()
    {
        super( );
    }
    
    
    /*{$*/
    public $type$ get$FieldName$At( int index )
    {
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
    }
    /*$}*/
    
    public static void main(String[] args)
    {        
        //basically I find 
        $ExMethodForm em = new $ExMethodForm();
        
        System.out.println( em.method );
        /*
        _method m = .getMethod();
        
        form form = 
        System.out.println( m );
        String derived = 
            f.derive( VarContext.of("type", int.class, "fieldName", "count" ) ); 
        
        
        //System.out.println( derived );
        */
    }
    
}
