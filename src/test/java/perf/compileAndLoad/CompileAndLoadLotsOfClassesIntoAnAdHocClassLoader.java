
package perf.compileAndLoad;

import java.util.UUID;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.JavaSourceFile;
import varcode.java.adhoc.AdHocClassPublisher;
import varcode.java.adhoc.SourceFolder;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class CompileAndLoadLotsOfClassesIntoAnAdHocClassLoader
{
    public static void main( String[] args )
        throws Exception
    {
        //IF we build a SourceFolder and call the compiler one on :
        int classCount = 
            //10;       //took 360ms, 360ms, 372 millis to compile and load 10 classes 
            //100;    //took 406ms, 422ms, 448ms to compile and load 100 classes
            //1000;   //took 797ms, 781ms to compile and load 1000 classes
            10;  //took 1890ms, 2766ms to compile and 9078 total time
        
        //IF we call the compiler for EACH individual java file, it takes 
        // CONSIDERABLY LONGER
        //  10000  //took 175378ms to compile Total time: 2:56.778s
                  //TOOK 158213ms to Compile Total Time: 2:42.576s 
                  //TOOK 230170ms Total time: 3:53.684s
        
                  // TOOK 60357 Total time: 1:03.610s using "cached" Javac compiler 
                  // AND cached Standard File Manager
        //230170
        
        //SO, basically DONT call the compiler for each individual class,
        //rather, create a workspace, add files, then call the compiler once
        
        //_class[] _cs = new _class[ classCount ];
        JavaSourceFile[] JF = new JavaSourceFile[ classCount ];
        for( int i = 0; i < classCount; i++ )
        {
            JF[i] //= _cs[i] 
                = _class.of("public class MyClass" + i )
                .field( "public static final String ID = \""+UUID.randomUUID().toString()+"\";" )
                    .toJavaFile(  );
        }
        
        //AdHocClassLoader adHocCL= compileOneAtATime( JF );
        
        AdHocClassLoader adHocCL = compileAllAtSameTimeInOneWorkspace( JF ); //_cs );
        
        long start = System.currentTimeMillis();
        AdHocClassPublisher.publishToParent( adHocCL );        
        long end = System.currentTimeMillis();
        
        System.out.println( "Promoting, took "+ (end - start) );
        
    }
    
    private static AdHocClassLoader compileAllAtSameTimeInOneWorkspace( 
        JavaSourceFile[] javaFiles ) //_class[] _cs )
    {
        //long start = System.currentTimeMillis();
        long start = System.currentTimeMillis();
        SourceFolder ws = new SourceFolder();
        ws.add( javaFiles );        
        AdHocClassLoader adHocCL = AdHoc.compile( ws );   
        long end = System.currentTimeMillis();
        System.out.println( "TOOK "+ (end - start) );
        return adHocCL;
    }
    
    public static AdHocClassLoader compileOneAtATime( JavaSourceFile[] javaFiles )        
    {
        long start = System.currentTimeMillis();
        SourceFolder ws = new SourceFolder();
        AdHocClassLoader adHocClassLoader = new AdHocClassLoader();
        for( int i = 0; i< javaFiles.length; i++ )
        {
            ws.clear();
            ws.add( javaFiles[ i ] );
            AdHoc.compile( ws, adHocClassLoader );    
        }
        long end = System.currentTimeMillis();
        System.out.println( "TOOK "+ (end - start) );
        return adHocClassLoader;
    }
}
