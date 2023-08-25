/*
 * File:                VectorThresholdHellingerDistanceLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.DefaultPair;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * Unit tests for class VectorThresholdHellingerDistanceLearner
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class VectorThresholdHellingerDistanceLearnerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public VectorThresholdHellingerDistanceLearnerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class VectorThresholdHellingerDistanceLearner.
     */
    public void testConstructors()
    {
        VectorThresholdHellingerDistanceLearner<Boolean> instance =
            new VectorThresholdHellingerDistanceLearner<Boolean>();
        assertNotNull(instance);
    }

    /**
     * Test of learn method, of class VectorThresholdHellingerDistanceLearner.
     */
    public void testLearn()
    {
        VectorThresholdHellingerDistanceLearner<Boolean> instance =
            new VectorThresholdHellingerDistanceLearner<Boolean>();

        VectorElementThresholdCategorizer result = instance.learn(null);
        assertNull(result);

        LinkedList<InputOutputPair<Vector3, Boolean>> data =
            new LinkedList<InputOutputPair<Vector3, Boolean>>();
        result = instance.learn(data);
        assertNull(result);

        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 2.0), true));
        result = instance.learn(data);
        assertNull(result);

        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 2.0), true));
        result = instance.learn(data);
        assertNull(result);

        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 1.0, 2.0), true));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(2.5, result.getThreshold());

        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 2.0, 3.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 4.0), false));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(2, result.getIndex());
        assertEquals(2.5, result.getThreshold());

        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 3.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 0.0, 2.0), true));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 5.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 7.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 8.0, 2.0), false));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(4.5, result.getThreshold());
    }

    /**
     * Test of computeBestGainAndThreshold method, of class VectorThresholdHellingerDistanceLearner.
     */
    public void testComputeBestThreshold()
    {
        VectorThresholdHellingerDistanceLearner<Boolean> instance =
            new VectorThresholdHellingerDistanceLearner<Boolean>();
        MapBasedDataHistogram<Boolean> baseCounts = null;
        DefaultPair<Double, Double> result = null;

        LinkedList<InputOutputPair<Vector3, Boolean>> data =
            new LinkedList<InputOutputPair<Vector3, Boolean>>();
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);

        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 2.0), true));
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);
        result = instance.computeBestGainAndThreshold(data, 1, baseCounts);
        assertNull(result);
        result = instance.computeBestGainAndThreshold(data, 2, baseCounts);
        assertNull(result);


        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 1.0, 2.0), true));

        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 1, baseCounts);
        assertEquals(0.0, result.getFirst());
        assertEquals(2.5, result.getSecond());
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 2, baseCounts);
        assertNull(result);

        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 2.0, 3.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 4.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 3.0, 5.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 0.0, 2.0), true));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 5.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 7.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 8.0, 2.0), false));

        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 1, baseCounts);
        assertEquals(0.919, result.getFirst(), 0.001);
        assertEquals(1.5, result.getSecond());
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 2, baseCounts);
        assertEquals(0.765, result.getFirst(), 0.001);
        assertEquals(2.5, result.getSecond());
    }

    /**
     * Test of computeSplitGain method, of class VectorThresholdHellingerDistanceLearner.
     */
    public void testComputeGain()
    {
        VectorThresholdHellingerDistanceLearner<Boolean> instance =
            new VectorThresholdHellingerDistanceLearner<Boolean>();

        MapBasedDataHistogram<Boolean> positiveCounts = new MapBasedDataHistogram<Boolean>();
        MapBasedDataHistogram<Boolean> negativeCounts = new MapBasedDataHistogram<Boolean>();
        MapBasedDataHistogram<Boolean> baseCounts = new MapBasedDataHistogram<Boolean>();
        double result = 0.0;

        // Test case: Zeros
        //     P   N   Total
        // T   0   0   0
        // F   0   0   0
        // Mean Hellinger distance = 0.000
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.0, result, 0.0);

        // Test case: 1/1 split
        //     P   N   Total
        // T   1   0   1
        // F   0   1   1
        // Mean Hellinger distance = 1.141 = sqrt(2)
        positiveCounts.add(true, 1);
        positiveCounts.add(false, 0);
        negativeCounts.add(true, 0);
        negativeCounts.add(false, 1);
        baseCounts.add(true, 1);
        baseCounts.add(false, 1);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(Math.sqrt(2.0), result, 0.001);

        // Test case: Zero distance
        //     P   N   Total
        // T   1   1   2
        // F   1   1   2
        // Mean Hellinger distance = 0.000
        baseCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts = new MapBasedDataHistogram<Boolean>();
        negativeCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts.add(true, 1);
        positiveCounts.add(false, 1);
        negativeCounts.add(true, 1);
        negativeCounts.add(false, 1);
        baseCounts.add(true, 2);
        baseCounts.add(false, 2);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.0, result, 0.001);

        // Test case: Example 1
        //     P   N   Total
        // T   2   2   4
        // F   6   0   6
        // Mean Hellinger distance = 0.765
        baseCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts = new MapBasedDataHistogram<Boolean>();
        negativeCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts.add(true, 2);
        positiveCounts.add(false, 6);
        negativeCounts.add(true, 2);
        negativeCounts.add(false, 0);
        baseCounts.add(true, 4);
        baseCounts.add(false, 6);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.765, result, 0.001);

        // Test case: Example 2
        //     P   N   Total
        // T   0   4   4
        // F   3   3   6
        // Mean Hellinger distance = 0.765
        positiveCounts = new MapBasedDataHistogram<Boolean>();
        negativeCounts = new MapBasedDataHistogram<Boolean>();
        baseCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts.add(true, 0);
        positiveCounts.add(false, 3);
        negativeCounts.add(true, 4);
        negativeCounts.add(false, 3);
        baseCounts.add(true, 4);
        baseCounts.add(false, 6);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.765, result, 0.001);

        // Test case: Example 3
        //     P   N   Total
        // T   1   2   3
        // F   6   0   6
        // Mean Hellinger distance = 0.919
        positiveCounts = new MapBasedDataHistogram<Boolean>();
        negativeCounts = new MapBasedDataHistogram<Boolean>();
        baseCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts.add(true, 1);
        positiveCounts.add(false, 6);
        negativeCounts.add(true, 2);
        negativeCounts.add(false, 0);
        baseCounts.add(true, 3);
        baseCounts.add(false, 6);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.919, result, 0.001);

        // Test case: Example 4
        //     P   N   Total
        // T   0   3   3
        // F   3   3   6
        // Mean Hellinger distance = 0.765
        positiveCounts = new MapBasedDataHistogram<Boolean>();
        negativeCounts = new MapBasedDataHistogram<Boolean>();
        baseCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts.add(true, 0);
        positiveCounts.add(false, 3);
        negativeCounts.add(true, 3);
        negativeCounts.add(false, 3);
        baseCounts.add(true, 3);
        baseCounts.add(false, 6);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.765, result, 0.001);


        // Test case: Example 5
        //     P   N   Total
        // T   0   1   2
        // F   1  10   11
        // Mean Hellinger distance = 0.474
        positiveCounts = new MapBasedDataHistogram<Boolean>();
        negativeCounts = new MapBasedDataHistogram<Boolean>();
        baseCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts.add(true, 1);
        positiveCounts.add(false, 1);
        negativeCounts.add(true, 1);
        negativeCounts.add(false, 10);
        baseCounts.add(true, 2);
        baseCounts.add(false, 11);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.474, result, 0.001);

        // Test case: Example 6
        //     P   N   Total
        // T  11   1   11
        // F   1   1   2
        // Mean Hellinger distance = 0.474
        positiveCounts = new MapBasedDataHistogram<Boolean>();
        negativeCounts = new MapBasedDataHistogram<Boolean>();
        baseCounts = new MapBasedDataHistogram<Boolean>();
        positiveCounts.add(true, 10);
        positiveCounts.add(false, 1);
        negativeCounts.add(true, 1);
        negativeCounts.add(false, 1);
        baseCounts.add(true, 11);
        baseCounts.add(false, 2);
        result = instance.computeSplitGain(baseCounts, positiveCounts, negativeCounts);
        assertEquals(0.474, result, 0.001);
    }

}
