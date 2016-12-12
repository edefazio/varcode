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

import varcode.java.lang._class;
import varcode.java.lang._code;

/**
 *
 * @author Eric
 * @
 */
public class _autoJavadoc 
     implements JavaMacro.Generator, JavaMacro.Mutator
{
    //make methods to pass in Javadoc @author
    public static _class to( _class _c )
    {
        return to( new _docProperties(), _c );
    }
    
    
    private static String emptyIfNull( Object value )
    {
        if( value == null )
        {
            return "";
        }
        return value.toString();
    }
     
    public static _class to( _docProperties dp, _class _c )
    {
        if( _c.getJavadoc().isEmpty() )
        {
            _c.javadoc(     
                "@author " + emptyIfNull( dp.author ),  
                "@version " + emptyIfNull( dp.version ),
                "@since " + emptyIfNull( dp.since ) );
        }
        _c = _autoJavadocMethod.ofClass( _c );
        return _c;
    }
    
    public static class _docProperties
    {
        public Object author;
        public Object version;
        public Object since;
    }
    
}
