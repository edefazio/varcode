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

/**
 * Understands the form for primitive literals within .java source code
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class PrimitiveTranslate
    implements Translator
{
    @Override
    public Object translate( Object source )
    {
        return translateFrom( source );
    }
    
    public static Object translateFrom( Object source )
    {
        if( source instanceof Number )
        {            
        }
        if( Character.class.isAssignableFrom( source.getClass() ) )
        {
            return "'" + source.toString() + "'";
        }
        if( Long.class.isAssignableFrom( source.getClass() ) )
        {
            return source.toString() + "L";
        }
        if( Integer.class.isAssignableFrom( source.getClass() ) )
        {
            return source.toString();
        }
        if( Float.class.isAssignableFrom( source.getClass() ) )
        {
            return source.toString()+ "f";
        }
        if( Double.class.isAssignableFrom( source.getClass() ) )
        {
            return source.toString()+ "d";
        }
        if( Byte.class.isAssignableFrom( source.getClass() ) )
        {
            return "(byte)"+source.toString();
        }
        if( Short.class.isAssignableFrom( source.getClass() ) )
        {
            return "(short)"+source.toString();
        }
        return source;
    }

}
