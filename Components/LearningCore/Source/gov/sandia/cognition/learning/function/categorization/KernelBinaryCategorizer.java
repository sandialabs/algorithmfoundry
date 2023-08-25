/*
 * File:                KernelBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.KernelContainer;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The <code>KernelBinaryCategorizer</code> class implements a binary 
 * categorizer that uses a kernel to do its categorization. It is parameterized 
 * by a kernel function, a list of examples and their weights, and a bias term. 
 * This type of classifier represents what is learned by a standard Support 
 * Vector Machine or the Kernel Perceptron.
 *
 * @param  <InputType> The input type for the categorizer.
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelBinaryCategorizer<InputType>
    extends AbstractThresholdBinaryCategorizer<InputType>
    implements KernelContainer<InputType>
{
    /** The default value for the bias is {@value}. */
    public static final double DEFAULT_BIAS = 0.0;

    /** The internal kernel. */
    protected Kernel<? super InputType> kernel;

    /** The list of weighted examples that are used for categorization. */
    protected Collection<? extends WeightedValue<? extends InputType>> examples;
    
    /** The bias term. */
    protected double bias;
    
    /**
     * Creates a new instance of KernelBinaryCategorizer.
     */
    public KernelBinaryCategorizer()
    {
        this((Kernel<? super InputType>) null);
    }
    
    /**
     * Creates a new instance of KernelBinaryCategorizer with the given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public KernelBinaryCategorizer(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, new ArrayList<WeightedValue<InputType>>(), DEFAULT_BIAS);
    }
        
    /**
     * Creates a new instance of KernelBinaryCategorizer with the given kernel,
     * weighted examples, and bias.
     *
     * @param  kernel The kernel to use.
     * @param  examples The weighted examples.
     * @param  bias The bias.
     */
    public KernelBinaryCategorizer(
        final Kernel<? super InputType> kernel,
        final Collection<? extends WeightedValue<? extends InputType>> examples,
        final double bias)
    {
        super(0.0);
        
        this.setExamples(examples);
        this.setBias(bias);
        this.setKernel(kernel);
    }
    
    /**
     * Creates a new copy of a KernelBinaryCategorizer.
     *
     * @param  other The KernelBinaryCategorizer to copy.
     */
    public KernelBinaryCategorizer(
        final KernelBinaryCategorizer<InputType> other)
    {
        this( ObjectUtil.cloneSafe(other.getKernel()),
            (other.getExamples() == null) ? null : new ArrayList<WeightedValue<? extends InputType>>( other.getExamples()),
            other.getBias() );

        this.setThreshold(other.getThreshold());
    }
    
    /**
     * Categorizes the given input vector as a double by:
     * 
     *     sum w_i * k(input, x_i)
     *
     * @param  input The input to categorize.
     * @return The output categorization as a double where the sign is the
     *         categorization.
     */
    public double evaluateAsDouble(
        final InputType input)
    {
        // The sum starts out with the bias term.
        double sum = this.bias;

        // Loop over all the examples.
        for ( WeightedValue<? extends InputType> example : this.examples )
        {
            final double weight = example.getWeight();

            if ( weight == 0.0 )
            {
                continue;
            }

            // Evaluate the kernel between the example and the input.
            final double value = 
                this.kernel.evaluate(input, example.getValue());

            // Updatre the sum.
            sum += weight * value;
        }
        
        return sum;
    }

    /**
     * Gets the list of weighted examples that categorizer is using.
     *
     * @return The list of weighted examples.
     */
    public Collection<? extends WeightedValue<? extends InputType>> 
        getExamples()
    {
        return this.examples;
    }
    
    /**
     * Sets the list of weighted examples that categorizer is using.
     *
     * @param  examples The list of weighted examples.
     */
    public void setExamples(
        final Collection<? extends WeightedValue<? extends InputType>> examples)
    {
        this.examples = examples;
    }

    /**
     * Gets the bias term.
     *
     * @return bias The bias term.
     */
    public double getBias()
    {
        return this.bias;
    }

    /**
     * Sets the bias term.
     *
     * @param  bias The bias term.
     */
    public void setBias(
        final double bias)
    {
        this.bias = bias;
    }

    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the internal kernel.
     *
     * @param  kernel The internal kernel.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
    }
    
}
