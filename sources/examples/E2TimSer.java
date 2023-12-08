package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
/**
* Example demonstrating atime series analysis
*/
public class E2TimSer {
   int k, l, n;
   AuxJNumberInput[] ni = new AuxJNumberInput[3];
   DatanFrame df;
   double p;
   double[] coneta, eta;
   TimeSeries ts;
// set up data
      double[] y ={
      38.7,50.3,45.6,46.4,43.7,42.0,
      21.8,21.8,51.3,39.5,26.9,23.2,
      19.8,24.4,17.1,29.3,43.0,35.9,
      19.6,33.2,38.8,35.3,23.4,14.9,
      15.3,17.7,16.5, 6.6, 9.5, 9.1,
       3.1, 9.3, 4.7, 6.1, 7.4,15.1      
      };

   public E2TimSer(){
      String s = "Example demonstrating a time series analysis and producing graphical output";
      df = new DatanFrame(getClass().getName(), s, getClass().getName() + ".txt");
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("k", "length of avaraging interval is 2 * k + 1 (k > 0)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("k", true, 1);
      ni[0].setNumberInTextField(2);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("l", "degree of avaraging polynomial (l < 2 * k + 1)", errorLabel);
      ni[1].setProperties("l", true, 1);
      ni[1].setNumberInTextField(2);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("P", "probability for confidence limit (>0, <1)", errorLabel);
      ni[2].setProperties("P", false, 0., 1.);
      ni[2].setNumberInTextField(.9);
      ig.add(ni[2]);
      df.add(ig);
      df.add(errorLabel);
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
      k = (int)ni[0].parseInput();  
      l = (int)ni[1].parseInput();  
      p = ni[2].parseInput();
      n = y.length;
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
      String caption = "N = " + n + ", k = " + k + ", l = " + l + ", P = " + String.format(Locale.US, "%6.3f", p);
      DatanGraphics.openWorkstation(getClass().getName(), getClass().getName() + ".ps");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(-10., 50., -20., 70.);
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

   public static void main(String s[]) {
      new E2TimSer();
   }

}
