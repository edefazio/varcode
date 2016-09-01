package varcode.markup;

import java.util.UUID;

import varcode.markup.VarNameAudit;
import junit.framework.TestCase;

public class VarNameAuditTest
    extends TestCase
{
    public void testValidJavaIdentifier()
    {
        assertTrue( "a".equals( 
            VarNameAudit.BASE.audit( "a" ) ) );
        
        assertTrue( "A".equals( 
            VarNameAudit.BASE.audit( "A" ) ) );
        
        assertTrue( "_A".equals( 
            VarNameAudit.BASE.audit( "_A" ) ) );
        
        assertTrue( "$A".equals( 
            VarNameAudit.BASE.audit( "$A" ) ) );
        
        for( char start = 'a'; start<= 'z'; start++ )
        {
            String name = start + "";
            assertTrue( name.equals( VarNameAudit.BASE.audit( 
                name ) ) ); 
            
            name = start + UUID.randomUUID().toString().replace( '-', '_' );
            
            assertTrue( name.equals(VarNameAudit.BASE.audit( 
                name ) ) ); 
        }
        
        for( char start = 'A'; start<= 'Z'; start++ )
        {
            String name = start + UUID.randomUUID().toString().replace( '-', '_' );
            assertTrue( name.equals( VarNameAudit.BASE.audit( 
                name ) ) ); 
        }
        
        assertTrue( 
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".equals(
                VarNameAudit.BASE.audit( 
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890" ) ));
        
        try
        {
            VarNameAudit.BASE.audit( " A" );
            fail ("Expected Exception");
        }
        catch ( MarkupException cme )
        {
            //expected
        }
        
        try
        {
            VarNameAudit.BASE.audit( "A " );
            fail ("Expected Exception");
        }
        catch ( MarkupException cme )
        {
            //expected
        }
        
       
    }
    
    /*
    public void testAnyNonEmpty()
    {
        assertTrue( "a".equals(  VarNameAudit.AnyNonEmpty.INSTANCE.audit( "a" ) ) );
        assertTrue( "!@#@%!@*&(#$".equals( 
            VarNameAudit.AnyNonEmpty.INSTANCE.audit( "!@#@%!@*&(#$" ) ) );
        
        try
        {
            VarNameAudit.AnyNonEmpty.INSTANCE.audit( "" );
            fail( "Expected Exception" );
        }
        catch( ParseException cme )        
        {
            //expected
        }
        
        try
        {
            VarNameAudit.AnyNonEmpty.INSTANCE.audit( null );
            fail( "Expected Exception" );
        }
        catch( ParseException cme )        
        {
            //expected
        }
        
        try
        {
            VarNameAudit.AnyNonEmpty.INSTANCE.audit( " " );
            fail( "Expected Exception" );
        }
        catch( ParseException cme )        
        {
            //expected
        }
        
        try
        {
            VarNameAudit.AnyNonEmpty.INSTANCE.audit( "     " );
            fail( "Expected Exception" );
        }
        catch( ParseException cme )        
        {
            //expected
        }        
    }
    */
}
