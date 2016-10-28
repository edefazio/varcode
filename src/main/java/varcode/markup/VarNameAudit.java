/*
 * Copyright 2015 M. Eric DeFazio.
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
package varcode.markup;

import varcode.context.eval.Eval_JavaScript;

/**
 * Audits Var names that occur in {@code Markup}. 
 * i.e. <PRE>
 * BindML.compile("{+type+}{+name+} = {+value+}");
 *                   ^^^^    ^^^^       ^^^^^
 *                   var names in BindML markup</PRE>
 *                     
 *   
 * Used when {@code Markup} is being read/ parsed / compiled. 
 * 
 * Provides a "default" implementation BASE.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface VarNameAudit
{
	/** a base class for auditing var names in markup*/ 
    public static final StandardVarName BASE = 
    	StandardVarName.INSTANCE;

    /** verifies that the name used for identifying an entity is valid */
    public String audit( String varName )
        throws MarkupException;

    /** 
     * validates that the String/VarName represents a Standard Identifier in 
     * most programming languages */
    public enum StandardVarName
        implements VarNameAudit
    {
        INSTANCE;

        public String audit( String varName )
            throws MarkupException
        { //first verify it is not empty or null             
            if( varName == null )
            {
                throw new MarkupException( "null var name not allowed" );
            }
            if( varName.trim().length() == 0 )
            {
                throw new MarkupException( "trimmed to empty var name not allowed" );
            }
            try
            {   //we validate names based on standard Java identifiers
                return validateName( varName );
            }
            catch( MarkupException ivc )
            {
                throw new MarkupException( 
                	" \"" + varName + "\" is invalid for a standard identifier",
                    ivc );
            }
        }

        public static String validateName( String varName )
            throws MarkupException
        {
            if( ( varName == null ) || varName.length() < 1 )
            {
                throw new MarkupException( "var names must be at least 1 character" );
            }
            if( !Character.isJavaIdentifierStart( varName.charAt( 0 ) ) )
            {
                throw new MarkupException(
                    "var names must start with a letter{a-z, A-Z} " + "or '_', '$'" );
            }
            for( int i = 1; i < varName.length(); i++ )
            {
                if( !Character.isJavaIdentifierPart( varName.charAt( i ) ) )
                {
                    throw new MarkupException( "var name cannot contain the character '"
                        + varName.charAt( i ) + "' at [" + i + "]" );
                }
            }
            if( Eval_JavaScript.RESERVED_WORDS.contains( varName ) )
            {
                throw new MarkupException( 
                	"var name \"" + varName + "\" is a reserved word" );
            }
            return varName;
        }
        
        public String toString()
        {
        	return this.getClass().getName();
        }
    }
}