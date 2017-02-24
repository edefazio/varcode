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

import java.util.List;
import varcode.java.model._ann;
import varcode.java.model._anns;
import varcode.java.model._fields;

/**
 * 
 * @author Eric
 */
public class _macroFields
{
    
    public static void prepareFields( List<_macro._typeExpansion> _expansions, _fields _fs )
    {        
        for( int i = 0; i < _fs.count(); i++ )
        {
            _macro._typeExpansion e = prepareField( _fs.getAt( i ) );
            if( e != null )
            {
                _expansions.add( e );
            }
        }        
    }
    /**
     * processes a single field of a class, enum, interface or annotationType
     * return the 
     * @param _f
     * @return 
     */
    public static _macro._typeExpansion prepareField( _fields._field _f )
    {
        _anns _as = _f.getAnnotations();
        if( !_as.contains( _macro.remove.class ) )
        {   //we are either copying or tailoring the field                           
            _ann parameter = _as.getOne( _macro.$.class );
            _ann sig = _as.getOne( _macro.sig.class );
            
            if( parameter != null )
            {   //you CANNOT have BOTH sig Macros AND parameterization
                //System.out.println( "processing " + parameter );
                
                String values = parameter.getAttributes().values.get( 0 );
                //System.out.println( "values " + values );                
                String[] valuesArray = _ann._attributes.parseStringArray( values );
                //System.out.println( "values[0]" + valuesArray[0] );                
                //System.out.println( "values[1]" + valuesArray[1] );                
                
                _fields._field _p = new _fields._field( _f );
                _p.getAnnotations().remove( parameter.getName() );
                return _macro.ExpandField.parameterize( _p, valuesArray );
            }
            //else if( sig != null )
            //{   //we didnt explicitly tailor or remove it, so copy the method
            //    System.out.println( "processing "+ sig );
            // }
            //just copy the field                        
            return new _macro.CopyField( _f );
        }
        return null;
    }
}
