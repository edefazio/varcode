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

import varcode.context.Context;
import varcode.java.model._class;
import varcode.java.model._enum;

/**
 * an _enum that has one or more Macro Annotations to be processed
 * and Macro Expanded to produce a customized _enum
 *   
 * @author Eric
 */
public class _macroEnum
{
    public interface expansion
    {
        public void expandTo( _enum _tailored, Object...keyValuePairs );
    }

    public static _macroEnum of( _enum _e )
    {
        return new _macroEnum( _e );
    }
    
    public _enum _prototype;
    
    
    /** method for originating the "tailored" _class */
    public _enumOriginator _originator;
    
    public interface _enumOriginator
    {
        public _enum initClass( Context context );
    }
    
    public _macroEnum( _enum _prototype )
    {
        this._prototype = _prototype;
    }
    
    public _enum expand( Object...keyValues )
    {
        return null;
    }
    
    public _enum expand( Context context )
    {
        return null;
    }    
}
