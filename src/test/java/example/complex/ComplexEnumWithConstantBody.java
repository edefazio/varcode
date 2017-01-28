package example.complex;

import varcode.java.load._JavaLoad;
import varcode.java.model._enum;

/**
 * we ripped this from JavaPoet Examples, an enum
 * that has a constant that has a body (in this case the ROCK constant
 * has a body containing methods and fields
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum ComplexEnumWithConstantBody 
{
    ROCK("fist") 
    {
        @Deprecated
        public int  doIt = 100;
        
        public void sayHello()
        {
            System.out.println( "Heyo");
        }
        
        @Override
        public String toString() 
        {
            return "avalanche!";
        }
        
        public int getCount()
        {
            return 10;
        }
    },
    
    SCISSORS("peace"),
    PAPER("flat");
    
    private final String handsign;
    
    ComplexEnumWithConstantBody( String handsign ) 
    {
        this.handsign = handsign;
    }    
}
