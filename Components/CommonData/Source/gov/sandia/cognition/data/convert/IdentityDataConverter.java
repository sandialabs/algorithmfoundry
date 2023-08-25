/*
 * File:                IdentityDataConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.convert;

/**
 * A pass-through converter that just returns the given value.
 * 
 * @param   <DataType>
 *      The data type that is allowed.
 * @author  Justin Basilico
 * @since   3.0
 */
public class IdentityDataConverter<DataType>
    extends AbstractReversibleDataConverter<DataType, DataType>
{

    /**
     * Creates a new {@code IdentityDataConverter}.
     */
    public IdentityDataConverter()
    {
        super();
    }

    /**
     * Returns the given input.
     * 
     * @param   input 
     *      The input value.
     * @return
     *      The input value.
     */
    public DataType evaluate(
        final DataType input)
    {
        return input;
    }

    /**
     * The reverse converter is this converter, since it is an identity 
     * converter.
     * 
     * @return  The reverse converter is this converter.
     */
    public IdentityDataConverter<DataType> reverse()
    {
        return this;
    }

}
