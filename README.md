<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
#**meta**-**sourcery**#
##**meta**programming + model based **source** code generation##
varcode provides models of java language constructs (i.e. a ```_class``` models a java ```class```) 
varcode uses these models to **generate source code and/or compile and run new generated code dynamically**.  
 
build a model:
```java
_class _model = _class.of( "public class Model" ).imports( UUID.class )
    .method( "public String createId",
        "return UUID.randomUUID().toString();" );
```
write the .java source code:
```java
String javaSource = _model.author(); //return the .java source the model represents

//export the .java source of the _model
Export.toDir( "C:\\myapp\\src\\main\\java\\", _model );
```
compile a **new instance** of the model:
```java
Object myModel = _model.instance();
```
invoke methods on the new instance:
```java
String id1 = (String)Java.call( myModel, "createId" );
String id2 = (String)Java.call( myModel, "createId" );     
```
##metaprogramming##
building a large (class, enum, interface, annotationType) in varcode is simple with the fluent model api.     
if you already the .java source of (class, enum, interface, or annotationType) varcode can **build a model ( \_class, \_enum, \_interface, \_annotationType) automatically** 

```java
_class _c = Java._classFrom( OriginalClass.class ); //build a _class from the .java source of a class
_c.setName("Tailored");// change the class Name on the model
_c.getOnlyMethodNamed("toString").body( "return getClass().getSimpleName() + value;") //change the method body

Object tailored = _c.instance(); // compile the model and return a new instance of "Tailored"
System.out.println( tailored );  //prints "Tailored100"
```
