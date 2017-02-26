/*
 * Copyright 2017 Eric.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.draft;

/**
 *
 * @author Eric
 */
public class Es
{
    public static enum TestEnum {
        ONE, TWO, THREE;
    }

    public static void main(String param [] ) {
        Es.enumValues( TestEnum.class);
        
    }
    
    public static <E extends Enum<E>> void enumValues( Class<E> enumData ) {
        for( Enum<E> enumVal: enumData.getEnumConstants() ) {  
            System.out.println( enumVal.toString() );
        }  
    }
}
