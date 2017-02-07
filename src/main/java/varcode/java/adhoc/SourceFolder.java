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

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import varcode.java.model._Java.FileModel;

/**
 * An In Memory "SourceFolder" (like an IDE sourceFolder in IntelliJ, eclipse, etc.) 
 containing a group of source files as {@code JavaSourceFile}s and optionally 
 * some tests as {@code JavaTestFiles} intended to be 
 * compiled together into a single {@link AdHocClassLoader} containing the 
 * .class files with the bytecodes.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SourceFolder
{
    /** *  Qualified Class Name -> Java source files of the {@code SourceFolder}*/
    private final Map<String, JavaSourceFile> classNameToJavaSourceFileMap
        = new HashMap<String, JavaSourceFile>();
        
    /** creates a SourceFolder of AdHocJavaFiles */
    public static SourceFolder of( JavaSourceFile...adHocJavaFiles )
    {
        SourceFolder sourceFolder = new SourceFolder();
        sourceFolder.add( adHocJavaFiles );
        return sourceFolder;
    }
    
    /** Creates a SourceFolder given one or more JavaFileModels */
    public static SourceFolder of( FileModel... javaModels )
    {
        SourceFolder sourceFolder = new SourceFolder();
        sourceFolder.add( javaModels );
        return sourceFolder;
    }
    
    public SourceFolder()
    {
    }
    
    public Set<String>getAllClassNames()
    {
        return classNameToJavaSourceFileMap.keySet();
    }
    
    public void clear()
    {
        this.classNameToJavaSourceFileMap.clear();     
    }
    
    /** counts the number of files in the sourceFolder */
    public int count()
    {
        return this.classNameToJavaSourceFileMap.size();
    }
    
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
    public Collection<JavaSourceFile> getFiles()
    {
        return classNameToJavaSourceFileMap.values();
    }
    
    public final SourceFolder add( SourceFolder...sourceFolders )
    {
        for( int i = 0; i < sourceFolders.length; i++ )
        {
            this.classNameToJavaSourceFileMap.putAll(
                sourceFolders[ i ].classNameToJavaSourceFileMap );
        }
        return this;
    }
    
    /**
     * Directly adds (_class, _enum, _interface, _annotationType)s
     * to the sourceFolder
     * @param fileModels any top Level Java file type
     * @return the updated sourceFolder
     */
    public final SourceFolder add( FileModel...fileModels )
    {
        for( int i = 0; i < fileModels.length; i++ )
        {
            add( fileModels[ i ].toJavaFile(  ) );
        }
        return this;
    }
    
    public final SourceFolder add( List<FileModel> fileModels )
    {
        return add( fileModels.toArray( new FileModel[ 0 ] ) );
    }
    /**
     * Adds one or more {@code AdHocJavaFile}s to the SourceFolder 
     *
     * @param adHocJavaFiles one or more {@code AdHocJavaFile}s to add to the 
     * {@code SourceFolder}
     * @return the SourceFolder containing the AdHocJavaFiles
     */
    public final SourceFolder add( JavaSourceFile... adHocJavaFiles )
    {
        for( int i = 0; i < adHocJavaFiles.length; i++ )
        {            
            classNameToJavaSourceFileMap.put(
                adHocJavaFiles[ i ].getQualifiedName(), adHocJavaFiles[ i ] );
        }
        return this;
    } 
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        JavaSourceFile[] sourceFiles = 
            classNameToJavaSourceFileMap.values().toArray( new JavaSourceFile[ 0 ] );
        
        for( int i = 0; i < sourceFiles.length; i++ )
        {
            sb.append( "   " );
            sb.append( sourceFiles[ i ].describe() );
            sb.append( System.lineSeparator() );
        }
        
        return "SourceFolder : " + System.lineSeparator() +
            sb.toString();
    }
}
