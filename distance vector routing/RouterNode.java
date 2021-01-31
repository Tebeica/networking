/*

 * Complete this class.

 * Student Name: Teodor Tebeica

 * Student ID No.: 30046038

 */

import javax.swing.*;        

import java.util.Arrays;



public class RouterNode {

  private boolean poisonedReverse = false;

  private int myID;

  private GuiTextArea myGUI;

  private RouterSimulator sim;

  private int[] costs = new int[RouterSimulator.NUM_NODES];

  private int[][] distanceTable = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];

  private int[] minRoute = new int[RouterSimulator.NUM_NODES];



  //--------------------------------------------------

  public RouterNode(int ID, RouterSimulator sim, int[] costs) {

    	myID = ID;

	this.sim = sim;

	myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");



    	System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);



    	// set all distance values to infinity

    	for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

		for (int j = 0; j < RouterSimulator.NUM_NODES; j++) {

			distanceTable[i][j] = RouterSimulator.INFINITY;

		}

    	}



	System.arraycopy(costs, 0, distanceTable[myID], 0, RouterSimulator.NUM_NODES);



    	// set minimal routes to direct links if exists

    	for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

    	

		if (costs[i] != RouterSimulator.INFINITY) {

			minRoute[i] = i;

		} else { 

			minRoute[i] = RouterSimulator.INFINITY;

		}

		sendDistanceVector();

    	}

  }



  public void sendDistanceVector() {

	

	  //send distance vec to all adjacent routers

	  for (int i = 0; i< RouterSimulator.NUM_NODES; i++) {

		if (i == myID || costs[i] == RouterSimulator.INFINITY) continue;

		

		// poisoned distance vector

		int[] distVector = new int[RouterSimulator.NUM_NODES];

		for (int k = 0; k < RouterSimulator.NUM_NODES; k++) {

			if (poisonedReverse && i == minRoute[k]) {

				distVector[k] = RouterSimulator.INFINITY;

			} else {

				distVector[k] = distanceTable[myID][k];

			}



		}

		// send poisoned distance vector

		RouterPacket pkt = new RouterPacket(myID, i, distVector);

		sendUpdate(pkt);

	  }

  }





  //--------------------------------------------------

  public void recvUpdate(RouterPacket pkt) {

	System.arraycopy(pkt.mincost, 0, distanceTable[pkt.sourceid], 0, RouterSimulator.NUM_NODES);

	recalculateDistanceVector();

  }

  

  public void recalculateDistanceVector() {

	int[] newDistanceVector = new int[RouterSimulator.NUM_NODES];

	

	for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

		int path = minRoute[i] = findShortestPath(i);

		if (path != RouterSimulator.INFINITY) {

			newDistanceVector[i] = costs[path] + distanceTable[path][i];

		} else {

			newDistanceVector[i] = RouterSimulator.INFINITY;

		}

	}

	if (!Arrays.equals(newDistanceVector, distanceTable[myID])) {

		distanceTable[myID] = newDistanceVector;

		sendDistanceVector();

	}

  }





  //--------------------------------------------------

  private void sendUpdate(RouterPacket pkt) {

    sim.toLayer2(pkt);



  }

  



  //--------------------------------------------------

  public void printDistanceTable() {

	StringBuilder b;

	myGUI.println("Current table for " + myID +

			"  at time " + sim.getClocktime());



	myGUI.println("Distancetable:");

	b = new StringBuilder(F.format("dst", 7) + " | ");

	for (int i = 0; i < RouterSimulator.NUM_NODES; i++)

		b.append(F.format(i, 5));

	myGUI.println(b.toString());



	for (int i = 0; i < b.length(); i++)

		myGUI.print("-");

	myGUI.println();



	for (int source = 0; source < RouterSimulator.NUM_NODES; source++) {

		if (source == myID) continue;

		b = new StringBuilder("nbr" + F.format(source, 3) + " | ");

		for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

			b.append(F.format(distanceTable[source][i], 5));

		}

		myGUI.println(b.toString());

	}



	myGUI.println("\nOur distance vector and routes:");



	b = new StringBuilder(F.format("dst", 7) + " | ");

	for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

		b.append(F.format(distanceTable[myID][i], 5));

	}

	myGUI.println(b.toString());



	for (int i = 0; i < b.length(); i++) {

		myGUI.print("-");

	}

	myGUI.println();



	b = new StringBuilder(F.format("cost", 7) + " | ");

	for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

		b.append(F.format(distanceTable[myID][i], 5));

	}

	myGUI.println(b.toString());



	b = new StringBuilder(F.format("route", 7) + " | ");

	for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

		if (minRoute[i] != RouterSimulator.INFINITY) {

			b.append(F.format(minRoute[i], 5));

		} else {

			b.append(F.format("-", 5));

		}

	}



	myGUI.println(b.toString());

	myGUI.println();

  }



  //--------------------------------------------------

  public void updateLinkCost(int dest, int newcost) {

	costs[dest] = newcost;

	recalculateDistanceVector();



  }



  private int findShortestPath(int dest) {

	int distance = costs[dest];

	int path;



	if (distance != RouterSimulator.INFINITY) {

		path = dest;

	} else {

		path = RouterSimulator.INFINITY;

	} 



	for (int i = 0; i < RouterSimulator.NUM_NODES; i++) {

		if (i == myID || i == dest) {

			continue;

		}



		if (costs[i] != RouterSimulator.INFINITY && distanceTable[i][dest] != RouterSimulator.INFINITY 

				&& costs[i] + distanceTable[i][dest] < distance) {

			distance = costs[i] + distanceTable[i][dest];

			path = i;

		}

	}

	return path;

  }



}