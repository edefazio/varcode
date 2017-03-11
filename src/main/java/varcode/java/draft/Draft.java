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
package varcode.java.draft;

import varcode.java.model._class;
import varcode.java.model._enum;

/**
 *
 * @author Eric
 */
public class Draft
{
    
    public static Object instanceOf( Class draftClass, Object...keyValuePairs )
    {
        return _classOf( draftClass, keyValuePairs ).instance(  );
    }
        
    public static _enum _enumOf( Class draftClass, Object...keyValueParis )
    {
        _draftEnum _dc = _draftEnum.of( draftClass );
        return _dc.draft( keyValueParis );
    }
    
    public static _class _classOf( Class draftClass, Object...keyValueParis )
    {
        _draftClass _dc = _draftClass.of( draftClass );
        return _dc.draft( keyValueParis );
    }
    
    public static Class classOf( Class draftClass, Object...keyValuePairs )
    {
        if( draftClass.isEnum() )
        {
            _draftEnum _de = _draftEnum.of( draftClass );
            _enum _e = _de.draft( keyValuePairs );
            return _e.loadClass();
        }
        return _classOf( draftClass, keyValuePairs ).loadClass();        
    }
}
