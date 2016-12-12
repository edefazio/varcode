
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

public class CommentQueue
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
