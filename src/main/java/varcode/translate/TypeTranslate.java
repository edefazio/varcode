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
package varcode.translate;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

/**
 * Translates java.lang.reflect.Types to Strings as they are represented in 
 * .java source code
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum TypeTranslate
    implements Translator
{
    INSTANCE;

    @Override
    public Object translate( Object source )
    {

        if( source instanceof AnnotatedType )
        {
            AnnotatedType t = (AnnotatedType)source;
            return t.getType().getTypeName();
        }

        if( source instanceof Type )
        {
            return ((Type)source).getTypeName();
        }
        return source;
    }
}
