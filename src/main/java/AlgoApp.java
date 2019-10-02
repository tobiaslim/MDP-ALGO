import algorithm.AlgorithmManager;
import algorithm.models.MDFFormat;
import networkmanager.NetworkManager;
import networkmanager.TestServer;
import simulator.Mode;
import simulator.Simulator;
import utility.MDFFormatterUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.ConnectException;


public class AlgoApp {
    public Simulator simulator;
    public NetworkManager networkManager;
    public AlgorithmManager algorithmManager;


    public Mode mode;

    public static final String SERVERL_URL = "192.168.25.25";
    public static final String SIMULATED_SERVER = "localhost";
    public static final int SERVER_PORT = 2555;
    private int i = 0;

    public static void main(String[] args) {
        AlgoApp app = new AlgoApp();
    }

    public AlgoApp(){
        setMode();
    }


    public void setMode(){
        JDialog initialDialog = new JDialog();
        initialDialog.setSize(400,80);
        initialDialog.setLayout(new FlowLayout());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        initialDialog.setLocation(dim.width / 4 - initialDialog.getSize().width / 2, dim.height / 2 - initialDialog.getSize().height / 2);

        JButton realRunButton = new JButton("Real Run");
        realRunButton.setSelected(true);
        realRunButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                initializeActual();
                initialDialog.setVisible(false);

            }
        });
        JButton simulationButton = new JButton("Simulation");
        simulationButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                initializeSimulation(initialDialog);
                initialDialog.setVisible(false);
            }
        });

        ButtonGroup bg = new ButtonGroup();
        bg.add(realRunButton);
        bg.add(simulationButton);
        initialDialog.add(realRunButton);
        initialDialog.add(simulationButton);
        initialDialog.setVisible(true);
    }


    /**
     * Set up for actual run
     */
    public void initializeActual(){
        mode = Mode.REAL_RUN;
        try{
            networkManager = new NetworkManager(SERVERL_URL, SERVER_PORT);
        }
        catch (IOException e){
            e.printStackTrace();
            return;
        }
        this.algorithmManager = new AlgorithmManager(mode, networkManager);
        this.simulator = new Simulator(mode, algorithmManager);
        this.simulator.display();
    }

    /**
     * set up for simulation
     */
    public void initializeSimulation(JDialog dialog){
        dialog.setVisible(false);
        mode = Mode.SIMULATION;
        MDFFormat simulatedMdf = null;
        while(simulatedMdf == null){
            simulatedMdf = handleLoadMap();
        }
        try {
            startServer(simulatedMdf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            this.networkManager = new NetworkManager(SIMULATED_SERVER, SERVER_PORT);
        }
        catch (IOException e){
            if(e instanceof ConnectException){
                e.printStackTrace();
                return;
            }
        }
        this.algorithmManager = new AlgorithmManager(mode, networkManager);
        this.simulator = new Simulator(mode, algorithmManager);
        this.simulator.display();
        this.simulator.showAnswerFrame(simulatedMdf);
    }


    /**
     * Use this function for local server to test
     * @throws IOException
     */
    public void startServer(MDFFormat simulatedMapMdf) throws IOException {
        TestServer server = new TestServer(simulatedMapMdf);
        server.setName("Simulated Server");
        server.start();
    }

    public MDFFormat handleLoadMap(){
        String cwd = System.getProperty("user.dir");
        JFileChooser jfc = new JFileChooser(cwd);


        int returnValue = jfc.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File selected = jfc.getSelectedFile();
            try {
                FileReader reader = new FileReader(selected);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String explorationString = bufferedReader.readLine();
                String obstacleString = bufferedReader.readLine();
                MDFFormat mdfFormat = new MDFFormat(explorationString, obstacleString);
                return mdfFormat;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}
