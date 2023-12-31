/*
 * File:                SupervisedLearnerComparisonExperiment.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.performance.PerformanceEvaluator;
import gov.sandia.cognition.statistics.method.NullHypothesisEvaluator;
import gov.sandia.cognition.util.Summarizer;
import java.util.Collection;

/**
 * A comparison experiment for supervised learners.
 * @param   <InputType> The type of the input data for supervised learning.
 * @param   <OutputType> The type of the output data for supervised learning.
 * @param   <StatisticType> The type of the statistic generated by the
 *          performance evaluator on the learned object for each fold. It is
 *          created by passing the learned object plus the test data for the
 *          fold into the performance evaluator.
 * @param   <SummaryType> The type produced by the summarizer at the end of
 *          the experiment from a collection of the given statistics (one for
 *          each fold). This represents the performance result for the learning
 *          algorithm for the whole experiment.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class SupervisedLearnerComparisonExperiment<InputType,OutputType,StatisticType, SummaryType>
    extends LearnerComparisonExperiment<
        InputOutputPair<InputType,OutputType>,
        InputOutputPair<InputType,OutputType>,
        Evaluator<? super InputType,OutputType>,
        StatisticType,
        SummaryType>
{

    /**
     * Creates a new instance of {@code SupervisedLearnerComparisonExperiment}.
     */
    public SupervisedLearnerComparisonExperiment()
    {
        this(null, null, null, null);
    }

    /**
     * Creates a new instance of {@code SupervisedLearnerComparisonExperiment}.
     *
     * @param  foldCreator The object to use for creating the folds.
     * @param  performanceEvaluator The evaluator to use to compute the
     *         performance of the learned object on each fold.
     * @param  statisticalTest The statistical test to apply to the performance
     *         results of the two learners to determine if they are
     *         statistically different.
     * @param  summarizer The summarizer for summarizing the result of the
     *         performance evaluator from all the folds.
     */
    public SupervisedLearnerComparisonExperiment(
        final ValidationFoldCreator<InputOutputPair<InputType, OutputType>, InputOutputPair<InputType, OutputType>> foldCreator,
        final PerformanceEvaluator<? super Evaluator<? super InputType, OutputType>, Collection<? extends InputOutputPair<InputType, OutputType>>, ? extends StatisticType> performanceEvaluator,
        final NullHypothesisEvaluator<Collection<? extends StatisticType>> statisticalTest,
        final Summarizer<? super StatisticType, ? extends SummaryType> summarizer)
    {
        super(foldCreator, performanceEvaluator, statisticalTest, summarizer);
    }

}
