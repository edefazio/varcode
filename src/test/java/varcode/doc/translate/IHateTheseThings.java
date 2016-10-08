package varcode.doc.translate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})

/**
 * 
 * @author eric
 */
public @interface IHateTheseThings
{
    String value() default "THEVALUE";
    
}
