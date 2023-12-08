package datan;

import java.text.*;
import java.util.*;
import java.lang.*;

/**
* A class computing the Matrix A of derivatives needed int classes LsqNon and LsqMar
* @author  Siegmund Brandt.
*/

public class AuxDri{


private static double DELTA = 1.E-11, CUT = 1.E-9;

DatanUserFunction f;
int il, r, nny, nred;
int[] list;
double arg, del, fm, fp, sav;
DatanVector t, x, y;
DatanMatrix a;

   
/**
* @param luf user function which must be an extension of the abstract class DatanUserFunction.
* @param t vector of controlled variables.
* @param y vector of measured values.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
*/
   public AuxDri(DatanUserFunction luf, DatanVector t, DatanVector y, int[] list){
      f = luf;
      this.list = list;
      this.t = t;
      this.y = y;
      nny = t.getNumberOfElements();
      r = list.length;
      nred = 0;
      for(int i = 0; i < r; i++){
         if(list[i] == 1) nred++;
      }
      a = new DatanMatrix(nny, nred);
   }
      
/**
* @return the matrix A of derivatives.
* @param xin position in n-space, where drivatives are to be computed.
*/
   public DatanMatrix getMatrixOfDerivatives(DatanVector xin){
      x = new DatanVector(xin);
      for(int k = 0; k < nny; k++){
         il = -1;
         if(nred > 0){
            for(int i = 0; i < r; i++){
               if(list[i] == 1){
                  il++;
                  arg = Math.abs(x.getElement(i));
                  if(arg < CUT) arg = CUT;
                  del = DELTA * arg;
                  sav = x.getElement(i);
                  x.setElement(i, sav + del);
                  fp = f.getValue(x, t.getElement(k));
                  x.setElement(i, sav - del);
                  fm = f.getValue(x, t.getElement(k));
                  a.setElement(k, il, (fp - fm) / (del + del));
               }
            }
         }
      }
      return a;
   }
}
