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
import varcode.java.model._enum;
import varcode.java.model._enum._constants;
import varcode.java.model._enum._constants._constant;
import varcode.java.model._interface;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;


/**
 * Reads in a Class / _enum model, and (based on macro annotations) prepares
 * to expand a _class model through macro expansion
 * 
 * Builds a new {@link _enum} by a single {@code _enumOrignator} and subsequent
 * {@code expansion}s that 
 * 
 * @see JavaSource
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _macroEnum
    extends _macroType
{    
    /**
     * Reads in the _class Model from the class, and "prepares" it for 
     * Macro expansion
     * @param draft the "draft" class containing @annotation markup for macro expansion
     * @return the _macroClass 
     */
    public static _macroEnum of( Class draft )
    {
        return new _macroEnum( _JavaLoad._enumFrom( draft ) );
    }
    
    /**
     * Build a MacroClass based on the _prototype _class
     * @param _prototype
     * @return the macroClass
     */
    public static _macroEnum of( _enum _prototype )
    {
        return new _macroEnum( _prototype );
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
    public final _enum _prototype;
    
    /** method for originating the "tailored" _class */
    public final _enumOriginator _originator;
    
    /** */
    public interface _enumOriginator
    {
        public _enum initEnum( Context context );
        
        public _enum initEnum( Object... keyValuePairs );
    }
    
    //nests    
    public _macroEnum( _enum _c )
    {
        this._prototype = new _enum( _c );
        this._originator = prepareEnumOriginator( _prototype );
        
        prepareTypePackage( this.typeExpansion, _c.getPackageName(), 
            _c.getAnnotations().getOne( _macro.packageName.class ) );
        
        prepareTypeImports( this.typeExpansion, _c.getImports(), 
            _c.getAnnotation( _macro.imports.class )  );
        
        //annotations
        prepareTypeAnnotations( this.typeExpansion, _c.getAnnotations(), 
            _c.getAnnotation( _macro.annotations.class ) );
        
        //constants
        _constants _consts = _prototype.getConstants();
        for( int i = 0; i < _consts.count(); i++ )
        {
            _constant _const = _consts.getAt( i );
            if( !( _const.getAnnotation( _macro.remove.class ) != null ) )
            {   //for now just COPY over the constant
                this.typeExpansion.add( new EnumConstCopy( _const ) );
            }
        }
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

    
    public static _enumOriginator prepareEnumOriginator( _enum _c )
    {
        if( _c.getAnnotation( _macro.sig.class ) != null )
        {
            String sig = _c.getAnnotation( sig.class ).getLoneAttributeString();
            if( sig != null) 
            {
                return new ExpandEnumOriginator( sig );
            }
            else
            {
                return
                    new CopyEnumOriginator( _c.getSignature().author() );
            }            
        }
        return new CopyEnumOriginator( _c.getSignature().author() );        
    }
    
    public _enum expand( Object...keyValuePairs )
    {
        return expand( VarContext.of( keyValuePairs ) );
    }
    
    public _enum expand( Context context )
    {
        //create the tailored target class
        _enum _tailored = this._originator.initEnum( context );
        
        //System.out.println( _tailored );
        for( int i = 0; i < this.typeExpansion.size(); i++ )
        {
            this.typeExpansion.get( i ).expandTo( _tailored, context );
        }
        return _tailored;
    }
    
    /**
     * This code is specific to Enums, but we extend _typeExpansion just to make
     * it work more easily with the existing 
     */
    public static class EnumConstCopy
        implements _typeExpansion
    {
        public _enum._constants._constant enumConst;
        
        public EnumConstCopy( _enum._constants._constant _const )
        {
            this.enumConst = new _enum._constants._constant( _const );
        }

        @Override
        public void expandTo( _class _c, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void expandTo( _class _c, Context context )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void expandTo( _enum _e, Object... keyValuePairs )
        {
            _e.constant( enumConst );
        }

        @Override
        public void expandTo( _enum _e, Context context )
        {
            _e.constant( enumConst );
        }

        @Override
        public void expandTo( _interface _i, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in interface." ); 
        }        
    }
    
    /**
     * This code is specific to Enums, but we extend _typeExpansion just to make
     * it work more easily with the existing 
     */
    public static class EnumConstExpand
        implements _typeExpansion
    {
        public _enum._constants._constant enumConst;
        
        public EnumConstExpand( _enum._constants._constant _const )
        {
            this.enumConst = new _enum._constants._constant( _const );
        }

        @Override
        public void expandTo( _class _c, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void expandTo( _class _c, Context context )
        {
            throw new UnsupportedOperationException( "Constants not supported in class." ); 
        }

        @Override
        public void expandTo( _enum _e, Object... keyValuePairs )
        {
            _e.constant( enumConst );
        }

        @Override
        public void expandTo( _enum _e, Context context )
        {
            _e.constant( enumConst );
        }

        @Override
        public void expandTo( _interface _i, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Constants not supported in interface." ); 
        }        
    }
    
    public static class CopyEnumOriginator
        implements _enumOriginator
    {        
        //_class _prototype;
        private final String signature;
        
        public CopyEnumOriginator( String signature )
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
    
    public static class ExpandEnumOriginator
        implements _enumOriginator
    {
        public Template signature;
        
        public ExpandEnumOriginator( String form )
        {
            //System.out.println( form );
            this.signature = BindML.compile( form );
        }
        
        
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
