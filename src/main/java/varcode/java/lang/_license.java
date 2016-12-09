/*
 * Copyright 2016 Eric.
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
package varcode.java.lang;

import varcode.Model;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric
 */
public class _license 
    implements JavaMetaLang    
{
    public static _license of( Dom dom, String...keyValues )
    {
        return new _license( dom, keyValues );
    }
    
    public static final Dom APACHE_2_0 = BindML.compile( 
"/*" + N +
" * Copyright 2016 {+author+}." + N +
" *" + N +
" * Licensed under the Apache License, Version 2.0 (the \"License\");" + N +
" * you may not use this file except in compliance with the License." + N +
" * You may obtain a copy of the License at" + N +
" *" + N +
" *      http://www.apache.org/licenses/LICENSE-2.0" + N +
" *" + N +
" * Unless required by applicable law or agreed to in writing, software" + N +
" * distributed under the License is distributed on an \"AS IS\" BASIS," + N +
" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." + N +
" * See the License for the specific language governing permissions and" + N +
" * limitations under the License." + N +
" */" + N );
    
    
    private Dom dom;
    private VarContext context;
    
    public _license( Dom dom, String...keyValues )
    {
        this.dom = dom;
        this.context = VarContext.of( (Object[])keyValues );
    }
    
    @Override
    public JavaMetaLang replace( String target, String replacement ) 
    {
        return this;
    }

    @Override
    public Model bind( VarContext context ) 
    {
        //context.merge( this.context );
        return this; //this doesnt bind yet
    }

    @Override
    public String author() 
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives ) 
    {
        return Compose.asString( dom, this.context, directives );
    }
    
}
