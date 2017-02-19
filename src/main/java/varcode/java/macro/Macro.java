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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import varcode.ModelException;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.java.macro._classMacro._classOriginator;
import varcode.java.model._annotationType;
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
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Macro
{
    public interface _typeExpansion
        extends _classMacro.expansion, _enumMacro.expansion
    {
        public void expandTo( _class _c, Object...keyValuePairs );
        
        public void expandTo( _enum _e, Object...keyValuePairs );
        
        public void expandTo( _interface _i, Object...keyValuePairs );
        
    }
    
    /**
     * A Way of Documenting and declaring the parameters/ variables
     * to be used within a Macro (at the _class, _enum, interface, 
     * _annotationType level)
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface declare
    {
        String[] value();
    }
    
    /** Macro Annotation to customize the package name
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
    
    /** 
     * Macro Annotation describing the contents of a static Block 
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
    
    /** Macro Annotation describing one or more fields
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
 
    /** Macro Annotation for adding annotations to an element
     * i.e. 
     * <PRE>
     * @annotation({"@Deprecated"})
     * public static AtomicBoolean isEmpty;
     * -----
     * @Deprecated
     * public static AtomicBoolean isEmpty;
     * 
     * @annotation({"{+json+}"})
     * public String model;
     * 
     * ----------- with ( "json", "@JsonProperty(\"carModel\")" );
     * 
     * @JsonProperty("carModel")
     * public String name;
     * </PRE>
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface annotation 
    {
        String[] values();
    }
    
    /**
     * Macro Annotation to add / remove imports
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface imports
    {
        /** Remove all imports containing these "patterns" */
        String[] remove() default {};

        /** Add all imports expanded by these BinML markup strings */ 
        String[] add() default {};
    }

    /**
     * Macro annotation to define the signature 
     * Annotation applied to the signature of class, enum, interface
     * definitions, methods, constructors, fields... contains the markup to be
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
     * //a method with as yet underfined arguments
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
    //Method & constructor
    public @interface sig
    {
        /**
         * the BindML markup used to create a Template for the signature
         */
        String value();
    }
    
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public @interface form
    {
        String value();
    }
    
    /**
     * Macro Annotation for replacing code
     * within a body of a method between a named label:
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
    public @interface formAt
    {
        String form();
        String at();
    }
    
    /**
     * Macro Annotation to Replace a key with a parameter
     * <PRE>
     * @$({"100", "nameCount"})
     * public final int names = 100;
     * ----------
     * public final int names = {+nameCount+};
     * 
     * ----with ( "nameCount", 5 )
     * public final int names = 5;
     * 
     */
    public @interface $
    {
        String[] value();
        //boolean required() default true;
        //String as() default "*";
    }
    
    public @interface $sig
    {
        String[] value();
    }
    
    public @interface $body
    {
        String[] value();
    }
    
    /** Convert all targets to parameters 
    public @interface parameterize
    {
        String[] value();
    }
    */ 
    
    /**
     * Macro Annotation for replacing the entire body of a 
     * constructor or method with a form
     * <PRE>
     * @body("return {{+:{+FIELDNAME+}.storeState( {+name+} ) | +}};" ) 
     * public long store( Boolean value1, Boolean value2 ) 
     * { 
     *     return FIELD1.storeState( value1 ) | FIELD2.storeState( value2 ); 
     * }
     * ------------
     * 
     */
    public @interface body
    {
        /**
         * the BindML markup used to create a Template for the body
         */
        String value();
    }

    /**
     * Macro annotation for removing a component 
     * when tailoring
     */
    public @interface remove
    {
    }
    
    /**
     * Macro for Copying a static block to a target class / enum
     */
    public static class CopyStaticBlock
        implements _classMacro.expansion, _enumMacro.expansion
    {
        public _staticBlock _prototype;
            
        public CopyStaticBlock( _staticBlock _prototype )
        {
            this._prototype = _prototype;
        }

        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            _tailored.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }

        public void expandTo( _class _tailored, Context context )            
        {
            _tailored.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }
        
        public void expandTo( _enum _e, Object...keyValuePairs )            
        {
            _e.staticBlock( _staticBlock.cloneOf( _prototype ) );
        }     
    }
    
    /**
     * Macro expansion for Tailoring a static block to a target class
     */
    public static class ExpandStaticBlock
        implements _classMacro.expansion, _enumMacro.expansion
    {
        public Template template;
        
        public ExpandStaticBlock( String bodyTemplate )
        {
            this.template = BindML.compile( bodyTemplate );
        }
        
        public void expandTo( _enum _e, Object...keyValuePairs )            
        {
            _e.staticBlock( 
                 _staticBlock.of( 
                     Author.toString( template, VarContext.of(keyValuePairs) ) ) );
        }
                
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            expandTo( _tailored, VarContext.of( keyValuePairs ) );
        }
        
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.staticBlock( 
                _staticBlock.of( Author.toString( template, context ) ) );
        }        
    }
    
    /**
     * Macro for copying a constructor to a target class/enum
     */
    public static class CopyConstructor
        implements _classMacro.expansion
    {
        private final _constructors._constructor ctor;
        
        public CopyConstructor( _constructors._constructor ctor )
        {
            this.ctor = ctor;
        }

        public void expandTo( _enum _tailored, Object...keyValuePairs )            
        {
            _tailored.constructor( _constructors._constructor.of( ctor ) );
        }
                
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            _tailored.constructor( _constructors._constructor.of( ctor ) );
        }
        
   
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.constructor( _constructors._constructor.of( ctor ) );
        }        
    }
    
    /**
     * Macro expansion for tailoring and transferring a constructor to a 
     * _class or _enum
     */
    public static class ExpandConstructor
        implements _classMacro.expansion, _enumMacro.expansion
    {
        public Form signature;
        public Template body;
        public _constructor _prototype;
        
        
        public static ExpandConstructor of( 
            _constructor _prototype, String signatureForm, String bodyForm )
        {
            return new ExpandConstructor( _prototype, signatureForm, bodyForm );
        }
        
        public static ExpandConstructor ofSignature( 
            _constructor _prototype, String signatureForm )
        {
            return new ExpandConstructor( _prototype, signatureForm, null );
        }
        
        public static ExpandConstructor ofBody( 
            _constructor _prototype, String bodyForm )
        {
            return new ExpandConstructor( _prototype, null, bodyForm );
        }
        
        public ExpandConstructor( 
            _constructor _prototype, String signatureForm, String bodyForm )
        {
            this.signature = ForML.compile( signatureForm );
            this.body = BindML.compile( bodyForm );
            this._prototype = _prototype;
        }

        
        public void expandTo( _enum _tailored, Object...keyValuePairs )            
        {            
            _tailored.constructor( expand( VarContext.of( keyValuePairs ) ) );
        }
        
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            _tailored.constructor( expand( VarContext.of( keyValuePairs ) ) );            
        }
        
        public void expandTo( _class _tailored, Context context )            
        {
            _tailored.constructor( expand( context ) );            
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
     * Macro for copying a package to the target _class, _interface
     * _enum, or _annotationType
     */
    public static class CopyPackage
        implements _classMacro.expansion, _enumMacro.expansion
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
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.packageName( packageName );
        }
        
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            //System.out.println("CLAZZZZ"+ keyValuePairs[ 0 ].getClass() );
            //expandTo( _tailored, VarContext.ofKeyValueArray( keyValuePairs ) );
            _tailored.packageName( packageName );
        }
        
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.packageName( packageName );
        }
    }
    
    /**
     * Macro Expansion for creating a package
     */
    public static class ExpandPackage
        implements _classMacro.expansion, _enumMacro.expansion
    {
        public Form packageNameForm;
        
        public ExpandPackage( String packageNameForm )
        {
            this.packageNameForm = ForML.compile(packageNameForm );
        }
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.packageName( packageNameForm.author( VarContext.of( keyValuePairs) ) );
        }
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            expandTo( _tailored, VarContext.of( keyValuePairs ) );
        }
        
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.packageName( packageNameForm.author( context ) );
        }        
    }
    
    /**
     * Macro expansion for copying imports
     */
    public static class CopyImports
        implements _classMacro.expansion, _enumMacro.expansion
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
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.imports( _imports.of( imports ) );
        }
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            //expandTo( _tailored, VarContext.ofKeyValueArray( (Object[])keyValuePairs ) );
            _tailored.imports( _imports.of( imports ) );
        }
        
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.imports( _imports.of( imports ) );
        }
    }
    
    /** Macro expansion for imports
     */
    public static class ExpandImports
        implements _classMacro.expansion, _enumMacro.expansion
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
        
        public void expandTo( _class _tailored, Object...keyValuePairs )
        {
            expandTo( _tailored, VarContext.of( keyValuePairs ) );
        }
        
        public void expandTo( _class _tailored, Context context )
        {   //create a prototype
            _tailored.imports( expand( context ) );
        }
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.imports( expand( VarContext.of( keyValuePairs ) ) ); 
        }
        
        public _imports expand( Context context )
        {
            _imports _is = new _imports( this.imports );
            
            for(int i=0; i< this.add.length; i++ )
            {
                String[] adds = this.add[ i ].authorSeries( context );
                _is.add( (Object[])adds );
            }  
            return _is;            
        }        
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
    
    public static class ExpandClassSignature
        implements _classOriginator
    {
        public Template signature;
        
        public ExpandClassSignature( String form )
        {
            System.out.println( form );
            this.signature = BindML.compile( form );
        }
        
        public _class initClass( Object... keyValuePairs )
        {
            return initClass( VarContext.of( keyValuePairs ) );
        }
        
        @Override
        public _class initClass( Context context )
        {
            return _class.of( Author.toString( signature, context ) );
        }
    }  
    
    public static class CopyField
        implements _typeExpansion //_classMacro.expansion, _enumMacro.expansion
    {
        private final _fields._field _prototype;
        
        public CopyField( _fields._field _prototype )
        {
            this._prototype = _prototype;
        }
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.field( new _field( _prototype  ) );
        }
        
        public void expandTo( _interface _i, Object...keyValuePairs )
        {
            _i.field( new _field( _prototype ) );
        }
        
        //public void expandTo( _annotationType _a, Object...keyValuePairs )
        //{
        //    _a.field( new _field( _prototype ) );
        // }
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            expandTo( _tailored, VarContext.of( keyValuePairs ) );
        }
        
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.field( new _field( _prototype ) );
        }
    }
    
    public static class ExpandField
        implements _typeExpansion //_classMacro.expansion, _enumMacro.expansion
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
                System.out.println( "REPLACE "+keyValues[ i ]+" " + keyValues[ i + 1 ] );
                _p.replace( keyValues[ i ], "{+" + keyValues[ i + 1 ] +  "*+}" );
            }
            System.out.println("AUTHOR" + _p.author() );
            return new ExpandField( _p.author() );
        }
        
        
        public ExpandField( String fieldMarkup )
        {
            this.fieldForm = ForML.compile( fieldMarkup );
        }
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.fields( fieldForm.authorSeries( VarContext.of( keyValuePairs ) ) );
        }
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            expandTo( _tailored, VarContext.of( keyValuePairs ) );
        }
        
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.fields( fieldForm.authorSeries( context ) );
        }

        @Override
        public void expandTo( _interface _i, Object... keyValuePairs )
        {
            _i.fields( fieldForm.authorSeries( VarContext.of( keyValuePairs ) ) );
        }
    }    

    public static class CopyMethod
        implements _classMacro.expansion, _enumMacro.expansion        
    {
        public final _method _prototype;
        
        public CopyMethod( _method _prototype )
        {
            this._prototype = _prototype;
        }
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.method( new _method( _prototype ) );
        }
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            expandTo( _tailored, VarContext.of( keyValuePairs ) );
        }
        
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.method( new _method( _prototype ) );
        }
    }
    
    public static class ExpandMethod
        implements _classMacro.expansion, _enumMacro.expansion
    {
        public final Form signatureForm;
        public final Template bodyTemplate;
        public final _method _prototype;
        
        public static ExpandMethod of( _method _m, String signature, String body )
        {
            return new ExpandMethod( _m, signature, body );
        }
        
        public static ExpandMethod ofSignature( _method _m, String signature )
        {
            return new ExpandMethod( _m, signature, null );
        }
        public static ExpandMethod ofBody( _method _m, String body )
        {
            return new ExpandMethod( _m, null, body );
        }
        public ExpandMethod( _method _m, String signature, String body )
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
        
        public void expandTo( _enum _e, Object...keyValuePairs )
        {
            _e.method( expand( VarContext.of( keyValuePairs ) ) );
        }
        
        public void expandTo( _class _tailored, Object...keyValuePairs )            
        {
            expandTo( _tailored, VarContext.of( keyValuePairs ) );
        }
                
        public void expandTo( _class _tailored, Context context )
        {
            _tailored.method( expand( context  ) );
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
            return _m;
        }
    }
}
