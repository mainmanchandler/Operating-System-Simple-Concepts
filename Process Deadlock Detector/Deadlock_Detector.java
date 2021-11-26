/*
 -------------------------------------
 File:    Deadlock_Detector.java
 Project: Deadlock Detector

 File description
 simulation of process deadlocks
 -------------------------------------
 Author:  Chandler Mayberry
 Version  2021-11-09
 -------------------------------------
 */

//imports
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Iterator;


public class Deadlock_Detector {

    private static ArrayList<Integer> availableResources = new ArrayList<Integer>();
    private static ArrayList<Process> processTable = new ArrayList<Process>();

	//we need to get the input through standard input
	// we need to start with a loop that scans for user input
    // when empty line is entered the output is given and the program is terminated
	public static void main(String... args) {
        
        Scanner console = new Scanner(System.in);
        boolean resources = true;
        int numResources = 0;

        while (console.hasNextLine()) {
        	//System.out.println("location: entering the main while loop");
            String input = console.nextLine();
        	
            // if the input is null then we are done, terminate program
            if (input.equals(""))
                break;

            // traverse the first line to get the resources
            if (resources){
                String[] resourceData = input.split(" ");
                numResources = resourceData.length;

                for (int i = 0; i < resourceData.length; i++)
                    availableResources.add(Integer.parseInt(resourceData[i]));
                resources = false;
                
                //System.out.println(availableResources);
            }   
            // get the resource requests from processes
            else{

                String[] processRequests = input.split(" ");
                int processID = Integer.parseInt(processRequests[1]);
                int resourceRequested = Integer.parseInt(processRequests[4])-1; //subtract 1 to default index to 0 for arraylist

                //we need to check if the process is already made, if not, make it, if so, manipulate it
                Process currProcess = getProcessById(processID, processTable);
                if (currProcess == null){
                    currProcess = new Process(processID, numResources);
                    processTable.add(currProcess);
                    //System.out.println("new process created: " + (processID));
                }

                //manipulate the data in allocated or requested
                // (1) look at the processID of the process we are currently with, and see if there are available global resources for it to get
                // (2) if there are no available resources, then it will be a requested resource
                int resourceLeft = availableResources.get(resourceRequested);
                if (resourceLeft != 0){
                    //decrement the available resources, update the number of resources allocated
                    availableResources.set(resourceRequested, resourceLeft-1);

                    ArrayList<Integer> currProcessAllocated = currProcess.getAllocated();
                    currProcessAllocated.set(resourceRequested, currProcessAllocated.get(resourceRequested) + 1);
                    currProcess.setAllocated(currProcessAllocated);

                } else {
                    //update the number of resources requested
                    ArrayList<Integer> currProcessRequested = currProcess.getRequested();
                    currProcessRequested.set(resourceRequested, currProcessRequested.get(resourceRequested) + 1);
                    currProcess.setRequested(currProcessRequested);
                }
            }   
        }

        //NOW we need to evaluate the system and determine if there is a deadlock
        //go through each of the requested vectors, see if any of them are empty, if any empty, then no deadlock associated with that process
        ArrayList<Integer> completionOrder = new ArrayList<Integer>();
        boolean deadlock = false;
        
        //Adding this sorter that sorts my arraylist everytime to ensure that:
        //If more than one process can complete the execution at any time, print them in the
        //increasing order of process number.
        while(processTable.size() != 0 && deadlock == false){
        	            
          //print the output of all of the processes
			/*
			 * ArrayList<Process> temp = new ArrayList<Process>();
			 * while(processTable.isEmpty()==false){
			 * 
			 * int largest_ID_index = 0; int largest_ID = 0; int ID_size = 0; for(int i = 0;
			 * i < processTable.size(); i++){
			 * 
			 * Process currProcess = processTable.get(i); ID_size =
			 * currProcess.getProcessID();
			 * 
			 * if (ID_size > largest_ID){ largest_ID_index = i; largest_ID = ID_size; }
			 * 
			 * }
			 * 
			 * //add the largest id to the output list and remove it from the processTable
			 * temp.add(processTable.get(largest_ID_index));
			 * processTable.remove(largest_ID_index); }
			 * 
			 * processTable = temp;
			 */
        	
            Iterator<Process> processes = processTable.iterator();
            boolean empty = true;

            int initialNumProcesses = processTable.size();
            //System.out.println(initialNumProcesses);
            //System.out.println("Number of resources: " + availableResources.size());

            while(processes.hasNext()){
                Process currProcess = processes.next();
                ArrayList<Integer> currRequested = currProcess.getRequested();
                
                empty = true;
                for(int i = 0; i < currRequested.size(); i++)
                    if (currRequested.get(i) != 0)
                        empty = false;
                
                //!!!!!!WE NEED TO CHECK IF ITS EMPTY OOOOORRRR THERE ARE AVAILABLE RESOURCES TO RUN THE PROCESS AFTER DEALLOCATION!!!!!
                
                //if empty we can deallocate 
                if (empty){
                    completionOrder.add(currProcess.getProcessID()); //this process can complete and will release resources
                    //release all of the resources being used
                    for(int i = 0; i < currRequested.size(); i++) {
                        ArrayList<Integer> currAllocated = currProcess.getAllocated();
                        int newResourceAmount = availableResources.get(i) + currAllocated.get(i);
                        availableResources.set(i, newResourceAmount);
                    }

                    processes.remove();
                
                } else {
                    //!!!!!!WE NEED TO CHECK IF THERE ARE AVAILABLE RESOURCES TO RUN THE PROCESS AFTER DEALLOCATION!!!!!
                    int i = 0;
                    boolean notEnoughResources = false;
                    while(!notEnoughResources && i < currRequested.size()) {
                        if (availableResources.get(i) < currRequested.get(i))
                            notEnoughResources = true;
                        i += 1;
                    }

                    if (!notEnoughResources){
                        
                        completionOrder.add(currProcess.getProcessID()); //this process can complete and will release resources
                        //release all of the resources being used
                        for(int j = 0; j < currRequested.size(); j++) {
                            ArrayList<Integer> currAllocated = currProcess.getAllocated();
                            int newResourceAmount = availableResources.get(j) + currAllocated.get(j);
                            availableResources.set(j, newResourceAmount);
                        }
                        processes.remove();
                    }
                }
            }
			/*
			 * System.out.println(":::::::::::::::::::::::::::");
			 * 
			 * //print out all of the resource arrays for testing
			 * System.out.println("requested:"); for (int i = 0; i < processTable.size();
			 * i++){ Process currProcess = processTable.get(i);
			 * System.out.println(currProcess.getRequested()); }
			 * 
			 * System.out.print("allocated:"); for (int i = 0; i < processTable.size();
			 * i++){ Process currProcess = processTable.get(i);
			 * System.out.println(currProcess.getAllocated()); }
			 * 
			 * System.out.println("unallocated:"); System.out.println(availableResources);
			 * System.out.println(":::::::::::::::::::::::::::");
			 */

            int afterNumProcesses = processTable.size();
            deadlock = afterNumProcesses == initialNumProcesses;

            // System.out.println(deadlock);
        }

		/*
		 * System.out.println("::::::::::ENDNDNDNDNND:::::::::::::::::");
		 * 
		 * //print out all of the resource arrays for testing for (int i = 0; i <
		 * processTable.size(); i++){ Process currProcess = processTable.get(i);
		 * System.out.println(currProcess.getRequested()); }
		 * 
		 * for (int i = 0; i < processTable.size(); i++){ Process currProcess =
		 * processTable.get(i); System.out.println(currProcess.getAllocated()); }
		 * 
		 * System.out.println(availableResources);
		 * System.out.println(":::::::::::::::::::::::::::");
		 */
        
        //::::PRINT OUT DEADLOCKED OR NOT::::
        
        if (processTable.size() == 0){
            System.out.println("No deadlock, completion order");
            for (int i = 0; i < completionOrder.size(); i++){
                System.out.print(completionOrder.get(i) + " ");
            }
            
        } else {
            System.out.println("Deadlock, processes involved are");       
		    //print the output of all of the processes
	        for (int i = 0; i < processTable.size(); i++){
	        	System.out.print(processTable.get(i).processInfo() + " ");
	        }
        }

        //exit the program
        console.close();
    }

    /**
     * Get the Process object from the arraylist from its ID
     * Loop through the processTable and return if found
     * @return process or null if not found
     */
    public static Process getProcessById(int processId, ArrayList<Process> processTable) {
        
        for (int i = 0; i < processTable.size(); i++) {
            Process process = processTable.get(i);
            if (process.getProcessID() == processId){
                return process;
            }
        }
        
        return null;
    }


}

class Process{

    //for the information regarding each process, we need to allocate two arrays for them:
    //the resoucesAllocated (resources that are free in the system)
    //the resourcesRequested (resources that are being requested, currently held by other processes, or non-existent)
    private ArrayList<Integer> resourcesAllocated;
    private ArrayList<Integer> resourcesRequested;

    // Attributes
    private int processID;

    /**
     * Process object resource tracker constructor 
     * @param processsID   the id of the process
     */
    public Process(final int processID, final int numResources){
        this.processID = processID;
        this.resourcesAllocated = new ArrayList<Integer>(numResources);
        this.resourcesRequested = new ArrayList<Integer>(numResources);

        //fill the arraylists with 0 on initialization
        for (int i = 0; i < numResources; i++){
            resourcesAllocated.add(0);
            resourcesRequested.add(0);
        }

    }

    //:::GETTERS:::
    //return the allocated resources arraylist
    public ArrayList<Integer> getAllocated(){
        return resourcesAllocated;
    }
    
    //return the requested resources arraylist
    public ArrayList<Integer> getRequested(){
        return resourcesRequested;
    }

    /**
     * Getter for process ID name
     */
    public int getProcessID() {
        return processID;
    }


    //:::SETTERS:::
    //set the allocated resources arraylist to new values
    public void setAllocated(ArrayList<Integer> resourcesAllocated){
        this.resourcesAllocated = resourcesAllocated;
    }
    
    //set the requested resources arraylist to new values
    public void setRequested(ArrayList<Integer> resourcesRequested){
        this.resourcesRequested = resourcesRequested;
    }


    /**
     * Create a string representation of the object
     * @return formatted object string
     */
    public String processInfo() {
        String processData = "" + processID;
        return processData;
    }
    
}