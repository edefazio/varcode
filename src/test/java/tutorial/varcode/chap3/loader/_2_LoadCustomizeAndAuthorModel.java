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
package tutorial.varcode.chap3.loader;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Model;
import varcode.VarException;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._methods._method;
import varcode.java.model.edit.AddImports;
import varcode.java.model.edit.Expect;
import varcode.java.model.edit.ExpectArgument;
import varcode.java.model.edit.ExpectInstanceField;
import varcode.java.model.edit.ExpectStaticField;
import varcode.java.model.edit.MethodEditor;
import varcode.java.model.load.JavaModelLoader;

/**
 *
 * @author eric
 */
public class _2_LoadCustomizeAndAuthorModel
    extends TestCase
{
    /** 
     * We want to load the _class model for this class
     * then specialize it at runtime
     * 
     */
    public static class PrefixCreateId
    {
        public final String prefix;
        
        public PrefixCreateId( String prefix )
        {
            this.prefix = prefix;
        }
        
        public String createId()
        {
            return this.prefix + UUID.randomUUID().toString();
        }
    }
    
    public static class DateCreateId
    {
        private String prefix;
        
        public String createId()
        {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd:hh:mm:ss" );
            return prefix + sdf.format( new Date() ) + UUID.randomUUID().toString();            
        }
    }
    
    public static class CodeContext
    {
        public Class clazz;
        public Method method;        
        public Map<String,Object> parameters;
        public Map<String,Object> instanceValues;
    }
    
    public interface _InstanceMethodBeforeAdvice
    {
        void before( Method method, Object[] args, Object target );
    }
    
    public interface _StaticMethodBeforeAdvice
    {
        void beforeStatic( Method method, Object[] args, Class target );
    }
    
    public interface _InstanceMethodAfterAdvice
    {
        void after( Method method, Object[] args, Object target );
    }
    
    public interface _StaticMethodAfterAdvice
    {
        void afterStatic( Method method, Object[] args, Class target );
    }
    
    public static abstract class _InstanceMethodAdvice
        implements _InstanceMethodBeforeAdvice, _InstanceMethodAfterAdvice
    {
        @Override
        public void before( Method method, Object[] args, Object target )
        {
            
        }
        
        @Override
        public void after( Method method, Object[] args, Object target )
        {
            
        }

        public static Expect expect()
        {
            return new Expect();
        }
        
        private final Expect expectModel;
        
        
        public _InstanceMethodAdvice( )
        {
            this( new Expect() );
        }
        
        public _InstanceMethodAdvice( Expect expectModel )
        {
            this.expectModel = expectModel;
        }
        
        
    }
    
    public static class LogMethodAdvice
        extends _InstanceMethodAdvice
    {        
        //expect the target class to have a static Logger named "LOG"
        static Logger LOG = LoggerFactory.getLogger( LogMethodAdvice.class );
        
        
        public LogMethodAdvice( )
        {
            super( expect()
                .staticField( LOG.getClass().getTypeName(), "LOG" )
                .instanceField( String.class.getTypeName(), "x" )
                .argument( "Map<String,Integer>", "blah") );    
                //.addMethodByName( "printArgs" )
                //.addImports( Logger.class, LoggerFactory.class ) );
        }
     
        public static String printArgs( Object... args )
        {
            return "(" + Arrays.asList( args ) + ")";
        }
        
        public void before( Method method, Object[] args, Object target )
        {
            LOG.debug( "calling " + method + " with " + printArgs( args ) );
        }
        
        public void after( Method method, Object[] args, Object target )
        {
            LOG.debug( "completed " + method + " with ( $args$ )" );                        
        }
    }
    
    public void testLoadClassModelAndSpecialize()
    {
        //load a model of the above class
        _class _classModel = 
            JavaModelLoader.ClassModel.fromClass( PrefixCreateId.class );
        
        //get rid of the "static" modifier (make this a top-level class)
        _classModel.getSignature().setModifiers( "public" );
        
        //create an id that has a Date Prefix
        //<Prefix>_<Date>_<UUID>
        
        _classModel.imports( Date.class, SimpleDateFormat.class );
        
        //get the createId method (there should be only 1 at index 0)
        _method m = _classModel.getMethodsByName( "createId" ).get( 0 );
        
        /*
        "SimpleDateFormat sdf = new SimpleDateFormat( \"YYYY-MM-DD-hh:mm:ss\" );
        "String date = DateFormat"
        "return this.prefix + date + UUID.randomUUID().toString();"
        */        
        //extract the original body from the code
        _code originalBody = m.getBody();
        
    }
    /*
    Object instance = //author, compile, load & instantiate a new AdHocClass
            _classModel.instance( "pre" );//pass in "pre" to constructor
                                    
        String id = //invoke "createId" method on the "AdHoc" instance
            (String)Java.invoke( instance, "createId" );
        
        assertTrue( id.startsWith( "pre" ) );
        System.out.println( id ); 
    */
}
