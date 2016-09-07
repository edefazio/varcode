package varcode.java.adhoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import varcode.VarException;

/**
 * Formalize the available javac Compiler Flags
 * (So developers aren't having to search for them during dev)
 * 
 * MOST OF these come from:
 * http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javac.html#options
 *  
 * @see varcode.java.adhoc.Workspace
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavacOptions 
{	
	INSTANCE;
	
	/**
	 * Options to be passed to the Javac Compiler (at runtime)
	 */
	public interface CompilerOption
	{
		public void addToOptions( List<String> javacOptions );
	}
	
	/**
	 * Here we take the Static instances and Enums and return
	 * the appropriate Iterable<String> containing all of the
	 * Compiler Options to be passed to the Javac compiler at runtime
	 * 
	 * @param compilerOptions the compiler options (could be null or 0 length)
	 * @return Iterable ready to be passed to the Runtime JavacTool
	 */
	public static Iterable<String> optionsFrom
		(JavacOptions.CompilerOption... compilerOptions  )
	{
		if( ( compilerOptions == null ) || compilerOptions.length == 0 )
		{
			return null;
		}
		ArrayList<String>javacOptions = new ArrayList<String>();
		for( int i = 0; i < compilerOptions.length; i++ )
		{
			compilerOptions[ i ].addToOptions( javacOptions);
		}
		return javacOptions;
	}

	
	/** These are "one-property" flags that can be added to the compiler */
	public enum Flags
		implements CompilerOption
	{
		DEPRECATION( "-deprecation", 
		"Shows a description of each use or override of a deprecated member or class. "
      + "Without the -deprecation option, javac shows a summary of the source files "
	  + "that use or override deprecated members or classes. "
	  + "The -deprecation option is shorthand for -Xlint:deprecation." ),
		
		ALL_DEBUG_INFORMATION( "-g",
        "Generates all debugging information, including local variables. "
      + "By default, only line number and source file information is generated."),
		
		NO_DEBUG_INFORMATION( "-g:none",
		"Does not generate any debugging information."),
			
		NOWARN( "-nowarn",
	    "Disables warning messages. This option operates the same as the "
	  + "-Xlint:none option."),
		
		NO_ANNOTATION_PROCESSING( "-proc:none",
		"Controls whether annotation processing and compilation are done. "
	  + "-proc:none means that compilation takes place without annotation processing" ),
		
		ONLY_PROCESS_ANNOTATIONS( "-proc:only", 
		"-proc:only means that only annotation processing is done, "
		+ "without any subsequent compilation."),
		
		STORE_FORMAL_PARAMETER_NAMES_FOR_REFLECTION ( "-parameters",
        "Stores formal parameter names of constructors and methods in "
      + "the generated class file so that the method "
      + "java.lang.reflect.Executable.getParameters from the Reflection API "
      + " can retrieve them." ),
		
		VERBOSE( "-verbose",
		"Uses verbose output, which includes information about each "
	  + "class loaded and each source file compiled.");
		
		//VERSION( "-version",
		//"Prints release information." ),
		
		//FAIL_ON_WARNING( "-werror",
		//"Terminates compilation when warnings occur." ),
		
		//SHOW_NONSTANDARD_COMPILER_OPTIONS ( "-X",
		//"Displays information about nonstandard options and exits.");
	
		private final String flag;
		
		private final String description;
	
		private Flags( String flag, String description )
		{
			this.flag = flag;
			this.description = description;
		}
	
		public String getName() 
		{
			return flag;
		}

		public String getDescription() 
		{
			return description;
		}
		
		public void addToOptions( List<String> javacOptions )
		{
			javacOptions.add( flag );
		}
	}

	/**
	 * Specifies options to pass to annotation processors. 
	 * These options are not interpreted by javac directly, 
	 * but are made available for use by individual processors. 
	 * The key value should be one or more identifiers separated by a dot (.).
	 * 
	 * <PRE>FORM: "-Akey[=value]"</PRE>
	 */
	public static class AnnotationKeyValue
		implements CompilerOption
	{
		public static AnnotationKeyValue of( String key, String value )
		{
			return new AnnotationKeyValue( key, value );
		}
		
		private final String key;
		private final String value;
		
		public AnnotationKeyValue( String key, String value )
		{
			this.key = key;
			this.value = value;			
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-A" + key );
			if( value != null )
			{
				optionsList.add( "=" + value );				
			}
		}
	}
	
	/**
	 * -classpath C:\java\MyClasses utility.myapp.Cool
	 */
	public static class ClassPath
		implements CompilerOption
	{
		
		public static ClassPath of( String path )
		{
			return new ClassPath( path );
		}
		
		//a series of paths separated by ';'
		private final String path;
		
		public ClassPath( String path)
		{
			this.path = path;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-classpath" );
			optionsList.add( path );			
		}
	}
	/**
	 * -Djava.ext.dirs=directories
	 * Overrides the location of installed extensions.	 
	 */
	public static class ExtensionDirs
		implements CompilerOption
	{
		public static ExtensionDirs of( String directories )
		{
			return new ExtensionDirs( directories );
		}
		
		private final String directories;
		
		public ExtensionDirs( String directories)
		{
			this.directories = directories;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-Djava.ext.dirs" );
			optionsList.add( directories );			
		}		
	}
	
	/**
	 *-Djava.endorsed.dirs=directories
     * Overrides the location of the endorsed standards path.
	 */
	public static class EndorsedDirs
		implements CompilerOption
	{
		
		public static EndorsedDirs of( String directories )
		{
			return new EndorsedDirs( directories );
		}
		
		private final String directories;
		
		public EndorsedDirs( String directories)
		{
			this.directories = directories;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-Djava.endorsed.dirs" );
			optionsList.add( directories );			
		}		
	}
	
	/**
	 * Sets the destination directory for class files. 
	 * The directory must already exist because javac does not create it. 
	 * If a class is part of a package, then javac puts the class file in a 
	 * subdirectory that reflects the package name and creates directories as 
	 * needed.
	 * If you specify -d C:\myclasses and the class is called com.mypackage.MyClass, 
	 * then the class file is C:\myclasses\com\mypackage\MyClass.class.
	 * 
	 * If the -d option is not specified, then javac puts each class file in the 
	 * same directory as the source file from which it was generated.
	 * 
	 * Note: The directory specified by the -d option is not automatically 
	 * added to your user class path.
	 */
	public static class DestinationDirectory
		implements CompilerOption
	{
		public static DestinationDirectory of( String dir )
		{
			return new DestinationDirectory( dir );
		}
		
		private final String directory;
		
		public DestinationDirectory( String directory )
		{
			this.directory = directory;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-d" );
			optionsList.add( directory );			
		}		
	}
	
	/**
	 *-encoding encoding
	 * Sets the source file encoding name, such as EUC-JP and UTF-8. 
	 * If the -encoding option is not specified, then the platform 
	 * default converter is used.
	 */
	public static class Encoding
		implements CompilerOption
	{
		
		public static Encoding of( String enc )
		{
			return new Encoding( enc );
		}
		
		private final String encoding;
		
		public Encoding( String encoding)
		{
			this.encoding = encoding;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-encoding" );
			optionsList.add( encoding );			
		}		
	}
	
	public static class DebugOptions
		implements CompilerOption
	{		
		public enum KeyWord
		{
			SOURCE( "source", "source file debugging information" ),
			LINES( "lines", "Line number debugging information" ),
			VARS( "vars", "Local variable debugging information." );
			
			private final String option;
			private final String description;
			
			private KeyWord( String option, String description )
			{
				this.option = option;
				this.description = description;
			}

			public String getOption() {
				return option;
			}

			public String getDescription() {
				return description;
			}		
		}
		
		public static DebugOptions of( KeyWord...keywords )
		{
			return new DebugOptions( keywords );
		}
		
		private final HashSet<KeyWord> keyWords;
		
		public DebugOptions( KeyWord...keyWordsList )
		{
			this.keyWords = new HashSet<KeyWord>();
			this.keyWords.addAll( Arrays.asList( keyWordsList ) );
		}
		
		public void addToOptions( List<String> optionsList )
		{
			if( this.keyWords.size() > 0 )
			{
				//optionsList.add( "-g:" );
				StringBuilder sb = new StringBuilder();
				Iterator<KeyWord> it = keyWords.iterator();
				boolean first = true;
				while( it.hasNext() )
				{
					if( ! first )
					{
						sb.append( ',' );
					}
					sb.append( it.next().option );
					first = false;
				}
				optionsList.add(  "-g:" +  sb.toString() );
			}
			else
			{
				optionsList.add( "-g:none" );
			}
		}		
	}
	
	/**
	 * -processor class1 [,class2,class3...]
     * Names of the annotation processors to run. This bypasses the default discovery process.
	 */
	public static class AnnotationProcessorClasses
		implements CompilerOption
	{
		public static AnnotationProcessorClasses of( String...annotationClasses )
		{
			return new AnnotationProcessorClasses( annotationClasses );
		}
		
		private final String classes;
		
		public AnnotationProcessorClasses( String... annotationProcessorClasses )
		{
			if( annotationProcessorClasses.length < 1 )
			{
				throw new VarException(
					"MUST provide at least (1) annotation processor class "
				  + "to use -processor compiler option" );
			}
			StringBuilder sb = new StringBuilder();
			for(int i=0; i < annotationProcessorClasses.length ; i++ )
			{
				if( i > 0 )
				{
					sb.append( "," );
				}
				sb.append( annotationProcessorClasses[ i ] );
			}
			classes = sb.toString();
			
		}		
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-processor" );
			optionsList.add( classes );			
		}		
	}
	
	/**
	 * -processorpath path
	 * Specifies where to find annotation processors. 
	 * If this option is not used, then the class path is searched for processors.
	 */
	public static class AnnotationProcessorPath
		implements CompilerOption	
	{
		public static AnnotationProcessorPath of( String path )
		{
			return new AnnotationProcessorPath( path );
		}
		
		private final String annotationProcessorPath;
		
		public AnnotationProcessorPath( String  annotationProcessorPath )
		{
			this.annotationProcessorPath = annotationProcessorPath;			
		}		
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-processorpath" );
			optionsList.add( annotationProcessorPath );			
		}
	}
	
	/**
	 * Specifies the directory <B>where to place the generated source files</B>. 
	 * The directory must already exist because javac does not create it. 
	 * If a class is part of a package, then the compiler puts the source file 
	 * in a subdirectory that reflects the package name and creates directories 
	 * as needed.
	 * If you specify -s C:\mysrc and the class is called com.mypackage.MyClass, 
	 * then the source file is put in in C:\mysrc\com\mypackage\MyClass.java.
	 * 
	 * (NOTE: this is MOST OFTEN used as the base directory where 
	 * <B>ANNOTATION PROCESSORS</B> will write source code to... 
	 */
	public static final class SourceDestinationDirectory
		implements CompilerOption
	{
		public static SourceDestinationDirectory of( String dir )
		{
			return new SourceDestinationDirectory( dir );
		}
		
		private final String directory;
		
		public SourceDestinationDirectory( String directory )
		{
			this.directory = directory;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-d" );
			optionsList.add( directory );			
		}		
	}
	
	/**
     * -source release    
	 * Specifies the version of source code accepted. 
	 * The following values for release are allowed:
	 * 
	 * 1.3 The compiler does not support assertions, generics, or other language features introduced after Java SE 1.3.
	 * 1.4 The compiler accepts code containing assertions, which were introduced in Java SE 1.4.
	 * 1.5 The compiler accepts code containing generics and other language features introduced in Java SE 5.
	 * 5 Synonym for 1.5.
	 * 1.6 No language changes were introduced in Java SE 6. However, encoding errors in source files are now reported as errors instead of warnings as in earlier releases of Java Platform, Standard Edition.
	 * 6 Synonym for 1.6.
	 * 1.7 The compiler accepts code with features introduced in Java SE 7.
	 * 7 Synonym for 1.7.
	 * 1.8 This is the default value. The compiler accepts code with features introduced in Java SE 8.
	 * 8 Synonym for 1.8.
	 */
	public static final class JavaSourceVersion	
	{		
		public enum MajorVersion
			implements CompilerOption
		{
			_1_3( "1.3", null, "The compiler does not support assertions, generics, or other language features introduced after Java SE 1.3." ),
			_1_4( "1.4", null, "The compiler accepts code containing assertions, which were introduced in Java SE 1.4." ),
			_1_5( "1.5", "5",  "The compiler accepts code containing generics and other language features introduced in Java SE 5"),
			_1_6( "1.6", "6",  "No language changes were introduced in Java SE 6. However, encoding errors in source files are now reported as errors instead of warnings as in earlier releases of Java Platform, Standard Edition."),
			_1_7( "1.7", "7",  "The compiler accepts code with features introduced in Java SE 7."),
			_1_8( "1.8", "8", "The compiler accepts code with features introduced in Java SE 8."),
			_1_9( "1.9", "9", "The compiler accepts code with features introduced in Java SE 9.");
			
			private final String option;
			private final String altOption;
			private final String description;
			
			private MajorVersion( 
				String option, String alternateOption, String description )
			{
				this.option = option;
				this.altOption = alternateOption;
				this.description = description;
			}

			public String getOption() 
			{
				return option;
			}

			public String getAltOption() 
			{
				return altOption;
			}

			public String getDescription() 
			{
				return description;
			}

			public void addToOptions(List<String> javacOptions) 
			{
				javacOptions.add( "-source" );
				javacOptions.add( option );
			}			
		}
		
		private final MajorVersion majorVersion;
		
		public JavaSourceVersion( MajorVersion majorVersion )
		{
			this.majorVersion = majorVersion;
		}

		public MajorVersion getMajorVersion() {
			return majorVersion;
		}			
	}
	
	/**
	 * -sourcepath sourcepath
	 * Specifies the source code path to search for class or interface definitions. 
	 * As with the user class path, source path entries are separated by colons (:) 
	 * on Oracle Solaris and semicolons on Windows and can be directories, 
	 * JAR archives, or ZIP archives. If packages are used, then the local path 
	 * name within the directory or archive must reflect the package name.
	 * 
	 * Note: Classes found through the class path might be recompiled when 
	 * their source files are also found. See Searching for Types.
	 */
	public static final class SourcePath
		implements CompilerOption
	{
		public static SourcePath of( String path )
		{
			return new SourcePath( path );
		}
		
		private final String path;
		
		public SourcePath( String path )
		{
			this.path = path;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-sourcepath" );
			optionsList.add( path );			
		}		
	}
	
	/**
	 * -sourcepath sourcepath
	 * Specifies the source code path to search for class or interface definitions. 
	 * As with the user class path, source path entries are separated by colons (:) 
	 * on Oracle Solaris and semicolons on Windows and can be directories, 
	 * JAR archives, or ZIP archives. If packages are used, then the local path 
	 * name within the directory or archive must reflect the package name.
	 * 
	 * Note: Classes found through the class path might be recompiled when 
	 * their source files are also found. See Searching for Types.
	 */
	public static final class BootClassPath
		implements CompilerOption
	{
		public static BootClassPath of( String path )
		{
			return new BootClassPath( path );
		}
		
		private final String path;
		
		public BootClassPath( String path )
		{
			this.path = path;
		}
		
		public void addToOptions( List<String> optionsList )
		{
			optionsList.add( "-bootclasspath" );
			optionsList.add( path );			
		}		
	}
}
