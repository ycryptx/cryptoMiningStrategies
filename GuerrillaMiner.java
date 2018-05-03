package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class GuerrillaMiner extends BaseMiner implements Miner{
    private Block currentHead;
	private Block currentlyMiningAt;
	private double expectedBlockRewardSum = 0;
	private double blockProbability;
	private int counter = 0;


    public GuerrillaMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
    	this.expectedBlockRewardSum += block.getBlockValue();
		this.counter ++;
		double expectedBlockReward = this.expectedBlockRewardSum/this.counter; //update expected block reward everytime you hear about a block
		
    	
    	
    	if(isMinerMe) {
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }
        else{
            if (currentHead == null) {
                currentHead = block;
            } else if (block != null && block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;

            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.currentlyMiningAt = genesis;
        //this.expectedBlockRewardSum = 0;
        networkUpdate(networkStatistics);
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
		this.blockProbability = (double)this.getHashRate()/statistics.getTotalHashRate();
    }
}
