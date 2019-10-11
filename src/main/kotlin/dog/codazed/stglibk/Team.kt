package dog.codazed.stglibk

class Team(stg: SmiteTeamGenerator) {

    private val team = ArrayList<Player>();

    fun add(player : Player) {
        team.add(player);
    }

    fun getGods() : List<String> {
        val gods = ArrayList<String>();
        for (player in team) {
            gods.add(player.getGod().getName());
        }
        return gods;
    }

    fun set(index : Int, player : Player) {
        team[index] = player;
    }

    fun getSize() : Int {
        return team.size;
    }

    fun getPlayer(index : Int) : Player {
        return team[index];
    }

    override fun toString() : String {
        val returnString = StringBuilder();
        returnString.append("Generated Team: \n");
        for (player in team) {
            returnString.append("${player.getGod()} - ${player.getBuild()}\n");
        }
        return returnString.toString();
    }
}
