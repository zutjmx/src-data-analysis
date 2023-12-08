package examples;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;              //for layout managers
import java.awt.event.*;        //for action and window events

import java.net.URL;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.lang.*;

import datan.*;

   
/**
* Class demonstrating the computation of various functions of mathematical statistics, of the gamma function and related functions
*/
public class FunctionsDemo extends JFrame {
    public static final long serialVersionUID = 1L;
    String[] actionCommands;;
    Vector<String[]> vac;
    Vector<AuxJRButtonGroup> vrbg;
    int groupNumber, functionNumber;
    JLabel resultLabel;
    JTabbedPane tabbedPane;
    AuxJNumberInput[] ni ;
    AuxJRButtonGroup rbg;
    double res;
    NumberFormat numForm;
    public FunctionsDemo() {

        
       numForm = NumberFormat.getNumberInstance(Locale.US);
	    numForm.setMaximumFractionDigits(8);

       setTitle("FunctionsDemo");
       Container contentPane = getContentPane();
       JLabel contentPaneLabel = new JLabel("Select Family of Functions");
	    JPanel contentPaneLabelCarrier = new JPanel();
	    contentPaneLabelCarrier.add(contentPaneLabel);


	     contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	     contentPane.add(contentPaneLabelCarrier);

        vac = new Vector<String[]>();
        vrbg = new Vector<AuxJRButtonGroup>();

        tabbedPane = new JTabbedPane();
        TabbedPaneListener tl = new TabbedPaneListener();
        tabbedPane.addChangeListener(tl);

        RadioListener rl = new RadioListener();
        
        AuxJInputGroup ig = new  AuxJInputGroup("Arguments","Arguments of selected function");
        ni = new AuxJNumberInput[4];

        resultLabel = new JLabel("             ");
	     JPanel resultLabelCarrier = new JPanel();
        resultLabelCarrier.setLayout(new BoxLayout( resultLabelCarrier, BoxLayout.X_AXIS));
	     resultLabelCarrier.add(new JLabel("           "));
	     resultLabelCarrier.add(Box.createHorizontalGlue());
	     resultLabelCarrier.add(resultLabel);
	     resultLabelCarrier.add(Box.createHorizontalGlue());
	     resultLabelCarrier.add(new JLabel("           "));
        AuxJInputGroup resultGroup = new  AuxJInputGroup("Result (in blue) or Error Message (in red)","");
        resultGroup.add(resultLabelCarrier);

        for (int i = 0; i < 4; i++){
          ni[i] = new AuxJNumberInput("","", resultLabel);
          ig.addNumberInput(ni[i]);
        }

        groupNumber = 0;
        functionNumber = 0;
        
        String[] ac0 = new String[2];
        ac0[0] = "Binomial Probability";
        ac0[1] = "Cumulative Binomial";
        vac.addElement(ac0);
        AuxJRButtonGroup rbg0 = new AuxJRButtonGroup("Select Function", "", ac0, rl);
        vrbg.addElement(rbg0);
        tabbedPane.addTab("Binomial", rbg0);
        //tabbedPane.setSelectedIndex(0);
        
        String[] ac1 = new String[2];
        ac1[0] = "Hypergeometric Probability";
        ac1[1] = "Cumulative Hypergeometric";
        vac.addElement(ac1);
        AuxJRButtonGroup rbg1 = new AuxJRButtonGroup("Select Function", "", ac1, rl);
        vrbg.addElement(rbg1);
        tabbedPane.addTab("Hypergeometric", rbg1);

        String[] ac2 = new String[3];
        ac2[0] = "Poisson Probability";
        ac2[1] = "Cumulative Poison Probability";
        ac2[2] = "Quantile of Poisson Mean";
        vac.addElement(ac2);
        AuxJRButtonGroup rbg2 = new AuxJRButtonGroup("Select Function", "", ac2, rl);
        vrbg.addElement(rbg2);
        tabbedPane.addTab("Poisson", rbg2);

        String[] ac3 = new String[3];
        ac3[0] = "Probability Density of Standard Normal";
        ac3[1] = "Cumulative Probability of Standard Normal";
        ac3[2] = "Quantile of Standard Normal";
        vac.addElement(ac3);
        AuxJRButtonGroup rbg3 = new AuxJRButtonGroup("Select Function", "", ac3, rl);
        vrbg.addElement(rbg3);
        tabbedPane.addTab("Standard Normal", rbg3);

        String[] ac4 = new String[3];
        ac4[0] = "Normal Probability Density";
        ac4[1] = "Cumulative Normal";
        ac4[2] = "Quantile of Normal Distribution";
        vac.addElement(ac4);
        AuxJRButtonGroup rbg4 = new AuxJRButtonGroup("Select Function", "", ac4, rl);
        vrbg.addElement(rbg4);
        tabbedPane.addTab("Normal", rbg4);

        String[] ac5 = new String[3];
        ac5[0] = "Chi Squared Probability Density";
        ac5[1] = "Cumulative Chi Squared";
        ac5[2] = "Quantile of Chi Squared Distribution";
        vac.addElement(ac5);
        AuxJRButtonGroup rbg5 = new AuxJRButtonGroup("Select Function", "", ac5, rl);
        vrbg.addElement(rbg5);
        tabbedPane.addTab("Chi Squared", rbg5);

        String[] ac6 = new String[3];
        ac6[0] = "F Probability Density";
        ac6[1] = "Cumulative F Distribution";
        ac6[2] = "Quantile of F Distribution";
        vac.addElement(ac6);
        AuxJRButtonGroup rbg6 = new AuxJRButtonGroup("Select Function", "", ac6, rl);
        vrbg.addElement(rbg6);
        tabbedPane.addTab("Fisher's F", rbg6);

        String[] ac7 = new String[3];
        ac7[0] = "Student Probability Density";
        ac7[1] = "Cumulative Student Distribution";
        ac7[2] = "Quantile of Student Distribution";
        vac.addElement(ac7);
        AuxJRButtonGroup rbg7 = new AuxJRButtonGroup("Select Function", "", ac7, rl);
        vrbg.addElement(rbg7);
        tabbedPane.addTab("Student's t", rbg7);

        String[] ac8 = new String[4];
        ac8[0] = "Gamma Function";
        ac8[1] = "Log of Gamma Function";
        ac8[2] = "Binomial Coeffient";
        ac8[3] = "Incomplete Gamma Function";
        vac.addElement(ac8);
        AuxJRButtonGroup rbg8 = new AuxJRButtonGroup("Select Function", "", ac8, rl);
        vrbg.addElement(rbg8);
        tabbedPane.addTab("Gamma", rbg8);

        String[] ac9 = new String[2];
        ac9[0] = "Beta Function";
        ac9[1] = "Incomplete Beta Function";
        vac.addElement(ac9);
        AuxJRButtonGroup rbg9 = new AuxJRButtonGroup("Select Function", "", ac9, rl);
        vrbg.addElement(rbg9);
        tabbedPane.addTab("Beta", rbg9);


        


        
        contentPane.add(tabbedPane);

        contentPane.add(ig);

        
        defineInput();

        contentPane.add(ig);
        JButton goButton = new JButton("Go");
        GoButtonListener gl = new GoButtonListener();
        goButton.addActionListener(gl);
        contentPane.add(goButton);
	     //contentPane.add(resultLabelCarrier);
        contentPane.add(resultGroup);
        
        setSize(850, 600);
        setVisible(true);

    }

    protected void defineInput() {
       if(groupNumber == 0){
          // Binomial
          ni[0].setProperties("k", true, 0.);
          ni[1].setProperties("n", true, 0.);
          ni[2].setProperties("p", false, 0., 1.);
          ni[3].setEnabled(false);
       }
       else if(groupNumber == 1){
          // Hypergeometric
          ni[0].setProperties("k", true, 0.);
          ni[1].setProperties("n", true, 0.);
          ni[2].setProperties("K", true, 0.);
          ni[3].setProperties("N", true, 0.);
       }
       else if(groupNumber == 2){
          // Poisson
          if(functionNumber == 0 || functionNumber == 1){
             ni[0].setProperties("k", true, 0.);
             ni[1].setProperties("lambda", false, 0.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 2){
             ni[0].setProperties("k", true, 0.);
             ni[1].setProperties("P", false, 0., 1.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
       }
       else if(groupNumber == 3){
          // standard normal
          if(functionNumber == 0 || functionNumber == 1){
             ni[0].setProperties("x", false);
             ni[1].setEnabled(false);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 2){
             ni[0].setProperties("P", false, 0., 1.);
             ni[1].setEnabled(false);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
       }
       else if(groupNumber == 4){
          // normal
          if(functionNumber == 0 || functionNumber == 1){
             ni[0].setProperties("x", false);
             ni[1].setProperties("x_0", false);
             ni[2].setProperties("sigma", false, 0.);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 2){
             ni[0].setProperties("P", false, 0., 1.);
             ni[1].setProperties("x_0", false);
             ni[2].setProperties("sigma", false, 0.);
             ni[3].setEnabled(false);
          }
       }
       else if(groupNumber == 5){
          // normal
          if(functionNumber == 0 || functionNumber == 1){
             ni[0].setProperties("x", false);
             ni[1].setProperties("n", true, 1.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 2){
             ni[0].setProperties("P", false, 0., 1.);
             ni[1].setProperties("n", true, 1.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
       }
       else if(groupNumber == 6){
          // Fisher's F
          if(functionNumber == 0 || functionNumber == 1){
             ni[0].setProperties("x", false);
             ni[1].setProperties("f1", true, 1.);
             ni[2].setProperties("f2", true, 1.);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 2){
             ni[0].setProperties("P", false, 0., 1.);
             ni[1].setProperties("f1", true, 1.);
             ni[2].setProperties("f2", true, 1.);
             ni[3].setEnabled(false);
          }
       }
       else if(groupNumber == 7){
          // Student's t
          if(functionNumber == 0 || functionNumber == 1){
             ni[0].setProperties("x", false);
             ni[1].setProperties("f", true, 1.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 2){
             ni[0].setProperties("P", false, 0., 1.);
             ni[1].setProperties("f", true, 1.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
       }
       else if(groupNumber == 8){
          // Gamma
          if(functionNumber == 0 || functionNumber == 1){
             ni[0].setProperties("x", false);
             ni[1].setEnabled(false);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 2){
             ni[0].setProperties("n", true, 0.);
             ni[1].setProperties("k", true, 0.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 3){
             ni[0].setProperties("a", false, 0.);
             ni[1].setProperties("x", false, 0.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
       }
       else if(groupNumber == 9){
          // Beta
          if(functionNumber == 0 ){
             ni[0].setProperties("z", false, 0.);
             ni[1].setProperties("w", false, 0.);
             ni[2].setEnabled(false);
             ni[3].setEnabled(false);
          }
          else if(functionNumber == 1){
             ni[0].setProperties("x", false, 0., 1.);
             ni[1].setProperties("a", false, 0.);
             ni[2].setProperties("b", false, 0.);
             ni[3].setEnabled(false);
          }
       }
    }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < 4; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected void compute() {
       if(groupNumber == 0){
          // Binomial
          int k = (int)ni[0].parseInput();
          int n = (int)ni[1].parseInput();
          double p = ni[2].parseInput();
          if(functionNumber == 0) res = StatFunct.binomial(k, n, p);
          else res = StatFunct.cumulativeBinomial(k, n, p);
       }
       else if (groupNumber == 1){
          // Hypergeometric
          int k = (int)ni[0].parseInput();
          int n = (int)ni[1].parseInput();
          int kk = (int)ni[2].parseInput();
          int nn = (int)ni[3].parseInput();
          if(functionNumber == 0) res = StatFunct.hypergeometric(k, n, kk, nn);
          else res = StatFunct.cumulativeHypergeometric(k, n, kk, nn);          
       }
       else if (groupNumber == 2){
          // Poisson
          int k = (int)ni[0].parseInput();
          double d = ni[1].parseInput();
          if(functionNumber == 0) res = StatFunct.poisson(k, d);
          else if(functionNumber == 1) res = StatFunct.cumulativePoisson(k, d);
          else if(functionNumber == 2) res = StatFunct.quantilePoisson(k, d);
       }
       else if (groupNumber == 3){
          // standard normal
          double x = ni[0].parseInput();
          if(functionNumber == 0) res = StatFunct.standardNormal(x);
          else if(functionNumber == 1) res = StatFunct.cumulativeStandardNormal(x);
          else if(functionNumber == 2) res = StatFunct.quantileStandardNormal(x);
       }
       else if (groupNumber == 4){
          // normal
          double x = ni[0].parseInput();
          double x0 = ni[1].parseInput();
          double sigma = ni[2].parseInput();
          if(functionNumber == 0) res = StatFunct.normal(x, x0, sigma);
          else if(functionNumber == 1) res = StatFunct.cumulativeNormal(x, x0, sigma);
          else if(functionNumber == 2) res = StatFunct.quantileNormal(x, x0, sigma);
       }
       else if (groupNumber == 5){
          // chi squared
          double x = ni[0].parseInput();
          int n = (int)ni[1].parseInput();
          if(functionNumber == 0) res = StatFunct.chiSquared(x, n);
          else if(functionNumber == 1) res = StatFunct.cumulativeChiSquared(x, n);
          else if(functionNumber == 2) res = StatFunct.quantileChiSquared(x, n);
       }
       else if (groupNumber == 6){
          // Fisher's F
          double x = ni[0].parseInput();
          int f1 = (int)ni[1].parseInput();
          int f2 = (int)ni[2].parseInput();
          if(functionNumber == 0) res = StatFunct.fDistribution(x, f1, f2);
          else if(functionNumber == 1) res = StatFunct.cumulativeFDistribution(x, f1, f2);
          else if(functionNumber == 2) res = StatFunct.quantileFDistribution(x, f1, f2);
       }
       else if (groupNumber == 7){
          // Student's t
          double x = ni[0].parseInput();
          int f = (int)ni[1].parseInput();
          if(functionNumber == 0) res = StatFunct.student(x, f);
          else if(functionNumber == 1) res = StatFunct.cumulativeStudent(x, f);
          else if(functionNumber == 2) res = StatFunct.quantileStudent(x, f);
       }
       else if (groupNumber == 8){
          // Gamma
          if(functionNumber == 0){
             double x = ni[0].parseInput();
             res =  Gamma.gamma(x);
          }
          else if(functionNumber == 1){
             double x = ni[0].parseInput();
             res =  Gamma.logGamma(x);
          }
          else if(functionNumber == 2){
             int n = (int)ni[0].parseInput();
             int k = (int)ni[1].parseInput();
             res =  Gamma.binomial(n, k);
          }
          else if(functionNumber == 3){
             double a = ni[0].parseInput();
             double x = ni[1].parseInput();
             res =  Gamma.incompleteGamma(a, x);
          }
       }
       else if (groupNumber == 9){
          // Beta
          if(functionNumber == 0){
             double z = ni[0].parseInput();
             double w = ni[1].parseInput();
             res =  Gamma.beta(z, w);
          }
          else if(functionNumber == 1){
             double x = ni[0].parseInput();
             double a = ni[1].parseInput();
             double b = ni[2].parseInput();
             res =  Gamma.incompleteBeta(a, b, x);
          }
       }
       resultLabel.setForeground(Color.blue); 
       resultLabel.setText(numForm.format(res));
           
    }

    protected Component makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    /** Listens to tabbed Pane. */
    
    private class TabbedPaneListener implements ChangeListener {
	 	public void stateChanged(ChangeEvent e){
         groupNumber = tabbedPane.getSelectedIndex();
         rbg = vrbg.elementAt(groupNumber);
         functionNumber = rbg.getSelectedIndex();
         defineInput();
		}
    }

    /** Listens to the radio buttons. */
    
    private class RadioListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           groupNumber = tabbedPane.getSelectedIndex();
           actionCommands = vac.elementAt(groupNumber);
           int l = actionCommands.length;
	        for(int i = 0; i < actionCommands.length; i++) {
		        if(e.getActionCommand() == actionCommands[i]){
                 functionNumber = i;
              }
          }
          rbg = vrbg.elementAt(groupNumber);
          rbg.setSelectedIndex(functionNumber);
          defineInput();
       }
    }

    /** Listens Go button. */
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }



    public static void main(String[] args) {
        FunctionsDemo frame = new FunctionsDemo();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

    }

    
}
