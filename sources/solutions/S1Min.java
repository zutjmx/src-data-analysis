package solutions;


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
* Use of various minimization methods; first approximation found by Monte-Carlo method
*/
public class S1Min extends JFrame {
    public static final long serialVersionUID = 1L;
    String[] ac0, ac1, actionCommands;
    int methodNumber, functionNumber, n;
    JLabel resultLabel;
    JTabbedPane tabbedPane;
    JPanel middlePanel;
    AuxJNumberInput[] ni ;
    AuxJRButtonGroup rbg;
    NumberFormat numForm;
    JTextArea ta;
    JScrollPane scrollText;
    DatanVector xtry, x, xmin;
    double min;
    int[] list;

    public S1Min() {


        
       numForm = NumberFormat.getNumberInstance(Locale.US);
       numForm.setMaximumFractionDigits(8);

       setTitle(getClass().getName());
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       Container contentPane = getContentPane();

       
       ta=new JTextArea(50, 75); 
       JScrollPane scrollText = new JScrollPane(ta);
       contentPane.add(scrollText);

       contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
       middlePanel = new JPanel();
       middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));
        resultLabel = new JLabel();
        AuxJInputGroup ig = new  AuxJInputGroup("Number of trial points (>=1)","first approximation taken from these points");
        ni = new AuxJNumberInput[1];
        ni[0] = new AuxJNumberInput("","", resultLabel);
        ig.addNumberInput(ni[0]);
        ni[0].setProperties("n", true, 1);
        ni[0].setNumberInTextField(10);

 

        methodNumber = 0;
        functionNumber = 0;
        
        ac0 = new String[5];
        ac0[0] = "MinSim";
        ac0[1] = "MinPow";
        ac0[2] = "MinCjg";
        ac0[3] = "MinQdr";
        ac0[4] = "MinMar";
        RadioListener0 rl0 = new RadioListener0();
        AuxJRButtonGroup rbg0 = new AuxJRButtonGroup("Select Minimization Method", "", ac0, rl0);
        
        ac1 = new String[7];
        ac1[0] = "f = r^2 = x1^2 + x2^2 + x3^2";
        ac1[1] = "f = r^10";
        ac1[2] = "f = r";
        ac1[3] = "f = - exp(- r^2)";
        ac1[4] = "f = r^6 - 2*r^4 + r^2";
        ac1[5] = "f = r^2 * exp(- r^2)";
        ac1[6] = "f = - exp(- r^2) - 10 * exp(- ra^2); ra^2 = (x1 - 3)^2 + (x2 - 3)^2 + (x2 - 3)^2";
        RadioListener1 rl1 = new RadioListener1();
        AuxJRButtonGroup rbg1 = new AuxJRButtonGroup("Select Function to be minimized", "", ac1, rl1);
        

        middlePanel.add(rbg0);
        middlePanel.add(rbg1);
        contentPane.add(middlePanel);
        contentPane.add(ig);
        JButton goButton = new JButton("Go");
        GoButtonListener gl = new GoButtonListener();
        goButton.addActionListener(gl);
        contentPane.add(goButton);
        contentPane.add(resultLabel);
//        contentPane.add(resultGroup);
        
        setSize(750, 600);
        setVisible(true);

    }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected Component makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    /** Listens to the radio buttons. */
    
    private class RadioListener0 implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           actionCommands = ac0;
           int l = actionCommands.length;
	        for(int i = 0; i < actionCommands.length; i++) {
		        if(e.getActionCommand() == actionCommands[i]){
                 methodNumber = i;
              }
          }
       }
    }
    
    private class RadioListener1 implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           actionCommands = ac1;
           int l = actionCommands.length;
	        for(int i = 0; i < actionCommands.length; i++) {
		        if(e.getActionCommand() == actionCommands[i]){
                 functionNumber = i;
              }
          }
       }
    }

    /** Listens Go button. */
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

    protected void compute() {
      n = (int)ni[0].parseInput();
// genrate n trial points and find minimum among them
      UserFunction uf = new UserFunction();
      xtry = trialPoint();
      min = uf.getValue(xtry);
      for(int i = 0; i < n; i ++){
         xtry = trialPoint();
         double f = uf.getValue(xtry);
         if(f < min){
            min = f;
            x = xtry;
//            ta.append("x = " + x.toString() + ", min = " + min + "\n");
         }
      }
      ta.append("\n Function " + ac1[functionNumber] + "\n");
      int[] list =  {1, 1, 1};
         if(methodNumber == 0){            
            MinSim msim = new MinSim(x, list, new UserFunction());
            ta.append("MinSim with x = " + x.toString()  + " and list ={" + list[0] + ", " +  list[1] + ", " + list[2] + "} yields \n");
            if(msim.hasConverged()){
               ta.append("nstep = " + msim.getSteps() + "\n");
               ta.append("xmin : " + msim.getMinPosition().toString() + "\n");
               ta.append("min = " + msim.getMinimum() + "\n");
            }
            else{
               ta.append("no convergence\n");
            }
         }
         else if(methodNumber == 1){            
            MinPow mpow = new MinPow(x, list, 0, 0., new UserFunction());
            ta.append("MinPow with x = " + x.toString()  + " and list ={" + list[0] + ", " +  list[1] + ", " + list[2] + "} yields \n");
            if(mpow.hasConverged()){
               ta.append("nstep = " + mpow.getSteps() + "\n");
               ta.append("xmin : " + mpow.getMinPosition().toString() + "\n");
               ta.append("min = " + mpow.getMinimum() + "\n");
            }
            else{
               ta.append("no convergence\n");
            }
         }
         else if(methodNumber == 2){            
            MinCjg mcjg = new MinCjg(x, list, 0, 0., new UserFunction());
            ta.append("MinCjg with x = " + x.toString()  + " and list ={" + list[0] + ", " +  list[1] + ", " + list[2] + "} yields \n");
            if(mcjg.hasConverged()){
            ta.append("nstep = " + mcjg.getSteps() + "\n");
            ta.append("xmin : " + mcjg.getMinPosition().toString() + "\n");
            ta.append("min = " + mcjg.getMinimum() + "\n");
            }
            else{
               ta.append("no convergence\n");
            }
         }
         else if(methodNumber == 3){            
            MinQdr mqdr = new MinQdr(x, list, 0, 0., new UserFunction());
            ta.append("MinQdr with x = " + x.toString()  + " and list ={" + list[0] + ", " +  list[1] + ", " + list[2] + "} yields \n");
            if(mqdr.hasConverged()){
               ta.append("nstep = " + mqdr.getSteps() + "\n");
               ta.append("xmin : " + mqdr.getMinPosition().toString() + "\n");
               ta.append("min = " + mqdr.getMinimum() + "\n");
            }
            else{
               ta.append("no convergence\n");
            }
         }
         else if(methodNumber == 4){            
            MinMar mmar = new MinMar(x, list, 0, 0., new UserFunction());
            ta.append("MinMar with x = " + x.toString()  + " and list ={" + list[0] + ", " +  list[1] + ", " + list[2] + "} yields \n");
            if(mmar.hasConverged()){
               ta.append("nstep = " + mmar.getSteps() + "\n");
               ta.append("xmin : " + mmar.getMinPosition().toString() + "\n");
               ta.append("min = " + mmar.getMinimum() + "\n");
            }
            else{
               ta.append("no convergence\n");
            }
         }
         ta.append("----------------------------------------------------------------------------------\n");
    }

    DatanVector trialPoint(){
      xtry = new DatanVector(3);
      double[] rand = DatanRandom.ecuy(3);
      for(int i = 0; i < 3; i++){
         xtry.setElement(i, -10. + 20. * rand[i]);         
      }
      return xtry;
    }

    private class UserFunction extends DatanUserFunction{
      double result, r2;
      public double getValue(DatanVector d){
         r2 = d.dot(d);
         if(functionNumber == 0){
            result = r2;
         }
         else if(functionNumber == 1){
            result = r2 * r2 * r2 * r2 * r2;
         }
         else if(functionNumber == 2){
            result = Math.sqrt(r2);
         } 
         else if(functionNumber == 3){
            result = - Math.exp(- r2);
         } 
         else if(functionNumber == 4){
            result = r2 * r2 * r2 - 2. * r2 * r2 + r2;
         } 
         else if(functionNumber == 5){
            result = r2 * Math.exp(- r2);
         } 
         else if(functionNumber == 6){
            double ra2 = (d.getElement(0) - 3.) * (d.getElement(0) - 3.) + (d.getElement(1) - 3.) * (d.getElement(1) - 3.)
               + (d.getElement(2) - 3.) * (d.getElement(2) - 3.);
            result = - Math.exp(- r2) - 10. * Math.exp(- ra2);
         }
         return result;
      }
    }



    public static void main(String[] args) {
        S1Min frame = new S1Min();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

    }

    
}
