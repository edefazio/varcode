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
package varcode.java.macro;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods;
import varcode.java.model._methods._method;

/**
 * Generates the appropriate Externalizable methods 
 * (readExternal, writeExternal) for a class
 * 
 * issue with final fields : (either you manually set accessible or use Unsafe)
 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6379948
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum _autoExternalizable
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    
    /**
     * Builds, creates and returns an externalizable version
 to _class 
 
 Does everything needed to have an object implement externalizable
     * 
     * @param c
     * @return 
     */
    public static _class to( _class _extern )
    {    
        _extern.getSignature().implement( Externalizable.class );
        
        _extern.imports( 
            Externalizable.class, ClassNotFoundException.class, 
            IOException.class, ObjectInput.class, ObjectOutput.class );
        
        _fields fields = _extern.getFields();
        
        _methods externalizableMethods = 
            methods( fields );
        
        _extern.methods( externalizableMethods );
        
        return _extern;
    }
    
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    /**
     * Builds and returns the readExternal() writeExternal()
     * 
     * methods based on the fields provided
     * 
     * @param fields the fields to the class
     * @return the readExternal, writeExternal() methods
     */
    public static _methods methods( _fields fields )
    {
        _methods methods = new _methods();
        
        _method writeExternal = _method.of(
            "@Override",
            "public void writeExternal( ObjectOutput out ) throws java.io.IOException" );
        
        methods.add( writeExternal );
        
        _method readExternal = _method.of(
            "@Override",
            "public void readExternal( ObjectInput in ) "
          + "throws java.io.IOException, ClassNotFoundException" );
        
        methods.add( readExternal );
        for( int i = 0; i < fields.count(); i++ )
        {
            _field field = fields.getAt( i );

            //transient fields are not serialized
            if( field.getModifiers().contains( "transient" ) )
            {
                continue;
            }
            String type = field.getType();
            
            if( type.equals( "int" ) || type.equals( "java.lang.int" ) )
            {
                writeExternal.add( "out.writeInt(" + field.getName() + ");" );
                readExternal.add( "this." + field.getName() + " = in.readInt( );" );
            }
            else if( type.equals( "boolean" ) || ( type.equals( "java.lang.boolean" ) ) )
            {
                writeExternal.add( "out.writeBoolean(" + field.getName() + ");" );
                readExternal.add( "this." + field.getName() + " = in.readBoolean( );" );
            }
            else if( type.equals( "byte" ) || ( type.equals( "java.lang.byte" ) ) )
            {
                writeExternal.add( "out.writeByte(" + field.getName() + ");" );
                readExternal.add( "this." + field.getName() + " = in.readByte( );" );
            }
            else if( type.equals( "short" ) || ( type.equals( "java.lang.short" ) ) )
            {
                writeExternal.add( "out.writeShort(" + field.getName() + ");" );
                readExternal.add( "this." + field.getName() + " = in.readShort( );" );      
            }
            else if( type.equals( "char" ) || ( type.equals( "java.lang.char" ) ) )
            {
                writeExternal.add( "out.writeChar(" + field.getName() + ");" );
                readExternal.add( "this." + field.getName() + " = in.readChar( );" );       
            }
            else if( type.equalsIgnoreCase( "long" ) || ( type.equalsIgnoreCase( "java.lang.long" ) ) )
            {
                writeExternal.add( "out.writeLong(" + field.getName() + ");" ); 
                readExternal.add( "this." + field.getName() + " = in.readLong( );" );
            }
            else if( type.equals( "float" ) || ( type.equals( "java.lang.float" ) ) )
            {
                writeExternal.add( "out.writeFloat(" + field.getName() + ");" );
                readExternal.add( "this." + field.getName() + " = in.readFloat( );" );
            }
            else if( type.equals( "double" ) || ( type.equals( "java.lang.double" ) ) )
            {
                writeExternal.add( "out.writeDouble(" + field.getName() + ");" );       
                readExternal.add( "this." + field.getName() + " = in.readDouble( );" );
            }            
            else
            {
                writeExternal.add( "out.writeObject(" + field.getName() + ");" );                   
                readExternal.add( 
                 "this." + field.getName() + " = (" + field.getType() + ") in.readObject( );" );
            }
        }
        return methods;
    }
}
