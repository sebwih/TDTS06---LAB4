import java.util.Arrays;

import javax.swing.*;        

public class RouterNode {
	private int myID;
	private GuiTextArea myGUI;
	private RouterSimulator sim;
	private int[] costsnbr = new int[RouterSimulator.NUM_NODES];
	private int[] routeNbr = new int[RouterSimulator.NUM_NODES]; //Same functionality as the array above
	private int nrNbr;
	private int [][] nbrDistanceTable;
	private RouterPacket packet;
	
	/*
	---------------------
	nbrDistanceTable
	---------------------
	Two-dimentional array that contains the cost to each of the nodes neighbors. The rows contain the different neighbors and the columns the distances.
	Each the indexes represents the node the distance corresponds to. The LAST index contains the neighbor nodes number tho!

	Ex. of how the table looks like	
	+--------------------------------+
	|	0	|	2	|	*	| nbr 1  |	
	+-------+-------+-------+--------+
	|	3	|	1	|	0	| nbr 3  |
	+--------------------------------+

	This table shows that the node has two neighbors, node 1 and 3. And the distance from node 1 to node 1-3 is 0,2 and infinity.
	The distance from node 3 to node 1-3 is 3,1,0.

	*/

	//--------------------------------------------------
	public RouterNode(int ID, RouterSimulator sim, int[] costs) {
		myID = ID;
		this.sim = sim;
		myGUI = new GuiTextArea("  Output window for Router #"+ ID + "  ");
		System.arraycopy(costs, 0, this.costsnbr, 0, RouterSimulator.NUM_NODES);
		howManyNbr();
		this.nbrDistanceTable = new int[this.nrNbr][RouterSimulator.NUM_NODES+1];
		initrouteNbr();
		initNbrArray();
		this.printDistanceTable();
		updateNbr();
	}

	//--------------------------------------------------
	public void recvUpdate(RouterPacket pkt) {
		for(int i=0; i<this.nrNbr; i++){
			if(this.nbrDistanceTable[i][RouterSimulator.NUM_NODES] == pkt.sourceid){
				for(int j=0; j<RouterSimulator.NUM_NODES; j++){
					this.nbrDistanceTable[i][j] = pkt.mincost[j];
				}
				break;
			}
		}
		
		if(updateRoute()){
			updateNbr();
		}

	}

	public boolean updateRoute(){
		
		int cmp;
		boolean update = false;

		for(int i=0; i<nrNbr; i++){
			for(int j=0; j<RouterSimulator.NUM_NODES; j++){
				cmp = nbrDistanceTable[i][myID]+nbrDistanceTable[i][j];
				if(cmp < costsnbr[j]){
					costsnbr[j] = cmp; //Updates cost
					routeNbr[j] = nbrDistanceTable[i][RouterSimulator.NUM_NODES]; //Updates route
					update = true;
				}
			}

		}
		return update;
	}


	//--------------------------------------------------
	private void sendUpdate(RouterPacket pkt) {
		sim.toLayer2(pkt);
	}

	private void updateNbr() {
		for(int i=0; i<nrNbr; i++){
			packet = new RouterPacket(myID,nbrDistanceTable[i][RouterSimulator.NUM_NODES],costsnbr);
			sendUpdate(packet);
		}
	}


	//--------------------------------------------------
	public void printDistanceTable() {

		myGUI.println("Current table for " + myID +
				"  at time " + sim.getClocktime() + "\n");

		myGUI.println("Distance Table:");
		printTableHeader();
		printNbr();
		myGUI.println("\n");
		myGUI.println("Our distance vector and routes:");
		printTableHeader();
		printCostRoute();
		myGUI.println("\n\n");

	}

	//--------------------------------------------------
	public void updateLinkCost(int dest, int newcost) {
		System.out.println("Link from [" + myID + "] to [" + dest + "] should update to " + newcost);
	}

	private void printTableHeader(){
		myGUI.print("  dest  |");
		for(int i=0; i<RouterSimulator.NUM_NODES; i++){
			myGUI.print("	" + i);
		}

		myGUI.print("\n-------------");

		for(int i=0; i<RouterSimulator.NUM_NODES-1; i++){
			myGUI.print("------------");
		}
	}

	private void printCostRoute(){ 

		myGUI.print("\n  cost  |");
		for(int i=0; i<this.costsnbr.length; i++){
			myGUI.print("	" + this.costsnbr[i]);
		}

		myGUI.print("\n  route|");
		for(int i=0; i<RouterSimulator.NUM_NODES; i++){
			if(this.costsnbr[i] != RouterSimulator.INFINITY){
				myGUI.print("	" + routeNbr[i]);
			}
			else{
				myGUI.print("	" + "-");
			}
		}
	}

	private void printNbr(){
		myGUI.println();

		for(int i=0; i<this.nrNbr; i++){
			myGUI.print("  nbr " + this.nbrDistanceTable[i][RouterSimulator.NUM_NODES] + "  |");
			for(int j=0; j<RouterSimulator.NUM_NODES; j++){
				myGUI.print("	" + this.nbrDistanceTable[i][j]);
			}
			myGUI.println();
		}
	}


	private void howManyNbr(){

		for(int i=0; i<this.costsnbr.length; i++){
			if(this.costsnbr[i] != RouterSimulator.INFINITY 
					&& i != this.myID){
				nrNbr = nrNbr + 1;
			}
		}
	}
	//Initialize routeNbr to be [1..Num_Nodes]
	private void initrouteNbr(){
		for(int i = 0; i<RouterSimulator.NUM_NODES; i++){
			routeNbr[i] = i;
		}
	}

	private void initNbrArray(){

		//Sets all the neighbors distance to infinity
		for(int i=0; i<this.nrNbr; i++){
			for(int j=0; j<RouterSimulator.NUM_NODES; j++){
				this.nbrDistanceTable[i][j] = RouterSimulator.INFINITY;			
			}
		}

		int temp = 0;

		//inserts the neighbors number
		for(int i=0; i<RouterSimulator.NUM_NODES; i++){
			if(this.costsnbr[i] != RouterSimulator.INFINITY 
					&& i != this.myID){
				this.nbrDistanceTable[temp][RouterSimulator.NUM_NODES] = i;
				temp++;
			}
		}
	}

}
