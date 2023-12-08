package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
/**
* Example demonstrating the use of the classes Histogram  and GraphicsWithHistogram
*/
public class E2Sample {
   AuxJNumberInput[] ni = new AuxJNumberInput[4];
   DatanFrame df;
   String[] ac, actionCommands;

   public E2Sample(){
      String s = "Example demonstrating the use of the classes Histogram and GraphicsWithHistogram";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "size of sample to be drawn from standard normal (>0, <= 100000)", errorLabel);
      ni[0].setProperties("N", true, 1, 100000);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("x0", "lower edge of histogram", errorLabel);
      ni[1].setProperties("x0", false);
      ni[1].setNumberInTextField(-5.);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("delx", "bin width (>0)", errorLabel);
      ni[2].setProperties("delx", false, 0.);
      ni[2].setNumberInTextField(.1);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("nx", "number of bins (>1)", errorLabel);
      ni[3].setProperties("nx", true, 2);
      ni[3].setNumberInTextField(100);
      ig.add(ni[3]);
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
      int n, nx;
      double x0, delx;
      Histogram hist;
      double[] rand;
      NumberFormat numForm;
      String caption;
// input
      n = (int)ni[0].parseInput();   
      x0 = ni[1].parseInput();
      delx = ni[2].parseInput(); 
      nx = (int)ni[3].parseInput();
// create sample
      rand = DatanRandom.standardNormal(n);
// create and fill histogram
      hist = new Histogram(x0, delx, nx);
      for(int i = 0; i < n; i++){
         hist.enter(rand[i], 1.);
      }
// prepare caption for graphics  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(3);
  	   numForm.setMinimumFractionDigits(0);
      caption = "N = " + n + ", x_0# = " + numForm.format(x0) + ", &D@x = " + numForm.format(delx) + ", n_x# = " + nx;
//create graphics
      new GraphicsWithHistogram(getClass().getName(), "", hist, "x", "n(x)", caption);
   }
    
   private class GoButtonListener implements ActionListener { 
       public void actionPerformed(ActionEvent e) {
          if (inputOk()) compute();
      }
   }

   public static void main(String s[]) {
      new E2Sample();
   }

}
