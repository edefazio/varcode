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

import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _bindNestTest
    extends TestCase
{
    /**
     * 
     * OK, heres the problem....
     * 
     * TO SUMMARIZE, I NEED TO DEPTH FIRST BIND
     * I need to go to the leaves, and bind, and then author the parents
     * 
     * 
     * What I am doing (NOW)(which wont work) is
     * I'm "authoring" the structure, and THEN binding
     * the values AFTER... so for instance if I have THIS in each Template.Base:
     * <PRE>
     * public String bind( VarContext context, Directive...directives )
       {
            Dom dom = BindML.compile( author() ); 
            return Author.code( dom, context, directives );
       }
     * </PRE>
     * the problem here is that in this situation:
     * 
     * _if bindMulti = _if.is("1==1", "{+bindAMultiLineValue+}");
     * where: 
     * bindMulti.condition == "1==1"
     * bindMulti.body = "{bindAMultiLineValue+}"
     * 
     * ...If I AUTHOR FIRST:
     * String authorFirst = buildMulti.author();
     * -which "authors" itself to this-
     * 
     *  if( 1==1 )
     *  {
     *      {+bindAMultiLineValue+}
     *  }
     * ...and THEN if I try and "bind" a multi-line value into 
     * {+bindAMuliLineValue+}, lets say we use "A\r\n\B\r\nC" (which is :
     * A
     * B
     * C
     * )
     * 
     * VarContext vc = VarContext.of( "bindAMultiLineValue", "A\r\n\B\r\nC" );
     * String authorThenBind = Author.code( BindML.compile(authorFirst), vc );
     * 
     * ...then I will get:
     * 
     * if( 1==1 )
     * {
     *      A
     * B
     * C
     * }
     * 
     * ...when What I really wanted was:
     * if( 1==1 )
     * {
     *     A
     *     B
     *     C
     * }
     * 
     * To achieve the second result... INSTEAD OF
     * Authoring the Document First, and then Binding the values in SECOND
     * I need to do the BIND each of the LEAF NODES FIRST and then
     * AUTHOR the document: 
     * 
     * 1) BIND the LEAF NODES (the condition and body nodes )
     * String conditionBind = bindMulti.condition.bind( vc );
     * ... which will bind to this: 
     * 1==1
     * 
     * String bodyBind = bindMulti.body.bind( vc );
     * ... which will bind to this: 
     * A
     * B
     * C
     * ... THEN when I am 
     * VarContext.
     * </PRE>
     * 
     * 
     * 
     */
    
    public static final String N = "\r\n";
    
    /**
     * In this example below, we want to bind in "A\r\nB"
     * and, if we do it right it will look like:
     * <PRE>
     * if( i > 0 )
     * { 
     *     A
     *     B
     * }
     * </PRE>
     * 
     * NOT:
     * 
     * <PRE>
     * if( i > 0 )
     * { 
     *     A
     * B
     * }
     * </PRE>
     */
    public void testIfMultiLineNestedBindIndent()
    {
        _if i = 
            _if.is( "i > 0", 
                "{+doThis+}" );
        
        //this illustrates the priblem
        String bind = i.bindIn( VarContext.of(
            "doThis", "A\r\nB") ).author();
        
        assertEquals(
            "if( i > 0 )" + N +
            "{" + N +
            "    A" + N +
            "    B" + N +
            "}", bind.trim() );
        
        //System.out.println( bind );
        /*
         _if i = 
            _if.is("i > 0", 
                "{+doThis+}",
                "{+doThat+}", 
               _if.is("x < 100", 
                    "{+$>(innerDo)+}", 
                    "{+$>(innerDoThat)+}" ) );
        
        //this illustrates the priblem
        String bind = i.bind( VarContext.of(
            "doThis", "A\r\nB", 
            "doThat", "B", 
            "innerDo", "A1\r\n A2\r\n A3\r\n", 
            "innerDoThat", _code.of("B1", "B2", "B3")) );
        */ 
        
    }
}
