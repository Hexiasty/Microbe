package pl.magosa.microbe.examples.xor;

import pl.magosa.microbe.*;

/**
 * Example of simple network trained under supervision.
 * Problem to solve:  XOR
 * Transfer function: Hyperbolic tangent
 * Assumptions:
 *  -1 -> false
 *   1 -> true
 *
 * (c) 2014 Krzysztof Magosa
 */
public class Main {
    protected static FeedForwardNetwork network;
    protected static TransferFunction transferFunction;

    private static void print(final double x, final double y) {
        network.setValues(new double[] { x, y });
        network.run();
        double[] output = network.getOutput();

        System.out.printf("(% .0f xor % .0f) -> % .0f\n", x, y, output[0]);
    }

    public static void main(String[] args) {
        transferFunction = new TanhTransferFunction();

        network = FeedForwardNetwork.newInstance()
            .inputLayer(2)
            .hiddenLayer(3, transferFunction)
            .outputLayer(1, transferFunction)
            .build();

        Teacher teacher = Teacher.factory(network);
        teacher.setMomentum(0.7);

        // Truth table
        teacher.addLearningSet(new double[] { -1.0, -1.0 }, new double[] { -1.0 });
        teacher.addLearningSet(new double[] { -1.0,  1.0 }, new double[] {  1.0 });
        teacher.addLearningSet(new double[] {  1.0, -1.0 }, new double[] {  1.0 });
        teacher.addLearningSet(new double[] {  1.0,  1.0 }, new double[] { -1.0 });

        // Train
        TeacherController controller = new TeacherController(teacher);
        controller.setGoal(0.01);
        controller.setDebug(true);
        controller.setMaximumLearningRate(0.25);
        controller.train();

        print(-1.0, -1.0);
        print(-1.0,  1.0);
        print( 1.0, -1.0);
        print( 1.0,  1.0);
    }
}
