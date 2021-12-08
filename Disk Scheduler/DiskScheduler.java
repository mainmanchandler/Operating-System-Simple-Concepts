/*
 -------------------------------------
 File:    DiskScheduler.java
 file description
 Simulation of Disk Scheduler for FCFS, SSTF, LOOK, CLOOK
 -------------------------------------
 Author:  Chandler Mayberry
 Version  2021-12-04
 -------------------------------------
 */

//imports
import java.util.ArrayList;
import java.util.Scanner;


public class DiskScheduler {

	// we need to get the input through standard input
	// we need to start with a loop that scans for user input
    // when empty line is entered the output is given and the program is terminated


	public static void main(String... args) {
        
        Scanner console = new Scanner(System.in);

        
        while (console.hasNextLine()) {
            String input = console.nextLine();
        	
            // if the input is null then we are done, terminate program
            if (input.equals(""))
                break;
            
            ArrayList<Integer> requestedTracks = new ArrayList<Integer>();
            
            // we need to get variables for k, ct, direction, tNi
            String[] inputData = input.split(" ");
            int k = Integer.parseInt(inputData[0]);
            int ct = Integer.parseInt(inputData[1]);
            String dir = inputData[2];
            
            for (int i = 3; i < inputData.length; i++){
                requestedTracks.add(Integer.parseInt(inputData[i]));
            }

            //System.out.println("k: " + k);
            //System.out.println("dt: " + ct);
            //System.out.println("dir: " + dir);
            //System.out.println("requestedTracks: " + requestedTracks);

            //Sending the variables and array of requests:
            //FCFS Call
            String fcfs = FCFS(k, ct, dir, requestedTracks);
            
            //SSTF Call
            String sstf = SSTF(k, ct, dir, requestedTracks);

            //LOOK Call
            String look = LOOK(k, ct, dir, requestedTracks);

            //C-LOOK Call
            String clook = CLOOK(k, ct, dir, requestedTracks);

           
            //Printout all of the output
            System.out.println(fcfs);
            System.out.println(sstf);
            System.out.println(look);
            System.out.println(clook);


        }

       
        

        //exit the program
        console.close();
    }
	
	
	/**
     * ---------------------------------------------------------------------------------------------------------
     * :::SCHEDULING ALGORITHMS:::
     * ---------------------------------------------------------------------------------------------------------
     */
	
	
   /**
    * Using the FCFS scheduling algorithm, calculate the total head movement of the harddrive
    * @return the output for FCFS scheduling
    */
    public static String FCFS(int k, int ct, String dir, ArrayList<Integer> requestedTracks)  {
        
        int currTrack = ct;
        int headMovement = 0;
        int totalHeadMovement = 0;
        String order = "";
        for(int i = 0; i < k; i++){
            headMovement = Math.abs(currTrack - requestedTracks.get(i));
            currTrack = requestedTracks.get(i);
            order += requestedTracks.get(i) + " ";
            totalHeadMovement += headMovement;
        }

        return "FCFS:	" + order + "Total Head Movement: " + totalHeadMovement;
    }

   /**
    * Using the SSTF scheduling algorithm, calculate the total head movement of the harddrive
    * @return the output for SSTF scheduling
    */
    public static String SSTF(int k, int ct, String dir, ArrayList<Integer> requestedTracks)  {
        
        int currTrack = ct;
        int headMovement = 0;
        int totalHeadMovement = 0;
        String order = "";

        //make a local copy of the global arraylist
        ArrayList<Integer> requestedTracksCopy = new ArrayList<>(requestedTracks);
        
        int minIndex = 0;
        int previousMinValue = 0;
        while(requestedTracksCopy.isEmpty() == false){
        	
        	int minValue = 1024;  //max interval for tracks is 0-1023
        	
            //find the next shortest next
            for (int i = 0; i < requestedTracksCopy.size(); i++){
                if (Math.abs(currTrack - requestedTracksCopy.get(i)) < minValue){
                    minValue = Math.abs(currTrack - requestedTracksCopy.get(i));
                    minIndex = i;
                }
            }
            
            //System.out.println("Current Track: " + currTrack);
        	//System.out.println("Requested Tracks: " + requestedTracksCopy);
        	//System.out.println("Min Value: " + minValue);
        	//System.out.println("Min Index: " + minIndex);
        	//System.out.println("order: " + order + '\n');	
            
            //If the same value is seen twice consecutively, then we don't want to add it
            if (minValue == previousMinValue) {
            	headMovement = minValue;
                currTrack = requestedTracksCopy.get(minIndex);
                order += requestedTracksCopy.get(minIndex) + " ";
                requestedTracksCopy.remove(minIndex);
            } else {
            	headMovement = minValue;
                currTrack = requestedTracksCopy.get(minIndex);
                order += requestedTracksCopy.get(minIndex) + " ";
                requestedTracksCopy.remove(minIndex);
                totalHeadMovement += headMovement;
            }
            


        }

        return "SSTF:	" + order + "Total Head Movement: " + totalHeadMovement;
        
    }

   /**
    * Using the LOOK scheduling algorithm, calculate the total head movement of the harddrive
    * @return the output for LOOK scheduling
    */
    public static String LOOK(int k, int ct, String dir, ArrayList<Integer> requestedTracks)  {
        
    	//make a local copy of the global arraylist
        ArrayList<Integer> requestedTracksCopy = new ArrayList<>(requestedTracks);
        ArrayList<Integer> requestedTracksSorted = new ArrayList<>();

        //first we want to sort the arraylist in ascending order    	
        int minIndex = 0;
        while(requestedTracksCopy.isEmpty() == false){

        	int minValue = 1024;  //max interval for tracks is 0-1023
        	
            //find the next shortest next
            for (int i = 0; i < requestedTracksCopy.size(); i++){
                if (requestedTracksCopy.get(i) < minValue){
                    minValue = requestedTracksCopy.get(i);
                    minIndex = i;
                }
            }
        	
            requestedTracksSorted.add(requestedTracksCopy.get(minIndex));
            requestedTracksCopy.remove(minIndex);
        }
    	
        //System.out.println("Requested Tracks: " + requestedTracksSorted);
        

        int currTrack = ct;
        int headMovement = 0;
        int totalHeadMovement = 0;
        String order = "";
        
        //now we need to find our starting point, work left/right
        //if up, move right through the list and then back right
        //if down, opposite
    

        if(dir.equals("up")){

            //find the starting position in the arraylist
            boolean start = false;
            int startIndex = 0;
            int i = 0;
            while(start==false){
                if (i == requestedTracksSorted.size() || currTrack <= requestedTracksSorted.get(i)){
                    start = true;
                    startIndex = i;
                }
                i += 1;
            }

            //System.out.println("Current Track: " + currTrack);
            //System.out.println("start index: " + startIndex);

            //now run through the arraylist to the right, then back through the left
            for(int j = startIndex; j < requestedTracksSorted.size(); j++){
                headMovement = Math.abs(currTrack - requestedTracksSorted.get(j));
                currTrack = requestedTracksSorted.get(j);
                order += requestedTracksSorted.get(j) + " ";
                totalHeadMovement += headMovement;
            }

            for(int j = startIndex-1; j >= 0; j--){
                headMovement = Math.abs(currTrack - requestedTracksSorted.get(j));
                currTrack = requestedTracksSorted.get(j);
                order += requestedTracksSorted.get(j) + " ";
                totalHeadMovement += headMovement;
            }


        }else{

            //find the starting position in the arraylist
            boolean start = false;
            int startIndex = 0;
            int i = 0;
            while(start==false){
                if (i == requestedTracksSorted.size() || currTrack <= requestedTracksSorted.get(i)){
                    start = true;
                    startIndex = i;
                }
                i += 1;
            }


            //now run through the arraylist to the left, then back through the right
            
            for(int j = startIndex-1; j >= 0; j--){
                headMovement = Math.abs(currTrack - requestedTracksSorted.get(j));
                currTrack = requestedTracksSorted.get(j);
                order += requestedTracksSorted.get(j) + " ";
                totalHeadMovement += headMovement;
            }

            for(int j = startIndex; j < requestedTracksSorted.size(); j++){
                headMovement = Math.abs(currTrack - requestedTracksSorted.get(j));
                currTrack = requestedTracksSorted.get(j);
                order += requestedTracksSorted.get(j) + " ";
                totalHeadMovement += headMovement;
            }

        }
        	
        return "LOOK:	" + order + "Total Head Movement: " + totalHeadMovement;

    }

    
	/**
	 * Using the CLOOK scheduling algorithm, calculate the total head movement of the harddrive
	 * @return the output for CLOOK scheduling
     */
	public static String CLOOK(int k, int ct, String dir, ArrayList<Integer> requestedTracks)  {
	    
		//first we want to sort the arraylist in ascending order
	    
		//make a local copy of the global arraylist
        ArrayList<Integer> requestedTracksCopy = new ArrayList<>(requestedTracks);
        ArrayList<Integer> requestedTracksSorted = new ArrayList<>();

        //first we want to sort the arraylist in ascending order    	
        int minIndex = 0;
        while(requestedTracksCopy.isEmpty() == false){

        	int minValue = 1024;  //max interval for tracks is 0-1023
        	
            //find the next shortest next
            for (int i = 0; i < requestedTracksCopy.size(); i++){
                if (requestedTracksCopy.get(i) < minValue){
                    minValue = requestedTracksCopy.get(i);
                    minIndex = i;
                }
            }
        	
            requestedTracksSorted.add(requestedTracksCopy.get(minIndex));
            requestedTracksCopy.remove(minIndex);
        }
    	
        //System.out.println("Requested Tracks: " + requestedTracksSorted);
        

        int currTrack = ct;
        int headMovement = 0;
        int totalHeadMovement = 0;
        String order = "";
        
        //now we need to find our starting point, work left/right
        //if up, move right through the list and then start from beginning of arraylist
        //if down, start at beginning of array list
		
        if(dir.equals("up")){

            //find the starting position in the arraylist
            boolean start = false;
            int startIndex = 0;
            int i = 0;
            while(start==false){
                if (i == requestedTracksSorted.size() || currTrack < requestedTracksSorted.get(i)){
                    start = true;
                    startIndex = i;
                }
                i += 1;
            }

            //System.out.println("Current Track: " + currTrack);
            //System.out.println("start index: " + startIndex);

            //now run through the arraylist to the right, then back through the left
            for(int j = startIndex; j < requestedTracksSorted.size(); j++){
                headMovement = Math.abs(currTrack - requestedTracksSorted.get(j));
                currTrack = requestedTracksSorted.get(j);
                order += requestedTracksSorted.get(j) + " ";
                totalHeadMovement += headMovement;
            }

            for(int j = 0; j <= startIndex - 1; j++){
                headMovement = Math.abs(currTrack - requestedTracksSorted.get(j));
                currTrack = requestedTracksSorted.get(j);
                order += requestedTracksSorted.get(j) + " ";
                totalHeadMovement += headMovement;
            }


        }else{

         
            //now run through the arraylist to the left, then back through the right
            
            for(int j = 0; j < requestedTracksSorted.size(); j++){
                headMovement = Math.abs(currTrack - requestedTracksSorted.get(j));
                currTrack = requestedTracksSorted.get(j);
                order += requestedTracksSorted.get(j) + " ";
                totalHeadMovement += headMovement;
            }


        } 
		
		
	    return "C-LOOK: " + order + "Total Head Movement: " + totalHeadMovement;
	}
	   
	
}













