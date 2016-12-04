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
package varcode.java.metalang.macro;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import varcode.java.JavaCase;
import varcode.java.metalang._code;
import varcode.java.metalang._fields;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._implements;
import varcode.java.metalang._imports;

/**
 * Creates a Delegate Class that implements all of the interfaces
 * of the target class, and delegates all methods to it.
 * 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoDelegate
{    
    private Class[] implementedInterfaces;
    
    private _imports imports;
    private _implements implementers;
    private _fields fields;
    
    private Map<Method, _delegateCode> methodToDelegateCode = 
        new HashMap<Method, _delegateCode>();
    
    public _autoDelegate( Class clazz )
    {
        Class<?>[] interfaces = clazz.getInterfaces();
        this.imports = new _imports();
        this.imports.addImports( (Object[]) interfaces );
        this.implementers = _implements.of( interfaces );
        this.fields = new _fields();
    }
    
    public _autoDelegate imports( Object...imports )
    {
        this.imports.addImports( imports );
        return this;
    }
    
    public _autoDelegate field( String fieldDef )
    {
        this.fields.addFields( _field.of( fieldDef ) );
        return this;
    }
    
    public _autoDelegate before( String methodName, Object...code )
    {
        
        return this;
    }
    
    public _autoDelegate after( String methodName, Object...code )
    {
        
        return this;
    }
    
    
    public static void main( String[] args )
    {
        _autoDelegate ad = new _autoDelegate( MySimpleClass.class );
        //add some imports
        ad.imports( org.slf4j.Logger.class, org.slf4j.LoggerFactory.class );
        //create a static logger instance
        ad.field( "public static final Log DLOG = LoggerFactory.getLog(\"DLOG\");" );
        
        ad.before( "callThis", "System.out.println(\"BEFORE\");" );
        ad.after( "callThis", "System.out.println(\"AFTER\");" );
        
        //ad.trycatch( "callThis", IOException.class, "System.out.println(\"AFTER\");");
        //ad.replaceDelegate( );
    }
    
    public static class MySimpleClass
    {
        public void callThis()
        {
            System.out.println("Called it");
        }
    }
    
    public static class _delegateCode
    {
        public _code code;
        
        public _delegateCode( Method method )
        {
            Class<?>ret =  method.getReturnType();
            String name = method.getName();
            Class<?>[] paramTypes = method.getParameterTypes();
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            System.out.println ( method.toGenericString() );
        }
    }
    
}
