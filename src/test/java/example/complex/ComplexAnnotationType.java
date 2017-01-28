package example.complex;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotated Annotation Type */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ComplexAnnotationType
{   
    int count();
    
    /** Property Javadoc */
    String[] arr() default {"NONE"};
    
    /** Annotated annotation property w/ default Enum constant */
    @ThisThing NestedEnum e() default NestedEnum.A;
    
    /** Using a nested enum */
    public enum NestedEnum
    {
        A,
        B,
        C;
    }
    
    /** A nested AnnotationType */
    @interface ThisThing
    {
        
    }
}
