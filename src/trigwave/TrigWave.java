package trigwave;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-05
 * @version 1.0
 */
public class TrigWave implements Runnable, ActionListener, ChangeListener {

    private final JFrame graphicsFrame;
    private final JFrame optionsFrame;
    private final Thread thread;
    
    private final JComboBox curveType;
    private final String[] curveTypes = {"Sine Curve", "Cosine Curve", "Tangent Curve"};
    private final JButton startButton;
    private final SpinnerNumberModel amplitudeSpinnerModel;
    private final JSpinner amplitudeSpinner;
    private final JLabel amplitudeLabel;
    private final SpinnerNumberModel frequencySpinnerModel;
    private final JSpinner frequencySpinner;
    private final JLabel frequencyLabel;
    private final SpinnerNumberModel dotSpaceSpinnerModel;
    private final JSpinner dotSpaceSpinner;
    private final JLabel dotSpaceLabel;
    private final SpinnerNumberModel speedSpinnerModel;
    private final JSpinner speedSpinner;
    private final JLabel speedLabel;
            
    private boolean isPaused = true;
    
    final DecimalFormat decimal = new DecimalFormat("#.##");
    private final Font font;
    
    private String trigFunction = "SIN";
    private int amplitude = 20;
    private double frequency = 90;
    private long displacement = 0;
    private int speed = 5;
    private int spacing = 2;
    
    public static void main(final String[] args) {
        final TrigWave tw = new TrigWave();
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public TrigWave() {
        graphicsFrame = new JFrame("Wave");
        optionsFrame = new JFrame("Options");
        
        graphicsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        optionsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        graphicsFrame.setSize(1000, 500);
        optionsFrame.setSize(200, 240);
        
        graphicsFrame.setLocationRelativeTo(null);
        optionsFrame.setLocationRelativeTo(null);
        
        graphicsFrame.setResizable(false);
        optionsFrame.setResizable(false);
        
        final Container container = optionsFrame.getContentPane();
        final SpringLayout layout = new SpringLayout();
        {
            container.setLayout(layout);
            container.setBackground(new Color(83, 83, 83));

            curveType = new JComboBox(curveTypes);
            startButton = new JButton("Start");
            amplitudeSpinnerModel = new SpinnerNumberModel(20, 1, 28, 1);
            amplitudeSpinner = new JSpinner(amplitudeSpinnerModel);
            amplitudeLabel = new JLabel("Amplitude");
            frequencySpinnerModel = new SpinnerNumberModel(90, 1, 720, 1);
            frequencySpinner = new JSpinner(frequencySpinnerModel);
            frequencyLabel = new JLabel("Frequency");
            speedSpinnerModel = new SpinnerNumberModel(5, 1, 500, 1);
            speedSpinner = new JSpinner(speedSpinnerModel);
            speedLabel = new JLabel("Speed");
            dotSpaceSpinnerModel = new SpinnerNumberModel(2, 1, 50, 1);
            dotSpaceSpinner = new JSpinner(dotSpaceSpinnerModel);
            dotSpaceLabel = new JLabel("Dot Spacing");

            curveType.setPreferredSize(new Dimension(180, 30));
            startButton.setPreferredSize(new Dimension(180, 30));
            amplitudeSpinner.setPreferredSize(new Dimension(40, 25));
            frequencySpinner.setPreferredSize(new Dimension(40, 25));
            speedSpinner.setPreferredSize(new Dimension(40, 25));
            dotSpaceSpinner.setPreferredSize(new Dimension(40, 25));
            
            startButton.setFocusable(false);
            amplitudeLabel.setForeground(Color.WHITE);
            frequencyLabel.setForeground(Color.WHITE);
            speedLabel.setForeground(Color.WHITE);
            dotSpaceLabel.setForeground(Color.WHITE);
            
            curveType.addActionListener(this);
            startButton.addActionListener(this);
            amplitudeSpinner.addChangeListener(this);
            frequencySpinner.addChangeListener(this);
            speedSpinner.addChangeListener(this);
            dotSpaceSpinner.addChangeListener(this);

            container.add(curveType);
            container.add(startButton);
            container.add(amplitudeSpinner);
            container.add(amplitudeLabel);
            container.add(frequencySpinner);
            container.add(frequencyLabel);
            container.add(speedSpinner);
            container.add(speedLabel);
            container.add(dotSpaceSpinner);
            container.add(dotSpaceLabel);

            layout.putConstraint(SpringLayout.NORTH, startButton, 5, SpringLayout.NORTH, container);
            layout.putConstraint(SpringLayout.WEST, startButton, 7, SpringLayout.WEST, container);
            layout.putConstraint(SpringLayout.NORTH, curveType, 35, SpringLayout.NORTH, startButton);
            layout.putConstraint(SpringLayout.WEST, curveType, 7, SpringLayout.WEST, container);
            layout.putConstraint(SpringLayout.NORTH, amplitudeSpinner, 35, SpringLayout.NORTH, curveType);
            layout.putConstraint(SpringLayout.EAST, amplitudeSpinner, -7, SpringLayout.EAST, container);
            layout.putConstraint(SpringLayout.NORTH, amplitudeLabel, 4, SpringLayout.NORTH, amplitudeSpinner);
            layout.putConstraint(SpringLayout.EAST, amplitudeLabel, -55, SpringLayout.EAST, container);
            layout.putConstraint(SpringLayout.NORTH, frequencySpinner, 35, SpringLayout.NORTH, amplitudeSpinner);
            layout.putConstraint(SpringLayout.EAST, frequencySpinner, -7, SpringLayout.EAST, container);
            layout.putConstraint(SpringLayout.NORTH, frequencyLabel, 4, SpringLayout.NORTH, frequencySpinner);
            layout.putConstraint(SpringLayout.EAST, frequencyLabel, -55, SpringLayout.EAST, container);
            layout.putConstraint(SpringLayout.NORTH, speedSpinner, 35, SpringLayout.NORTH, frequencySpinner);
            layout.putConstraint(SpringLayout.EAST, speedSpinner, -7, SpringLayout.EAST, container);
            layout.putConstraint(SpringLayout.NORTH, speedLabel, 4, SpringLayout.NORTH, speedSpinner);
            layout.putConstraint(SpringLayout.EAST, speedLabel, -55, SpringLayout.EAST, container);
            layout.putConstraint(SpringLayout.NORTH, dotSpaceSpinner, 35, SpringLayout.NORTH, speedSpinner);
            layout.putConstraint(SpringLayout.EAST, dotSpaceSpinner, -7, SpringLayout.EAST, container);
            layout.putConstraint(SpringLayout.NORTH, dotSpaceLabel, 4, SpringLayout.NORTH, dotSpaceSpinner);
            layout.putConstraint(SpringLayout.EAST, dotSpaceLabel, -55, SpringLayout.EAST, container);
        }
        graphicsFrame.setVisible(true);
        optionsFrame.setVisible(true);
        
        graphicsFrame.createBufferStrategy(2);
        
        font = new Font("Arial", Font.PLAIN, 24);
        
        thread = new Thread(this);
        thread.start();
    }
    
    public void close() {
        graphicsFrame.setVisible(false);
        optionsFrame.setVisible(false);
        
        thread.interrupt();
        System.exit(0);
    }
    
    private void update() {
        displacement += speed;
    }
    
    private void drawGraphics() {
        final BufferStrategy bf = graphicsFrame.getBufferStrategy();
        Graphics2D g = null;
        
        try {
            g = (Graphics2D) bf.getDrawGraphics();
            g.setFont(font);
            
            final FontMetrics fontMetrics = g.getFontMetrics();
            
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHints(rh);
            
            g.setColor(new Color(38, 38, 38));
            g.fillRect(0, 0, 1000, 500);
            
            g.setColor(new Color(83, 83, 83));
            g.fillRect(0, 0, 300, 500);
            
            final String by = "Richard Dahlgren";
            g.setColor(new Color(53, 53, 53));
            g.drawString(by, 990 - fontMetrics.stringWidth(by), 480);
            
            g.setColor(Color.WHITE);
            g.fillOval(150 - (amplitude * 5), (500 / 2) - (amplitude * 5), amplitude * 10, amplitude * 10);
            
            g.setColor(new Color(83, 83, 83));
            g.drawLine(0, 500 / 2, 1000, 500 / 2);
            g.drawLine(300, (500 / 2) - (amplitude * 5), 1000, (500 / 2) - (amplitude * 5));
            g.drawLine(300, (500 / 2) + (amplitude * 5), 1000, (500 / 2) + (amplitude * 5));
            g.drawLine(300 / 2, 0, 300 / 2, 500);
            
            g.setColor(Color.WHITE);
            
            double xUnitCircleTrigFunctValue = 0;
            int yUnitCircle = 0;
            
            for (int x = 300; x < 1000; x += spacing) {
                
                final double trigFunctValue = Math.toRadians((x - 300) * Math.toRadians(frequency) + displacement);
                int y = 0;
                
                switch (trigFunction) {
                    case "SIN":
                    {
                        y = (int) ((500 / 2) - ((amplitude * 5) * Math.sin(trigFunctValue)));
                        break;
                    }   
                    case "COS":
                    {
                        y = (int) ((500 / 2) - ((amplitude * 5) * Math.cos(trigFunctValue)));
                        break;
                    }   
                    case "TAN":
                    {
                        y = (int) ((500 / 2) - ((amplitude * 5) * Math.tan(trigFunctValue)));
                        break;
                    }
                }
                
                g.drawLine(x, y, x, y);
            
                if (x == 300) {
                    xUnitCircleTrigFunctValue = trigFunctValue;
                    yUnitCircle = y;
                }
            }
            
            g.setColor(Color.RED);
            switch (trigFunction) {
                case "SIN":
                {
                    final int xUnitCircle = (int) ((amplitude * 5) * Math.cos(xUnitCircleTrigFunctValue)) + (300 / 2);
                    g.drawLine(xUnitCircle, yUnitCircle, 300, yUnitCircle);
                    g.drawLine((300 / 2), 500 / 2, xUnitCircle, yUnitCircle);
                    break;
                }
                case "COS":
                {
                    final int xUnitCircle = (int) ((300 / 2) - ((amplitude * 5) * Math.sin(xUnitCircleTrigFunctValue)));
                    g.drawLine(xUnitCircle, yUnitCircle, 300, yUnitCircle);
                    g.drawLine((300 / 2), 500 / 2, xUnitCircle, yUnitCircle);
                    break;
                }
                case "TAN":
                {
                    final int xUnitCircle = (int) (int) ((amplitude * 5) * Math.cos(xUnitCircleTrigFunctValue)) + (300 / 2);
                    final int yUnitCircleTan = (int) ((500 / 2) - ((amplitude * 5) * Math.sin(xUnitCircleTrigFunctValue)));
                    g.drawLine(xUnitCircle, yUnitCircleTan, 300, yUnitCircle);
                    g.drawLine((300 / 2), 500 / 2, xUnitCircle, yUnitCircleTan);
                    break;
                }
            }
            
            final String equation = amplitude + trigFunction + "(" + decimal.format(Math.toRadians(frequency)) + "x + v)";
            g.setColor(Color.WHITE);
            g.drawString(equation, 990 - fontMetrics.stringWidth(equation), 50);
            
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
        
        bf.show();
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void run() {
        
        while (!thread.isInterrupted()) {
            
            if (!isPaused) {
                update();
            }
            drawGraphics();
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(TrigWave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        final Object obj = e.getSource();
        if (obj instanceof JComboBox) {
            
            final JComboBox selected = (JComboBox) obj;
            final String curve = (String) selected.getSelectedItem();
            
            switch (curve) {
                case "Sine Curve":
                {
                    trigFunction = "SIN";
                    break;
                }
                case "Cosine Curve":
                {
                    trigFunction = "COS";
                    break;
                }
                case "Tangent Curve":
                {
                    trigFunction = "TAN";
                    break;
                }
            }
            displacement = 0;
            return;
        }
        
        if (obj instanceof JButton) {
            
            final JButton selected = (JButton) obj;
            
            if (selected.getText().equals("Start")) {
                selected.setText("Pause");
                isPaused = false;
            } else {
                selected.setText("Start");
                isPaused = true;
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        
        final Object obj = e.getSource();
        
        if (obj == amplitudeSpinner) {
            amplitude = (int) amplitudeSpinner.getValue();
            
        } else if (obj == frequencySpinner) {
            frequency = (int) frequencySpinner.getValue();
            
        } else if (obj == speedSpinner) {
            speed = (int) speedSpinner.getValue();
            
        } else if (obj == dotSpaceSpinner) {
            spacing = (int) dotSpaceSpinner.getValue();
        }
    }
}