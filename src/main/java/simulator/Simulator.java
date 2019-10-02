package simulator;

import algorithm.AlgorithmManager;
import algorithm.ArenaMemory;
import algorithm.models.MDFFormat;

import javax.swing.*;
import java.awt.*;

public class Simulator {
    private Mode mode;
    private JFrame wrapper;
    private Arena map;
    private Controls controls;
    private AlgorithmManager algorithmManager;

    public Simulator(Mode mode, AlgorithmManager algorithmManager){
        // attach dependencies
        this.mode = mode;
        this.algorithmManager = algorithmManager;


        /// Set Jframe Parameters
        wrapper = new JFrame();
        wrapper.setLayout(new FlowLayout(FlowLayout.LEFT));
        wrapper.setSize(900, 800);
        wrapper.setLocation(200, 200);
        wrapper.setTitle("Simulator Mode: " + mode.toString());
        wrapper.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        // setMap
        map = new Arena(algorithmManager.getExploredArenaMemory(), algorithmManager.getRobotModel());
        wrapper.add(map.build());

        // set controls
        controls = new Controls(algorithmManager, mode);
        wrapper.add(controls.build());
    }

    public void display(){
        wrapper.setVisible(true);
    }

    public void showAnswerFrame(MDFFormat mdfFormat) {
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.setSize(900, 800);
        frame.setLocation(400, 200);
        frame.setTitle("Loaded map");
        ArenaMemory correctMemory = new ArenaMemory();
        correctMemory.updateArenaWithMDF(mdfFormat);
        Arena arena = new Arena(correctMemory, algorithmManager.getRobotModel());
        frame.add(arena.build());
        frame.setVisible(true);
    }
}


