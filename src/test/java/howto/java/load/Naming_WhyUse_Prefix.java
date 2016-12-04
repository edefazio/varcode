package howto.java.load;

import varcode.java.metalang._class;

/**
 * Just a discussion on why we made the decision to defy Oracle/Java coding 
 * conventions
 * <A HREF="http://www.oracle.com/technetwork/java/codeconventions-135099.html">
 * </A>
 * 
 * specifically:
 * <BLOCKQUOTE>
 * Class names should be nouns, in mixed case with the first letter of each 
 * internal word capitalized. Try to keep your class names simple and descriptive. 
 * Use whole words-avoid acronyms and abbreviations 
 * (unless the abbreviation is much more widely used than the long form, such as 
 * URL or HTML).
 * </BLOCKQUOTE>
 * 
 * <BLOCKQUOTE>
 * Except for variables, all instance, class, and class constants are in mixed 
 * case with a lowercase first letter. Internal words start with capital 
 * letters. Variable names should not start with underscore _ or dollar sign $ 
 * characters, even though both are allowed.
 * 
 * Variable names should be short yet meaningful. The choice of a variable name 
 * should be mnemonic- that is, designed to indicate to the casual observer the 
 * intent of its use. One-character variable names should be avoided except for 
 * temporary "throwaway" variables. Common names for temporary variables are i, 
 * j, k, m, and n for integers; c, d, and e for characters.
 * </BLOCKQUOTE>
 * 
 * @author M. Eric DeFazio
 */
public class Naming_WhyUse_Prefix 
{
   public static void main( String[] args )
   {
       // To Start: 
       // we need to have a way of differentiating a MetaLang Model of a class
       // (in which we use "_class" ) to "mean" a metalang Model of a the 
       // definition of a "class"
       //we use _ (prefix) 
       _class _sixDemonBag = _class.of( "public class SixDemonBag" );
       
       //alternatively, we could have chosen a more "specific" name, 
       //something more descriptive, like MetaLangClass, or ClassMetaLang, 
       //or ClassModel, lets go with ClassModel
       ClassModel sixDemonBagClassModel = ClassModel.of("public class A");
    
       //so (above) we are modelling a Class declaration of a SixDemonBag 
       //, to make the name of the variable have meaning, we add "ClassModel"
       // postfix, we aren't REALLY modelling a Six Demon Bag, but rather we
       // are modelling the MODEL of a Six Demon Bag.
       
       // when you sc
       
   }
   
   private static class ClassModel 
   { 
       public static ClassModel of( String classDecl )
       {
           return new ClassModel();
       }
   }
}
