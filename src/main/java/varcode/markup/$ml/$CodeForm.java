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

import java.util.HashSet;
import java.util.Set;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.java.JavaMarkupRepo;
import varcode.java.code._code;
import varcode.markup.bindml.BindML;
import varcode.markup.repo.MarkupRepo;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class $CodeForm
    extends $Parse
{
    /** This is the compiled Form for the code */
    protected Dom dom;
    
    public $CodeForm()
    {
        MarkupRepo.MarkupStream ms = 
            JavaMarkupRepo.INSTANCE.markupStream( getClass() );
        String s = ms.asString();
        int startIndex = s.indexOf( OPEN );
        int endIndex = s.indexOf( CLOSE );
        String inner = s.substring( startIndex + OPEN.length() , endIndex ).trim();
        this.dom = parseTemplate( inner );
    }
    
    private $CodeForm( Dom dom )
    {
        this.dom = dom;
    }
    
    private $CodeForm( String form )
    {
        this.dom = BindML.compile( form );
    }
    
    public Dom getDom()
    {
        return this.dom;
    }
    
    public _code getCode()
    {
        return _code.of( this.dom.getMarkupText() );
    }
    
    @Override
    public $CodeForm bindIn( VarContext context )
    {
        return new $CodeForm( Compose.asString( this.dom, context ) );
    }

    @Override
    public $CodeForm replace( String target, String replacement )
    {
        return new $CodeForm( 
            BindML.compile( 
                this.dom.getMarkupText().replace( target, replacement ) ) );
    }

    @Override
    public String author( Directive... directives )
    {
        return Compose.asString( this.dom, VarContext.of() );
    }
    
    /** gets the vars for the CodeTemplate */
    public Set<String> getVars()
    {
        HashSet<String> varNames = new HashSet<String>();
        return this.dom.getAllVarNames( VarContext.of() );
    }

    public String toString()
    {
        return this.dom.getMarkupText();
    }
}
