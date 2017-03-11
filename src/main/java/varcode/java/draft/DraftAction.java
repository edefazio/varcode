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
import varcode.ModelException;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.java.model._ann;
import varcode.java.model._anns;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._interface;
import varcode.java.model._methods._method;
import varcode.java.model._package;
import varcode.java.model._staticBlock;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;
import varcode.markup.form.Form;
import varcode.markup.forml.ForML;

/**
 * A draft makes the act of building SPECIALIZED code Easy.
 * 
 * A Draft is a fully functional Java Class/ Enum / Interface or AnnotationType
 that can have have DraftAction annotations associated with entities that denote
 "instructions" for creating a new draft or (specialized) version of the code.
 
 <UL>
 * <LI>@annotations signify which aspects of the code are parameterized/variable
 * <LI>the _typeDraft entities are the "workers" who enact the creation of the
 * 
 * components of the Draft.
 * there are:
 * <UL>
 *  <LI> Copy... variants which do simple copies of existing models to the 
 *  <LI> Draft... variants that create new parameterized 
 * </UL>
 * </UL>
 * 
 * In a similar vein it seems the JVM may be headed for a similar kind of dynamicity
 * this tries to make the act 
 * <A HREF="https://youtu.be/gii6ySfsVfs?t=22m47s">John Rose, JVMLS 2016 "templates Classes"</A> 
 * @sig("public class {+className+}")
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface DraftAction
{   

    public void draftTo( _class _c, Object...keyValuePairs ); 
        
    public void draftTo( _class _c, Context context ); 
        
    public void draftTo( _enum _e, Object...keyValuePairs );
        
    public void draftTo( _enum _e, Context context  );
        
    public void draftTo( _interface _i, Object...keyValuePairs );
       
    /*
    public interface _typeDraft
    {
        
        
    }     
    */
    /**
     * DraftAction for Copying a static block to a target class / enum
     */
    public static class CopyStaticBlock
        implements DraftAction //_classMacro.expansion, _macroEnum.expansion
    {
        public _staticBlock _prototype;
            
        public CopyStaticBlock( _staticBlock _prototype )
        {
            this._prototype = _prototype;
        }

        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }

        @Override
        public void draftTo( _class _draft, Context context )            
        {
            _draft.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )            
        {
            _e.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }     
        
        @Override
        public void draftTo( _enum _draft, Context context )            
        {
            _draft.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            throw new ModelException("Cannot add Static Block to an interface " ); 
        }
    }
    
    /**
     * DraftAction expansion for Tailoring a static block to a target class
 
 {@link staticBlock}
     */
    public static class ExpandStaticBlock
        implements DraftAction
    {
        public Template template;
        
        public ExpandStaticBlock( String... bodyTemplate )
        {
            this.template = BindML.compile( bodyTemplate );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )            
        {
            _e.staticBlock( 
                 _staticBlock.of( 
                     Author.toString( template, VarContext.of(keyValuePairs) ) ) );
        }
                
        @Override
        public void draftTo( _enum _e, Context context )            
        {
            _e.staticBlock( 
                 _staticBlock.of( 
                     Author.toString( template, context ) ) );
        }
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.staticBlock( 
                _staticBlock.of( Author.toString( template, context ) ) );
        }        

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            throw new ModelException( "Cannot add static block to interface " ); 
        }
    }
    
    /**
     * DraftAction for copying a constructor to a target class/enum
     */
    public static class CopyConstructor
        implements DraftAction
    {
        private final _constructors._constructor ctor;
        
        public CopyConstructor( _constructors._constructor ctor )
        {
            this.ctor = ctor;
        }

        @Override
        public void draftTo( _enum _draft, Object...keyValuePairs )            
        {
            _draft.constructor( _constructors._constructor.of( ctor ) );
        }
                
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.constructor( _constructors._constructor.of( ctor ) );
        }
        
   
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.constructor( _constructors._constructor.of( ctor ) );
        }        

       
        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.constructor( _constructors._constructor.of( ctor ) );
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            throw new UnsupportedOperationException( "Interfaces have no constructor" ); 
        }
    }
    
    /**
     * DraftAction expansion for tailoring and transferring a constructor to a 
 _class or _enum
 
 {@link sig}
     * {@link s}
     * {@link body}
     * {@link b}
     * {@link $}
     */
    public static class ExpandConstructor
        implements DraftAction
    {
        public Template signature;
        public Template body;
        public _constructor _prototype;
        
        
        public static ExpandConstructor of( 
            _constructor _prototype, String signatureForm, String[] bodyForm )
        {
            return new ExpandConstructor( _prototype, signatureForm, bodyForm );
        }
        
        public static ExpandConstructor ofSignature( 
            _constructor _prototype, String signatureForm )
        {
            return new ExpandConstructor( _prototype, signatureForm, null );
        }
        
        public static ExpandConstructor ofBody( 
            _constructor _prototype, String[] bodyForm )
        {
            return new ExpandConstructor( _prototype, null, bodyForm );
        }
        
        public static ExpandConstructor parameterize( 
            _constructor _m, String[] keyValues )
        {
            if( keyValues.length %2 != 0 )
            {
                throw new ModelException(
                    "could not parameterize, key values must be even" );
            }
            for( int i = 0; i < keyValues.length; i+= 2 )
            {   //call replace on the prototype (clone)
                //System.out.println( "REPLACE "+keyValues[ i ]+" " + keyValues[ i + 1 ] );
                _m.replace( keyValues[ i ], "{+" + keyValues[ i + 1 ] +  "*+}" );
            }
            //System.out.println("AUTHOR" + _p.author() );
            return new ExpandConstructor( _m, _m.getSignature().author(), new String[]{_m.getBody().author()} );            
        }
        
        public ExpandConstructor( 
            _constructor _prototype, String signatureForm, String[] bodyForm )
        {
            if( signatureForm != null )
            {
                this.signature = BindML.compile( signatureForm );
            }
            else
            {
                this.signature = BindML.compile( _prototype.getSignature().author() );
            }
            if( bodyForm != null )
            {
                this.body = BindML.compile( bodyForm );
            }
            else
            {
                this.body = BindML.compile( _prototype.getBody().author() ); 
            }
            this._prototype = _prototype;
        }

        
        @Override
        public void draftTo( _enum _draft, Object...keyValuePairs )            
        {            
            _draft.constructor( draft( VarContext.of( keyValuePairs ) ) );
        }
        
        @Override
        public void draftTo( _enum _draft, Context context )            
        {
            _draft.constructor( draft( context ) );            
        }
                
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.constructor( draft( VarContext.of( keyValuePairs ) ) );            
        }
        
        @Override
        public void draftTo( _class _draft, Context context )            
        {
            _draft.constructor( draft( context ) );            
        }
                
        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            throw new ModelException( "Interfaces cannot have constructors" );
        }
        
        public _constructor draft( Context context )
        {
            _constructors._constructor _c = null;
            if( signature != null )
            {
                _c = _constructors._constructor.of( Author.toString( signature, context ) );
            }
            else
            {
                _c = _constructors._constructor.of( _prototype );
            }            
            
            if( body != null )
            {
                _c.setBody( Author.toString( body, context ) );
            }
            else
            {
                _c.setBody( new _code( _prototype.getBody() ) );
            }
            return _c;            
        }        

    }
    
    /**
     * DraftAction for copying a package to the target _class, _interface,
 _enum, or _annotationType
     */
    public static class CopyPackage
        implements DraftAction //_classMacro.expansion, _macroEnum.expansion
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
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.packageName( packageName );
        }
        
        @Override
        public void draftTo( _enum _draft, Context context )
        {
            _draft.packageName( packageName );
        }
                
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.packageName( packageName );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.packageName( packageName );
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            _i.packageName( packageName );            
        }
    }
    
    /**
     * DraftAction Expansion for creating a package
     */
    public static class ExpandPackage
        implements DraftAction
    {
        public Form packageNameForm;
        
        public ExpandPackage( String packageNameForm )
        {
            this.packageNameForm = ForML.compile( packageNameForm );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.packageName( packageNameForm.author( VarContext.of( keyValuePairs) ) );
        }

        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.packageName( packageNameForm.author( context ) );
        }
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.packageName( packageNameForm.author( context ) );
        }        

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            _i.packageName( packageNameForm.author( VarContext.of( keyValuePairs ) ) );
        }
    }
    
    /**
     * DraftAction expansion for copying imports
     */
    public static class CopyImports
        implements DraftAction //_classMacro.expansion, _macroEnum.expansion
    {
        private final _imports imports;
        
        public static CopyImports of( _imports _im )
        {
            return new CopyImports( _im );
        }
        
        public CopyImports( _imports imports )
        {
            this.imports = _imports.of( imports );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.imports( _imports.of( imports ) );
        }
        
        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.imports( _imports.of( imports ) );
        }
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.imports( _imports.of( imports ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.imports( _imports.of( imports ) );
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            _i.imports( _imports.of( imports ) );
        }
    }
    
    public static class ExpandClassAnnotations
        implements DraftAction
    {
        public static ExpandClassAnnotations of (
            _anns annotations, String[] remove, String[] addMarkup )
        {
            return new ExpandClassAnnotations( annotations, remove, addMarkup ); 
        }
        
        private final _anns annotations;
        
        private final Form[] add;
        
        
        public ExpandClassAnnotations( 
            _anns _source, String[] remove, String[] addMarkup )
        {
            //create a prototype
            _anns _as = new _anns( _source );
            
            //remove all I need
            List<_ann> toRemove = new ArrayList<_ann>();
            if( remove != null )
            {
                for( int i = 0; i < remove.length; i++ )
                {
                    String rem = remove[ i ];
                    for( int j = 0; j < _as.count(); j++ )
                    {
                        if( _as.getAt( j ).name.equals( rem ) )
                        {
                            toRemove.add( _as.getAt( j ) );
                        }
                    }                    
                }
                _as.remove( toRemove );      
            }
            if( addMarkup == null )
            {
                this.add = new Form[ 0 ];
            }
            else
            {
                this.add = new Form[ addMarkup.length ];
                for( int i = 0; i < addMarkup.length; i++ )
                {
                    add[ i ] = ForML.compile(addMarkup[ i ] );
                }
            }
            this.annotations = _as;            
        }
         
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.annotate( expand( context ) );
        }
        
        @Override
        public void draftTo( _enum _draft, Context context )
        {   //create a prototype
            _draft.annotate( expand( context ) );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.annotate( expand( VarContext.of( keyValuePairs ) ) ); 
        }
        
        public _anns expand( Context context )
        {
            _anns _as = new _anns( this.annotations );
            
            for( int i = 0; i < this.add.length; i++ )
            {
                String[] adds = this.add[ i ].authorSeries( context );
                if( adds.length > 0 )
                {
                    for( int j=0; j< adds.length; j++ )
                    {
                        _ann _a = _ann.of( adds[ j ] );
                        _as.add( _a );
                    }
                }
            }  
            return _as;            
        }        

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            _i.annotate( expand( VarContext.of( keyValuePairs ) ) );
        }
    }
    
    /** DraftAction expansion for imports
     */
    public static class ExpandImports
        implements DraftAction //_classMacro.expansion, _macroEnum.expansion
    {
        private final _imports imports;
        
        private final Form[] add;
        
        public static ExpandImports of( _imports _i, String[] remove, String[] adds )
        {
            return new ExpandImports( _i, remove, adds );
        }
        
        public ExpandImports( 
            _imports _source, String[] remove, String[] addMarkup )
        {
            //create a prototype
            _imports _is = new _imports( _source );
            
            //remove all I need
            List<String> toRemove = new ArrayList<String>();
            if( remove != null )
            {
                for( int i = 0; i < remove.length; i++ )
                {
                    toRemove.addAll( _is.match( remove[ i ] ) );
                }
                _is.remove( toRemove );      
            }
            if( addMarkup == null )
            {
                this.add = new Form[ 0 ];
            }
            else
            {
                this.add = new Form[ addMarkup.length ];
                for( int i = 0; i < addMarkup.length; i++ )
                {
                    add[ i ] = ForML.compile(addMarkup[ i ] );
                }
            }
            this.imports = _is;            
        }
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {   //create a prototype
            _draft.imports( draft( context ) );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.imports( draft( VarContext.of( keyValuePairs ) ) ); 
        }
        
        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.imports( draft( context ) );
        }
        
        public _imports draft( Context context )
        {
            _imports _is = new _imports( this.imports );
            
            for( int i = 0; i < this.add.length; i++ )
            {
                String[] adds = this.add[ i ].authorSeries( context );
                _is.add( (Object[])adds );
            }  
            return _is;            
        }        

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            _i.imports(draft( VarContext.of( keyValuePairs ) ) );
        }
    }
    
    public static class CopyField
        implements DraftAction //_classMacro.expansion, _macroEnum.expansion
    {
        private final _fields._field _prototype;
        
        public CopyField( _fields._field _prototype )
        {
            this._prototype = _prototype;
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.field( new _field( _prototype  ) );
        }
        
        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.field( new _field( _prototype  ) );
        }
        
        @Override
        public void draftTo( _interface _i, Object...keyValuePairs )
        {
            _i.field( new _field( _prototype ) );
        }
        
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.field( new _field( _prototype ) );
        }
    }
    
    public static class ExpandField
        implements DraftAction
    {
        public Form fieldForm;
        
        public static ExpandField of( String markup )
        {
            return new ExpandField( markup );
        }
        
        /**
         * This creates a optional parameterized based on 
         * @param _f
         * @param keyValues
         * @return 
         
        public static TailorField parameterizeOptional( _field _f, String...keyValues )
        {                      
            if( keyValues.length %2 != 0 )
            {
                throw new ModelException(
                    "could not parameterize, must have an even numbers of Key Values" );
            }
            String s = _f.author();
            
            for( int i = 0; i < keyValues.length; i+= 2 )
            {   //call replace on the prototype (clone)
                if( !s.contains( keyValues[ i ] ) )
                {
                    throw new ModelException( "Could not find " + keyValues[ i ] );
                    
                }
                s = s.replace( keyValues[i], "{+" + keyValues[ i + 1 ] + "|" 
                    + keyValues[ i ] + "+}" );                
            }
            return new TailorField( s );
        }
        */ 
        
        /**
         * This creates a Required parameterized based on 
         * @param _f
         * @param keyValues
         * @return 
         */
        public static ExpandField parameterize( _field _f, String...keyValues )
        {          
            _field _p = new _field( _f ); //create a prototype as to not "break" client assumptions
            if( keyValues.length %2 != 0 )
            {
                throw new ModelException(
                    "could not parameterize, must have an even numbers of Key Values" );
            }
            for( int i = 0; i < keyValues.length; i+= 2 )
            {   //call replace on the prototype (clone)
                //System.out.println( "REPLACE "+keyValues[ i ]+" " + keyValues[ i + 1 ] );
                _p.replace( keyValues[ i ], "{+" + keyValues[ i + 1 ] +  "*+}" );
            }
            //System.out.println("AUTHOR" + _p.author() );
            return new ExpandField( _p.author() );
        }
        
        
        public ExpandField( String fieldMarkup )
        {
            this.fieldForm = ForML.compile( fieldMarkup );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.fields( fieldForm.authorSeries( VarContext.of( keyValuePairs ) ) );
        }
        
        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.fields( fieldForm.authorSeries( context ) );
        }
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.fields( fieldForm.authorSeries( context ) );
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            _i.fields( fieldForm.authorSeries( VarContext.of( keyValuePairs ) ) );
        }
    }    

    public static class CopyMethod
        implements DraftAction //_classMacro.expansion, _macroEnum.expansion        
    {
        public final _method _prototype;
        
        public static CopyMethod of( _method _m )
        {
            return new CopyMethod( _m );
        }
        
        public CopyMethod( _method _prototype )
        {
            this._prototype = _prototype;
        }

        public void expandTo( _interface _i, Context context)
        {
            _i.method( new _method( _prototype ) );
        }
        
        @Override
        public void draftTo( _interface _i, Object...keyValuePairs )
        {
            _i.method( new _method( _prototype ) );
        }
        
        @Override
        public void draftTo( _enum _e, Context context)
        {
            _e.method( new _method( _prototype ) );
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.method( new _method( _prototype ) );
        }
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.method( new _method( _prototype ) );
        }
    }
    
    public static class ExpandMethod
        implements DraftAction
    {
        public final Template signatureTemplate;
        public final Template bodyTemplate;
        public final _method _prototype;
        
        
        public static ExpandMethod parameterize( 
            _method _m, String[] keyValues )
        {
            if( keyValues.length %2 != 0 )
            {
                throw new ModelException(
                    "could not parameterize, key values must be even" );
            }
            for( int i = 0; i < keyValues.length; i+= 2 )
            {   //call replace on the prototype (clone)
                System.out.println( "REPLACE "+keyValues[ i ]+" " + keyValues[ i + 1 ] );
                _m.replace( keyValues[ i ], "{+" + keyValues[ i + 1 ] +  "*+}" );
            }
            //System.out.println("AUTHOR" + _p.author() );
            return new ExpandMethod( _m, _m.getSignature().author(), _m.getBody().author() );            
        }
        
        public static ExpandMethod of( _method _m, String signature, String... body )
        {
            return new ExpandMethod( _m, signature, body );
        }
        
        public static ExpandMethod ofSignature( _method _m, String signature )
        {
            return new ExpandMethod( _m, signature, null );
        }
        public static ExpandMethod ofBody( _method _m, String... body )
        {
            return new ExpandMethod( _m, null, body );
        }
        public ExpandMethod( _method _m, String signature, String... body )
        {
            this._prototype = _m;
            if( signature != null )
            {
                this.signatureTemplate = BindML.compile( signature );
            }
            else
            {
                this.signatureTemplate = null;
            }
            if( body != null )
            {
                this.bodyTemplate = BindML.compile( body );
            }
            else
            {
                this.bodyTemplate = null;
            }
        }
        
        @Override
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.method( expand( VarContext.of( keyValuePairs ) ) );
        }
        
        @Override
        public void draftTo( _enum _e, Context context )
        {
            _e.method( expand( context ) );
        }
        
        @Override
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
                
        @Override
        public void draftTo( _class _draft, Context context )
        {
            _draft.method( expand( context  ) );
        }
        
        public _method expand( Context context )
        {
            _method _m = null;
            if( signatureTemplate != null )
            {
                _m = _method.of(Author.toString(this.signatureTemplate, context ) );
            }
            else
            {
                _m = new _method( _prototype );
            }            
            
            if( bodyTemplate != null )
            {
                _m.setBody( Author.toString( bodyTemplate, context ) );
            }
            else
            {
                _m.setBody( new _code( _prototype.getBody() ) );
            }
            return _m;
        }

        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            _i.method( expand( VarContext.of( keyValuePairs ) ) ); 
        }
    }
}
