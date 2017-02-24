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
package varcode.java.macro;

import java.util.ArrayList;
import java.util.List;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.java.load._JavaLoad;
import varcode.java.macro._macro._typeExpansion;
import varcode.java.model._class;
import varcode.java.macro._macro.sig;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;


/**
 * Reads in a Class / _class model, and (based on macro annotations) prepares
 * to expand a _class model through macro expansion
 * 
 * Builds a new {@link _class} by a single {@code _classOrignator} and subsequent
 * {@code expansion}s that 
 * 
 * @see JavaSource
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _macroClass
    extends _macroType
{    
    /**
     * Reads in the _class Model from the class, and "prepares" it for 
     * Macro expansion
     * @param draft the "draft" class containing @annotation markup for macro expansion
     * @return the _macroClass 
     */
    public static _macroClass of( Class draft )
    {
        return new _macroClass( _JavaLoad._classFrom( draft ) );
    }
    
    /**
     * Build a MacroClass based on the _prototype _class
     * @param _prototype
     * @return the macroClass
     */
    public static _macroClass of( _class _prototype )
    {
        return new _macroClass( _prototype );
    }
    
    /** 
     * methods for 
     * <UL>
     * <LI>transferring components (fields, methods, etc.) from the 
     * _prototype to the "tailored" _class
     * <LI>"building"/ "tailoring" new components and populating 
     * them in the "tailored" _class
     * </UL>
     */
    public final List<_typeExpansion> typeExpansion = 
        new ArrayList<_typeExpansion>();
    
    /** the prototype _class */
    public final _class _prototype;
    
    /** method for originating the "tailored" _class */
    public final _classOriginator _originator;
    
    /** */
    public interface _classOriginator
    {
        public _class initClass( Context context );
    }
    
    //nests    
    public _macroClass( _class _c )
    {
        this._prototype = new _class( _c );
        this._originator = prepareClassOriginator( _prototype );
        
        prepareTypePackage( this.typeExpansion, _c.getPackageName(), 
            _c.getAnnotations().getOne( _macro.packageName.class ) );
        
        prepareTypeImports( this.typeExpansion, _c.getImports(), 
            _c.getAnnotation( _macro.imports.class )  );
        
        //annotations
        prepareTypeAnnotations( this.typeExpansion, _c.getAnnotations(), 
            _c.getAnnotation( _macro.annotations.class ) );
        
        prepareTypeFields( this.typeExpansion, 
            _c.getAnnotations().getOne( _macro.fields.class ) );
        
        prepareStaticBlock( this.typeExpansion, 
            _c.getAnnotations().getOne( _macro.staticBlock.class ),
            _c.getStaticBlock() );
        
        //TODO fieldANNOTATIONS, methodANNOTATIONS, fieldJDOC methodJDOC
        //go through the individual member fields and methods and process them
        _macroFields.prepareFields( this.typeExpansion, _c.getFields() );
        _macroMethods.prepareMethods( this.typeExpansion, _c.getMethods() ); 
    }

    
    public static _classOriginator prepareClassOriginator( _class _c )
    {
        if( _c.getAnnotation( _macro.sig.class ) != null )
        {
            String sig = _c.getAnnotation( sig.class ).getLoneAttributeString();
            if( sig != null) 
            {
                return new ExpandClassOriginator( sig );
            }
            else
            {
                return
                    new CopyClassOriginator( _c.getSignature().author() );
            }            
        }
        return new CopyClassOriginator( _c.getSignature().author() );        
    }
    
    
    public _class expand( Object...keyValuePairs )
    {
        //System.out.println( keyValuePairs.length );
        return expand( VarContext.of( keyValuePairs ) );
    }
    
    public _class expand( Context context )
    {
        //create the tailored target class
        _class _tailored = this._originator.initClass( context );
        
        for( int i = 0; i < this.typeExpansion.size(); i++ )
        {
            this.typeExpansion.get( i ).expandTo( _tailored, context );
        }
        return _tailored;
    }
    
    
    public static class CopyClassOriginator
        implements _classOriginator
    {        
        //_class _prototype;
        private final String signature;
        
        public CopyClassOriginator( String signature )
        {
            this.signature = signature;
        }
        
        @Override
        public _class initClass( Context context )
        {
            return _class.of( signature );
        }
    }
    
    public static class ExpandClassOriginator
        implements _classOriginator
    {
        public Template signature;
        
        public ExpandClassOriginator( String form )
        {
            System.out.println( form );
            this.signature = BindML.compile( form );
        }
        
        public _class initClass( Object... keyValuePairs )
        {
            return initClass( VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public _class initClass( Context context )
        {
            return _class.of( Author.toString( signature, context ) );
        }
    }  
}
