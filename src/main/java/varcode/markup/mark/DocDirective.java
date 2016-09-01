package varcode.markup.mark;

/**
 * {@code Mark} which names a {@code Directive} to be called
 * during the pre-processing/post-processing stages of {@code Tailor} 
 *
 * Some Examples of {@code Directive}s are:
 * <UL>
 *   <LI>(PreProcessing) validate the input data provided in the {@code VarContext} {@see ContextValidator} 
 *   <LI>(PreProcessing) remove all {@code Mark}s from the {@code Dom} {@see StripMarks}
 *   <LI>(PostProcessing) formatting the source code for a specific coding conventions
 *   <LI>(PostProcessing) run a Lint Checker/FindBugs and export a report on the source (to metadata)
 *   <LI>(Pre/PostProcessing) create a Checksum of the {@code Dom} and the tailored Code {@see SHA1Checksum}
 *   <LI>(PostProcessing) indent each line of the the source code by a tab or certain number of spaces
 *   <LI>(PostProcessing) use a compiler to compile and test the code (verify functionality) 
 * </UL>      
 * @author M. Eric DeFazio eric@varcode.io
 */
//In BindML "()" is optional
//  {$$removeEmptyLines$$}
//  {$$checksum$$} <-- the name "checksum" is "bound" to a Directive in the VarContext 
//  {$$ex.myproject.MyCode$$}  <-- a class that implements Directive 
    //                               either with static Field "INSTANCE"
    //                               or 

//In CodeML "()" is optional
    /*{$$removeEmptyLines$$}*/
    /*{$$checksum$$}*/

/*{$$formatCode()$$}*/
/*{$$findBugs()$$}*/
/*{$$lintCheck()$$}*/
/*{$$formatSource()$$}*/
/*{$$searchAndReplace()$$}*/

public class DocDirective
	extends Mark 
{
	private final String name;
	
	public DocDirective( 
		String text, 
		int lineNumber, 
		String directiveName )  
	{
		super( text, lineNumber );
		this.name = directiveName;		
	}

	public String getName()
	{
		return name;
	}
}
