package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class FeeSnipingMiner extends BaseMiner implements Miner{
	private Block currentHead;
	private Block currentlyMiningAt;
	private double expectedBlockRewardSum = 0;
	private double blockProbability;
	private boolean attacking = false;
	private int counter = 0;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
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
    		this.expectedBlockRewardSum += block.getBlockValue();
    		this.counter ++;
    		double expectedBlockReward = this.expectedBlockRewardSum/this.counter; //update expected block reward everytime you hear about a block
    		
    		//System.out.println(this.blockProbability);
    		
        if (attacking) { //if currently performing an attack (aka trying to steal a block)
    			if (isMinerMe) {
    				if (block.getHeight() < this.currentHead.getHeight()) { //if this is an old block abandon
    					this.attacking = false;
    					this.currentlyMiningAt = currentHead;
    				} else if (block.getHeight() == this.currentHead.getHeight()) { //your found block is the same height as the block you're trying to steal
    					this.currentlyMiningAt = block; //mine at your block
    					this.currentHead = block; //use it as header
    				} else { //you won, surpassed the block
    					this.currentlyMiningAt = block;
    					this.currentHead = block;
    					this.attacking = false; //succeded
    				}
    			} else { //if you arent the miner and you're behind the new found block abandon because you can't fork two blocks now
    				if (block.getHeight() > this.currentlyMiningAt.getHeight()) {
    					this.attacking = false;
    					this.currentHead = block;
    					this.currentlyMiningAt = block;
    				}
    			}
        } else { //when sniping for fees
        		//check to see if it's worth sniping it
        		if (this.blockProbability * this.blockProbability * (block.getBlockValue() + expectedBlockReward)> this.blockProbability * expectedBlockReward) {
        			this.attacking = true;//if so set your state to attacking
        			this.currentHead = currentlyMiningAt;
        			if (isMinerMe) { //if it was mined by you then disregard the attack
        				if (block.getHeight() > this.currentlyMiningAt.getHeight()) {
        	                this.currentHead = block;
        	                this.currentlyMiningAt = block;
        	                this.attacking = false;
        	            }
        	            	
        			} else { //else keep it as you header but continue mining wherever that block was mined on
        				if (block.getHeight() > this.currentlyMiningAt.getHeight()) {
        					this.currentHead = block;
        				} else {
        					this.attacking = false;
        				}
        				
        			}
        		} else { //if not worth stealing ack as a compliant miner
        	        if(isMinerMe) {
        	            if (block.getHeight() > currentHead.getHeight()) {
        	                this.currentHead = block;
        	                this.currentlyMiningAt = block;
        	            }
        	        }
        	        else{
        	            if (currentHead == null) {
        	                currentHead = block;
        	                this.currentlyMiningAt = block;
        	            } else if (block != null && block.getHeight() > currentHead.getHeight()) {
        	                this.currentHead = block;
        	                this.currentlyMiningAt = block;

        	            }
        	        }
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