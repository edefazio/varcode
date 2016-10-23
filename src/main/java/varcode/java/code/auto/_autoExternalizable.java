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
package varcode.java.code.auto;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import varcode.java.code._class;
import varcode.java.code._fields;
import varcode.java.code._fields._field;
import varcode.java.code._methods;
import varcode.java.code._methods._method;

/**
 * Generates the appropriate Externalizable methods (readExternal, writeExternal)
 * for a class
 * 
 * issue with final fields : (either you manually set accessible or use Unsafe)
 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6379948
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoExternalizable
{
    public static _class of( _class c )
    {
        return externalize( c );
    }
    
    /**
     * Builds, creates and returns an externalizable version
     * of _class 
     * 
     * Does everything needed to have an object implement externalizable
     * 
     * @param c
     * @return 
     */
    public static _class externalize( _class c )
    {
        _class extern = _class.cloneOf( c );

        extern.getSignature().implement( Externalizable.class );
        
        extern.imports( 
            Externalizable.class, ClassNotFoundException.class, 
            IOException.class, ObjectInput.class, ObjectOutput.class );
        
        _fields fields = extern.getFields();
        
        _methods externalizableMethods = 
            externalizableMethodsForFields( fields );
        
        extern.methods( externalizableMethods );
        
        return extern;
    }
    
    public static _methods externalizableMethodsForFields( _fields fields )
    {
        _methods methods = new _methods();
        
        _method writeExternal = _method.of(
            "public void writeExternal( ObjectOutput out ) throws IOException" );
        
        writeExternal.annotate("@Override");
        
        methods.addMethod( writeExternal );
        
        _method readExternal = _method.of(
        "public void readExternal( ObjectInput in ) "
      + "throws IOException, ClassNotFoundException" );
        
        readExternal.annotate("@Override");
        
        methods.addMethod( readExternal );
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
                writeExternal.addToBody( "out.writeInt(" + field.getName() + ");" );
                readExternal.addToBody( "this." + field.getName() + " = in.readInt( );" );
            }
            else if( type.equals( "boolean" ) || ( type.equals( "java.lang.boolean" ) ) )
            {
                writeExternal.addToBody( "out.writeBoolean(" + field.getName() + ");" );
                readExternal.addToBody( "this." + field.getName() + " = in.readBoolean( );" );
            }
            else if( type.equals( "byte" ) || ( type.equals( "java.lang.byte" ) ) )
            {
                writeExternal.addToBody( "out.writeByte(" + field.getName() + ");" );
                readExternal.addToBody( "this." + field.getName() + " = in.readByte( );" );
            }
            else if( type.equals( "short" ) || ( type.equals( "java.lang.short" ) ) )
            {
                writeExternal.addToBody( "out.writeShort(" + field.getName() + ");" );
                readExternal.addToBody( "this." + field.getName() + " = in.readShort( );" );      
            }
            else if( type.equals( "char" ) || ( type.equals( "java.lang.char" ) ) )
            {
                writeExternal.addToBody( "out.writeChar(" + field.getName() + ");" );
                readExternal.addToBody( "this." + field.getName() + " = in.readChar( );" );       
            }
            else if( type.equalsIgnoreCase( "long" ) || ( type.equalsIgnoreCase( "java.lang.long" ) ) )
            {
                writeExternal.addToBody( "out.writeLong(" + field.getName() + ");" ); 
                readExternal.addToBody( "this." + field.getName() + " = in.readLong( );" );
            }
            else if( type.equals( "float" ) || ( type.equals( "java.lang.float" ) ) )
            {
                writeExternal.addToBody( "out.writeFloat(" + field.getName() + ");" );
                readExternal.addToBody( "this." + field.getName() + " = in.readFloat( );" );
            }
            else if( type.equals( "double" ) || ( type.equals( "java.lang.double" ) ) )
            {
                writeExternal.addToBody( "out.writeDouble(" + field.getName() + ");" );       
                readExternal.addToBody( "this." + field.getName() + " = in.readDouble( );" );
            }            
            else
            {
                writeExternal.addToBody( "out.writeObject(" + field.getName() + ");" );                   
                readExternal.addToBody( 
                 "this." + field.getName() + " = (" + field.getType() + ") in.readObject( );" );
            }
        }
        return methods;
    }
}
