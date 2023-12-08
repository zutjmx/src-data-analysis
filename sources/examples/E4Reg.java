package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

public class E4Reg {
   int n, methodNumber;
   AuxJNumberInput[] ni = new AuxJNumberInput[1];
   String[] ac, actionCommands;
   String[] methodString = {"Errors known", "Errors unknown"};
   DatanFrame df;
   Regression reg;
   NumberFormat numForm;
      double[] t ={0., 1., 2., 3.};
      double[] y = {1.4, 1.5, 3.7, 4.1};
      double[] deltay ={0.5, 0.2, 1., 0.5};

   public E4Reg(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(12);
  	   numForm.setMinimumFractionDigits(12);
      String s = "Example demonstrating linear regression with known and with unknown errors";
      df = new DatanFrame(getClass().getName(), s);
      AuxJInputGroup ig = new AuxJInputGroup("Enter variables", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("P", "probability for confidence limits(> 0, < 1)", errorLabel);
      ni[0].setProperties("P", false);
      ni[0].setMinimum(0.);
      ni[0].setMaximum(1.);
      ni[0].setNumberInTextField(.95);
      ig.add(ni[0]);
      df.add(ig);
      df.add(errorLabel);
// radio-button group for choice of method
      ac = new String[2];
      ac[0] = methodString[0];
      ac[1] = methodString[1];
      RadioListener rl = new RadioListener();
      AuxJRButtonGroup rbg = new AuxJRButtonGroup("Select", "", ac, rl);
      df.add(rbg); 
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.repaint();
// set up data
      n = t.length;
   }

   protected void producePlot(){
      double prob = ni[0].parseInput();
      int[] colorindex = new int[3];
// prepare data and fitted curve for plotting
      int npl = 1000;
      double[][] xpl = new double[3][npl];
      double[][] ypl = new double[3][npl];
      double[] datx = new double[n];
      double[] daty = new double[n];
      double[] datsx = new double[n];
      double[] datsy = new double[n];
      double[] datrho = new double[n];
      for(int i = 0; i < n; i++){
         datx[i] = t[i];
         daty[i] = y[i];
         datsx[i] = 0.;
         datsy[i] = deltay[i];
         if(methodNumber == 1)datsy[i] = 0.;
         datrho[i] =0.;
      }
      double del = 5. / (double)(npl - 1);
      for(int j = 0; j < 3; j++){
         colorindex[j] = 3;
         if(j ==0) colorindex[j] = 5;
         if(methodNumber == 0) reg = new Regression(t, y, deltay, 2);
         else reg = new Regression(t, y, 2);
         for(int i = 0; i < npl; i++){
            xpl[j][i] = -1. + (double)i * del;
            if(j == 0) ypl[j][i] = reg.regressionLine(xpl[j][i]);
            else if(j == 1) ypl[j][i] = ypl[0][i] + reg.confidenceLimit(xpl[j][i], prob);
            else ypl[j][i] = ypl[0][i] - reg.confidenceLimit(xpl[j][i], prob);
         }
      }
// produce graphics
      String caption = "Linear regression, P = " + prob;
      if(methodNumber == 0) caption += ", errors known";
      else caption += ", errors unknown";
      DatanGraphics.openWorkstation(getClass().getName(), "");
      DatanGraphics.setWindowInComputingCoordinates(-1., 4., 0., 5.);
      DatanGraphics.setViewportInWorldCoordinates(.2, .9, .16, .86);
      DatanGraphics.setWindowInWorldCoordinates(-.414, 1., 0., 1.);
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.drawScaleX("t");
      DatanGraphics.drawScaleY("y");
      DatanGraphics.drawCaption(0., caption);
      DatanGraphics.drawBoundary();
      DatanGraphics.setSmallClippingWindow();
      DatanGraphics.chooseColor(1);
      for(int i = 0; i < n; i++){
         DatanGraphics.drawDatapoint(1, .3, datx[i], daty[i], datsx[i], datsy[i], datrho[i]);
      }
      for(int i = 0; i < 3; i++){
         DatanGraphics.chooseColor(colorindex[i]);
         DatanGraphics.drawPolyline(xpl[i], ypl[i]);
      }
      DatanGraphics.closeWorkstation();
   }

   

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) producePlot();
       }
    }

    /** Listens to the radio buttons. */
    
    private class RadioListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           actionCommands = ac;
           int l = actionCommands.length;
	        for(int i = 0; i < actionCommands.length; i++) {
		        if(e.getActionCommand() == actionCommands[i]){
                 methodNumber = i;
              }
          }
       }
    }

   public static void main(String s[]) {
      new E4Reg();
   }

}
