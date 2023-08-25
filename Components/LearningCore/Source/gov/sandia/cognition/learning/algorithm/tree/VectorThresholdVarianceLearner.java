/*
 * File:                VectorThresholdVarianceLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * The {@code VectorThresholdVarianceLearner} computes the best threshold over
 * a dataset of vectors using the reduction in variance to determine the 
 * optimal index and threshold. This is an implementation of what is used in
 * the CART regression tree algorithm.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorThresholdVarianceLearner
    extends AbstractCloneableSerializable
    implements DeciderLearner<Vectorizable, Double, Boolean, VectorElementThresholdCategorizer>
{
    /**
     * Creates a new instance of VectorThresholdVarianceLearner.
     */
    public VectorThresholdVarianceLearner()
    {
        super();
    }
    
    /**
     * Learns a VectorElementThresholdCategorizer from the given data by 
     * picking the vector element and threshold that best maximizes information
     * gain.
     *
     * @param  data The data to learn from.
     * @return
     *      The learned threshold categorizer, or none if there is no good
     *      categorizer.
     */
    public VectorElementThresholdCategorizer learn(
        final Collection
            <? extends InputOutputPair<? extends Vectorizable, Double>> 
            data)
    {
        if ( data == null || data.size() <= 1 )
        {
            // Nothing to learn.
            return null;
        }
        
        // Compute the base variance.
        final double baseVariance = DatasetUtil.computeOutputVariance(data);
        
        // Figure out the dimensionality of the data.
        final int dimensionality = this.getDimensionality(data);
        
        // Go through all the dimensions to find the one with the best gain and
        // the best threshold.
        double bestGain = -1.0;
        int bestIndex = -1;
        double bestThreshold = 0.0;
        for (int i = 0; i < dimensionality; i++)
        {
            // Compute the best gain-threshold pair for the given dimension of
            // the data.
            final DefaultPair<Double, Double> gainThresholdPair = 
                this.computeBestGainThreshold(data, i, baseVariance);
            
            if ( gainThresholdPair == null )
            {
                // There was no gain-threshold pair that created a threshold.s
                continue;
            }
            
            // Get the gain from the pair.
            final double gain = gainThresholdPair.getFirst();
            
            // Determine if this is the best gain seen.
            if ( bestIndex == -1 || gain > bestGain )
            {
                // This is the best gain, so store the gain, threshold, and
                // index.
                final double threshold = gainThresholdPair.getSecond();
                bestGain = gain;
                bestIndex = i;
                bestThreshold = threshold;
            }
        }
        
        if ( bestIndex < 0 )
        {
            // There was no dimension that provided any information gain for
            // the data, so no decision function can be made.
            return null;
        }
        else
        {
            // Create the decision function for the best gain.
            return new VectorElementThresholdCategorizer(
                bestIndex, bestThreshold);
        }
    }
    
    /**
     * Figures out the dimensionality of the Vector data.
     *
     * @param  data The data.
     * @return The dimensionality of the data in the vector.
     */
    protected int getDimensionality(
        final Collection
            <? extends InputOutputPair<? extends Vectorizable, ?>> 
            data)
    {
        if ( data == null || data.size() <= 0 )
        {
            // Bad data.
            return 0;
        }
        else
        {
            // Get the dimensionality of the first data element.
            return data.iterator().next().getInput().convertToVector()
                .getDimensionality();
        }
    }
    
    /**
     * Computes the best information gain-threshold pair for the given 
     * dimension on the given data. It does this by sorting the data according
     * to the dimension and then walking the sorted values to find the one that
     * has the best threshold.
     * 
     * 
     * @param data The data to use.
     * @param dimension The dimension to compute the best threshold over.
     * @param baseVariance The variance of the data.
     * @return
     *      The pair containing the best information gain found along this
     *      dimension and the corresponding threshold.
     */
    public DefaultPair<Double, Double> computeBestGainThreshold(
        final Collection
            <? extends InputOutputPair<? extends Vectorizable, Double>> 
            data,
        final int dimension,
        final double baseVariance)
    {
        // To compute the gain we will sort all of the values along the given
        // dimension and then walk along the values to determine the best 
        // threshold.
        
        // The first step is to create a list of (value, output) pairs.
        final int total = data.size();
        final ArrayList<DefaultPair<Double, Double>> values = 
            new ArrayList<DefaultPair<Double, Double>>(total);
        double totalOutputSum = 0.0;
        for ( InputOutputPair<? extends Vectorizable, Double> example 
            : data )
        {
            // Add this example to the list.
            final Vector input = example.getInput().convertToVector();
            final Double output = example.getOutput();
            final double value = input.getElement(dimension);
            
            values.add(new DefaultPair<Double, Double>(value, output));
// TODO: Compute this only once for all dimensions.
            totalOutputSum += output;
        }
        
        // Sort the list in ascending order by value.
        Collections.sort(values, new Comparator<DefaultPair<Double, Double>>()
        {
            public int compare(
                DefaultPair<Double, Double> o1, 
                DefaultPair<Double, Double> o2)
            {
                return o1.getFirst().compareTo(o2.getFirst());
            }
        });
        
        // If all the values on this dimension are the same then there is 
        // nothing to split on.
        if (    total <= 1 
             || values.get(0).getFirst().equals(values.get(total - 1).getFirst()) )
        {
            // All of the values are the same.
            return null;
        }
        
        // In order to find the best split we are going to keep track of the
        // counts of each label on each side of the threshold. This means
        // that we maintain two counting objects.
        // To start with all of the examples are on the positive side of
        // the split, so we initialize the base counts (all the data points)
        // and the negative counts with nothing.
        
        double sumNegative = 0.0;
        double sumPositive = totalOutputSum;
        
        // We are going to loop over all the values to compute the best gain
        // and the best threshold.
        double bestGain = 0.0;
        double bestTieBreaker = 0.0;
        double bestThreshold = 0.0;
        
        // We need to keep track of the previous value for two reasons:
        //    1) To determine if we've already tested the value, since we loop
        //       over a >= threshold.
        //    2) So that the threshold can be computed to be half way between 
        //       two values.
        double previousValue = 0.0;
        for (int i = 0; i < total; i++)
        {
            final DefaultPair<Double, Double> valueLabel = values.get(i);
            final double value = valueLabel.getFirst();
            final double label = valueLabel.getSecond();
            
            if ( i == 0 )
            {
                // We are going to loop over a threshold value that is >=. 
                // Since there is no point on splitting on the first value, 
                // since nothing will be less than it, we skip it. However, we 
                // do need to add it to the counts.       
                bestGain = 0.0;
                bestTieBreaker = 0.0;
                bestThreshold = value;
            }
            else if ( value != previousValue )
            {   
                // Evaluate this threshold.
                
                // Compute the total positive and negative at this point.
                final int numNegative = i;
                final int numPositive = total - i;
                
                // Compute the mean and variance of the negatives.
                final double meanNegative = sumNegative / numNegative;
                double varianceNegative = 0.0;
                for ( int j = 0; j < i; j++ )
                {
                    final double output = values.get(j).getSecond();
                    final double difference = output - meanNegative;
                    varianceNegative += difference * difference;
                }
                varianceNegative /= numNegative;
                
                // Compute the mean and variance of the positives.
                final double meanPositive = sumPositive / numPositive;
                double variancePositive = 0.0;
                for ( int j = i; j < total; j++ )
                {
                    final double output = values.get(j).getSecond();
                    final double difference = output - meanPositive;
                    variancePositive += difference * difference;
                }
                variancePositive /= numPositive;
                
                // Compute the proportion of positives and negatives.
                final double proportionPositive = (double) numPositive / total;
                final double proportionNegative = (double) numNegative / total;
                
                // Compute the gain.
                final double gain = baseVariance
                    - proportionPositive * variancePositive
                    - proportionNegative * varianceNegative;
                
                if ( gain >= bestGain )
                {
                    // This is our tiebreaker criteria for the case where the
                    // gains are equal. It means that we prefer ties that are
                    // more balanced in how they split (50%/50% being optimal).
                    final double tieBreaker = 1.0 
                        - Math.abs(proportionPositive - proportionNegative);
                    
                    if ( gain > bestGain || tieBreaker > bestTieBreaker )
                    {
                        // For the decision threshold we actually want to pick 
                        // the point that is half way between the current value 
                        // and the previous value. Hopefully this will be more 
                        // robust than using just the value itself.
                        final double threshold = 
                            (value + previousValue) / 2.0;

                        bestGain = gain;
                        bestTieBreaker = tieBreaker;
                        bestThreshold = threshold;
                    }
                }
            }
            // else - This threshold was equal to the previous one. Since we
            //        use a >= cutting criteria, 
            
            
            // For the next loop we remove the label from the positive side
            // and add it to the negative side of the threshold.
            sumPositive -= label;
            sumNegative += label;
            
            // Store this value as the previous value.
            previousValue = value;
        }
        
        // Return the pair containing the best gain and best threshold found.
        return new DefaultPair<Double, Double>(bestGain, bestThreshold);
    }
}
