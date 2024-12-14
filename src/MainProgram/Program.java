
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
    
    static void printGanttChart(List<String> ganttChart) {
        System.out.println("\n--- Gantt Chart ---");

        // Print the timeline
        StringBuilder timeline = new StringBuilder();
        StringBuilder bar = new StringBuilder();

        for (String segment : ganttChart) {
            String[] parts = segment.split(" "); // Extract process and time range
            String range = parts[0]; // e.g., "[0-5]"
            String process = parts[1]; // e.g., "P1"

            // Add the process name and its timeline
            bar.append("| ").append(process).append(" ");
            timeline.append(range).append(" ");
        }
        bar.append("|"); // Close the final process bar

        // Print the Gantt chart
        System.out.println(bar.toString());
        System.out.println(timeline.toString());
    }


    // FCFS Scheduling
    static void simulateFCFS(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime)); // Sort by arrival time
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        List<String> ganttChart = new ArrayList<>();

        System.out.println("\n--- FCFS Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        for (Process process : processes) {
            currentTime = Math.max(currentTime, process.arrivalTime); // Account for idle time
            ganttChart.add(String.format("[%d-%d] %s", currentTime, currentTime + process.burstTime, process.name));
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
        printGanttChart(ganttChart);
    }

    // SJF Scheduling
    static void simulateSJF(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.burstTime)); // Sort by burst time
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        List<String> ganttChart = new ArrayList<>();

        System.out.println("\n--- SJF Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        for (Process process : processes) {
            currentTime = Math.max(currentTime, process.arrivalTime); // Account for idle time
            ganttChart.add(String.format("[%d-%d] %s", currentTime, currentTime + process.burstTime, process.name));
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
        printGanttChart(ganttChart);
    }

    // Round-Robin Scheduling
    static void simulateRR(List<Process> processes, Scanner scanner) {
        System.out.print("Enter the time quantum: ");
        int timeQuantum = scanner.nextInt();
        scanner.nextLine();

        Queue<Process> queue = new LinkedList<>(processes);
        int currentTime = 0;
        int totalWaitingTime = 0;
        List<String> ganttChart = new ArrayList<>();

        System.out.println("\n--- Round-Robin Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        while (!queue.isEmpty()) {
            Process process = queue.poll();
            int timeSlice = Math.min(process.burstTime, timeQuantum);
            ganttChart.add(String.format("[%d-%d] %s", currentTime, currentTime + timeSlice, process.name));
            currentTime += timeSlice;
            process.burstTime -= timeSlice;

            if (process.burstTime > 0) {
                queue.add(process);
            } else {
                process.completionTime = currentTime;
                process.turnaroundTime = process.completionTime - process.arrivalTime;
                process.waitingTime = process.turnaroundTime - process.remainingTime;
                totalWaitingTime += process.waitingTime;

                System.out.printf("%-10s%-15d%-15d%-15d%-15d%-15d\n",
                                  process.name, process.arrivalTime, process.remainingTime,
                                  process.completionTime, process.turnaroundTime, process.waitingTime);
            }
        }
        printSummary(totalWaitingTime, processes.size());
        printGanttChart(ganttChart);
    }

    static void simulatePriority(List<Process> processes, Scanner scanner) {
        Map<String, Integer> priorities = new HashMap<>();
        System.out.println("Enter the priorities for each process (lower number = higher priority):");

        for (Process process : processes) {
            System.out.print("Priority for " + process.name + ": ");
            priorities.put(process.name, scanner.nextInt());
        }

        processes.sort(Comparator.comparingInt(p -> priorities.get(p.name)));
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        List<String> ganttChart = new ArrayList<>();

        System.out.println("\n--- Priority Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        for (Process process : processes) {
            currentTime = Math.max(currentTime, process.arrivalTime);
            ganttChart.add(String.format("[%d-%d] %s", currentTime, currentTime + process.burstTime, process.name));
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
        printGanttChart(ganttChart);
    }

    //Simulate Shortest Remaining Time First Scheduling
    static void simulateSRTF(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime)); // Sort by arrival time
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        List<String> ganttChart = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>();
        int completedProcesses = 0; // Track number of completed processes
        
        System.out.println("\n--- SRTF Scheduling ---");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s%-15s\n", 
                          "Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time");

        // While there are processes that haven't been executed yet
        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Add all the processes that have arrived at the current time to the ready queue
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                readyQueue.add(processes.remove(0));
            }

            if (!readyQueue.isEmpty()) {
                // Sort the ready queue by remaining burst time (SRTF)
                readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));

                Process process = readyQueue.remove(0); // Get the process with the shortest remaining time
                ganttChart.add(String.format("[%d-%d] %s", currentTime, currentTime + process.burstTime, process.name));
                process.completionTime = currentTime + process.burstTime;
                process.turnaroundTime = process.completionTime - process.arrivalTime;
                process.waitingTime = process.turnaroundTime - process.burstTime;

                totalWaitingTime += process.waitingTime;
                totalTurnaroundTime += process.turnaroundTime;

                System.out.printf("%-10s%-15d%-15d%-15d%-15d%-15d\n",
                                  process.name, process.arrivalTime, process.burstTime,
                                  process.completionTime, process.turnaroundTime, process.waitingTime);

                currentTime += process.burstTime;
                completedProcesses++; // Increment completed processes count
            } else {
                currentTime++; // No processes are ready, so increment time
            }
        }

        printSummary(totalWaitingTime, completedProcesses); // Use completedProcesses instead of original processCount
        printGanttChart(ganttChart);
    }



    // Utility function to print summary
    static void printSummary(int totalWaitingTime, int processCount) {
        System.out.println("\n--- Summary ---");
        double averageWaitingTime = (double) totalWaitingTime / processCount;
        System.out.printf("Average Waiting Time: %.2f\n", averageWaitingTime);
    }
}
