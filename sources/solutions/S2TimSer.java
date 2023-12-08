package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
/**
* Creating data modelling a sine, a step, or a saw-toot function and performing a time series analysis on them
*/
public class S2TimSer {
   int k, l, n, methodNumber;
   AuxJNumberInput[] ni = new AuxJNumberInput[5];
   String[] methodString = {"Sine function", "Step function", "Saw-tooth function"};
   String[] ac, actionCommands;
   DatanFrame df;
   double p, sigma;
   double[] coneta, eta;
   TimeSeries ts;
// set up data
      double[] y;

   public S2TimSer(){
      String s = "Creating data modelling a sine, a step, or a saw-toot function and performing a time series analysis on them";
      df = new DatanFrame(getClass().getName(), s, getClass().getName() + ".txt");
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n", "number of data points (> 10)", errorLabel);
      ni[0].setProperties("n", true, 10);
      ni[0].setNumberInTextField(400);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("sigma", "error on data points (>0)", errorLabel);
      ni[1].setProperties("sigma", false, 1.E-10);
      ni[1].setNumberInTextField(.1);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("k", "length of avaraging interval is 2 * k + 1 (k > 0)", errorLabel);
      ni[2].setProperties("k", true, 1);
      ni[2].setNumberInTextField(2);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("l", "degree of avaraging polynomial (l < 2 * k + 1)", errorLabel);
      ni[3].setProperties("l", true, 0);
      ni[3].setNumberInTextField(2);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("P", "probability for confidence limit (>0, <1)", errorLabel);
      ni[4].setProperties("P", false, 1.E-10, 1. - 1.E-10);
      ni[4].setNumberInTextField(.9);
      ig.add(ni[4]);
      df.add(ig);
      df.add(errorLabel);
      ac = new String[3];
      ac[0] = methodString[0];
      ac[1] = methodString[1];
      ac[2] = methodString[2];
      RadioListener rl = new RadioListener();
      AuxJRButtonGroup rbg = new AuxJRButtonGroup("Select data model", "", ac, rl);
      df.add(rbg); 
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.repaint();
   }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected void compute(){
      n = (int)ni[0].parseInput();
      sigma = ni[1].parseInput();    
      k = (int)ni[2].parseInput();  
      l = (int)ni[3].parseInput();  
      p = ni[4].parseInput();
// create data
      y = DatanRandom.standardNormal(n);
      double f;
      double fact = Math.PI / 180.;
      for(int i = 0; i < n; i++){
         y[i] = sigma * y[i];
         double t = (double)(i + 1);
         if(methodNumber == 0){
// sine function
            f = Math.sin(fact * t);
         }
         else if (methodNumber == 1){
// step function
            int im = (i + 1) % 200;
            if(im <= 100) f = -1.;
            else f = 1.;
         }
         else{
            int im = (i + 1) % 100;
            f = 0.02 * (double)(im - 50);
// saw/tooth function
         }
         y[i] = y[i] + f;
      }
      ts = new TimeSeries(y, k, l, p);
      eta = ts.getMovingAverages();
      coneta = ts.getConfidenceLimits();
// write out data and results
      df.writeLine("Time series analysis for N = " + n + ", k = " + k + ", l = " + l + ", P = " + String.format(Locale.US, "%6.3f", p));
      df.writeLine("    i          y        eta     coneta");
      for(int i = -k; i < n + k; i++){
         if(i < 0 || i > n-1){
            df.writeLine(String.format(Locale.US, "%5d", i) + "            " +
                         String.format(Locale.US, "%10.5f %10.5f" , eta[i + k], coneta[i + k]));
         }
         else{
            df.writeLine(String.format(Locale.US, "%5d %10.5f %10.5f %10.5f " , i, y[i], eta[i + k], coneta[i + k]));
         }
      }
      df.writeLine("--------------------------------------------------------------------------");
      produceGraphics();
    }

    protected void produceGraphics(){
      double ya, yb;
      String caption = "N = " + n + ", k = " + k + ", l = " + l + ", P = " + String.format(Locale.US, "%6.3f", p);
      DatanGraphics.openWorkstation(getClass().getName(), getClass().getName() + ".ps");
      DatanGraphics.setFormat(0., 0.);
      ya = -1. - 5. * sigma;
      yb = 1. + 5. * sigma;
      DatanGraphics.setWindowInComputingCoordinates(-10., (double)n + 10., ya, yb);
      DatanGraphics.setViewportInWorldCoordinates(0., 1., .2, .8);
      DatanGraphics.setWindowInWorldCoordinates(-.314, 1.1, 0., 1.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.drawScaleX("t");
      DatanGraphics.drawScaleY("y");
      DatanGraphics.drawBoundary();
// draw data points
      DatanGraphics.chooseColor(1);
      for(int i = 0; i < n; i++){
         DatanGraphics.drawMark(1, .1, (double)(i + 1), y[i]);
      }
// draw moving averages
      int nn = n + 2 * k;
      double[] xpl = new double[nn];
      double[] ypl = new double[nn];
      double[] yplp = new double[nn];
      double[] yplm = new double[nn];
      for(int i = 0; i < nn; i++){
         xpl[i] = i - k + 1;
         ypl[i] = eta[i];
         yplp[i] = eta[i] + coneta[i];
         yplm[i] = eta[i] - coneta[i];
      }
      DatanGraphics.chooseColor(5);
      DatanGraphics.drawPolyline(xpl, ypl);
      DatanGraphics.chooseColor(6);
      DatanGraphics.drawPolyline(xpl, yplp);
      DatanGraphics.drawPolyline(xpl, yplm);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawCaption(1., caption);
      DatanGraphics.closeWorkstation();
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
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
      new S2TimSer();
   }

}
