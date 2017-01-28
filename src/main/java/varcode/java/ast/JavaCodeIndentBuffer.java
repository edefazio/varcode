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
package varcode.java.ast;

/**
 * A simple "Buffer" built specifically for writing Java source code
 * that maintains an indentation level. (So in addition to storing the 
 * data in an internal "buffer", we also store an indentation level)
 * 
 * NOTE: this code was ripped/ modified from the gitHub JavaParser
 * API and adapted (it's pretty much the same code as 
 * @author Julio Vilmar Gessers)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class JavaCodeIndentBuffer
{
    private final String indentation;

    private int level = 0;

    private boolean indented = false;

    private final StringBuilder buf = new StringBuilder();
    
    public JavaCodeIndentBuffer( final String indentation )
    {
        this.indentation = indentation;
    }
    
    public void indent()
    {
        level++;
    }

    public void unindent()
    {
        level--;
    }

    private void makeIndent()
    {
        for( int i = 0; i < level; i++ )
        {
            buf.append( indentation );
        }
    }

    public void print( final String arg )
    {
        if( !indented )
        {
            makeIndent();
            indented = true;
        }
        buf.append( arg );
    }

    public void printLn( final String arg )
    {
        print( arg );
        printLn();
    }

    public void printLn()
    {
        buf.append( System.getProperty( "line.separator" ) );
        indented = false;
    }

    public String getSource()
    {
        return buf.toString();
    }

    @Override
    public String toString()
    {
        return getSource();
    }
}
