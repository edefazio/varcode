package varcode.markup.bindml;

import varcode.author.Author;
import varcode.context.VarBindException.NullVar;
import varcode.context.VarContext;
import varcode.markup.bindml.BindMLCompiler;
import varcode.markup.mark.AddVar;

public class BindMLFunctionalTests_AddVar
    extends BindMLFunctionalTest
{

    public void testAddVar()
    {
        is( "", Author.toString( "{+a+}" ) );
        is( "1", Author.toString( "{+a+}", "a", "1" ) );
        is( "1", Author.toString( "{+a+}", "a", 1 ) );
        is( "1", Author.toString( "{+a+}", "a", new Integer( 1 ) ) );
        is( "1", Author.toString( "{+a+}", "a", new Short( (short)1 ) ) );
        is( "1", Author.toString( "{+a+}", "a", new Byte( (byte)1 ) ) );
        is( "1", Author.toString( "{+a+}", "a", new Long( 1 ) ) );
        is( "ZZ", Author.toString( "{+int+}", "int", "ZZ" ) );
    }


    public void testAddVarRequired()
    {
        try
        {
            Author.toString( "{+a*+}" );
            fail( "expected Exception" );
        }
        catch( NullVar rbn )
        {

        }
        is( "1", Author.toString( "{+a*+}", "a", "1" ) );
    }

    public void testAddVarDefault()
    {
        AddVar m = (AddVar)BindMLCompiler.parseMark( "{+a|2+}" );
        assertEquals( "a", m.getVarName() );
        assertEquals( "2", m.getDefault() );
        
        
        Object out = m.derive( VarContext.of() );
        System.out.println( out );
        assertEquals( "2", out );
        
        is( "2", Author.toString( "{+a|2+}" ) );
        is( "1", Author.toString( "{+a|2+}", "a", "1" ) );
        is( "1", Author.toString( "{+a|2+}", "a", 1 ) );

        //NOTE: the Blank String IS NOT null, so we DONT use the default
        is( "", Author.toString( "{+a|2+}", "a", "" ) );
    }

   
}
