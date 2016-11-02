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

import java.util.ArrayList;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;
import varcode.java.model._code;

/**
 *
 * @author eric
 */
public class _try
    implements Model
{        
    /**
	 * Any try(withResources)...catch()...finally() block of code
	 */
	public static final Dom TRY_CATCH_FINALLY_BLOCK = BindML.compile(
		"try{{+:( {+withResources+} )+}}" + N +
		"{" + N +
		"{+$>(codeBlock)+}"+ N +        
		"}" + N +
		"{{+:catch( {+catchException+} e )"+ N +
		"{" + N +
		"{+$>(handleException)+}" + N +
		"}"+ N +
		"+}}{{+:finally" + N +
		"{" + N +
		"{+$>(finallyBlock)+}" + N +
		"}"+ N + 
		"+}}");
    
    public VarContext getContext()
    {
        String[] catchException = 
            new String[ this.catchExceptionHandleBlocks.size() ];
        
        _code[] handleException = 
            new _code[ this.catchExceptionHandleBlocks.size() ];    
        
        for( int i = 0; i < this.catchExceptionHandleBlocks.size(); i++ )
        {
            catchException[ i ] = 
                this.catchExceptionHandleBlocks.get( i ).exception;
            
            handleException[ i ] =
                this.catchExceptionHandleBlocks.get( i ).handleBlock;    
        }
        VarContext vc = VarContext.of(
            "codeBlock", this.body,
            "catchException", catchException,
            "handleException", handleException );
        if(! this.withResources.isEmpty() )
        {
            vc.set( "withResources", this.withResources );
        }
        if(! this.finallyBlock.isEmpty() )
        {
            vc.set( "finallyBlock", this.finallyBlock );
        }
        return vc;
    }
    
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
    
    @Override
    public String author( Directive... directives )
    {
        return Compose.asString( TRY_CATCH_FINALLY_BLOCK, getContext(), directives );
    }
    
    @Override
    public String toString()
    {
        return author();
    }
    
    /** the body inside the try/catch or try/finally or try/catch/finally*/
    private _code body;
    
    private List<_catchHandleBlock> catchExceptionHandleBlocks;
    
    private _code withResources;
    
    private _code finallyBlock;
    
    public static _try withResources( _code resourcesInit, Object...code )
    {
        _try t = new _try( _code.of( code ) );
        
        t.with( resourcesInit );
        return t;
    }
    
    public static _try finallyBlock( _code body, Object... finallyBody )
    {
        _try t = new _try( body );
        t.finallyBlock = _code.of( finallyBody );
        return t;
    }
    
    public static _try catchAndHandle( 
        _code body, Object exceptionClass, Object...handleCode)
    {
        _try t = new _try( body );
        
        t.catchAndHandle( exceptionClass, _code.of( handleCode ) );
        return t;
    }
   
    public _try( _code body )
    {
        this.catchExceptionHandleBlocks = 
            new ArrayList<_catchHandleBlock>();
        this.body = body;
        this.withResources = new _code();
        this.finallyBlock = new _code();
    }
    
    public final _try with( _code resourcesInit )
    {
        this.withResources = resourcesInit;
        return this;
    }
    
    public final _try finallyBlock( Object... finallyBody )
    {
        this.finallyBlock.addTailCode( finallyBody );
        return this;
    }
     
    public final _try catchAndHandle( 
        Object exceptionClass, Object... handleExceptionCode )
    {
        this.catchExceptionHandleBlocks.add( 
            new _catchHandleBlock( exceptionClass, _code.of( handleExceptionCode) ) );
        return this;
    }
    
    public final _try catchAndHandle( 
        Object execptionClass, _code handleExceptionCode )
    {
        this.catchExceptionHandleBlocks.add( 
            new _catchHandleBlock( execptionClass, handleExceptionCode ) );
        return this;
    }

    @Override
    public _try replace( String target, String replacement )
    {
        this.body = this.body.replace( target, replacement );
        for( int i = 0; i < this.catchExceptionHandleBlocks.size(); i++)
        {
            this.catchExceptionHandleBlocks.get( i )
                .replace( target, replacement );
        }
        this.finallyBlock.replace( target, replacement );
        this.withResources.replace( target, replacement );        
        return this;
    }

    @Override
    public _try bind( VarContext context )
    {
        this.body.bind( context );
        this.finallyBlock.bind( context );
        this.withResources.bind( context );
        for( int i = 0; i < this.catchExceptionHandleBlocks.size(); i++ )
        {
            catchExceptionHandleBlocks.get( i ).bind( context );
        }
        return this;
    }

    /**
     * A block of code for catching and handling a specific exception
     * 
     */
    public static class _catchHandleBlock
        implements Model
    {    
        private String exception;
        private _code handleBlock;
        
        public _catchHandleBlock( Object exception, _code handleBlock )
        {
            this.handleBlock = handleBlock;
            if( exception instanceof Class )
            {
                this.exception = ((Class) exception).getCanonicalName();    
            }
            else
            {
                this.exception = exception.toString();
            }                        
        }

        @Override
        public _catchHandleBlock bind( VarContext context )
        {
            this.exception = Compose.asString( BindML.compile( this.exception ), context );
            this.handleBlock.bind( context );
            return this;
        }
        
        @Override
        public _catchHandleBlock replace( String target, String replacement )
        {
            this.exception = this.exception.replace( target, replacement );
            this.handleBlock = this.handleBlock.replace( target, replacement);
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
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }        
    }
}
