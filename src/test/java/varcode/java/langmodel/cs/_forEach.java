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
package varcode.java.langmodel.cs;

import varcode.Model.LangModel;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.doc.translate.JavaTranslate;
import varcode.java.langmodel._code;
import varcode.markup.bindml.BindML;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _forEach
    implements LangModel
{        
    /**
     * builds a forEach statement with body: <PRE>
     * _forEach( int.class, "x", "array")
     *     .body("System.out.printlnt(x);");
     * 
     * for( int x : array )
     * {
     *     System.out.println(x);
     * }
     * </PRE>
     * @param collection the variable name of the collection or array
     * @param elementType the element type within the collection
     * @param elementName the name for each element
     * @return _forEach that contains a block
     */
    public static _forEach of( 
        Class elementType, String elementName, String collection)
    {
        return new _forEach( 
            collection, 
            JavaTranslate.INSTANCE.translate( elementType ),  
            elementName );
    }
    
    /**
     * builds a forEach statement with body: <PRE>
     * _forEach( "String", "name", "names")
     *     .body("System.out.printlnt(name);");
     * 
     * for( String name : names )
     * {
     *     System.out.println(name);
     * }
     * </PRE>
     * @param collection the variable name of the collection or array
     * @param elementType the element type within the collection
     * @param elementName the name for each element
     * @return _forEach that contains a block
     */
    public static _forEach of( 
        String elementType, String elementName, String collection)
    {
        return new _forEach( collection, elementType, elementName );
    }
    
    public static Dom FOREACH = BindML.compile( 
        "for( {+elementType*+} {+elementName*+} : {+collection*+} )" + N +    
        "{" + N + 
        "{{+?body:{+$>(body)+}" + N + 
        "+}}" +         
        "}"                        
    );
    
    private String collection;
    private String elementType;
    private String elementName;
    private _code body;
    
    /**
     * 
     * @param collection
     * @param elementType
     * @param elementName 
     */
    public _forEach( String collection, String elementType, String elementName )
    {
       this.collection = collection;
       this.elementType = elementType;
       this.elementName = elementName;
       this.body = new _code();
    }
    
    
    @Override
    public _forEach bind(VarContext context)
    {
        this.collection = Compose.asString( BindML.compile(this.collection), context );
        this.elementType = Compose.asString( BindML.compile(this.elementType), context );
        this.elementName = Compose.asString( BindML.compile(this.elementName), context );
        this.body.bind( context );
        return this;
    }

    @Override
    public _forEach replace(String target, String replacement)
    {
        this.collection = this.collection.replace(target, replacement);
        this.elementName = this.elementName.replace( target, replacement);
        this.elementType = this.elementType.replace( target, replacement );
        this.body.replace(target, replacement);
        return this;
    }

    public VarContext getContext()
    {
        return VarContext.of(
            "elementType", this.elementType, 
            "elementName", this.elementName,
            "collection", this.collection, 
            "body", this.body );
    }

    public String getCollection()
    {
        return collection;
    }

    public String getElementType()
    {
        return elementType;
    }

    public String getElementName()
    {
        return elementName;
    }
    
    public _code getBody()
    {
        return this.body;
    }
    
    public _forEach body( Object... codeLines )
    {
        this.body.addTailCode( codeLines );
        return this;
    }
    
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
    
    @Override
    public String author( Directive... directives )
    {
        return Compose.asString( FOREACH, getContext(), directives );
    }
    
    @Override
    public String toString()
    {
        return author( );
    }

    /*
    public static String _each( 
        String collection, Class elementType, String elementName )
    {
        return "for( " + 
        JavaTranslate.INSTANCE.translate( elementType ) + " " +  elementName 
                +" : "+ collection +" )";
    }
    */
}
