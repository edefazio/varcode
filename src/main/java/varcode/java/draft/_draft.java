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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import varcode.ModelException;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.java.draft._draftClass._classOriginator;
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
 * that can have have _draft annotations associated with entities that denote
 * "instructions" for creating a new draft or (specialized) version of the code.
 * 
 * <UL>
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
public class _draft
{   
    public interface _typeDraft
    {
        public void draftTo( _class _c, Object...keyValuePairs ); 
        
        public void draftTo( _class _c, Context context ); 
        
        public void draftTo( _enum _e, Object...keyValuePairs );
        
        public void draftTo( _enum _e, Context context  );
        
        public void draftTo( _interface _i, Object...keyValuePairs );
        
    }
    
    //public @interface defaultTo
   // {
   //     String[] value();
   // }
    
    /**
     * A Way of Documenting and declaring the parameters/ variables
 to be used within a _draft (at the _class, _enum, interface, 
  _annotationType level)
     
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface declare
    {
        String[] value();
    }
    */ 
    
    /** _draft Annotation to customize the package name
     * @packageName("io.typeframe.fields") //set the packageName statically
     * @packageName("io.typeframe.{+project+}") //set packageName using the "project" var
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface packageName
    {
        String value();
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({TYPE, METHOD})
    public @interface annotations
    {
        /** add annotations explicitly (java.util.Map) or by parameter {+impotts+}*/
        String[] add() default {};
        
        /** Remove all annotations with these name patterns */
        String[] remove() default {};
    }
    
    /** 
     * _draft Annotation describing the contents of a static Block 
     * @staticBlock({"System.out.println( \"Hi\" );}) //constant static block
     * @staticBlock({"{+init+}"}); //set static Block content by var "init"
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface staticBlock
    {
        String[] value();
    }
    
    /** _draft Annotation describing one or more fields
     * @fields( {"public int a;", "public String name=\"Eric\";"} )
     * <PRE>
     * public class C
     * {
     * 
     * }
     * -----------------(Expands to) 
     * 
     * public class C
     * {
     *    public int a;
     *    public String name = \"Eric\";
     * }
     * </PRE>
     * 
     * @fields( {"public int {+fieldName+};"} )
     * <PRE>
     * public class MyPrototype
     * {
     * }
     * --------(Expands with ("fieldName", "x"))
     * public class MyPrototype
     * {
     *    public int x;
     * }
     * --------(Expands with ("fieldName", new String[]{"x", "y", "z"}))
     * public class MyPrototype
     * {
     *     public int x;
     *     public int y;
     *     public int z;
     * }
     * </PRE>
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface fields
    {
        String[] value();
    }
     
    /**
     * _draft Annotation to add / remove imports
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    public @interface imports
    {
        /** Remove all imports containing these "patterns" */
        String[] remove() default {};

        /** Add all imports expanded by these BinML markup strings */ 
        String[] add() default {};
    }

    /**
     * _draft annotation to define the signature 
 Annotation applied to the signature of class, enum, interface
 definitions, methods, constructors, fields... contains the markup to be
     * compiled to a {@link Template} for creating the signature:
     * <PRE>
     * @sig("public class A")
     * class B
     * {
     * }
     * ------------
     * public class A
     * {
     * 
     * }
     * </PRE>
     * //a method with as yet undefined arguments
     * sig("public static final int getValue( {{+:{+type+} {+name+}+}} )")
     * public static final int getValue( int a, int b )
     * {
     * 
     * }
     * --------------(with "type" and "name" == null)
     * public static final int getValue( )
     * {
     * 
     * }
     * --------------(with ("type", "String", "name", "label" ))
     * public static final int getValue( String label )
     * {
     *    
     * }
     * @sig("public static {+returnType+} doThisMethod( {{+:{+type+} {+name+},
     * +}} )") public static MyObj doThisMethod( String f1, int f2 ) { //... }
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)    
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    public @interface sig
    {
        /** BindML markup used to create a Template for the signature*/
        String value();
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
    public @interface form
    {
        String[] value();
    }
    
    /**
     * _draft Annotation for replacing code within a body of a method between a named label:
     * <PRE>
     * @formAt(form="System.out.println( {+fieldName+} );\n",at="label" )
     * public void describe()
     * {
     *     System.out.println( this.getClass().getSimpleName() );
     *     label:
     *     System.out.println( myField );
     *     label:
     * }
     * ------If we pass in ("fieldName", new String[]{"a", "b", "c"})
     * ------Will Expand to:
     * public void describe()
     * {
     *     System.out.println( this.getClass().getSimpleName() );
     *     System.out.println( a );
     *     System.out.println( b );
     *     System.out.println( c );
     * }
     * 
     */    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
    public @interface formAt
    {
        String[] value();
        String at();
    }
    
    /**
     * _draft Annotation to Replace a key with a parameter
     * <PRE>
     * @$({"100", "nameCount"})
     * public final int names = 100;
     * ---------- produces the BindML:
     * public final int names = {+nameCount+};
     * 
     * ----with ( "nameCount", 5 )
     * public final int names = 5;
     * 
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface $
    {
        String[] value();
    }
    
    
    /**
     * _draft Annotation for replacing the entire body of a 
 constructor or method with a form
     * <PRE>
     * @body("return {{+:{+FIELDNAME+}.storeState( {+name+} ) | +}};" ) 
     * public long store( Boolean value1, Boolean value2 ) 
     * { 
     *     return FIELD1.storeState( value1 ) | FIELD2.storeState( value2 ); 
     * }
     * ------------
     * 
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface body
    {
        /** the BindML markup used to create a Template for the body*/
        String[] value();
    }

    /**
     * _draft annotation for removing a component 
 (field, method, nest, etc.) when macro expanding
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface remove
    {
    }
    
    /**
     * _draft for Copying a static block to a target class / enum
     */
    public static class CopyStaticBlock
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
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
     * _draft expansion for Tailoring a static block to a target class
     */
    public static class DraftStaticBlock
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
    {
        public Template template;
        
        public DraftStaticBlock( String... bodyTemplate )
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
     * _draft for copying a constructor to a target class/enum
     */
    public static class CopyConstructor
        implements _draftClass.expansion
    {
        private final _constructors._constructor ctor;
        
        public CopyConstructor( _constructors._constructor ctor )
        {
            this.ctor = ctor;
        }

        public void expandTo( _enum _draft, Object...keyValuePairs )            
        {
            _draft.constructor( _constructors._constructor.of( ctor ) );
        }
                
        public void expandTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.constructor( _constructors._constructor.of( ctor ) );
        }
        
   
        public void expandTo( _class _draft, Context context )
        {
            _draft.constructor( _constructors._constructor.of( ctor ) );
        }        
    }
    
    /**
     * _draft expansion for tailoring and transferring a constructor to a 
 _class or _enum
     */
    public static class DraftConstructor
        implements _typeDraft
    {
        public Form signature;
        public Template body;
        public _constructor _prototype;
        
        
        public static DraftConstructor of( 
            _constructor _prototype, String signatureForm, String bodyForm )
        {
            return new DraftConstructor( _prototype, signatureForm, bodyForm );
        }
        
        public static DraftConstructor ofSignature( 
            _constructor _prototype, String signatureForm )
        {
            return new DraftConstructor( _prototype, signatureForm, null );
        }
        
        public static DraftConstructor ofBody( 
            _constructor _prototype, String bodyForm )
        {
            return new DraftConstructor( _prototype, null, bodyForm );
        }
        
        public DraftConstructor( 
            _constructor _prototype, String signatureForm, String bodyForm )
        {
            if( signatureForm != null )
            {
                this.signature = ForML.compile( signatureForm );
            }
            else
            {
                this.signature = ForML.compile( _prototype.getSignature().author() );
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

        
        public void draftTo( _enum _draft, Object...keyValuePairs )            
        {            
            _draft.constructor( expand( VarContext.of( keyValuePairs ) ) );
        }
        
        public void draftTo( _enum _draft, Context context )            
        {
            _draft.constructor( expand( context ) );            
        }
                
        
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.constructor( expand( VarContext.of( keyValuePairs ) ) );            
        }
        
        public void draftTo( _class _draft, Context context )            
        {
            _draft.constructor( expand( context ) );            
        }
                
        
        @Override
        public void draftTo( _interface _i, Object... keyValuePairs )
        {
            throw new ModelException( "Interfaces cannot have constructors" );
        }
        
        public _constructor expand( Context context )
        {
            _constructors._constructor _c = null;
            if( signature != null )
            {
                _c = _constructors._constructor.of( signature.author( context ) );
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
     * _draft for copying a package to the target _class, _interface
 _enum, or _annotationType
     */
    public static class CopyPackage
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
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
        
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.packageName( packageName );
        }
        
        public void draftTo( _enum _draft, Context context )
        {
            _draft.packageName( packageName );
        }
                
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            _draft.packageName( packageName );
        }
        
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
     * _draft Expansion for creating a package
     */
    public static class DraftPackage
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
    {
        public Form packageNameForm;
        
        public DraftPackage( String packageNameForm )
        {
            this.packageNameForm = ForML.compile(packageNameForm );
        }
        
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.packageName( packageNameForm.author( VarContext.of( keyValuePairs) ) );
        }

        public void draftTo( _enum _e, Context context )
        {
            _e.packageName( packageNameForm.author( context ) );
        }
        
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
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
     * _draft expansion for copying imports
     */
    public static class CopyImports
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
    {
        private _imports imports;
        
        public static CopyImports of( _imports _im )
        {
            return new CopyImports( _im );
        }
        
        public CopyImports( _imports imports )
        {
            this.imports = _imports.of( imports );
        }
        
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.imports( _imports.of( imports ) );
        }
        
        public void draftTo( _enum _e, Context context )
        {
            _e.imports( _imports.of( imports ) );
        }
        
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            //expandTo( _draft, VarContext.ofKeyValueArray( (Object[])keyValuePairs ) );
            _draft.imports( _imports.of( imports ) );
        }
        
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
    
    public static class DraftClassAnnotations
        implements _typeDraft
    {
        public static DraftClassAnnotations of (
            _anns annotations, String[] remove, String[] addMarkup )
        {
            return new DraftClassAnnotations( annotations, remove, addMarkup ); 
        }
        
        private final _anns annotations;
        
        private final Form[] add;
        
        
        public DraftClassAnnotations( 
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
         
        public void draftTo( _class _draft, Object...keyValuePairs )
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        public void draftTo( _class _draft, Context context )
        {
            _draft.annotate( expand( context ) );
        }
        
        public void draftTo( _enum _draft, Context context )
        {   //create a prototype
            _draft.annotate( expand( context ) );
        }
        
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
    
    /** _draft expansion for imports
     */
    public static class DraftImports
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
    {
        private final _imports imports;
        
        private final Form[] add;
        
        public static DraftImports of( _imports _i, String[] remove, String[] adds )
        {
            return new DraftImports( _i, remove, adds );
        }
        
        public DraftImports( 
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
        
        public void draftTo( _class _draft, Object...keyValuePairs )
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        public void draftTo( _class _draft, Context context )
        {   //create a prototype
            _draft.imports( draft( context ) );
        }
        
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.imports( draft( VarContext.of( keyValuePairs ) ) ); 
        }
        
        public void draftTo( _enum _e, Context context )
        {
            _e.imports( draft( context ) );
        }
        
        public _imports draft( Context context )
        {
            _imports _is = new _imports( this.imports );
            
            for(int i=0; i< this.add.length; i++ )
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
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
    {
        private final _fields._field _prototype;
        
        public CopyField( _fields._field _prototype )
        {
            this._prototype = _prototype;
        }
        
        public void draftTo( _enum _e, Object...keyValuePairs )
        {
            _e.field( new _field( _prototype  ) );
        }
        
        public void draftTo( _enum _e, Context context )
        {
            _e.field( new _field( _prototype  ) );
        }
        
        public void draftTo( _interface _i, Object...keyValuePairs )
        {
            _i.field( new _field( _prototype ) );
        }
        
        
        public void draftTo( _class _draft, Object...keyValuePairs )            
        {
            draftTo( _draft, VarContext.of( keyValuePairs ) );
        }
        
        public void draftTo( _class _draft, Context context )
        {
            _draft.field( new _field( _prototype ) );
        }
    }
    
    public static class DraftField
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
    {
        public Form fieldForm;
        
        public static DraftField of( String markup )
        {
            return new DraftField( markup );
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
        public static DraftField parameterize( _field _f, String...keyValues )
        {          
            _field _p = new _field( _f ); //create a prototype as to not "break" client assumptions
            if( keyValues.length %2 != 0 )
            {
                throw new ModelException(
                    "could not parameterize, must have an even numbers of Key Values" );
            }
            for( int i = 0; i < keyValues.length; i+= 2 )
            {   //call replace on the prototype (clone)
                System.out.println( "REPLACE "+keyValues[ i ]+" " + keyValues[ i + 1 ] );
                _p.replace( keyValues[ i ], "{+" + keyValues[ i + 1 ] +  "*+}" );
            }
            System.out.println("AUTHOR" + _p.author() );
            return new DraftField( _p.author() );
        }
        
        
        public DraftField( String fieldMarkup )
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
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion        
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
    
    public static class DraftMethod
        implements _typeDraft //_classMacro.expansion, _macroEnum.expansion
    {
        public final Form signatureForm;
        public final Template bodyTemplate;
        public final _method _prototype;
        
        
        public static DraftMethod parameterize( 
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
            return new DraftMethod( _m, _m.getSignature().author(), _m.getBody().author() );            
        }
        
        public static DraftMethod of( _method _m, String signature, String... body )
        {
            return new DraftMethod( _m, signature, body );
        }
        
        public static DraftMethod ofSignature( _method _m, String signature )
        {
            return new DraftMethod( _m, signature, null );
        }
        public static DraftMethod ofBody( _method _m, String... body )
        {
            return new DraftMethod( _m, null, body );
        }
        public DraftMethod( _method _m, String signature, String... body )
        {
            this._prototype = _m;
            if( signature != null )
            {
                this.signatureForm = ForML.compile( signature );
            }
            else
            {
                this.signatureForm = null;
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
            if( signatureForm != null )
            {
                _m = _method.of( signatureForm.author( context ) );
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
