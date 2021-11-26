/*
 -------------------------------------
 File:    Process_Dispatcher.java
 Project: Process Dispatcher Simulation
 
 File description
 simulation of simple dispatcher program
 -------------------------------------
 Author:  Chandler Mayberry
 Version  2021-10-13
 -------------------------------------
 */

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;

//Your system consists of process table, ready queue and 3 lists of blocked processes plus some global variables (such as running process). 
//Processing one line will result in some changes in your system (such as creation of new process and 
//adding it to ready queue, moving running process to ready queue and making another process running ... and so on).

public class Process_Dispatcher {
    private static ArrayList<Process> processTable = new ArrayList<Process>();
    private static ArrayList<Process> readyQueue = new ArrayList<Process>();
    private static ArrayList<Process> resource1 = new ArrayList<Process>();
    private static ArrayList<Process> resource2 = new ArrayList<Process>();
    private static ArrayList<Process> resource3 = new ArrayList<Process>();
   
    // we need to start with a loop that scans for user input
    // when empty line is entered the output is given and the program is terminated
    public static void main(String... args) {
        
        Scanner console = new Scanner(System.in);
       
    	int timePassed = 0;
    	
        //create process 0 and put it in the running queue before any operations are made
        Integer zeroId = 0;
        createEvent(zeroId);

        while (console.hasNextLine()) {
        	//System.out.println("location: entering the main while loop");
            String input = console.nextLine();
        	
            // if the input is null then we are done, terminate program
            if (input.equals(""))
                break;

            // traverse string, get input variables
            //String[] eventData = eventInputTraverser(input);
            String[] eventData = input.split(" ");
            int currentTime = Integer.parseInt(eventData[0]);
            String eventAction = eventData[1].toUpperCase();
            int arg1 = 0;
            int arg2 = 0;
            
            //Update all the processes state times, then change states
            for(int i = 0; i < processTable.size(); i++){
                Process currProcess = processTable.get(i);
                currProcess.updateTimePassed(currentTime - timePassed);
            }

            //we need to keep track of the time that has passed
            timePassed = currentTime;
            	
            // Perform system event, put processes into new states, create new ones, or terminate current ones
            switch (eventAction) {
                case "C": // create process: <time> C <processID>
                	arg1 = Integer.parseInt(eventData[2]);
                	

                    createEvent(arg1);
                    break;
                case "E": // process exits completely: <time> E <process>
                	arg1 = Integer.parseInt(eventData[2]);

                    exitEvent(arg1);
                    break;
                case "R": // process requests resource: <time> R <resource> <process>
                	arg1 = Integer.parseInt(eventData[2]);
                	arg2 = Integer.parseInt(eventData[3]);

                    requestEvent(arg2, arg1);
                    break;
                case "I": // process interrupt: <time> I <resource> <process>
                	arg1 = Integer.parseInt(eventData[2]);
                	arg2 = Integer.parseInt(eventData[3]);

                    interruptEvent(arg2, arg1);
                    break;
                case "T": // process timer interrupt: <time> T
                    timerInterruptEvent();
                    break;
            }

        }
        
        
        //print the output of all of the processes
        ArrayList<Process> outputList = new ArrayList<Process>();
        while(processTable.isEmpty()==false){

            int largest_ID_index = 0;
            int largest_ID = 0;
            int ID_size = 0;
            for(int i = 0; i < processTable.size(); i++){
         
               
            	Process currProcess = processTable.get(i);
                ID_size = currProcess.getProcessID();
                
            	//System.out.println("process Id: " + Integer.toString(currProcess.getProcessID()));

                
                if (ID_size > largest_ID){
                	//System.out.println("I am the largest right now" + Integer.toString(currProcess.getProcessID()));
                    largest_ID_index = i;
                    largest_ID = ID_size;
                }
            	//System.out.println("----------------------- done search iteration -----------------------" );

            }
            
            //add the largest id to the output list and remove it from the processTable
            outputList.add(processTable.get(largest_ID_index));
            processTable.remove(largest_ID_index);
        }
        
        //System.out.println("location: printing output");
        //print all of the processes in ascending order 
        String output = "";
        for(int i = outputList.size()-1; i >= 0; i--){
        	
        	
        	//print the zero process first (less data)
        	if(i == outputList.size()-1) {
        		Process currProcess = outputList.get(i);
	            output = currProcess.zeroProcessInfo();
	            System.out.println(output);
        	} else {
	            Process currProcess = outputList.get(i);
	            output = currProcess.processInfo();
	            System.out.println(output);
        	}
        }

        //exit program
        console.close();
    }

    /**
     * ---------------------------------------------------------------------------------------------------------
     * :::INPUT AND UPDATING STATES METHODS:::
     * ---------------------------------------------------------------------------------------------------------
     */

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

    

    /**
     * ---------------------------------------------------------------------------------------------------------
     * :::POSSIBLE EVENTS:::
     * ---------------------------------------------------------------------------------------------------------
     */

    /**
     * C: Process is created and added to the arraylist ready queue
     */
    public static void createEvent(int processId) {
    	
    	//creation of process zero condition:
    	if(processTable.size() == 0) {
    		Process zeroProcess = new Process(processId, 0,0,0, "running");
            processTable.add(zeroProcess); //keeps track of all processes
            //System.out.println("location: creation of the zero process");
            return;
    	}
    	
    	//if the Process 0 is running then we want to stop it from running
    	//and put the new process in the running state
        Process zero = processTable.get(0);
        if(zero.getState() == "running"){
            zero.setState("terminate");
            Process newProcess = new Process(processId, 0,0,0, "running");
            processTable.add(newProcess); //keeps track of all processes            
        } else {
            Process newProcess = new Process(processId, 0,0,0, "ready");
            readyQueue.add(newProcess); //this adds to the end of the list
            processTable.add(newProcess); //keeps track of all processes
        }
    
    }

    /**
     * E: Process exits the system, no more updates (change state to terminated for no more updates)
     * Conditition: ONLY running processes can exit
     */
    public static void exitEvent(int processId) {
        //find the process in the processTable and make its state terminated
        Process exitingProcess = getProcessById(processId, processTable);

        if (exitingProcess != null) 
            exitingProcess.setState("terminated");        
              
        //set new running process
        //if the readyQueue is empty then we can put put process 0 back into running state
        if(readyQueue.isEmpty()){
            Process processZero = processTable.get(0);
            processZero.setState("running");
        
        } else { //put the next item in the queue to running state
            Process newRunningProcess = readyQueue.get(0);
            readyQueue.remove(0);
            newRunningProcess.setState("running");
            
            //just in case process 0 is running reset it
            Process processZero = processTable.get(0);
            processZero.setState("terminated");
        }
    }

    /**
     * R: Process requests to use a resource and gets put into "blocked" state 
     */
    public static void requestEvent(int processId, int resourceId) {
        
        //System.out.println("location: R Checking the processID input " + Integer.toString(processId));
        //System.out.println("location: R Checking the resourceID input " + Integer.toString(resourceId));

        

            Process blockingProcess = getProcessById(processId, processTable);
            
            //System.out.println("location: checking state1 " + Integer.toString(processId) + " " + blockingProcess.getState());
            if(blockingProcess.getState() == "running"){
                blockingProcess.setState("blocked");
                //System.out.println("location: checking state2 " + blockingProcess.getState());

                if(resourceId == 1)
                    resource1.add(blockingProcess);
                else if(resourceId == 2)
                    resource2.add(blockingProcess);
                else if(resourceId == 3)
                    resource3.add(blockingProcess);
            
        }
        
        //for(int i = 0; i < readyQueue.size(); i++)
        //{
        //    System.out.println("Location: R before setting new running process: " + readyQueue.get(i).getProcessID());
        //} 
        
        //need to put next process in the ready queue into running:
        if(readyQueue.isEmpty()) {
        	Process zero = processTable.get(0);
        	zero.setState("running");
		}
		else {
			Process nextProcess = readyQueue.get(0);
			readyQueue.remove(0);
			nextProcess.setState("running");
		}
        
        
        //for(int i = 0; i < readyQueue.size(); i++)
        //{
        //    System.out.println("Location: R after setting new running process: " + readyQueue.get(i).getProcessID());
        //} 
        
    }

    /**
     * I: Iterrupt Process inside of resource. Gets taken out of blocked states and put in ready state:
     */
    public static void interruptEvent(int processId, int resourceId) {
        Process unblockingProcess = getProcessById(processId, processTable);

        //if the process that is running is 0 
        boolean processZeroRunning = false;
        if(readyQueue.isEmpty())
            processZeroRunning = true;


        //remove from resource block and put back in ready queue
        if(resourceId == 1){
            resource1.remove(unblockingProcess);
            readyQueue.add(unblockingProcess);
            unblockingProcess.setState("ready");
        }
        else if(resourceId == 2){
            resource2.remove(unblockingProcess);
            readyQueue.add(unblockingProcess);
            unblockingProcess.setState("ready");
        }
        else if(resourceId == 3){
            resource3.remove(unblockingProcess);
            readyQueue.add(unblockingProcess);
            unblockingProcess.setState("ready");
        }

        //if process 0 was running, then remove unblocked process from ready queue and put in running state
        if(processZeroRunning){
        	
        	//reset 0 process, then set the now ready process to running
        	Process zero = processTable.get(0);
        	zero.setState("terminated");
        	
            Process newRunningProcess = readyQueue.get(0);
            readyQueue.remove(0);
            newRunningProcess.setState("running");
        }
        
    }

    /**
     * T: Timer iterrupt, process gets taken out of the running state and put back in the readyQueue
     */
    public static void timerInterruptEvent() {

        boolean stop = false;
        
        //loop through all of the processes in the process table to find the running process and set it to ready
        Iterator<Process> processTableIterator = processTable.iterator();
        while(processTableIterator.hasNext() && stop == false){
            
            Process runningProcess = processTableIterator.next();
            
            //if we found the running process then we can stop
            if(runningProcess.getState() == "running"){
                runningProcess.setState("ready");
                readyQueue.add(runningProcess);
                stop = true;

          
            }

        }
        
      //if the readyQueue is empty then we can put put process 0 back into running state
        if(readyQueue.isEmpty()){
            Process processZero = processTable.get(0);
            processZero.setState("running");
        
        } else { //put the next item in the queue to running state
            Process newRunningProcess = readyQueue.get(0);
            readyQueue.remove(0);
            newRunningProcess.setState("running");
        }
        
    }
    
}


/**
 * ---------------------------------------------------------------------------------------------------------
 * :::PROCESS OBJECT CLASS:::
 * ---------------------------------------------------------------------------------------------------------
 */
class Process {

    // Attributes
    private int processID;
    private int totalTimeRunning;
    private int totalTimeReady;
    private int totalTimeBlocked;
    private String currentState;
    ;

    /**
     * Process constructor, used to create new process object
     * 
     * @param processID        process ID
     * @param totalTimeRunning total time running state
     * @param totalTimeReady   total time ready state
     * @param totalTimeBlocked total time blocked state
     * @param currentState the current state the process is in
     */
    public Process(final int processID, int totalTimeRunning, int totalTimeReady, int totalTimeBlocked, String currentState) {
        this.processID = processID;
        this.totalTimeRunning = totalTimeRunning;
        this.totalTimeReady = totalTimeReady;
        this.totalTimeBlocked = totalTimeBlocked;
        this.currentState = currentState;
    }

    /**
     * Set the state of the process
     */
    public void setState(String currentState) {
        this.currentState = currentState;
    }

    /**
     * Get the state of the process
     */
    public String getState() {
        return currentState;
    }

    /**
     * Getter for process ID name
     * @return
     */
    public int getProcessID() {
        return processID;
    }


    /**
     * Updates the times of all of the processes that are not terminated, based on their current state
     */
    public void updateTimePassed(int timePassed) {
        if(currentState == "running"){
            this.totalTimeRunning += timePassed;

        } else if(currentState == "ready"){
            this.totalTimeReady += timePassed;

        } else if(currentState == "blocked"){
            this.totalTimeBlocked += timePassed;
        }
        //else, if state is terminated then we dont have to add anything
    }


    /**
     * Create a string representation of the object
     * @return formatted object string
     */
    public String processInfo() {
        String processData = processID + " " + totalTimeRunning + " " + totalTimeReady + " " + totalTimeBlocked;
        return processData;
    }
    
    /**
     * Create a string representation of the object
     * @return formatted object string
     */
    public String zeroProcessInfo() {
        String processData = processID + " " + totalTimeRunning;
        return processData;
    }

}