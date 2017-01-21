<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
***load, model, compile and run custom Java code at runtime***

build, compile and run a snippet of code:
```java 
Snippet product = Snippet.of( "(int a,int b){return a * b;}" );
int prod1 = (int)product.call(2,3); //prod1 = 6
int prod2 = (int)product.call(5,4); //prod2 = 20
```
model, compile, and use a new Class / instance at runtime:
```java
//model a class
_class _model = _class.of( "Model" ).imports(UUID.class)
    .method( "public String createId",
        "return UUID.randomUUID().toString();" );
        
//compile the _model to a new Model.class and create a new "Model" instance
Object adHocModel = _model.instance();

//call a method on the adHocModel instance
String id1 = (String)Java.call( adHocModel, "createId" );
String id2 = (String)Java.call( adHocModel, "createId" );    
// will set id1 and id2 to new UUIDs like "2184d924-780d-4203-9fff-fa26c0886fc4"    
```
load the model from an existing Class / modify the model, and create a new instance
```java
package my.original;
import java.util.UUID;

public class OriginalClass
{
    public static int value = 100;
    
    public String toString()
    {
        return "Hello World";
    }
    
    public String createId()
    {
        return 
    }
}

//...Find an read the .java source for a class at runtime, then convert 
// the .java source to a _class model
_class _c = _Java.classFrom( OriginalClass.class );
// change the class Name
_c.setName("Tailored");
//change the toString method body to return the 
_c.getOnlyMethodNamed("toString").body( "return getClass().getSimpleName();") 

// compile the model and return a new instance of "Tailored"
Object tailored = _c.instance();
System.out.println( tailored );  //prints "Tailored"
```
