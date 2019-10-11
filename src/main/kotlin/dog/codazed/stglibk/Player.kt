package dog.codazed.stglibk

class Player(private val god : God, private val build : List<Item>, private val relics : List<Item>) {

    fun getGod() : God {
        return god;
    }

    fun getBuild() : List<String> {
        val buildList = ArrayList<String>();
        for (item in build) {
            buildList.add(item.toString());
        }
        return buildList;
    }

    fun getRelics() : List<String> {
        val relicList = ArrayList<String>();
        for (relic in relics) {
            relicList.add(relic.toString());
        }
        return relicList;
    }

    fun getBuildAsItems() : List<Item> {
        return build;
    }

    fun getRelicsAsItems() : List<Item> {
        return relics;
    }

    override fun toString() : String {
        return "$god - ${build.toString()} - Relics: ${relics.toString()}";
    }

}
