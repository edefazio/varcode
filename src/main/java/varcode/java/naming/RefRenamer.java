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
package varcode.java.naming;

/**
 * A more evolved String "replace" for replacing references within .java source code
 *
 * for instance a naive replace:
 * <PRE>
 * String s = "public class a{ public int bad = 100;}";
 * String rep = s.replace( "a", "XXX" );
 *
 *  //would produce :
 * "public claXXXss XXX{ public int bXXXd = 100; }"
 * </PRE> ...when what we REALLY want is this (just changing the name of the
 * class: "public class XXX{ public int bad = 100; }
 *
 * THIS IS PARTICULARLY USEFUL WHEN A REFERENCE NAME (TO BE REPLACED) IS
 * SOMETHING LIKE "i"... since I is very common
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class RefRenamer
{
    /**
     * Replaces the value within the 
     * @param code some Java code perhaps containing references
     * @param targetRefName
     * @param replaceRefName
     * @return the code with the References replaced
     */
    public static String apply( 
        String code, 
        String targetRefName, 
        String replaceRefName )
    {
        if( code == null )
        {
            return null;
        }
        int nextIndex = code.indexOf(targetRefName );
        StringBuilder sb = new StringBuilder();
        while( nextIndex >= 0 )
        {
            //System.out.println( "found at "+ nextIndex );
            boolean replaceIt = true;
            if( nextIndex > 0 )
            {   //check that the character BEFORE is not a valid Java identifier
                char charBefore = code.charAt( nextIndex - 1 );
                if( Character.isJavaIdentifierPart( charBefore ) )
                {   //whoops we matched 
                    replaceIt = false;
                }
            }
            if( replaceIt && nextIndex < code.length() - 1 )
            {   //check that the character AFTER is not a valid Java identifier
                //if( nextIndex + targetRefName.length() )
                if( code.length() > nextIndex + targetRefName.length() )
                {   
                    char charAfter = code.charAt( nextIndex + targetRefName.length() );
                    if( Character.isJavaIdentifierPart( charAfter ) )
                    {   //whoops we matched a part of an existing identifier
                        replaceIt = false;
                    }
                }
            }
            if( replaceIt )
            {
                //add stuff before the token
                sb.append( code.substring( 0, nextIndex ) );
                //add the new stering
                sb.append(replaceRefName );

                //consume/skip over the token     
                code = code.substring(nextIndex + targetRefName.length() );
                //find the next token
                nextIndex = code.indexOf(targetRefName );
            }
            else
            {
                //false alarm, move to the next index in the string                    
                nextIndex = code.indexOf(targetRefName, nextIndex + targetRefName.length() );
            }
        }
        sb.append( code );
        return sb.toString();
    }
}
