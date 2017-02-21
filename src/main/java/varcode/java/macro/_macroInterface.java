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
import varcode.java.model._interface;

/**
 *
 * @author Eric
 */
public class _macroInterface
{
    public interface expansion
    {
        public void expandTo( _interface _tailored, Object...keyValuePairs );
    }
    
    public static _macroInterface of( _interface _i )
    {
        return new _macroInterface( _i );
    }
    
    public _interface _prototype;
    
    public _macroInterface( _interface _prototype )
    {
        this._prototype = _prototype;
    }
    
    
    public _interface expand( Context context )
    {
        return null;
    }    
}
