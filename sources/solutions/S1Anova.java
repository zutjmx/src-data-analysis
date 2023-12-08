package solutions;

import datan.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Performing an analysis of variance with crossed classification on simulated data
*/
public class S1Anova {
   AuxJNumberInput[] ni = new AuxJNumberInput[8];
   DatanFrame df;
   int n, nni, nnj, nnk, rcount;
   double sigma, a, b, ab, mu, ai, bj, abij;
   double[] r;
   double[][][] x;
   String[] ac, actionCommands;
   AnalysisOfVariance av;
   NumberFormat numForm;

   public S1Anova(){
      String s = "Performing an analysis of variance with crossed classification on simulated data";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("sigma", "standard deviation of data (>0)", errorLabel);
      ni[0].setProperties("sigma", false, 0.00001);
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
      ni[4] = new AuxJNumberInput("mu", "parameter in data model", errorLabel);
      ni[4].setProperties("mu", false);
      ni[4].setNumberInTextField(0.);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("a", "parameter in data model", errorLabel);
      ni[5].setProperties("a", false);
      ni[5].setNumberInTextField(0.);
      ig.add(ni[5]);
      ni[6] = new AuxJNumberInput("b", "parameter in data model", errorLabel);
      ni[6].setProperties("b", false);
      ni[6].setNumberInTextField(0.);
      ig.add(ni[6]);
      ni[7] = new AuxJNumberInput("ab", "parameter in data model", errorLabel);
      ni[7].setProperties("ab", false);
      ni[7].setNumberInTextField(0.);
      ig.add(ni[7]);
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
      mu = ni[4].parseInput();
      a = ni[5].parseInput();
      b = ni[6].parseInput();
      ab = ni[7].parseInput();
// simulate data
      n = nni * nnj * nnk;
      r = DatanRandom.standardNormal(n);
      rcount = 0;
      x = new double[nni][nnj][nnk];
      for(int i = 0; i < nni; i++){
         ai = ((double)(i + 1) - 0.5 * (double)(nni + 1)) * a;
         for(int j = 0; j < nnj; j++){
            bj = ((double)(j + 1) - 0.5 * (double)(nnj + 1)) * b;
//            abij =  (((double)i - 0.5 * (double)(nni - 1)) - ((double)j - 0.5 * (double)(nnj - 1)))* ab ;
//            abij =  ((double)i  * (double)j )* ab ;
//              abij = ab * (ai + bj) / (Math.abs(ai) + Math.abs(bj));
              abij = ab * Math.signum(ai) *  Math.signum(bj);
            for(int k = 0; k < nnk; k++){
               x[i][j][k] = mu + ai + bj + abij + sigma * r[rcount];
               rcount++;
            }
         }
      }
// analysis of variance and output     
      av = new AnalysisOfVariance(x);
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(3);
  	   numForm.setMinimumFractionDigits(0);
      df. writeLine("Data are simulated according to the model");
      df. writeLine("x_ijk = mu + a_i + b_j + (ab)_ij + epsilon_ijk");
      df. writeLine("with a_i = a * ((i + 1) - 0.5 * (I + 1)), b_j = b * ((j + 1) - 0.5 * (J + 1)), (ab)_ij  = (ab) * sign(a_i) * sign(b_j)");
      df.writeLines(av.displayData());
      df.writeLines(av.tableCrossed());
      df.writeLine("-------------------------------------------------------------------------------");
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new S1Anova();
   }

}
