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
package varcode.markup.bindml;

import java.util.HashMap;
import java.util.Map;

import varcode.markup.form.Form;
import varcode.markup.MarkupException;
import varcode.context.VarNameAudit;
import varcode.markup.forml.ForML;
import varcode.markup.mark.AddForm;
import varcode.markup.mark.AddFormIfVar;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddIfVar;
import varcode.markup.mark.AddScriptResultIfVar;
import varcode.markup.mark.AddVar;
import varcode.markup.mark.Mark;
import varcode.markup.mark.RunScript;
import varcode.markup.mark.AuthorDirective;

/**
 * Reads markup and compiles marks "{+name+}" into {@link Mark}s 
 * (i.e. {@link AddVar}, {@link AddForm}, {@link AuthorDirective}
 *
 * NOTE: This is the standard usage for Javadoc /C, C++ "style" Marks.
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum BindMLCompiler
{
    ; //singleton enum idiom
    
    public static final VarNameAudit VARNAME_AUDIT = VarNameAudit.BASE;

    public static final String N = System.lineSeparator();

    //"{+",   "+}"  //Add               
    //"{+$",  "+}"  //AddScriptResult
    //"{+?",  "+}"  //AddIf
    //"{+?",  ")+}" //AddScriptResultIfVar
 
    //"{{+",  "+}}"  //AddForm(TBD)
    //"{{+?", "+}}"  //AddFormIf
    //"{_+",  "+_}" //AddForm(TBD)
    //"{_+?", "+_}" //AddFormIf    
    //"{$",   ")$}"  //RunScriptMark
    public static String firstOpenTag( String line )
    {
        int openBraceIndex = line.indexOf( '{' );

        //the open brace cant be the last char on the line
        while( openBraceIndex >= 0 && openBraceIndex < line.length() - 1 )
        {
            //if the NEXT character AFTER the '{' is a '+', '#', '-', '@' or '{'        
            switch( line.charAt( openBraceIndex + 1 ) )
            {
                case '+': // "{+", "{+?" "{+$" "{+("
                    return "{+";

                case '$': //"/*{$"
                    if( charIs( line, openBraceIndex + 2, '$' ) )
                    {
                        return "{$$"; //Directive
                    }
                    return "{$"; //RunScript                   
                case '{': // "{{"
                    // "{{+"  
                    // "{{+?"
                    if( charIs( line, openBraceIndex + 2, '+' ) )
                    {
                        return "{{+";
                    }
                    break;
                case '_': // "{_"
                    // "{_+"  
                    // "{_+?"                    
                    if( charIs( line, openBraceIndex + 2, '+' ) )
                    {
                        return "{_+";
                    }
                    break;
            }
            openBraceIndex = line.indexOf( '{', openBraceIndex + 1 );
        }
        return null;
    }

    private static final Map<String, String> OPENTAG_TO_CLOSETAG
        = new HashMap<String, String>();

    static
    {
        OPENTAG_TO_CLOSETAG.put( "{+", "+}" ); //Add               
        OPENTAG_TO_CLOSETAG.put( "{+$", "+}" ); //AddScriptResult
        OPENTAG_TO_CLOSETAG.put( "{+?", "+}" ); //Add --> AddIf, AddScriptResultIfVar

        OPENTAG_TO_CLOSETAG.put( "{{+", "+}}" ); //AddForm(TBD)
        OPENTAG_TO_CLOSETAG.put( "{{+?", "+}}" ); //AddFormIf

        OPENTAG_TO_CLOSETAG.put( "{_+", "+_}" ); // AddForm(TBD)
        OPENTAG_TO_CLOSETAG.put( "{_+?", "+_}" ); //AddFormIf
        
        OPENTAG_TO_CLOSETAG.put( "{$$", "$$}" ); //Directive
        OPENTAG_TO_CLOSETAG.put( "{$", ")$}" ); //RunScriptMark        
    }

    /**
     * 
     * @param openTag
     * @return 
     */
    public static String matchCloseTag( String openTag )
    {
        String matchingCloseTag = OPENTAG_TO_CLOSETAG.get( openTag );
        if( matchingCloseTag == null )
        {
            throw new MarkupException( 
                "No Matching close tag for Open tag \"" + openTag + "\"" );
        }
        return matchingCloseTag;
    }

    private static boolean charIs( String string, int index, char expect )
    {
        return string != null
            && string.length() > index
            && index > -1
            && string.charAt( index ) == expect;
    }

    //"{+",   "+}"  //Add               
    //"{+$",  "+}"  //AddScriptResult, ReplaceWithScript
    //"{+?",  "+}"  //Add --> AddIf
    //"{{+:",  "+}}"  //AddForm
    //"{{+?", "+}}"  //AddFormIf
    //"{_+:",  "+_}" //AddForm(Alt)
    //"{_+?", "+_}" //AddFormIf(Alt)
    //"{$$",  ")$$}" //AuthorDirective        
    public static Mark parseMark( String text )
    {
        return parseMark( text, -1 );
    }

    /**
     * Given Text return the appropriate Mark: <PRE>
     * "{+",     "+}"  ); //AddVar         
     * "{+$",    "+}"  ); //AddScriptResult
     * "{+?",    "+}"  ); //AddIfVar, AddScriptResultIfVar
     * "{{+",    "+}}" ); //AddForm
     * "{{+?",   "+}}" ); //AddFormIf
     * "{_+",    "+_}" ); //AddFormAlt
     * "{_+?",   "+_}" ); //AddFormIfAlt
     * "{$$",   ")$$}" ); //AuthorDirective
     * </PRE>
     * @param text the text of the entire mark
     * @param lineNumber the line number where the mark appears
     * @return the mark
     * @throws MarkupException if the Mark is invalid
     */
    public static Mark parseMark( String text, int lineNumber )
        throws MarkupException
    {
        if( text.startsWith( "{" ) )
        {
            //---------------------------------------            
            // "{+",     "+}"  ); //AddVar         
            // "{+$",    "+}"  ); //AddScriptResult
            // "{+?",    "+}"  ); //AddIfVar
            if( charIs( text, 1, '+' ) )
            {
                if( charIs( text, 2, '$' ) )
                {   // "{+$",    "+}"  ); //AddScriptResult
                    return AddScriptResultMark.of( text, lineNumber );
                }
                if( charIs( text, 2, '?' ) )
                {
                    /* MED ADDED *///ends with ")+}"
                    if( text.endsWith( AddScriptResultIfVarMark.CLOSE_TAG )
                        && text.contains( AddScriptResultIfVarMark.MID_TAG ) )
                    {
                        return AddScriptResultIfVarMark.of( text, lineNumber );
                    }
                    return AddIfVarMark.of( text, lineNumber );
                }
                return AddVarMark.of( text, lineNumber );
            }
            // "{{+",     "+}}" ); //AddForm
            // "{{+?",    "+}}" ); //AddFormIf
            if( text.substring( 1, 3 ).equals( "{+" ) )
            {
                if( charIs( text, 3, '?' ) )
                {
                    return AddFormIfVarMark.of( text, lineNumber );
                }
                return AddFormMark.of( text, lineNumber); //, forMLCompiler );
            }
            // "{_+",     "+_}" ); //AddForm
            // "{_+?",    "+_}" ); //AddFormIf
            if( text.substring( 1, 3 ).equals( "_+" ) )
            {
                if( charIs( text, 3, '?' ) )
                {
                    return AddFormIfVarMark_Alt.of( text, lineNumber);
                }
                return AddFormMark_Alt.of( text, lineNumber );
            }            
            // "{$",     ")}"  // ScriptMark 
            if( charIs( text, 1, '$' ) )
            {
                if( charIs( text, 2, '$' ) ) //  {$$directive()$$}  
                {
                    return AuthorDirectiveMark.of( text, lineNumber );
                }
                return RunScriptMark.of( text, lineNumber );
            }
        }
        throw new MarkupException(
            "Could not find Open Tag matching Mark :" + N + text + N
            + "on line [" + lineNumber + "]", text, lineNumber );
    }

    private static String[] nameDefault( String tag )
    {
        int tagIndex = tag.indexOf( '|' );
        String[] nameDefault = { tag };
        if( tagIndex > 0 && tagIndex < tag.length() - 1 )
        {
            nameDefault = new String[]
                { tag.substring( 0, tagIndex ), tag.substring( tagIndex + 1 ) };
        }
        return nameDefault;
    }

    // {+name}
    // {+name|eric}
    // {+name+}    
    // {+name|eric+}
    public static class AddVarMark
    {
        public static final String OPEN_TAG = "{+";
        public static final String CLOSE_TAG = "+}";

        public static AddVar of( String text )
        {
            return of( text, -1 );
        }

        public static AddVar of( String text, int lineNumber )
            throws MarkupException
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException( "Invalid AddVar : " + N
                    + text + N + "  ... on line ["
                    + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ", text, lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( "Invalid AddVar : " + N
                    + text + N + "  ... on line ["
                    + lineNumber + "] must end with CLOSE MARK"
                    + " \"" + CLOSE_TAG + "\" ", text, lineNumber );
            }
            String tag = text.substring(
                OPEN_TAG.length(),
                text.indexOf( CLOSE_TAG ) );

            String[] nameDefault = nameDefault( tag );

            String name = null;
            boolean isRequired = false;
            String defaultValue = null;

            if( nameDefault[ 0 ].endsWith( "*" ) )
            {
                name = nameDefault[ 0 ].substring( 0, nameDefault[ 0 ].length() - 1 );
                isRequired = true;
            }
            else
            {
                name = nameDefault[ 0 ];
                isRequired = false;
            }
            if( nameDefault.length == 2 )
            {   //they specified a default
                defaultValue = nameDefault[ 1 ];
            }
            else
            {
                defaultValue = null;
            }
            try
            {
                VARNAME_AUDIT.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddVar Name \"" + name + "\", on line ["
                    + lineNumber + "] ", text, lineNumber, e );
            }

            return new AddVar( text, lineNumber, name, isRequired, defaultValue );
        }
    }

    public static class AddFormMark
    {
        /** Opening mark for a AddForm*/
        public static final String OPEN_TAG = "{{+:";

        /** Closing mark for a AddForm */
        public static final String CLOSE_TAG = "+}}";

        public static AddForm of( String text, int lineNumber )
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must start "
                    + "with open mark \"" + OPEN_TAG + "\" ",
                    text,
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text,
                    lineNumber );
            }
            String formText
                = text.substring(
                    OPEN_TAG.length(),
                    text.length() - CLOSE_TAG.length() );

            boolean isRequired = false;
            if( formText.endsWith( "*" ) ) //is it "required?"
            {
                formText = formText.substring( 0, formText.length() - 1 );
                isRequired = true;
            }
            try
            {
                Form form = ForML.compile( lineNumber, formText );
                return new AddForm( text, lineNumber, isRequired, form );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark: " + N + text + N
                    + "on line [" + lineNumber + "], form :" + N
                    + formText + N + "...is invalid",
                    text,
                    lineNumber,
                    t );
            }
        }
    }

    public static class AddFormMark_Alt
    {
        /** Open tag for a AddForm */
        public static final String OPEN_TAG = "{_+:";

        /** Closing tag for a AddForm */
        public static final String CLOSE_TAG = "+_}";

        public static AddForm of( String text, int lineNumber)
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must start "
                    + "with open mark \"" + OPEN_TAG + "\" ",
                    text,
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text,
                    lineNumber );
            }
            String formText
                = text.substring( 
                    OPEN_TAG.length(), 
                    text.length() - CLOSE_TAG.length() );
            try
            {
                Form form = ForML.compile( lineNumber, formText );
                return new AddForm( text, lineNumber, false, form ); //NEVER required
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark: " + N + text + N
                    + "on line [" + lineNumber + "], form :" + N
                    + formText + N + "...is invalid",
                    text,
                    lineNumber,
                    t );
            }
        }
    }

    /**
     * An "alternative" tag AddFormIfMark
     */
    public static class AddFormIfVarMark_Alt
    {
        /** Opening mark for a IfAddForm */
        public static final String OPEN_TAG = "{_+?";

        /** Closing mark for a IfAddForm */
        public static final String CLOSE_TAG = "+_}";

        public static AddFormIfVar of( String text, int lineNumber ) 
        {
            String name;
            Form form;
            String targetValue;

            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber
                    + "], must start with open mark \"" + OPEN_TAG + "\" and end with \"" + CLOSE_TAG + "\"",
                    text,
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text,
                    lineNumber );
            }
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar " + N + text + N + " on line ["
                    + lineNumber + "], must contain ':' separating condition from code",
                    text,
                    lineNumber );
            }

            String nameEquals = text.substring( OPEN_TAG.length(), colonIndex ); //parseNameEquals( this.text, lineNumber );

            int equalsIndex = nameEquals.indexOf( "==" );
            if( equalsIndex > -1 )
            {
                name = nameEquals.substring( 0, equalsIndex ).trim();

                targetValue = nameEquals.substring(
                    equalsIndex + 2,
                    nameEquals.length() );
            }
            else
            {
                equalsIndex = nameEquals.indexOf( "=" );
                if( equalsIndex > -1 )
                {
                    name = nameEquals.substring( 0, equalsIndex ).trim();

                    targetValue = nameEquals.substring(
                        equalsIndex + 1,
                        nameEquals.length() );
                }
                else
                {
                    name = nameEquals.trim();
                    targetValue = null;
                }
            }

            //"/*{{?ifAddCode:" + "/+** {+javadocComment} *+/ /+* {+comment} *+/" + "}}*/");
            String formText
                = text.substring(
                    colonIndex + 1,
                    text.length() - CLOSE_TAG.length() );

            try
            {
                form = ForML.compile( lineNumber, formText );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar:" + N + text + N
                    + "on line [" + lineNumber + "]"
                    + "the form : " + N
                    + formText + N
                    + "is invalid",
                    text,
                    lineNumber,
                    t );
            }
            try
            {
                VARNAME_AUDIT.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar name \"" + name
                    + "\" on line [" + lineNumber + "]",
                    text,
                    lineNumber, e );
            }
            return new AddFormIfVar( text, lineNumber, name, targetValue, form );
        }
    }

    public static class AddFormIfVarMark
    {
        /** Opening mark for a IfAddForm */
        public static final String OPEN_TAG = "{{+?";

        /** Closing mark for a IfAddForm */
        public static final String CLOSE_TAG = "+}}";

        public static AddFormIfVar of( String text, int lineNumber ) 
        {
            String name;
            Form form;
            String targetValue;

            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber
                    + "], must start with open mark \"" + OPEN_TAG + "\" and end with \"" + CLOSE_TAG + "\"",
                    text,
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text,
                    lineNumber );
            }
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar " + N + text + N + " on line ["
                    + lineNumber + "], must contain ':' separating condition from code",
                    text,
                    lineNumber );
            }

            String nameEquals = text.substring( OPEN_TAG.length(), colonIndex ); //parseNameEquals( this.text, lineNumber );

            int equalsIndex = nameEquals.indexOf( "==" );
            if( equalsIndex > -1 )
            {
                name = nameEquals.substring( 0, equalsIndex ).trim();

                targetValue = nameEquals.substring(
                    equalsIndex + 2,
                    nameEquals.length() );
            }
            else
            {
                equalsIndex = nameEquals.indexOf( "=" );
                if( equalsIndex > -1 )
                {
                    name = nameEquals.substring( 0, equalsIndex ).trim();

                    targetValue = nameEquals.substring(
                        equalsIndex + 1,
                        nameEquals.length() );
                }
                else
                {
                    name = nameEquals.trim();
                    targetValue = null;
                }
            }

            //"/*{{?ifAddCode:" + "/+** {+javadocComment} *+/ /+* {+comment} *+/" + "}}*/");
            String formText
                = text.substring(
                    colonIndex + 1,
                    text.length() - CLOSE_TAG.length() );

            try
            {
                form = ForML.compile( lineNumber, formText );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar:" + N + text + N
                    + "on line [" + lineNumber + "]"
                    + "the form : " + N
                    + formText + N
                    + "is invalid",
                    text,
                    lineNumber,
                    t );
            }
            try
            {
                VARNAME_AUDIT.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar name \"" + name
                    + "\" on line [" + lineNumber + "]",
                    text,
                    lineNumber, e );
            }
            return new AddFormIfVar( text, lineNumber, name, targetValue, form );
        }
    }

    /**
     * {@link varcode.markup.mark.AddIfVar}
     */
    public static class AddIfVarMark
    {
        /** Opening mark for a AddIf */
        public static final String OPEN_TAG = "{+?";

        /** Closing mark for a AddIf */
        public static final String CLOSE_TAG = "+}";

        public static AddIfVar of( String text, int lineNumber )
        {
            return of( text, lineNumber, VarNameAudit.BASE );
        }

        public static AddIfVar of(
            String text, int lineNumber, VarNameAudit nameAudit )
        {
            String name = null;
            String targetValue = null;
            String code = null;

            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddIfVar mark on [" + lineNumber + "], must start "
                    + "with open mark \"" + OPEN_TAG + "\" ",
                    text,
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddIfVar on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text,
                    lineNumber );
            }
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddIfVar " + N + text + " on line ["
                    + lineNumber + "], Mark must contain ':' separating condition"
                    + "from code",
                    text,
                    lineNumber );
            }
            String condition = text.substring( OPEN_TAG.length(), colonIndex );
            //String nameEquals = parseNameEquals( this.text, lineNumber );

            int equalsIndex = condition.indexOf( "==" );
            if( equalsIndex > -1 )
            {
                name = condition.substring( 0, equalsIndex ).trim();

                targetValue = condition.substring(
                    equalsIndex + 2,
                    condition.length() );
            }
            else
            {
                equalsIndex = condition.indexOf( "=" );
                if( equalsIndex > -1 )
                {
                    name = condition.substring( 0, equalsIndex ).trim();

                    targetValue = condition.substring(
                        equalsIndex + 1,
                        condition.length() );
                }
                else
                {
                    name = condition.trim();
                    targetValue = null;
                }
            }
            //basically everything AFTER : and before close MARK
            //TODO TEST THIS
            code = text.substring(
                colonIndex + 1,
                text.length() - CLOSE_TAG.length() );
            try
            {
                nameAudit.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddIfVar name \"" + name
                    + "\" on line [" + lineNumber + "]",
                    text,
                    lineNumber );
            }
            return new AddIfVar( text, lineNumber, name, targetValue, code );
        }
    }

    /**
     * IF a var is bound and is not null, then write the result of the script
     * to the document <CODE><BR>
     * "{+?var:$>(var)+}" //if( var is bound and non-null 
     *                    //call > (indent) script and write on var 
     * </CODE>
     * 
     * IF a var is bound to a target value, then write the result of
     * the script to the document:<CODE><BR>
     * 
     * "{+?var==3:$>(var)+}" //if ( var is bound to a value that represents "3"
     *                       //call > (indent) script and write on var       
     * </CODE>
     * STARTS WITH "{+?" CONTAINS ":$" ENDS WITH ")+}"
     */
    public static class AddScriptResultIfVarMark
    {
        public static final String OPEN_TAG = "{+?";
        public static final String MID_TAG = ":$";
        public static final String CLOSE_TAG = ")+}";

        public static AddScriptResultIfVar of( String text, int lineNumber )
            throws MarkupException
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultIfVarMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text,
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultIfVarMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must end with CLOSE MARK"
                    + " \"" + CLOSE_TAG + "\" ",
                    text,
                    lineNumber );
            }
            int midIndex = text.indexOf( MID_TAG );
            if( midIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultIfVarMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must contain INTERIOR"
                    + " \"" + MID_TAG + "\" ",
                    text,
                    lineNumber );
            }
            int openParamIndex = text.lastIndexOf( '(' );

            if( openParamIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultIfVarMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must contain (",
                    text,
                    lineNumber );
            }

            // {+?var:$>(var)+}
            // ---   -- -   --- 
            // ---              OPEN_TAG
            //       --         MID_TAG
            //          -       OPEN_PARAM_INDEX            
            //              --- CLOSE_TAG 
            // {+?var=true:$>(var)+}
            // ---        -- -   --- 
            // ---                   OPEN_TAG         "{+?"
            //            --         MID_TAG          ":$"
            //              -        SCRIPT NAME      ">"
            //               -       OPEN_PARAM_INDEX "("            
            //                  ---  CLOSE_TAG        ")+}"
            String expression = text.substring( OPEN_TAG.length(), midIndex );
            String scriptName = text.substring( midIndex + MID_TAG.length(), openParamIndex );

            int eqIndex = expression.indexOf( "=" );
            String varName = null;
            String targetValue = null;
            if( eqIndex > 0 )
            {
                varName = expression.substring( 0, expression.indexOf( "=" ) ).trim();
                targetValue = expression.substring( expression.lastIndexOf( "=" ) + 1 ).trim();
            }
            else
            {
                varName = expression.trim();
            }
            String paramContents
                = text.substring( openParamIndex + 1, text.length() - CLOSE_TAG.length() );

            return new AddScriptResultIfVar(
                text,
                lineNumber,
                varName,
                targetValue,
                scriptName,
                paramContents );
        }
    }

    public static class AddScriptResultMark
    {
        public static final String OPEN_TAG = "{+$";
        public static final String CLOSE_TAG = "+}";

        public static AddScriptResult of( String text, int lineNumber )
            throws MarkupException
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text,
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must end with CLOSE MARK"
                    + " \"" + CLOSE_TAG + "\" ",
                    text,
                    lineNumber );
            }

            int openParamIndex = text.indexOf( '(' );
            int closeParamIndex = text.lastIndexOf( ')' );

            //they MIGHT not have Open and close param indexes
            //if not, then we'll add them in
            if( ( openParamIndex < 0 )&& (closeParamIndex < 0 ) )
            {
                text = text + "()"; //lets add it in there
                openParamIndex = text.indexOf( '(' );
                closeParamIndex = text.lastIndexOf( ')' );                
            }
            // "{+$stripSpaces(f)+}"            
            String scriptName = text.substring( OPEN_TAG.length(), openParamIndex );
            // parameters = null; 
            if( openParamIndex < 0 || closeParamIndex < 0 )
            {
                throw new MarkupException(
                    "AddScriptResult Mark : " + N
                    + text + N
                    + " with name \"" + scriptName
                    + "\" on line [" + lineNumber
                    + "] must have a '(' and ')' to demarcate parameters ",
                    text,
                    lineNumber );
            }
            if( openParamIndex > closeParamIndex )
            {
                throw new MarkupException(
                    "AddScriptResultMark with script name \"" + scriptName
                    + "\" on line [" + lineNumber
                    + "] must have a '(' BEFORE ')' to demarcate parameters",
                    text, lineNumber );
            }
            String paramContents
                = text.substring( openParamIndex + 1, closeParamIndex );

            boolean isRequired = false;
            if( charIs( text, closeParamIndex + 1, '*' ) )
            {
                isRequired = true;
            }
            return new AddScriptResult(
                text,
                lineNumber,
                scriptName,
                paramContents,
                isRequired );
        }
    }

    /**
     * Runs a Script (doesn't print anything to the document)
     * an example would be a script that does input validation
     * 
     * For example:
     * "{$validateContext(input)$}"
     * 
     * (it will fail and stop if the data supplied is invalid for the document,
     * or succeed and proceed with creating the document
     */
    public static class RunScriptMark
    {
        public static final String OPEN_TAG = "{$";

        public static final String CLOSE_TAG = "$}";

        public static RunScript of( String markText, int lineNumber )
        {
            String scriptName = null;
            String input = null;

            if( !markText.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N
                    + "... does not start with \"" + OPEN_TAG + "\"",
                    markText, lineNumber );
            }

            if( !markText.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N
                    + "... does not end with \"" + CLOSE_TAG + "\"",
                    markText, lineNumber );
            }
            int indexOfOpenParen = markText.indexOf( '(' );
            if( indexOfOpenParen < 0 )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N
                    + "... does not contain a requisite '(' ",
                    markText, lineNumber );
            }
            int indexOfCloseParen = markText.indexOf( ')', indexOfOpenParen );
            if( indexOfCloseParen < 0 )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N
                    + "... does not contain a requisite ')' ",
                    markText, lineNumber );
            }
            scriptName = markText.substring( OPEN_TAG.length(), indexOfOpenParen );

            input = markText.substring( indexOfOpenParen + 1, indexOfCloseParen );

            boolean isRequired = false;
            if( charIs( markText, indexOfCloseParen + 1, '*' ) )
            {
                isRequired = true;
            }
            return new RunScript( markText, lineNumber, scriptName, input, isRequired );
        }
    }

    /*{$$directive()$$}*/
    /*{$$directive$$}*/
    public static class AuthorDirectiveMark
    {
        public static final String OPEN_TAG = "{$$";

        public static final String CLOSE_TAG = "$$}";

        public static AuthorDirective of(
            String text, int lineNumber )
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Directive mark:" + N + text + N + " does not start with \""
                    + OPEN_TAG + "\" on line [" + lineNumber + "]", text, lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Directive mark: " + N + text + N
                    + " does not end with \"" + CLOSE_TAG + "\" on line ["
                    + lineNumber + "]", text, lineNumber );
            }
            String tag = text.substring(
                OPEN_TAG.length(),
                text.length() - CLOSE_TAG.length() );
            if( tag.endsWith( "()" ) )
            {
                tag = tag.substring( 0, tag.length() - 2 );
            }

            String directiveName = tag;
            return new AuthorDirective( text, lineNumber, directiveName ); //, input, isRequired );
        }
    }
}
