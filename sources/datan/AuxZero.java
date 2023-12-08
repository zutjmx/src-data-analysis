package datan;

/**
* A class with which a zero of a scalar monotonic function of a single variable is found
*
* @author  Siegmund Brandt.
*/

public class AuxZero{
double EPSILON = 1.E-5;
   DatanUserFunction uf;
   double x0, x1, xm, xs, f0, f1, fm;
   boolean converged;
   int nstep;

/**
* @param uf user function which must be an extension of the abstract class DatanUserFunction.
* @param x0 one value of the function's argument. 
* @param x1 a second value of the function's argument.  
*/
public AuxZero(DatanUserFunction uf, double x0, double x1){
   this.uf = uf;
   this.x0 = x0;
   this.x1 = x1;
   converged = false;
   bracketZero();
   if(converged) findZero();
}

private void bracketZero(){
   if(x0 == x1) x1 = x0 + 1.;
   f0 = uf.getValue(x0);
   f1 = uf.getValue(x1);
   nstep = 0;
   while(f0 * f1 > 0 && nstep < 1000){
      nstep++;
      if(Math.abs(f0) <= Math.abs(f1)){
         xs = x0;
         x0 = x0 + 2. * (x0 - x1);
         x1 = xs;
         f1 = f0;
         f0 = uf.getValue(x0);
      }
      else{
         xs = x1;
         x1 = x1 + 2. * (x1 - x0);
         x0 = xs;
         f0 = f1;
         f1 = uf.getValue(x1);
      }
   }
   if(nstep >= 1000) converged = false;
   else converged = true;
}

private void findZero(){
   xs = x0;
   loop:
   for(int i = 0; i < 10000; i++){
      f0 = uf.getValue(x0);
      f1 = uf.getValue(x1);
      if(f0 == 0.){
         xs = x0;
         break loop;
      }
      if(f1 == 0.){
         xs = x1;
         break loop;
      }
      xm = 0.5 * (x0 + x1);
      if(Math.abs(x0 - x1) >= EPSILON){
         fm = uf.getValue(xm);
         if(f0 * fm < 0.) x1 = xm;
         else x0 = xm;
      }
      else{
         xs = xm;
         break loop;
      }
   }
}
/**
* @return true if method has converged, false otherwise.
*/
public boolean hasConverged(){
   return converged;
}
/**
* @return the argument value for which the function is zero.
*/
public double getZero(){
   return xs;   
}


}