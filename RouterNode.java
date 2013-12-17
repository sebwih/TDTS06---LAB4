import java.util.Arrays;

import javax.swing.*;        

public class RouterNode {
	private int myID;
	private GuiTextArea myGUI;
	private RouterSimulator sim;
	private int[] costsnbr = new int[RouterSimulator.NUM_NODES];
	private int nrNbr;
	private int [][] nbrDistanceTable;

	//--------------------------------------------------
	public RouterNode(int ID, RouterSimulator sim, int[] costs) {
		myID = ID;
		this.sim = sim;
		myGUI = new GuiTextArea("  Output window for Router #"+ ID + "  ");
		System.arraycopy(costs, 0, this.costsnbr, 0, RouterSimulator.NUM_NODES);
		howManyNbr();
		this.nbrDistanceTable = new int[this.nrNbr][RouterSimulator.NUM_NODES+1];
		initNbrArray();
		this.printDistanceTable();
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
		//printDistanceTable();

	}


	//--------------------------------------------------
	private void sendUpdate(RouterPacket pkt) {
		sim.toLayer2(pkt);
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
				myGUI.print("	" + i);
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
