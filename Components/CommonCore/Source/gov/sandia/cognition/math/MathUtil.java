/*
 * File:                MathUtil.java
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

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * The {@code MathUtil} class implements mathematical utility functions.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-26",
    changesNeeded=false,
    comments={
        "Minor changes, log2 uses callback to main log() method.",
        "Otherwise, looks fine."
    }
)
public class MathUtil
{
    /**
     * Returns the log of the given base of the given value,
     * y=log_b(x) such that x=b^y
     * @param x The value.
     * @param base The base for the logarithm
     * @return The log of x using the given base.
     */
    public static double log(
        final double x,
        final double base)
    {
        return Math.log(x) / Math.log(base);
    }
    
    /**
     * Returns the base-2 logarithm of the given value. It is computed as
     * log(x) / log(2).
     *
     * @param  x The value.
     * @return The base-2 logarithm.
     */
    public static double log2(
        final double x)
    {
        return MathUtil.log(x, 2.0);
    }

    /**
     * Computes the logarithm of the Gamma function.
     * @param input
     * Input to evaluate the Natural Logarithm of the Gamma Function
     * @return
     * Natural Logarithm of the Gamma Function about the input
     */
    @PublicationReferences(
        references={
            @PublicationReference(
                author="Wikipedia",
                title="Gamma Function",
                type=PublicationType.WebPage,
                year=2010,
                url="http://en.wikipedia.org/wiki/Gamma_function"
            )
            ,
            @PublicationReference(
                author="jdhedden",
                title="Bug in 2nd edition version of gammln()",
                type=PublicationType.WebPage,
                year=2005,
                url="http://www.numerical-recipes.com/forum/showthread.php?t=606"
            )
        }
    )
    public static double logGammaFunction(
        double input )
    {

        if (input <= 0.0)
        {
            throw new IllegalArgumentException( "Input must be > 0.0" );
        }

        double xx = input;

        double tmp, ser;

        tmp = xx + 4.5;
        tmp -= (xx - 0.5) * Math.log( tmp );

        ser = 1.000000000190015
            + (76.18009172947146 / xx)
            - (86.50532032941677 / (xx + 1.0))
            + (24.01409824083091 / (xx + 2.0))
            - (1.231739572450155 / (xx + 3.0))
            + (0.1208650973866179e-2 / (xx + 4.0))
            - (0.5395239384953e-5 / (xx + 5.0));

        return (Math.log( 2.5066282746310005 * ser ) - tmp);
    }

    /**
     * Computes the Lower incomplete gamma function.
     * Note that this has the reverse parameters order from octave.
     * @param a
     * Degrees of Freedom
     * @param x
     * Input value
     * @return
     * Value of the IncompleteGammaFunction(a,x)
     */
    @PublicationReferences(
        references={
            @PublicationReference(
                author="Wikipedia",
                title="Incomplete gamma function",
                type=PublicationType.WebPage,
                year=2010,
                url="http://en.wikipedia.org/wiki/Incomplete_gamma_function"
            )
            ,
            @PublicationReference(
                author={
                    "William H. Press",
                    "Saul A. Teukolsky",
                    "William T. Vetterling",
                    "Brian P. Flannery"
                },
                title="Numerical Recipes in C, Second Edition",
                type=PublicationType.Book,
                year=1992,
                pages=218,
                notes="Function gammap",
                url="http://www.nrbook.com/a/bookcpdf.php"
            )
        }
    )
    public static double lowerIncompleteGammaFunction(
        double a,
        double x )
    {

        if (a <= 0.0)
        {
            throw new IllegalArgumentException( "a must be > 0.0" );
        }

        double gammp;
        if (x <= 0.0)
        {
            gammp = 0.0;
        }
        else if (x > 1e10)
        {
            gammp = 1.0;
        }
        else if (x < (a + 1.0))
        {
            gammp = incompleteGammaSeriesExpansion( a, x );
        }
        else
        {
            gammp = incompleteGammaContinuedFraction( a, x );
        }

        return gammp;

    }

    /**
     * Computes the series expansion approximation to the incomplete
     * gamma function.
     * Note that this has the reverse parameters order from octave.
     * @param a
     * Degrees of Freedom
     * @param x
     * Input value
     * @return
     * Value of the IncompleteGammaFunction(a,x)
     */
    @PublicationReference(
        author={
            "William H. Press",
            "Saul A. Teukolsky",
            "William T. Vetterling",
            "Brian P. Flannery"
        },
        title="Numerical Recipes in C, Second Edition",
        type=PublicationType.Book,
        year=1992,
        pages={218,219},
        url="http://www.nrbook.com/a/bookcpdf.php",
        notes="Function gser()"
    )
    protected static double incompleteGammaSeriesExpansion(
        double a,
        double x )
    {
        final int MAX_ITERATIONS = 1000;
        final double EPS = 3e-7;
        double gamser = 0.0;

        if (x <= 0.0)
        {
            if (x < 0.0)
            {
                throw new IllegalArgumentException( "x must be >= 0.0" );
            }
            gamser = 0.0;
        }
        else
        {
            double gln = logGammaFunction( a );
            double del, sum;
            double ap = a;
            del = sum = 1.0 / a;
            int n;
            for (n = 1; n <= MAX_ITERATIONS; n++)
            {
                ap++;
                del *= x / ap;
                sum += del;
                if (Math.abs( del ) < (Math.abs( sum ) * EPS))
                {
                    gamser = sum * Math.exp( -x + a * Math.log( x ) - gln );
                    break;
                }
            }

            if (n > MAX_ITERATIONS)
            {
                throw new OperationNotConvergedException(
                    "a too large, MAX_ITERATIONS too small in seriesExpansion()" );
            }

        }

        return gamser;

    }

    /**
     * Returns the incomplete Gamma function using the continued
     * fraction expansion evaluation using Lentz's method
     * @param a
     * Degrees of Freedom
     * @param x
     * Input value
     * @return
     * Value of the IncompleteGammaFunction(a,x)
     */
    @PublicationReference(
        author={
            "William H. Press",
            "Saul A. Teukolsky",
            "William T. Vetterling",
            "Brian P. Flannery"
        },
        title="Numerical Recipes in C, Second Edition",
        type=PublicationType.Book,
        year=1992,
        pages={216,219},
        url="http://www.nrbook.com/a/bookcpdf.php"
    )
    public static double incompleteGammaContinuedFraction(
        double a,
        double x )
    {

        LentzMethod lentz = new LentzMethod();

        lentz.initializeAlgorithm(0.0);
        lentz.iterate(1.0, (x+1-a) );
        while( lentz.getKeepGoing() )
        {
            int i = lentz.getIteration();
            double aterm = -i * (i-a);
            double bterm = x + (2*i+1)-a;
            lentz.iterate(aterm, bterm);
        }

        double gln = logGammaFunction( a );
        double gcf = lentz.getResult();
        double gamma = 1.0 - Math.exp( -x + a * Math.log( x ) - gln ) * gcf;
        return gamma;
    }

    /**
     * Returns the binomial coefficient for "N choose k".  In other words, this
     * is the number of different ways of choosing k objects from a total of
     * N different ones, where order doesn't matter and without replacement.
     * @param N Total number of objects in the bag
     * @param k Total number of objects to choose, must be less than or equal
     * to N
     * @return Binomial coefficient for N choose k
     */
    @PublicationReference(
        author="Wikipedia",
        title="Binomial coefficient",
        type=PublicationType.WebPage,
        year=2010,
        url="http://en.wikipedia.org/wiki/Binomial_coefficient"
    )
    public static int binomialCoefficient(
        int N,
        int k )
    {
        return (int) Math.round( Math.exp( logBinomialCoefficient(N, k) ) );
    }

    /**
     * Computes the natural logarithm of the binomial coefficient.
     * @param N Total number of objects in the bag
     * @param k Total number of objects to choose, must be less than or equal
     * to N
     * @return Natural logarithm of the binomial coefficient for N choose k
     */
    public static double logBinomialCoefficient(
        int N,
        int k )
    {
        return logFactorial( N ) - logFactorial( k ) - logFactorial( N - k );
    }

    /**
     * Returns the natural logarithm of n factorial log(n!) =
     * log(n*(n-1)*...*3*2*1)
     * @param n
     * Parameter for choose for n factorial
     * @return
     * n factorial
     */
    public static double logFactorial(
        int n )
    {
        if (n < 0)
        {
            throw new IllegalArgumentException( "Factorial must be >= 0" );
        }
        // Less than 1 is defined to be 1, taking its logarithm yields 0.0
        else if (n <= 1)
        {
            return 0.0;
        }
        else
        {
            return logGammaFunction( n + 1.0 );
        }
    }

    /**
     * Compute the natural logarithm of the Beta Function.
     * @param a
     * First parameter to the Beta function
     * @param b
     * Second parameter to the Beta function
     * @return
     * Natural logarithm of the Beta Function.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Beta function",
        type=PublicationType.WebPage,
        year=2010,
        url="http://en.wikipedia.org/wiki/Beta_function"
    )
    public static double logBetaFunction(
        double a,
        double b )
    {
        double ga = logGammaFunction( a );
        double gb = logGammaFunction( b );
        double gab = logGammaFunction( a + b );
        return ga + gb - gab;
    }

    /**
     * Computes the regularized incomplete Beta function.
     * @param a
     * Parameter a to the Beta function
     * @param b
     * Parameter b to the Beta function
     * @param x
     * Parameter x to for the integral from 0 to x
     * @return
     * Incomplete beta function for I_x(a,b)
     */
    @PublicationReferences(
        references={
            @PublicationReference(
                author="Wikipedia",
                title="Beta function, Incomplete Beta function",
                type=PublicationType.WebPage,
                year=2010,
                url="http://en.wikipedia.org/wiki/Beta_function#Incomplete_beta_function"
            )
            ,
            @PublicationReference(
                author={
                    "William H. Press",
                    "Saul A. Teukolsky",
                    "William T. Vetterling",
                    "Brian P. Flannery"
                },
                title="Numerical Recipes in C, Second Edition",
                type=PublicationType.Book,
                year=1992,
                pages=227,
                notes="Function betai",
                url="http://www.nrbook.com/a/bookcpdf.php"
            )
        }
    )
    public static double regularizedIncompleteBetaFunction(
        double a,
        double b,
        double x )
    {

        double bt;

        if ((x < 0.0) || (x > 1.0))
        {
            throw new IllegalArgumentException( "0 <= x <= 1" );
        }

        if ((x == 0.0) || (x == 1.0))
        {
            bt = 0.0;
        }
        else
        {
            bt = Math.exp(
                a*Math.log( x ) +
                b*Math.log( 1.0-x ) -
                logBetaFunction(a, b) );
        }

        if (x < ((a + 1.0) / (a + b + 2.0)))
        {
            return bt * incompleteBetaContinuedFraction( a, b, x ) / a;
        }
        else
        {
            return 1.0 - bt*incompleteBetaContinuedFraction( b, a, 1.0 - x )/b;
        }

    }

    /**
     * Evaluates the continued fraction of the incomplete beta function.
     * Based on the math from NRC's 6.4 "Incomplete Beta Function"
     * @param a
     * Parameter a to the beta continued fraction
     * @param b
     * Parameter b to the beta continued fraction
     * @param x
     * Parameter x to the beta continued fraction
     * @return
     * Incomplete beta function continued fraction
     */
    @PublicationReference(
        author={
            "William H. Press",
            "Saul A. Teukolsky",
            "William T. Vetterling",
            "Brian P. Flannery"
        },
        title="Numerical Recipes in C, Second Edition",
        type=PublicationType.Book,
        year=1992,
        pages=227,
        notes="Incomplete Beta Function continued fraction terms for Lentz's method",
        url="http://www.nrbook.com/a/bookcpdf.php"

    )
    protected static double incompleteBetaContinuedFraction(
        double a,
        double b,
        double x )
    {

        double apb = a+b;
        LentzMethod lentz = new LentzMethod();
        lentz.initializeAlgorithm( 0.0 );
        lentz.iterate(1.0, 1.0);
        while( lentz.getKeepGoing() )
        {
            int m = lentz.getIteration() / 2;
            double ap2m = a + 2*m;

            double aterm;

            if( (lentz.getIteration() % 2) != 0 )
            {
                // Odd iteration cycle
                double num = -(a+m) * (apb+m) * x;
                double den = ap2m * (ap2m + 1);
                aterm = num / den;
            }
            else
            {
                // Even iteration cycle
                double num = m * (b-m) * x;
                double den = (ap2m - 1) * ap2m;
                aterm = num / den;
            }

            lentz.iterate(aterm, 1.0);

        }

        if( lentz.isResultValid() )
        {
            return lentz.getResult();
        }
        else
        {
            System.out.printf( "a = %f, b = %f, x = %f\n", a, b, x );
            throw new OperationNotConvergedException(
                "Lentz's Method failed in Beta continuous fraction!" );
        }

    }

    /**
     * Evaluates the natural logarithm of the multinomial beta function
     * for the given input vector.
     * @param input
     * Input vector to consider.
     * @return
     * Natural logarithm of the Multinomial beta function evaluated at the
     * given input.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Dirichlet distribution",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Dirichlet_distribution",
        notes="Multinomial Beta Function found in the \"Probability density function\" section."
    )
    static public double logMultinomialBetaFunction(
        Vector input)
    {
        double logsum = 0.0;
        double inputSum = 0.0;
        for( int i = 0; i < input.getDimensionality(); i++ )
        {
            double ai = input.getElement(i);
            inputSum += ai;
            logsum += logGammaFunction(ai);
        }
        logsum -= logGammaFunction(inputSum);
        return logsum;
    }

}
