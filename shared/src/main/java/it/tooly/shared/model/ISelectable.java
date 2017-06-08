/**
 * 
 */
package it.tooly.dctmclient.model;

/**
 * A basic interface for an object that is selectable
 * 
 * @author M.E. de Boer
 *
 */
public interface ISelectable {
	/**
	 * @return <code>true</code> if this object is selected
	 */
	public boolean getIsSelected();
	
	/**
	 * @param isSelected <code>true</code> if this object is selected
	 */
	public void setIsSelected(boolean isSelected);
}
