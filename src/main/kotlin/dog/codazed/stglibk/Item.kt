package dog.codazed.stglibk

class Item (
    private val name: String, private val isPhysical: String, private val isMagical: String, private val itemType: String,
    private val availability: ArrayList<Int>
){

    fun isPhysical() : Boolean {
        return isPhysical.toBoolean();
    }

    fun isMagical() : Boolean {
        return isMagical.toBoolean();
    }

    fun isMask() : Boolean {
        return name.contains("Mask");
    }

    fun available(god : God) : Boolean {
        return when(god.getPosition().toLowerCase()) {
            "assassin" -> availability[0] == 1;
            "hunter" -> availability[1] == 1;
            "mage" -> availability[2] == 1;
            "warrior" -> availability[3] == 1;
            "guardian" -> availability[4] == 1;
            else -> false;
        }
    }

    fun isOffensive() : Boolean {
        return itemType == "OFFENSE" || itemType == "BOTH";
    }

    fun isDefensive() : Boolean {
        return itemType == "DEFENSE" || itemType == "BOTH";
    }

    override fun toString() : String {
        return this.name;
    }

}
