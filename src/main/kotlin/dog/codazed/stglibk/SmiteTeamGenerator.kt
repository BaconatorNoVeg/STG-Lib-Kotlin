package dog.codazed.stglibk

import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class SmiteTeamGenerator() {

    val BOOTS = ArrayList<Item>();
    val GODS = ArrayList<God>();
    val ITEMS = ArrayList<Item>();
    val RELICS = ArrayList<Item>();

    var isForcingBalanced = false;
    var isForcingBoots = true;
    val warriorsOffensive = true;

    private val bootChance : Int = 65; // Chance of getting boots  Default: 65%

    var buildType = 0;

    val positions = arrayListOf<String>("assassin", "hunter", "mage", "warrior", "guardian");

    fun getVersion() : String {
        return "Kotlin-2019.10.08";
    }

    private fun getLists() {
        var input : Scanner;
        println("Attempting to fetch the lists from Github...");
        val bootsFile = URL("https://raw.githubusercontent.com/BaconatorNoVeg/STG-Lib/master/Lists/boots.csv").openStream();
        val godsFile = URL("https://raw.githubusercontent.com/BaconatorNoVeg/STG-Lib/master/Lists/gods.csv").openStream();
        val itemsFile = URL("https://raw.githubusercontent.com/BaconatorNoVeg/STG-Lib/master/Lists/items.csv").openStream();
        val relicsFile = URL("https://raw.githubusercontent.com/BaconatorNoVeg/STG-Lib/master/Lists/relics.csv").openStream();

        // Begin parsing boots
        input = Scanner(bootsFile);
        input.nextLine();

        while (input.hasNextLine()) {
            val line = input.nextLine();
            val values = line.split(",");
            val availability = arrayListOf(1,1,1,1,1);
            BOOTS.add(Item(values[0], values[1], values[2], values[3], availability));
        }

        input.close();

        // Begin parsing gods
        input = Scanner(godsFile);
        input.nextLine();

        while (input.hasNextLine()) {
            val line = input.nextLine();
            val values = line.split(",");
            GODS.add(God(values[0], values[1]));
        }

        input.close();

        // Begin parsing items
        input = Scanner(itemsFile);
        input.nextLine();

        while (input.hasNextLine()) {
            val line = input.nextLine();
            val values = line.split(",");
            val availability = arrayListOf(convertBooltoInt(values[4].toBoolean()), convertBooltoInt(values[5].toBoolean()), convertBooltoInt(values[6].toBoolean()), convertBooltoInt(values[7].toBoolean()), convertBooltoInt(values[8].toBoolean()));
            ITEMS.add(Item(values[0], values[1], values[2], values[3], availability));
        }

        input.close();

        // Begin parsing relics
        input = Scanner(relicsFile);
        input.nextLine();

        while (input.hasNextLine()) {
            val line = input.nextLine();
            val values = line.split(",");
            val availability = arrayListOf(1,1,1,1,1);
            RELICS.add(Item(values[0], "TRUE", "TRUE", "Relic", availability));
        }

        input.close();

        println("STG-Lib successfully loaded ${BOOTS.size} boots, ${GODS.size} gods, ${RELICS.size} relics, and ${ITEMS.size} items.");

    }

    private fun convertBooltoInt(bool : Boolean) : Int {
        if (bool) return 1 else return 0;
    }

    private fun shufflePositions() {
        positions.shuffle();
    }

    fun generateTeam(size: Int, forceBalanced: Boolean, forceBoots: Boolean, buildType: Int) {
        isForcingBalanced = forceBalanced;
        isForcingBoots = forceBoots;
        this.buildType = buildType;
        val team = Team(this);
        if (forceBalanced) {
            // Generate a team that does not duplicate positions
            shufflePositions();
            for (i in 0..size) {
                val loadout = makeLoadout(positions[i]);
                team.add(loadout);
            }
        }
    }

    fun makeLoadout(position: String): Player {
        val player = getGod(position);
        val items = generateBuild(player);
        val build: List<Item> = processBuild(player, items);
        val relics = ArrayList<Item>();
        
        val firstRelic = getRelic();
        var secondRelic = getRelic();
        while (firstRelic == secondRelic) {
            secondRelic = getRelic();
        }
        relics.add(firstRelic);
        relics.add(secondRelic);
        return Player(player, build, relics);
    }

    private fun processBuild(god: God, items: List<Item>): List<Item> {
        var build = items;
        val buildItems = ArrayList<String>();
        while (true) {
            buildItems.clear();
            for (item in build) {
                buildItems.add(item.toString());
            }
            if (!god.checkBuild(build)) {
                build = generateBuild(god);
            } else if (!checkMasks(build)) {
                build = generateBuild(god);
            } else {
                var buildReady = false;
                var offensiveCount = 0;
                var defensiveCount = 0;
                for (item in build) {
                    if (item.isOffensive() && item.isDefensive()) {
                        offensiveCount++;
                        defensiveCount++;
                    } else if (item.isOffensive()) offensiveCount++;
                    else if (item.isDefensive()) defensiveCount++;
                }
                when (buildType) {
                    1 -> {
                        // Full offensive
                        if (offensiveCount < 6) build = generateBuild(god);
                        else buildReady = true;
                    }
                    2 -> {
                        // Full defensive
                        if (defensiveCount < 6) build = generateBuild(god);
                        else buildReady = true;
                    }
                    3 -> {
                        // Half-and-half
                        if (offensiveCount < 3 || defensiveCount < 3) build = generateBuild(god);
                        else buildReady = true;
                    }
                    4 -> {
                        // Off on off gods, def on def gods
                        if (god.isOffensive(this)) {
                            if (offensiveCount < 6) build = generateBuild(god);
                            else buildReady = true;
                        } else {
                            if (defensiveCount < 6) build = generateBuild(god);
                            else buildReady = true;
                        }
                    }
                    else -> {
                        // Default random
                        buildReady = true;
                    }
                }
                if (buildReady) {
                    return build;
                }
            }
        }
    }

    private fun checkMasks(build: List<Item>): Boolean {
        var numMasks = 0;
        for (item in build) {
            if (item.isMask()) {
                numMasks++;
                if (numMasks > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private fun getRelic(): Item {
        return RELICS[(0 until RELICS.size).random()];
    }

    private fun generateBuild(god: God): List<Item> {
        val build = ArrayList<Item>();
        val type = god.getPosition();
        if ((1..100).random() < bootChance || isForcingBoots) {
            if (god.getName() == "Ratatoskr") {
                val availability = arrayListOf(1,0,0,0,0);
                build.add(Item("Acorn of Yggdrasil", "true", "false", "BOTH", availability));
            } else {
                build.add(getBoot(god));
            }
        } else {
            build.add(getItem(god));
        }
        for (i in 0 until 5) {
            build.add(getItem(god));
        }
        return build;
    }

    private fun getBoot(god: God): Item {
        var boot = BOOTS[(0 until BOOTS.size).random()];
        if (god.isPhysical()) {
            while (boot.isMagical()) {
                boot = BOOTS[(0 until BOOTS.size).random()];
            }
        } else {
            while (boot.isPhysical()) {
                boot = BOOTS[(0 until BOOTS.size).random()];
            }
        }
        return boot;
    }

    private fun getItem(god: God): Item {
        var item = ITEMS[(0 until ITEMS.size).random()];
        if (god.isPhysical()) {
            while (!item.isPhysical()) {
                item = ITEMS[(0 until ITEMS.size).random()];
            }
        } else {
            while (!item.isMagical()) {
                item = ITEMS[(0 until ITEMS.size).random()];
            }
        }
        return item;
    }

    private fun getGod(position: String): God {
        var god = GODS.random();
        while (!god.getPosition().equals(position, ignoreCase = true)) {
            god = GODS.random();
        }
        return god;
    }

    fun getGodsAsStrings() : List<String> {
        val returnList = ArrayList<String>();
        for (god in GODS) {
            returnList.add(god.getName());
        }
        return returnList;
    }

    fun getItemsAsStrings() : List<String> {
        val returnList = ArrayList<String>();
        for (item in ITEMS) {
            returnList.add(item.toString());
        }
        return returnList;
    }

    fun getBootsAsStrings() : List<String> {
        val returnList = ArrayList<String>();
        for (boot in BOOTS) {
            returnList.add(boot.toString());
        }
        return returnList;
    }

    init {
        getLists();
    }

}