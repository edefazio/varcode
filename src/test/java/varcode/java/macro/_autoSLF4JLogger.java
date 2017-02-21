/*
 * Copyright 2016 Eric.
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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._Java._model;

/**
 * looks at the contents of a _class MetaLang Model and either
 * returns an existing Logger field (SLF4J)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoSLF4JLogger 
{
    public static _field getOrCreate( _model _c )
    {
        return _autoSLF4JLogger.getOrCreate( _c, "LOG" );        
    }
    
    /**
     * given a 
     * @param _c
     * @return 
     */
    private static List<_field> getLoggerFields( _model _c )
    {
        List<_field> fields = new ArrayList<_field>();
        _fields _fs = _c.getFields();
        
        for( int i = 0; i < _fs.count(); i++ )
        {
            _field _f = _fs.getAt( i );
            if( _f.getType().equals( "Logger" ) || 
                _f.getType().equals( Logger.class.getCanonicalName() ) )
            {
                fields.add( _f );
            }
        }
        return fields;
    }
    
    /**
     * 
     * @param _c the _class MetaLang model (NOTE: It may be mutated during method)
     * @param loggerFieldName the name of the LOGGER field if one is to be created
     * @return the _field that was found or created on the _class
     */
    public static _field getOrCreate( 
        _model _c, 
        String loggerFieldName )
    {        
        List<_field>loggerFields = getLoggerFields( _c ); //( Logger.class );
        if( loggerFields.isEmpty() )
        {   //need to create a Logger field on the class and return the field
            _c.getImports().add( Logger.class, LoggerFactory.class );
            _fields._field _logger = _fields._field.of( 
                "private static final Logger " + loggerFieldName 
                + " = LoggerFactory.getLogger( " + _c.getName() + ".class );" );
            _c.getFields().add( _logger );
            return _logger;
        }
        if( loggerFields.size() == 1 )
        {   //there is already a LOGGER return it
            return loggerFields.get( 0 );
        }
        //more than one logger field, try and get the field named LOG
        for( int i = 0; i < loggerFields.size(); i++ )
        {
            _fields._field _f = loggerFields.get( i );
            if( _f.getName().equalsIgnoreCase( loggerFieldName ) )
            {
                return _f;
            }
        }
        //I give up... just return the first Logger
        return loggerFields.get( 0 );
    }
}
