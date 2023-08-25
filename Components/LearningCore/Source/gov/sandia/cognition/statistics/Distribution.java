/*
 * File:                Distribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Describes a very high-level distribution of data.  Basically, this is an
 * object that can be sampled according to its distribution of data.
 * @param <DataType> 
 * Type of data used on the domain of this distribution.  For example, a
 * scalar distribution would have DataType of Double, and a multivariate
 * distribution would have a DataType of Vector.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface Distribution<DataType>
    extends CloneableSerializable
{

    /**
     * Gets the arithmetic mean, or "first central moment" or "expectation",
     * of the distribution.
     * @return
     * Mean of the distribution.
     */
    public DataType getMean();
    
    /**
     * Draws a single random sample from the distribution.
     * @param random
     * Random-number generator to use in order to generate random numbers.
     * @return
     * Sample drawn according to this distribution.
     */
    public DataType sample(
        Random random );
    
    /**
     * Draws multiple random samples from the distribution.  It is generally
     * more efficient to use this multiple-sample method than multiple calls of
     * the single-sample method.  (But not always.)
     * @param random
     * Random-number generator to use in order to generate random numbers.
     * @param numSamples
     * Number of samples to draw from the distribution.
     * @return
     * Samples drawn according to this distribution.
     */
    public ArrayList<? extends DataType> sample(
        Random random,
        int numSamples );    
    
}
