package datan;

import java.text.*;
import java.util.*;
import java.lang.*;

/**
* A class computing the gradient vector of a function of n variables
* @author  Siegmund Brandt.
*/

public class AuxGrad{


private static double DELTA = 1.E-11, CUT = 1.E-9;

DatanUserFunction f;
int il, n, nred;
int[] list;
double arg, del, fm, fp, sav;
DatanVector x, grad;

   
/**
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
*/
   public AuxGrad(DatanUserFunction muf, int[] list){
      f = muf;
      this.list = list;
      n = list.length;
      nred = 0;
      for(int i = 0; i < n; i++){
         if(list[i] == 1) nred++;
      }
      grad = new DatanVector(nred);
   }
      
/**
* @return gradient.
* @param xin position in n-space, where gradient is to be computed.
*/
   public DatanVector getGradient(DatanVector xin){
      x = new DatanVector(xin);
      il = -1;
      if(nred > 0){
         for(int i = 0; i < n; i++){
            if(list[i] == 1 || nred == n){
               il++;
               arg = Math.abs(x.getElement(i));
               if(arg < CUT) arg = CUT;
               del = DELTA * arg;
               sav = x.getElement(i);
               x.setElement(i, sav + del);
               fp = f.getValue(x);
               x.setElement(i, sav - del);
               fm = f.getValue(x);
               grad.setElement(il, (fp - fm) / (del + del));
            }
         }
      }
      return grad;
   }
}
