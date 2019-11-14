import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class Project {
	//file reading variables
	private static final String FOLDERNAME = System.getProperty("user.dir") + "\\src\\tests";
	private static BufferedReader reader;
	
	//structures for storing information
	static ArrayList<Coordinate> locations = new ArrayList<Coordinate>();
	static ArrayList<Integer> perm = new ArrayList<Integer>();
	static Vector<Integer> bestPermutation = new Vector<Integer>();
	
	//variables for calculating distances
	static float bestCost = -1;
	static float currentCost = 0;
	static int firstIdentifier;
	static int secondIdentifier;
	static float x1;
	static float y1;
	static float x2;
	static float y2;
	static float radicand;
	
	//function for reading in file information
	public static void getFileContent(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		reader = new BufferedReader(new InputStreamReader(stream));
		
		String line = null;
		while (!(line = reader.readLine()).equals("NODE_COORD_SECTION")) {
			//ignore metadata
		}
		
		while ((line = reader.readLine()) != null) {
			//split each location line on spaces to separate information
			String[] components = line.split("\\s+");
			
			//store each location using the Coordinate class
			Coordinate coord = new Coordinate();
			coord.setIdentifier(Integer.parseInt(components[0]));
			coord.setXCoord(Float.parseFloat(components[1]));
			coord.setYCoord(Float.parseFloat(components[2]));
			
			//add each location to the locations ArrayList
			locations.add(coord);
		}
		
		reader.close();
	}
	
	//function for calculating the distance for the permutation passed in (perm3)
	static void calcCost(Integer[] perm3, int n) {
		for (int i = 0; i < (n - 1); i++) {
			//get two adjacent locations in the permutation, starting at the first location
			firstIdentifier = perm3[i];
			secondIdentifier = perm3[i + 1];
			
			//search for first location's information
			for (int j = 0; j < locations.size(); j++) {
				if (locations.get(j).getIdentifier() == firstIdentifier) {
					x1 = locations.get(j).getXCoord();
					y1 = locations.get(j).getYCoord();
					break;
				}
			}
			
			//search for second location's information
			for (int j = 0; j < locations.size(); j++) {
				if (locations.get(j).getIdentifier() == secondIdentifier) {
					x2 = locations.get(j).getXCoord();
					y2 = locations.get(j).getYCoord();
					break;
				}
			}
			
			//calculate the distance between the two adjacent locations
			radicand = ((x2 - x1)*(x2 - x1)) + ((y2 - y1)*(y2 - y1));
			//add that distance to the total distance of the permutation's path so far
			currentCost += Math.sqrt(radicand);
		}	
		
		//check if the current permutation is as good as the current best permutation
		if (bestCost == currentCost) {
			bestPermutation.addAll(Arrays.asList(perm3));
		}
		
		//check if best cost has not been set or the currentCost is better than the current best
		if (bestCost == -1 || currentCost < bestCost) {
			//set the new bestCost
			bestCost = currentCost;
			
			//clear the bestPermutation list and replace it with the new best permutation
			bestPermutation.removeAllElements();
			bestPermutation.addAll(Arrays.asList(perm3));
		}
		//reset currentCost once the permutation's total cost has been calculated
		currentCost = 0;
	}
	
	//function for printing arrays
	static void printArray(Integer[] perm2, int n) {
		for (int i = 0; i < n; i++) {
	        System.out.print(perm2[i] + " ");
		}
	    System.out.println();
	}
	 
	//function using Heap's Algorithm to generate permutations
	//source: http://www.geeksforgeeks.org/heaps-algorithm-for-generating-permutations/
	static void heapPermutation(Integer[] perm2, int size, int n, int first) {
		//if size == 1 permuation is finished
	    if (size == 1) {
		   Integer [] perm3 = new Integer[perm2.length + 2];
		   
		   //add first location to beginning of permuation
	       perm3[0] = first;
	       //add permutation found with Heap's algorithm in between start/end location
	       for (int x = 0; x < perm2.length; x++) {
	    	   perm3[x + 1] = perm2[x]; 
	       }
	       //add first location to end of permutation so path is a cycle
	       perm3[perm3.length - 1] = first;

	       //calculate cost of each permutation
	       calcCost(perm3, perm3.length);
	    }
	 
	    for (int i = 0; i < size; i++) {
	    	//recursive call to heap algorithm
	        heapPermutation(perm2, size-1, n, first);
	 
	        // if size is odd, swap first and last element, else swap ith and last
	        if (size % 2 == 1) {
	    	   int temp = perm2[0];
	           perm2[0] = perm2[size-1];
	           perm2[size-1] = temp;
	        } else {
	    	   int temp = perm2[i];
	           perm2[i] = perm2[size-1];
	           perm2[size-1] = temp;
	        }
	    }
	}
	
	public static void main(String[] args) throws IOException {
		//variable to keep track of first location in cycle
		int firstLocation = 0;
		
		//variables for calculating the time each file takes
		long startTime;
		long endTime;
		
		//set up variables for reading in file information
		File folder = new File(FOLDERNAME);
		File[] files = folder.listFiles();
		
		//for loop goes through every file in specified directory
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			
			//checks that the file is a .tsp file
			if (file.isFile() && file.getName().endsWith(".tsp")) {
				//print file name and start time
				System.out.println("File Name: " + file);
				startTime = System.currentTimeMillis();
				
				getFileContent(file);
				
				//create list of location identifiers to use for generating permutations
				for (int n = 0; n < locations.size(); n++) {
					if(n == 0) {
						//store first location but don't use when generating permutations
						firstLocation = locations.get(n).getIdentifier();
					} else {
						//store all other locations to be used for generating permutations
						perm.add(locations.get(n).getIdentifier());
					}
				}
				
				//generate permutations and calculate their costs
				Integer [] perm2 = perm.toArray(new Integer[perm.size()]);
				heapPermutation(perm2, perm2.length, perm2.length, firstLocation);
				
				//print best cost for each file
				System.out.println("BEST COST: " + bestCost);
				
				//print best permutation(s) for each file
				for (int j = 0; j < bestPermutation.size(); j++) {
					//separate permutations if more than one had the best cost
					if ((j % (locations.size() + 1) == 0) && j != 0) {
						System.out.println();
					}
					System.out.print(bestPermutation.get(j) + " ");
				}
				
				//clear lists and reset variables
				bestPermutation.removeAllElements();
				perm.clear();
				locations.clear();
				bestCost = -1;
				
				//print end time
				System.out.println();
				endTime = System.currentTimeMillis();
				System.out.println(endTime - startTime);
				System.out.println();
			}
		}
	}
}
