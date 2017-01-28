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
package varcode.markup;

import varcode.VarException;

/**
 * A Syntax error in the input String Markup (i.e. a malformed {@link varcode.markup.mark.Mark}).
 *
 * Failure that occurs when the Markup source is being read in / parsed
 * (Synonymous with a "Compile Time" exception when reading a source file).
 * <BR><BR>
 * 
 * Attempt to Read/Parse/Compile a {@code VarCode} from an input source failed.
 */
public class MarkupException
    extends VarException
{
    private static final long serialVersionUID = 4145424684335838684L;

    public String markText = null;

    public int lineNumber = -1;

    public static String N = "\r\n";

    public MarkupException( String message, Throwable throwable )
    {
        super( message, throwable );
    }

    public MarkupException(
        String message, String markText, int lineNumber )
    {
        super( message + N + markText + N + "on line [" + lineNumber + "]" );
        this.markText = markText;
        this.lineNumber = lineNumber;
    }

    public MarkupException(
        String message, String markText, int lineNumber, Throwable throwable )
    {
        super( message, throwable );
        this.markText = markText;
        this.lineNumber = lineNumber;
    }

    public MarkupException( String message )
    {
        super( message );
    }

    public MarkupException( Throwable throwable )
    {
        super( throwable );
    }
}
