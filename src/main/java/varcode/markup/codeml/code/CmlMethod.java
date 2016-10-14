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
package varcode.markup.codeml.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.FillInTheBlanks;
import varcode.dom.Dom;
import varcode.java.JavaMarkupRepo;
import varcode.java.code._code;
import varcode.java.code._methods._method;
import varcode.markup.codeml.CodeML;
import varcode.markup.repo.MarkupRepo;

/**
 * Extend this class, then provide a method and wrap the method in $ Marks $
    /*$*/
    // method in here 
    /*$*/ 

/* the method can contain CodeML Marks  
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class CmlMethod
{   
    /** Dom Compiled CodeML for the method signature*/
    private final Dom signature;
    
    /** Dom Compiled CodeML for the method body*/
    private final Dom body;
    
    /** The Tag to represent the start of the Markup and the "left gutter" */
    public static final String OPEN = "/*$*/";
    
    /** The Tag to represent the start of the Markup and the "left gutter" */
    public static final String CLOSE = "/*$*/";
    
    
    protected CmlMethod(  )
    {
        this( JavaMarkupRepo.INSTANCE );
    }
    
    protected CmlMethod( MarkupRepo markupRepo )
    {
        Class fileClass = null;
        Class c = getClass();
        if( c.isMemberClass() )
        {
            fileClass = c.getDeclaringClass();
        }
        else
        {
            fileClass = c;
        }
        MarkupRepo.MarkupStream ms = 
            //JavaMarkupRepo.INSTANCE.markupStream( getClass() );
            markupRepo.markupStream( fileClass.getCanonicalName()+ ".java" );    
        
        if( ms == null )
        {
            throw new VarException( 
                "source for \"" + fileClass.getCanonicalName() + ".java\" not found on classpath" );
        }
        String s = ms.asString();
        
        //System.out.println( "CLASS : " + getClass().getSimpleName() );
        int classIndex = s.indexOf( "class " + getClass().getSimpleName() );
        int startIndex = s.indexOf( OPEN, classIndex );
        int endIndex = s.indexOf(  CLOSE, startIndex + 1 );
        String inner = s.substring( startIndex + OPEN.length(), endIndex ).trim();
        
        //System.out.println( "INNER : " + inner );
        
        //we need to close the method arguments
        int closeParenIndex = inner.indexOf( ')' ); 
        
        int openBraceIndex = inner.indexOf( '{', closeParenIndex );
        String signatureString = inner.substring( 0, openBraceIndex );
        
        String bodyString = 
            inner.substring( openBraceIndex + 1, inner.lastIndexOf( '}' ) );
        
        //System.out.println( "SIGNATURE : " +signatureString );
        //System.out.println( "BODY      : " +bodyString );
        
        //this will convert $name$ to 
        signatureString = parseTemplate( signatureString.trim() );
        
        bodyString = parseTemplate( bodyString.trim() );
        
        //System.out.println( "SIGNATURE : " +signatureString );
        //System.out.println( "BODY      : " +bodyString );
        
        //now I need to retroactively Chan the $'se
        this.signature = CodeML.compile( signatureString.trim() );
        this.body = CodeML.compile( bodyString.trim() );
    }
    
    /** 
     * There is a BUG here because of the way FORMS are being Processed
     * ( we REVERESE ORDER fill the variables inside Form marks within
     * the Markup Document)... fix this later (this only existed for 
     * convenience anyways)
     * 
     * Inlines values into the signature and body Doms and returns the _method
     * @param values the values to be put into the method
     * @return the _method
     
    public _method inline( Object...values )
    {
        DocState ds = Compose.inlineToState( signature, values );
        String sig = ds.getTranslateBuffer().toString();
        DocState bodyDS = new DocState( body, ds.getContext() );
        Compose.toState( bodyDS );
        String bd = bodyDS.getTranslateBuffer().toString();
        return _method.of( sig, _code.of( bd ) );
    }
    */
    
    public _method compose( Object...keyValues )
    {
        VarContext vc = VarContext.of( keyValues );
        return compose( vc );
    }
        
    public Dom getSignatureDom()
    {
        return signature;
    }
    
    public Dom getBodyDom()
    {
        return body;
    }
    
    public _method compose( VarContext context )
    {
        String sigString = Compose.asString( signature, context );
        String bodyString = Compose.asString( body, context );
        
        return _method.of( sigString, _code.of( bodyString ) ); 
    }
    
    /**
     * Takes in a $Template 
     * (where "parameters" are signified by leading and trailing $'s, like:
     * 
     * "public $returnType$ get$Name$()" ($returnType$ and $Name$ are parameters)
     * ... and replaces them with CodeMLTags:
     */ 
     //public "/*{+returnType**/$returnType$/*+}*/ get/*{+$^(name)+}*/()"
    
     /* 
     * @param $template template with $'s demarcating parameters
     * @return the CodeML String
     */
    private static String parseTemplate( String $template )
    {
        BufferedReader sourceReader = 
            new BufferedReader( new StringReader( $template ) ); 
        
        String line = null;
        
        //these are the $parameters$ that exist in code
        List<String> $params = new ArrayList<String>();
        
        //this Template will replace $params$ with {+params+} 
        FillInTheBlanks.Builder $fillBuilder = new FillInTheBlanks.Builder();
        
        int lineNumber = 0;
        int prefixSpaces = -1;
        try
        {
            while( ( line = sourceReader.readLine() ) != null ) 
            {
                if( lineNumber > 0 )
                {
                    $fillBuilder.text( "\r\n" );
                    prefixSpaces = countPrefixSpaces( line, prefixSpaces );
                }
                parseLine( line, $fillBuilder, $params );
                lineNumber++;
            }
        
            FillInTheBlanks.FillTemplate template = $fillBuilder.compile(); 
        
            String formString = template.fill((Object[]) $params.toArray( new String[0] ) );
        
            //System.out.println( "PREFIX SPACES " + prefixSpaces );
            
            formString = normalizeFormPrefixSpaces( formString, prefixSpaces ); 
            
            return formString;
        }
        catch( IOException ioe )
        {
            throw new VarException( "Unable to parse template ", ioe );
        }
    }
    
    public static int countPrefixSpaces( String line, int currentPrefixSpaces )
    {
        int preSpaces = 0;
        for( int i = 0; i < line.length(); i++ )
        {
            if( Character.isWhitespace( line.charAt( i ) ) )
            {
                preSpaces++;
            }
            else
            {
                break;
            }
        }
        if( currentPrefixSpaces < 0 )
        {
            return preSpaces;
        }
        else
        {
            return Math.min( currentPrefixSpaces, preSpaces );
        }
    }
     
    protected static void parseLine( 
        String line, FillInTheBlanks.Builder fillBuilder, List<String> params )
    {
        int $openIndex = line.indexOf( '$' );
        if( $openIndex < 0 )
        {
            fillBuilder.text( line );
        }
        else 
        {
            int $closeIndex = line.indexOf( '$', $openIndex + 1 );  
            if( $closeIndex > $openIndex )
            {
                //add everything BEFORE the open
                fillBuilder.text( line.substring( 0, $openIndex ) );
                
                //add a blank
                fillBuilder.blank();
                String theTag = line.substring($openIndex, $closeIndex );
                
                //System.out.println( theTag );
                params.add( tagForToken( theTag ) );
                parseLine( line.substring( $closeIndex + 1 ), fillBuilder, params );                    
            }
            else
            {
                fillBuilder.text( line );
            }
        }                
    }
    
    private static String firstLower( String in )
    {
        return Character.toLowerCase( in.charAt( 0 ) ) + in.substring( 1 );
    }
    
    private static String tagForToken( String token )
    {
        if( Character.isUpperCase( token.charAt( 1 ) ) )
        {
            String tokenName = firstLower( token.substring( 1, token.length() ) );
            return "{+$^(" + tokenName + ")*+}";
        }
        else
        {
            String tokenName = token.substring(1, token.length() );
            return "{+" + tokenName + "+}";
            
        }
    }
    
    public static String normalizeFormPrefixSpaces( String formString, int prefixSpaces )
    {       
        try
        {
            LineNumberReader lnr = new LineNumberReader( 
                new StringReader( formString ) );
            
            
            String line = lnr.readLine();
            //System.out.println( "\""+ line +"\"" );
            StringBuilder normalized = new StringBuilder();
            boolean firstLine = true;
            while( line != null )
            {
                //we trimmed the first line of spaces
                if( !firstLine ) 
                {
                    //prefix a line break before the next line
                    normalized.append( "\r\n" ); 
                    normalized.append( line.substring( prefixSpaces ) );
                }
                else
                {
                    normalized.append( line );
                }
                line = lnr.readLine();
                firstLine = false;
            }
            return normalized.toString();
        }
        catch( IOException e )
        {
            throw new VarException( "Unable to read lines" );
        }
    }
}

