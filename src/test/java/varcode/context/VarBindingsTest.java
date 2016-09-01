package varcode.context;

import java.util.Date;

import junit.framework.TestCase;

public class VarBindingsTest
    extends TestCase
{   
    public static class SimpleBean
    {
        private String name;
        private int count;
        private Date date;
        
        public String getName()
        {
            return name;
        }
        public void setName( String name )
        {
            this.name = name;
        }
        public int getCount()
        {
            return count;
        }
        public void setCount( int count )
        {
            this.count = count;
        }
        public Date getDate()
        {
            return date;
        }
        public void setDate( Date date )
        {
            this.date = date;
        }        
    }
    
    public void testVar()
    {
    	Date d = new Date();
    	
    	VarBindings vb = new VarBindings();
    	vb.put( Var.Define.of( "name", d ) );
    	
    	assertEquals( d, vb.get( "name" ) );
    	
    }
    
    //verify that I can set the bean properties
    //(and read 'em out after setting em)
    public void testSetBeanProperties()
    {
        Date d = new Date();
        SimpleBean sb = new SimpleBean();
        sb.setCount( 1 );
        sb.setDate( d );
        sb.setName( "A" );
        
        VarBindings vb = new VarBindings();
        vb.setBeanProperties( sb );
        
        assertEquals( 1, vb.get( "count" ));
        assertEquals( d, vb.get( "date" ));
        assertEquals( "A", vb.get( "name" ));
    }
    
    public void testSetAllBeanProperties()
    {
        Date aDate = new Date();
        Date bDate = new Date( System.currentTimeMillis() - 100000000 );
        
        SimpleBean a = new SimpleBean();
        a.setDate( aDate );
        a.setCount( 1 );
        a.setName( "A" );
        
        SimpleBean b = new SimpleBean();
        b.setDate( bDate );
        b.setCount( 2 );
        b.setName( "B" );
        
        VarBindings vb = new VarBindings();
        vb.setAllBeanProperties( a, b );
        
        assertEquals( 1, ((Object[])vb.get( "count" ))[0] );
        assertEquals( aDate, ((Object[])vb.get( "date" ))[0] );
        assertEquals( "A", ((Object[])vb.get( "name" ))[0] );
               

        assertEquals( 2, ((Object[])vb.get( "count" ))[1] );
        assertEquals( bDate, ((Object[])vb.get( "date" ))[1] );
        assertEquals( "B", ((Object[])vb.get( "name" ))[1] );
    }
    
    public void testCount()
    {
        VarBindings vb = new VarBindings();
        
        //verify that when there are no values the count is null 
        assertEquals( null, vb.get( "#name") );
        
        //put a (single) name
        vb.put( "name", "eric" );
        
        //verify I can get it out
        assertEquals( "eric", vb.get( "name" ) );
        
        //verify that the count is 1
        //assertEquals( 1, vb.get( "#name") );
        
        vb.put( "name", new String[0] );        
        //assertEquals( 0, vb.get( "#name") );
        
        vb.put( "name", new String[] {"eric"} );                
        //assertEquals( 1, vb.get( "#name") );
        
        
        vb.put( "name", new String[] {"nicolle", "dom", "eric", "theresa", "dante"} );                
        //assertEquals( 5, vb.get( "#name" ) );        
    }

    /*
    public void testFirstCap()
    {
        VarBindings vb = new VarBindings();
        
        //verify that when there are no values the count is null 
        assertEquals( null, vb.get( "^name") );
        
        //put a (single) name
        vb.put( "name", "eric" );
        
        //verify I can get it out
        assertEquals( "Eric", vb.get( "^name" ) );
        
        assertEquals( "Eric", vb.get( "^name" ) );
        
        vb.put( "name", new String[ 0 ] );    
        Object res = vb.get( "^name" );
        assertTrue( res.getClass().isArray() );
        assertTrue( Array.getLength( res ) == 0 );
        
        
        vb.put( "name", new String[] {"eric"} );
        assertEquals( ((String[])vb.get( "^name"))[0], "Eric" );
        
        vb.put( "name", new String[] {"nicolle", "dom", "eric", "theresa", "dante"} );                
        assertEquals( ((String[])vb.get( "^name" ) )[0], "Nicolle" );
        assertEquals( ((String[])vb.get( "^name" ) )[1], "Dom" );
        assertEquals( ((String[])vb.get( "^name" ) )[2], "Eric" );
        assertEquals( ((String[])vb.get( "^name" ) )[3], "Theresa" );
        assertEquals( ((String[])vb.get( "^name" ) )[4], "Dante" );
        
    }
    */

    public void testCap()
    {
        VarBindings vb = new VarBindings();
        
        //verify that when there are no values the count is null 
        //assertEquals( null, vb.get( "^name^") );
        
        //put a (single) name
        vb.put( "name", "eric" );
        
        //verify I can get it out
        //assertEquals( "ERIC", vb.get( "name" ) );
        
        vb.put( "name", new String[ 0 ] );    
        //Object res = vb.get( "^name^" );
        //assertTrue( res.getClass().isArray() );
        //assertTrue( Array.getLength( res ) == 0 );
        
        
        vb.put( "name", new String[] {"eric"} );
        //assertEquals( ((String[])vb.get( "^name^"))[0], "ERIC" );
        
        vb.put( "name", new String[] {"nicolle", "dom", "eric", "theresa", "dante"} );                
        //assertEquals( ((String[])vb.get( "^name^"))[0], "NICOLLE" );
        //assertEquals( ((String[])vb.get( "^name^"))[1], "DOM" );
        //assertEquals( ((String[])vb.get( "^name^"))[2], "ERIC" );
        //assertEquals( ((String[])vb.get( "^name^"))[3], "THERESA" );
        //assertEquals( ((String[])vb.get( "^name^"))[4], "DANTE" );
        
    }
}
