package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example creating points fluctuating about a straight line, using the method DatanRandom.line
*/
public class E2Random {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n;
   double a, b, t0, deltat, sigmay;
   double xa, xb, ya, yb;
   double[] r;
   double t;
   NumberFormat numForm;
   String caption, header;
   String[] ac, actionCommands;

   public E2Random(){
      String s = "Example creating points fluctuating about a straight line, using the method DatanRandom.line";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of points (> 1, <= 100)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true, 2, 100);
      ni[0].setNumberInTextField(20);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("a", "slope", errorLabel);
      ni[1].setProperties("a", false);
      ni[1].setNumberInTextField(1);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("b", "intercept", errorLabel);
      ni[2].setProperties("b", false);
      ni[2].setNumberInTextField(0);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("t_0", "abscissa value of first point", errorLabel);
      ni[3].setProperties("t_0", false);
      ni[3].setNumberInTextField(0);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("delta_t", "interval in t between points", errorLabel);
      ni[4].setProperties("delta_t", false);
      ni[4].setNumberInTextField(1);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("sigma_y", "Gaussian error in ordinate value (>= 0)", errorLabel);
      ni[5].setProperties("sigma_y", false, 0.);
      ni[5].setNumberInTextField(2);
      ig.add(ni[5]);
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
      a = ni[1].parseInput();   // intercept
      b = ni[2].parseInput();   // slope
      t0 = ni[3].parseInput();   // abscissa value of first point
      deltat = ni[4].parseInput();   // interval in t between points
      sigmay = ni[5].parseInput();   // Gaussian error in ordinate value
      double[] datx = new double[n];
      double[] daty = new double[n];
// create points
      DatanRandom.line(a, b, t0, deltat, sigmay, datx, daty);
// write output
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(6);
  	   numForm.setMinimumFractionDigits(0);
      header = "y = " + numForm.format(a) + " t + " +  numForm.format(b) + "; sigma_y = " + numForm.format(sigmay);
//      header = "y = " + String.format("%f", a) + " t + " +  String.format("%f",b) + "; sigma_y = " + String.format("%f",sigmay);
      df.writeLine(header);
      df.writeLine("              t               y");
      for(int i = 0; i < n; i++){
         df.writeLine(String.format(Locale.US,"%15.5f %15.5f", datx[i], daty[i]) );
      }       
      df.writeLine("------------------------------------------------");
// produce scatter diagram
      xa = datx[0] - deltat;
      xb = datx[n-1] + deltat;
      ya = a * xa + b - 5. * Math.signum(a)* sigmay;
      yb = a * xb + b + 5. * Math.signum(a)* sigmay;
      if(ya > yb){
         double help = ya;
         ya = yb;
         yb = help;
      }
	   numForm.setMaximumFractionDigits(5);
  	   numForm.setMinimumFractionDigits(0); 
      caption = "y = " + numForm.format(a) + " t + " +  numForm.format(b) + ", &s@_y# = " + numForm.format(sigmay); 
      new GraphicsWith2DScatterDiagram(getClass().getName(), "", datx, daty, xa, xb, ya, yb, "t", "y", caption);
    }

   public static void main(String s[]) {
      new E2Random();
   }

}
