package state;

public interface ParcourState {
    public String getName();
    public void init();
    public void update(int elapsedTime);
    public void reset();
}
