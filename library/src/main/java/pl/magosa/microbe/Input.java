package pl.magosa.microbe;

import java.util.Random;

/**
 * (c) 2014 Krzysztof Magosa
 */
public class Input {
    protected double weight;
    protected double value;

    public Input() {
        weight = -0.5 + Math.random();
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(final double weight) {
        this.weight = weight;
    }

    public double getValue() {
        return value;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    public double getWeightedValue() {
        return value * weight;
    }
}
