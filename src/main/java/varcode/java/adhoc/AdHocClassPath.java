/*
 * Copyright 2017 Eric.
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
package varcode.java.adhoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helps build an AdHocClassPath, either from:
 * <UL>
 * <LI>the "existing" Classpath (that is at the System property "java.class.path")
 * <LI>or a brand new (from scratch) Classpath 
 * </UL>
 * Takes the existing ClassPath and adds locations (.jar files, 
 * 
 * and class folder locations)
 * 
 * @author Eric
 */
public class AdHocClassPath
    implements Javac.JavacOptions.CompilerOption
{
    /** The System property containing the current Java ClassPath*/
    public static final String CLASS_PATH_SYS_PROPERTY = "java.class.path";
    
    public List<String>paths = new ArrayList<String>();
    
    /**
     * Returns an AdHocClassPath based on the current ClassPath in 
     * the System properties
     * 
     * @return an AdHocClassPath based on the ClassPath in the System properties
     */
    public static AdHocClassPath current() 
    {
        String currentClassPath = 
            System.getProperty( CLASS_PATH_SYS_PROPERTY );
        String[] paths = currentClassPath.split( File.pathSeparator );
        AdHocClassPath adHocCP = new AdHocClassPath();
        return adHocCP.add( paths );
    }
    
    public AdHocClassPath()
    {        
    }
    
    public AdHocClassPath( List<String> paths )
    {
        this.paths = paths;
    }
    
    public AdHocClassPath add( String...paths )
    {
        for(int i=0; i< paths.length; i++ )
        {
            this.paths.add( paths[ i ] );
        }
        return this;
    }    
    
    
    public AdHocClassPath removeAll( Collection<String> paths )
    {
        this.paths.removeAll( paths );
        return this;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "AdHocClassPath:" );
        sb.append( System.lineSeparator() );
        for( int i = 0; i < this.paths.size(); i++ )
        {
            if( i > 0 )
            {
                sb.append( System.lineSeparator() );
            }
            sb.append( "    " );
            sb.append( this.paths.get( i ) ); 
        }
        return sb.toString();
    }
    
    /**
     * Find all paths that contain the target String
     * @param target
     * @return 
     */
    public List<String> findAllWith( String target )
    {
        List<String> found = new ArrayList<String>();
        
        for(int i=0; i< this.paths.size(); i++ )
        {
            if( paths.get( i ).contains( target ) )
            {
                found.add( paths.get( i ) );
            }
        }
        return found;
    }
    
    public AdHocClassPath removeAllWith( String target )
    {
        List<String> found = findAllWith( target );
        this.paths.removeAll( found );
        return this;
    }
    
    /**
     * This will go through the path, removing 
     * for instance: 
     * 
     * AdHocClassPath.removeJar( "junit-4.11" );
     * or
     * AdHocClassPath.removeJar( "junit-4.11.jar" );
     * 
     * will remove ALL paths that end with the "junit-4.11.jar"
     * 
     * NOTE: we DO have to specify the EXACT VERSION / name of the jar 
     * alternatively, if we want to remove "junit"
     * we'd: 
     * AdHocClassPath.removeAllPathsWith( "junit" );
     * @param jarName
     * @return 
     */
    public AdHocClassPath removeJar( String jarName )
    {
        if( !jarName.endsWith( ".jar" ) )
        {
            jarName = jarName + "jar";
        }
        List<String> found = new ArrayList<String>();
        for( int i = 0; i < this.paths.size(); i++ )
        {
            if( this.paths.get( i ).endsWith( jarName ) )
            {
                found.add( paths.get( i ) );
            }            
        }
        this.paths.removeAll( found );
        return this;
    }

    /**
     * Returns the path separated by ";"s
     * 
     * @return 
     */
    public String toStringPath()
    {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < this.paths.size(); i++ )
        {
            if( i > 0 )
            {
                sb.append( ";" );
            }
            sb.append( paths.get( i ) );
        }
        return sb.toString();
    }
    
    @Override
    public void addToOptions( List<String> javacOptions )
    {
        javacOptions.add( "-cp" );
        javacOptions.add( toStringPath() );
    }    
}
