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
package varcode.load;

import java.io.File;
import java.net.URL;
import varcode.load.Source.SourceFromClassLoader;
import varcode.load.Source.SourceStream;
import varcode.load.Source.SourceLoader;

/**
 * Finds source files (.java files OR any other language source files)
 * if they exist somewhere on the classpath)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ClassPathSourceLoader
    implements SourceLoader, SourceFromClassLoader
{   
    /** 
     * find the source ".java" file for the runtime class {@code markupClass}
     * 
     * @param runtimeClass the runtime class
     * @return a SourceStream 
     */
    @Override
    public SourceStream sourceOf( Class runtimeClass )
    {
        Class topLevelClass = runtimeClass;
        if( runtimeClass.isMemberClass() )
        {
            topLevelClass = runtimeClass.getDeclaringClass();
        }
        SourceStream ms = sourceStream( 
            topLevelClass.getCanonicalName()
                .replace( ".", File.separator ) + ".java" ); 
        
        if( topLevelClass == runtimeClass )
        {   //the class asked for is the main file
            return ms;
        }
        //LOG.info( runtimeClass.getName()+ 
        //    " is a nested class, returning Stream for the Declaring Class" );
        return ms;
    }
    
    @Override
    public SourceStream sourceStream( String sourceId )
    {
        String relativePath = 
	    File.separatorChar + SourceLanguages.fromSourceId( sourceId ).resolvePath( sourceId );
        
        relativePath = relativePath.replace( File.separatorChar, '/' );
        
        URL url = getClass().getResource( relativePath );
        if( url != null )
        {
            //LOG.debug( "Found resource on classpath at:" + url.toString() );
            
            return new FileInJarSourceStream( 
                url.toString(), 
                sourceId, 
                getClass().getResourceAsStream( relativePath ) );
        }
        //LOG.debug( "Could not find resource on classpath:" +  relativePath );
        return null;        
    }

    @Override
    public String describe()
    {
        return "[Classpath]";
    }
    
}
