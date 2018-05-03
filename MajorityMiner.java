package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class MajorityMiner extends BaseMiner implements Miner{
	private Block currentHead = null;
	private Block currentlyMiningAt = null;

	
	public MajorityMiner(String id, int hashRate, int connectivity) {
		super (id, hashRate, connectivity);
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
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.currentlyMiningAt = genesis;
        //this.forkingBranchLength = 0;
        //this.blockProbability = (double)this.getHashRate()/networkStatistics.getTotalHashRate();
        
    }
    
    @Override
    public void networkUpdate(NetworkStatistics statistics) {
    	
    }

	@Override
	public void blockMined(Block block, boolean isMinerMe) {

		
		if (isMinerMe) { //if you find a new block that you haven't heard off always add it to what you consider as the header and mine on top of it
			if (block.getHeight() > this.currentHead.getHeight() ) {
				this.currentHead = block;
				this.currentlyMiningAt = block;
			}
		} else {
			 //make this your current header
			if (this.currentlyMiningAt.getHeight() >= block.getHeight() - 2 && block.getHeight() > this.currentHead.getHeight()) {
				this.currentHead = currentlyMiningAt; //if you're less than two block behind try to fork it
			} else if (block.getHeight() > this.currentHead.getHeight()){ //else abandon the forking attack and accept is as the top of the chain
				this.currentHead = block;
				this.currentlyMiningAt = block;
			}
		}



		//System.out.println(block);
		//System.out.println(this.blockProbability);
	}
}

