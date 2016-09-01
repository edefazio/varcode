package varcode.markup.mark;

import varcode.markup.codeml.CodeMLParser;
import varcode.markup.mark.CutComment;
import junit.framework.TestCase;

public class CutCommentTest
    extends TestCase
{
    public void testCut()
    {
        //CutComment cc = CutComment.of( "/*{- cut comment }*/", 10 );
        CutComment cc = CodeMLParser.CutCommentMark.of( 
            CodeMLParser.CutCommentMark.OPEN_TAG +" cut comment "+ 
            CodeMLParser.CutCommentMark.CLOSE_TAG, 10 );
        assertEquals( cc.lineNumber, 10 );
        assertEquals( cc.text, "/*{- cut comment -}*/" );
        assertEquals( cc.getCutText(), " cut comment ");
    }
}
