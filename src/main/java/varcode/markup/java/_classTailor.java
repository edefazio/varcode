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
package varcode.markup.java;

import java.util.ArrayList;
import java.util.List;
import varcode.author.Author;
import varcode.context.Context;
import varcode.java.model._Java.Annotated;
import varcode.java.model._ann;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._methods._method;
import varcode.java.model._package;
import varcode.java.model._staticBlock;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;
import varcode.markup.form.Form;
import varcode.markup.forml.ForML;
import varcode.markup.java.JavaSource.sig;

/**
 * Builds a new {@link _class} by a single {@code _classOrignator} and subsequent
 * {@code _classTransfer}s that 
 * 
 * @see JavaSource
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _classTailor
{
    /** the prototype _class */
    public _class _prototype;
    
    /** method for originating the "tailored" _class */
    public _classOriginator _originator;
    
    /** methods for 
     * <UL>
     * <LI>transferring components (fields, methods, etc.) from the 
     * _prototype to the "tailored" _class
     * <LI>"building"/ "tailoring" new components and populating 
     * them in the "tailored" _class
     * </UL>
     */
    public List<_classTransfer> _transfers = 
        new ArrayList<_classTransfer>();
        
    public static _classTailor of( _class prototype )
    {
        return new _classTailor( prototype );
    }
    //nests
    
    public static _ann getOneAnnotation( Annotated ann, Class clazz )
    {
        List<_ann> sig = 
            ann.getAnnotations().getByClass( clazz );
        if( sig.size() > 0 )
        {
            return sig.get( 0 );
        }
        return null;
    }
    
    /**
     * returns the String content value of an Annotation of clazz
     * 
     * NOTE: works only for 
     * @param _c
     * @param clazz
     * @return 
     */
    public static String getAnnotationStringProperty( 
        Annotated _c, Class clazz )
    {
        _ann _a = getOneAnnotation( _c, sig.class );
        if( _a != null )
        {
            String attr = _a.attributes.values.get( 0 );
            return attr.substring( 1, attr.length() - 1 ); 
        }
        return null;
    }
    
    public _classTailor( _class _c )
    {
        String sig = getAnnotationStringProperty( _c, sig.class );
        if( sig != null )
        {
            this._originator = new TailorClassSignature( sig );
        }
        else
        {
            this._originator = 
                new CopyClassSignature( _c.getSignature().author() );
        }
        
        //package
        if( _c.getAnnotations().contains( JavaSource.packageName.class ) )
        {
             this._transfers.add( 
                new TailorPackage( 
                    getAnnotationStringProperty( 
                        _c, JavaSource.packageName.class ) ) ); 
        }
        else
        {
           this._transfers.add( new CopyPackage(_c.getClassPackage() ) ); 
        }
        
        //free fields
        if( _c.getAnnotations().contains( JavaSource.fields.class ) )
        {
             this._transfers.add( 
                new TailorField( 
                    getAnnotationStringProperty( 
                        _c, JavaSource.fields.class ) ) ); 
        }
         
        //imports
        
        
        for( int i = 0; i < _c.getFields().count(); i++ )
        {
            _field _f = _c.getFields().getAt( i );
            System.out.println( _f );
            if( !_f.getAnnotations().contains( JavaSource.remove.class ) )
            {                
                this._transfers.add(
                    new CopyField( _f ) );
            }
        }
        
    }
    
    /**
     * Mutates the _class model by transferring 
     * (either a copy or new "tailored" instance)
     */
    public interface _classTransfer
    {
        public void transfer( _class _tailored, Context context );
    }
    
    public static class CopyStaticBlock
        implements _classTransfer
    {
        public _staticBlock _prototype;
            
        public CopyStaticBlock( _staticBlock _prototype )
        {
            this._prototype = _prototype;
        }

        @Override
        public void transfer( _class _tailored, Context context )
        {
            _tailored.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }        
    }
    
    public static class TailorStaticBlock
        implements _classTransfer
    {
        public Template template;
        
        public TailorStaticBlock( String bodyTemplate )
        {
            this.template = BindML.compile( bodyTemplate );
        }
        
        @Override
        public void transfer( _class _tailored, Context context )
        {
            _tailored.staticBlock( 
                _staticBlock.of( Author.toString( template, context ) ) );
        }        
    }
    
    
    public static class CopyConstructor
        implements _classTransfer
    {
        private final _constructor ctor;
        
        public CopyConstructor( _constructor ctor )
        {
            this.ctor = ctor;
        }

        @Override
        public void transfer( _class _tailored, Context context )
        {
            _tailored.constructor( _constructor.of( ctor ) );
        }        
    }
    
    public static class TailorConstructor
        implements _classTransfer
    {
        public Form signature;
        public Template body;
        public _constructor _prototype;
        
        public TailorConstructor( 
            String signatureForm, String bodyForm, _constructor _prototype )
        {
            this.signature = ForML.compile( signatureForm );
            this.body = BindML.compile( bodyForm );
            this._prototype = _prototype;
        }

        @Override
        public void transfer( _class _tailored, Context context )
        {
            _constructor _c = null;
            if( signature != null )
            {
                _c = _constructor.of( signature.author( context ) );
            }
            else
            {
                _c = _constructor.of( _prototype );
            }            
            
            if( body != null )
            {
                _c.setBody( Author.toString( body, context ) );
            }
            else
            {
                _c.setBody( new _code( _prototype.getBody() ) );
            }
            _tailored.constructor( _c );
        }        
    }
    
    public static class CopyPackage
        implements _classTransfer
    {       
        private String packageName;
        
        public CopyPackage( _package _pkg )
        {
            if( _pkg == null || _pkg.getName() == null )
            {
                this.packageName = null;
            }
            else
            {
                this.packageName = _pkg.getName();
            }
        }
        public CopyPackage( String packageName )
        {
            this.packageName = packageName;
        }
        
        public void transfer( _class _tailored, Context context )
        {
            _tailored.packageName( packageName );
        }
    }
    
    public static class TailorPackage
        implements _classTransfer
    {
        public Form packageNameForm;
        
        public TailorPackage( String packageNameForm )
        {
            this.packageNameForm = ForML.compile(packageNameForm );
        }
        
        public void transfer( _class _tailored, Context context )
        {
            _tailored.packageName( packageNameForm.author( context ) );
        }        
    }
    
    public static class CopyImports
        implements _classTransfer
    {
        private _imports imports;
        
        public CopyImports( _imports imports )
        {
            this.imports = imports;
        }
        
        public void transfer( _class _tailored, Context context )
        {
            _tailored.imports( _imports.of( imports ) );
        }
    }
    
    public static class TailorImports
        implements _classTransfer
    {
        private _imports imports;
        
        private Form[] add;
        
        public TailorImports( 
            _imports _source, String[] remove, String[] addMarkup )
        {
            //create a prototype
            _imports _is = new _imports( _source );
            
            //remove all I need
            List<String> toRemove = new ArrayList<String>();
            for( int i = 0; i < remove.length; i++ )
            {
                toRemove.addAll( _is.match( remove[ i ] ) );
            }
            _is.remove( toRemove );      
            
            this.add = new Form[ addMarkup.length ];
            for( int i = 0; i < addMarkup.length; i++ )
            {
                add[ i ] = ForML.compile(addMarkup[ i ] );
            }
            this.imports = _is;
        }
        
        
        public void transfer( _class _tailored, Context context )
        {   //create a prototype
            _imports _is = new _imports( this.imports );
            
            for(int i=0; i< this.add.length; i++ )
            {
                String[] adds = this.add[ i ].authorSeries( context );
                _is.add( (Object[])adds );
            }  
            _tailored.imports( _is );
        }        
    }
    
    public interface _classOriginator
    {
        public _class initClass( Context context );
    }
    
    public static class CopyClassSignature
        implements _classOriginator
    {        
        //_class _prototype;
        private final String signature;
        
        public CopyClassSignature( String signature )
        {
            this.signature = signature;
        }
        
        @Override
        public _class initClass( Context context )
        {
            return _class.of( signature );
        }
    }
    
    public static class TailorClassSignature
        implements _classOriginator
    {
        public Form signature;
        
        public TailorClassSignature( String form )
        {
            System.out.println( form );
            this.signature = ForML.compile( form );
        }
        
        @Override
        public _class initClass( Context context )
        {
            return _class.of( signature.author(context ) );
        }
    }  
    
    public static class CopyField
        implements _classTransfer
    {
        private final _field _prototype;
        
        public CopyField( _field _prototype )
        {
            this._prototype = _prototype;
        }
        
        public void transfer( _class _tailored, Context context )
        {
            _tailored.field( new _field( _prototype ) );
        }
    }
    
    public static class TailorField
        implements _classTransfer
    {
        public Form fieldForm;
        
        public TailorField( String fieldMarkup )
        {
            this.fieldForm = ForML.compile( fieldMarkup );
        }
        
        public void transfer( _class _tailored, Context context )
        {
            _tailored.fields( fieldForm.authorSeries( context ) );
        }
    }
    

    public static class CopyMethod
        implements _classTransfer
    {
        public final _method _prototype;
        
        public CopyMethod( _method _prototype )
        {
            this._prototype = _prototype;
        }
        
        public void transfer( _class _tailored, Context context )
        {
            _tailored.method( new _method( _prototype ) );
        }
    }
    
    public static class TailorMethod
        implements _classTransfer
    {
        public Form signatureForm;
        public Template bodyTemplate;
        public _method _prototype;
        
        public TailorMethod of(_method _m, String signature, String body )
        {
            return new TailorMethod( _m, signature, body );
        }
        
        public TailorMethod ofSignature(_method _m, String signature )
        {
            return new TailorMethod( _m, signature, null );
        }
        public TailorMethod ofBody(_method _m, String body )
        {
            return new TailorMethod( _m, null, body );
        }
        public TailorMethod( _method _m, String signature, String body )
        {
            this._prototype = _m;
            if( signature != null )
            {
                this.signatureForm = ForML.compile( signature );
            }
            if( body != null )
            {
                this.bodyTemplate = BindML.compile( body );
            }
        }
        
        public void transfer( _class _tailored, Context context )
        {
            _method _m = null;
            if( signatureForm != null )
            {
                _m = _method.of( signatureForm.author( context ) );
            }
            else
            {
                _m = _method.of( _prototype );
            }            
            
            if( bodyTemplate != null )
            {
                _m.setBody( Author.toString( bodyTemplate, context ) );
            }
            else
            {
                _m.setBody( new _code( _prototype.getBody() ) );
            }
            _tailored.method( _m );
        }
    }
    
    public _class tailor( Context context )
    {
        //create the tailored target class
        _class _tailored = this._originator.initClass( context );
        
        for( int i = 0; i < this._transfers.size(); i++ )
        {
            this._transfers.get( i ).transfer( _tailored, context );
        }
        return _tailored;
    }
}
