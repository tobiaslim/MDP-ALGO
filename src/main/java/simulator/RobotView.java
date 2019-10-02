package simulator;

import algorithm.models.ArenaCellCoordinate;
import algorithm.models.RobotModel;
import algorithm.contracts.RobotSubscriber;

import javax.swing.*;
import java.awt.*;

public class RobotView extends JComponent implements RobotSubscriber {

    private RobotModel robotModel;
    private Color color = Color.black;
    private Cell[] cells;

    public RobotView(RobotModel robotModel, Cell[] cells){
        this.robotModel = robotModel;
        this.cells = cells;
        robotModel.subscribe(this);
        this.setSize(480,645);
        this.setBounds(0, 0, 10, 10);
        this.setPreferredSize(new Dimension(480,645));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        ArenaCellCoordinate robotCenter = robotModel.getRobotCenter();
        int index = robotCenter.calculateIndex();
        int x = 15;
        int y = 11;

        //retrieve subscribe cell instance and draw based on cell x and y
        Cell c = cells[index];

        x = x+c.getX();
        y = y+c.getY();
        drawCenteredCircle(g,  x, y, 66);

        g.setColor(Color.yellow);
        switch (robotModel.getCurrentDirection()){
            case NORTH:
                drawCenteredCircle(g, x, y - 20, 22);
                break;
            case EAST:
                drawCenteredCircle(g,x + 20 , y,  22);
                break;
            case SOUTH:
                drawCenteredCircle(g, x , y+20,  22);
                break;
            case WEST:
                drawCenteredCircle(g, x-20, y,22);
                break;
        }
    }

    private void drawCenteredCircle(Graphics g, int x, int y, int r){
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);
    }

    @Override
    public void onMove() {
        this.repaint();
    }
}
