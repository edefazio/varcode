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

import varcode.author.Author;
import varcode.markup.Fill;
import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._methods._method;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 * Builds a {@code getXXX()} method
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum _autoGetters
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    
    public static final Template SIGNATURE = BindML.compile(
        "public {+type+} get{+Name+}()" );

    public static final Template BODY = BindML.compile(
        "return this.{+name+};" );

    
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    public static _method of( _fields._field _f )
    {
        return of( _f.getType(), _f.getName() );
    }

    public static _method of( Object type, Object name )
    {
        return _method.of( Fill.of( SIGNATURE, type, name ),
            Fill.of( BODY, name ) );
    }
    
    public static _class to( _class _c )
    {
     //_class addSetters = _class.cloneOf( theClass );
        _fields fields = _c.getFields();
        for( int i = 0; i < fields.count(); i++ )
        {   //dont have getters for static methods
            if( fields.getAt( i ).getModifiers().contains( "static" ) )
            {
                continue;
            }
            _c.method(
                of( fields.getAt( i ).getType(), fields.getAt( i ).getName() ) );
        }
        return _c;
    }
}
