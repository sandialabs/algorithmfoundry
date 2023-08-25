/*
 * File:                DiagonalMatrixFactoryMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.MatrixFactoryTestHarness;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DiagonalMatrixFactoryMTJTest.
 *
 * @author krdixon
 */
public class DiagonalMatrixFactoryMTJTest
    extends MatrixFactoryTestHarness
{

    /**
     * Tests for class DiagonalMatrixFactoryMTJTest.
     * @param testName Name of the test.
     */
    public DiagonalMatrixFactoryMTJTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public DiagonalMatrixFactoryMTJ createFactory()
    {
        return DiagonalMatrixFactoryMTJ.INSTANCE;
    }

    @Override
    public Matrix createRandomMatrix()
    {
        int M = random.nextInt(10) + 1;
        return this.createFactory().createUniformRandom(M, M, -RANGE, RANGE, random);
    }

    /**
     * Test of createMatrix method, of class DiagonalMatrixFactoryMTJ.
     */
    public void testCreateMatrix_int()
    {
        System.out.println("createMatrix");
        DiagonalMatrixFactoryMTJ instance = this.createFactory();
        int M = random.nextInt(10) + 1;
        DiagonalMatrixMTJ result = instance.createMatrix(M);
        assertNotNull( result );
        assertEquals( M, result.getDimensionality() );
    }

    /**
     * Test of diagonalValues method, of class DiagonalMatrixFactoryMTJ.
     */
    public void testDiagonalValues()
    {
        System.out.println("diagonalValues");
        double[] diagonal = { random.nextGaussian(), random.nextGaussian(), random.nextGaussian() };
        DiagonalMatrixFactoryMTJ instance = this.createFactory();
        DiagonalMatrixMTJ result = instance.diagonalValues(diagonal);
        assertEquals( diagonal.length, result.getDimensionality() );
        for( int i =0 ; i < diagonal.length; i++ )
        {
            assertEquals( diagonal[i], result.getElement(i) );
        }
    }

    public void testCreateUniformRandom_Diagonal()
    {
        System.out.println( "createUniformRandom" );

        int M = random.nextInt(10) + 1;
        int N = M+1;
        DiagonalMatrixFactoryMTJ factory = this.createFactory();
        try
        {
            factory.createUniformRandom(M, N, -RANGE, RANGE, random);
            fail( "Cannot create non-suare matrix" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        DiagonalMatrixMTJ m = factory.createUniformRandom(M, M, -RANGE, RANGE, random);
        assertNotNull( m );
        assertEquals( M, m.getDimensionality() );
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < M; j++ )
            {
                double v = m.getElement(i,j);
                if( i == j )
                {
                    assertTrue( -RANGE <= v );
                    assertTrue( v <= RANGE );
                }
                else
                {
                    assertEquals( 0.0, v );
                }
            }
        }

    }


}
