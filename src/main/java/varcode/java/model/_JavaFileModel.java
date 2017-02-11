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
package varcode.java.model;

import java.util.ArrayList;
import java.util.List;
import static varcode.Model.N;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.java.naming.ClassNameQualified;
import varcode.java.JavaAuthor;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.JavaSourceFile;
import varcode.markup.Template;

    /**
     * _model of a Java component (_class, _enum, _interface, _annotationType)
     * that can be declared in it's own File   
     */
    public abstract class _JavaFileModel
        implements _Java._model, ClassNameQualified
    {
        /** Imports for this _model */
        protected _imports imports = new _imports();
        
        /** package for the model */
        protected _package pckage = _package.of( null );
        
        /** 
         * Returns the fully qualified Class name for the _model
         * i.e. "java.util.Map" 
         * -instead of-
         * "Map"
         * 
         * @return the fully qualified Java Class Name 
         * (based on package and class name)
         */        
        @Override
        public final String getQualifiedName()
        {
            if( this.pckage != null && !this.pckage.isEmpty() )
            {
                return this.pckage.getName() + "." + getName();            
            }
            else
            {
                return getName();
            }
        }
            
        /**
         * 
         * @param nests the nests children of this _model
         * @return authored nested child/member Classes (1) level 
         * beneath this _model
         */
        public static final String authorNests( _nests nests )
        {
            String authoredNesteds = "";
            if( nests.count() > 0 )
            {
                //I need to go to each of the nested classes/ interfaces/ etc.
                // and read what thier imports are, then add these imports to my imports
                //String[] nested = new String[ nests.count() ];
                
                for( int i = 0; i < nests.count(); i++ )
                {
                    _Java._model _model = nests.models.get( i );
                    Context ctx = _model.getContext();

                    //inner classes inherit package, so remove the package                    
                    ctx.remove( "package" );
                    ctx.remove( "imports" );

                    //author the member class/enum/interface w/o package or imports
                    String res = JavaAuthor.toString( _model.getTemplate(), ctx ) 
                        + N + N ;
                    
                    authoredNesteds += res;                                     
                }
            }
            return authoredNesteds;
        }
        
        /** 
         * Each model can contain nested models, each may define 
         * thier own imports, so, when we author the model, we "roll up"
         * all imports to the top level to out the imports at the Declaring 
         * Top level model 
         */
        @Override
        public final _imports getImports()
        {
            for( int i = 0; i < this.getNests().count(); i++ )
            {
                this.imports.merge(
                    getNests().models.get( i ).getImports() );
            }
            return this.imports;
        }
    
        /**
         * <UL>
         * <LI>Build the (.java) source of the {@code _class} model
         * <LI>compile the source using Javac to create .class
         * <LI>load the .class in a new {@code AdHocClassLoader}
         * <LI>return a reference to the loaded class
         * </UL>
         *
         * @return the {@code Class } loaded from a {@code AdHocClassLoader}
         */
        public final Class loadClass()
        {
            return AdHoc.compile( new JavaSourceFile[]{ toJavaFile()} )
                .findClassBySimpleName( getName() );
        }
            
        /**
         * Returns an AdHocJavaFile containing the authored Java code
         * based on the _model
         * 
         * @param directives directives to apply to the source (i.e. audits
         * code formatting, etc.)
         * @return the AdHocJavaFile 
         */
        public final JavaSourceFile toJavaFile( Directive... directives )
        {
            return authorJavaFile(
                getQualifiedName(), 
                getTemplate(), 
                getContext(), 
                directives );
        }
            
        /**
         * Authors and returns an {@code AdHocJavaFile} with name
         * {@code className} using a {@code Dom} and based on the specialization
         * provided in the {@code context} and {@code directives}
         *
         * @param className the class name to be authored (i.e.
         * "io.varcode.ex.MyClass")
         * @param template the document object template to be specialized
         * @param context the specialization/functionality to be applied to the
         * Dom
         * @param directives optional pre and post processing commands
         * @return an AdHocJavaFile
         */
        protected static JavaSourceFile authorJavaFile(
            String className, Template template, Context context,
            Directive... directives )
        {
            
            return JavaAuthor.toJavaFile( className, template, context, directives );            
        }
        
        public final List<String>getAllClassNames()
        {
            String qualifiedName = getQualifiedName();
            List<String> cn = getAllNestClassNames( qualifiedName );
            cn.add( qualifiedName );
            return cn;
        }
        
        public final List<String> getAllNestClassNames()
        {
            return getAllNestClassNames( new ArrayList<String>(), getQualifiedName() );
        }
        
        public final List<String> getAllNestClassNames( String qualifiedClassName )
        {
            return getAllNestClassNames( new ArrayList<String>(), qualifiedClassName );
        }
        /**
         * Returns a String[] representing all class Names (for the top level and
         * all nested classes, enums, interfaces)
         * NOTE: uses the canonical form (i.e. ALL '.' paths)
         * i.e.
         * <PRE>
         * package io.ef;
         * 
         * public class Declaring
         * {
         *    public static class Nested
         *    {}
         * }
         * </PRE>
         * would use "io.ef.Declaring.Nested" for the nested class
         * (NOT io.ef.Declaring$Nested)
         * 
         * @param nestClassNames the accumulated Nested class names so far
         * (since this recursively looks through all nests of nests of nests)
         * @return all Names for the nest class names
         */
        @Override
        public final List<String> getAllNestClassNames(
            List<String> nestClassNames, String containerClassName )
        {
            for( int i = 0; i < this.getNests().count(); i++ )
            {
                _Java._model nest = this.getNests().getAt( i );
                String nestedClassName = nest.getName();
                String thisNestClassName = containerClassName + "." + nestedClassName;
                nestClassNames.add( thisNestClassName );
                for( int j = 0; j < nest.getNestCount(); j++ )
                {
                    nestClassNames
                        = nest.getAllNestClassNames(nestClassNames, thisNestClassName );
                }
            }
            return nestClassNames;
        }
    }
