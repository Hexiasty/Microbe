package pl.magosa.microbe;

import java.util.ArrayList;
import java.util.Collections;

/**
 * (c) 2014 Krzysztof Magosa
 */
public abstract class Teacher<T extends Network>  {
    protected double learningRate = 0.75;
    protected double momentum = 0.95;
    protected double lastEpochError;
    protected ArrayList<LearningSet> learningData;
    protected T network;

    public Teacher() {
        learningData = new ArrayList<LearningSet>();
    }

    public void setLearningRate(final double rate) {
        learningRate = rate;
    }

    public void setMomentum(final double momentum) {
        this.momentum = momentum;
    }

    public void addLearningSet(final LearningSet set) {
        learningData.add(set);
    }

    public double getError() {
        return lastEpochError;
    }

    protected double squaredError(LearningSet set) {
        network.setValues(set.getInput());
        network.run();

        double error = 0;
        double outputs[] = network.getOutput();
        double desired[] = set.getOutput();

        for (int i = 0; i < outputs.length; i++) {
            double x = outputs[i] - desired[i];
            error += Math.pow(x, 2);
        }

        return error;
    }

    protected double squaredErrorEpoch() {
        double error = 0;

        for (LearningSet set : learningData) {
            error += squaredError(set);
        }

        return Math.sqrt(error / learningData.size());
    }

    public boolean train(int maxEpochs, double maxError) {
        if (squaredErrorEpoch() <= maxError) {
            // Don't train already trained network
            return true;
        }

        for (int epoch = 1; epoch <= maxEpochs; epoch++) {
            trainEpoch();

            lastEpochError = squaredErrorEpoch();
            if (lastEpochError <= maxError) {
                return true;
            }

            Collections.shuffle(learningData);
        }

        return false;
    }

    public static Teacher factory(Network network) {
        if (network instanceof FeedForwardNetwork) {
            return new FeedForwardTeacher((FeedForwardNetwork)network);
        }

        throw new RuntimeException("Unsupported network type.");
    }

    abstract protected void trainEpoch();
}
