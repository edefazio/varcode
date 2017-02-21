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
import varcode.ModelException;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.java.load._JavaLoad;
import varcode.java.model._class;
import varcode.java.model._fields._field;
import varcode.java.macro._macro.sig;
import varcode.java.macro._macro.CopyClassSignature;
import varcode.java.macro._macro.CopyField;
import varcode.java.macro._macro.CopyMethod;
import varcode.java.macro._macro.CopyPackage;
import varcode.java.macro._macro.CopyStaticBlock;
import varcode.java.macro._macro.ExpandClassSignature;
import varcode.java.macro._macro.ExpandField;
import varcode.java.macro._macro.ExpandMethod;
import varcode.java.macro._macro.ExpandPackage;
import varcode.java.macro._macro.ExpandStaticBlock;
import varcode.java.macro._macro._typeExpansion;
import varcode.java.macro._macro.body;
import varcode.java.macro._macro.form;
import varcode.java.macro._macro.formAt;
import varcode.java.model._Java;
import varcode.java.model._ann;
import varcode.java.model._ann._attributes;
import varcode.java.model._anns;
import varcode.java.model._fields;
import varcode.java.model._imports;
import varcode.java.model._methods;
import varcode.java.model._methods._method;

/**
 * 
 * Either 
 * 
 * Builds a new {@link _class} by a single {@code _classOrignator} and subsequent
 * {@code expansion}s that 
 * 
 * @see JavaSource
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _macroClass
{    
    public static _macroClass of( Class classPrototype )
    {
        return new _macroClass( _JavaLoad._classFrom( classPrototype ) );
    }
    
    public static _macroClass of( _class prototype )
    {
        return new _macroClass( prototype );
    }
    
    /** the prototype _class */
    public _class _prototype;
    
    /** method for originating the "tailored" _class */
    public _classOriginator _originator;
    
    public interface _classOriginator
    {
        public _class initClass( Context context );
    }
        
    /**
     * Mutates the _class model by transferring (either a copy or new "tailored"
     * instance)
     */
    public interface expansion
    {
        public void expandTo( _class _tailored, Context context );    
        public void expandTo( _class _tailored, Object...keyValuePairs );    
    }
    
    /**
     * returns the String content value of an Annotation of clazz
     * 
     * NOTE: works only for 
     * @param _c
     * @param clazz
     * @return 
     */
    protected static String getAnnotationStringProperty( 
        _Java.Annotated _c, Class clazz )
    {
        //_ann _a = getOneAnnotation( _c, clazz );
        _ann _a = _c.getAnnotations().getOne( clazz );
        if( _a != null )
        {
            String attr = _a.attributes.values.get( 0 );
            return attr.substring( 1, attr.length() - 1 ); 
        }
        return null;
    }
    
    protected static String[] getAnnotationStringArrayProperty(
        _Java.Annotated _c, Class clazz )
    {
        //_ann _a = getOneAnnotation( _c, clazz );
        _ann _a = _c.getAnnotations().getOne( clazz );
        if( _a != null )
        {
            String attr = _a.attributes.values.get( 0 );
            return _ann._attributes.parseStringArray( attr );
        }
        return null;
    }
    
    /** methods for 
     * <UL>
     * <LI>transferring components (fields, methods, etc.) from the 
     * _prototype to the "tailored" _class
     * <LI>"building"/ "tailoring" new components and populating 
     * them in the "tailored" _class
     * </UL>
     */
    public List<_typeExpansion> _transfers = 
        new ArrayList<_typeExpansion>();
        
    //nests    
    public _macroClass( _class _c )
    {
        //_attributes.parseStringArray( 
        //    _c.getAnnotation( sig.class ).getAttributes().values.get( 0 ) );
        
        //String sig = getAnnotationStringProperty( _c, sig.class );
        
        //String sig = "";
        if( _c.getAnnotation( _macro.sig.class ) != null )
        {
            String sig = _c.getAnnotation( sig.class ).getLoneAttributeString();
            if( sig != null) 
            {
                this._originator = new ExpandClassSignature( sig );
            }
            else
            {
                this._originator = 
                    new CopyClassSignature( _c.getSignature().author() );
            }            
        }
        else
        {
            this._originator = 
                new CopyClassSignature( _c.getSignature().author() );
        }
        
        //package
        if( _c.getAnnotations().contains( _macro.packageName.class ) )
        {
            this._transfers.add( 
                new ExpandPackage( 
                    _c.getAnnotations().getOne( _macro.packageName.class )
                        .getLoneAttributeString() ) );
                    //getAnnotationStringProperty( 
                    //    _c, _macro.packageName.class ) ) ); 
        }
        else
        {
           this._transfers.add( new CopyPackage(_c.getClassPackage() ) ); 
        }
        
        //free fields
        if( _c.getAnnotations().contains( _macro.fields.class ) )
        {           
            String[] arr = 
                getAnnotationStringArrayProperty( _c, _macro.fields.class );
            for(int i=0; i< arr.length; i++ )
            {
                this._transfers.add( new ExpandField( arr[ i ] ) ); 
            }
        }
         
        //imports
        _ann imports = _c.getAnnotations().getOne( _macro.imports.class );
        processImports( _c.getImports(), imports, this._transfers );
        //System.out.println( "IMPORTS <<<<< "+ imports );
        
        //Static Block
        _ann staticB = _c.getAnnotations().getOne( _macro.staticBlock.class );
        if( staticB != null )
        {
            String[] s = _attributes.parseStringArray( staticB.attributes.values.get( 0 ) );
            this._transfers.add( new ExpandStaticBlock( s ) );
        }
        else
        {   //we copy the static block as is
            if( _c.getStaticBlock() != null )
            {
                this._transfers.add( new CopyStaticBlock( _c.getStaticBlock() ) );
            }
        }        
        
        //List<_typeExpansion> macroFields = ;
        //this._transfers.addAll( this._transfers, processFields( _c.getFields() ) );    
        
        processFields( this._transfers, _c.getFields() );
        processMethods( this._transfers, _c.getMethods() ); 
    }

    public static final void processMethods( List<_typeExpansion> _transfers, _methods _ms )
    {
        for( int i = 0; i < _ms.count(); i++ )
        {
            _method _m = _ms.getAt( i );
            if( _m.getAnnotations().contains( form.class ) || 
                _m.getAnnotations().contains( formAt.class ) )
            {  //handle form or formAt 
                _transfers.add( processMethodForms( _m ) );                
            }
            else
            {   //handle sig, body, and remove
                _typeExpansion exp = processMethod( _m );
                if( exp != null )
                {
                    _transfers.add( exp );
                }   
            }            
        }
    }
    
    public static _typeExpansion processMethodForms( _method _m )
    {
        _anns _as = _m.getAnnotations();
        _ann form = _as.getOne( _macro.form.class );
        _ann formAt = _as.getOne( _macro.formAt.class );
        
        if( form != null )
        {
            //System.out.println( "doing form" );
            _method _p = new _method( _m );
            _p.getAnnotations().remove( form.class ); //remove form  annotation from the target method
            String beforeForm = "";
            String afterForm = "";
            String body = _m.getBody().author();
            
            System.out.println( body );
            //int firstFormIndex = body.indexOf( "form:" );
            //int lastFormIndex = body.indexOf( "form:", firstFormIndex + 1 );
            
            //this is what I WANT to do, but, the EXISTING javaparser is havingt a hard time with
            // internal comments inside of code 
            //int firstFormIndex = body.indexOf( "/*{*/" );
            //int lastFormIndex = body.indexOf( "/*}*/", firstFormIndex + 1 );
            int firstFormIndex  = body.indexOf( "form:" );
            int lastFormIndex  = body.indexOf( "/*}*/" );
            if( firstFormIndex < 0 || lastFormIndex < 0 )
            {
                throw new ModelException( 
                    "expected body to contains /*{*/ and /*}*/ labels" );
            }
            beforeForm = body.substring( 0, firstFormIndex );
            if( lastFormIndex > 0 )
            {
                afterForm = body.substring( lastFormIndex + "form:".length() );
            }
            String[] attrs = 
                _attributes.parseStringArray( form.getAttributes().values.get( 0 ) );
            
            
            //I need to make the prefix and postfix for FORMS
            attrs[ 0 ]= "{{+:" + attrs[ 0 ];
            attrs[ attrs.length -1 ] = attrs[ attrs.length -1 ] + "+}}";
            
            //time to stich the revised form back together
            String[] stitchedBody = new String[ attrs.length + 2 ];
            stitchedBody[ 0 ] = beforeForm;
            System.arraycopy( attrs, 0, stitchedBody, 1, attrs.length);
            stitchedBody[ stitchedBody.length -1 ] = afterForm;
            return ExpandMethod.ofBody( _p, stitchedBody );
        }
        else if( formAt != null )
        {
            System.out.println( "Doing FormAt" );
        }
        return null;
    }
    
    public static _typeExpansion processMethod( _method _m )
    {
        _anns _as = _m.getAnnotations();
        if( !_as.contains( _macro.remove.class ) )
        {   //we are either copying or tailoring the field                           
            _ann parameter = _as.getOne( _macro.$.class ); //TODO PARAMETER
            _ann sig = _as.getOne( _macro.sig.class );
            _ann body = _as.getOne( _macro.body.class );
            
            _method _p = new _method( _m );
            //first param
            if( parameter != null )
            {   //you CANNOT have BOTH sig Macros AND parameterization
                //System.out.println( "processing " + parameter );                
                String values = parameter.getAttributes().values.get( 0 );
                //System.out.println( "values " + values );                
                String[] valuesArray = _ann._attributes.parseStringArray( values );
                //System.out.println( "values[0]" + valuesArray[0] );                
                //System.out.println( "values[1]" + valuesArray[1] );                
                _p.getAnnotations().remove( _macro.$.class );
                return ExpandMethod.parameterize( _p, valuesArray );   
            }
            else if( sig != null )
            {   //we didnt explicitly tailor or remove it, so copy the method
                //System.out.println( "processing "+ sig );
                String[] str = _attributes.parseStringArray( 
                    sig.getAttributes().values.get( 0 ) );
                if( body != null )
                {
                    _p.getAnnotations().remove( sig.class );
                    _p.getAnnotations().remove( body.class );
                    String[] bod = _attributes.parseStringArray( 
                        body.getAttributes().values.get( 0 ) );
                    return ExpandMethod.of( _p, str[0], bod );
                }
                else
                {
                    _p.getAnnotations().remove( sig.class );
                    return ExpandMethod.ofSignature( _p, str[0] );
                }
            }
            else if( body != null )
            {
                String[] bod = _attributes.parseStringArray( 
                    body.getAttributes().values.get( 0 ) );
                return ExpandMethod.ofBody( _p, bod );
            }
            //just copy the field                        
            return new CopyMethod( _m );
        }
        return null;
    }
    
    /**
     * Decide how to handle imports (whether to add, remove imports
     * @param _i
     * @param importAnnMacro
     * @param _transfers 
     */
    public static void processImports( 
        _imports _i, _ann importAnnMacro, List<_typeExpansion> _transfers )
    {
        if( importAnnMacro != null )
        {
            //System.out.println( "ADD IMPORTS " );
            _attributes _attrs = importAnnMacro.getAttributes();
            String adds = _attrs.getRawValueForKey( "add" );
            String[] addsArr = new String[0];
            if( adds != null )
            {
                addsArr = _attributes.parseStringArray( adds );                
            }
            String remove = _attrs.getRawValueForKey( "remove" );
            String[] removeArr = new String[0];
            if( remove != null )
            {
                removeArr = _attributes.parseStringArray( remove );                
            }
            _transfers.add( 
                _macro.ExpandImports.of( _i, removeArr, addsArr ) );
        }
        else
        {
            //System.out.println( "COPY IMPORTS ");
            _transfers.add( _macro.CopyImports.of( _i ) );
        }
    }
    
    public static void processFields( List<_typeExpansion> _expansions, _fields _fs )
    {        
        //List<_typeExpansion>_macroFields = 
        //    new ArrayList<_typeExpansion>();
        for( int i = 0; i < _fs.count(); i++ )
        {
            _typeExpansion e = processField( _fs.getAt( i ) );
            if( e != null )
            {
                //_macroFields.add( e );
                _expansions.add( e );
            }
        }        
        //return _macroFields;
    }
    
    /**
     * processes a single field of a class, enum, interface or annotationType
     * return the 
     * @param _f
     * @return 
     */
    public static _typeExpansion processField( _field _f )
    {
        _anns _as = _f.getAnnotations();
        if( !_as.contains( _macro.remove.class ) )
        {   //we are either copying or tailoring the field                           
            _ann parameter = _as.getOne( _macro.$.class );
            _ann sig = _as.getOne( _macro.sig.class );
            
            if( parameter != null )
            {   //you CANNOT have BOTH sig Macros AND parameterization
                //System.out.println( "processing " + parameter );
                
                String values = parameter.getAttributes().values.get( 0 );
                //System.out.println( "values " + values );                
                String[] valuesArray = _ann._attributes.parseStringArray( values );
                //System.out.println( "values[0]" + valuesArray[0] );                
                //System.out.println( "values[1]" + valuesArray[1] );                
                
                _field _p = new _field( _f );
                _p.getAnnotations().remove( parameter.getName() );
                return ExpandField.parameterize( _p, valuesArray );
            }
            //else if( sig != null )
            //{   //we didnt explicitly tailor or remove it, so copy the method
            //    System.out.println( "processing "+ sig );
            // }
            //just copy the field                        
            return new CopyField( _f );
        }
        return null;
    }
    
    public _class expand( Object...keyValuePairs )
    {
        System.out.println( keyValuePairs.length );
        return expand( VarContext.ofKeyValueArray( keyValuePairs ) );
    }
    
    public _class expand( Context context )
    {
        //create the tailored target class
        _class _tailored = this._originator.initClass( context );
        
        for( int i = 0; i < this._transfers.size(); i++ )
        {
            this._transfers.get( i ).expandTo( _tailored, context );
        }
        return _tailored;
    }
}
