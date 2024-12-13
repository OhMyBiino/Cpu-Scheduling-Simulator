
package MainProgram;

import java.util.*;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int waitingTime;
    int turnaroundTime;
    int completionTime;

    Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime; // Initially, remaining time equals burst time
    }
}

public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display menu
            System.out.println("\n--- CPU Scheduling Menu ---");
            System.out.println("1. First-Come, First-Served (FCFS)");
            System.out.println("2. Shortest Job First (SJF)");
            System.out.println("3. Round-Robin (RR)");
            System.out.println("4. Priority Scheduling");
            System.out.println("5. Shortest Remaining Time First (SRTF)");
            System.out.println("6. Exit");
            System.out.print("Enter the number of the scheduling type you'd like to simulate: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("6")) {
                System.out.println("Exiting program. Goodbye!");
                break;
            }

            // Get processes from the user
            List<Process> processes = getProcesses(scanner);

            // Perform the selected scheduling simulation
            switch (choice) {
                case "1":
                    System.out.println("You selected First-Come, First-Served (FCFS).");
                    simulateFCFS(processes);
                    break;
                case "2":
                    System.out.println("You selected Shortest Job First (SJF).");
                    simulateSJF(processes);
                    break;
                case "3":
                    System.out.println("You selected Round-Robin (RR).");
                    simulateRR(processes, scanner);
                    break;
                case "4":
                    System.out.println("You selected Priority Scheduling.");
                    simulatePriority(processes, scanner);
                    break;
                case "5":
                    System.out.println("You selected Shortest Remaining Time First (SRTF).");
                    simulateSRTF(processes);
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
                    break;
            }
        }

        scanner.close();
    }

    // Helper to get process input
    static List<Process> getProcesses(Scanner scanner) {
        List<Process> processes = new ArrayList<>();
        System.out.print("Enter the number of processes: ");
        int processCount = scanner.nextInt();
        scanner.nextLine();  // Consume the leftover newline

        for (int i = 0; i < processCount; i++) {
            System.out.print("Enter the name of process " + (i + 1) + ": ");
            String name = scanner.nextLine();
            System.out.print("Enter the arrival time of process " + name + ": ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Enter the burst time of process " + name + ": ");
            int burstTime = scanner.nextInt();
            scanner.nextLine();  // Consume the leftover newline
            processes.add(new Process(name, arrivalTime, burstTime));
        }
        return processes;
    }

    // FCFS Scheduling
    static void simulateFCFS(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime)); // Sort by arrival time
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        System.out.println("\n--- FCFS Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        for (Process process : processes) {
            currentTime = Math.max(currentTime, process.arrivalTime); // Account for idle time
            process.completionTime = currentTime + process.burstTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.burstTime;

            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;

            System.out.printf("%-10s%-15d%-15d%-15d%-15d%-15d\n",
                              process.name, process.arrivalTime, process.burstTime,
                              process.completionTime, process.turnaroundTime, process.waitingTime);

            currentTime += process.burstTime;
        }
        // Print the summary of total waiting time and average waiting time
        printSummary(totalWaitingTime, processes.size());
    }

    // SJF Scheduling
    static void simulateSJF(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.burstTime)); // Sort by burst time
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        System.out.println("\n--- SJF Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        for (Process process : processes) {
            currentTime = Math.max(currentTime, process.arrivalTime); // Account for idle time
            process.completionTime = currentTime + process.burstTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.burstTime;

            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;

            System.out.printf("%-10s%-15d%-15d%-15d%-15d%-15d\n",
                              process.name, process.arrivalTime, process.burstTime,
                              process.completionTime, process.turnaroundTime, process.waitingTime);

            currentTime += process.burstTime;
        }

        printSummary(totalWaitingTime, processes.size());
    }

    // Round-Robin Scheduling
    static void simulateRR(List<Process> processes, Scanner scanner) {
        System.out.print("Enter the time quantum: ");
        int timeQuantum = scanner.nextInt();
        scanner.nextLine();  // Consume the newline

        // Clone the original processes to preserve their initial burst times for accurate calculations
        List<Process> originalProcesses = new ArrayList<>();
        for (Process p : processes) {
            originalProcesses.add(new Process(p.name, p.arrivalTime, p.burstTime)); // Store original burst time
        }

        Queue<Process> queue = new LinkedList<>(processes);
        int currentTime = 0;
        int totalWaitingTime = 0;

        System.out.println("\n--- Round-Robin Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        while (!queue.isEmpty()) {
            Process process = queue.poll();
            int originalBurstTime = originalProcesses.stream()
                                                      .filter(p -> p.name.equals(process.name))
                                                      .findFirst()
                                                      .get()
                                                      .burstTime;

            // Calculate the time slice (time quantum or remaining burst time)
            int timeSlice = Math.min(process.burstTime, timeQuantum);
            process.burstTime -= timeSlice; // Deduct the time slice from the remaining burst time
            currentTime += timeSlice; // Update current time

            if (process.burstTime > 0) {
                queue.add(process); // Re-add process to queue if it hasn't finished
            } else {
                // Calculate completion time, turnaround time, and waiting time
                process.completionTime = currentTime;
                process.turnaroundTime = process.completionTime - process.arrivalTime;
                process.waitingTime = process.turnaroundTime - originalBurstTime; // Correct calculation of waiting time

                totalWaitingTime += process.waitingTime;

                // Print the process details
                System.out.printf("%-10s%-15d%-15d%-15d%-15d%-15d\n",
                                  process.name, process.arrivalTime, originalBurstTime, // Use original burst time
                                  process.completionTime, process.turnaroundTime, process.waitingTime);
            }
        }

        // Print the summary of total waiting time and average waiting time
        printSummary(totalWaitingTime, processes.size());
    }

//    static void simulatePriority(List<Process> processes, Scanner scanner) {
//        Map<String, Integer> priorities = new HashMap<>();
//        System.out.println("Enter the priorities for each process (lower number = higher priority):");
//        
//        // Get the priorities from the user
//        for (Process process : processes) {
//            System.out.print("Priority for " + process.name + ": ");
//            int priority = scanner.nextInt();
//            priorities.put(process.name, priority);
//        }
//
//        // Sort the processes by priority (ascending order)
//        processes.sort(Comparator.comparingInt(p -> priorities.get(p.name)));
//        
//        // FCFS simulation after sorting by priority
//        simulateFCFS(processes);
//    }
    
    static void simulatePriority(List<Process> processes, Scanner scanner) {
        Map<String, Integer> priorities = new HashMap<>();
        System.out.println("Enter the priorities for each process (lower number = higher priority):");
        
        // Get the priority for each process from the user
        for (Process process : processes) {
            System.out.print("Priority for " + process.name + ": ");
            priorities.put(process.name, scanner.nextInt());
        }

        // Sort processes based on priority values (lower number means higher priority)
        processes.sort(Comparator.comparingInt(p -> priorities.get(p.name)));

        // Now that the processes are sorted by priority, proceed with FCFS or other scheduling logic
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        System.out.println("\n--- Priority Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turnaround Time", "Waiting Time");

        // Process each process in the sorted order
        for (Process process : processes) {
            // Handle completion time, turnaround time, and waiting time
            currentTime = Math.max(currentTime, process.arrivalTime); // Account for idle time
            process.completionTime = currentTime + process.burstTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.burstTime;

            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;

            System.out.printf("%-10s%-15d%-15d%-15d%-15d%-15d\n",
                              process.name, process.arrivalTime, process.burstTime,
                              process.completionTime, process.turnaroundTime, process.waitingTime);

            currentTime += process.burstTime;
        }

        // Print summary of the scheduling simulation
        printSummary(totalWaitingTime, processes.size());
    }

    static void simulateSRTF(List<Process> processes) {
        // Initialize remaining time for each process
        for (Process process : processes) {
            process.remainingTime = process.burstTime;
        }

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime)); // Sort by arrival time
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        System.out.println("\n--- SRTF Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n",
                "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        List<Process> readyQueue = new ArrayList<>();
        int completedProcesses = 0;

        while (completedProcesses < processes.size()) {
            // Add processes that have arrived by currentTime
            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && !readyQueue.contains(process) && process.remainingTime > 0) {
                    readyQueue.add(process);
                }
            }

            // If there are processes to execute
            if (!readyQueue.isEmpty()) {
                // Select the process with the shortest remaining time
                readyQueue.sort(Comparator.comparingInt(p -> p.remainingTime));
                Process currentProcess = readyQueue.get(0);

                // Execute the process for 1 unit of time
                currentProcess.remainingTime--;
                currentTime++;

                // If the process is completed
                if (currentProcess.remainingTime == 0) {
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    totalWaitingTime += currentProcess.waitingTime;
                    totalTurnaroundTime += currentProcess.turnaroundTime;

                    // Remove from ready queue
                    readyQueue.remove(currentProcess);
                    completedProcesses++;

                    // Print process details
                    System.out.printf("%-10s%-15d%-15d%-15d%-15d%-15d\n",
                            currentProcess.name, currentProcess.arrivalTime,
                            currentProcess.burstTime, currentProcess.completionTime,
                            currentProcess.turnaroundTime, currentProcess.waitingTime);
                }
            } else {
                // If no process is ready, advance time
                currentTime++;
            }
        }

        printSummary(totalWaitingTime, processes.size());
    }



    // Utility function to print summary
    static void printSummary(int totalWaitingTime, int processCount) {
        System.out.println("\n--- Summary ---");
        double averageWaitingTime = (double) totalWaitingTime / processCount;
        System.out.printf("Average Waiting Time: %.2f\n", averageWaitingTime);
    }
}
