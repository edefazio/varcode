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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.Expression;
import java.util.ArrayList;
import java.util.List;
import varcode.ModelException;
import varcode.context.Context;
import varcode.java.ast.JavaAst;
import varcode.java.model._class;
import varcode.java.model._fields._field;
import varcode.java.macro.Macro.sig;
import varcode.java.macro.Macro.CopyClassSignature;
import varcode.java.macro.Macro.CopyField;
import varcode.java.macro.Macro.CopyPackage;
import varcode.java.macro.Macro.ExpandClassSignature;
import varcode.java.macro.Macro.ExpandField;
import varcode.java.macro.Macro.ExpandPackage;
import varcode.java.macro.Macro._typeExpansion;
import varcode.java.model._Java;
import varcode.java.model._ann;
import varcode.java.model._anns;
import varcode.java.model._fields;

/**
 * Builds a new {@link _class} by a single {@code _classOrignator} and subsequent
 * {@code expansion}s that 
 * 
 * @see JavaSource
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _classMacro
{    
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
        public void expandTo( _class _tailored, Object...keyValuePairs );    
    }

    
    protected static _ann getOneAnnotation( _Java.Annotated ann, Class clazz )
    {
        List<_ann> sig = 
            ann.getAnnotations().get( clazz );
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
    protected static String getAnnotationStringProperty( 
        _Java.Annotated _c, Class clazz )
    {
        _ann _a = getOneAnnotation( _c, clazz );
        if( _a != null )
        {
            String attr = _a.attributes.values.get( 0 );
            return attr.substring( 1, attr.length() - 1 ); 
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
    public List<expansion> _transfers = 
        new ArrayList<expansion>();
        
    public static _classMacro of( _class prototype )
    {
        return new _classMacro( prototype );
    }
    //nests
    
    public _classMacro( _class _c )
    {
        String sig = getAnnotationStringProperty( _c, sig.class );
        if( sig != null )
        {
            this._originator = new ExpandClassSignature( sig );
        }
        else
        {
            this._originator = 
                new CopyClassSignature( _c.getSignature().author() );
        }
        
        //package
        if( _c.getAnnotations().contains( Macro.packageName.class ) )
        {
             this._transfers.add(new ExpandPackage( 
                    getAnnotationStringProperty( 
                        _c, Macro.packageName.class ) ) ); 
        }
        else
        {
           this._transfers.add( new CopyPackage(_c.getClassPackage() ) ); 
        }
        
        //free fields
        if( _c.getAnnotations().contains( Macro.fields.class ) )
        {
             this._transfers.add(new ExpandField( 
                    getAnnotationStringProperty( 
                        _c, Macro.fields.class ) ) ); 
        }
         
        //imports
        _ann imports = _c.getAnnotations().getOne( Macro.imports.class );
        if( imports != null )
        {
            System.out.println( "ADD IMPIORTS ");
            //this._transfers.add( e )
            //this._transfers.add( 
            //    new ExpandField( 
            //        getAnnotationStringProperty( 
            //            _c, Macro.fields.class ) ) ); 
        }
        else
        {
            System.out.println( "COPY IMPORTS ");
            this._transfers.add( Macro.CopyImports.of( _c.getImports() ) );
        }
        List<_typeExpansion> macroFields = processFields( _c.getFields() );
        this._transfers.addAll( macroFields );
        
    }

    public static List<_typeExpansion>processFields( _fields _fs )
    {        
        List<_typeExpansion>_macroFields = 
            new ArrayList<_typeExpansion>();
        for( int i = 0; i < _fs.count(); i++ )
        {
            _typeExpansion e = processField( _fs.getAt( i ) );
            if( e != null )
            {
                _macroFields.add( e );
            }
        }        
        return _macroFields;
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
        if( !_as.contains( Macro.remove.class ) )
        {   //we are either copying or tailoring the field                           
            _ann parameter = _as.getOne( Macro.$.class );
            _ann sig = _as.getOne( Macro.sig.class );
            
            if( parameter != null )
            {   //you CANNOT have BOTH sig Macros AND parameterization
                System.out.println( "processing " + parameter );
                
                String values = parameter.getAttributes().values.get( 0 );
                System.out.println( "values " + values );                
                String[] valuesArray = _ann._attributes.parseStringArray( values );
                System.out.println( "values[0]" + valuesArray[0] );                
                System.out.println( "values[1]" + valuesArray[1] );                
                
                _field _p = new _field( _f );
                _p.getAnnotations().remove( parameter.getName() );
                return ExpandField.parameterize( _p, valuesArray );
            }
            else if( sig != null )
            {   //we didnt explicitly tailor or remove it, so copy the method
                System.out.println( "processing "+ sig );
            }
            //just copy the field                        
            return new CopyField( _f );
        }
        return null;
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
