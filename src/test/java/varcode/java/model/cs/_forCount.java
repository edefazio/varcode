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
package varcode.java.model.cs;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;
import varcode.java.model._code;

/**
 * This will be a static inner class inside _for
 * 
 * 
 * 
 * @author M. Eric DeFazio eric@
 */
public class _forCount
    implements Model
{        
    public static _forCount up( int count )
    {
        return new _forCount( int.class, "i", 0, "<", count, "i++", new _code() );
    }

    public static _forCount up( String name, String min, String maxExclusive )
    {
        return new _forCount( int.class, name, min, "<", maxExclusive, name+ "++", new _code() );
    }
    
    public static _forCount up( String name, String maxExclusive )
    {
        return new _forCount( int.class, name, 0, "<", maxExclusive, name+ "++", new _code() );
    }
    
    public static _forCount up( String name, int count )
    {
        return new _forCount( int.class, name, 0, "<", count, name + "++", new _code() );
    }

    public static _forCount up( int min, int maxExclusive )
    {
        return new _forCount( int.class, "i", min, "<", maxExclusive, "i++", new _code() );
    }

    public static _forCount up( String name, int min, int maxExclusive )
    {
        return new _forCount( int.class, name, min, "<", maxExclusive, name+"++", new _code() );
    }
    
    public static _forCount up( int count, _code body )
    {
        return new _forCount( int.class, "i", 0, "<", count, "i++", body );
    }
    
    public static _forCount up( String name, int min, int maxExclusive, _code body )
    {
        return new _forCount( int.class, name, min, "<", maxExclusive, name + "++", body );
    }
    
    public static _forCount up( String name, int count, _code body )
    {
        return new _forCount( int.class, name, 0, "<", count, name + "++", body );
    }

    public static _forCount down( int count )
    {
        return new _forCount( int.class, "i", count, ">=", 0, "i--",new _code() );
    }
    
    public static _forCount down( int count, _code body )
    {
        return new _forCount( int.class, "i", count, ">=", 0, "i--", body );
    }
    
    public static _forCount down( String name, int count, _code body )
    {
        return new _forCount( int.class, name, count, ">=", 0, name+"--", body );
    }

    public static _forCount down( String name, int maxInclusive, int minInclusive,  _code body )
    {
        return new _forCount( int.class, name, maxInclusive, ">=", minInclusive, name + "--", body );
    }
    
    
                                 // for(int i = 0; i < 100; i++)
    private Class varType;       //     int
    private String varName;      //         i      ^         
    private Object initialValue; //             0 
    private Object operator;     //                  <
    private Object endValue;     //                    100
    private Object delta;        //                         i++
    
    private _code body;
    
    public static final Dom FOR_COUNT = 
        BindML.compile( 
            "for( {+varType*+} {+varName*+} = {+init*+};" +
                " {+varName*+} {+operator*+} {+endValue*+};" +
                " {+delta*+} )" + N +
            "{" + N +
            "{{+?body:{+$>(body)+}" + N +"+}}" +                    
            "}" + N );
    
    public VarContext getContext()
    {
        return VarContext.of( 
            "varType", this.varType,
            "varName", this.varName,
            "init", this.initialValue,
            "operator", this.operator,
            "endValue", this.endValue,
            "delta", this.delta,
            "body", this.body );
    }
    
    private static Object doBindIn( Object element, VarContext context )
    {
        if( element == null )
        {
            return null;            
        }
        if( element instanceof String )
        {
            return Compose.asString( BindML.compile((String)element), context );
        }
        if( element instanceof Model )
        {
            return ((Model)element).bind( context );
        }
        return element;
    }
        
    @Override
    public _forCount bind( VarContext context )
    {
        this.varName = Compose.asString( BindML.compile( this.varName ), context );
        this.delta = doBindIn( this.delta , context );
        this.initialValue = doBindIn( this.initialValue, context );
        this.operator = doBindIn( this.operator, context );
        this.endValue = doBindIn( this.endValue, context );
        this.body.bind( context );
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
        return Compose.asString( FOR_COUNT, getContext(), directives ); 
    }
    
    @Override
    public String toString()
    {
        return author();
    }
    
    public _forCount body( Object...codeLines )
    {
        if( this.body.isEmpty() )
        {
            this.body = _code.of( codeLines );
        }
        else
        {
            this.body.addTailCode( codeLines );
        }
        return this;
    }
    
    public _code getBody()
    {
        return this.body;
    }
    
    public _forCount( 
        Class varType, String varName, Object initialValue, 
        Object operator, Object endValue, Object delta, _code code )
    {
        this.varType = varType;
        this.varName = varName;
        this.initialValue = initialValue;
        this.operator = operator;
        this.endValue = endValue;
        this.delta = delta;
        this.body = code;        
    }
     
    private static Object rep( Object o, String target, String replacement )
    {
        if( o == null )
        {
            return null;
        }
        if( o instanceof String )
        {
            return ((String) o).replace(target, replacement);
        }
        if( o instanceof Model )
        {
            return ((Model)o).replace(target, replacement);
        }
        return o.toString().replace( target, replacement );
    }
    

    
    @Override
    public _forCount replace( String target, String replacement )
    {
        //this.v
        this.varName = this.varName.replace( target, replacement );
        this.delta = rep( this.delta, target, replacement );
        this.initialValue = rep( this.initialValue, target, replacement );
        this.operator = rep( this.operator, target, replacement );
        this.endValue = rep( this.endValue, target, replacement );        
        this.body = this.body.replace(target, replacement);
        return this;
    }
}
