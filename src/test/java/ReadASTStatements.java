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


import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.lang._if;

/**
 *
 * @author Eric
 */
public class ReadASTStatements 
    extends TestCase
{
    public static class MethodWithSomeCode
    {
        private final static int a = 100;
        
        public static void method ()
        {
            
            int w = 3; 
            /*1*/
            if( a == 100 )
            {
                //2
                System.out.println( "BLAH" ); //3
                /*4*/
            }
            //5
            else
            {
                System.out.println( "NOT =" );
            }
            /* comment */
            for( int i=0; i< 100; i++ )
            {
                System.out.println( " i " + i );
            }
            boolean b = true;
            while( b )
            {
                System.out.println( "TRUW");
                b = false;
            }
            do
            {
                b= true;
            }
            while( b != true );
            
            try
            {
                System.out.println( "HEY");
                
            }
            catch( Exception e )
            {
                System.out.println ("exception ");
            }
            
            int count = 2;
            switch( count )
            {
                case 1: 
                    break;
                case 2:                     
                case 3:
                    System.out.println( "2 or 3");
                default:
                    System.out.println( "def" );                    
            }
            List l = new ArrayList();
            l.removeIf( a -> a == null );
            
            new String( "3" );
            throw new RuntimeException( "F" );
            //continue
            //break
        }
    }
    

    
    public MethodDeclaration getMethod( TypeDeclaration astTypeDef )
    {
        List<BodyDeclaration> members = astTypeDef.getMembers();
        for( int i = 0; i < members.size(); i++ )
        {
            if( members.get( i ) instanceof MethodDeclaration )
            {
                return (MethodDeclaration)members.get( i );
            }            
            
        }
        return null;        
    }
    
    public static class CommentQueue
    {
        int index = -1;
        List<Comment>comments = new ArrayList<Comment>();
        
        public CommentQueue( List<Comment> comments )
        {
            this.comments.addAll( comments );
            this.comments.sort( new CommentPositionComparator() );
        }
        
        static class CommentPositionComparator
            implements Comparator<Comment>
        {

            @Override
            public int compare( Comment o1, Comment o2 ) 
            {
                //return o2.getBegin().compareTo( o1.getBegin() );
                return o1.getBegin().compareTo( o2.getBegin() );
            }            
        }
        public List<Comment> toQueue()
        {           
            return comments;
        }
        
        public List<Comment> drain()
        {
            List<Comment> remaining = new ArrayList<Comment>();
            
            for( int i = this.index + 1; i < this.comments.size(); i++ )
            { 
                remaining.add( this.comments.get( i ) );
            }
            return remaining;
        }
        
        public void reset()
        {
            this.index = -1;
        }
        
        public List<Comment> commentsBefore( Node node )
        {            
            List<Comment> commentsBeforeNode = new ArrayList<Comment>();
            int incr = 0;
            for( int i = this.index + 1; i < this.comments.size(); i++ )
            {              
                
                Comment c = this.comments.get( i );
                System.out.println( node.getBegin() + " " + c.getBegin() );
                if( node.getBegin().isAfter( c.getBegin() ) )
                {
                    commentsBeforeNode.add( c );
                    incr++;
                }
                else
                {
                    break;
                }
            }
            this.index += incr;
            System.out.println( "FOUND "+ commentsBeforeNode.size());
            return commentsBeforeNode;
        }
    }
    
    public static class MethodWithIf
    {
        public static void methodWithIf()
        {            
            if( ( System.currentTimeMillis() & 1L ) == 1 )
            {
                System.out.println( "Odd" );
            }
        }
    }
    public void testThing( )
    {
        TypeDeclaration astTypeDef = 
            _Java.astTypeDeclarationFrom( MethodWithIf.class ); //MethodWithSomeCode.class );
        
        MethodDeclaration md = getMethod( astTypeDef );
        
        CommentQueue cq = new CommentQueue( 
            md.getAllContainedComments());

        BlockStmt bs = md.getBody();
        List<Statement> stmts = bs.getStmts();
        for( int i = 0; i < stmts.size(); i++ )
        {            
            Statement stmt = stmts.get( i );
            List<Comment>comms = cq.commentsBefore( stmt );
            for( int j = 0; j < comms.size(); j++ )
            {
                System.out.println( "COMMENT" + comms.get( j ) );
            }
            
            System.out.println( "[" + i + "]" + stmt.getClass() );
            if( stmt instanceof IfStmt )
            {
                
                handleIfStatment( (IfStmt) stmt );
            }   
            if( stmt instanceof ExpressionStmt )
            {
                handleExpressionStmt( (ExpressionStmt) stmt );
            }            
        }
        List<Comment> remain = cq.drain();
        for( int i = 0; i < remain.size(); i++ )
        {
            System.out.println( "COMMENT" + remain.get( i ) );
        }
    }
    
    
    public static void handleExpressionStmt( ExpressionStmt es )
    {
        System.out.println( es.getExpression().toString() );
    }
    
    public static void handleIfStatment( IfStmt ifStmt )            
    {
        _if _ifs = ASTCodeTo_body.toModel( ifStmt );
        
        System.out.println( _ifs );
        
        Expression e = ifStmt.getCondition();
        System.out.println("Condition" + e );
        
        Statement thenS = ifStmt.getThenStmt();
        System.out.println("Then" + thenS );
        
        Statement elseS = ifStmt.getElseStmt();
        System.out.println("Else" + elseS );
        
    }
}
