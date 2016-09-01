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
	
	public static class Define 
		implements Var
	{
		public static Define of( String name, Object value )
		{
			return new Define( name, value );
		}
		
		private String name;
	
		private Object value;
	
		public Define( String name, Object value )
		{
			this.name = name;
			this.value = value;
		}

		public String getName() 
		{
			return name;
		}

		public Object getValue() 
		{
			return value;
		}		
	}
}
