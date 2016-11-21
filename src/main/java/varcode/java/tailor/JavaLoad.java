package varcode.java.tailor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import varcode.Model.ModelLoadException;
import varcode.java.lang._class;
import varcode.java.lang._enum;
import varcode.java.lang._interface;
import varcode.load.BaseSourceLoader;
import varcode.load.SourceLoader;
import varcode.load.SourceLoader.SourceStream;

/**
 * Tailoring in varcode means modifying existing code.
 * To Tailor code you need to Load source into an AST node {@code TypeDeclaration}
 * then convert the AST node into a LangModel (_class, _interface, _enum), 
 * then mutate the model, then author the .java source for the modified model.
 *
 * JavaTailor brings these concepts into a single abstraction
 * <UL>
 *   <LI>Loading an AST root node {@code TypeDeclaration} from source 
     using a <CODE>SourceLoader</CODE>.
 *   <LI>Converting an AST root node {@code TypeDeclaration} to a LangModel 
 *   (_class, _interface, or _enum) 
 * </UL>
 * @author Eric DeFazio eric@varcode.io
 */
public class JavaLoad
{
    ; //singleton enum idiom
    
    public static SourceLoader SOURCE_LOADER = 
        BaseSourceLoader.INSTANCE;

    private JavaLoad()
    {
    }

    /**
     * Load the _class (LangModel) of a runtime Class <CODE>clazz</CODE> using 
     * the default {@code SOURCE_LOADER}
     * @param clazz the runtime class to read the source for
     * @return the _class (LangModel) of the runtime class.
     * @throws ModelLoadException if there is an exception loading the _class
     */
    public static _class _classOf( Class clazz )
    {
        return _classOf( SOURCE_LOADER, clazz );
    }
    
    /**
     * Load the _class (LangModel) of a runtime Class <CODE>clazz</CODE> using 
     * the default {@code SOURCE_LOADER}
     * @param sourceLoader the loader that can resolve the .java source from 
     * the class
     * @param clazz the runtime class to read the source for
     * @return the _class (LangModel) of the runtime class.
     * @throws ModelLoadException if there is an exception loading the _class
     */
    public static _class _classOf( SourceLoader sourceLoader, Class<?> clazz )
    {
        ClassOrInterfaceDeclaration coid = classASTOf( sourceLoader, clazz );
        _class _c = null; 
        if( clazz.getPackage() != null )
        {
           _c = _class.of( clazz.getPackage().getName(), coid.getName() );
        //I need to 
        }
        else
        {
           _c = _class.of( coid.getName() ); 
        }
        return JavaASTToLangModel.fromClassNode( _c, coid );        
    }   
    
    /**
     * Load the _class (LangModel) of a runtime Class <CODE>clazz</CODE> using 
     * the default {@code SOURCE_LOADER}
     * @param interfaceClazz the interface runtime class to read the source for
     * @return the _class (LangModel) of the runtime class.
     * @throws ModelLoadException if there is an exception loading the _class
     */
    public static _interface _interfaceOf( Class interfaceClazz )
    {
        return _interfaceOf( SOURCE_LOADER, interfaceClazz );
    }
    
    /**
     * Load the _interface (LangModel) of a runtime Class <CODE>clazz</CODE> using 
     * the default {@code SOURCE_LOADER}
     * @param sourceLoader the loader that can resolve the .java source from 
     * the class
     * @param interfaceClazz the runtime class to read the source for
     * @return the _interface (LangModel) of the runtime class.
     * @throws ModelLoadException if there is an exception loading the _class
     */
    public static _interface _interfaceOf( 
        SourceLoader sourceLoader, Class<?> interfaceClazz )
    {
        ClassOrInterfaceDeclaration coid = interfaceASTOf( 
            sourceLoader, interfaceClazz );
        _interface _i = null;
        if( interfaceClazz.getPackage() != null )
        {
            _i = _interface.of( interfaceClazz.getPackage().getName(), 
                "interface " + coid.getName() );
        }
        else
        {
            _i = _interface.of( "interface " + coid.getName() );
        }
        //return JavaASTParser._Interface.fromInterfaceNode( _i, coid );
        _i = JavaASTToLangModel.fromInterfaceNode( _i, coid );
        
        
        return _i;
    }   
    
    
    /**
     * Load the _enum (LangModel) of a runtime enum Class <CODE>enumClazz</CODE> 
     * using the default {@code SOURCE_LOADER}
     * @param enumClazz the interface runtime class to read the source for
     * @return the _class (LangModel) of the runtime class.
     * @throws ModelLoadException if there is an exception loading the _class
     */
    public static _enum _enumOf( Class<?> enumClazz )
        throws ModelLoadException
    {
        return _enumOf( SOURCE_LOADER, enumClazz );
    }
    
    /**
     * Load the _enum (LangModel) of a runtime Class <CODE>enumclazz</CODE> 
     * the {@code sourceLoader}
     * @param sourceLoader the loader that can resolve the .java source from 
     * the class
     * @param enumClazz the runtime enum class to read the source for
     * @return the _enum (LangModel) of the runtime class.
     * @throws ModelLoadException if there is an exception loading the _class
     */
    public static _enum _enumOf( 
        SourceLoader sourceLoader, Class<?> enumClazz )
        throws ModelLoadException
    {
        EnumDeclaration ed = enumASTOf( sourceLoader, enumClazz );
        
        _enum _e = null;
        if( enumClazz.getPackage() != null )
        {
            _e = _enum.of( enumClazz.getPackage().getName(), 
                "enum " + ed.getName() );
        }
        else
        {
            _e = _enum.of( "enum " + ed.getName() );
        }
        return JavaASTToLangModel.fromEnumNode( _e, ed );
    }   
    
    private static Class getTopLevelClass( Class c )
    {
        Class dec = c.getDeclaringClass();
        if( dec != null )
        {
            return dec;
        }
        return c;
    }
    
    /**
     * Loads the root TypeDeclaration AST node for a <CODE>clazz</CODE> using 
     * the sourceLoader 
     * @param sourceLoader resolves and returns the source for the classes
     * @param clazz the class to return (could be a Top Level OR nested class)
     * @return the root TypeDeclaration AST Node
     * @throws ModelLoadException if we are unable to locate the source code
     *  for the class, or if we are unable to Parse the source code for the class
     */
    private static TypeDeclaration typeASTOf( 
        SourceLoader sourceLoader, Class<?> clazz )
        throws ModelLoadException
    {
        //System.out.println( clazz );
        //System.out.println( clazz.getDeclaringClass() );
        
        SourceStream declaringClassStream = 
            sourceLoader.sourceStream(
                getTopLevelClass( clazz ).getCanonicalName() + ".java" );
                //clazz.getDeclaringClass().getCanonicalName() + ".java" ); 
        
        CompilationUnit cu = null;
        try
        {
            cu = JavaParser.parse( declaringClassStream.getInputStream() );
            TypeDeclaration td = JavaASTParser.findTypeDeclaration( cu, clazz );
            return td;            
        }
        catch( ParseException pe )
        {
            throw new ModelLoadException(
                "could not load AST for " + clazz, pe );
        }
    }
    
    /**
     * Returns the root TypeDeclaration AST of a Java class <CODE>clazz</CODE>
     * by resolving reading the source of <CODE>clazz</CODE> from 
     * <CODE>sourceLoader</CODE>
     * @param sourceLoader resolves and returns the source for the classes
     * @param clazz the class to return (could be a Top Level OR nested class)
     * @return the root TypeDeclaration AST Node
     * @throws ModelLoadException if we are unable to locate the source code
     *  for the class, or if we are unable to Parse the source code for the class
     */
    public static ClassOrInterfaceDeclaration classASTOf ( 
        SourceLoader sourceLoader, Class<?> clazz )
        throws ModelLoadException
    {
        if( clazz.isLocalClass() )
        {
            throw new ModelLoadException(
                "Cannot load AST of Local Class " + clazz + "" );
        }
        if( clazz.isAnonymousClass() )
        {
            throw new ModelLoadException(
                "Cannot load AST of Anonymous Class " + clazz + "" );
        }
        if( clazz.isAnnotation() )
        {
            throw new ModelLoadException(
                "Class " + clazz + " is an Annotation not a class" );
        }
        if( clazz.isInterface() )
        {
            throw new ModelLoadException(
                "Class " + clazz + " is an interface, not a class" );
        }
        return (ClassOrInterfaceDeclaration)typeASTOf( sourceLoader, clazz );
    }    
    
    /**
     * Returns the root TypeDeclaration AST of a Java class <CODE>clazz</CODE>
     * by resolving reading the source of <CODE>clazz</CODE> from 
     * <CODE>sourceLoader</CODE>
     * @param sourceLoader resolves and returns the source for the classes
     * @param clazz the class to return (could be a Top Level OR nested class)
     * @return the root TypeDeclaration AST Node
     * @throws ModelLoadException if we are unable to locate the source code
     *  for the class, or if we are unable to Parse the source code for the class
     */
    public static ClassOrInterfaceDeclaration interfaceASTOf ( 
        SourceLoader sourceLoader, Class<?> clazz )
        throws ModelLoadException
    {
        if( !clazz.isInterface() )
        {
            throw new ModelLoadException(
                "Class " + clazz + " is not an interface" );
        }
        return (ClassOrInterfaceDeclaration)typeASTOf( sourceLoader, clazz );
    } 
    
    /**
     * Returns the root EnumDeclaration AST of a Java enum <CODE>clazz</CODE>
     * by resolving reading the source of <CODE>clazz</CODE> from 
     * <CODE>sourceLoader</CODE>
     * @param sourceLoader resolves and returns the source for the classes
     * @param clazz the class to return (could be a Top Level OR nested enum)
     * @return the root TypeDeclaration AST Node
     * @throws ModelLoadException if we are unable to locate the source code
     *  for the class, or if we are unable to Parse the source code for the class
     */    
    public static EnumDeclaration enumASTOf( 
        SourceLoader sourceLoader, Class<?> clazz )
        throws ModelLoadException
    {
        if( !clazz.isEnum() )
        {
            throw new ModelLoadException(
                "Class " + clazz + " is not an enum" );
        }
        return (EnumDeclaration)typeASTOf( sourceLoader, clazz );
    }
}
