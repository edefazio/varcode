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

import java.util.ArrayList;
import java.util.List;
import varcode.Template;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/** List of One-Per-Line Annotations*/
public class _annotations
    extends Template.Base
{
    private List<Object> annList;
    
    public static final Dom ANNOTATION_LIST = 
        //BindML.compile( "{{+?code:{+code+}" + N + "+}}" );
        BindML.compile( "{{+:{+annotation+}" + N + "+}}" );
    
    public _annotations( )
    {
        this( new ArrayList<Object>() );
    }
    
    public _annotations( List<Object> annotations )
    {
        this.annList = new ArrayList<Object>();
    }
    
    public VarContext getContext()
    {
        return VarContext.of( "annotation", annList );
    }
        
    public _annotations add( Object...annotations )
    {
        for( int i = 0; i < annotations.length; i++ )
        {
            this.annList.add( annotations[ i ] );
        }
        return this;
    }
    
    public _annotations replace( String target, String replacement )
    {
        List<Object> repList = new ArrayList<Object>();
        for( int i = 0; i < annList.size(); i++ )
        {
            Object o = annList.get( i );
            if( o instanceof String )
            {
                repList.add( ((String)o).replace( target, replacement ) );
            }
            else if (o instanceof Template.Base )
            {
                repList.add( ((Template.Base)o).replace( target, replacement ) );
            }
            else
            {
                repList.add( o.toString().replace( target, replacement ) );
            }
        }
        this.annList = repList;
        return this;
    }
    
    @Override
    public String author( Directive... directives )
    {
        if( ! isEmpty() )
        {
            return Author.code( ANNOTATION_LIST, getContext(), directives );
        }
        return "";
    }
        
    @Override
    public String toString()
    {
        return author();
    }
    
    public boolean isEmpty()
    {
        return this.annList.isEmpty();
    }
        
    public List<Object> getAnnotations()
    {
        return this.annList;
    }
    
    public int count()
    {
        return this.annList.size();
    }
}        
