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
import varcode.java.model._ann;
import varcode.java.model._anns;
import varcode.java.model._imports;
import varcode.java.model._staticBlock;

/**
 * Abstract Base class for preparing and expanding Types from Macros 
 * 
 * @author M. Eric DeFazio eric@varcode.io 
 */
public abstract class draftType
{        
    protected static void prepareTypePackage( 
        List<draftAction> _expansions, String packageName, _ann pkgAnn )
    {
        if( pkgAnn != null )
        {
            _expansions.add( new draftAction.ExpandPackage( 
                pkgAnn.getAttrString() ) );
        }
        else
        {
           _expansions.add( new draftAction.CopyPackage( packageName ) ); 
        }
    }
        
    /**
     * Prepare imports of a given type for macro expansion
     * @param _i the existing imports
     * @param importAnn the @imports macro annotation with instructions (or null)
     * @param _expansions the existing macro expansions
     */
    protected static void prepareTypeImports( 
        List<draftAction> _expansions, _imports _i, _ann importAnn )
    {
        if( importAnn != null )
        {
            //System.out.println( "ADD IMPORTS " );
            _ann._attrs _attrs = importAnn.getAttrs();
            String adds = _attrs.getValueOf( "add" );
            String[] addsArr = new String[0];
            if( adds != null )
            {
                addsArr = _ann._attrs.parseStringArray( adds );                
            }
            String remove = _attrs.getValueOf( "remove" );
            String[] removeArr = new String[0];
            if( remove != null )
            {
                removeArr = _ann._attrs.parseStringArray( remove );                
            }
            _expansions.add( 
                draftAction.ExpandImports.of( _i, removeArr, addsArr ) );
        }
        else
        {
            //System.out.println( "COPY IMPORTS ");
            _expansions.add( draftAction.CopyImports.of( _i ) );
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
        List<draftAction> expansions, _anns typeAnnotations, _ann annAnnotation )
    {        
        if( annAnnotation != null )
        {
            //System.out.println( "Update Class Annotations" );
            String[] remove = 
                annAnnotation.getAttrStringArray( "remove" );
            String[] add = 
                annAnnotation.getAttrStringArray( "add" );
            
            expansions.add(draftAction.ExpandClassAnnotations.of( typeAnnotations, remove, add ) );                        
        }
        else
        {
            //System.out.println( "Copy Class Annotations " );
            //copy over all accept the known macro annotations
            expansions.add( draftAction.ExpandClassAnnotations.of( typeAnnotations, 
                new String[]{
                    annotations.class.getSimpleName(),
                    imports.class.getSimpleName(),
                    sig.class.getSimpleName(),
                    //_draft.declare.class.getSimpleName(),
                    $.class.getSimpleName(),
                    staticBlock.class.getSimpleName(),
                    packageName.class.getSimpleName(),
                    fields.class.getSimpleName()
                },
                new String[ 0 ] ) ); 
        }            
    }
    
    /**
     * Prepares fields defined via the @fields annotation on the type for macro expansion 
 (adds the appropriate _typeDraft to the existing list of typeExpansions)
     * @param _expansions
     * @param fieldsAnn 
     */
    protected static void prepareTypeFields( 
        List<draftAction> _expansions, _ann fieldsAnn )
    {
        if( fieldsAnn != null )
        {
            String[] arr = fieldsAnn.getAttrStringArray();
            
            for( int i = 0; i < arr.length; i++ )
            {
                draftAction.ExpandField ef = new draftAction.ExpandField( arr[ i ] );
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
        List<draftAction> _expansions, _ann staticBlockAnn, _staticBlock _sb )
    {
        //Static Block    
        //System.out.println("STATIC BLOCK ANNOTATION :"+ staticBlockAnn);
        
        if( staticBlockAnn != null )
        {
            //System.out.println("STATIC BLOCK ANNOTATION NOT NULL" );
            //System.out.println("STATIC BLOCK" + staticBlockAnn.attributes );
            //System.out.println( staticBlockAnn.attributes.keys );
            //System.out.println( staticBlockAnn.attributes.values );
            String rem = staticBlockAnn.getAttrString( "remove" );
            //System.out.println( rem );                
            if( rem != null && rem.equals( "true" ) )
            {
                //System.out.println( "REMOVE STATIC");                
            }
            else
            {
                String[] s = _ann._attrs.parseStringArray( 
                    staticBlockAnn.attributes.values.get( 0 ) );
                _expansions.add( new draftAction.ExpandStaticBlock( s ) );
            }
        }
        else
        {   //we copy the static block as is
            if( _sb != null && !_sb.isEmpty() )
            {                
                _expansions.add( new draftAction.CopyStaticBlock( _sb ) );
            }
        }                
    }
}
