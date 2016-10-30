package varcode.context;

/**
 * Named Variable
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Var 
{
	public String getName();
	
	public Object getValue();
	
    /** Simple Var implementation */
	public static class Define 
		implements Var
	{
		public static Define of( String name, Object value )
		{
			return new Define( name, value );
		}
		
		private final String name;
	
		private final Object value;
	
		public Define( String name, Object value )
		{
			this.name = name;
			this.value = value;
		}

        @Override
		public String getName() 
		{
			return name;
		}

        @Override
		public Object getValue() 
		{
			return value;
		}		
	}
}
