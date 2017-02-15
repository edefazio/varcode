/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.markup.form;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.context.VarBindException.NullVar;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.translate.TranslateBuffer;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.Bind;

/**
 * {@code Form} of text and variable "blanks" that can be populated.
 * {@code VarForm} also manages handling a {@code SeriesOfForms} when the data
 * provided (to fill in the forms) has cardinality > 1.
 *
 * for example:
 * <PRE>String form = "{+type*} {+fieldName*}, "</PRE>
 *
 * <PRE>
 * VarForm declareField = ForMLParser.INSTANCE.fromString( form );
 * if we pass in the (2) required* vars to derive the {@code VarForm}:
 *
 * VarContext vc = VarContext.of(
 *   "type", "int",
 *   "fieldName", "age" );
 *
 * when we derive() the form with the VarContext; we get:
 * "int age"
 *
 * NOTE: since we only have (1) instance of the VarForm, the trailing ", "
 * from the Form Definition:
 * "{+type*} {+fieldName*}, "
 *
 * ...is not printed (there are "rules" as to characters that are (by default)
 * Between Tokens {@see BetweenTokens})
 *
 * ...if we pass in more than one instance of each field, we create a
 * {@code SeriesOfForms}.
 *
 * VarContext vc = VarContext.of( "type", new String[] { "int", "String"},
 * "fieldName", "new String[]{ "age", "name"} );
 *
 * ...the output is: "int age, String name"
 *
 * Notice that here, we used the {@code BetweenToken} ", " only between the (2)
 * instances
 * </PRE>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarForm
    implements Form
{
    public final FormTemplate formTemplate;

    public final SeriesFormatter seriesFormatter;

    public VarForm(
        FormTemplate formTemplate,
        SeriesFormatter seriesFormatter )
    {
        this.formTemplate = formTemplate;
        this.seriesFormatter = seriesFormatter;
    }

    @Override
    public String toString()
    {
        return this.formTemplate.toString();
    }

    /**
     * Gets the form in textual form
     *
     * @return the FormTemplate
     */
    public FormTemplate getFormTemplate()
    {
        return formTemplate;
    }

    
    @Override
    public Set<String> getVarNames()
    {
        return this.formTemplate.getVarNames();
    }

    /**
     * NOTE: this will fill ONE, if there is a list, we need another method
     */
    private String fillOne( Context context, TranslateBuffer tb )
    {
        try
        {
            Bind[] bindMarks = this.formTemplate.getBindMarks();
            String[] fills = new String[ bindMarks.length ];
            for( int i = 0; i < bindMarks.length; i++ )
            {
                fills[ i ] = tb.translate(
                    bindMarks[ i ].derive( context ) );
            }
            return formTemplate.getBlankBinding().bind( (Object[])fills );
        }
        catch( Exception e )
        {
            throw new VarBindException(
                "Unable to Author VarForm :" + N + this.toString(), e );
        }
    }

    private static final String N = System.lineSeparator();

    /**
     * Given the context, determine the "cardinality" (or the Loop count) for
     * the number of form instances to create:
     *
     * For example with Form: "{+type} {+name}, " <BR>
     *
     * if we pass in the context:<BR>
     * <PRE>
     * VarContext context =
     *     VarContext.of(
     *           "type", new String[]{"int", "String", "float"},
     *           "name", new String[]{"A", "B", "C"} );
     * </PRE> ...then the cardinality is (3), since we have to
     *
     * @param context
     * @return
     */
    private int getFormInstanceCount( Context context )
    {
        Set<String> varNames = getVarNames();

        if( varNames.isEmpty() ) //some VarForms dont have need of vars
        {
            return 1;
        }
        int cardinality = 0;
        Iterator<String> it = varNames.iterator();

        //now that I have all the var names, I need to find
        // the cardinality of each, so that I can create the appropriate amount
        // of form instances
        for( int i = 0; i < varNames.size(); i++ )
        {
            String next = it.next();
            //Object value = context.get( next );
            Object value = context.getVarResolver().resolveVar( context, next );
            if( value != null )
            {
                if( value.getClass().isArray() )
                {
                    int length = Array.getLength( value );
                    if( length > cardinality )
                    {
                        cardinality = length;
                    }
                }
                else if( value instanceof Collection )
                {
                    Collection<?> c = (Collection<?>)value;
                    int length = c.size();
                    if( length > cardinality )
                    {
                        cardinality = length;
                    }
                }
                else
                {
                    if( cardinality == 0 )
                    {
                        cardinality = 1;
                    }
                }
            }
            else
            {
                if( this.formTemplate.isRequiredVar( next ) )
                {
                    throw new NullVar( next, getText() );
                }
            }
        }
        return cardinality;
    }

    public void deriveTo( Context context, TranslateBuffer out )
    {
        //find out the number of Forms
        int cardinality = getFormInstanceCount( context );

        //now generate Each Form
        String[] eachForm = new String[ cardinality ];
        for( int i = 0; i < cardinality; i++ )
        {
            eachForm[ i ] = tailorAt( i, context, out );
        }
        //	use the form separator
        out.append( seriesFormatter.format( eachForm ) );
    }

    //should be private, making public for testing
    private String tailorAt( int index, Context context, TranslateBuffer tb )
    {
        //System.out.println( "Tailoring at [" + index + "]" );
        //create a new bindings for populating the VarCode that is going to be 
        //at the LOOP level        

        Set<String> vNames = formTemplate.getVarNames();
        
        String[] varNames = vNames.toArray( new String[ 0 ] );

        for( int i = 0; i < varNames.length; i++ )
        {
            //Object thisOne = context.get( varNames[ i ] );
            Object thisOne = context.getVarResolver().resolveVar(
                context, varNames[ i ] );

            //System.out.println( varNames[ i ] + "=" + thisOne );
            if( thisOne != null )
            {
                if( thisOne.getClass().isArray() )
                {   //do a range check
                    if( Array.getLength( thisOne ) > index )
                    {   //note this will OVERWRITE the last var value 
                        //from the last iteration of the loop
                        context.set( varNames[i], 
                            Array.get( thisOne, index ),
                            VarScope.LOOP );
                    }
                    else
                    {
                        //if( requiredVars.contains( varNames[ i ] ) )
                        if( this.formTemplate.isRequiredVar( varNames[ i ] ) )
                        {
                            throw new VarBindException(
                                "Cardinality Mismatch for Required Var \""
                                + varNames[ i ] + "\" of Form " + N + getText()
                                + N + "failed at [" + index + "]" );
                        }
                        //failed range check (it's null, but "" will suffice)
                        context.set( varNames[ i ], "", VarScope.LOOP );
                    }
                }
                if( thisOne instanceof Collection )
                {
                    Collection<?> l = (Collection<?>)thisOne;

                    if( l.size() > index )
                    {
                        context.set( varNames[ i ], 
                            l.toArray( new Object[ 0 ] )[ index ],
                            VarScope.LOOP );
                    }
                    else
                    {
                        if( this.formTemplate.isRequiredVar( varNames[ i ] ) )
                        {
                            throw new VarBindException(
                                "Cardinality Mismatch for Required Var \"" + varNames[ i ] + "\"" );
                        }
                        context.set( varNames[ i ], "", VarScope.LOOP);
                    }
                }
            }
        }

        String tailoredAt = this.fillOne( context, tb );

        //ensure there is no bleed through of data 
        //(replace the "loop") bindings
        //loopBindings.clear();
        context.clearAllScopeBindings( VarScope.LOOP );

        return tailoredAt;
    }

    @Override
    public String author( Context context )
    {
        TranslateBuffer textBuffer = new TranslateBuffer();
        deriveTo( context, textBuffer );
        return textBuffer.toString();
    }
    
    public String[] authorSeries( Context context )
    {
        TranslateBuffer textBuffer = new TranslateBuffer();
        
        //find out the number of Forms
        int cardinality = getFormInstanceCount( context );

        //now generate Each Form
        String[] series = new String[ cardinality ];
        
        for( int i = 0; i < cardinality; i++ )
        {
            series[ i ] = tailorAt( i, context, textBuffer );
            series[ i ] = seriesFormatter.format( new String[]{series[ i ]} );
        }
        return series;        
    }

    @Override
    public String author( Object... keyValuePairs )
    {
        return author( VarContext.of( keyValuePairs ) );
    }

    @Override
    public String getText()
    {
        return formTemplate.getMarkup() + seriesFormatter.getText();
    }

    @Override
    public Mark[] getAllMarks()
    {
        return formTemplate.getMarks();
    }
}
