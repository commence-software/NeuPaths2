# Example: Using the CellClusterDaemon

## Description

Demonstrates the use of the CellClusterDaemon to deploy cell clusters.  This example
dynamically extends a pool of load-balanced cells by deploying an additional cell cluster.

## Features/Techniques Used

- Cluster and Cell definition files
- Use of CellClusterDaemon to deploy cell clusters.
- Custom load-balanced activator
- Transactions
- Local stream synapses (Unix sockets)

## Build

`javac -cp neupaths.jar:. *.java`

## Run

Steps:

1. Open three terminals and change to the examples/ClusterDaemon directory in each.

2. In the first terminal, start the CellClusterDaemon:  `java -cp ../../source/java/neupaths.jar:. neupaths.util.CellClusterDaemon Local#Stream#Listener#Test#/tmp/daemon.syn LBC_Daemon $PWD/cfg`

3. The CellClusterDaemon should display the following:
   
   ```
   Daemon ready.
   Name:       LBC_Daemon
   Synapse:    Local#Stream#Listener#Test#/tmp/daemon.syn
   Config Dir: /home/aaron/Development/GitHub_Repos/NeuPaths2/examples/Daemon/cfg
   ```

4. In the second terminal, deploy the LBCluster1 cell cluster: `java -cp ../../source/java/neupaths.jar:. neupaths.util.IssueCommandToDaemon Local#Stream#Peer#Test#/tmp/daemon.syn deploy cluster LBC_Daemon LBCluster1.xml`

5. In the third terminal, run the injector program:  `java -cp ../../source/java/neupaths.jar:. Main`

6. In the third terminal, observe the load-balanced cells (LBC_1, LBC_2 and LBC_3) alternate processing.

7. In the second terminal, deploy the LBCluster2 cell cluster: `java -cp ../../source/java/neupaths.jar:. neupaths.util.IssueCommandToDaemon Local#Stream#Peer#Test#/tmp/daemon.syn deploy cluster LBC_Daemon LBCluster2.xml`

8. In the third terminal, observe stimuli processing alternate between all of the load-balanced cells, including the new LBC_4 and LBC_5 cells.
