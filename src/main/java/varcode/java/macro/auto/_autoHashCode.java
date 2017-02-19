/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.java.macro.auto;

import java.util.ArrayList;
import java.util.List;
import varcode.author.Author;
import varcode.java.model._class;
import varcode.markup.Template;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric
 */
public enum _autoHashCode
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    
    public static _class to( _class _c )
    {
        return _c.method( of( _c ) );
    }
    
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    public static _method of( _class _model )
    {        
        _fields _fs = _model.getFields();
        List<_field> _hcFields = new ArrayList<_field>();
        for( int i=0; i< _fs.count(); i++ )
        {
            _field _f = _fs.getAt( i );
            if( !_f.getModifiers().contains( "static" ) )
            {
                _hcFields.add( _f );
            }
        }
        return of( _hcFields.toArray( new _field[ 0 ] ) );
    }
    
    /**
     * "return Objects.hash(name, age, passport);"
     * @param _fs
     * @return 
     */
    public static _method of( _fields _fs )
    {
        return of( _fs.getFieldNames() );
    }
    
    //Here we fully qualified the class to use to avoid having to 
    // manually import it
    public static Template BODY = 
        BindML.compile( "return java.util.Objects.hash( {{+:this.{+field+}, +}} );" );
    
    /**
     * 
     * @param fieldNames
     * @return 
     */
    public static _method of( String[] fieldNames )
    {
        return _method.of( "@Override", 
            "public int hashCode()", 
             Author.toString( BODY, "field", fieldNames ) );        
    }
    
    public static _method of( _field...fields )
    {
        String[] fieldNames = new String[ fields.length ]; 
        for( int i=0; i< fields.length; i++ )
        {
            fieldNames[ i ] = fields[ i ].getName();
        }
        return of( fieldNames );        
    }
}
