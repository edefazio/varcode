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
import varcode.context.Context;
import varcode.java.model._ann;
import varcode.java.model._anns;
import varcode.java.model._class;
import varcode.java.model._imports;
import varcode.java.model._staticBlock;
import varcode.java.draft._draft._typeDraft;

/**
 * Abstract Base class for preparing and expanding Types from Macros 
 * 
 * @author M. Eric DeFazio eric@varcode.io 
 */
public abstract class _draftType
{
    /**
     * Mutates the _class model by transferring (either a copy or new "tailored"
     * instance)
     */
    public interface expansion
    {
        public void expandTo( _class _tailored, Context context );    
        public void expandTo( _class _tailored, Object...keyValuePairs );    
    }
        
    protected static void prepareTypePackage( 
        List<_typeDraft> _expansions, String packageName, _ann pkgAnn )
    {
        if( pkgAnn != null )
        {
            _expansions.add( new _draft.DraftPackage( 
                pkgAnn.getLoneAttributeString() ) );
        }
        else
        {
           _expansions.add( new _draft.CopyPackage( packageName ) ); 
        }
    }
        
    /**
     * Prepare imports of a given type for macro expansion
     * @param _i the existing imports
     * @param importAnn the @imports macro annotation with instructions (or null)
     * @param _expansions the existing macro expansions
     */
    protected static void prepareTypeImports( 
        List<_typeDraft> _expansions, _imports _i, _ann importAnn )
    {
        if( importAnn != null )
        {
            //System.out.println( "ADD IMPORTS " );
            _ann._attributes _attrs = importAnn.getAttributes();
            String adds = _attrs.getRawValueForKey( "add" );
            String[] addsArr = new String[0];
            if( adds != null )
            {
                addsArr = _ann._attributes.parseStringArray( adds );                
            }
            String remove = _attrs.getRawValueForKey( "remove" );
            String[] removeArr = new String[0];
            if( remove != null )
            {
                removeArr = _ann._attributes.parseStringArray( remove );                
            }
            _expansions.add( 
                _draft.DraftImports.of( _i, removeArr, addsArr ) );
        }
        else
        {
            //System.out.println( "COPY IMPORTS ");
            _expansions.add( _draft.CopyImports.of( _i ) );
        }
    }
    
    /**
     * Prepares the annotations of a type for macro expansion
 (adds the appropriate _typeDraft to the existing list of typeExpansions
     * 
     * @param expansions the existing macro expansions
     * @param typeAnnotations all the annotations 
     * @param annAnnotation the @annotations annotation describing the macro expansion (or null)
     */
    protected static void prepareTypeAnnotations( 
        List<_typeDraft> expansions, _anns typeAnnotations, _ann annAnnotation )
    {        
        if( annAnnotation != null )
        {
            //System.out.println( "Update Class Annotations" );
            String[] remove = 
                annAnnotation.getAttributeStringArray( "remove" );
            String[] add = 
                annAnnotation.getAttributeStringArray( "add" );
            
            expansions.add(_draft.DraftClassAnnotations.of( typeAnnotations, remove, add ) );                        
        }
        else
        {
            //System.out.println( "Copy Class Annotations " );
            //copy over all accept the known macro annotations
            expansions.add(_draft.DraftClassAnnotations.of(typeAnnotations, 
                new String[]{
                    _draft.annotations.class.getSimpleName(),
                    _draft.imports.class.getSimpleName(),
                    _draft.sig.class.getSimpleName(),
                    //_draft.declare.class.getSimpleName(),
                    _draft.$.class.getSimpleName(),
                    _draft.staticBlock.class.getSimpleName(),
                    _draft.packageName.class.getSimpleName(),
                    _draft.fields.class.getSimpleName()
                },
                new String[0] ) ); 
        }            
    }
    
    /**
     * Prepares fields defined via the @fields annotation on the type for macro expansion 
 (adds the appropriate _typeDraft to the existing list of typeExpansions)
     * @param _expansions
     * @param fieldsAnn 
     */
    protected static void prepareTypeFields( 
        List<_typeDraft> _expansions, _ann fieldsAnn )
    {
        if( fieldsAnn != null )
        {
            String[] arr = fieldsAnn.getLoneAttributeStringArray();
            
            for( int i = 0; i < arr.length; i++ )
            {
                _draft.DraftField ef = new _draft.DraftField( arr[ i ] );
                //System.out.println( "ADDING "+ ef );
                _expansions.add( ef ); 
            }
        }        
    }
    
    /**
     * Prepares a static block defined via a @staticBlock staticBlockAnn for macro expansion
     * @param _expansions
     * @param staticBlockAnn
     * @param _sb 
     */
    protected static void prepareStaticBlock( 
        List<_typeDraft> _expansions, _ann staticBlockAnn, _staticBlock _sb )
    {
        //Static Block        
        if( staticBlockAnn != null )
        {
            String[] s = _ann._attributes.parseStringArray(staticBlockAnn.attributes.values.get( 0 ) );
            _expansions.add( new _draft.DraftStaticBlock( s ) );
        }
        else
        {   //we copy the static block as is
            if( _sb != null && !_sb.isEmpty() )
            {
                _expansions.add( new _draft.CopyStaticBlock( _sb ) );
            }
        }                
    }
}