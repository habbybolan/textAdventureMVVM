package com.habbybolan.textadventure.model.encounter;

public class RandomBenefitModelBuilder {

    private RandomBenefitModel randomBenefitModel;

    public RandomBenefitModelBuilder(String[] dialogue) {
        randomBenefitModel = new RandomBenefitModel(dialogue);
    }

    /**
     *  Sets a specific tier of loot to weight the rewards towards. Can be tier 1-3.
     * @param tier  The tier of the weapon, 1 being worst, 3 being best
     * @return      this
     */
    public RandomBenefitModelBuilder setTier(int tier) {
        randomBenefitModel.tier = tier;
        return this;
    }

    /**
     *  Sets the number of Inventory rewards to receive
     * @param numRewards    Number of inventory rewards
     * @return              this
     */
    public RandomBenefitModelBuilder setNumRewards(int numRewards) {
        randomBenefitModel.numRewards = numRewards;
        return this;
    }

    /**
     *  Sets exp to be rewarded
     * @return  this
     */
    public RandomBenefitModelBuilder setExpReward() {
        randomBenefitModel.isExp = true;
        return this;
    }

    /**
     *  Sets gold to be rewarded.
     * @return          this
     */
    public RandomBenefitModelBuilder setGoldReward() {
        randomBenefitModel.isGold = true;
        return this;
    }

    public RandomBenefitModel build() {
        return randomBenefitModel;
    }

}
