<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
**meta**programming + **model** based **source** code generation

varcode provides models of java language constructs _(a varcode ```_class``` models a java ```class```)_ 
these models can **generate and run source code dynamically**.  
 
build a model:
```java
_class _model = _class.of( "public class Model" ).imports( UUID.class )
    .method( "public String createId",
        "return UUID.randomUUID().toString();" );
```
write or export the .java source of a model:
```java
String modelSource = _model.author(); //return the .java source the model represents

//export the .java source of the _model
Export.toDir( "C:\\myapp\\src\\main\\java\\", _model );
```
create a **new instance** of the model:
```java
Object myModel = _model.instance();
```
invoke methods on the new instance:
```java
String id1 = (String)Java.call( myModel, "createId" );
String id2 = (String)Java.call( myModel, "createId" );     
```
##building & mutating hierarchial models##
building a model for a (```java \_class, \_interface, \_enum, \_annotationType```) can be done in a single compound statement, or by calling individual mutator methods on the model.  "components" of models are themseleves models, i.e. we model (```\_methods, \_fields,  \parameters, \annotations, \imports...```)
```java 

_class _c = _class.of("package ex.mutable;",
    "import java.io.Serializable;"
    "@Deprecated",     
    "public class MyMutableModel implements Serializable" );
_method _m = _method.of( "/** create a random number */", 
    "@Generated",
    "public static final double random()", 
    "return Math.random();" );
_field _f = _field.of( "/** field javadoc */", "public static int ID = 100;"); 

_c.add( _m ); //add the "random" method to the _class 
_c.add( _f ); //add the "ID" field to the _class
```    

##metaprogramming##
if you already the .java source of (class, enum, interface, or annotationType) varcode can **import / build a the correct model ( \_class, \_enum, \_interface, \_annotationType) from the source automatically**. then, just as before, we can mutate the model, create new instances, and call methods on the "adHoc" models.

```java
_class _c = Java._classFrom( OriginalClass.class ); //build a _class from the .java source of a class
_c.setName("Tailored");// change the class Name on the model
_c.getOnlyMethodNamed("toString").body( "return getClass().getSimpleName() + value;") //change the method body

//after we "import" the model, we can change it in full metaprogramming fashion 
Object tailored = _c.instance(); // compile the model and return a new instance of "Tailored"
System.out.println( tailored );  //prints "Tailored100"
```
