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
package varcode.java.code;

import varcode.Template;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _if
    extends Template.Base
{
    public static _if is( Object condition, Object... bodyLines )
    {
        return new _if( condition, bodyLines );
    }
    
    public _code condition;
    public _code body;
    
    public _if( Object condition, Object...bodyLines )
    {
        this.condition = _code.of( condition );
        this.body = _code.of( bodyLines );        
    }

    @Override
    public _if replace(String target, String replacement)
    {
        this.condition = this.condition.replace( target, replacement );
        this.body = this.body.replace( target, replacement );
        return this;
    }

    public VarContext getContext()
    {
        return VarContext.of(
            "condition", this.condition, 
            "body", this.body );
    }
    
    public static final Dom IF_BLOCK = BindML.compile(
        "if( {+condition*+} )" + N +
        "{" + N +
        "{+$>(body)*+}" + N +
        "}" + N );
    
    @Override
    public String author( Directive... directives )
    {
        return Author.code( IF_BLOCK, getContext(), directives );
    }

    
    @Override
    public String bind( VarContext context, Directive...directives )
    {
        VarContext vc = VarContext.of(
            "condition", this.condition.bind( context, directives ), 
            "body", this.body.bind( context, directives ) );
        
        return Author.code( IF_BLOCK, vc, directives );
    }

    
    @Override
    public String toString()
    {
        return author();
    }
}
