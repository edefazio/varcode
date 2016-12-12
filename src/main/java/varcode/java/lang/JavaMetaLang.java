/*
 * Copyright 2016 Eric.
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
package varcode.java.lang;

import java.io.Serializable;
import java.util.List;
import varcode.Model.MetaLang;
import varcode.context.VarContext;
import varcode.doc.Dom;

/**
 * "Meta" Model view of components within the Java Language.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface JavaMetaLang 
    extends MetaLang
{    
    /**
     * A "Brute Force" replace for the content within the Java MetaLanguage Model
     * @param target the target string to look for within the model
     * @param replacement the replacement string 
     * @return the modified JavaMetaModel, (if it is mutable) or a modified clone
     */
    JavaMetaLang replace( String target, String replacement ); 
 
    /**
     * Java language component that can be nested (_class, _enum, _interface) 
     * within a top level model (_class, _enum, _interface)
     */
    public interface _model
        extends JavaMetaLang
    {
        /** @return a clone of this component */
        _model clone();
    
        /** @return the name of this component (simple name) */
        String getName();

        /** @return the Dom of the component */
        Dom getDom();

        /** @return the context for this nest component*/
        VarContext getContext();

        /** @return all imports for this component */
        _imports getImports();

        /** @return the fields for this component */
        _fields getFields();

        /** @return the _methods for this component */
        _methods getMethods();
    
        /** @return the count of nested (_classes, _interfaces, _enums) */
        int getNestedCount();
    
        /** 
        * return the nested component at index 
        * @param index the index of the nested component to retrieve
        * @return  the component
        */
        _model getNestedAt( int index );
        
        /** 
         * Gets all nested subcomponents of this _component
         * @return nested sub components
         */
        _nests getNesteds(); 
    
        /**
         * 
         * @param nestedClassNames mutable list of names of all classes
         * @param containerClassName the name of the immediate container class
         * @return a List of all Class names (nested within this component
         */
        List<String> getAllNestedClassNames( 
            List<String>nestedClassNames, String containerClassName );
    }
    
    
    public interface _body extends Serializable
    {
        
    }
    
    /**
     * Marker interface for organizing "facets" which are
     * "parts" that can be added within a hierarchal 
     * {@code JavaMetaLang._model (_class, _enum, _interfaces)}
     * 
     * (any field, method, modifier, annotation,...)
     * this allows the interface for an entity (_class,_enum, _interface)
     * 
     * to have "generic" methods:
     * public _class add( facet facet )
     * {
     *    //...adds a _method, _annotation, _field, _constructor, _import, 
     * }
     * 
     * 
     * @author Eric DeFazio eric@varcode.io
     */
    public interface _facet //extends JavaMetaLang
    {
    
    }
}
