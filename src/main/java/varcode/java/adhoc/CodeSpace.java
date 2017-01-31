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
package varcode.java.adhoc;

import java.util.ArrayList;
import java.util.List;
import varcode.java.Java;
import varcode.java.model._Java;
import varcode.java.model._Java.FileModel;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._methods._method;

/**
 * Convenient way to "run" a block of Java code with using AdHoc classes.
 * 
 * When we create multiple AdHoc components, we often want to "use" or
 * "test" these abstractions and thier interactions with one another 
 * in some arbitrary code.
 * 
 * Alternatively, each invocation to
 * 
 * @author M. Eric DeFazio
 */
public class CodeSpace
{
    public List<_Java.FileModel> models = new ArrayList<_Java.FileModel>();
    public _imports imports = new _imports(); 
    public _fields fields = new _fields();    
    public _code code = new _code();
    public Class extendsFrom;
    
    public static CodeSpace of( String...linesOfCode )
    {
        return new CodeSpace( new ArrayList<_Java.FileModel>(), linesOfCode );
    }
   
    public static CodeSpace of( _Java.FileModel model, String...codeLines )
    {
        List< _Java.FileModel> files = new ArrayList< _Java.FileModel >(); 
        files.add( model );
        return new CodeSpace( files, codeLines );
    }
 
    public CodeSpace( List<_Java.FileModel> models, String...linesOfCode )
    {
        this.models = models;
        this.code.addTailCode( (Object[])linesOfCode );
    }
    
    public CodeSpace init( String...fieldDeclarations )
    {        
        _fields _fs = _fields.of(  fieldDeclarations ).addModifiers( "public" );        
        this.fields.add( _fs );        
        return this;        
    }
    
    public CodeSpace extend( Class baseClass )
    {
        this.extendsFrom = baseClass;
        return this;
    }
    
    public CodeSpace imports( Object...imports )
    {
        this.imports.add( imports );
        return this;
    }
    
    public CodeSpace importStatic( Object...importStatic )
    {
        this.imports.addStaticImports( importStatic );
        return this;
    }
    
    public CodeSpace code( String...linesOfCode )
    {
        code.addTailCode( (Object[])linesOfCode );
        return this;
    }
    
    public Space space()
    {
        return new Space( 
            this.models, this.buildSpaceClass() ) ; //, this.stateKeyValues );
    }

    
    public static Object bake( String...code )
    {
        return bake( new Workspace(), new _imports(), code );
    }
    
    
    /**
     * This is code that returns the evaluation
     * @param code
     * @return 
     */
    public static Object bake( 
        Workspace ws, _imports imports, String... code )
    {
        Workspace combined = new Workspace();
        combined.add( ws );
        
        //create a new class
        _class _c = _class.of(
            "package adhoc.eval", "public class Eval" )
            .imports( imports )
            .imports( (Object[])ws.getAllClassNames().toArray( new String[ 0 ] ) )
            .field( _field.of( "public static Object e;" ).init( code ) );
        
        combined.add( _c );
        //compile
        AdHocClassLoader adHocCL = AdHoc.compile( combined );        
        //get the class
        Class clazz = adHocCL.findClass( _c );
        
        //get The value e on the class
        return Java.get(  clazz, "e" );
    }
    
    public Object eval(  )
    {
        return space().eval();
    }
    
    /**
     * Builds and returns the Space Class which has access to 
     * all (directly imported) classes, AND automatically imports
     * all classes
     *  
     * @return the 
     */
    public _class buildSpaceClass()
    {
        //add all the models in the workspace as imports
        _imports modelImports = new _imports();
        for( int i = 0; i < this.models.size(); i++ )
        {   
            //add all models as imports 
            modelImports.add( this.models.get( i ).getAllClassNames() );
            
            //automatically add all imports from all models classes
            modelImports.add( this.models.get( i ).getImports() );
        }
        _class _c = _class.of( "CodeSpace" )
            .packageName( "adhoc.codespace" )
            .extend( this.extendsFrom )
            .imports( this.imports ).imports( modelImports ).imports( this.extendsFrom )
            .fields( this.fields )
            .method( _method.of( 
                "public void eval( )", this.code ) );
        return _c;
    }
 
    /**
     * This is a fully realized Instance WITH the source code that has set
     */
    public static class Space
    {
        /** Workspace containing the source */
        public final Workspace workspace;
        
        /** the loaded Classes */
        public final AdHocClassLoader adHocClassLoader; 
        
        // the space representing the space
        public final Object instance;
        
        // the Source of the class
        public final _class _class;
        
        public Space( 
            List<FileModel> models, _class _c ) //, List<KeyValue> stateKeyValues )
        {
            this.workspace = Workspace.of( models.toArray( new FileModel[ 0 ] ) );
            this.workspace.add( _c );
            this._class = _c;
            try
            {
                this.adHocClassLoader = AdHoc.compile( workspace );
                Class spaceClass = this.adHocClassLoader.findClass( _class );                
                this.instance = Java.instance( spaceClass );
            }
            catch( JavacException je )
            {
                throw new AdHocException( 
                    "unable to compile CodeSpace "+ System.lineSeparator() +
                    _class, je );
            }        
        }        
        
        public Space eval()
        {
            Java.call( instance, 
                _class.getMethods().getAt( 0 ).getName() );
            
            return this;
        }
        
        /**
         *evaluates the code count number of times 
         * @param count the count of iterations
         * @return the modified Space
         */
        public Space iterate( int count )
        {
            for( int i = 0; i < count; i++ )
            {
                eval();
            }
            return this;
        }
        
        public Object get( String param )
        {
            return Java.get( instance, param );
        }
        
        public Space set( String param, Object value )
        {
            Java.set( instance, param, value );
            return this;
        }
    }
    
    public static class KeyValue
    {
        public String key;
        public Object value;
        
        public KeyValue( String key, Object value )
        {
            this.key = key;
            this.value = value;
        }
    }
}
