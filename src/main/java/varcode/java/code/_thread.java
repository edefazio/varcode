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

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * Thread template idioms which benefit from editor-based code completion
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _thread
{
    /**
     * Author code for running a series of code in another {@code Thread} 
     * (and optionally starting that {@code Thread})
     * <PRE>
     * _thread.run("a = 100");
     * 
     * // authors:
     * new Thread( 
     *     new Runnable() 
     *     { 
     *         public void run() 
     *         {       
     *             a = 100;
     *         }
     *     }
     * )
     * 
     * _thread.run("a = 100").start();
     * 
     * //authors:
     * new Thread( 
     *     new Runnable() 
     *     { 
     *         public void run() 
     *         {       
     *             a = 100;
     *         }
     *     }
     * ).start();
     * </PRE>
     * @param codeLines the lines of code to be within the thread
     * @return  the _runnable (code template)
     */
    public static _runnable run( Object...codeLines )
    {
        return new _runnable( codeLines );
    }
            
    /** Creates an anonymous runnable for threading */
    public static class _runnable
        implements Model
    {        
        /**
         * 
         * @param context contains bound variables and scripts to bind data into
         * the template
         * @param directives pre-and post document directives 
         * @return the populated Template bound with Data from the context
         */
        @Override
        public String bind( VarContext context, Directive...directives )
        {
            Dom dom = BindML.compile( author() ); 
            return Compose.asString( dom, context, directives );
        }
        
        private _code body; 
        private boolean start = false;
        
        public _runnable( Object... codeLines )
        {
            this.body = new _code( );
            this.body.addTailCode( codeLines );            
        }
        
        @Override
        public _runnable bindIn( VarContext context )
        {
            this.body.bindIn( context );
            return this;
        }
        
        public _runnable start()
        {
            this.start = true;
            return this;
        }

        @Override
        public _runnable replace(String target, String replacement)
        {
            this.body.replace( target, replacement );
            return this;
        }

        public static final Dom THREAD_RUN = BindML.compile( 
            "new Thread(" + N +
            "    new Runnable()" + N +
            "    {" + N +
            "        public void run()" + N +
            "        {" + N +        
            "{+$>>>(body)*+}" + N +
            "        }" + N +
            "    }" + N +
            "){{+?((start==true)):.start();+}}" + N );
        
        public VarContext getContext()
        {
            return VarContext.of( "body", this.body, "start", this.start );
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Compose.asString(THREAD_RUN, getContext(), directives);
        }
        
        @Override
        public String toString()
        {
            return author();
        }
    }    
}
