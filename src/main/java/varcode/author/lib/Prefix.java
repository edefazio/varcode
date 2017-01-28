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
package varcode.author.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.Collection;

import varcode.context.Context;
import varcode.author.AuthorState;
import varcode.author.PostProcessor;
import varcode.context.VarBindException;
import varcode.context.VarScript;

/**
 * Given a String, indents each line a number of spaces
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Prefix 
    implements VarScript, PostProcessor
{
    public static Prefix INDENT_4_SPACES = new Prefix( "    " );
    
    public static Prefix INDENT_8_SPACES = new Prefix( "        " );
    
    public static Prefix INDENT_12_SPACES = new Prefix( "            " );
    
    public static Prefix INDENT_16_SPACES = new Prefix( "                " );
	
    public static Prefix INDENT_TAB = new Prefix( '\t' + "" );
	
    public final String prefix;
	
    public Prefix( String prefix )
    {
	this.prefix = prefix;
    }
	
    @Override
    public void postProcess( AuthorState authorState ) 
    {
	String original = authorState.getTranslateBuffer().toString();
		
	authorState.getTranslateBuffer().replaceBuffer( doPrefix( original ).toString() );
	//tailorState.setTextBuffer( new FillBuffer(  doPrefix( original ) ) );			
    }

    public String prefixForms( Object input )
    {
	if( input == null )
	{
            return ""; 
	}
	if( input.getClass().isArray() )
	{
            StringBuilder sb = new StringBuilder();
            for( int i = 0; i < java.lang.reflect.Array.getLength( input ); i++ )
            {
                if( i > 0 )
		{
                    sb.append( System.lineSeparator() );
		}
		sb.append( prefixForms( Array.get( input, i ) ) );				
            }
            return sb.toString();
        }
	if( input instanceof Collection )
	{
            Collection<?> c = (Collection<?>)input;
            return prefixForms( c.toArray() );
	}
	return doPrefix( input.toString() ).toString();		
    }
	
    public StringBuilder doPrefix( String input )
    {
	if (input == null )
	{
            return new StringBuilder();
        }
	StringBuilder fb = new StringBuilder();
		
	BufferedReader br = new BufferedReader( 
            new StringReader( input ) );
		
	String line;
	try 
	{
            line = br.readLine();
            boolean firstLine = true;
            while( line != null )
            {
                if(! firstLine )
                {
                    fb.append( System.lineSeparator() );					
		}
		if( line.trim().length() > 0 )
                {
                    fb.append( prefix );
		}
		fb.append( line );
		firstLine = false;
		line = br.readLine();
            }
            return fb;
	} 
	catch( IOException e ) 
	{
            throw new VarBindException( "Error prefixing", e );
	}	
    }

    public String doPrefixObject( Object val )
    {
	if( val == null )
	{
            return null;
	}
	if( val instanceof String )
	{
            return doPrefix( (String)val ).toString();
	}
	if( val.getClass().isArray() )
	{
            StringBuilder sb = new StringBuilder();
            int len = Array.getLength( val );
            for( int i = 0; i < len; i++)
            {
		sb.append( doPrefixObject( Array.get( val, i ) ) ); 
            }
            return sb.toString();
	}
	if( val instanceof Collection )
	{			
            return doPrefixObject( ((Collection<?>)val).toArray( new Object[ 0 ] ) );
	}
	return doPrefixObject( val.toString() );
    }
	
    @Override
    public Object eval( Context context, String input ) 
    {
	Object val = context.resolveVar( input );
	if( val != null )
	{
            return doPrefixObject( val );
	}
	return null;		
    }
	
    @Override
    public String toString()
    {
	return this.getClass().getName() + " with \"" + prefix + "\"";
    }
}
