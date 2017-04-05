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
import varcode.java.model._enum;
import varcode.java.model._enum._constants;
import varcode.java.model._enum._constants._constant;
import varcode.java.model._interface;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;
import varcode.java.draft.draftAction;


/**
 * Reads in a Class / _enum model, and (based on macro annotations) prepares
 to draft a _class model through macro expansion
 
 Builds a new {@link _enum} by a single {@code _enumOrignator} and subsequent
 * {@code expansion}s that 
 * 
 * @see JavaSource
 * @author M. Eric DeFazio eric@varcode.io
 */
public class draftEnum
    extends draftType
{    
    /**
     * Reads in the _class Model from the class, and "prepares" it for 
     * Macro expansion
     * @param draft the "draft" class containing @annotation markup for macro expansion
     * @return the _macroClass 
     */
     public static draftEnum of( Class draft )
    {
        return new draftEnum( _JavaLoad._enumFrom( draft ) );
    }
    
    /**
     * Build a MacroClass based on the _prototype _class
     * @param _prototype
     * @return the macroClass
     */
    public static draftEnum of( _enum _prototype )
    {
        return new draftEnum( _prototype );
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
    public final List<draftAction> typeExpansion = 
        new ArrayList<draftAction>();
    
    /** the prototype _class */
    public final _enum _prototype;
    
    /** method for originating the "tailored" _class */
    public final _enumAction _originator;
    
    /** */
    public interface _enumAction
    {
        public _enum initEnum( Context context );
        
        public _enum initEnum( Object... keyValuePairs );
    }
    
    //nests    
    public draftEnum( _enum _c )
    {
        this._prototype = new _enum( _c );
        this._originator = prepareEnumOriginator( _prototype );
        
        prepareTypePackage( this.typeExpansion, _c.getPackageName(), 
            _c.getAnnotations().getOne( packageName.class ) );
        
        prepareTypeImports( this.typeExpansion, _c.getImports(), 
            _c.getAnnotation( imports.class )  );
        
        //annotations
        prepareTypeAnnotations( this.typeExpansion, _c.getAnnotations(), 
            _c.getAnnotation( annotations.class ) );
        
        //constants
        _constants _consts = _prototype.getConstants();
        for( int i = 0; i < _consts.count(); i++ )
        {
            _constant _const = _consts.getAt( i );
            if( !( _const.getAnnotation( remove.class ) != null ) )
            {   //for now just COPY over the constant
                this.typeExpansion.add( new CopyEnumConst( _const ) );
            }
        }
        prepareTypeFields( this.typeExpansion, 
            _c.getAnnotations().getOne( fields.class ) );
        
        prepareStaticBlock( this.typeExpansion, 
            _c.getAnnotations().getOne( staticBlock.class ),
            _c.getStaticBlock() );
        
        //TODO fieldANNOTATIONS, methodANNOTATIONS, fieldJDOC methodJDOC
        //go through the individual member fields and methods and process them
        draftFields.prepareFields( this.typeExpansion, _c.getFields() );
        draftMethods.prepareMethods( this.typeExpansion, _c.getMethods() ); 
    }

    
    public static _enumAction prepareEnumOriginator( _enum _c )
    {
        if( _c.getAnnotation( sig.class ) != null )
        {
            String sig = _c.getAnnotation( sig.class ).getAttrString();
            if( sig != null) 
            {
                _c.getAnnotations().remove( sig.class );
                return new ExpandEnumSignature( sig );
            }
            else
            {
                return
                    new CopyEnumSignature( _c.getSignature().author() );
            }            
        }
        return new CopyEnumSignature( _c.getSignature().author() );        
    }
    
    public _enum draft( Object...keyValuePairs )
    {
        return draft( VarContext.of( keyValuePairs ) );
    }
    
    public _enum draft( Context context )
    {
        //create the tailored target class
        _enum _tailored = this._originator.initEnum( context );
        
        //System.out.println( _tailored );
        for( int i = 0; i < this.typeExpansion.size(); i++ )
        {
            this.typeExpansion.get( i ).draftTo( _tailored, context );
        }
        return _tailored;
    }
    
    /**
     * This code is specific to Enums, but we extend _typeDraft just to make
 it work more easily with the existing 
     */
    public static class CopyEnumConst
        implements draftAction
    {
        public _enum._constants._constant enumConst;
        
        public CopyEnumConst( _enum._constants._constant _const )
        {
            this.enumConst = new _enum._constants._constant( _const );
        }

        @Override
        public void draftTo( _class _c, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void draftTo( _class _c, Context context )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void draftTo( _enum _e, Object... keyValuePairs )
        {
            _e.constant( enumConst );
        }

        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.constant( enumConst );
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in interface." ); 
        }        
    }
    
    /**
     * This code is specific to Enums, but we extend _typeDraft just to make
     * it work more easily with the existing 
     */
    public static class ExpandEnumConst
        implements draftAction
    {
        public _enum._constants._constant enumConst;
        
        
        public ExpandEnumConst( _enum._constants._constant _const )
        {
            this.enumConst = new _enum._constants._constant( _const );
        }

        @Override
        public void draftTo( _class _c, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void draftTo( _class _c, Context context )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void draftTo( _enum _e, Object... keyValuePairs )
        {
            _e.constant( enumConst );
        }

        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.constant( enumConst );
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in interface." ); 
        }        
    }
    
    public static class CopyEnumSignature
        implements _enumAction
    {        
        //_class _prototype;
        private final String signature;
        
        public CopyEnumSignature( String signature )
        {
            this.signature = signature;
        }
        
        @Override
        public _enum initEnum( Context context )
        {
            return _enum.of( signature );
        }
        
        @Override
        public _enum initEnum( Object...keyValuePairs )
        {
            return _enum.of( signature );
        }
    }
    
    public static class ExpandEnumSignature
        implements _enumAction
    {
        public Template signature;
        
        public ExpandEnumSignature( String form )
        {
            //System.out.println( form );
            this.signature = BindML.compile( form );
        }
        
        
        @Override
        public _enum initEnum( Object... keyValuePairs )
        {
            return initEnum( VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public _enum initEnum( Context context )
        {
            return _enum.of( Author.toString( signature, context ) );
        }
    }  
}
