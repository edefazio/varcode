package varcode.markup.repo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.dom.Dom;
import varcode.java.JavaCase;
import varcode.markup.codeml.CodeMLCompiler;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 * Verify that I can use a group of Urls as a Repo
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class UrlRepoTest
	extends TestCase
{
	/** 
	 * Compiles {@Markup} from a the Raw Text of a GitHub Gist 
	 * ...and tailors new source 
	 */
	public void testGitHub()
	{
		String markupId = "ex.varcode._1.java";
		
		URL url = null;
		try 
		{
			//this maps to "ex.varcode._1.java" (the raw text content)
			url = new URL( 
				//"https://gist.githubusercontent.com/edefazio/d069b76be72ba39f68411fc6e99408c9/raw/dd965e9ed302a850e1d48ac7992354b30c7ed6e3/ex.varcode._1.java" );
			    "https://gist.githubusercontent.com/edefazio/d069b76be72ba39f68411fc6e99408c9/raw/033984a901f175141fb2befb45430a4133473996/ex.varcode._1.java" );
		} 
		catch( MalformedURLException e ) 
		{
			e.printStackTrace();
		}
		
		Map<String, URL>markupIdToURLMap = new HashMap<String,URL>();
		markupIdToURLMap.put( markupId, url );
		
		UrlRepo urlRepo = new UrlRepo( "GitHub Gist Examples", markupIdToURLMap );
		
    	JavaCase jc = JavaCase.of( 
    		urlRepo, 
    		"ex.varcode._1", 
    		"ex.varcode.GitHubGistTailored",
    		VarContext.of( 
    			"className", "GitHubGistTailored",
    			"message", "Tailored a Gist From GitHub") );

		System.out.println( jc );
		
	}
	
	public void testGitHub2()
	{
		UrlRepo urlRepo = new UrlRepo( "github Gists" );
		
		String markupId = "io.semiotics.field._UniformIntField32.java"; 
		urlRepo.addMarkupUrl( 
			markupId, 
			"https://gist.githubusercontent.com/edefazio/d069b76be72ba39f68411fc6e99408c9/raw/033984a901f175141fb2befb45430a4133473996/ex.varcode._1.java" );
			//"https://gist.githubusercontent.com/edefazio/b1bdf42ebe38da9904c3/raw/518636e74678396dc9de9e988f6fb2f72b84fc20/io.semiotics.field._UniformIntField32.java" );		
		
		MarkupStream markupStream = urlRepo.markupStream( markupId );
		
		Dom markup = CodeMLCompiler.INSTANCE.compile( markupStream );
		
		System.out.println( markup );
	}
}
