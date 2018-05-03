package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class SelfishMiner extends BaseMiner implements Miner{
	private Block currentHead = null;
	private Block currentlyMiningAt = null;
	private int privateBranchLength = 0;  //keeps track of the private branch length
	private Block privateChain = null; //keeps track of the private bloks

    public SelfishMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return currentlyMiningAt;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
    	int chainDistance = this.privateChain.getHeight() - this.currentHead.getHeight(); //check how far ahead is your chain
    	
	    if(isMinerMe) { //if i mined it
        		this.privateChain = block; //add it to my private chain
        		privateBranchLength ++; //increase its length
            if (chainDistance == 0 && privateBranchLength == 2) { //if chain distance is 0 but the branch length is 2
            		this.currentHead = this.privateChain; //make your private chain public
            		this.privateBranchLength = 0; //reset privateBranchLength
            }
            this.currentlyMiningAt = this.privateChain; //mine at your private chain (could be announced)
        }else{
			this.currentHead = block; //add it as you header
			if (chainDistance == 0) { //attack loses (give up)
				this.privateChain = this.currentHead;
				this.privateBranchLength = 0;
			} else if (chainDistance == 1) { //same length so try our luck
				this.currentHead = this.privateChain;
			} else if (chainDistance == 2) { //we win so make everything public to orphan the block
				this.currentHead = this.privateChain;
				this.privateBranchLength = 0;
			} else { //publish the first unpublished block in the private chain
				Block blockToPublish = this.privateChain;
				for (int i = 0; i < chainDistance - 1; i++) {
					blockToPublish = blockToPublish.getPreviousBlock();
				}
				this.currentHead = blockToPublish;
			}
			this.currentlyMiningAt = this.privateChain; //mine at your private chain
        }

    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        //this.currentlyMiningAt = genesis;
        this.privateChain = genesis;
        this.privateBranchLength = 0;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
    		
    }
}