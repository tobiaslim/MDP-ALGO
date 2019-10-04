package algorithm.contracts;

import algorithm.models.RobotModel;

public interface AlgorithmContract extends Runnable {

    void pauseAlgorithm();

    boolean canPlay();

    void resumeAlgorithm();
}
