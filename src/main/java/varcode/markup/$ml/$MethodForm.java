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

import java.util.Set;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.Model;
import varcode.dom.Dom;
import varcode.java.JavaMarkupRepo;
import varcode.java.code._code;
import varcode.java.code._methods._method;
import varcode.markup.repo.MarkupRepo;

/**
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class $MethodForm
    implements Model
{
    //protected String methodName;
    
    public _method method;
    
    public $MethodForm( ) //String methodName )
    {
        //this.methodName = methodName;
        
        MarkupRepo.MarkupStream ms = 
            JavaMarkupRepo.INSTANCE.markupStream( getClass() );
         
        String s = ms.asString();
        
        int startIndex = s.indexOf( $Parse.OPEN );
        int endIndex = s.indexOf( $Parse.CLOSE );
        String inner = s.substring( startIndex + $Parse.OPEN.length(), endIndex ).trim();
        
        int openBraceIndex = inner.indexOf( '{' );
        String methodSignature = inner.substring( 0, openBraceIndex );
        
        System.out.println( "METHOD SIGNATURE " + methodSignature );
        System.out.println( "METHOD SIGNATURE " + methodSignature.trim() );
        
        String methodBody = 
            inner.substring( openBraceIndex + 1, inner.lastIndexOf( '}' ) );
        
        String m = $Parse.parseTemplate( methodSignature.trim() );
        System.out.println( "METHOD SIGNATURE " + m );
        this.method = _method.of( methodSignature.trim(), 
            _code.of( $Parse.parseTemplate( methodBody.trim() ) ) ); 
        
        //now I need to retroactively Chan the $'se
        Dom dom = $Parse.parseTemplateDom( methodSignature.trim() );
        Set<String>params = dom.getAllVarNames( VarContext.of() );
        
        if( params.size() > 0 )
        {
            String[] names = params.toArray(new String[ 0 ] );
            /*
            for( int i = 0; i < names.length; i++ )
            {
                System.out.println( names[ i ] );
                this.method = this.method.getSignature().replace( 
                    "$"+names[ i ] + "$", "{+"+names[i]+"+}" );
            }
            */
        }
                
        //this.method = _method.of( parseTemplate( methodSignature.trim() ), 
        //    _code.of( parseTemplate( methodBody.trim() ) ) );  
        System.out.println( "PARAM 0 " + this.method.getSignature().getParameters().getAt( 0 ) );
        
    }
    /*
    public $MethodForm( )
    {
        this.methodName = null;
    } 
    */

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
    
    
    private static int countOpenBraces( String internalText, int countOpen )
    {
        for( int i = 0; i < internalText.length(); i++ )
        {
            char c = internalText.charAt( i );
            switch( c )
            {
                case '{' : countOpen++; break;
                case '}' : countOpen--; break;
                default : break;
            }            
        }
        return countOpen;
    }
    
    private void theOldCode()
    {
        MarkupRepo.MarkupStream ms = 
            JavaMarkupRepo.INSTANCE.markupStream( getClass() );
         
        String s = ms.asString();
        int indexOfMethod = 0; //s.indexOf( methodName );
        int indexOfOpenBrace = s.indexOf( '{', indexOfMethod );
        int openBraceCount = 0;
        int indexOfCloseBrace = s.indexOf( '}', indexOfOpenBrace );
        
        do
        {
            String betweenText = s.substring( 
                indexOfOpenBrace + 1 , indexOfCloseBrace );
                
            System.out.println( betweenText );
            
            openBraceCount = countOpenBraces( betweenText, openBraceCount );
            //System.out.println( "BETWEEN TEXT " + betweenText );
            if( openBraceCount != 0)
            {
                indexOfOpenBrace = s.indexOf( '{', indexOfCloseBrace );
                indexOfCloseBrace = s.indexOf( '}', indexOfOpenBrace );
            }
        }
        while( openBraceCount != 0 );
        
        String theMethodText = 
            s.substring( indexOfMethod, indexOfCloseBrace );
        
        
        System.out.println( theMethodText );
    }
}
