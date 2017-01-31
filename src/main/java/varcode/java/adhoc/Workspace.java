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
package varcode.java.adhoc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import varcode.java.model._Java.FileModel;

/**
 * Group of {@code AdHocJavaFile}s (.java source code files) intended to be 
 * compiled together into a single {@link AdHocClassLoader} containing the 
 * .class files with the bytecodes.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Workspace
{
    /**
     * Fully qualified Class Name -> Java source files of the {@code Workspace}
     */
    private final Map<String, AdHocJavaFile> classNameToAdHocJavaFileMap
        = new HashMap<String, AdHocJavaFile>();

    /** creates a Workspace of AdHocJavaFiles */
    public static Workspace of( AdHocJavaFile...adHocJavaFiles )
    {
        Workspace workspace = new Workspace();
        workspace.add( adHocJavaFiles );
        return workspace;
    }
    
    /** Creates a Workspace given one or more JavaFileModels */
    public static Workspace of( FileModel... javaModels )
    {
        Workspace workspace = new Workspace();
        workspace.add(javaModels );
        return workspace;
    }
    
    /**
     * Gets the fully qualified names of all Java class files in the workspace
     * @return 
     */
    public Set<String>getAllClassNames()
    {
        return this.classNameToAdHocJavaFileMap.keySet();
    }
    
    /**
     * Removes all Java Files from the Workspace
     */
    public void clear()
    {
        classNameToAdHocJavaFileMap.clear();
    }
    
    /** counts the number of files in the workspace */
    public int count()
    {
        return this.classNameToAdHocJavaFileMap.size();
    }
    
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
    /**
     * Gets the Java files from the Workspace
     * @return a collection of JavaFiles
     */
    public Collection<AdHocJavaFile> getJavaFiles()
    {
        return classNameToAdHocJavaFileMap.values();
    }
    
    public final Workspace add( Workspace...workspaces )
    {
        for( int i = 0; i < workspaces.length; i++ )
        {
            this.classNameToAdHocJavaFileMap.putAll( 
                workspaces[ i ].classNameToAdHocJavaFileMap );
        }
        return this;
    }
    
    /**
     * Directly adds (_class, _enum, _interface, _annotationType)s
     * to the workspace
     * @param fileModels any top Level Java file type
     * @return the updated workspace
     */
    public final Workspace add( FileModel...fileModels )
    {
        for( int i = 0; i < fileModels.length; i++ )
        {
            add( fileModels[ i ].toJavaFile(  ) );
        }
        return this;
    }
    
    public final Workspace add( List<FileModel> fileModels )
    {
        return add( fileModels.toArray( new FileModel[ 0 ] ) );
    }
    /**
     * Adds one or more {@code AdHocJavaFile}s to the Workspace 
     *
     * @param adHocJavaFiles one or more {@code AdHocJavaFile}s to add to the 
     * {@code Workspace}
     * @return the Workspace containing the AdHocJavaFiles
     */
    public final Workspace add( AdHocJavaFile... adHocJavaFiles )
    {
        for( int i = 0; i < adHocJavaFiles.length; i++ )
        {            
            classNameToAdHocJavaFileMap.put(
                adHocJavaFiles[ i ].getQualifiedName(), adHocJavaFiles[ i ] );
        }
        return this;
    } 
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        AdHocJavaFile[] javaFiles = 
            classNameToAdHocJavaFileMap.values().toArray( new AdHocJavaFile[ 0 ] );
        
        for( int i=0; i< javaFiles.length; i++ )
        {
            sb.append("   ");
            sb.append( javaFiles[ i ].describe() );
            sb.append( System.lineSeparator() );
        }
        
        return "@Workspace "+ hashCode()+"@" + System.lineSeparator() +
            sb.toString();
    }
}
