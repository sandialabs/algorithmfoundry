/*
 * File:                LogNormalDistribution.java
 * Authors:             Kevin R. Dixon
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

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothScalarDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Log-Normal distribution PDF and CDF implementations.  The Log-Normal
 * distribution is related to a UnivariateGaussian where the natural logarithm
 * of the random variable is normally distributed.  This turns up in 
 * application areas that are the product of some random variables, where
 * each random variable is i.i.d. and normally distributed.  Stock market
 * returns are the classic example of a Log-Normal distribution.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Log-normal distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Log-normal_distribution"
)
public class LogNormalDistribution
    extends AbstractClosedFormSmoothScalarDistribution
{

    /**
     * Default log normal mean, {@value}.
     */
    public static final double DEFAULT_LOG_NORMAL_MEAN = 0.0;

    /**
     * Default log normal variance, {@value}.
     */
    public static final double DEFAULT_LOG_NORMAL_VARIANCE = 1.0;

    /**
     * Constant value of Math.sqrt(2*Math.PI)
     */
    public static final double SQRT2PI = Math.sqrt( 2.0 * Math.PI );

    /**
     * Mean of the underlying distribution, (-infinity,+infinity)
     */
    private double logNormalMean;

    /**
     * Variance of the underlying distribution, (0,infinity)
     */
    private double logNormalVariance;

    /**
     * Default constructor.
     */
     public LogNormalDistribution()
     {
         this( DEFAULT_LOG_NORMAL_MEAN, DEFAULT_LOG_NORMAL_VARIANCE );
     }

    /**
     * Creates a new instance of LogNormalDistribution
     * @param logNormalMean 
     * Mean of the underlying distribution, (-infinity,+infinity)
     * @param logNormalVariance 
     * Variance of the underlying distribution, (0,infinity)
     */
    public LogNormalDistribution(
        double logNormalMean,
        double logNormalVariance )
    {
        super();
        this.setLogNormalMean( logNormalMean );
        this.setLogNormalVariance( logNormalVariance );
    }

    /**
     * Copy Constructor
     * @param other LogNormalDistribution to copy
     */
    public LogNormalDistribution(
        LogNormalDistribution other )
    {
        this( other.getLogNormalMean(), other.getLogNormalVariance() );
    }

    /**
     * Returns a 2-dimensional Vector with ( logNormalMean logNormalVariance )
     * @return 
     * 2-dimensional Vector with ( logNormalMean logNormalVariance )
     */
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getLogNormalMean(), this.getLogNormalVariance() );
    }

    /**
     * Sets the parameters of the distribution from a
     * 2-dimensional Vector with ( logNormalMean logNormalVariance )
     * @param parameters 
     * 2-dimensional Vector with ( logNormalMean logNormalVariance )
     */
    public void convertFromVector(
        Vector parameters )
    {
        if( parameters.getDimensionality() != 2 )
        {
            throw new IllegalArgumentException(
                "Parameters must be dimension 2" );
        }
        this.setLogNormalMean( parameters.getElement( 0 ) );
        this.setLogNormalVariance( parameters.getElement( 1 ) );
    }

    /**
     * Getter for logNormalMean
     * @return 
     * Mean of the underlying distribution, (-infinity,+infinity)
     */
    public double getLogNormalMean()
    {
        return this.logNormalMean;
    }

    /**
     * Setter for logNormalMean
     * @param logNormalMean 
     * Mean of the underlying distribution, (-infinity,+infinity)
     */
    public void setLogNormalMean(
        double logNormalMean )
    {
        this.logNormalMean = logNormalMean;
    }

    /**
     * Getter for logNormalVariance
     * @return 
     * Variance of the underlying distribution, (0,infinity)
     */
    public double getLogNormalVariance()
    {
        return this.logNormalVariance;
    }

    /**
     * Setter for logNormalVariance
     * @param logNormalVariance 
     * Variance of the underlying distribution, (0,infinity)
     */
    public void setLogNormalVariance(
        double logNormalVariance )
    {
        if( logNormalVariance <= 0.0 )
        {
            throw new IllegalArgumentException(
                "logNormalVariance must be > 0.0" );
        }
        this.logNormalVariance = logNormalVariance;
    }

    public Double getMean()
    {
        double exp = this.getLogNormalMean() + 0.5 * this.getLogNormalVariance();
        return Math.exp( exp );
    }

    public double getVariance()
    {
        double exp1 = this.getLogNormalVariance();
        double exp2 = 2.0 * this.getLogNormalMean() + this.getLogNormalVariance();

        // (Math.exp(exp1)-1)*Math.exp(exp2)
        return (Math.expm1( exp1 ) * Math.exp( exp2 ));
    }

    public ArrayList<Double> sample(
        Random random,
        int numSamples )
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        final double std = Math.sqrt(this.logNormalVariance);
        for( int n = 0; n < numSamples; n++ )
        {
            double normal = random.nextGaussian();
            double exponent = this.logNormalMean + std * normal;
            samples.add( Math.exp( exponent ) );
        }
        return samples;
    }

    public LogNormalDistribution.CDF getCDF()
    {
        return new LogNormalDistribution.CDF( this );
    }

    public LogNormalDistribution.PDF getProbabilityFunction()
    {
        return new LogNormalDistribution.PDF( this );
    }

    @Override
    public String toString()
    {
        return "Log-Mean: " + this.logNormalMean + " Log-Variance: " + this.logNormalVariance;
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
     * PDF of a Log-normal distribution
     */
    public static class PDF
        extends LogNormalDistribution
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
         * Creates a new instance of LogNormalDistribution
         * @param logNormalMean 
         * Mean of the underlying distribution, (-infinity,+infinity)
         * @param logNormalVariance 
         * Variance of the underlying distribution, (0,infinity)
         */
        public PDF(
            double logNormalMean,
            double logNormalVariance )
        {
            super( logNormalMean, logNormalVariance );
        }

        /**
         * Copy Constructor
         * @param other LogNormalDistribution to copy
         */
        public PDF(
            LogNormalDistribution other )
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
            return evaluate( 
                input, this.getLogNormalMean(), this.getLogNormalVariance() );
        }

        /**
         * Evaluates the Log-Normal PDF for the given input and parameters
         * logNormalMean, logNormalVariance
         * @param input 
         * Input about which to evaluate the PDF
         * @param logNormalMean 
         * Mean of the underlying distribution, (-infinity,+infinity)
         * @param logNormalVariance 
         * Variance of the underlying distribution, (0,infinity)
         * @return 
         * pdf(input|mean,variance)
         */
        public static double evaluate(
            double input,
            double logNormalMean,
            double logNormalVariance )
        {
            if (input <= 0)
            {
                return 0.0;
            }

            double delta = Math.log( input ) - logNormalMean;
            double exp = Math.exp( delta * delta / (-2.0 * logNormalVariance) );
            double denom = SQRT2PI * input * Math.sqrt( logNormalVariance );
            return exp / denom;
        }

        public double logEvaluate(
            Double input)
        {
            return logEvaluate(input, this.getLogNormalMean(), this.getLogNormalVariance());
        }

        /**
         * Computes the natural logarithm of the PDF.
         * @param input
         * Inpu to consider.
         * @param logNormalMean
         * Log normal mean.
         * @param logNormalVariance
         * Log normal variance.
         * @return
         * Natural logarithm of the PDF.
         */
        public static double logEvaluate(
            double input,
            double logNormalMean,
            double logNormalVariance )
        {
            final double logInput = Math.log(input);
            final double delta = logInput - logNormalMean;
            final double exponent = delta*delta / (-2.0*logNormalVariance);
            final double coefficient = -0.5 * Math.log(UnivariateGaussian.PI2*logNormalVariance);
            return exponent + coefficient - logInput;
        }

        @Override
        public PDF getProbabilityFunction()
        {
            return this;
        }

    }
    
    /**
     * CDF of the Log-Normal Distribution
     */
    public static class CDF
        extends LogNormalDistribution
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
         * Creates a new instance of LogNormalDistribution
         * @param logNormalMean 
         * Mean of the underlying distribution, (-infinity,+infinity)
         * @param logNormalVariance 
         * Variance of the underlying distribution, (0,infinity)
         */
        public CDF(
            double logNormalMean,
            double logNormalVariance )
        {
            super( logNormalMean, logNormalVariance );
        }

        /**
         * Copy Constructor
         * @param other LogNormalDistribution to copy
         */
        public CDF(
            LogNormalDistribution other )
        {
            super( other );
        }

        public double evaluate(
            double input )
        {
            return evaluate(
                input, this.getLogNormalMean(), this.getLogNormalVariance() );
        }

        /**
         * Evaluates the Log-Normal CDF for the given input and parameters
         * @param x 
         * Input about which to compute the CDF
         * @param logNormalMean 
         * Mean of the underlying distribution, (-infinity,+infinity)
         * @param logNormalVariance 
         * Variance of the underlying distribution, (0,infinity)
         * @return 
         * CDF of the distribution
         */
        public static double evaluate(
            double x,
            double logNormalMean,
            double logNormalVariance )
        {
            if (x <= 0.0)
            {
                return 0.0;
            }

            double num = Math.log( x ) - logNormalMean;
            double denom = Math.sqrt( 2 * logNormalVariance );
            double erf = UnivariateGaussian.ErrorFunction.INSTANCE.evaluate( num / denom );
            return 0.5 * (1 + erf);
        }

        public Double evaluate(
            Double input )
        {
            return this.evaluate( input.doubleValue() );
        }        

        @Override
        public LogNormalDistribution.CDF getCDF()
        {
            return this;
        }

        public LogNormalDistribution.PDF getDerivative()
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
     * Maximum Likelihood Estimator of a log-normal distribution.
     */
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double,LogNormalDistribution>
    {

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
        }

        public LogNormalDistribution.PDF learn(
            Collection<? extends Double> data)
        {
            ArrayList<Double> logdata = new ArrayList<Double>( data.size() );
            for( Double value : data )
            {
                logdata.add( Math.log(value) );
            }

            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeMeanAndVariance( logdata );
            return new LogNormalDistribution.PDF(
                pair.getFirst(), pair.getSecond() );
        }

    }

    /**
     * Maximum Likelihood Estimator from weighted data
     */
    public static class WeightedMaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<Double,LogNormalDistribution>
    {

        /**
         * Default constructor
         */
        public WeightedMaximumLikelihoodEstimator()
        {
        }

        public LogNormalDistribution.PDF learn(
            Collection<? extends WeightedValue<? extends Double>> data )
        {
            ArrayList<DefaultWeightedValue<Double>> logdata =
                new ArrayList<DefaultWeightedValue<Double>>( data.size() );
            for( WeightedValue<? extends Number> value : data )
            {
                final double x = value.getValue().doubleValue();
                double logx;
                double weight;
                if( x > 0.0 )
                {
                    logx = Math.log(x);
                    weight = value.getWeight();
                }
                else
                {
                    logx = Double.NEGATIVE_INFINITY;
                    weight = 0.0;
                }                
                logdata.add( new DefaultWeightedValue<Double>( logx, weight ) );
            }

            Pair<Double,Double> pair =
                UnivariateStatisticsUtil.computeWeightedMeanAndVariance(logdata);
            return new LogNormalDistribution.PDF(
                pair.getFirst(), pair.getSecond() );
        }

    }

}
