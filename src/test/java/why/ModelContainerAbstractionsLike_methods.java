package why;

/**
 * Wanted to write down (in code form) why I decided it was a good idea to 
 * create these _model "container" abstractions (like: 
 * <UL>
 *   <LI>_methods
 *   <LI>_constructors
 *   <LI>_nests
 *   <LI>_parameters
 *   <LI>_modifiers
 * </UL>
 * when I could have just used 
 * <UL>
 *   <LI>List<_method>
 *   <LI>List<_constructor>
 *   <LI>List<_model>
 *   <LI>List<_parameter>
 *   <LI>List<_modifier>
 * </UL> 
 * 
 * basically, we want the option to model the nuances of the Rules of the
 * Java Language there is more intrinsic knowledge about a group of methods from
 * a real-life a class ( they cannot have methods that "share" the same Signature
 * etc.) they often have a precedence ordering (static methods are conventionally
 * written at the TOP of the class, then abstract methods, then instance methods...
 * 
 * All of these things need to be modeled in a single place, otherwise we end up
 * with all these helper classes (MethodOrganizerUtil, MethodDuplicateUtil) 
 * required to validate a group of methods, or organize methods, etc. 
 * 
 * IF we modeled a group of methods as a List<_method>... then whenever we added
 * a method to the List, we would need to remind ourselves to validate that 
 * the method we are about to add does not share a signature with an existing 
 * method within the LIst...
 * 
 * There are "ensemble" rules for a group of _methods, and modeling these
 * rules should take place in the same abstraction that houses the entities.
 * 
 * @author Eric
 */
public class ModelContainerAbstractionsLike_methods 
{
    
}
