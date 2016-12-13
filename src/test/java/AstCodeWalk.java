
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.IOException;
import varcode.java._Java;
import varcode.java.ast.JavaAst;
import varcode.java.ast.JavaAstCodeVisitor;

/*
 * Copyright 2016 Eric.
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

/**
 *
 * @author Eric
 */
public class AstCodeWalk 
{
    
    public static class Methods
    {
        public static void methodWithIf()
            throws RuntimeException, IOException
        {            
            if( ( System.currentTimeMillis() & 1L ) == 1 )
            {
                System.out.println( "Odd" );
            }
        }
        
        public static int IfElse( String name )
        {
            if( name == null )
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
        
        public static int IfElseIf( String name )
        {
            if( name == null )
            {
                return 0;
            }
            else if( name.equals( "Eric" ) )
            {
                return 1;
            }
            return 2;
        }
        
        public static void For()
        {
            for(int i=0; 1<100; i++ )
            {
                System.out.println( " i " + i );
            }
        }
        
        public static void While()
        {
            while( ( System.currentTimeMillis() & 1L ) == 1L )
            {
                System.out.println( "Odd MIllis" );
            }
        }
        
        public static void DoWhile()
        {
            do
            {
                System.out.println("A");
            }
            while( ( System.currentTimeMillis() & 1L ) == 1L );
        }
        
        public static void Try()
        {
            try
            {
                System.out.println( "A"  );
            }
            catch( Exception e )
            {
                System.out.println( "exception " + e );
            }
        }
        
        public static void TryMultiCatch()
        {
            try
            {
                System.out.println( "A"  );
            }
            catch( RuntimeException e )
            {
                System.out.println( "exception " + e );
            }
            catch( Throwable t )
            {
                System.out.println( "exception " + t );
            }
            finally
            {
                System.out.println( "Finally " );
            }
        }
        
        public void Switch( int i )
        {            
            switch( i )
            {
                case 1: System.out.println( "One" );
                case 2: System.out.println( "2" ); break;
                case 3: System.out.println( "3" );
                    System.out.println( "or maybe more");
                    break;
                default: System.out.println( "theDefault" );    
            }
        }
        
        /** Javadoc */
        @Deprecated
        public void WithAnns( @Deprecated String s, @Deprecated int count )
            throws RuntimeException
        {
            /*1*/
            int i=0; /*2*/
            String name = "Eric"; // 3 this is Eric
        }
    }

    
     
    public static void main( String[] args )
    {
        TypeDeclaration astTypeDef = 
            _Java.astTypeDeclarationFrom( Methods.class );
        
        MethodDeclaration[] astMethods = JavaAst.findAllMethods( astTypeDef );
        
        for(int i=0; i< astMethods.length; i++ )
        {
            MethodDeclaration astMethod = astMethods[ i ];
            JavaAstCodeVisitor jcv = new JavaAstCodeVisitor();
            astMethod.accept( jcv, args );
            System.out.println( jcv.getSource() );
        }
        /*
        astMethod = astMethods[ 1 ];
        jcv = new JavaAstCodeVisitor();
        astMethod.accept( jcv, args );
        System.out.println( jcv.getSource() );
        
        astMethod = astMethods[ 2 ];
        jcv = new JavaAstCodeVisitor();
        astMethod.accept( jcv, args );
        System.out.println( jcv.getSource() );
        
        astMethod = astMethods[ 3 ];
        jcv = new JavaAstCodeVisitor();
        astMethod.accept( jcv, args );
        System.out.println( jcv.getSource() );
        */        
    }    
}
