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
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._extends;
import varcode.java.model._imports;
import varcode.java.model._methods._method;
import varcode.java.model._parameters;

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
    public _parameters _params = new _parameters();
    public _code code = new _code();
    public _extends extendsFrom;
    
    public static CodeSpace of ( String...linesOfCode )
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
    
    public Object eval( Object...params )
    {
        Workspace workspace = new Workspace();
        //add all the AdHoc Entities
        _class _spaceClass = buildSpaceClass();
        workspace.add( this.models );
        workspace.add( _spaceClass );
        
        try
        {
            AdHocClassLoader adHocCL = AdHoc.compile(workspace );
            Class spaceClass = adHocCL.findClass( _spaceClass );
            Object instance = Java.instance( spaceClass );
        
            return Java.call( instance, 
                _spaceClass.getMethods().getAt( 0 ).getName(), 
                params );
        }
        catch( JavacException je )
        {
            throw new AdHocException( 
                "unable to compile CodeSpace "+ System.lineSeparator() +
                _spaceClass, je );
        }        
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
            modelImports.add( this.models.get( i ).getAllClassNames() );  
            //automatically add all imports from all models classes
            modelImports.add( this.models.get( i ).getImports() );
        }
        return _class.of("CodeSpace" )
            .packageName( "adhoc.codespace" )
            .imports( this.imports ).imports( modelImports )
            .method( _method.of( 
                "public static void eval( )", this.code )
                .setParameters( this._params ) );
    }
    
}
