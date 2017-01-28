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
 * Translates a Java Class to be Serialized to be just a Simple name
 *
 * For Example:
 * <UL>
 * <LI> ClassToStringTranslator.INSTANCE.translate( int.class ); // = int
 * <LI> ClassToStringTranslator.INSTANCE.translate( String.class ); // = String
 * <LI> ClasstoStringTranslator.INSTANCE.translate( java.util.HashMap.class );
 * // = java.util.HashMap
 * <LI> ClassToStringTranslator.INSTANCE.translate( io.varcode.Lang.class ); //
 * = io.varcode.Lang
 * </UL>
 */
public enum ClassToStringTranslate
    implements Translator
{
    INSTANCE;

    @Override
    public Object translate( Object source )
    {
        if( source == null )
        {
            return "";
        }
        if( source instanceof Class )
        {
            Class<?> clazz = (Class<?>)source;

            if( clazz.isPrimitive() )
            //&& clazz.getPackage() != null	
            //&& clazz.getPackage().getName().equals( "java.lang" ) )
            {
                return clazz.getSimpleName();
            }
            return clazz.getCanonicalName();
        }
        return source;
    }
}
