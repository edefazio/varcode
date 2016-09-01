/*
 * Copyright 2015 M. Eric DeFazio.
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
package varcode.markup.repo;

import java.io.File;

/*{-?(log==false):*/import org.slf4j.Logger;
import org.slf4j.LoggerFactory;/*-}*/

import varcode.Lang;
import varcode.VarException;

/**
 * Hierarchical "Base" Directory containing Files of Markup source
 * for instance, if we have:
 * <PRE>
 * DirectoryRepo baseDir = 
 *     DirectoryRepo.of( "C:/workspace/myproj/markup/" );
 * </PRE>
 * and we wanted to load/compile the Markup in MarkupId:
 * <PRE>"com.myco.blah.Dummy.java"</PRE>
 * 
 * we'd load it like this:<PRE>
 * MarkupStream markupStream = baseDir.loadMarkup( 
 *     "com.myco.blah.Dummy.java" );
 * </PRE>  
 * <PRE>
 * "C:/workspace/myproj/markup/com/myco/blah/_Dummy.java" 
 *  \________________________/\________________________/
 *          |                         |
 *      baseDirectory                 |                  
 *                        markupId: "com.myco.blah._Dummy.java"
 * </PRE>                                                  
 */
public class DirectoryRepo
	implements MarkupRepo
{	
    /*{-?(log==false):*/private static final Logger LOG = 
        LoggerFactory.getLogger( DirectoryRepo.class );/*-}*/
    
	/** the "base" directory where "varcode" files reside */
	private final String baseDirectory;
	
	public static DirectoryRepo of( String baseDirectory )
	{
		return new DirectoryRepo( baseDirectory );
	}
	
	public DirectoryRepo( String baseDirectory )
	{
	    this( baseDirectory, false, false );
	}
	
	public DirectoryRepo( 
	    String baseDirectory, 
	    boolean createIfNeccessary, 
	    boolean exceptionOnCantCreate )
	{
		File f = new File( baseDirectory );			
		if( !f.exists() || !f.isDirectory() || !f.canRead() )
		{
		    if( createIfNeccessary )
		    {
		        boolean madeDirs = f.mkdirs();
		        
		        if(! madeDirs )
		        {
		            throw new VarException(
		                "Markup Repo Base Directory \"" + baseDirectory 
		                + "\" does not exist, is not a Directory or cannot be read from " );
		        }
		        LOG.debug( "Creating Markup Directory Repo at \"" + f.getAbsolutePath() + "\"");
		    }
		    else if ( exceptionOnCantCreate )
		    {
		        throw new VarException(
                    "Markup Repo Base Directory \"" + baseDirectory 
                    + "\" does not exist, is not a Directory or cannot be read from " );
		    }			 
		}
		this.baseDirectory = baseDirectory;
	}
	
	public String getBaseDirectory()
	{
		return baseDirectory;
	}	
	
	
	public MarkupStream loadMarkup( Class<?> clazz )
	{
		return markupStream( clazz.getCanonicalName().replace( ".", File.separator ) + ".java" );
	}
	
	/**
	 * The MarkupId should signify the language using the appropriate language 
	 * file extension, and it should also include the namespace/package of the 
	 * resource to be unambiguous.<BR>
	 * 
	 * for Java Source Code:<BR>
	 *  
	 * <PRE>"com.myproj.mytool.SomeClass.java"
	 *       \_______________/ \________/\__/ 
	 *           Full package       |      |
	 *                          ClassName  |
	 *                               language extension
	 *      |________________________________|
	 *                     | 
	 *                  markupId</PRE>         
	 */
	public MarkupStream markupStream( String markupId )
	{	    
	    String relativePath = 
	        Lang.fromCodeId( markupId ).resolvePath( markupId ); //NamespaceToMarkupPath.INSTANCE.resolvePath( markupId );
	    
	    String fileName = 
	        this.baseDirectory 
	      + File.separatorChar 
	      + relativePath;
	    	    
	    File f = new File( fileName );
	    
	    if( f.exists() &&  f.isFile() && f.canRead() )
	    {
	        /*{-?(log==false):*/
	        LOG.debug( "Reading Markup from File: \"" + fileName + "\"" );
	        /*-}*/
	        return new FileMarkupStream( markupId, fileName );            
	    }
	    return null;   
	}

    public String describe()
    {        
        return "[DIR] " + baseDirectory;
    }    
}
