package simulator;

import algorithm.AlgorithmManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class Controls {
    JPanel controlPanel;
    JButton startButton;
    JToggleButton pauseResume;
    AlgorithmManager algorithmManager;

    Controls(AlgorithmManager manager, Mode mode){
        this.algorithmManager = manager;
        controlPanel = new JPanel();
        LayoutManager layout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
        controlPanel.setLayout(layout);

        startButton = new JButton("Start");
        controlPanel.add(startButton);
        startButton.addActionListener(startAlgo());


        pauseResume = new JToggleButton("Pause");
        pauseResume.setEnabled(false);
        pauseResume.addItemListener(toggleAlgo());
        controlPanel.add(pauseResume);
    }

    public ActionListener startAlgo(){
        ActionListener a = e->{
            startButton.setEnabled(false);
            pauseResume.setEnabled(true);
            algorithmManager.startAlgo();
        };
        return a;
    }

    public ItemListener toggleAlgo(){
        ItemListener i = e -> {
            int state = e.getStateChange();

            if(state == ItemEvent.SELECTED){
                algorithmManager.pauseAlgo();
                pauseResume.setText("Resume");
            }
            else{
                algorithmManager.resumeAlgo();
                pauseResume.setText("Pause");
            }
        };
        return i;
    }



    public Component build(){
        return controlPanel;
    }
}
