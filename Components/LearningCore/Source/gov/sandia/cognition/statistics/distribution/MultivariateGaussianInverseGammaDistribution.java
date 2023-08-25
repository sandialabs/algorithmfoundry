/*
 * File:                MultivariateGaussianInverseGammaDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 1, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.AbstractDistribution;
import java.util.ArrayList;
import java.util.Random;

/**
 * A distribution where the mean is selected by a multivariate Gaussian and
 * a variance parameter (either for a univariate Gaussian or isotropic Gaussian)
 * is determined by an Inverse-Gamma distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */

public class MultivariateGaussianInverseGammaDistribution
    extends AbstractDistribution<Vector>
{

    /**
     * Default dimensionality, {@value}.
     */
    public static final int DEFAULT_DIMENSIONALITY = 2;

    /**
     * Gaussian component
     */
    protected MultivariateGaussian gaussian;

    /**
     * Inverse-Gamma component
     */
    protected InverseGammaDistribution inverseGamma;

    /**
     * Default constructor
     */
    public MultivariateGaussianInverseGammaDistribution()
    {
        this( DEFAULT_DIMENSIONALITY );
    }

    /**
     * Creates a new instance of MultivariateGaussianInverseGammaDistribution
     * @param dimensionality
     * Dimensionality of the multivariate Gaussian
     */
    public MultivariateGaussianInverseGammaDistribution(
        int dimensionality )
    {
        this( new MultivariateGaussian( dimensionality ),
            new InverseGammaDistribution() );
    }

    /**
     * Creates a new instance of MultivariateGaussianInverseGammaDistribution
     * @param gaussian
     * Gaussian component
     * @param inverseGamma
     * Inverse-Gamma component
     */
    public MultivariateGaussianInverseGammaDistribution(
        MultivariateGaussian gaussian,
        InverseGammaDistribution inverseGamma)
    {
        this.gaussian = gaussian;
        this.inverseGamma = inverseGamma;
    }

    public Vector getMean()
    {
        return this.getGaussian().getMean();
    }

    public ArrayList<Vector> sample(
        Random random,
        int numSamples)
    {

        ArrayList<? extends Double> varianceScales =
            this.getInverseGamma().sample(random,numSamples);
        MultivariateGaussian sampler = this.getGaussian().clone();
        ArrayList<Vector> samples = new ArrayList<Vector>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            double varianceScale = varianceScales.get(n);
            sampler.setCovariance(
                this.getGaussian().getCovariance().scale( varianceScale ) );
            samples.add( sampler.sample(random) );
        }
        return samples;
    }

    /**
     * Getter for gaussian
     * @return
     * Gaussian component
     */
    public MultivariateGaussian getGaussian()
    {
        return gaussian;
    }

    /**
     * Setter for gaussian
     * @param gaussian
     * Gaussian component
     */
    public void setGaussian(
        MultivariateGaussian gaussian)
    {
        this.gaussian = gaussian;
    }

    /**
     * Getter for inverseGamma
     * @return
     * Inverse-Gamma component
     */
    public InverseGammaDistribution getInverseGamma()
    {
        return this.inverseGamma;
    }

    /**
     * Setter for inverseGamma
     * @param inverseGamma
     * Inverse-Gamma component
     */
    public void setInverseGamma(
        InverseGammaDistribution inverseGamma)
    {
        this.inverseGamma = inverseGamma;
    }

}

