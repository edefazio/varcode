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
package varcode.java.macro;

import varcode.java.lang._code;

/**
 * Adds some (static) code at the beginning of a method
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoMethodCodePrefix
    implements JavaMacro
{    
    /*the code to add at the "prefix" (before any other code) of the method */    
    public _code codePrefix;
    
    //_transposeCode
    
    public _autoMethodCodePrefix ( _code codePrefix )
    {
        this.codePrefix = codePrefix;
    }
}
