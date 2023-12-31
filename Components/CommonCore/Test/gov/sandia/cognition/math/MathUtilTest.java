/*
 * File:                MathUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import junit.framework.TestCase;


/**
 * This class implements JUnit tests for the following classes:
 *
 *     MathUtil
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class MathUtilTest
    extends TestCase
{

    /**
     * Random number generator
     */
    public Random RANDOM = new Random(1);

    /**
     * Tolerance
     */
    public double TOLERANCE = 1e-5;

    /**
     * Constructor
     * @param testName
     * Name
     */
    public MathUtilTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of log2 method, of class gov.sandia.cognition.math.MathUtil.
     */
    public void testLog2()
    {
        assertEquals(0.0, MathUtil.log2(1.0));
        assertEquals(1.0, MathUtil.log2(2.0));
        assertEquals(2.0, MathUtil.log2(4.0));
    }

    /**
     * Test of log method, of class MathUtil.
     */
    public void testLog()
    {
        assertEquals(0.0, MathUtil.log(1.0, 4.0));
        assertEquals(1.0, MathUtil.log(2.0, 2.0));
        assertEquals(2.0, MathUtil.log(4.0, 2.0));
        assertEquals(0.5, MathUtil.log(2.0, 4.0));
    }

    /**
     * Test of binomialCoefficient method, of class BinomialDistribution.
     */
    public void testBinomialCoefficient()
    {
        System.out.println( "binomialCoefficient" );

        assertEquals( 3, MathUtil.binomialCoefficient( 3, 2 ) );
        assertEquals( 6, MathUtil.binomialCoefficient( 4, 2 ) );
        assertEquals( 4, MathUtil.binomialCoefficient( 4, 3 ) );

        assertEquals( 1, MathUtil.binomialCoefficient( 1, 0 ) );

        try
        {
            MathUtil.binomialCoefficient( 4, 5 );
            fail( "k must be <= N" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of logFactorial method, of class BinomialDistribution.
     */
    public void testLogFactorial()
    {
        System.out.println( "logFactorial" );

        assertEquals( Math.log( 6 ), MathUtil.logFactorial( 3 ), TOLERANCE );
        assertEquals( Math.log( 720 ), MathUtil.logFactorial( 6 ), TOLERANCE );
        assertEquals( Math.log( 3628800 ), MathUtil.logFactorial( 10 ), TOLERANCE );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.cost.StudentTTest.LogGammaFunction.
     */
    public void testLogGammaEvaluate()
    {
        System.out.println( "GammaDistribution.GammaFunction.LogGammaFunction.evaluate" );

        assertEquals( 2.252713, MathUtil.logGammaFunction( 0.1 ), TOLERANCE );
        assertEquals( 1.524064, MathUtil.logGammaFunction( 0.2 ), TOLERANCE );
        assertEquals( 1.095798, MathUtil.logGammaFunction( 0.3 ), TOLERANCE );
        assertEquals( 0.796678, MathUtil.logGammaFunction( 0.4 ), TOLERANCE );
        assertEquals( 0.572365, MathUtil.logGammaFunction( 0.5 ), TOLERANCE );
        assertEquals( 0.398234, MathUtil.logGammaFunction( 0.6 ), TOLERANCE );
        assertEquals( 0.260867, MathUtil.logGammaFunction( 0.7 ), TOLERANCE );
        assertEquals( 0.152060, MathUtil.logGammaFunction( 0.8 ), TOLERANCE );
        assertEquals( 0.066376, MathUtil.logGammaFunction( 0.9 ), TOLERANCE );
        assertEquals( 0.000000, MathUtil.logGammaFunction( 1.0 ), TOLERANCE );
        assertEquals( 12.801827, MathUtil.logGammaFunction( 10.0 ), TOLERANCE );

        try
        {
            MathUtil.logGammaFunction( 0.0 );
            fail( "x > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.statistics.Statistics.GammaFunction.Incomplete.
     */
    public void testGammaFunctionIncompleteEvaluate()
    {
        System.out.println( "GammaDistribution.GammaFunction.Incomplete.evaluate" );

        // These were checked using octave's gammainc() function...
        // however, we use evaluate(a,x), octave uses gammainc(x,a)
        // so just remember to switch the arguments when checking
        assertEquals( 0.8275517596, MathUtil.lowerIncompleteGammaFunction( 0.1, 0.1 ), TOLERANCE );
        assertEquals( 0.9414024459, MathUtil.lowerIncompleteGammaFunction( 0.1, 0.5 ), TOLERANCE );
        assertEquals( 0.9943261760, MathUtil.lowerIncompleteGammaFunction( 0.1, 2.0 ), TOLERANCE );

        assertEquals( 0.0951625820, MathUtil.lowerIncompleteGammaFunction( 1.0, 0.1 ), TOLERANCE );
        assertEquals( 0.1987480431, MathUtil.lowerIncompleteGammaFunction( 1.5, 0.5 ), TOLERANCE );
        assertEquals( 0.5939941503, MathUtil.lowerIncompleteGammaFunction( 2.0, 2.0 ), TOLERANCE );

    }

    /**
     * Beta function evaluate
     */
    public void testBetaFunctionEvaluate()
    {
        System.out.println( "BetaFunction.evaluate" );

        // These values come from octave's beta() function
        assertEquals( Math.log(1.0), MathUtil.logBetaFunction( 1, 1 ), TOLERANCE );
        assertEquals( Math.log(0.5), MathUtil.logBetaFunction( 2, 1 ), TOLERANCE );
        assertEquals( Math.log(0.5), MathUtil.logBetaFunction( 1, 2 ), TOLERANCE );

        assertEquals( Math.log(0.0095238095), MathUtil.logBetaFunction( 3, 5 ), TOLERANCE );
        assertEquals( Math.log(0.0095238095), MathUtil.logBetaFunction( 5, 3 ), TOLERANCE );

    }

    /**
     * Test of function method, of class gov.sandia.cognition.learning.util.function.cost.StudentTTest.BetaFunction.Incomplete.
     */
    public void testBetaFunctionIncompleteFunction()
    {
        System.out.println( "BetaFunction.Incomplete.evaluate" );

        assertEquals( 0.0, MathUtil.regularizedIncompleteBetaFunction( RANDOM.nextDouble(), RANDOM.nextDouble(), 0.0 ) );
        assertEquals( 1.0, MathUtil.regularizedIncompleteBetaFunction( RANDOM.nextDouble(), RANDOM.nextDouble(), 1.0 ) );

        assertEquals( 0.133975, MathUtil.regularizedIncompleteBetaFunction( 1.0, 0.5, 0.25 ), TOLERANCE );
        assertEquals( 0.292893, MathUtil.regularizedIncompleteBetaFunction( 1.0, 0.5, 0.50 ), TOLERANCE );
        assertEquals( 0.500000, MathUtil.regularizedIncompleteBetaFunction( 1.0, 0.5, 0.75 ), TOLERANCE );

        assertEquals( 0.000977, MathUtil.regularizedIncompleteBetaFunction( 5.0, 1.0, 0.25 ), TOLERANCE );
        assertEquals( 0.590490, MathUtil.regularizedIncompleteBetaFunction( 5.0, 1.0, 0.90 ), TOLERANCE );

        assertEquals( 0.018535, MathUtil.regularizedIncompleteBetaFunction( 4.0, 8.0, 0.10 ), TOLERANCE );
        assertEquals( 0.886719, MathUtil.regularizedIncompleteBetaFunction( 4.0, 8.0, 0.50 ), TOLERANCE );
        assertEquals( 0.999999, MathUtil.regularizedIncompleteBetaFunction( 4.0, 8.0, 0.90 ), TOLERANCE );

        try
        {
            MathUtil.regularizedIncompleteBetaFunction( RANDOM.nextDouble(), RANDOM.nextDouble(), -1.0 );
            fail( "x must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            MathUtil.regularizedIncompleteBetaFunction( RANDOM.nextDouble(), RANDOM.nextDouble(), 1.01 );
            fail( "x must be <= 1.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * MathUtil.logMultinomialBetaFunction
     */
    public void testLogMultinomialBetaFunction()
    {
        System.out.println( "MathUtil.logMultinomialBetaFunction" );

        Vector a = VectorFactory.getDefault().copyValues(1.0, 2.0, 3.0 );
        assertEquals( -4.094344562222101, MathUtil.logMultinomialBetaFunction(a), TOLERANCE );
        a = VectorFactory.getDefault().copyValues(1.0, 2.0, 3.0, 4.0 );
        assertEquals( -10.316920830293707, MathUtil.logMultinomialBetaFunction(a), TOLERANCE );
        a = VectorFactory.getDefault().copyValues(4.0, 2.0, 3.0, 4.0 );
        assertEquals( -15.710548376647242, MathUtil.logMultinomialBetaFunction(a), TOLERANCE );

        a = VectorFactory.getDefault().copyValues(1.0, 0.0, 3.0 );
        try
        {
            MathUtil.logMultinomialBetaFunction(a);
            fail( "a must be positive!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
