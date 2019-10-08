package simulator;

import algorithm.models.ArenaCellModel;
import algorithm.contracts.ArenaCellSubscriber;

import javax.swing.*;
import java.awt.*;

public class Cell extends JComponent implements ArenaCellSubscriber {
    private ArenaCellModel cellModel;

    public static final int CELL_WIDTH = 30;
    public static final int CELL_HEIGHT = 30;

    private Color color;

    public Cell(){
        this.setPreferredSize(new Dimension(CELL_HEIGHT, CELL_WIDTH));
    }

    public void setCellModel(ArenaCellModel cellModel) {
        this.cellModel = cellModel;
        cellModel.subscribe(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (this.cellModel.getCellType()){
            case BLOCK:
                color = Color.black;
                break;
            case EMPTY:
                color = this.cellModel.isVirtualWall() ? Color.yellow : Color.white;
                if(cellModel.isWaypoint()){
                    color = Color.blue;
                }
                break;
            case START_ZONE:
                color = Color.green;
                break;
            case END_ZONE:
                color = Color.RED;
                break;
            case UNKNOWN:
                default:
                color =  this.cellModel.isVirtualWall() ? Color.yellow : Color.gray;
        }
        g.setColor(color);
        g.fillRect(0, 0, CELL_WIDTH,CELL_HEIGHT);
        g.setColor(Color.blue);
        g.drawString(cellModel.getCellStatus().toString().substring(0, 1), 5, 15);

    }


    @Override
    public void onUpdate() {
        SwingUtilities.invokeLater(() -> repaint());
    }
}

