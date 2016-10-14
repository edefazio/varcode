package varcode.markup.repo;

import java.io.InputStream;

/**
 * VERY *Similar* to <B>a single entry in the -sourcepath</B> when using javac.
 * 
 * Defines a path containing varcode source (i.e. _*.java files). 
 * These files COULD be:
 * <UL>
 * <LI>inline with existing project source code
 * <LI>in a zip file
 * <LI>in a separate source folder
 * <LI>in a compressed resource bundle/archive
 * <LI>stored on a remote server
 * <LI>in a version control system
 * <LI>...
 * </UL>
 *  
 * Instead of using a ClassLoader to return a specific .class file, 
 * which could either be returns a {@code Markup}.
 */
public interface MarkupRepo
{    
    /**
     * @param markupId the name of the markup to be loaded 
     * (i.e. for Java the fully qualified Java class name like:
     * <BLOCKQUOTE>"java.io.InputStream.java"</BLOCKQUOTE>
     * 
     * @return a {@code MarkupStream} for streaming in the text of the markup
     *  <B>null</B> if a no MarkupStream is found in this Repo with "markupId".
     */
    public MarkupStream markupStream( String markupId );
    
    /**
     * Each element of the Path should be able to describe itself 
     * (most of the time the "repo" will be a path on the fileSystem)
     * so I can print out the order/strategy that was used to resolve 
     * a specific source file. 
     * 
     * (I want to be able to print out the VarSourcePath in text if the VarSource
     * for a given class is not found)
     *  
     * @return a String that describes a repository (a file path, a jar file, URI, etc.)
     */
    public String describe();
    
    /**
     * Input Stream to the {@code Markup}
     */
    public interface MarkupStream
    {
        /** An InputStream to the Markup File*/
        InputStream getInputStream();
        
        /** the identity of the Markup to be loaded */
        String getMarkupId();
        
        /** describe the nature of the Stream (a File, a (remote Server, etc.) */
        String describe();        
       
        /** Return the contents of the InputStream as a String */
        String asString();
    }
}