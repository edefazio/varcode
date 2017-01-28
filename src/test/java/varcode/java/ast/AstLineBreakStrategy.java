/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.ast;

/**
 * A Strategy for breaking long lines of code that exceed a certain width
 * into two or more lines:
 * 
 * specifically tries to make code "look good"
 * 
 * some strategies:
 * 
 *  //for a long method signature 
 * "public static final <E extends Enum> Map<Integer, String> getMap( String theFirstParameter, Integer theSecondParameter );"
 * 
 * //strategy 1 : break after open (
 * "public static final <E extends Enum> Map<Integer, String> getMap(
 *     String theFirstParameter, Integer theSecondParameter );"
 * 
 *  //for a long assignment
 *  "        Map<String, Set<String>> aliases = AReallyLongClassName.aReallyLongMethodName( withArg1, withArg2 );"
 * // strategy: break after = 
 *  "        Map<String, Set<String>> aliases =
 *  "            AReallyLongClassName.aReallyLongMethodName( ALongArgName1, anotherLongArgName );"
 * 
 *  //then we can apply ANOTER break strategy (break after (
 *  "        Map<String, Set<String>> aliases =" 
 *  "            AReallyLongClassName.aReallyLongMethodName("
 *  "                ALongArgName1, anotherLongArgName );"
 * 
 * So basically, the "breakAfter =" strategy is of higher precedence than
 * the break after ( strategy
 * @author Eric
 */
public class AstLineBreakStrategy 
{
    
}
