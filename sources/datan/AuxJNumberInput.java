package datan;


import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;              //for layout managers
import java.awt.event.*;        //for action and window events

import java.net.URL;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.lang.*;

/**
* A class representing a JPanel carrying a JLabel with Text (and tooltip text)
* and a JTextField for numerical input, the validity of which is checked. 
* @author  Siegmund Brandt.
*/
public class AuxJNumberInput extends JPanel
	implements ActionListener, FocusListener {

    private double minimum = -Double.MAX_VALUE;
    private double maximum =  Double.MAX_VALUE;

    private boolean isInteger = false;
    private boolean errorMessageDisplayed = false;

    double contents;
    boolean parsedWell = true;
    NumberFormat numForm;
    JTextField tf;
    JLabel l, errorLabel;
    String labelText, toolTipText;
    JPanel sp;
	 JPanel leftPanel, middlePanel, rightPanel;


/**
* creates an  AuxJNumberInput which is a JPanel carrying a JLabel with Text (and tooltip text)
* and a JTextField for numerical input, the validity of which is checked.
* @param errorLabel  label on which a possible error message is written.
*/
    public AuxJNumberInput(String labelText, String toolTipText,
		JLabel errorLabel) {
	this.minimum = Double.NEGATIVE_INFINITY;
	this.maximum = Double.POSITIVE_INFINITY;
	this.labelText = labelText;
	this.toolTipText = toolTipText;
	this.errorLabel = errorLabel;
	initialize();

	}

/**
* creates an  AuxJNumberInput which is a JPanel carrying a JLabel with Text (and tooltip text)
* and a JTextField for numerical input, the validity of which is checked.
* @param errorLabel     label on which a possible error message is written.
* @param minimum     minimum permissible input value.
* @param maximum     maximum permissible input value.
*/
    public AuxJNumberInput(String labelText, String toolTipText,
          JLabel errorLabel, double minimum, double maximum) {
	this.minimum = minimum;
	this.maximum = maximum;
	this.labelText = labelText;
	this.toolTipText = toolTipText;
	this.errorLabel = errorLabel;
	initialize();

	}


	private void initialize(){
	leftPanel = new JPanel();
	middlePanel = new JPanel();
	rightPanel = new JPanel();
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));  
   numForm = NumberFormat.getNumberInstance(Locale.US);
	numForm.setMaximumFractionDigits(8);
	tf = new JTextField(10);
	Dimension d = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
	Dimension dm = tf.getMinimumSize();
	d.height = (int)((float)dm.height*1.5f+0.5f);
	tf.setPreferredSize(dm);
	tf.setMaximumSize(dm);
	tf.setHorizontalAlignment(JTextField.RIGHT);
	l = new JLabel(labelText);
	if(toolTipText != "") l.setToolTipText(toolTipText);
	tf.addActionListener(this);
	tf.addFocusListener(this);
	l.setLabelFor(tf);
	leftPanel.add(l);
	rightPanel.add(tf);
	add(leftPanel);
	add(Box.createHorizontalGlue());
	add(middlePanel);
	add(Box.createHorizontalGlue());
	add(rightPanel);
	}

/**
* sets properties; enables or disables input; if isInteger == true, only the input of integers is permissable.
*/
	public void setProperties(boolean enabled, boolean isInteger){
		setEnabled(enabled);
		setIsInteger(isInteger);
      minimum = Double.NEGATIVE_INFINITY;
      maximum = Double.POSITIVE_INFINITY;
	}

/**
* sets properties; enables or disables input.
* @param minimum     minimum permissible input value.
*/
	public void setProperties(boolean enabled, boolean isInteger, double minimum){
		setEnabled(enabled);
		setIsInteger(isInteger);
      this.minimum = minimum;
      this. maximum = Double.POSITIVE_INFINITY;
	}

/**
* sets properties; enables or disables input; if isInteger == true, only the input of integers is permissable.
* @param minimum     minimum permissible input value.
* @param maximum     maximum permissible input value.
*/
	public void setProperties(boolean enabled, boolean isInteger, double minimum, double maximum){
		setEnabled(enabled);
		setIsInteger(isInteger);
      this.minimum = minimum;
      this.maximum = maximum;
	}

/**
* sets properties; input is enabled; if isInteger == true, only the input of integers is permissable.
* @param labelText     text placed on label to the left of text field for input.
*/
	public void setProperties(String labelText, boolean isInteger){
		setEnabled(true);
		setIsInteger(isInteger);
      l.setText(labelText);
      minimum = Double.NEGATIVE_INFINITY;
      maximum = Double.POSITIVE_INFINITY;
	}

/**
* sets properties; input is enabled; if isInteger == true, only the input of integers is permissable.
* @param labelText     text placed on label to the left of text field for input.
* @param minimum     minimum permissible input value.
*/
	public void setProperties(String labelText, boolean isInteger, double minimum){
		setEnabled(true);
		setIsInteger(isInteger);
      l.setText(labelText);
      this.minimum = minimum;
      this. maximum = Double.POSITIVE_INFINITY;
	}

/**
* sets properties; input is enabled; if isInteger == true, only the input of integers is permissable.
* @param labelText     text placed on label to the left of text field for input.
* @param minimum     minimum permissible input value.
* @param maximum     maximum permissible input value.
*/
	public void setProperties(String labelText, boolean isInteger, double minimum, double maximum){
		setEnabled(true);
		setIsInteger(isInteger);
      l.setText(labelText);
      this.minimum = minimum;
      this.maximum = maximum;
	}

/**
* enables or disables input.
*/
	public void setEnabled(boolean enabled){
		tf.setEnabled(enabled);
		if(enabled == true) l.setText(labelText);
		else l.setText("");
	}

/**
* sets text for label.
*/
	public void setText(String text){
		l.setText(text);
	}

/**
* sets tooltip text for label.
*/
	public void setToolTipText(String toolTipText){
		if(toolTipText != "") l.setToolTipText(toolTipText);
	}

/**
* returns label text.
*/
	public String getLabelText(){
		return(l.getText());
	}

/**
* places a number in the text field and checks it for validity.
*/
	public void setNumberInTextField(double d){
		tf.setText(numForm.format(d));
		contents = parseInput();
	}

/**
* sets minmum permissible value.
*/
	public void setMinimum(double minimum){
		this.minimum = minimum;
	}

/**
* sets maximum permissible value.
*/
	public void setMaximum(double maximum){
		this.maximum = maximum;
	}

/**
* sets a property; if isInteger == true, only the input of integers is permissable.
*/
	public void setIsInteger(boolean isInteger){
		this.isInteger = isInteger;
		if(isInteger) tf.setColumns(8);
      else tf.setColumns(10);
	}

/**
* returns true if input is enabled.
*/
	public boolean isEnabled(){
		return tf.isEnabled();
	}

/**
* parses the input; if input permissible the result is returned as double;
* if it is not permissible, zero is returned and an error message is written on the error label.
*/
	public double parseInput(){
      parsedWell = true;
      errorLabel.setText("");
	   double res = 0.;
      try{
          Number inputNumber = numForm.parse(tf.getText().trim());
          res = inputNumber.doubleValue();
		    if((res < minimum) || (res > maximum)){
			   tf.selectAll();
		    	tf.grabFocus();
				errorMessageDisplayed = true;
		      errorLabel.setForeground(Color.red);  
            errorLabel.setText(l.getText() + ": input out of range ");
            parsedWell = false;
            Toolkit.getDefaultToolkit().beep();
		   }
         if(isInteger && (Math.floor((double)res) != (double)res)){
			   tf.selectAll();
		    	tf.grabFocus();
				errorMessageDisplayed = true;
		      errorLabel.setForeground(Color.red);  
            errorLabel.setText(l.getText() + ": input is not integer ");
            parsedWell = false;
            Toolkit.getDefaultToolkit().beep();
		   }
      }
      catch (ParseException pe) {
		   tf.selectAll();
		   tf.grabFocus();
		   errorMessageDisplayed = true;
		   errorLabel.setForeground(Color.red); 
         errorLabel.setText(l.getText() + ": format mismatch ");
         parsedWell = false;
         Toolkit.getDefaultToolkit().beep();
      }
      return res;
	}
   
/**
* returns true if input parses correctly, false otherwise.
*/
     	public boolean parseOk(){
         parseInput();
         return parsedWell;
    	}

     	public void focusGained(FocusEvent f){
    	}
    	public void focusLost(FocusEvent f){
			contents = parseInput();
		}
    	public void actionPerformed(ActionEvent a){
			contents = parseInput();
		}
}
