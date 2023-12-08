package datan;

/**
* A class computing the value of a function at a given point on straight line in n-dimensional space.
* 
* @author  Siegmund Brandt.
*/
public final class FunctionOnLine {
    DatanUserFunction function;
    DatanVector x0, xdir;
/**
* @param x0 point in n-space
* @param xdir direction in n-space. Straight line is given by x = x0 + a * xdir, where a is a running scalar variable.
* @param function user function which must be an extension of the abstract class DatanUserFunction.
*/
   public FunctionOnLine(DatanVector x0, DatanVector xdir, DatanUserFunction function){
      this.function = function;
      this.x0 = x0;
      this.xdir = xdir;
   }
/**
* @return the function value for a given value of the parameter a.
*/
   public double getFunctionOnLine(double a){
      return function.getValue(x0.add(xdir.multiply(a)));
   }   

}
