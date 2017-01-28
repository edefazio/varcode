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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import varcode.context.Context.alias;
import varcode.context.Directive;
import varcode.context.VarScript;

/**
 * 
 * Scrapes the static fields from a Class  
 * Read a class, and collect the static fields that are of 
 * {@link Resolve} type:
 * <UL>
 *   <LI>{@link varcode.context.resolve.Resolve.VarResolver}
 *   <LI>{@link varcode.context.resolve.Resolve.DirectiveResolver}
 *   <LI>{@link varcode.context.resolve.Resolve.ScriptResolver}
 * </UL>
 * 
 * ...will ALSO identify static fields that are of type:
 * <UL>
 *   <LI>{@link varcode.author.PreProcessor} (Directive)
 *   <LI>{@link varcode.author.PostProcessor} (Directive)
 *   <LI>{@link varcode.context.VarScript}
 * </UL>
 
 and will "registerTo" these entities to the field name and @alias({names[]})
 in the {@link Context}
 * 
 * ...And updates the context with these 
 * 
 * Used when initializing a new {@link varcode.context.VarContext} for reading
 * and associating {@link varcode.context.VarScript} and 
 * {@link varcontext.context.Directive} implementations to specific names
 * 
 * @see InitVarContextBindings
 * @see ContextBindingRegistry
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class StaticFieldsScraper    
{   
    /**
     * Register a Class as containing 
     * @param clazz
     * @return 
     */
    public StaticFieldsScraper register( Class clazz )
    {
        scrapeStaticFields( clazz );
        return this;
    }
    
    /**
     * Gets all of the alias names (via the @alias({"a", "b"}) annotation 
     * applied to this field (and returns a String[])
     * @param field the field
     * @return a String array containing all aliases for this field
     */
    private static String[] getAliases( Field field )
    {
        //alias[] aliases =
        //    field.getAnnotationsByType( alias.class );
        Set<String>ali = new HashSet<String>();
        
        Annotation[] anns = field.getAnnotations();
        
        alias aliases = field.getAnnotation( alias.class );
        //System.out.println( "GOT "+ aliases );
        for( int k = 0; k < aliases.value().length; k++ )
        {
            //System.out.println("ADDING "+ aliases.value()[k] );
            ali.add( aliases.value()[k] );
        }
        return ali.toArray( new String[ 0 ] );
    }
    
    
    /**
     * 
     * @param clazz class containing static fields to add 
     * and register to a Context
     * @return this (updated internally with the Fields)
     */
    public static ContextBindingRegistry scrapeStaticFields( Class clazz )
    {
        ContextBindingRegistry registry = 
           new ContextBindingRegistry();
        
        Field[] fields = clazz.getFields();
        
        for( int i = 0; i < fields.length; i++ )
        {               
            try
            {
                if ( ( fields[i].getModifiers() & Modifier.STATIC ) > 0 )
                {
                    if( Resolve.class.isAssignableFrom( fields[ i ].getType() ) )
                    {                         
                        Resolve fieldValue = (Resolve)fields[i].get( null );
                        registry.setResolver( fieldValue );                        
                    }                    
                    else if( VarScript.class.isAssignableFrom( fields[ i ].getType() ) )
                    {
                        
                        VarScript vs = (VarScript)fields[i].get( null );
                        registry.nameToVarScriptBinding.put( fields[i].getName(), vs );
                        String[] aliases = getAliases( fields[ i ] ); 
                        for( int j = 0; j < aliases.length; j++ )
                        {
                            registry.nameToVarScriptBinding.put( aliases[ j ], vs );    
                        }                        
                    }
                    if( Directive.class.isAssignableFrom( fields[ i ].getType() ) )
                    {
                        Directive dir  = (Directive)fields[i].get( null );
                        
                        registry.nameToDirectiveBinding.put( fields[ i ].getName(), dir );
                        
                        String[] aliases = getAliases( fields[ i ] ); 
                        for( int j = 0; j < aliases.length; j++ )
                        {
                            registry.nameToDirectiveBinding.put( aliases[ j ], dir );    
                        }
                    }
                }                
            }
            catch( Exception e )
            {
                //couldnt register
            }                        
        }
        return registry;
    }    
    
    
    
    public static void main(String[] args) throws NoSuchFieldException
    {
        //System.out.println( 
        //    StaticFieldsScraper.class.getField( "ID" ).getAnnotations().length );
        
        //System.out.println( "THERE ARE " +
        //    InitVarContextBindings.class.getField("count").getAnnotations().length );
        
        StaticFieldsScraper rr = new StaticFieldsScraper();
        
        rr.register( InitVarContextBindings.class );
        
        System.out.println( rr );
    }
}
