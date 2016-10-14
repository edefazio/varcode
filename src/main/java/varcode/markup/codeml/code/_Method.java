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

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.dom.Dom;
import varcode.java.JavaMarkupRepo;
import varcode.java.code._code;
import varcode.java.code._methods._method;
import varcode.markup.codeml.CodeML;
import varcode.markup.repo.MarkupRepo;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 * Extend this class, then provide a method and wrap the method in $ Marks $
    /*$*/
    // method in here 
    /*$*/ 

/* the method can contain CodeML Marks  
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class _Method
{   
    /** Dom Compiled CodeML for the method signature*/
    private final Dom signatureDom;
    
    /** Dom Compiled CodeML for the method body*/
    private final Dom bodyDom;
    
    /** Dom compiled CodeML from the comment contents of the method */
    private final Dom javadocDom;
    

    
    
    protected _Method(  )
    {
        this( JavaMarkupRepo.INSTANCE );
    }
  
    /** Load ".java" Source Code for the class from the MarkupRepo */
    private static MarkupStream loadSourceForClass( 
        Class markupClass, MarkupRepo markupRepo )
    {
        Class fileClass = markupClass;
        
        if( fileClass.isMemberClass() )
        {
            fileClass = fileClass.getDeclaringClass();
        }
        
        MarkupStream sourceCodeStream = 
            markupRepo.markupStream( fileClass.getCanonicalName()+ ".java" );    
        
        if( sourceCodeStream == null )
        {
            throw new VarException( 
                "source for \"" + fileClass.getCanonicalName() 
              + ".java\" not found in Repo: " + markupRepo.describe() );
        }
        return sourceCodeStream;        
    }
    


    protected _Method( MarkupRepo markupRepo )
    {        
        MarkupStream sourceCodeStream = 
            loadSourceForClass( getClass(), markupRepo );
        
        String allSourceCode = sourceCodeStream.asString();
        String inner = _Parse.extractContentIn$$(getClass(), allSourceCode );
        /*
        //NOW, within the stream, parse out the 
        int classIndex = s.indexOf( "class " + getClass().getSimpleName() );
        int startIndex = s.indexOf( OPEN, classIndex );
        int endIndex = s.indexOf(  CLOSE, startIndex + 1 );
        String inner = s.substring( startIndex + OPEN.length(), endIndex ).trim();
        */
        int endCommentIndex = inner.indexOf( "*/" );        
        int openParenIndex = inner.indexOf( '(' ); 
        
        
        if( endCommentIndex >= 0 && endCommentIndex < openParenIndex )
        {   //is there a comment BEFORE the method?
            int openCommentIndex = inner.indexOf( "/*" );
            if( openCommentIndex < 0 )
            {
                throw new VarException(
                    "unable to parse method, found */ before method with no /*" );
            }
            String commentText = inner.substring( openCommentIndex, endCommentIndex + 2 );
            
            System.out.println( 
                "Skipping comment " + commentText );
            
            String commentContent = 
                _Javadoc.parseJavadocCommentContent( commentText );
            this.javadocDom = CodeML.compile( _Parse.toCodeML( commentContent ) );
            
            inner = inner.substring( endCommentIndex + 2 ); 
        } 
        else
        {
            this.javadocDom = CodeML.compile( "" );
        }
            
        openParenIndex = inner.indexOf( '(' ); 
        //we need to close the method arguments
        int closeParenIndex = inner.indexOf( ')' ); 
        int openBraceIndex = inner.indexOf( '{', closeParenIndex );
        String signatureString = inner.substring( 0, openBraceIndex );
        
        String bodyString = 
            inner.substring( openBraceIndex + 1, inner.lastIndexOf( '}' ) );
        
        //this will convert $name$ to 
        signatureString = _Parse.toCodeML( signatureString.trim() );
        
        bodyString = _Parse.toCodeML( bodyString.trim() );
        
        //System.out.println( "SIGNATURE : " +signatureString );
        //System.out.println( "BODY      : " +bodyString );
        
        //now I need to retroactively Chan the $'se
        this.signatureDom = CodeML.compile( signatureString.trim() );
        this.bodyDom = CodeML.compile( bodyString.trim() );
    }
    
    /**
     * composes and returns the _method binding in the keyValues provided
     * 
     * @param keyValues key values to be bound into the 
     * @return 
     */
    public _method compose( Object...keyValues )
    {
        VarContext vc = VarContext.of( keyValues );
        return compose( vc );
    }
        
    public Dom getSignatureDom()
    {
        return signatureDom;
    }
    
    public Dom getBodyDom()
    {
        return bodyDom;
    }
    
    public Dom getJavadocDom()
    {
        return javadocDom;
    }
    
    public _method compose( VarContext context )
    {
        if( this.javadocDom.getMarkupText().length() == 0 )
        {   //there is no comment
            String sigString = Compose.asString(signatureDom, context );
            String bodyString = Compose.asString(bodyDom, context );
            return _method.of( sigString, _code.of( bodyString ) ); 
        }
        //there IS a comment
        String commentString = Compose.asString(javadocDom, context );
        String sigString = Compose.asString(signatureDom, context );
        String bodyString = Compose.asString(bodyDom, context );
        _method m  = _method.of( sigString, _code.of( bodyString ) );                
        m.javadoc( commentString );
        return m;
    }    
}

