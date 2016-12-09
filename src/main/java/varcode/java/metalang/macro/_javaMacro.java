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
package varcode.java.metalang.macro;

import varcode.java.metalang._constructors._constructor;
import varcode.java.metalang._methods._method;
import varcode.java.metalang.JavaMetaLang._model;

/**
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface _javaMacro 
{
    /** 
     * Change that is applied to a _javaComponent via a Macro
     * 
     * We *COULD* maintain a changelist of what a macro did
     * since we often apply multiple macros to a given model...
     * 
     * This allows traceability into what happened, so if
     * something failed, we can still generate Partial results.
     */ 
    public interface _edit
    {
        
    }
    
    public static class AddConstructorEdit
        implements _edit
    {
        public final _model component;
        public final _constructor constructor;
        
        public AddConstructorEdit( _model component, _constructor constructor )
        {
            this.component = component;
            this.constructor = constructor;
        }
    }
    
    public static class AddMethodEdit
        implements _edit
    {
        public final _model component;
        public final _method method;
        
        public AddMethodEdit( _model component, _method method )
        {
            this.component = component;
            this.method = method;
        }
    }
    
}
