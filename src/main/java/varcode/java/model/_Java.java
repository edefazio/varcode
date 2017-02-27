/*
 * Copyright 2017 M. Eric DeFazio.
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

import java.util.List;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.Model;

/**
 * Model of Java Language top level entities: 
 * (_class, _interface, _enum, _annotationType) code components
 * and components:
 * (_method, _field, _annotation, _package, _imports, ...)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface _Java
    extends Model
{
    /** 
     * Build and return the Java source String representation of the model 
     * (code in the target language)
     * @return String language representation of the model 
     */    
    String author( );
    
    /** 
     * Build and return the String representation of the model 
     * (code in the target language) using the Directives provided
     * 
     * @param directives (optional) directives to apply when 
     * authoring the document
     * @return document representation of the model
     */ 
    String author( Directive... directives );
    
    /**
     * A "Brute Force" replace for the content within the Java Model
     * Does a depth-first replace on any _Java component and any
     * children components.
     *
     * @param target the target toString to look for within the model
     * @param replacement the replacement toString
     * @return the modified _Java, (if it is mutable) or a modified clone
     */
    _Java replace( String target, String replacement );

    /** Visit the "nodes" of Hierarcial Java code */
    void visit( ModelVisitor visitor );
    
    /**
     * Java language component that can be nested (_class, _enum, _interface)
     * within a top level model (_class, _enum, _interface)
     */
    public interface _model
        extends _Java, Authored, Annotated, Javadoced, Nests, Declares
    {        
        /**
         * @return the name of the model (simple name)
         */
        String getName();

        /**
         * @return all imports for this component
         */
        _imports getImports();
    }

    /**
     * Return the Template  and Context for the State used for Authoring the
     * Component
     */
    public interface Authored
    {
        /**
         * @return the Template for the syntax structure of the language
         */
        Template getTemplate();

        /**
         * @return the context details/parameters for this model
         */
        Context getContext();
    }
    
    /**
     * Model of a Container that has monotonic countable models
     * (i.e. {@code _methods, _fields})
     */
    public interface Countable
    {
        public int count();
        
        public boolean isEmpty();
        
        public Object getAt( int index );
    }
    
    /* entity that has a Javadoc (class, enum, method, field, ...) */
    public interface Javadoced
    {
        _javadoc getJavadoc();
    }

    /**
     * entity that has annotations (class, method, field, parameter, ...)
     */
    public interface Annotated
    {
        _anns getAnnotations();
        
        Annotated annotate( _anns _annotations );
    }

    public interface Declares
    {
        /**
         * @return the fields for this component
         */
        _fields getFields();

        /**
         * @return the _methods for this component
         */
        _methods getMethods();
    }

    /**
     * Nests are Member (classes, interrfaces, enums, etc.) Declarations
     * embedded within another (class, interface, enum)
     */
    public interface Nests
    {
        /**
         * @return the count of nested (_classes, _interfaces, _enums)
         */
        int getNestCount();

        /**
         * return the nested component at index
         *
         * @param index the index of the nested component to retrieve
         * @return the component
         */
        _model getNestAt( int index );

        /**
         * Gets all nested subcomponents of this _component
         *
         * @return nested sub components
         */
        _nests getNests();

        /**
         *
         * @param nestedClassNames mutable list of names of all classes
         * @param containerClassName the name of the immediate container class
         * @return a List of all Class names (nested within this component
         */
        List<String> getAllNestClassNames(
            List<String> nestedClassNames, String containerClassName );

    }

    /**
     * Marker interface for organizing "facets" which are "parts" that can be
     * added within a hierarchal
     * {@code JavaMetaLang._model (_class, _enum, _interfaces)}
     *
     * (any field, method, modifier, annotation,...) this allows the interface
     * for an entity (_class,_enum, _interface)
     *
     * to have "generic" methods: public _class add( facet facet ) { //...adds a
     * _method, _annotation, _field, _constructor, _import, }
     *
     *
     * @author Eric DeFazio eric@varcode.io
     */
    public interface _facet
        extends _Java
    {

    }

    /**
     * Visit the model, and all children of the model
     */
    public interface ModelVisitor
    {
        public void visit( _Java jmm );
    }
}
