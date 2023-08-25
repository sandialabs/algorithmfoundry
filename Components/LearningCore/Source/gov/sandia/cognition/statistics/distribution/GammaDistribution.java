/*
 * File:                GammaDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothScalarDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Class representing the Gamma distribution.  This is a two-parameter family
 * of continuous distributions. The well-known exponential and chi-square
 * distributions are special cases of the Gamma distribution.  Please note that
 * we use the "shape" and "scale" parameters to describe the PDF/CDF, whereas
 * octave/MATLAB use "shape" and "1.0/scale" to parameterize a Gamma
 * distribution, so please beware when comparing results to octave/MATLAB.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Gamma distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Gamma_distribution"
)
public class GammaDistribution
    extends AbstractClosedFormSmoothScalarDistribution
{

    /**
     * Default shape, {@value}.
     */
    public static final double DEFAULT_SHAPE = 1.0;

    /**
     * Default scale, {@value}.
     */
    public static final double DEFAULT_SCALE = 1.0;

    /**
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     */
    private double shape;

    /**
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    private double scale;

    /**
     * Default constructor.
     */
    public GammaDistribution()
    {
        this( DEFAULT_SHAPE, DEFAULT_SCALE );
    }

    /**
     * Creates a new instance of GammaDistribution
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    public GammaDistribution(
        double shape,
        double scale )
    {
        this.setShape( shape );
        this.setScale( scale );
    }

    /**
     * Copy constructor
     * @param other
     * GammaDistribution to copy
     */
    public GammaDistribution(
        GammaDistribution other  )
    {
        this( other.getShape(), other.getScale() );
    }

    @Override
    public GammaDistribution clone()
    {
        return (GammaDistribution) super.clone();
    }

    /**
     * Getter for shape
     * @return
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     */
    public double getShape()
    {
        return this.shape;
    }

    /**
     * Setter for shape
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     */
    public void setShape(
        double shape )
    {
        if (shape <= 0.0)
        {
            throw new IllegalArgumentException( "Shape must be > 0.0" );
        }
        this.shape = shape;
    }

    /**
     * Getter for scale
     * @return
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Setter for scale
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     */
    public void setScale(
        double scale )
    {
        if (scale <= 0.0)
        {
            throw new IllegalArgumentException( "Scale must be > 0.0" );
        }
        this.scale = scale;
    }


    public Double getMean()
    {
        return this.getShape() * this.getScale();
    }

    public double getVariance()
    {
        return this.getShape() * this.getScale() * this.getScale();
    }

    /**
     * Gets the parameters of the distribution
     * @return
     * 2-dimensional Vector with (shape scale)
     */
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getShape(), this.getScale() );
    }

    /**
     * Sets the parameters of the distribution
     * @param parameters
     * 2-dimensional Vector with (shape scale)
     */
    public void convertFromVector(
        Vector parameters )
    {
        if (parameters.getDimensionality() != 2)
        {
            throw new IllegalArgumentException(
                "Expected a 2-dimensional Vector!" );
        }

        this.setShape( parameters.getElement( 0 ) );
        this.setScale( parameters.getElement( 1 ) );

    }

    /**
     * Efficiently samples from a Gamma distribution given by the
     * shape and scale parameters.
     * @param shape
     * Shape parameter of the Gamma distribution, often written as "k",
     * must be greater than zero
     * @param scale
     * Scale parameters of the Gamma distribution, often written as "theta",
     * must be greater than zero.
     * Note that this is the INVERSE of what octave uses!!
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to generate
     * @return
     * Samples simulated from the Gamma distribution.
     */
    public static ArrayList<Double> sample(
        double shape,
        double scale,
        Random random,
        int numSamples )
    {

        if( shape <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Shape must be > 0.0" );
        }
        if( scale <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Scale must be > 0.0" );
        }

        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        int k = (int) Math.floor(shape);
        double delta = shape - k;
        final double v0 = (delta > 0.0) ? Math.exp(1) / (Math.exp(1)+delta) : 0.0;
        for( int n = 0; n < numSamples; n++ )
        {
            double logSum = 0.0;
            for( int i = 0; i < k; i++ )
            {
                double u = random.nextDouble();
                logSum += Math.log( u );
            }

            double xi = 0.0;
            if( delta > 0.0 )
            {
                double nu = 0.0;
                double xidm1;
                double emxi;
                final int MAX_ITERATIONS = 100;
                int m = 0;
                for( m = 0; m < MAX_ITERATIONS; m++ )
                {
                    double vm2 = random.nextDouble();
                    double vm1 = random.nextDouble();
                    double vm0 = random.nextDouble();
                    if( vm2 < v0 )
                    {
                        xi = Math.pow( vm1, 1.0/delta );
                        xidm1 = Math.pow(xi,delta-1.0);
                        emxi = Math.exp(-xi);
                        nu = vm0 * xidm1;
                    }
                    else
                    {
                        xi = 1.0 - Math.log(vm1);
                        xidm1 = Math.pow(xi,delta-1.0);
                        emxi = Math.exp(-xi);
                        nu = vm0 * emxi;
                    }

                    if( nu <= xidm1*emxi )
                    {
                        break;
                    }
                }

                if( m >= MAX_ITERATIONS )
                {
                    throw new IllegalArgumentException(
                        "Exceeded max iterations in GammaDistribution.sample" );
                }

            }

            samples.add( scale * (xi - logSum) );

        }

        return samples;

    }
    
    public ArrayList<Double> sample(
        Random random,
        int numSamples )
    {
        return sample( this.getShape(), this.getScale(), random, numSamples );
    }

    public GammaDistribution.CDF getCDF()
    {
        return new GammaDistribution.CDF( this );
    }

    public GammaDistribution.PDF getProbabilityFunction()
    {
        return new GammaDistribution.PDF( this );
    }

    @Override
    public String toString()
    {
        return "Shape = " + this.getShape() + ", Scale = " + this.getScale();
    }

    public Double getMinSupport()
    {
        return 0.0;
    }

    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Closed-form PDF of the Gamma distribution
     */
    public static class PDF
        extends GammaDistribution
        implements ScalarProbabilityDensityFunction
    {
     
        /**
         * Default constructor.
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of PDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         */
        public PDF(
            double shape,
            double scale )
        {
            super( shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * GammaDistribution to copy
         */
        public PDF(
            GammaDistribution other  )
        {
            super( other );
        }

        public Double evaluate(
            Double input )
        {
            return this.evaluate( input.doubleValue() );
        }
        
        public double evaluate(
            double input )
        {
            return evaluate( input, this.getShape(), this.getScale() );
        }

        /**
         * Evaluates the Gamma PDF about the input "x", using the given
         * shape and scale
         * @param x
         * Input to the PDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         * @return
         * p(x;shape,scale)
         */
        public static double evaluate(
            double x,
            double shape,
            double scale )
        {
            
            double p;
            if (x > 0.0)
            {
                p =  Math.exp( logEvaluate(x, shape, scale) );
            }
            else
            {
                p = 0.0;
            }

            return p;
        }

        public double logEvaluate(
            Double input)
        {
            return logEvaluate( input, this.getShape(), this.getScale() );
        }

        /**
         * Evaluates the natural logarithm of the PDF.
         * @param input
         * Input to consider.
         * @param shape
         * Shape factor.
         * @param scale
         * Scale factor.
         * @return
         * Natural logarithm of the PDF.
         */
        public static double logEvaluate(
            double input,
            double shape,
            double scale )
        {

            if( input <= 0.0 )
            {
                return Math.log(0.0);
            }
            else
            {
                final double n1 = (shape-1.0) * Math.log(input);
                final double n2 = -input / scale;
                final double d1 = MathUtil.logGammaFunction(shape);
                final double d2 = shape * Math.log(scale);
                return n1 + n2 - d1 - d2;
            }

        }

        @Override
        public GammaDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }
    
    /**
     * CDF of the Gamma distribution
     */
    public static class CDF
        extends GammaDistribution
        implements SmoothCumulativeDistributionFunction
    {
        
        /**
         * Default constructor.
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of CDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         */
        public CDF(
            double shape,
            double scale )
        {
            super( shape, scale );
        }

        /**
         * Copy constructor
         * @param other
         * GammaDistribution to copy
         */
        public CDF(
            GammaDistribution other  )
        {
            super( other );
        }
        

        public Double evaluate(
            Double input )
        {
            return this.evaluate( input.doubleValue() );
        }

        
        public double evaluate(
            double input )
        {
            return evaluate( input, this.getShape(), this.getScale() );
        }

        /**
         * Evaluates the CDF of the Gamma distribution about x, given 
         * the shape and scale parameters
         * @param x
         * Input to the CDF
         * @param shape
         * Shape parameter of the Gamma distribution, often written as "k",
         * must be greater than zero
         * @param scale
         * Scale parameters of the Gamma distribution, often written as "theta",
         * must be greater than zero.
         * Note that this is the INVERSE of what octave uses!!
         * @return
         * Pr(y le x;shape,scale)
         */
        public static double evaluate(
            double x,
            double shape,
            double scale )
        {
            double p;
            if (x <= 0.0)
            {
                p = 0.0;
            }
            else
            {
                p = MathUtil.lowerIncompleteGammaFunction( shape, x / scale );
            }
            return p;
        }

        @Override
        public GammaDistribution.CDF getCDF()
        {
            return this;
        }

        public GammaDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        public Double differentiate(
            Double input)
        {
            return this.getDerivative().evaluate(input);
        }

    }

    /**
     * Computes the parameters of a Gamma distribution by the
     * Method of Moments
     */
    public static class MomentMatchingEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,GammaDistribution>
    {

        /**
         * Default constructor
         */
        public MomentMatchingEstimator()
        {
        }

        public GammaDistribution learn(
            Collection<? extends Double> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeMeanAndVariance(data);
            return learn( pair.getFirst(), pair.getSecond() );
        }

        /**
         * Computes the Gamma distribution describes by the given moments
         * @param mean
         * Mean of the distribution
         * @param variance
         * Variance of the distribution
         * @return
         * Gamma distribution that has the same mean/variance as the
         * given parameters.
         */
        public static GammaDistribution learn(
            double mean,
            double variance )
        {
            double scale = variance / mean;
            double shape = mean*mean / variance;
            return new GammaDistribution(shape, scale);
        }

    }

    /**
     * Estimates the parameters of a Gamma distribution using the matching
     * of moments, not maximum likelihood.
     */
    public static class WeightedMomentMatchingEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,GammaDistribution>
    {

        /**
         * Default constructor
         */
        public WeightedMomentMatchingEstimator()
        {
        }

        public GammaDistribution learn(
            Collection<? extends WeightedValue<? extends Double>> data)
        {
            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeWeightedMeanAndVariance(data);
            return MomentMatchingEstimator.learn(
                pair.getFirst(), pair.getSecond());
        }

    }

}
