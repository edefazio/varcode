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

import varcode.java.model._class;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.java.model._parameters._parameter;

/**
 * pass in any _class, _interface, or _enum
 * and will create _javadoc entries for all methods: 
 * <UL>
 *  <LI>@atParam
  <LI>@return 
 *  <LI>@throws 
 * </UL>
 * 
 * for example: 
 * _class _c = 
 *     _class.of("io.varcode", "public abstract class MyClass")
 *     .method( "public String myMethod( int a, String name) throws IOException" );
 * 
 * produces:
 * <PRE>
 * package io.varcode;
 * 
 * public abstract class MyClass
 * {
 *     public String myMethod( int a, String name )
 *         throws IOException
 *     {
 *         
 *     }
 * }
 * </PRE>
 * 
 * If 
 * <PRE>
 _c = _autoJavadocMethod.of( _c );
 
 package io.varcode;
 
 public abstract class MyClass
 {
     /**
      * --- Javadoc here  ---
      * @atParam a 
      * @atParam name
      * @return 
      * @throws IOException
      * /
     public String myMethod( int a, String name )
         throws IOException
     {
         
     }
 }
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum _autoMethodJavadocs
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    public static final String PARAM_TAG  = "@param";
    public static final String RETURN_TAG = "@return";
    public static final String THROWS_TAG = "@throws";
    public static final String AUTHOR_TAG = "@author";
    
    /**
     * 
     * @param _c
     * @return 
     */
    public static _class to( _class _c )
    {
        _methods _ms = _c.getMethods();
        to( _ms );
        return _c;
    }
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    public static void to( _method _m )
    {
        if( !_m.getJavadoc().isEmpty() )
        {   //dont mess with existing Javadocs
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append( " - auto generated javadoc - ");
        sb.append( System.lineSeparator() );
        for( int j = 0; j < _m.getParameters().count(); j++ )
        {            
            _parameter _p = _m.getParameters().getAt( j );
            sb.append( "@param " );
            sb.append( _p.getName() );
            sb.append( System.lineSeparator() );
        }
        if( !_m.getReturnType().equals( "void" ) )
        {
            sb.append( "@return " );
            //sb.add( shortName( _m.getReturnType() ) );
            sb.append( System.lineSeparator() );
        }
        for( int j = 0; j < _m.getThrows().count(); j++ )
        {
            String _th = _m.getThrows().getAt( j );
            sb.append( "@throws " );
            sb.append( shortName( _th ) );
                sb.append( System.lineSeparator() );                
            }
            _m.javadoc( sb.toString() );
    }
    
    public static void to( _methods _ms )
    {        
        for( int i = 0; i < _ms.count(); i++ )
        {
            _method _m = _ms.getAt( i );
            to( _m );            
        }        
    }
    
    public static String shortName( String name )
    {
        if( name.contains( "." ) )
        {
            name = name.substring( name.lastIndexOf( "." ) + 1 );
        }
        return name;
    }   
}
