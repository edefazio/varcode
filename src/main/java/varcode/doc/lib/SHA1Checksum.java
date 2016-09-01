package varcode.doc.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.context.VarScope;
import varcode.doc.Directive;
import varcode.doc.DocState;

/**
 * Generates SHA-1 Checksums of: 
 * <UL>
 *  <LI>the Dom (based on the Original Markup Text) during Directive pre-processing
 *  <LI>the tailored text during post-processing  
 * </UL>
 * 
 * Creates properties
 * <UL>
 * <LI>"dom.checksum.sha1" during pre-processing (stored in VarContext metadata)
 * <LI>"tailor.checksum.sha1" during post-processing (stored in VarContext metadata)
 * </UL>
 * 
 * https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum SHA1Checksum
	implements Directive.PreProcessor, Directive.PostProcessor
{
	INSTANCE;
	
	/*{-?(removeLog==true):*/
	private static final Logger LOG = 
        LoggerFactory.getLogger( SHA1Checksum.class );
	/*-}*/
	
	public static final String SHA1 = "SHA-1";
	
	/** The Checksum is written as (2) properties in the Metadata of the TailorState */
	
	/** Hierarchical Property Name to bind the Tailor SHA1 checksum to in the metadata */
	public static final String TAILOR_CHECKSUM_SHA1_NAME = 
		"tailor.checksum.sha1";
	
	/** Hierarchical Property Name to bind the Dom SHA1 checksum to in the metadata */
	public static final String DOM_CHECKSUM_SHA1_NAME = 
		"dom.checksum.sha1";

	public void preProcess( DocState tailorState )
	{
		//LOG.trace( "    In Checksum Pre Process" );		
		ByteArrayInputStream domInputStream = 
			new ByteArrayInputStream 
				( tailorState.getDom().getMarkupText().getBytes() );
		
		String domChecksum = generateChecksum( domInputStream );
		
		LOG.debug( DOM_CHECKSUM_SHA1_NAME + ":" + domChecksum );
		tailorState.getContext().getOrCreateBindings( VarScope.METADATA ).put( 
			DOM_CHECKSUM_SHA1_NAME, domChecksum );
		//tailorState.getContext().getMetadata().put( DOM_CHECKSUM_SHA1_NAME, domChecksum );	    	
	}
	
	public void postProcess( DocState tailorState ) 
	{
		//LOG.debug( "    In Checksum Post Process" );
		ByteArrayInputStream bais = new ByteArrayInputStream 
			( tailorState.getTranslateBuffer().toString().getBytes() );
				
		String tailorChecksum = generateChecksum( bais );
		
		LOG.debug( TAILOR_CHECKSUM_SHA1_NAME + ":" + tailorChecksum );
		//tailorState.getContext().getMetadata().put( TAILOR_CHECKSUM_SHA1_NAME, tailorChecksum );
		tailorState.getContext().getOrCreateBindings( VarScope.METADATA ).put( 
			TAILOR_CHECKSUM_SHA1_NAME, tailorChecksum );
	}
	
	public String generateChecksum( ByteArrayInputStream byteStream )
	{
	    MessageDigest md = null;
		byte[] dataBytes = new byte[ 1024 ];
		
		try
		{
			md = MessageDigest.getInstance( SHA1 );
		}
		catch( NoSuchAlgorithmException e )
		{
			throw new VarException( "CheckSum Algorithm provided  \""
		        + SHA1 + "\" is not valid" );
		}
		int nread = 0; 
	    try
	    {
	    	while( ( nread = byteStream.read( dataBytes ) ) != -1 ) 
	    	{
	    		md.update( dataBytes, 0, nread );
	    	}
	    }
	    catch( IOException ioe )
	    {
	    	
	    }
	    byte[] mdbytes = md.digest();
	    
	    //convert the byte to hex format
	    StringBuffer sb = new StringBuffer();
	    for( int i = 0; i < mdbytes.length; i++ ) 
	    {
	    	sb.append(
	    		Integer.toString( 
	    			( mdbytes[ i ] & 0xff ) + 0x100, 16 ).substring( 1 ) );
	    }
	    return sb.toString();
	}
	
	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}

}
