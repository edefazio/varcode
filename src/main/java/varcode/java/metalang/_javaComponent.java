/*
 * Copyright 2016 M. Eric DeFazio.
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
package varcode.java.metalang;

import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Dom;
import varcode.Model.MetaLang;

/**
 * A Component that can be nested (_class, _enum, _interface) within a top level
 * model (_class, _enum, _interface)
 */
public interface _javaComponent
    extends MetaLang
{
    /** @return a clone of this component */
    _javaComponent clone();
    
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
    _javaComponent getNestedAt( int index );
    
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
