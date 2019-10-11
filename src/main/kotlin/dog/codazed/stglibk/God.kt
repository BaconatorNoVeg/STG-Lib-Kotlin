package dog.codazed.stglibk

class God(private val name: String, private val position: String){

    private val physicals = arrayOf("Assassin", "Hunter", "Warrior");
    private val magicals = arrayOf("Mage", "Guardian");

    fun checkBuild(build : List<Item>) : Boolean {
        for (item in build) {
            if (item.available(this).not()) {
                return false;
            }
        }
        return true;
    }

    fun isPhysical() : Boolean {
        return physicals.asList().contains(position);
    }

    fun isMagical() : Boolean {
        return magicals.asList().contains(position);
    }

    fun isOffensive(stg : SmiteTeamGenerator) : Boolean {
        return this.position == "Assassin" || this.position == "Hunter" || this.position == "Mage" || (this.position == "Warrior" && stg.warriorsOffensive);
    }

    fun isDefensive(stg : SmiteTeamGenerator) : Boolean {
        return (this.position == "Warrior" && !stg.warriorsOffensive) || this.position == "Guardian";
    }

    fun getName() : String {
        return this.name;
    }

    fun getPosition() : String {
        return this.position;
    }

    override fun toString() : String {
        return this.name;
    }

}
