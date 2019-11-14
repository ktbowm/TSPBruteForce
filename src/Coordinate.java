//class for storing location information
public class Coordinate {
	//class variables
	int identifier;
	float xCoord;
	float yCoord;
	
	//setters
	public void setIdentifier(int id) {
		identifier = id;
	}
	
	public void setXCoord(float x) {
		xCoord = x;
	}
	
	public void setYCoord(float y) {
		yCoord = y;
	}
	
	//getters
	public int getIdentifier() {
		return identifier;
	}
	
	public float getXCoord() {
		return xCoord;
	}
	
	public float getYCoord() {
		return yCoord;
	}
	
	//printing functions
	public void printIdentifier() {
		System.out.println(identifier);
	}
	
	public void printXCoord() {
		System.out.println(xCoord);
	}
	
	public void printYCoord() {
		System.out.println(yCoord);
	}
}
