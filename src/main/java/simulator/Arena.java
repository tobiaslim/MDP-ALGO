package simulator;

import algorithm.ArenaMemory;
import algorithm.models.RobotModel;

import javax.swing.*;
import java.awt.*;


public class Arena {
    public static final int NO_OF_ROWS = 20;
    public static final int NO_OF_COLUMNS = 15;

    public static final int GRID_V_GAP = 1;
    public static final int GRID_H_GAP = 1;

    JPanel mapPanel;
    JPanel robotPanel;
    JLayeredPane arenaLayers;

    ArenaMemory arenaMemory;
    private Cell[] cells;

    public Arena(ArenaMemory arenaMemory, RobotModel robotModel){
        //bind dependencies
        this.arenaMemory = arenaMemory;

        /// Map Panel
        mapPanel = new JPanel();
        mapPanel.setBorder(BorderFactory.createTitledBorder("Map"));
        GridLayout layout = new GridLayout(NO_OF_ROWS, NO_OF_COLUMNS);
        mapPanel.setLayout(layout);


        this.cells = new Cell[300];

        // create cell view and bind cell model
        for(int i = arenaMemory.getLength()-15; i >= 0; i-=15){
            int index = i;
            for(int j = 0; j < NO_OF_COLUMNS; j++){
                Cell c = new Cell();
                c.setCellModel(arenaMemory.getCellModelByArrayIndex(index+j));
                mapPanel.add(c);
                cells[index+j] = c;
            }
        }

        arenaLayers = new JLayeredPane();
        arenaLayers.setPreferredSize(new Dimension(500,650));
        mapPanel.setBounds(0, 0, 480, 645);
        arenaLayers.add(mapPanel,new Integer(1));


        //Robot
        robotPanel = new JPanel();
        robotPanel.setSize(480, 645);
        robotPanel.setBounds(0, 0, 480, 645);
        RobotView robotView = new RobotView(robotModel, cells);
        robotPanel.add(robotView);
        robotPanel.setOpaque(false);
        arenaLayers.add(robotPanel, new Integer(2));
    }

    ///return the map panel
    Component build(){
        return arenaLayers;
    }

}
