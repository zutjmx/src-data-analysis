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
* A class representing a JPanel with a border, a border title, and
a tooltip text, to which other elements can be added, one below the other.
* @author  Siegmund Brandt.
*/
 public class AuxJInputGroup extends JPanel{

/**
* ceates an AuxJInputGroup, which is a JPanel with a border, a border title,
a tooltip text to which other elements can be added, one below the other.
*/
    public AuxJInputGroup(String borderTitle, String toolTipText) {

	setLayout(new BorderLayout());
        setBorder(
                BorderFactory.createCompoundBorder(
                      BorderFactory.createTitledBorder(borderTitle),
                      BorderFactory.createEmptyBorder(5,5,5,5)));

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	if(toolTipText != "") setToolTipText(toolTipText);



    }
    
/**
* adds an element of type AuxJNumberInput.
*/
	public void addNumberInput(AuxJNumberInput ni){
		add(ni);
		revalidate();
	}

/**
* removes an element of type AuxJNumberInput.
*/
	public void removeNumberInput(AuxJNumberInput ni){
		remove(ni);
		revalidate();
		repaint();
	}

/**
* adds a text string which will be placed on a label.
*/
	public void addText(String text){
		JPanel labelCarrier = new JPanel();
		labelCarrier.setLayout(new BorderLayout());
		JLabel label = new JLabel(text);
		labelCarrier.add(label);
		add(labelCarrier);
	}

}


