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
package varcode.java.draft;

import java.util.ArrayList;
import java.util.List;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.java.load._JavaLoad;
import varcode.java.model._class;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;


/**
 * Reads in a Class / _class model, and (based on macro annotations) prepares
 to draft a _class model through macro expansion
 
 Builds a new {@link _class} by a single {@code _classOrignator} and subsequent
 * {@code expansion}s that 
 * 
 * @see JavaSource
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _draftClass
    extends _draftType
{    
    /**
     * Reads in the _class Model from the class, and "prepares" it for 
     * Macro expansion
     * @param draft the "draft" class containing @annotation markup for macro expansion
     * @return the _draftClass 
     */
    public static _draftClass of( Class draft )
    {
        return new _draftClass( _JavaLoad._classFrom( draft ) );
    }
    
    /**
     * Build a MacroClass based on the _prototype _class
     * @param _prototype
     * @return the macroClass
     */
    public static _draftClass of( _class _prototype )
    {
        return new _draftClass( _prototype );
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
    public final List<DraftAction> typeExpansion = 
        new ArrayList<DraftAction>();
    
    /** the prototype _class */
    public final _class _prototype;
    
    /** method for originating the "tailored" _class */
    public final _classAction _originator;
    
    /** */
    public interface _classAction
    {
        public _class draftClass( Context context );
    }
    
    //nests    
    public _draftClass( _class _c )
    {
        this._prototype = new _class( _c );
        this._originator = prepareClassAction( _prototype );
        
        prepareTypePackage( this.typeExpansion, this._prototype.getPackageName(), 
            _c.getAnnotations().getOne( packageName.class ) );
        
        prepareTypeImports( this.typeExpansion, this._prototype.getImports(), 
            _c.getAnnotation( imports.class )  );
        
        //annotations
        prepareTypeAnnotations( this.typeExpansion, this._prototype.getAnnotations(), 
            _c.getAnnotation( annotations.class ) );
        
        
            
        prepareTypeFields( this.typeExpansion, 
            _c.getAnnotations().getOne( fields.class ) );
        
        prepareStaticBlock( this.typeExpansion, 
            _c.getAnnotations().getOne( staticBlock.class ),
            _c.getStaticBlock() );
        
        _draftConstructors.prepareConstructors( this.typeExpansion, _c.getConstructors() );
        
        //TODO fieldANNOTATIONS, methodANNOTATIONS, fieldJDOC methodJDOC
        //go through the individual member fields and methods and process them
        _draftFields.prepareFields( this.typeExpansion, this._prototype.getFields() );
        _draftMethods.prepareMethods( this.typeExpansion, this._prototype.getMethods() ); 
    }

    
    public static _classAction prepareClassAction( _class _c )
    {
        if( _c.getAnnotation( sig.class ) != null )
        {
            String sig = _c.getAnnotation( sig.class ).getLoneAttributeString();
            if( sig != null) 
            {   
                _c.getAnnotations().remove( sig.class );
                System.out.println( _c.getAnnotations() );
                return new ExpandClassSignature( sig );
            }
            else
            {
                return
                    new CopyClassSignature( _c.getSignature().author() );
            }            
        }
        return new CopyClassSignature( _c.getSignature().author() );        
    }
    
    
    public _class draft( Object...keyValuePairs )
    {
        //System.out.println( keyValuePairs.length );
        return draft( VarContext.of( keyValuePairs ) );
    }
    
    public _class draft( Context context )
    {
        //create the tailored target class
        _class _tailored = this._originator.draftClass( context );
        
        for( int i = 0; i < this.typeExpansion.size(); i++ )
        {
            this.typeExpansion.get( i ).draftTo( _tailored, context );
        }
        return _tailored;
    }
    
    
    public static class CopyClassSignature
        implements _classAction
    {        
        //_class _prototype;
        private final String signature;
        
        public CopyClassSignature( String signature )
        {
            this.signature = signature;
        }
        
        @Override
        public _class draftClass( Context context )
        {
            return _class.of( signature );
        }
    }
    
    public static class ExpandClassSignature
        implements _classAction
    {
        public Template signature;
        
        public ExpandClassSignature( String form )
        {
            //System.out.println( form );
            this.signature = BindML.compile( form );
        }
        
        public _class initClass( Object... keyValuePairs )
        {
            return draftClass( VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public _class draftClass( Context context )
        {
            return _class.of( Author.toString( signature, context ) );
        }
    }  
}
