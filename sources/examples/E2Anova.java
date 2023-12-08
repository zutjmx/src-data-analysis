package examples;

import datan.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example performing an analysis of variance on simulated data
*/
public class E2Anova {
   AuxJNumberInput[] ni = new AuxJNumberInput[7];
   DatanFrame df;
   int n, nni, nnj, nnk, rcount;
   double sigma, dxi, dxj, dxk;
   double[] r;
   double[][][] x;
   String[] ac, actionCommands;
   AnalysisOfVariance av;
   NumberFormat numForm;

   public E2Anova(){
      String s = "Example performing an analysis of variance on simulated data";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("sigma", "standard deviation of data (>0)", errorLabel);
      ni[0].setProperties("sigma", false, 0.);
      ni[0].setNumberInTextField(1);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("I", "number of classifications A (>=2, <10)", errorLabel);
      ni[1].setProperties("I", true, 2, 10);
      ni[1].setNumberInTextField(2);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("J", "number of classifications B (>=2, <10)", errorLabel);
      ni[2].setProperties("J", true, 2, 10);
      ni[2].setNumberInTextField(3);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("K", "number of data for fixed A and B >=2, <10)", errorLabel);
      ni[3].setProperties("K", true, 2, 10);
      ni[3].setNumberInTextField(4.);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("Delta_i", "step for deviation of mean from zero in i", errorLabel);
      ni[4].setProperties("Delta_i", false);
      ni[4].setNumberInTextField(0.);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("Delta_i", "step for deviation of mean from zero in i", errorLabel);
      ni[5].setProperties("Delta_i", false);
      ni[5].setNumberInTextField(0.);
      ig.add(ni[5]);
      ni[6] = new AuxJNumberInput("Delta_i", "step for deviation of mean from zero in i", errorLabel);
      ni[6].setProperties("Delta_i", false);
      ni[6].setNumberInTextField(0.);
      ig.add(ni[6]);
      df.add(ig);
      df.add(errorLabel);
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.pack();
      df.toFront();
   }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected void compute(){  
      sigma = ni[0].parseInput();
      nni = (int)ni[1].parseInput();  
      nnj = (int)ni[2].parseInput();  
      nnk = (int)ni[3].parseInput();   
      dxi = ni[4].parseInput();
      dxj = ni[5].parseInput();
      dxk = ni[6].parseInput();
// simulate data
      n = nni * nnj * nnk;
      r = DatanRandom.standardNormal(n);
      rcount = 0;
      x = new double[nni][nnj][nnk];
      for(int i = 0; i < nni; i++){
         for(int j = 0; j < nnj; j++){
            for(int k = 0; k < nnk; k++){
               x[i][j][k] = r[rcount] * sigma + (double)i * dxi + (double)j * dxj + (double)k * dxk;
               rcount++;
            }
         }
      }
// analysis of variance and output     
      av = new AnalysisOfVariance(x);
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(3);
  	   numForm.setMinimumFractionDigits(0);
      df. writeLine("Data are drawn from a normal distribution with standard deviation sigma = "  + numForm.format(sigma));
      df. writeLine("and mean 0. + i * Delta_i + j * Delta_j + k * Delta_k,");
      df. writeLine("where Delta_i = " + numForm.format(dxi) + ", Deltaj = "
         + numForm.format(dxj) + ", Delta_k = " + numForm.format(dxk) + ".\n");
      df.writeLines(av.displayData());
      df.writeLines(av.tableCrossed());
      df.writeLines(av.tableNested());

      df.writeLine("-------------------------------------------------------------------------------");
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new E2Anova();
   }

}
