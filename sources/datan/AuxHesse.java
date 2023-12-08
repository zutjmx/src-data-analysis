package datan;

import java.text.*;
import java.util.*;
import java.lang.*;

/**
* A class computing the Hessian matrix of second derivatives of a function of n variables
* @author  Siegmund Brandt.
*/

public class AuxHesse{


private static double DELTA = 1.E-5, CUT = 1.E-9;

DatanUserFunction funct;
int il, kl, n, nred;
int[] list;
double arg, dfdxp, dfdxm, deli, delk, e, f, fm, fp, h, savi, savk;
DatanVector x;
DatanMatrix hesse;

   
/**
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
*/
   public AuxHesse(DatanUserFunction muf, int[] list){
      funct = muf;
      this.list = list;
      n = list.length;
      nred = 0;
      for(int i = 0; i < n; i++){
         if(list[i] == 1) nred++;
      }
      hesse = new DatanMatrix(nred, nred);
   }
      
/**
* @return Hessian matrix of second derivatives.
* @param xin position in n-space, for which Hessian is to be computed.
*/
   public DatanMatrix getHessian(DatanVector xin){
      x = new DatanVector(xin);
      if(nred > 0){
         il = -1;
         for(int i = 0; i < n; i++){
            kl = -1;
            if(list[i] == 1 || nred == n){
               il++;
               for(int k = 0; k < n; k++){
                  if(list[k] == 1 || nred == n){
                     kl++;
                     if(kl < il){
                        hesse.setElement(il, kl, hesse.getElement(kl, il));
                     }
                     else{
                        e = Math.abs(x.getElement(i));
                        if(e < CUT) e = CUT;
                        deli = DELTA * e;
                        savi = x.getElement(i);
                        if(k == i){
                           f = funct.getValue(x);
                           x.setElement(i, savi + deli);
                           fp = funct.getValue(x);
                           x.setElement(i, savi - deli);
                           fm = funct.getValue(x);
                           x.setElement(i, savi);
                           h = ((fp - f) / deli - (f - fm) / deli) / deli;
                           hesse.setElement(il, il, h);
                        }
                        else{
                           e = Math.abs(x.getElement(k));
                           if(e < CUT) e = CUT;
                           delk = DELTA * e;
                           savk = x.getElement(k);
                           f = funct.getValue(x);
                           x.setElement(k, savk + delk);
                           x.setElement(i, savi + deli);
                           fp = funct.getValue(x);
                           x.setElement(i, savi - deli);
                           fm = funct.getValue(x);
                           dfdxp = (fp - fm ) / (deli + deli);
                           x.setElement(k, savk - delk);
                           x.setElement(i, savi + deli);
                           fp = funct.getValue(x);
                           x.setElement(i, savi - deli);
                           fm = funct.getValue(x);
                           dfdxm = (fp - fm ) / (deli + deli);
                           h = (dfdxp - dfdxm) / (delk + delk);
                           hesse.setElement(il, kl, h);                           
                        }                        
                     }
                  }
               }
            }
         }
      }
      return hesse;
   }
}
