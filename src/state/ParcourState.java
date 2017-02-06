package state;

public interface ParcourState {
	/**
	 * 
	 * @return a name for the state to be displayed on main menu
	 */
    public String getName();
    
    /**
     * For initializing the state, you can also start movement here
     */
    public void init();
    
    /**
     * Gets called once every program frame
     * @param elapsedTime the time since last call
     */
    public void update(int elapsedTime);
    
    /**
     * Clean up method, to reset sensor positions etc
     */
    public void reset();
    
    /**
     * This is used to determine if the state can be changed on detecting a line.
     * Return true if that event should stop your state.
     * Return false if detecting a line is expected during operation and should not trigger
     * a change in state.
     * 
     * @return true iff state can be changed when a line is detected
     */
    public boolean changeOnBarcode();
    
    /**
     * This is used when end of state should be determined by the state itself instead of 
     * barcode detection in main.
     * 
     * @return true iff state should be changed immediately. No further call to update() will be issued.
     */
    public boolean changeImmediately();
}
