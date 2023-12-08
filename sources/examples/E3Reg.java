package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

public class E3Reg {
   int n;
   AuxJNumberInput[] ni = new AuxJNumberInput[2];
   DatanFrame df;  
   NumberFormat numForm;
      double[] t ={-.9, -.7, -.5, -.3, -.1, .1, .3, .5, .7, .9};
      double[] y = {81., 50., 35., 27., 26., 60. ,106., 189. ,318., 520.};
      double[] deltay;

   public E3Reg(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(12);
  	   numForm.setMinimumFractionDigits(12);
      String s = "Example demonstrating polynomial regression including graphics of data and regrssion line";
      df = new DatanFrame(getClass().getName(), s);
      AuxJInputGroup ig = new AuxJInputGroup("Enter variables", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("r", "number of parameters in  polynomial (>= 1, <= 10)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("r", true);
      ni[0].setMinimum(1);
      ni[0].setMaximum(10);
      ni[0].setNumberInTextField(3);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("P", "probability for confidence limits(> 0, < 1)", errorLabel);
      ni[1].setProperties("P", false);
      ni[1].setMinimum(0.);
      ni[1].setMaximum(1.);
      ni[1].setNumberInTextField(.95);
      ig.add(ni[1]);
      df.add(ig);
      df.add(errorLabel);
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.repaint();
// set up data
      n = t.length;
      deltay = new double[n];
      for(int i = 0; i < n; i++){
         deltay[i] = Math.sqrt(y[i]);
      }
   }

   protected void producePlot(){
      int nnr = (int)ni[0].parseInput();
      double prob = ni[1].parseInput();
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
         datrho[i] =0.;
      }
      double del = (t[n - 1] - t[0]) / (double)(npl - 1);
      for(int j = 0; j < 3; j++){
         colorindex[j] = 3;
         if(j ==0) colorindex[j] = 5;
         Regression reg = new Regression(t, y, deltay, nnr);
         for(int i = 0; i < npl; i++){
            xpl[j][i] = t[0] + (double)i * del;
            if(j == 0) ypl[j][i] = reg.regressionLine(xpl[j][i]);
            else if(j == 1) ypl[j][i] = ypl[0][i] + reg.confidenceLimit(xpl[j][i], prob);
            else ypl[j][i] = ypl[0][i] - reg.confidenceLimit(xpl[j][i], prob);
         }
      }
// produce graphics
      String caption = "Polynomial regression, r#=" + nnr + ", P=" + prob;
      double scale = .3;
      String frameTitle = getClass().getName();
      String fileName = getClass().getName() + ".ps";
      new GraphicsWithDataPointsAndMultiplePolylines(frameTitle, fileName, xpl, ypl,
            colorindex, 1, scale, datx, daty, datsx, datsy, datrho, "t", "y", caption);
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

   public static void main(String s[]) {
      new E3Reg();
   }

}
