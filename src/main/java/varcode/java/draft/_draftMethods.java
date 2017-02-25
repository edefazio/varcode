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

import java.util.List;
import varcode.ModelException;
import varcode.java.draft._draft.formAt;
import varcode.java.model._ann;
import varcode.java.model._anns;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.java.draft._draft._typeDraft;

/**
 *
 * @author Eric
 */
public class _draftMethods
{
    /**
     * Prepares methods for macro expansion based on the presence of 
 annotations (@remove, @sig, @body, @form, @formAt) on each method 
 
 Adds the appropriate _typeDraft for the method
     * @param expansions 
     * @param _ms the methods of a type
     */
    public static final void prepareMethods( 
        List<_typeDraft> expansions, _methods _ms )
    {
        for( int i = 0; i < _ms.count(); i++ )
        {
            _methods._method _m = _ms.getAt( i );
            if( _m.getAnnotations().contains( _draft.form.class ) || 
                _m.getAnnotations().contains( formAt.class ) )
            {  //handle form or formAt 
                expansions.add( processMethodForms( _m ) );                
            }
            else
            {   //handle sig, body, and remove
                _draft._typeDraft exp = processMethod( _m );
                if( exp != null )
                {
                    expansions.add( exp );
                }   
            }            
        }
    }
    /**
     * processes a method that contains a @form or @formAt annotation
     * @param _m
     * @return 
     */
    public static _draft._typeDraft processMethodForms( _method _m )
    {
        _anns _as = _m.getAnnotations();
        _ann form = _as.getOne( _draft.form.class );
        _ann formAt = _as.getOne( _draft.formAt.class );
        
        if( form != null )
        {
            //System.out.println( "doing form" );
            _methods._method _p = new _methods._method( _m );
            _p.getAnnotations().remove( _draft.form.class ); //remove form  annotation from the target method
            String beforeForm = "";
            String afterForm = "";
            String body = _m.getBody().author();
            
            //System.out.println( body );
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
                _ann._attributes.parseStringArray( form.getAttributes().values.get( 0 ) );
            
            
            //I need to make the prefix and postfix for FORMS
            attrs[ 0 ]= "{{+:" + attrs[ 0 ];
            attrs[ attrs.length -1 ] = attrs[ attrs.length -1 ] + "+}}";
            
            //time to stich the revised form back together
            String[] stitchedBody = new String[ attrs.length + 2 ];
            stitchedBody[ 0 ] = beforeForm;
            System.arraycopy( attrs, 0, stitchedBody, 1, attrs.length);
            stitchedBody[ stitchedBody.length -1 ] = afterForm;
            return _draft.DraftMethod.ofBody( _p, stitchedBody );
        }
        else if( formAt != null )
        {
            //TODO complete this
            System.out.println( "Doing FormAt" );
        }
        return null;
    }
    
    /**
     * Determine how to prepare a method for macro expansion: 
     * <UL>
     *  <LI>DON'T INCLUDE IT (if the method contains an {@link _macro.remove} annotation
     *  <LI>EXPAND IT (if it includes ANY OF THESE:
     *     {@link _macro.$} parameterize
     *     {@link _macro.sig} macro expand method signature
     *     {@link _macro.body} macro expand body
     *     {@link _macro.form} macro expand body within the form
     *     {@link _macro.formAt} macro expand body at location
     *  <LI>COPY IT AS IS if it contains NONE of the above annotations
     * </UL>
     * @param _m the method
     * @return 
     */
    public static _draft._typeDraft processMethod( _method _m )
    {
        _anns _as = _m.getAnnotations();
        if( !_as.contains( _draft.remove.class ) )
        {   //we are either copying or tailoring the field                           
            _ann parameter = _as.getOne( _draft.$.class ); //TODO PARAMETER
            _ann sig = _as.getOne( _draft.sig.class );
            _ann body = _as.getOne( _draft.body.class );
            
            _method _p = new _methods._method( _m );
            //first param
            if( parameter != null )
            {   //you CANNOT have BOTH sig Macros AND parameterization
                //System.out.println( "processing " + parameter );                
                String values = parameter.getAttributes().values.get( 0 );
                //System.out.println( "values " + values );                
                String[] valuesArray = _ann._attributes.parseStringArray( values );
                //System.out.println( "values[0]" + valuesArray[0] );                
                //System.out.println( "values[1]" + valuesArray[1] );                
                _p.getAnnotations().remove( _draft.$.class );
                return _draft.DraftMethod.parameterize( _p, valuesArray );   
            }
            else if( sig != null )
            {   //we didnt explicitly tailor or remove it, so copy the method
                //System.out.println( "processing "+ sig );
                String[] str = _ann._attributes.parseStringArray( 
                    sig.getAttributes().values.get( 0 ) );
                if( body != null )
                {
                    _p.getAnnotations().remove( _draft.sig.class );
                    _p.getAnnotations().remove( _draft.body.class );
                    String[] bod = _ann._attributes.parseStringArray( 
                        body.getAttributes().values.get( 0 ) );
                    return _draft.DraftMethod.of( _p, str[0], bod );
                }
                else
                {
                    _p.getAnnotations().remove( _draft.sig.class );
                    return _draft.DraftMethod.ofSignature( _p, str[0] );
                }
            }
            else if( body != null )
            {
                String[] bod = _ann._attributes.parseStringArray( 
                    body.getAttributes().values.get( 0 ) );
                return _draft.DraftMethod.ofBody( _p, bod );
            }
            //just copy the field                        
            return new _draft.CopyMethod( _m );
        }
        return null;
    }
}
