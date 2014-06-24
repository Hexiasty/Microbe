package pl.magosa.microbe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Class represents multi layer feed forward network.
 *
 * (c) 2014 Krzysztof Magosa
 */
public class FeedForwardNetwork extends Network {
    protected ArrayList<FeedForwardLayer> layers;

    public FeedForwardNetwork() {
        layers = new ArrayList<>();
    }

    /**
     * Creates layer, associate it with this network, initialise it using provided lambda.
     *
     * @param initFunction Lamba which initialize layer
     * @return Created layer object
     */
    public FeedForwardLayer createLayer(Consumer<FeedForwardLayer> initFunction) {
        FeedForwardLayer layer = new FeedForwardLayer();

        if (!layers.isEmpty()) {
            layer.setPreviousLayer(getLastLayer());
        }

        initFunction.accept(layer);

        // It must be below initFunction, because while creating layer, last layer is needed.
        layers.add(layer);

        return layer;
    }

    /**
     * Helper method for easy creation of input layer.
     * @param inputNeurons Number of neurons (also inputs)
     */
    public void createInputLayer(final int inputNeurons) {
        createLayer((FeedForwardLayer layer) -> {
            layer.createNeurons(inputNeurons, (Neuron neuron) -> {
                neuron.setTransferFunction(new LinearTransferFunction());
                neuron.setThreshold(0);

                neuron.createInput((Input input) -> {
                    input.setWeight(1);
                });
            });
        });
    }

    /**
     * Sets input values of network
     * @param values
     */
    public void setValues(final double[] values) {
        if (values.length != layers.get(0).getNeurons().size()) {
            throw new RuntimeException("Incorrect number of input values.");
        }

        ArrayList<Neuron> neurons = layers.get(0).getNeurons();

        for (int index = 0; index < values.length; index++) {
            neurons.get(index).getInputs().get(0).setValue(values[index]);
        }
    }

    /**
     * Activate neurons on each layer
     */
    public void run() {
        for (FeedForwardLayer layer : layers) {
            layer.run();

            if (layer.hasNextLayer()) {
                layer.forward();
            }
        }
    }

    /**
     * Returns array of outputs from last layer
     * @return
     */
    public double[] getOutput() {
        FeedForwardLayer layer = getLastLayer();
        ArrayList<Neuron> neurons = layer.getNeurons();

        double[] output = new double[neurons.size()];

        for (int i = 0; i < neurons.size(); i++) {
            output[i] = neurons.get(i).getOutput();
        }

        return output;
    }

    /**
     * Returns last layer in network.
     * It's useful while creating network, e.g to check number of neurons in previously created layer
     * to know how many inputs we need in each neuron.
     * @return Last layer of network
     */
    public FeedForwardLayer getLastLayer() {
        return layers.get(layers.size() - 1);
    }

    /**
     * Returns list of layers associated to this network
     */
    public ArrayList<FeedForwardLayer> getLayers() {
        return layers;
    }

    /**
     * Returns hash map of neurons associated with layers of this network.
     * It's used internally to provide consistent interface for KnowledgeIO reader/writer.
     * @return Hash map of all neurons
     */
    protected HashMap<String, Neuron> getNeuronsMap() {
        HashMap<String, Neuron> map = new HashMap<>();

        for (int li = 0; li < layers.size(); li++) {
            for (int ni = 0; ni < layers.get(li).getNeurons().size(); ni++) {
                map.put(li + "/" + ni, layers.get(li).getNeurons().get(ni));
            }
        }

        return map;
    }
}
