package algorithm;

import algorithm.contracts.AlgorithmContract;

public class FastestPathAlgorithm implements AlgorithmContract {


    @Override
    public void run() {
        System.out.println("Fastest path started");
    }

    @Override
    public void pauseAlgorithm() {

    }

    @Override
    public boolean canPlay() {
        return false;
    }

    @Override
    public void resumeAlgorithm() {

    }
}
