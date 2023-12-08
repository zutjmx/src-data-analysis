package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Creation of random numbers distributed according to a Breit-Wigner distribution
*/
public class S1Random {
   AuxJNumberInput[] ni = new AuxJNumberInput[3];
   DatanFrame df;
   int n, nx;
   double a, gamma, x0, delx;
   double[] r;
   Histogram hist;
   NumberFormat numForm;
   String line, caption;
   String[] ac, actionCommands;

   public S1Random(){
      String s = "Creation of random numbers distributed according to a Breit-Wigner distribution";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of points (> 1, <= 10000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true, 2, 10000);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("a", "mean", errorLabel);
      ni[1].setProperties("a", false);
      ni[1].setNumberInTextField(0.);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("gamma", "full width at halg maximum", errorLabel);
      ni[2].setProperties("gamma", false);
      ni[2].setNumberInTextField(1.);
      ig.add(ni[2]);
      df.add(ig);
      df.add(errorLabel);
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
      a = ni[1].parseInput();   // mean
      gamma = ni[2].parseInput();   // full width at half maximum
// create points
      r = breitWigner(a, gamma, n);
// write output
      df.writeLine("Mean a = " + String.format(Locale.US, "%10.5f", a) + ", full width at half maximum gamma = "
         + String.format(Locale.US, "%10.5f", gamma)) ;
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
      delx = 10. * gamma / nx;
      x0 = a - 5. * gamma;
      hist = new Histogram(x0, delx, nx);
      for(int i = 0; i < n; i++){
         hist.enter(r[i], 1.);
      }
      caption = "Random Numbers following a Breit-Wigner distribution; a=" + a + ", &G=" + gamma;
      new GraphicsWithHistogram(getClass().getName(), "", hist, "x", "N(x)", caption);
    }

    public double[] breitWigner(double a, double gamma, int n){
      double[] r = DatanRandom.ecuy(n);
      for(int i = 0; i < n; i++){
         r[i] = a + 0.5 * gamma * Math.tan(Math.PI * (r[i] - 0.5));
      }
      return r;
    }

   public static void main(String s[]) {
      new S1Random();
   }

}
