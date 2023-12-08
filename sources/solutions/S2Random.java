package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Creation of random numbers distributed according to a triangular distribution
*/
public class S2Random {
   AuxJNumberInput[] ni = new AuxJNumberInput[4];
   DatanFrame df;
   int n, nx, methodNumber;
   double a, b, c, x0, delx;
   double[] r;
   Histogram hist;
   NumberFormat numForm;
   String line, caption;
   String[] ac, actionCommands;
   String[] methodString = {"Transformation", "Rejection"};

   public S2Random(){
      String s = "Creation of random numbers distributed according to a triangular distribution";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of points (> 1, <= 10000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true, 2, 10000);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("a", "left corner of triangle", errorLabel);
      ni[1].setProperties("a", false);
      ni[1].setNumberInTextField(1.);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("b", "right corner of triangle", errorLabel);
      ni[2].setProperties("b", false);
      ni[2].setNumberInTextField(4.);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("c", "position of triangle top", errorLabel);
      ni[3].setProperties("c", false);
      ni[3].setNumberInTextField(3.);
      ig.add(ni[3]);
      df.add(ig);
      df.add(errorLabel);
// radio-button group for choice of method
      ac = new String[2];
      ac[0] = methodString[0];
      ac[1] = methodString[1];
      RadioListener rl = new RadioListener();
      AuxJRButtonGroup rbg = new AuxJRButtonGroup("Random number Generator", "", ac, rl);
      rbg.setSelectedIndex(1);
      methodNumber=0;
      df.add(rbg);
      JButton goButton = new JButton("Go");
      goButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (inputOk()) compute();
         }
      });
      df.add(goButton);
   }

    private boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    private void compute(){
      n = (int)ni[0].parseInput();  // number of points
      a = ni[1].parseInput();  
      b = ni[2].parseInput();  
      c = ni[3].parseInput();     
// create points
      if(methodNumber == 0) r = triangularRandomNumbersTrans(a, b, c, n);
      if(methodNumber == 1) r = triangularRandomNumbersRej(a, b, c, n);
      if(methodNumber == 0) caption = "Triangular Distribution by Transformation: ";
      if(methodNumber == 1) caption = "Triangular Distribution by Rejection: "; 
      caption += "a=" + a + ", b=" + b + ", c=" + c;
// write output
      df.writeLine(caption);
      int nlines = n / 5;
      int iline;
      for(iline = 0; iline < nlines; iline++){
         line = "";
         for(int i = 0; i < 5; i++){
            line = line + "  " + String.format(Locale.US, "%16.12f", r[5 * iline + i]);
         }
         df.writeLine(line);
      }
      if(n % 5 != 0){
         line = "";
         for(int i = 0; i < n % 5; i++){
            line = line + "  " + String.format(Locale.US, "%16.12f", r[5 * iline + i]);
         }
         df.writeLine(line);
      }
      df.writeLine("-----------------------------------------------------------------------");
// create and display histogram
      nx = 100;
      delx = (b - a) / nx;
      x0 = a;
      hist = new Histogram(x0, delx, nx);
      for(int i = 0; i < n; i++){
         hist.enter(r[i], 1.);
      }
      new GraphicsWithHistogram(getClass().getName(), "", hist, "x", "N(x)", caption);
    }

/**
* Returns n random numbers distributed according to a triangular distribution, created by the transformation method
*/
    public double[] triangularRandomNumbersTrans(double a, double b, double c, int n){
      double ba, ca, xlim;
      ba = b - a;
      ca = c - a;
      xlim = ca / ba;
      double[] r = DatanRandom.ecuy(n);
      for(int i = 0; i < n; i++){
         if(r[i] <= xlim){
            r[i] = a + Math.sqrt(ba * ca * r[i]);
         }
         else{
            r[i] = b - Math.sqrt(ba * (b - c) * (1. - r[i]));
         }
      }
      return r;
    }
/**
* Returns n random numbers distributed according to a triangular distribution, created by the rejection method
*/
    public double[] triangularRandomNumbersRej(double a, double b, double c, int n){
      double[] r = new double[n];
      double[] rr;
      double r1, h;
      int i = 0;
      while(i < n){
         rr = DatanRandom.ecuy(2);
         r1 = a + rr[0] * (b - a);
         if(r1 <= c){
            h = (r1 - a) / (c - a);
         }
         else{
            h = (b - r1) / (b - c);
         }
         if(rr[1] < h){
            r[i] = r1;
            i++;
         }
      }
      return r;
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
      new S2Random();
   }

}
