/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.context.resolve;

import varcode.author.PostProcessor;
import varcode.author.PreProcessor;
import varcode.author.lib.AllCap;
import varcode.author.lib.CondenseMultipleBlankLines;
import varcode.author.lib.Count;
import varcode.author.lib.CountIndex;
import varcode.author.lib.EscapeString;
import varcode.author.lib.FirstCap;
import varcode.author.lib.FirstLower;
import varcode.author.lib.LowerCase;
import varcode.author.lib.Prefix;
import varcode.author.lib.PrintAsLiteral;
import varcode.author.lib.RemoveEmptyLines;
import varcode.author.lib.StripMarks;
import varcode.author.lib.Trim;
import varcode.author.lib.Quote;
import varcode.context.Context;
import varcode.context.Context.alias;
import varcode.context.VarScript;
import varcode.context.resolve.DirectiveResolver.SmartDirectiveResolver;
import varcode.context.resolve.VarScriptResolver.SmartScriptResolver;
import varcode.context.resolve.VarResolver.SmartVarResolver;

/**
 * Declares fields that are used within the {@link Context} for authoring
 * {@code Template}s. (Base functionality like indentation, uppercase, etc.) 
 * <UL>
 *   <LI>{@link Resolve} implementations used internally by the {@link Context}, 
 *     {@link DirectiveResolver} {@link VarResolver} {@link ScriptResolver}
 * 
 *   <LI>{@link VarScript}s bound to var names in the {@link Context}
 * 
 *   <LI>{@link Directive}s ({@link PreProcessor}, {@link PostProcessor})
 *       bound to variable names in the {@link Context}
 * </UL>
 * NOTE: this Class is "INPUT" to {@link StaticFieldsScraper}, so, by adding
 * a static field that implements {@link Resolve}, {@link Directive}, 
 * {@link VarScript}, it will be "bound" into the Context by name.
 
 This is how we registerTo "new functionality" into the Context en masse. (instead
 of having to manually registerTo individual 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class InitVarContextBindings 
{    
    /** these fields are known */
    public static SmartVarResolver VAR_RESOLVER = 
        SmartVarResolver.INSTANCE;
    
    public static SmartDirectiveResolver DIRECTIVE_RESOLVER = 
        SmartDirectiveResolver.INSTANCE; 
    
    public static SmartScriptResolver SCRIPT_RESOLVER = 
        SmartScriptResolver.INSTANCE;
            
    @alias({"#"})
    public static final VarScript count = Count.INSTANCE;
    
    @alias({"quot", "\"" })
    public static final VarScript quote = Quote.INSTANCE;
    
    @alias({"[#]", "indexCount"})
    public static final VarScript countIndex = CountIndex.INSTANCE;

    @alias({"^^", "cap", "caps"})
    public static VarScript allCap = AllCap.INSTANCE;
    
    @alias({"^", "firstCap", "firstCaps"})
    public static VarScript firstCap = FirstCap.INSTANCE;
    
    @alias({"lower", "allLower" })
    public static VarScript lowercase = LowerCase.INSTANCE;
    
    @alias({"firstLowerCase"})
    public static VarScript firstLower = FirstLower.INSTANCE;
    
    @alias({"escapeString"})
    public static VarScript escape = EscapeString.INSTANCE;

    @alias({"lit", "literal", "printAsLiteral"})
    public static VarScript literal = PrintAsLiteral.COMMA_SEPARATED_LIST;
    
    @alias({"lit[]", "literal[]", "printAsLiteral[]"})
    public static VarScript literalArray = PrintAsLiteral.USE_ARRAY_NOTATION;
    
    @alias({"><", "||"})
    public static VarScript trim = Trim.INSTANCE;
    
    @alias({"-{}", "-[]", "[-]"})
    public static RemoveEmptyLines removeEmptyLines = RemoveEmptyLines.INSTANCE;	    	    
    
    @alias({"condenseMultipleBlankLines", "collapseBlankLines"} )
    public static PostProcessor condenseBlankLines = CondenseMultipleBlankLines.INSTANCE;
        
    public static PreProcessor stripMarks = StripMarks.INSTANCE;
   
    public static Prefix tab = Prefix.INDENT_TAB;
    
    @alias( {">", "indent", "indent4", "indent4Spaces" } )
    public static Prefix indent4 = Prefix.INDENT_4_SPACES;
    
    @alias( {">>"} )
    public static Prefix indent8 = Prefix.INDENT_8_SPACES;
    
    @alias( {">>>"} )
    public static Prefix indent12 = Prefix.INDENT_12_SPACES;
    
    @alias( {">>>>"} )
    public static Prefix indent16 = Prefix.INDENT_16_SPACES;
    
        
    /** Singleton instance */ 
    public static final InitVarContextBindings INSTANCE = 
        new InitVarContextBindings();
    
    private final ContextBindingRegistry bindRegistry;
    
    /** 
     * If you create an instance, then it will do all this reflective
     * stuff only once, so I can pass the instance to a Context
     * and have it automatically do stuff.
     */
    public InitVarContextBindings()
    {        
        bindRegistry = 
            StaticFieldsScraper.scrapeStaticFields(InitVarContextBindings.class );
        //StaticFieldsScraper rsf = new StaticFieldsScraper();
        //rsf.scrapeStaticFields( InitVarContextBindings.class );
    }
    
    public void registerTo( Context context )
    {
        bindRegistry.registerTo( context );
    }        
}
