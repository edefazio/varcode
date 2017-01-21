<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
**meta**programming + **model** based **source** code generation

varcode provides models of java language constructs _(a varcode ```_class``` models a java ```class```)_ 
these models can **generate and run source code dynamically**.  
 
build a model:
```java
_class _model = _class.of( "public class Model" ).imports( UUID.class )
    .method( "public String createId()",
        "return UUID.randomUUID().toString();" );
```
write or export the .java source of a model:
```java
Export.toDir( "C:\\myapp\\src\\main\\java\\", _model );
```
...generates/ exports the following .java source code:
```java
import java.util.UUID;

public class Model
{
    public String createId(  )
    {
        return UUID.randomUUID().toString();
    }
}
```
to create a **new instance** of the model dynamically:
```java
Object myModel = _model.instance();
```
invoke methods on the new instance:
```java
String id1 = (String)Java.call( myModel, "createId" );
String id2 = (String)Java.call( myModel, "createId" );     
```
##building & mutating hierarchial models##
building a model for a (```java _class, _interface, _enum, _annotationType```) can be done in a single compound statement, or by calling individual mutator methods on the model.  "components" of models are themseleves models, i.e. we model (```_methods, _fields,  _parameters, _annotations, _imports...```)
```java 
_class _c = _class.of("package ex.mutable;",
    _import.of(Serializable.class),
    "@Deprecated",     
    "public class MyMutableModel implements Serializable" );
    
_method _m = _method.of( "/** create a random number */", 
    "@Generated",
    "public static final double random()", 
    "return Math.random();" );
    
_field _f = _field.of( "/** field javadoc */", 
    "public static int ID = 100;"); 

_c.add( _m ); //add the "random" method to the _class 
_c.add( _f ); //add the "ID" field to the _class
```    

##metaprogramming##
to support metaprogramming, varcode can accept the .java source of (class, enum, interface, or annotationType) and **build a the correct ```( _class, _enum, _interface, _annotationType )``` model from the source automatically**. the metaprogramming "process" is simple:
1. build a model from the .java source 
2. modify the model 
3. compile and instantiate and use the "ad hoc" modified model

```java
// 1. read in the .java source and create a _class model
_class _c = Java._classFrom( OriginalClass.class ); //build a _class from the .java source of a class

// 2. modify the model
_c.setName("Tailored");// change the class Name on the model
_c.field("private static final int ID = 100;");
_c.getOnlyMethodNamed("toString").body( "return getClass().getSimpleName() + value;") //change the method body

// 3.  
Object tailored = _c.instance(); // create a new instance of "Tailored"
System.out.println( tailored );  //prints "Tailored100"
```
