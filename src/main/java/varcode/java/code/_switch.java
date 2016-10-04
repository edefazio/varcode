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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.translate.JavaTranslate;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 *
 * int i = 100;
   switch( i )
   {    
        case 1:   //MULTICASE
        case 2:
            System.out.println( "1 or 2" );                
        case 5:
            break; //breaks only
        case Modifier.ABSTRACT:
            System.out.println(" ABSTRACT " );
            break; //break After                               
    }
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _switch
    implements Model
{   
    public static _switch of( String variable )
    {
        return new _switch( variable );
    }
    
    public static Dom SWITCH = BindML.compile(
        "switch( {+varName*+} )" + N +
        "{" + N +
        "{+cases+}" +
        "{{+?defaultCase:" + N +
        "{+defaultCase+}" + N +
        "+}}" + N + 
        "}" );    

    public _code varName;
    
    public List<Model>cases;
    
    public DefaultCase defaultCase;
    
    public _switch( String varName )
    {
        this.varName = _code.of( varName );
        this.cases = new ArrayList<Model>();
    }
    
    public VarContext getContext()
    {
        return VarContext.of(
            "varName", this.varName,  
            "cases", this.cases, 
            "defaultCase", this.defaultCase );
    }
    
    @Override
    public String toString()
    {
        return author();
    }
    
    @Override
    public _switch bindIn( VarContext context )
    {
        this.varName.bindIn( context );
        this.defaultCase.bindIn( context );
        for(int i=0; i< this.cases.size(); i++ )
        {
            this.cases.get(i).bindIn( context );
        }
        return this;
    }
    
    @Override
    public _switch replace(String target, String replacement)
    {
        this.varName = varName.replace(target, replacement);
        for(int i=0; i<cases.size(); i++)
        {
            Object o = cases.get( i );
            if( o instanceof Case )
            {
                ((Case)o).replace(target, replacement);
            }
            else
            {
                ((MultiCase)o).replace(target, replacement);
            }
        }
        this.defaultCase = this.defaultCase.replace(target, replacement);
        return this;
    }

    @Override
    public String author( Directive... directives )
    {
        return Compose.asString( SWITCH, getContext(), directives );
    }
    
    public _switch addCase( Object equal, String code, boolean breakAfter )
    {
        return addCase( equal, _code.of( code ), breakAfter );
    }
    
    /**
     * Should handle 
     * multi-cases by passing in arrays
     * like :
     * 
     * 
     * addCase( new int[]{1,2,3}, "System.out.println(\"1,2,3\");", true);
     * produces:
     *    case 1:
     *    case 2:
     *    case 3:
     *        System.out.println(\"1,2,3\");
     *    break;
     * 
     * @param equal the case equality
     * @param code code for this case
     * @param breakAfter add a break after this case?
     * @return this (modified) 
     */
    public _switch addCase( Object equal, _code code, boolean breakAfter )
    {
        if( equal.getClass().isArray() )
        {
            int len = Array.getLength( equal );
            _code[] caseEquals = new _code[ len ];
            
            for( int i = 0; i < len; i++ )
            {
                Object o = Array.get( equal, i );
                caseEquals[ i ] = 
                    _code.of( JavaTranslate.INSTANCE.translate( o ) );
            }
            this.cases.add( new MultiCase( caseEquals, code, breakAfter ) );
            return this;            
        }
        this.cases.add( 
            new Case( 
                _code.of( JavaTranslate.INSTANCE.translate( equal ) ), 
                    code, 
                    breakAfter ) );            
        return this;
    }
    
    public _switch defaultCase( String code )
    {
        return defaultCase( _code.of( code ) );
    }
    
    public _switch defaultCase( _code code )
    {
        this.defaultCase = new DefaultCase( code );
        return this;
    }
    
    public static class DefaultCase
        implements Model
    {   
        public _code code;
         
        public static Dom DEFAULT_CASE = BindML.compile(
            "{{+:    default:" + N +
            "+}}{+$>>(code)*+}" + N );
        
        public DefaultCase( _code code )
        {
            this.code = code;
        }
        
        public VarContext getContext()
        {
            return VarContext.of(
                "code", this.code
            );
        }
        
        @Override
        public String toString()
        {
            return author();
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Compose.asString( DEFAULT_CASE, getContext(), directives );
        }
        
        @Override
        public DefaultCase bindIn( VarContext context )
        {
            code = code.bindIn( context );
            return this;
        }
        
        @Override
        public DefaultCase replace( String target, String replacement )
        {
            this.code.replace(target, replacement);
            return this;
        }
    }        
    
    /** Individual case within a Switch statement */
    public static class Case
        implements Model
    {                
        public _code caseEqual;
        public _code code;
        public boolean breakAfter;
        
        public static Dom CASE = BindML.compile(
            "{{+:    case:{+caseEquals+}" + N +
            "+}}{+$>>(code)*+}" + N +
            "{{+?breakAfter:    break;+}}" );
         
        public Case( _code caseEqual, _code code, boolean breakAfter )
        {
            this.caseEqual = caseEqual;
            this.code = code;
            this.breakAfter = breakAfter;
        }

        @Override
        public String toString()
        {
            return author();
        }
        
        public VarContext getContext()
        {            
            VarContext vc = VarContext.of(
                "caseEquals", this.caseEqual,
                "code", this.code );
            if( this.breakAfter )
            {
                vc.set("breakAfter", this.breakAfter );
            }
            return vc;
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Compose.asString( CASE, getContext(), directives );
        }
        
        @Override
        public Case bindIn( VarContext context )
        {
            caseEqual = caseEqual.bindIn( context );
            
            code = code.bindIn( context );
            return this;
        }
        
        @Override
        public Case replace( String target, String replacement )
        {
            this.caseEqual.replace(target, replacement);
            this.code.replace(target, replacement);
            return this;
        }
    }
    
    /**
     * A Multi-Case within a Switch statement ie.<PRE>
     * switch( count )
     * {
     * case 1:
     * case 2:
     * case 3:
     *    return "small";
     *  ...
     * }
     * </PRE>
     */
    public static class MultiCase
        implements Model
    {                
        public _code[] caseEquals;
        public _code code;
        public boolean breakAfter = false;
        
        public MultiCase( _code[] caseEquals, _code code, boolean breakAfter )
        {
            this.caseEquals = caseEquals;
            this.code = code;
            this.breakAfter = breakAfter;
        }
        
        public static Dom MULTICASE = BindML.compile(
            "{{+:    case:{+caseEquals+}" + N +
            "+}}{+$>>(code)*+}" + N +
            "{+?((breakAfter)):break;+}" );

        @Override
        public MultiCase replace(String target, String replacement)
        {
            for( int i = 0; i < caseEquals.length; i++ )
            {
                caseEquals[ i ] = caseEquals[ i ].replace( target, replacement );
            }
            code = code.replace(target, replacement);
            return this;
        }

        public VarContext getContext()
        {
            return VarContext.of(
                "caseEquals", this.caseEquals,
                "code", this.code,
                "breakAfter", this.breakAfter
            );
        }
        @Override
        public String author( Directive... directives )
        {
            return Compose.asString( MULTICASE, getContext(), directives );
        }
        
        @Override
        public MultiCase bindIn( VarContext context )
        {
            for( int i = 0; i < caseEquals.length; i++ )
            {
                caseEquals[ i ] = caseEquals[ i ].bindIn( context );
            }
            code = code.bindIn( context );
            return this;
        }
    }
}
