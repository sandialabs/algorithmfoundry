/*
 * File:                UniformDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2007, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothScalarDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Contains the (very simple) definition of a continuous Uniform distribution,
 * parameterized between the minimum and maximum bounds.
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Uniform distribution (continuous)",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Uniform_distribution_(continuous)"
)
public class UniformDistribution
    extends AbstractClosedFormSmoothScalarDistribution
{

    /**
     * Default min, {@value}.
     */
    public static final double DEFAULT_MIN = 0.0;

    /**
     * Default max, {@value}.
     */
    public static final double DEFAULT_MAX = 1.0;

    /**
     * Minimum x bound on the distribution
     */
    private double minSupport;

    /**
     * Maximum bound on the distribution
     */
    private double maxSupport;
    
    /** 
     * Creates a new instance of UniformDistribution 
     */
    public UniformDistribution()
    {
        this( DEFAULT_MIN, DEFAULT_MAX );
    }
    
    /** 
     * Creates a new instance of UniformDistribution 
     * @param minSupport
     * Minimum x bound on the distribution
     * @param maxSupport
     * Maximum bound on the distribution
     */
    public UniformDistribution(
        double minSupport,
        double maxSupport )
    {
        this.setMinSupport( minSupport );
        this.setMaxSupport( maxSupport );
    }
    
    /**
     * Copy constructor
     * @param other
     * UniformDistribution to copy
     */
    public UniformDistribution(
        UniformDistribution other )
    {
        this( other.getMinSupport(), other.getMaxSupport() );
    }
    
    @Override
    public UniformDistribution clone()
    {
        return (UniformDistribution) super.clone();
    }    
    
    public Double getMean()
    {
        return (this.getMaxSupport() + this.getMinSupport()) / 2.0;
    }

    public double getVariance()
    {
        double d = this.getMaxSupport() - this.getMinSupport();
        return d * d / 12.0;
    }

    public ArrayList<Double> sample(
        Random random,
        int numSamples )
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        final double a = UniformDistribution.this.getMinSupport();
        final double delta = UniformDistribution.this.getMaxSupport() - a;
        for( int n = 0; n < numSamples; n++ )
        {
            double x = random.nextDouble();
            samples.add( (x*delta) + a );
        }

        return samples;
    }
    
    public Double getMinSupport()
    {
        return this.minSupport;
    }

    /**
     * Setter for minSupport
     * @param minSupport
     * Minimum x bound on the distribution
     */
    public void setMinSupport(
        double minSupport )
    {
        this.minSupport = minSupport;
    }

    public Double getMaxSupport()
    {
        return this.maxSupport;
    }

    /**
     * Setter for maxSupport
     * @param maxSupport
     * Maximum x bound on the distribution
     */
    public void setMaxSupport(
        double maxSupport )
    {
        this.maxSupport = maxSupport;
    }    
    
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getMinSupport(), this.getMaxSupport() );
    }

    public void convertFromVector(
        Vector parameters )
    {
        if( parameters.getDimensionality() != 2 )
        {
            throw new IllegalArgumentException(
                "Parameters must be dimension 2" );
        }
        double a = parameters.getElement(0);
        double b = parameters.getElement(1);

        this.setMinSupport( Math.min(a,b) );
        this.setMaxSupport( Math.max(a, b) );
    }

    public UniformDistribution.CDF getCDF()
    {
        return new UniformDistribution.CDF( this );

    }

    public UniformDistribution.PDF getProbabilityFunction()
    {
        return new UniformDistribution.PDF( this );
    }

    /**
     * Cumulative Distribution Function of a uniform
     */
    public static class CDF
        extends UniformDistribution
        implements SmoothCumulativeDistributionFunction,
        InvertibleCumulativeDistributionFunction<Double>
    {

        /** 
         * Creates a new instance of CDF 
         */
        public CDF()
        {
            super();
        }

        /** 
         * Creates a new instance of CDF 
         * @param minSupport
         * Minimum x bound on the distribution
         * @param maxSupport
         * Maximum bound on the distribution
         */
        public CDF(
            double minSupport,
            double maxSupport )
        {
            super( minSupport, maxSupport );
        }
        
        /**
         * Copy constructor
         * @param other
         * UniformDistribution to copy
         */
        public CDF(
            UniformDistribution other )
        {
            super( other );
        }
        
        @Override
        public CDF clone()
        {
            return (CDF) super.clone();
        }

        public double evaluate(
            double input )
        {
            return evaluate( input, this.getMinSupport(), this.getMaxSupport() );
        }

        public Double evaluate(
            Double input )
        {
            return this.evaluate( input.doubleValue() );
        }
        
        /**
         * Evaluates the Uniform(minSupport,maxSupport) CDF for the given input
         *
         * @return Uniform(minSupport,maxSupport) CDF evaluated at input
         * @param minSupport
         * Minimum x bound on the distribution
         * @param maxSupport
         * Maximum x bound on the distribution
         * @param input Input to evaluate the CDF at
         */
        public static double evaluate(
            double input,
            double minSupport,
            double maxSupport )
        {
            double p;
            if (input < minSupport)
            {
                p = 0.0;
            }
            else if (input > maxSupport)
            {
                p = 1.0;
            }
            else
            {
                p = (input - minSupport) / (maxSupport - minSupport);
            }

            return p;
        }

        @Override
        public UniformDistribution.CDF getCDF()
        {
            return this;
        }

        public UniformDistribution.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        public Double differentiate(
            Double input)
        {
            return this.getDerivative().evaluate(input);
        }

        public Double inverse(
            double probability)
        {
            if( probability <= 0.0 )
            {
                return this.getMinSupport();
            }
            else if( probability >= 1.0 )
            {
                return this.getMaxSupport();
            }
            else
            {
                return probability*(this.getMaxSupport()-this.getMinSupport()) + this.getMinSupport();
            }
        }

    }
    
    /**
     * Probability density function of a Uniform Distribution
     */
    public static class PDF
        extends UniformDistribution
        implements ScalarProbabilityDensityFunction
    {
        
        /** 
         * Creates a new instance of PDF 
         */
        public PDF()
        {
            super();
        }

        /** 
         * Creates a new instance of PDF 
         * @param minSupport
         * Minimum x bound on the distribution
         * @param maxSupport
         * Maximum bound on the distribution
         */
        public PDF(
            double minSupport,
            double maxSupport )
        {
            super( minSupport, maxSupport );
        }
        
        /**
         * Copy constructor
         * @param other
         * UniformDistribution to copy
         */
        public PDF(
            UniformDistribution other )
        {
            super( other );
        }        
        
        public double evaluate(
            double input )
        {
            return evaluate( input, this.getMinSupport(), this.getMaxSupport() );
        }
        
        /**
         * Evaluates the Uniform(minSupport,maxSupport) PDF for the given input
         *
         * @return Uniform(minSupport,maxSupport) PDF evaluated at input
         * @param minSupport
         * Minimum x bound on the distribution
         * @param maxSupport
         * Maximum x bound on the distribution
         * @param input Input to evaluate the CDF at
         */
        public static double evaluate(
            double input,
            double minSupport,
            double maxSupport )
        {
            
            if( (input < minSupport) ||
                (input > maxSupport) )
            {
                return 0.0;
            }
            else
            {
                double d = maxSupport - minSupport;
                if( d != 0.0 )
                {
                    return 1.0/d;
                }
                else
                {
                    return Double.POSITIVE_INFINITY;
                }
            }
            
        }

        public Double evaluate(
            Double input )
        {
            return this.evaluate( input.doubleValue() );
        }

        public double logEvaluate(
            Double input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public PDF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * Maximum Likelihood Estimator of a log-normal distribution.
     */
    @PublicationReference(
        author="Wikipedia",
        title="German tank problem",
        type=PublicationType.WebPage,
        year=2010,
        url="http://en.wikipedia.org/wiki/German_tank_problem"
    )
    public static class MaximumLikelihoodEstimator
        extends AbstractCloneableSerializable
        implements DistributionEstimator<Double, UniformDistribution>
    {

        /**
         * Default constructor
         */
        public MaximumLikelihoodEstimator()
        {
        }

        public UniformDistribution.PDF learn(
            Collection<? extends Double> data)
        {
            Pair<Double,Double> result = UnivariateStatisticsUtil.computeMinAndMax(data);
            final double min = result.getFirst();
            final double max = result.getSecond();
            final int k = data.size();

            double a = min - Math.abs(min/k);
            double b = max + Math.abs(max/k);
            return new UniformDistribution.PDF( a, b );
        }

    }

}
