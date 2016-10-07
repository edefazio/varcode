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
package varcode.markup.$ml;

import varcode.Model;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.java.JavaMarkupRepo;
import varcode.java.code._code;
import varcode.java.code._methods;
import varcode.markup.repo.MarkupRepo;

/**
 * Allows A Method to be extracted from text within PARSE_OPEN and PARSE_CLOSE
 * tags
 * 
 * extend this class and delineate 
 * 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class $Method
    implements Model
{
    public _methods._method method;
    
    public static final String PARSE_OPEN ="/*{$";
    public static final String PARSE_CLOSE ="$}*/";
    
    public $Method( )
    {
        MarkupRepo.MarkupStream ms = 
            JavaMarkupRepo.INSTANCE.markupStream( getClass() );
         
        //now get the Text of the Source Document as a String
        String s = ms.asString();
        
        //find the Class containing the template within the class
        int indexOfClassInit = s.indexOf( "class " + getClass().getSimpleName() );
        
        //AFTER the definition of the class, find the PARSE tags
        int startIndex = s.indexOf( PARSE_OPEN, indexOfClassInit );
        
        //AFTER the start tag, find the end tag
        int endIndex = s.indexOf( PARSE_CLOSE, startIndex );
        
        String inner = s.substring( startIndex + PARSE_OPEN.length(), endIndex ).trim();
        
        int openBraceIndex = inner.indexOf( '{' );
        String methodSignature = inner.substring( 0, openBraceIndex );
        
        String methodBody = 
            inner.substring( openBraceIndex + 1, inner.lastIndexOf( '}' ) );
        
        this.method = _methods._method.of( methodSignature.trim(), 
            _code.of( $Parse.parseTemplate( methodBody.trim() ) ) );       
    }

    @Override
    public Model bindIn( VarContext context )
    {
        return this.method.bindIn( context );
    }

    @Override
    public Model replace( String target, String replacement )
    {
        return this.method.replace( target, replacement );
    }

    @Override
    public String author( Directive... directives )
    {
        return this.method.author( directives );
    }  
}

