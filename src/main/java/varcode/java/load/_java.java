/*
 * Copyright 2016 Eric DeFazio.
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
package varcode.java.load;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import varcode.Model.ModelLoadException;
import varcode.java.lang._class;
import varcode.java.lang._interface;
import varcode.load.BaseSourceLoader;
import varcode.load.SourceLoader;
import varcode.load.SourceLoader.SourceStream;

/**
 * Loads/Builds Source, AST and Models for Java source code
 *
 * @author Eric DeFazio eric@varcode.io
 */
public class _java
{
    /**
     * Load the (.java) source for a given Class
     * @param clazz the class to load the source for
     * @return the SourceStream (carrying an inputStream)
     */
    public static SourceStream sourceFrom ( Class clazz )
    {
        return BaseSourceLoader.INSTANCE.sourceStream( clazz );
    }
    
    /**
     * Load the (.java) source for a given Class using the 
     * <CODE>sourceLoader</CODE> provided.
     * @param sourceLoader the loader for finding and returning the source
     * @param clazz the class to load the source for
     * @return  the SourceStream ( wrapping an inputStream)
     */
    public static SourceStream sourceFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        return sourceLoader.sourceStream( clazz.getCanonicalName() );
    }
    
    /**
     * Loads the  
     * @param topLevelClass
     * @return 
     */
    public static CompilationUnit astFrom( Class topLevelClass )
    {
        try
        {
            return JavaASTParser.astFrom(
                sourceFrom( topLevelClass ).getInputStream() );
        }
        catch( ParseException pe )
        {
            throw new ModelLoadException(
                "Unable to parse Model from " + topLevelClass, pe );
        }
    }
    
    /**
     * Gets the "top level" class (the one having a file name) that contains
     * the source/ declaration of the <CODE>clazz</CODE>
     * @param clazz the class to retrieve
     * @return the top Level Class for this class
     */
    private static Class getTopLevelClass( Class clazz )
    {
        if( clazz.getDeclaringClass() == null )
        {
            return clazz;
        }
        return getTopLevelClass( clazz.getDeclaringClass() );
    }
    
    /**
     * returns the AST TypeDeclaration node that represents the clazz
     * the TypeDeclaration instance is a child node of a ast Root 
     * <CODE>CompilationUnit</CODE>
     * @param clazz a Class
     * @return 
     */
    public static TypeDeclaration astDeclarationFrom( Class clazz )
    {
        Class topLevelClass = getTopLevelClass( clazz ); 
        SourceStream ss = 
            BaseSourceLoader.INSTANCE.sourceStream( topLevelClass );
        try
        {
            CompilationUnit cu = JavaASTParser.astFrom( ss.getInputStream() );
            return JavaASTParser.findTypeDeclaration( cu, clazz );
        }
        catch( ParseException ex )
        {
            throw new ModelLoadException(
                "Unable to Parse " + topLevelClass + " to extract source for " 
                + clazz, ex );
        }
    }
    
    public static CompilationUnit astFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        if( clazz.getDeclaringClass() != null )
        {
            try
            {
                return JavaASTParser.astFrom( 
                    sourceLoader.sourceStream( clazz.getName() + ".java" )
                    .getInputStream() );
            }
            catch( ParseException pe )
            {
                throw new ModelLoadException( 
                    "Unable to parse contents of class "+ clazz+" ", pe );
            }
        }
        throw new ModelLoadException( 
            "Class " + clazz + " is NOT a declaring class, "+ System.lineSeparator() +
            "either load source for the declaring class, or call getASTNode( " +
            clazz + " )" );        
    }
    
    public static _class _classFrom( Class clazz )
    {
        return _JavaLoader._Class.from( clazz );
    }
    
    public static _class _classFrom( SourceLoader sourceLoader, Class clazz )
    {
        return _JavaLoader._Class.from( sourceLoader, clazz );
    }
    
    public static _class _classFrom( CompilationUnit astRoot, Class clazz )
    {
        return _JavaLoader._Class.from( astRoot, clazz );
    }
    
    public static _interface _interfaceFrom( Class clazz )
    {
        return _JavaLoader._Interface.from( clazz );
    }
}
