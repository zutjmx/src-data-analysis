package datan;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;              //for layout managers
import java.awt.event.*;        //for action and window events

/**
* A class representing a JPanel carrying a vertical row of radio buttons with a border, a border title,
a tooltip text; all buttons are listened to by the same ActionListener. 
* @author  Siegmund Brandt.
*/
public class AuxJRButtonGroup extends JPanel {


    int i, j, selectedIndex;
    String[] actionCommands;
    String borderTitle, toolTipText;
    JRadioButton[] radioButtons;
	 JPanel[] buttonRows;
    ActionListener myListener;
/**
* ceates an AuxJRbuttonGroup, which is a JPanel carrying a vertical row of radio buttons with a border, a border title,
* a tooltip text; all buttons are listened to by the same ActionListener.
* @param     actionCommands an array of text strings, the length of which determines the number of buttons; the texts
* themselves are written to the right of the buttons and act as action commands.
*/
    public AuxJRButtonGroup(String borderTitle, String toolTipText,
	 String[] actionCommands, ActionListener myListener) {
		this.actionCommands = actionCommands;
		this.borderTitle = borderTitle;
		this.toolTipText = toolTipText;
      this.myListener = myListener;
	
	   setLayout(new BorderLayout());
		if(borderTitle != ""){
           setBorder(
                BorderFactory.createCompoundBorder(
                      BorderFactory.createTitledBorder(borderTitle),
                      BorderFactory.createEmptyBorder(5,5,5,5)));
		}
		if(toolTipText != "") setToolTipText(toolTipText);

  	   JPanel buttonGroupPanel = new JPanel();
	   buttonGroupPanel.setLayout(new BoxLayout(buttonGroupPanel, BoxLayout.Y_AXIS));
	   ButtonGroup group = new ButtonGroup();
	   radioButtons = new JRadioButton[actionCommands.length];
	   buttonRows = new JPanel[actionCommands.length];
	   for(i = 0; i < actionCommands.length; i++) {
		   radioButtons[i] = new JRadioButton( actionCommands[i]);
         radioButtons[i].addActionListener(myListener);
		   group.add(radioButtons[i]);
		   buttonGroupPanel.add(radioButtons[i]);
	   }
      radioButtons[0].setSelected(true);
      setSelectedIndex(0);
	   buttonGroupPanel.add(Box.createVerticalGlue());
	   add(buttonGroupPanel);
   }

/**
* adds a text string which will be placed on a label.
*/
	public void addText(String text){
		JPanel labelCarrier = new JPanel();
		labelCarrier.setLayout(new BoxLayout(labelCarrier, BoxLayout.X_AXIS));
		JLabel label = new JLabel(text);
		labelCarrier.add(label);
		add(labelCarrier);
	}
   
/**
* replaces the text strings displayed to the right of the radio buttons.
*/
   public void setText(String[] texts){
	   for(i = 0; i < actionCommands.length; i++) {
	  		radioButtons[i].setText(texts[i]);
	   }
	}
/**
* sets a tooltip text for each radio button.
*/
   public void setToolTipText(String[] texts){
	   for(i = 0; i < actionCommands.length; i++) {
	  		radioButtons[i].setToolTipText(texts[i]);
	   }
	}

/**
* sets the index of the selected radio button.
*/
	public void setSelectedIndex(int i){
      selectedIndex = i;
	}
/**
* returns the index of the selected radio button.
*/
	public int getSelectedIndex(){
      return selectedIndex;
	}

/**
* enables or disables all buttons, depending on boolean argument.
*/
	public void setEnabled(boolean enabled){
	 	for(i = 0; i < actionCommands.length; i++){
			setEnabled(i, enabled);
		}
	}

/**
* enables or disables the button referenced by index, depending on boolean argument.
*/
	public void setEnabled(int index, boolean enabled){
	 	radioButtons[index].setEnabled(enabled);
		if(enabled == true) radioButtons[index].setForeground(Color.black);
		else radioButtons[index].setForeground(Color.gray);
	}


}
