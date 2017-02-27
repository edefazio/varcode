/*
 * Copyright 2016 M. Eric DeFazio
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
package varcode.java.load;

import varcode.LoadException;
import varcode.java.ast.JavaAst;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import varcode.java.ast.FormatJavaCode_AllmanScanStyle;
import varcode.java.ast.JavaCodeFormatVisitor;
import varcode.java.model._annotationType;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._interface;
import varcode.load.Source.SourceLoader;
import varcode.load.Source.SourceStream;
import static varcode.java.load.JavaSourceLoader.BaseJavaSourceLoader;

/**
 * Combines the operation of finding and loading Java code, with the operation of
 * porting the (string) source code to another model (like an AST or a _Java 
 * model ( _class, _interface,...)
 * 
 * Similar to a Class loader, but instead loads and Ports "Models" from one form 
 * to another.
 * 
 * ASTs Parse Trees that represent the model of things like
 * (_class,_interface,_enum, _method,_annotation, _import) etc.
 *
 * the reason for building models of source, is to make using, querying,
 * mutating models easier ( i.e. for meta-programming )
 *
 * for instance the code:
 * <PRE>public class MyClass extends MyBaseClass implements Serializable
 *  {
 *  }
 * </PRE> would be contained in a mutable _class model:
 * <PRE>
 _class _myClass = _JavaLoad._classFrom(
   "public class MyClass extends MyBaseClass implements Serializable");
 </PRE>
 *
 * we can easily read facets of the _class model:
 * <PRE>
 * System.out.println( _myClass.getName() );
 * _extends ext = myClass.getExtends();
 * _implements impls = myClass.getImplements();
 * _modifiers mods = myClass.getModifiers();
 *
 * </PRE>
 *
 * @author Eric DeFazio eric@varcode.io
 */
public class _JavaLoad
{
    public static final String N = System.lineSeparator();

    /**
     * Reads nested:
     * <UL>
     * <LI>{@link varcode.java.meta._annotatonType}
     * <LI>{@link varcode.java.model._class}
     * <LI>{@link varcode.java.model._interface}
     * <LI>{@link varcode.java.model._enum}
     * </UL>
     * AND provides the ability to either retain or remove the
     * <B>package and imports</B> declared in the parent top-level class...
     *
     * for example:
     * <PRE><CODE>
 package my.pack;

 import java.util.UUID;
 import java.util.Map;

 public class TopLevel
 {
     public static class Nested
     {
          private final String name = "A";
          public String getName()
          {
               return name;
          }
     }

     public static void main(String[] args)
     {
         //the _class model for Nested.class
         _class _nested = _JavaLoad.Nested._classFrom( Nested.class );

         //does not retain the packageName of TopLevel.class
         _nested.getPackageName(); // = null

         //does not retain the imports from the TopLevel.class
         _nested.getImports().count(); // = 0
 
         //import the model, include package
         _class _nestedIncludePkg = _JavaLoad.Nested._classFrom( 
             Nested.class, 
             true,  //include package
             false ); //DONT include imports
 
         _nestedIncludePkg.getPackageName(); // = "my.pack"

         //does not retain the imports from the TopLevel.class
         _nested.getImports().count(); // = 0      
 
         //import the model, include package AND imports
         _class _nestedIncludeBoth = _JavaLoad.Nested._classFrom( 
             Nested.class, 
             true,  //include package
             true ); //include imports
 
         _nestedIncludeBoth.getPackageName(); // = "my.pack"

         //retains the imports from the TopLevel.class
         _nested.getImports().count(); // = 2      
 
     }
 }
 </PRE></CODE>
     */
    public static class Nested
    {
        /**
         * Read the nested _class model from the Class
         *
         * @param clazz the class
         * @return the _class (without package or imports of the declaring
         * class)
         */
        public static _class _classFrom( Class clazz )
        {
            return _classFrom(
                JavaSourceLoader.BaseJavaSourceLoader.INSTANCE,
                clazz,
                new FormatJavaCode_AllmanScanStyle() );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @return
         * @throws LoadException
         */
        public static _class _classFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter )
            throws LoadException
        {
            return _classFrom( sourceLoader, clazz, codeFormatter, false, false );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @param retainPackage
         * @param retainImports
         * @return
         * @throws LoadException
         */
        public static _class _classFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage,
            boolean retainImports )
        {
            if( !clazz.isMemberClass() )
            {
                throw new LoadException( "Class " + clazz + " is not a nested class" );
            }
            SourceStream ss = null;
            try
            {
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
                Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();

                ss = sourceLoader.sourceStream(
                    declaringClass.getCanonicalName() + ".java" );
                if( ss == null )
                {
                    throw new LoadException(
                        "Unable to find source for \"" + declaringClass
                        + "\" with " + sourceLoader.describe() );
                }

                //Parse the Declaring Class into an AST
                CompilationUnit astRoot
                    = JavaAst.astFrom( ss.getInputStream() );

                return _classFrom( 
                    astRoot, clazz, codeFormatter, retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing Source " + ss.describe(), pe );
            }
        }

        public static _class _classFrom(
            CompilationUnit astRoot, Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage, boolean retainImports )
        {
            try
            {
                ClassOrInterfaceDeclaration astClassDecl
                    = JavaAst.findClassDeclaration( astRoot, clazz );
                return _JavaAstPort._classFrom(
                    astRoot, astClassDecl, codeFormatter,
                    retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing " + clazz + " from AST ", pe );
            }
        }
        
        /**
         * Read the nested _class model from the Class
         *
         * @param clazz the class
         * @return the _class (without package or imports of the declaring
         * class)
         */
        public static _interface _interfaceFrom( Class clazz )
        {
            return _interfaceFrom(
                BaseJavaSourceLoader.INSTANCE,
                clazz,
                new FormatJavaCode_AllmanScanStyle() );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @return
         * @throws LoadException
         */
        public static _interface _interfaceFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter )
            throws LoadException
        {
            return _interfaceFrom( 
                sourceLoader, clazz, codeFormatter, false, false );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @param retainPackage
         * @param retainImports
         * @return
         * @throws LoadException
         */
        public static _interface _interfaceFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage,
            boolean retainImports )
        {
            if( !clazz.isMemberClass() )
            {
                throw new LoadException( "Class " + clazz + " is not a nested class" );
            }
            SourceStream ss = null;
            try
            {
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
                Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();

                ss = sourceLoader.sourceStream(
                    declaringClass.getCanonicalName() + ".java" );
                if( ss == null )
                {
                    throw new LoadException(
                        "Unable to find source for \"" + declaringClass
                        + "\" with " + sourceLoader.describe() );
                }

                //Parse the Declaring Class into an AST
                CompilationUnit astRoot
                    = JavaAst.astFrom( ss.getInputStream() );

                return _interfaceFrom( 
                    astRoot, clazz, codeFormatter, retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing Source " + ss.describe(), pe );
            }
        }

        public static _interface _interfaceFrom(
            CompilationUnit astRoot, Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage, boolean retainImports )
        {
            try
            {
                ClassOrInterfaceDeclaration astClassDecl
                    = JavaAst.findInterfaceDeclaration( astRoot, clazz );
                return _JavaAstPort._interfaceFrom(
                    astRoot, astClassDecl, codeFormatter,
                    retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing " + clazz + " from AST ", pe );
            }
        }        
        
        /**
         * Read the nested _class model from the Class
         *
         * @param clazz the class
         * @return the _class (without package or imports of the declaring
         * class)
         */
        public static _enum _enumFrom( Class clazz )
        {
            return _enumFrom(
                BaseJavaSourceLoader.INSTANCE,
                clazz,
                new FormatJavaCode_AllmanScanStyle() );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @return
         * @throws LoadException
         */
        public static _enum _enumFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter )
            throws LoadException
        {
            return _enumFrom( 
                sourceLoader, clazz, codeFormatter, false, false );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @param retainPackage
         * @param retainImports
         * @return
         * @throws LoadException
         */
        public static _enum _enumFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage,
            boolean retainImports )
        {
            if( !clazz.isMemberClass() )
            {
                throw new LoadException( "Class " + clazz + " is not a nested class" );
            }
            SourceStream ss = null;
            try
            {
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
                Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();

                ss = sourceLoader.sourceStream(
                    declaringClass.getCanonicalName() + ".java" );
                if( ss == null )
                {
                    throw new LoadException(
                        "Unable to find source for \"" + declaringClass
                        + "\" with " + sourceLoader.describe() );
                }

                //Parse the Declaring Class into an AST
                CompilationUnit astRoot
                    = JavaAst.astFrom( ss.getInputStream() );

                return _enumFrom( 
                    astRoot, clazz, codeFormatter, retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing Source " + ss.describe(), pe );
            }
        }

        public static _enum _enumFrom(
            CompilationUnit astRoot, Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage, boolean retainImports )
        {
            try
            {
                EnumDeclaration astClassDecl
                    = JavaAst.findEnumDeclaration( astRoot, clazz );
                return _JavaAstPort._enumFrom(
                    astRoot, astClassDecl, codeFormatter,
                    retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing " + clazz + " from AST ", pe );
            }
        }        
        
        
        /**
         * Read the nested _class model from the Class
         *
         * @param clazz the class
         * @return the _class (without package or imports of the declaring
         * class)
         */
        public static _annotationType _annotationTypeFrom( Class clazz )
        {
            return _annotationTypeFrom(
                BaseJavaSourceLoader.INSTANCE,
                clazz,
                new FormatJavaCode_AllmanScanStyle() );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @return
         * @throws LoadException
         */
        public static _annotationType _annotationTypeFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter )
            throws LoadException
        {
            return _annotationTypeFrom( 
                sourceLoader, clazz, codeFormatter, false, false );
        }

        /**
         *
         * @param sourceLoader
         * @param clazz
         * @param codeFormatter
         * @param retainPackage
         * @param retainImports
         * @return
         * @throws LoadException
         */
        public static _annotationType _annotationTypeFrom(
            SourceLoader sourceLoader,
            Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage,
            boolean retainImports )
        {
            if( !clazz.isMemberClass() )
            {
                throw new LoadException( "Class " + clazz + " is not a nested class" );
            }
            SourceStream ss = null;
            try
            {
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
                Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();

                ss = sourceLoader.sourceStream(
                    declaringClass.getCanonicalName() + ".java" );
                if( ss == null )
                {
                    throw new LoadException(
                        "Unable to find source for \"" + declaringClass
                        + "\" with " + sourceLoader.describe() );
                }

                //Parse the Declaring Class into an AST
                CompilationUnit astRoot
                    = JavaAst.astFrom( ss.getInputStream() );

                return _annotationTypeFrom( 
                    astRoot, clazz, codeFormatter, retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing Source " + ss.describe(), pe );
            }
        }

        public static _annotationType _annotationTypeFrom(
            CompilationUnit astRoot, Class clazz,
            JavaCodeFormatVisitor codeFormatter,
            boolean retainPackage, boolean retainImports )
        {
            try
            {
                AnnotationDeclaration astClassDecl
                    = JavaAst.findAnnotationTypeDeclaration( astRoot, clazz );
                
                return _JavaAstPort._annotationTypeFrom(
                    astRoot, astClassDecl, codeFormatter,
                    retainPackage, retainImports );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing " + clazz + " from AST ", pe );
            }
        }
        
    }//NESTED

    public static _interface _interfaceFrom( Class clazz )
    {
        return _interfaceFrom(
            BaseJavaSourceLoader.INSTANCE,
            clazz,
            new FormatJavaCode_AllmanScanStyle() );
    }

    public static _interface _interfaceFrom(
        Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        return _interfaceFrom( 
            BaseJavaSourceLoader.INSTANCE, clazz, codeFormatter );
    }

    
    public static _interface _interfaceFrom(
        SourceStream sourceStream, Class clazz )
        throws LoadException
    {
        return _interfaceFrom( sourceStream, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _interface _interfaceFrom(
        SourceStream sourceStream, Class clazz, JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        try
        {
            //Parse the Declaring Class into an AST
            CompilationUnit astRoot
                = JavaAst.astFrom( sourceStream.getInputStream() );

            return _interfaceFrom( astRoot, clazz, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from SourceStream :" + System.lineSeparator()
                + sourceStream.describe(), pe );
        }
    }
    
    public static _interface _interfaceFrom(
        SourceLoader sourceLoader,
        Class clazz,
        JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        if( !clazz.isInterface() )
        {
            throw new LoadException(
                clazz.getCanonicalName() + " is NOT an Interface " );
        }
        if( clazz.isMemberClass() )
        {
            //need to extract the Nodes _annotationTypeFrom within the declared class
            SourceStream sourceStream = null;
            try
            {
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
                Class declaringClass = clazz.getDeclaringClass();
                sourceStream = sourceLoader.sourceStream(
                    declaringClass.getCanonicalName() + ".java" );

                if( sourceStream == null )
                {
                    throw new LoadException(
                        "Unable to find source for \"" + declaringClass
                        + "\" with " + sourceLoader.describe() );
                }
                //Parse the Declaring Class into an AST
                CompilationUnit astRoot
                    = JavaAst.astFrom( sourceStream.getInputStream() );

                TypeDeclaration astTypeDecl
                    = JavaAst.findTypeDeclaration( astRoot, clazz );

                if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
                {
                    ClassOrInterfaceDeclaration astInterfaceDecl
                        = (ClassOrInterfaceDeclaration)astTypeDecl;
                    if( astInterfaceDecl.isInterface() )
                    {
                        //return JavaASTParser._Interface._annotationTypeFrom( cu, id );
                        return _JavaAstPort._interfaceFrom(
                            astRoot, astInterfaceDecl, codeFormatter );
                    }
                }
                throw new LoadException(
                    clazz + " source not an interface " );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing Source " + sourceStream.describe(), pe );
            }
        }
        //top Level Interface
        SourceStream ss = BaseJavaSourceLoader.INSTANCE.sourceStream( clazz );
        try
        {
            // parse the file
            //CompilationUnit cu = JavaParser.parse( ss.getInputStream() );
            CompilationUnit astRoot
                = JavaAst.astFrom( ss.getInputStream() );

            ClassOrInterfaceDeclaration astClassDecl
                = JavaAst.findInterfaceDeclaration( astRoot, clazz );

            return _JavaAstPort._interfaceFrom(
                astRoot, astClassDecl, codeFormatter );

        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Unable to parse Interface Source for \"" + clazz + "\" from "
                + sourceLoader.describe(), pe );
        }
    }

    public static _interface _interfaceFrom( CompilationUnit astRoot,
        Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        return _interfaceFrom( astRoot, clazz.getSimpleName(), codeFormatter );
    }

    public static _interface _interfaceFrom( CompilationUnit astRoot,
        String typeName )
    {
        return _interfaceFrom( astRoot, typeName, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _interface _interfaceFrom( CompilationUnit astRoot,
        String typeName,
        JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            ClassOrInterfaceDeclaration astClassDecl
                = JavaAst.findInterfaceDeclaration( astRoot, typeName );
            return _JavaAstPort._interfaceFrom(
                astRoot, astClassDecl, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "unable to parse " + typeName + " from AST ", pe );
        }
    }

    public static _interface _interfaceFrom(
        CharSequence javaSourceCode,
        JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
            TypeDeclaration astTypeDecl
                = JavaAst.findRootTypeDeclaration( astRoot );
            String typeName = astTypeDecl.getName();
            return _interfaceFrom( astRoot, typeName, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaSourceCode, pe );
        }
    }

    public static _interface _interfaceFrom(
        CharSequence javaCode, String simpleClassName,
        JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaCode );
            ClassOrInterfaceDeclaration astClassDecl
                = JavaAst.findClassDeclaration( astRoot, simpleClassName );
            return _JavaAstPort._interfaceFrom(
                astRoot, astClassDecl, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaCode, pe );
        }
    }

    /**
     * Gets the top level class i.e. a Class named "TopLevelClass" (defined in a
     * file like TopLevelClass.java") for a given Class
     *
     * NOTE: since we can get many nest Levels Deep:
     * <PRE>
     * public class TopLevel
     * {
     *    public static class Nested1Level
     *    {
     *        public static class Nested2Level
     *        {
     *        }
     *    }
     * }
     * </PRE> ... we cant just get a Classes declaringClass, we need to continue
     * recursing until we find the class that has null for DeclaringClass.
     *
     * @param clazz the class to obtain top level class _annotationTypeFrom
     * @return the top level Class that contains the Class (could be itself)
     */
    public static Class getTopLevelClass( Class clazz )
    {
        if( clazz.getDeclaringClass() == null )
        {   //it must be declared in its own file 
            return clazz;
        }
        return getTopLevelClass( clazz.getDeclaringClass() );
    }

    /**
     * Loads the _class (langmodel)
     *
     * @param clazz
     * @return
     */
    public static _annotationType _annotationTypeFrom( Class clazz )
    {
        return _annotationTypeFrom( 
            BaseJavaSourceLoader.INSTANCE, clazz );
    }

    public static _annotationType _annotationTypeFrom(
        CompilationUnit astRoot, Class clazz )
    {
        try
        {
            AnnotationDeclaration astClassDecl
                = JavaAst.findAnnotationTypeDeclaration( astRoot, clazz );

            return _JavaAstPort._annotationTypeFrom( astRoot, astClassDecl );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AnnotationType Class " + clazz + " from AST ", pe );
        }
    }

    public static _annotationType _annotationTypeFrom(
        CompilationUnit astRoot, String simpleClassName )
    {
        try
        {
            AnnotationDeclaration astAnnotationDecl
                = JavaAst.findAnnotationTypeDeclaration(
                    astRoot, simpleClassName );
            return _JavaAstPort._annotationTypeFrom(
                astRoot, astAnnotationDecl );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing name \"" + simpleClassName + "\" from AST ", pe );
        }
    }

    public static _annotationType _annotationTypeFrom(
        CharSequence javaSourceCode )
        throws LoadException
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
            TypeDeclaration astTypeDecl
                = JavaAst.findRootTypeDeclaration( astRoot );
            String className = astTypeDecl.getName();
            if( astTypeDecl instanceof AnnotationDeclaration )
            {
                return _annotationTypeFrom( astRoot, className );
            }
            throw new LoadException(
                "could not find class " + className + " in source " );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaSourceCode, pe );
        }
    }

    public static _annotationType _annotationTypeFrom(
        CharSequence javaCode, String simpleClassName )
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaCode );
            AnnotationDeclaration astClassDecl
                = JavaAst.findAnnotationTypeDeclaration(
                    astRoot, simpleClassName );

            return _JavaAstPort._annotationTypeFrom(
                astRoot, astClassDecl );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaCode, pe );
        }
    }

    public static _annotationType _annotationTypeFrom( 
        SourceStream sourceStream, Class clazz )
    {
        return _annotationTypeFrom( 
            sourceStream, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _annotationType _annotationTypeFrom( 
        SourceStream sourceStream, Class clazz, JavaCodeFormatVisitor codeFormatter )
        throws LoadException 
    {
        try
        {
            //Parse the Declaring Class into an AST
            CompilationUnit astRoot
                = JavaAst.astFrom( sourceStream.getInputStream() );

            return _annotationTypeFrom( astRoot, clazz );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from sourceStream :" + System.lineSeparator()
                + sourceStream.describe(), pe );
        }
    }
    
    public static _annotationType _annotationTypeFrom(
        SourceLoader sourceLoader, Class clazz )
    {
        return _annotationTypeFrom( 
            sourceLoader, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    /**
     *
     * @param sourceLoader
     * @param clazz
     * @param codeFormatter
     * @return
     * @throws LoadException
     */
    public static _annotationType _annotationTypeFrom(
        SourceLoader sourceLoader, Class clazz, JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        if( clazz.isMemberClass() )
        {
            SourceStream ss = null;
            try
            {
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
            Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();

            ss = sourceLoader.sourceStream(
                declaringClass.getCanonicalName() + ".java" );
            
            if( ss == null )
            {
                throw new LoadException(
                    "Unable to find source for \"" + declaringClass
                    + "\" with " + sourceLoader.describe() );
            }

            CompilationUnit astRoot
                = JavaAst.astFrom( ss.getInputStream() );
            
            AnnotationDeclaration at = 
                JavaAst.findAnnotationTypeDeclaration( astRoot, clazz );
            
            return _annotationTypeFrom( astRoot, clazz );
            }
            catch( ParseException pe )
           {
                throw new LoadException(
                    "Error Parsing Source " + ss.describe(), pe );
           }
        }
        SourceStream ss = BaseJavaSourceLoader.INSTANCE.sourceStream( clazz );
        return _annotationTypeFrom( ss, clazz, codeFormatter );
    }

    /**
     *
     * @param clazz
     * @return a _class model
     */
    public static _class _classFrom( Class clazz )
    {
        return _classFrom(
            clazz, new FormatJavaCode_AllmanScanStyle() );
    }

    public static _class _classFrom(
        SourceLoader sourceLoader, Class clazz )
    {
        return _classFrom(
            sourceLoader, clazz, new FormatJavaCode_AllmanScanStyle() );
    }

    /**
     * Loads the _class (model)
     *
     * @param clazz the class to load the _class for
     * @param codeFormatter the formatter for formatting
     * @return
     */
    public static _class _classFrom(
        Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        return _classFrom( BaseJavaSourceLoader.INSTANCE, clazz, codeFormatter );
    }


    public static _class _classFrom( CompilationUnit astRoot, String className )
    {
        return _classFrom( astRoot, className,  new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _class _classFrom( CompilationUnit astRoot, Class clazz )
    {
        return _classFrom( astRoot, clazz,  new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _class _classFrom(
        CompilationUnit astRoot, Class clazz,
        JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            ClassOrInterfaceDeclaration astClassDecl
                = JavaAst.findClassDeclaration( astRoot, clazz );
            return _JavaAstPort._classFrom( astRoot, astClassDecl, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing " + clazz + " from AST ", pe );
        }
    }

    public static _class _classFrom(
        CompilationUnit astRoot, String simpleClassName,
        JavaCodeFormatVisitor codeFormat )
    {
        try
        {
            ClassOrInterfaceDeclaration astClassDecl
                = JavaAst.findClassDeclaration( astRoot, simpleClassName );
            return _JavaAstPort._classFrom( astRoot, astClassDecl, codeFormat );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing name \"" + simpleClassName + "\" from AST ", pe );
        }
    }

    public static _class _classFrom(
        CharSequence javaSourceCode, JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
            TypeDeclaration astTypeDecl
                = JavaAst.findRootTypeDeclaration( astRoot );
            String className = astTypeDecl.getName();
            if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration astClassDecl
                    = (ClassOrInterfaceDeclaration)astTypeDecl;
                if( !astClassDecl.isInterface() )
                {
                    return _classFrom(
                        astRoot, astClassDecl.getName(), codeFormatter );
                }
                throw new LoadException(
                    className + " is an interface; expected a class" );
            }
            throw new LoadException(
                "could not find class " + className + " in source " );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaSourceCode, pe );
        }
    }

    public static _class _classFrom(
        CharSequence javaCode,
        String simpleClassName,
        JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaCode );
            ClassOrInterfaceDeclaration astClassDecl
                = JavaAst.findClassDeclaration( astRoot, simpleClassName );
            return _JavaAstPort._classFrom(
                astRoot, astClassDecl, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaCode, pe );
        }
    }

    public static _class _classFrom( 
        SourceStream ss, Class clazz )
    {
        return _classFrom( ss, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _class _classFrom( 
        SourceStream ss, Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            //Parse the Declaring Class into an AST
            CompilationUnit astRoot
                = JavaAst.astFrom( ss.getInputStream() );

            return _classFrom( astRoot, clazz, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from SourceStream :" + System.lineSeparator()
                + ss.describe(), pe );
        }
    }
    
    /**
     *
     * @param sourceLoader
     * @param clazz
     * @param codeFormatter
     * @return
     * @throws LoadException
     */
    public static _class _classFrom(
        SourceLoader sourceLoader, Class clazz,
        JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        if( clazz.isMemberClass() )
        {
            SourceStream ss = null;
            try
            {
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
                Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();

                ss = sourceLoader.sourceStream(
                    declaringClass.getCanonicalName() + ".java" );
                if( ss == null )
                {
                    throw new LoadException(
                        "Unable to find source for \"" + declaringClass
                        + "\" with " + sourceLoader.describe() );
                }

                //Parse the Declaring Class into an AST
                CompilationUnit astRoot
                    = JavaAst.astFrom( ss.getInputStream() );

                return _classFrom( astRoot, clazz, codeFormatter );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing Source " + ss.describe(), pe );
            }
        }
        SourceStream ss = BaseJavaSourceLoader.INSTANCE.sourceStream( clazz );
        try
        {
            // parse the file
            //CompilationUnit cu = JavaParser.parse( ss.getInputStream() );
            CompilationUnit astRoot
                = JavaAst.astFrom( ss.getInputStream() );

            return _classFrom( astRoot, clazz, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Unable to parse Source for \"" + clazz + "\" from "
                + sourceLoader.describe(), pe );
        }
    }

    public static _enum _enumFrom( Class clazz )
    {
        return _enumFrom(
            BaseJavaSourceLoader.INSTANCE,
            clazz,
            new FormatJavaCode_AllmanScanStyle() );
    }

    public static _enum _enumFrom(
        Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        return _enumFrom( BaseJavaSourceLoader.INSTANCE, clazz, codeFormatter );
    }

    public static _enum _enumFrom(
        CompilationUnit astRoot, Class clazz,
        JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            EnumDeclaration astEnumDecl
                = JavaAst.findEnumDeclaration( astRoot, clazz );
            return _JavaAstPort._enumFrom( astRoot, astEnumDecl, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing enum " + clazz + " from AST ", pe );
        }
    }

    public static _enum _enumFrom( 
        SourceStream ss, Class clazz )
    {
        return _enumFrom( ss, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _enum _enumFrom( 
        SourceStream ss, Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            //Parse the Declaring Class into an AST
            CompilationUnit astRoot
                = JavaAst.astFrom( ss.getInputStream() );

            return _enumFrom( astRoot, clazz, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from SourceStream :" + System.lineSeparator()
                + ss.describe(), pe );
        }
    }
    
    public static _enum _enumFrom(
        SourceLoader sourceLoader, Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        Class topLevelClass = getTopLevelClass( clazz );
        if( clazz.isMemberClass() )
        {
            SourceStream ss = null;
            try
            {
                System.out.println( "IS MEMBER ENUM ");
                // we have to find the source of the Member class WITHIN the 
                // source of the declaring class
                Class declaringClass = clazz.getDeclaringClass();
                ss = sourceLoader.sourceStream(
                    declaringClass.getCanonicalName() + ".java" );
                if( ss == null )
                {
                    throw new LoadException(
                        "Unable to find source for \"" + declaringClass
                        + "\" with " + sourceLoader.describe() );
                }

                //Parse the Declaring Class into an AST
                CompilationUnit astRoot
                    = JavaAst.astFrom( ss.getInputStream() );

                EnumDeclaration enumDecl
                    = JavaAst.findEnumDeclaration( astRoot, clazz );

                //return JavaASTParser._Enum.fromCompilationUnit( cu, classDecl );
                return _JavaAstPort._enumFrom(
                    astRoot, enumDecl, codeFormatter );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing Source " + ss.describe(), pe );
            }
        }
        SourceStream ss = BaseJavaSourceLoader.INSTANCE.sourceStream( clazz );
        return _enumFrom( ss, clazz, codeFormatter );       
    }

    public static _enum _enumFrom(
        CharSequence javaCode, JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaCode );
            TypeDeclaration astTypeDecl
                = JavaAst.findRootTypeDeclaration( astRoot );
            String enumName = astTypeDecl.getName();
            return _enumFrom( astRoot, enumName, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaCode, pe );
        }
    }

    public static _enum _enumFrom( CompilationUnit astRoot, 
        String simpleClassName )
    {
        return _enumFrom( astRoot, simpleClassName, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _enum _enumFrom(
        CompilationUnit astRoot, String simpleClassName,
        JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            EnumDeclaration astEnumDecl
                = JavaAst.findEnumDeclaration( astRoot, simpleClassName );
            return _JavaAstPort._enumFrom(
                astRoot, astEnumDecl, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing name \"" + simpleClassName + "\" from AST ", pe );
        }
    }

    public static _enum _enumFrom(
        CharSequence javaSourceCode,
        String className,
        JavaCodeFormatVisitor codeFormatter )
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
            EnumDeclaration astEnumDecl
                = JavaAst.findEnumDeclaration( astRoot, className );
            return _JavaAstPort._enumFrom(
                astRoot, astEnumDecl, codeFormatter );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Error Parsing AST from Java Source :" + System.lineSeparator()
                + javaSourceCode, pe );
        }
    }
}
