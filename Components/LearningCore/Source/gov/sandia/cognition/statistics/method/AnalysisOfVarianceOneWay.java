/*
 * File:                AnalysisOfVarianceOneWay.java
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

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.distribution.SnedecorFDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Analysis of Variance single-factor null-hypothesis testing procedure,
 * usually called "1-way ANOVA".
 * ANOVA evaluates the probability of the null hypothesis for a Collection of
 * treatment cases.  Each "treatment" is an experiment with a Collection of
 * results from a given population.  You can have different population sizes
 * in each treatment.  The null hypothesis is that there are no differences
 * between the populations and that observed differences are due to chance. 
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@ConfidenceTestAssumptions(
    name="One-Way Analysis of Variance",
    alsoKnownAs={
        "1-way ANOVA",
        "Fixed-effects 1-way ANOVA",
        "F test"
    },
    description={
        "ANOVA tests to determine if the means between the various treatments are equal.",
        "ANOVA is a generalization of the paired Student t-test, where there can be multiple treatments.",
        "When there are two groups, a control group and a treatment group, ANOVA is equivalent to the unpaired t-test."
    },
    assumptions={
        "The data are sampled from a Gaussian distribution.",
        "The variance within the different groups is equal.",
        "The data from each group is collected independently of each other."
    },
    nullHypothesis="The means from all groups are equal.",
    dataPaired=false,
    dataSameSize=false,
    distribution=SnedecorFDistribution.CDF.class,
    reference=@PublicationReference(
        author="Wikipedia",
        title="Analysis of Variance",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Analysis_of_variance"
    )
)
public class AnalysisOfVarianceOneWay
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<Double>>
{
    
    /**
     * Creates a new instance of AnalysisOfVarianceOneWay
     */
    public AnalysisOfVarianceOneWay()
    {
    }
    
    /**
     * Computes the ANOVA statistics for and arbitrary number of Collection 
     * of treatments, where each treatment can have a different 
     * number of samples.
     * @param data A Collection of treatments, where each treatment can
     * have a different number of samples
     * @return ANOVA Confidence statistics
     */
    @PublicationReference(
        author={
            "Frederick J. Gravetter",
            "Larry B. Wallnau"
        },
        title="Statistics for the Behavioral Sciences",
        type=PublicationType.Book,
        year=2003,
        pages={406,412},
        notes="Chapter 13.3"
    )
    static public AnalysisOfVarianceOneWay.Statistic evaluateNullHypothesis(
        Collection<Collection<Double>> data )
    {
        
        // I apologize for this clunky notation... it comes from a social
        // science text book: "Statistics for the Behavioral Sciences" by
        // Gravetter & Wallnau, Chapter 13.3, p. 406-412
        
        // number of treatments
        int k = data.size();
        
        // total number of samples
        int N = 0;
        double G = 0.0;
        double Sxx = 0.0;
        double SSwithin = 0.0;
        double SSbetween = 0.0;
        LinkedList<UnivariateGaussian> gaussians = 
            new LinkedList<UnivariateGaussian>();
        UnivariateGaussian.MaximumLikelihoodEstimator mle = 
            new UnivariateGaussian.MaximumLikelihoodEstimator();
        for( Collection<Double> treatment : data )
        {
            int n = treatment.size();
            UnivariateGaussian g = mle.learn( treatment );
            double SS = g.getVariance() * (n-1);
            SSwithin += SS;
            
            double T = g.getMean() * n;
            SSbetween += ((T*T) / n);
            
            gaussians.add( g );
            N += n;
            for( Double x : treatment )
            {
                G += x;
                Sxx += x*x;
            }
        }
        
        SSbetween -= ((G*G) / N);
        
        //double SStotal = Sxx - ((G*G)/N);
        
        int dfbetween = k-1;
        int dfwithin = N-k;
        
        double MSbetween = SSbetween / dfbetween;
        double MSwithin = SSwithin / dfwithin;
        
        double F = MSbetween / MSwithin;
        
        return new AnalysisOfVarianceOneWay.Statistic( F, dfbetween, dfwithin );
        
    }
    
    /**
     * Evaluates the ANOVA statistics for the two given treatments, each
     * treatment can have a different number of samples
     * @param data1 First treatment
     * @param data2 Second treatment
     * @return ANOVA Confidence statistics
     */
    public AnalysisOfVarianceOneWay.Statistic evaluateNullHypothesis(
        Collection<Double> data1,
        Collection<Double> data2)
    {
        ArrayList<Collection<Double>> data =
            new ArrayList<Collection<Double>>(2);
        data.add( data1 );
        data.add( data2 );
        return AnalysisOfVarianceOneWay.evaluateNullHypothesis( data );
    }
    
    
    /**
     * Returns the confidence statistic for an ANOVA test
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {
        
        /**
         * Input to the Snedecor F-distribution
         */
        private double F;
        
        /**
         * Degrees of freedom between the treatments
         */
        private double DFbetween;
        
        /**
         * Degrees of freedom within the treatments
         */
        private double DFwithin;
        
        
        /**
         * Creates a new instance of Statistic
         * @param F 
         * Input to the Snedecor F-distribution
         * @param DFbetween 
         * Degrees of freedom between the treatments
         * @param DFwithin 
         * Degrees of freedom within the treatments
         */
        public Statistic(
            double F,
            double DFbetween,
            double DFwithin )
        {
            super( 1 - SnedecorFDistribution.CDF.evaluate( F, DFbetween, DFwithin ) );
            this.setF( F );
            this.setDFbetween( DFbetween );
            this.setDFwithin( DFwithin );
        }
        
        @Override
        public Statistic clone()
        {
            return (Statistic) super.clone();
        }

        /**
         * Getter for F
         * @return 
         * Input to the Snedecor F-distribution
         */
        public double getF()
        {
            return this.F;
        }

        /**
         * Setter for F
         * @param F 
         * Input to the Snedecor F-distribution
         */
        protected void setF(
            double F)
        {
            this.F = F;
        }

        /**
         * Getter for DFbetween
         * @return 
         * Degrees of freedom between the treatments
         */
        public double getDFbetween()
        {
            return this.DFbetween;
        }

        /**
         * Setter for DFbetween
         * @param DFbetween 
         * Degrees of freedom between the treatments
         */
        protected void setDFbetween(
            double DFbetween)
        {
            this.DFbetween = DFbetween;
        }

        /**
         * Getter for DFwithin
         * @return 
         * Degrees of freedom within the treatments
         */
        public double getDFwithin()
        {
            return this.DFwithin;
        }

        /**
         * Setter for DFwithin
         * @param DFwithin 
         * Degrees of freedom within the treatments
         */
        protected void setDFwithin(
            double DFwithin)
        {
            this.DFwithin = DFwithin;
        }
        
    }
    
}
