package varcode.doc.lib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import varcode.context.VarContext;
import varcode.script.VarScript;

/**
 * Current Date time (with optional format)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum DateTime
{
    ; //singleton enum idiom
    
    public static final FormatDate DATE_FORMAT = new FormatDate();
    
    public static final TimeMillis TIME_MILLIS = new TimeMillis();
    
    /** 
     * Retrieves the current Date and
     * (optionally) using a SimpleDateFormat to format the date 
     */
    public static class FormatDate
        implements VarScript
    {
        private FormatDate() 
        { }
        
        public Object eval( VarContext context, String input )
        {            
            if( input != null && input.trim().length() > 0 )
            {
                SimpleDateFormat sdf = new SimpleDateFormat( input  );
                return sdf.format( new Date() );
            }
            return new Date();            
        }

		public String toString()
		{
			return this.getClass().getName() + "." + super.toString();
		}
		
		public void collectAllVarNames( Set<String> collection, String input ) 
		{
			
		}
    }
    
    public static class TimeMillis
    	implements VarScript
    {
        private TimeMillis()
    	{ }
    	
    	public Object eval( VarContext context, String input )
        {
    		return System.currentTimeMillis();            
        }

 		public String toString()
 		{
 			return this.getClass().getName() + "." + super.toString();
 		}
 		
 		public void collectAllVarNames( Set<String> collection, String input ) 
 		{
 		}
    }
    
    public static class TimeNanos
		implements VarScript
	{
    	private TimeNanos()
    	{ }
	
    	public Object eval( VarContext context, String input )
    	{
    		return System.nanoTime();            
    	}        	
		
		public String toString()
		{
			return this.getClass().getName() + "." + super.toString();
		}

		public void collectAllVarNames( Set<String> collection, String input ) 
		{ 
		}
		
	}
}
