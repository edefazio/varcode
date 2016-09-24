/*
 * Copyright 2016 eric.
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
package varcode.doc.translate;

import java.lang.reflect.Array;

/**
 *
 * Translates Java Elements (Classes, primitives, literals, arrays) 
 * to appropriate String representations for code.
 * 
 * this implementation ALSO handles JSArrays 
 * (Arrays created in nashorn Javascript returned to Java)
 * 
 * for instance, if we pass the
 * JavaElementTranslate jet = new JavaElementTranslate();
 * <PRE>
 * String translated = jet.translate( 'a' );
 *                     "'a'"; 
 * 
 * String translated = jet.translate( Math.PI );
 *                     "3.14159d"; <-- Note the d POSTFIX
 * 
 * String translated = jet.translate( 1.0f );
 *                     "1.0F"; <-- Note the F POSTFIX
 * </PRE>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 * 
 */
public class JavaTranslate
    implements Translator
{
    public static final JavaTranslate INSTANCE = new JavaTranslate();

    private final Translator[] translators = new Translator[] 
    {
        ClassToStringTranslate.INSTANCE,
        CollectionToArrayTranslate.INSTANCE,
        JSArrayToArrayTranslate.INSTANCE
    };
    
    /**
     * Sometimes you JUST want to translate WITHOUT adding
     * 
     * @param input the input
     * @return the translated String
     */
    @Override
    public String translate( Object input )
    {
    	if( input == null )
        {
    		return "";
    	}
    	if( input instanceof CharSequence )
    	{
    		return ((CharSequence)input).toString();    		
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	
    	if( input.getClass().isArray() )
        {
    		int len = Array.getLength( input );
         			
         	for( int i = 0; i < len; i++ )
         	{
         		if( i > 0 )
         		{
         			sb.append( ", " );
         		}
         		Object o = Array.get( input, i );
                Object translated = o;
             	for( int j = 0; j < this.translators.length; j++ )
             	{
             		translated = this.translators[ j ].translate( translated );
             	}
         		sb.append( translated ); 
         	}
         	return sb.toString();
        }
    	Object translated = input;
    	for( int i = 0; i < this.translators.length; i++ )
    	{
    		translated = this.translators[ i ].translate( translated );
    	}
    	
    	//Object translated = translate( input );
    	if( !( translated.equals( input ) ) && !( translated instanceof String ) )
    	{   //it was translated, but not into a String, might need further translation
    		return translate( translated );
    	}
    	//if there was no translation, just append
    	sb.append( translated );
        return sb.toString();
    }   
}
