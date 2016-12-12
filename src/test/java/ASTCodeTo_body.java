

import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import java.util.List;
import varcode.java.lang.JavaMetaLang._body;
import varcode.java.lang._code;
import varcode.java.lang._if;

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
public class ASTCodeTo_body 
{
    
    public static _body toModel( Statement astStmt )
    {
        return _code.of( astStmt );
    }
    
    public static _body toModel( Expression astExpression )
    {
        return _code.of( astExpression );
    }
    
    public static _body toModel( Comment comment )
    {
        return _code.of( comment );
    }
    
    public static _body toModel( BlockStmt astBlock )
    {
        System.out.println( "BLOCK STMT" );
        ReadASTStatements.CommentQueue cq = new ReadASTStatements.CommentQueue( 
            astBlock.getAllContainedComments() );

        List<Statement> stmts = astBlock.getStmts();
        
        _code _theCode = new _code();
        
        for( int i = 0; i < stmts.size(); i++ )
        {            
            Statement stmt = stmts.get( i );
            List<Comment>comms = cq.commentsBefore( stmt );
            for( int j = 0; j < comms.size(); j++ )
            {
                _theCode.addTailCode( comms.get( j ) );
            }
            
            /*
            for( int j = 0; j < comms.size(); j++ )
            {
                System.out.println( "COMMENT" + comms.get( j ) );
            }
            */
            System.out.println( "[" + i + "]" + stmt.getClass() );
            if( stmt instanceof IfStmt )
            {                
                _theCode.addTailCode( toModel( stmt ) ); //handleIfStatment( (IfStmt) stmt );
            }   
            if( stmt instanceof ExpressionStmt )
            {
                _theCode.addTailCode( toModel( stmt ) );
                //handleExpressionStmt( (ExpressionStmt) stmt );
            }            
        }
        List<Comment> remain = cq.drain();
        for( int i = 0; i < remain.size(); i++ )
        {
            _theCode.addTailCode( remain.get( i ) );
            //System.out.println( "COMMENT" + remain.get( i ) );
        }
        return _theCode;
    }
    
    public static _if toModel( IfStmt astIfStmt )            
    {
        Expression astCondition = astIfStmt.getCondition();
        System.out.println( "Condition" + astCondition );
        
        Statement astThen = astIfStmt.getThenStmt();
        System.out.println( "Then" + astThen + "C**CLASS**" +  astThen.getClass() );
        
        Statement astElse = astIfStmt.getElseStmt();
        System.out.println( "Else" + astElse );
        
        _if _ifs = _if.is( toModel( astCondition ), toModel( (BlockStmt)astThen ) );
        
        if( astElse != null )
        {
            _ifs._else( toModel( astElse ) );
        }
        return _ifs;
    }
}
