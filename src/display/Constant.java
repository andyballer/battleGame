package display;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileNameExtensionFilter;

import jgame.JGColor;
import jgame.JGFont;

public class Constant {
	
	public final static boolean DEBUG = false;
	public final static boolean enableAI = true;
	public final static boolean DEBUG_JGAME = false;
	
	public final static int leftBorder = 10;
	public final static int rightBorder = 10;
	public final static int upperBorder = 10;
	public final static int bottomBorder = 10;
	
	public final static int mainWindowWidth = 1024;
	public final static int mainWindowHeight = 768;
	
	public final static int prefWindowWidth = 600;
	public final static int prefWindowHeight = 400;

	public final static int jgameWindowWidth = 740;
	public final static int jgameWindowHeight = 660;
	
	public final static int statusWidth = 250;
	public final static int statusHeight = 300;
	
	public final static int statsWidth = 300;
	public final static int statsHeight = 180;
	
	public final static int listSelectorWidth = 250;
	public final static int listSelectorHeight = 150;
	
	public final static int listSelectorLocationX = 400;
	public final static int listSelectorLocationY = 100;
	
	public final static int dialogWindowWidth = 250;
	public final static int dialogWindowHeight = 100;
	
	public final static int listSelectorMainWidth = 550;
	public final static int listSelectorMainHeight = 300;
	public final static int listSelectorMainHeightLarger = 400;
	public final static int listSelectorMainWidthSmaller = 200;
	
	public final static int goalWidth = 250;
	public final static int goalHeight = 80;
	
	public final static int statsWindowXDeviation = 300;
	public final static int statsWindowYDeviation = 50;
	
	public final static int buttonsPerCol = 2;
	
	public final static int jgameRate = 100;
	
	public final static int playerTeam = 0;
	public final static int enemyTeam = 1;
	public final static int bossTeam = 2;
	
	public static final int AIFactor = 10;
	public static final int maxAIIntelligence = 100;
	
	public final static int playerCollisionID = 1;
	public final static int enemyCollisionID = 2;
	public final static int bossCollisionID = 2;
	public final static int envCollisionID = 4;
	public final static int imageDefaultCollisionID = 8;
	public final static int cursorCollisionID = 10;
	
	public final static String levelSelectorInit = "Level 1";
	public final static String gameSelectorInit = "Game 1";
	public final static String progName = "@_@";
	public final static String prefName = "Preferences";
	public final static String settingName = "Settings For New Level";
	public final static String objectSelectName = "Select And Drop Object";
	public final static String actionSelectName = "Select Action";
	public final static String inventorySelectName = "Select Item in Inventory";
	
	public final static String gameNameTag = "Game Name: ";
	public final static String levelNameTag = "Level Name: ";
	public final static String objectiveTag = "The Objective of This Level: ";
	public final static String numrowTag = "Number of Rows: ";
	public final static String numcolTag = "Number of Columns: ";
	public final static String tileSizeTag = "Tile Size (Optional):";
	public final static String playerHealthTag = "Player HP: ";
	public final static String leaderboardTag = "Leaderboard: ";
	public final static String userNameTag = "Please enter your name: ";
	public final static String scoreTag = "Score: ";
	
	public final static String AIintelligenceHint = "Intelligence must be 99 or less";
	public final static String AIintelligenceTag = "AI Intelligence";
	public final static String AIAttackTag = "AI Attack";
	public final static String AIDefenseTag = "AI Defense";
	public final static String AIMoneyTag = "AI Money";
	public final static String AIObjectiveTag = "AI Objective";
	
	public final static String okTag = "OK";
	public final static String cancelTag = "Cancel";
	public final static String deleteTag = "Delete";
	public final static String invalidGameNameTag = "Invalid Game Name";			
	public final static String invalidLevelNameTag = "Invalid Level Name";
	public final static String invalidRowColTag = "Minimum number of"+"\n"+"rows and columns is two.";
	public final static String invalidGridTag = "Grid Too Large";
	public final static String invalidTileSizeTag = "Minimum tile size is 16." + "\n" 
													+ "Enter zero to create grid based on rows and columns.";
	public final static String invalidTag = "Invalid Input";
	
	public final static String enableTag = "Enable";
	public final static String hardnessTag = "Difficulty";
	public final static String moderateTag = "Moderate";
	public final static String easyTag = "Easy";
	public final static String hardTag = "Hard";
	
	public final static String objectNameTag = "Object Name: ";
	public final static String imageChooserTag = "Object Image: ";
	public final static String initialHealthTag = "Health: ";
	public final static String initialPowerTag = "Power: ";
	public final static String initialDefenseTag = "Defense: ";
	public final static String initialSpeedTag = "Speed: ";
	public final static String initialRangeTag = "Range: ";
	public final static String initialValueTag = "Initial: ";
	public final static String levelUpPercentageTag = "LevelUp: ";
	public final static String boostPercentageTag = "Boost: ";
	public final static String objectSkillTag = "Items: ";
	public final static String abilityTag = "Abilities ";
	public final static String imageSelectedPathTag = "imageSelectedPath";
	
	public final static String inititialStatsTag = "InitialStats";
	public final static String currentHealthTag = "Current Health: ";
	public final static String experienceTag = "Current Experience: ";
	public final static String needlevelUpTag = "Experience to Next Level: ";
	public final static String currentLevelTag = "Current Level: ";
	
	public final static String colorAvailableMoveTag = "Color for Available Grids to Move";
	public final static String playerFirstTag = "Start with Player's Turn";
	
	public final static String listShowMainTag = "Main Category";
	public final static String listShowItemTag = "Stock";
	public final static String listShowWeaponTag = "Weapons";
	
	public final static String nextLevelTag = "Next Level";
	public final static String retryTag = "Retry";
	public final static String backToEditTag = "Back To Edit Mode";
	
	public final static String hitTag = "Hit";
	public final static String moveTag = "Move";
	public final static String plantTag = "PlantTree";
	public final static String placeRockTag = "PlaceRock";
	public final static String pickUpTag = "PickUp";
	public final static String pushTag = "Push";
	public final static String waitTag = "Wait";
	public final static String inventoryTag = "Inventory";
	public final static String boostTag = "Boost";
	public final static String destroyRockTag = "DestroyRock";
	public final static String destroyTreeTag = "DestroyTree";
	public final static String destroyTag = "Destroy";
	
	public final static String objectivePropertyFile = "resources.objectives";
	public final static String imagePropertyFile = "resources.images";
	public final static String algorithmPropertyFile = "resources.algorithms";
	
	public final static String animationDirectory = "/resources/animations/";
	public final static String animationTableFile = "/resources/animations.tbl";
	public final static String defaultSaveFilePath = "games/default.json";
	public final static String gameImageFilePath = "/gamepics/";
	public final static String highScoreFilePath = "/highscores/";
	public final static String highScoreTemplate = "Score_Template_scores.dat";
	public final static String defaultHighScoreFilePathSuffix = "_scores.dat";
	public final static String defaultSavePathPrefix = "/games/";
	
	public final static String commonPropertyFile = "resources.common";
	public final static String prefDirectory = "/edu/duke/cs308/2014/oogasalad/pref";
	public final static String imageDirectory = "/resources/images/";
	public final static String soundPropertyFile = "resources.sounds";
	public final static String soundDirectory = "/resources/sounds/";
	
	public final static String sourceSelectionMode = "Source Selection Mode";
	public final static String targetSelectionMode = "Target Selection Mode";
	public final static String sourceSelectionCompleteMode = "Source Selection Complete Mode";
	public final static String targetSelectionCompleteMode = "Target Selection Complete Mode";
	public final static String actionMode = "Action Mode";
	public final static String enemyMode = "Enemy Mode";
	public final static String levelCompleteMode = "Level Complete Mode";
	public final static String exitMode = "Game Over Mode";
	public final static String allOverMode = "All Over Mode";
	
	public final static String levelNormalParamsTag = "levelParams";
	public final static String levelJGObjectParamsTag = "gameObjectDefinitionMap";
	public final static String levelNamesTag = "levelNames";
	
	public final static String animationTag = "Animation";
	public final static String explodeAnimationTag = "Explode";
	public final static String wordAnimationTag = "Word";
	public final static String healAnimationTag = "Heal";
	
	public final static String playAllKeyTag = "playAllKey";
	public final static String playCurrKeyTag = "playCurrKey";
	public final static String newLevelKeyTag = "newLevelKey";
	public final static String loadFileKeyTag = "loadFileKey";
	public final static String saveFileKeyTag = "saveFileKey";
	public final static String prefKeyTag = "prefKey";
	public final static String exitKeyTag = "exitKey";
	public final static String undoKeyTag = "undoKey";
	public final static String redoKeyTag = "redoKey";
	public final static String helpKeyTag = "helpKey";
	public final static String playAllTag = "playAllKey";
	public final static String playCurrTag = "playCurrKey";

	public static final String[][] shortcuts = {
		{playAllKeyTag, "R"},
		{playCurrKeyTag, "E"},
		{newLevelKeyTag, "T"},
		{loadFileKeyTag, "N"},
		{saveFileKeyTag, "S"},
		{prefKeyTag, "P"},
		{exitKeyTag, "W"},
		{undoKeyTag, "Z"},
		{redoKeyTag, "Y"},
		{helpKeyTag, "H"},
		{playAllKeyTag, "A"},
		{playCurrKeyTag, "C"}
	};
	
	public static final String[] highScoreColumns = { "Rank", "Name", "Score"};
	
	public static final String[] statTableColumns = {"Name", "Row", "Column", "Level", "Move Rate", "Attack Rate"};

	public static final String playerTag = "Standard Player";
	public static final String knightTag = "Knight";
	public static final String enemyTag = "Standard Enemy";
	public static final String gargoyleTag = "Gargoyle";
	public static final String bossTag = "Standard Boss";
	public static final String dragonTag = "Dragon";
	public static final String treeTag = "Tree";
	public static final String rockTag = "Rock";
	public static final String gemTag = "Gem";
	public static final String gobletTag = "Goblet";
	public static final String elixirTag = "Elixir";
	public static final String towerTag = "Tower";
	public static final String coinTag = "Coin";
	public static final String wateringCanTag = "Watering Can";
	public static final String flagTag = "Flag";
	public static final String potionTag = "Potion";
	public static final String shieldTag = "Shield";
	public static final String swordTag = "Sword";
	public static final String axeTag = "Axe";
	public static final String hammerTag = "Hammer";
	public static final String terrainTag = "Terrain";
	public static final String wheelbarrowTag = "Wheelbarrow";
	
	public static final String mainTypeTag = "Main";
	public static final String playerTypeTag = "Player";
	public static final String enemyTypeTag = "Enemy";
	public static final String terrainTypeTag = "Terrain";
	public static final String itemTypeTag = "Stock";
	public static final String weaponTypeTag = "Weapon";
	public static final String exitTypeTag = "Back";
	
	public static final String actionTypeTag = "Action";
	
	public static final String weaponSmallIconTag = "SmallWeapon";
	public static final String actionSelectionTag = "NewAction";
	public static final String defaultItemActionTag = "DefaultItemAction";
	
	@SuppressWarnings("serial")
	public static final Map<String, Integer> nameToCollisionID = new HashMap<String, Integer>() {
		{
			put(playerTag, playerCollisionID);
			put(knightTag, playerCollisionID);
			put(enemyTag, enemyCollisionID);
			put(gargoyleTag, enemyCollisionID);
			put(bossTag, enemyCollisionID);
			put(dragonTag, enemyCollisionID);
			put(treeTag, envCollisionID);
			put(rockTag, envCollisionID);
			put(gemTag, envCollisionID);
			put(towerTag, envCollisionID);
			put(coinTag, envCollisionID);
			put(potionTag, envCollisionID);
			put(gobletTag, envCollisionID);
			put(elixirTag, envCollisionID);
			put(shieldTag, envCollisionID);
			put(swordTag, envCollisionID);
			put(axeTag, envCollisionID);
			put(wateringCanTag, envCollisionID);
			put(wheelbarrowTag, envCollisionID);
			put(hammerTag, envCollisionID);
		}
	};
	
	@SuppressWarnings("serial")
	public static final Map<String, Integer> nameToTeamID = new HashMap<String, Integer>() {
		{
			put(playerTag, playerTeam);
			put(knightTag, playerTeam);
			put(enemyTag, enemyTeam);
			put(gargoyleTag, enemyTeam);
			put(bossTag, bossTeam);
			put(dragonTag, bossTeam);
			put(treeTag, -1);
			put(rockTag, -1);
			put(gemTag, -1);
			put(coinTag, -1);
			put(towerTag, -1);
			put(potionTag, -1);
			put(gobletTag, -1);
			put(elixirTag, -1);
			put(shieldTag, -1);
			put(swordTag, -1);
			put(axeTag, -1);
			put(hammerTag, -1);
			put(wateringCanTag, -1);
			put(wheelbarrowTag, -1);
		}
	};
	
	@SuppressWarnings("serial")
	public static final Map<String, String> nameToType = new HashMap<String, String>() {
		{
			put(playerTag, playerTypeTag);
			put(knightTag, playerTypeTag);
			put(enemyTag, enemyTypeTag);
			put(gargoyleTag, enemyTypeTag);
			put(bossTag, enemyTypeTag);
			put(dragonTag, enemyTypeTag);
			put(treeTag, terrainTypeTag);
			put(rockTag, terrainTypeTag);
			put(towerTag, terrainTypeTag);
			put(gemTag, itemTypeTag);
			put(coinTag, itemTypeTag);
			put(potionTag, itemTypeTag);
			put(gobletTag, itemTypeTag);
			put(elixirTag, itemTypeTag);
			put(wateringCanTag, itemTypeTag);
			put(wheelbarrowTag, itemTypeTag);
			put(shieldTag, weaponTypeTag);
			put(swordTag, weaponTypeTag);
			put(axeTag, weaponTypeTag);
			put(hammerTag, weaponTypeTag);
		}
	};
	
	public final static Integer defaultTileSize = 0;
	public final static Integer defaultNumberOfRows = 10;
	public final static Integer defaultNumberOfCols = 10;
	public final static Integer defaultHealth = 150;
	public final static Integer defaultDefense = 100;
	public final static Integer defaultPower = 200;
	public final static Integer defaultSpeed = 3;
	public final static Integer defaultRange = 2;
	public final static Integer defaultAIIntelligence = 99;
	public final static Integer defaultAIAttack = 15;
	public final static Integer defaultAIDefense = 0;
	public final static Integer defaultAIMoney = 20;
	public final static Integer defaultAIObjective = 10;
	public final static Integer defaultLevelCompleteScore = 100;
	public final static Integer defaultObjectiveCompleteScore = 30;
	
	public final static Integer defaultGameThumbnailWidth = 200;
	public final static Integer defaultGameThumbnailHeight = 200;
	
	public static final String defaultObjectName = "Default Object";
	
	public final static Double defaultHealthLevelUp = 0.1;
	public final static Double defaultCurrHealthLevelUp = 0.5;
	public final static Double defaultDefenseLevelUp = 0.1;
	public final static Double defaultPowerLevelUp = 0.1;
	public final static Double defaultSpeedLevelUp = 0.1;
	public final static Double defaultRangeLevelUp = 0.1;
	
	public final static int defaultWordInterval = 40;
	public final static int defaultWordHeight = 110;
	public final static double defaultWordDuration = 6;
	
	public final static Integer maxRows = 40;
	public final static Integer minRows = 2;
	public final static Integer minTileSize = 16;
	
	public final static Object[] identifiers = {"Item", "Number"};
	
	public final static String goalCreditOverTag = "Get Enough Credits";
	public final static Object[][] goalCreditOver = {
		{"Objective Name: ", String.class, goalCreditOverTag, new Boolean(false)},  
		{"Credits to win: ", Integer.class, new Integer(100), new Boolean(true)}, 
		{"Finish Within (turns): ", Integer.class, new Integer(30), new Boolean(true)}
	};
	
	public final static String goalKillAllTag = "Kill Enemies";
	public final static Object[][] goalKillAll = {
		{"Objective Name: ", String.class, goalKillAllTag, new Boolean(false)},
		{"Number of enemies that can be left: ", Integer.class, new Integer(0), new Boolean(true)},
		{"Finish Within (turns): ", Integer.class, new Integer(30), new Boolean(true)}
	};

	public final static String goalKillBossTag = "Kill Boss";
	public final static Object[][] goalKillBoss = {
		{"Objective Name: ", String.class, goalKillBossTag, new Boolean(false)},
		{"Finish Within (turns): ", Integer.class, new Integer(30), new Boolean(true)}
	};

	public final static String goalObjectsTag = "Collect Objects";
	public final static Object[][] goalObjects = {
		{"Objective Name: ", String.class, goalObjectsTag, new Boolean(false)},
		{"Number of objects that can be left: ", Integer.class, new Integer(0), new Boolean(true)},
		{"Finish Within (turns): ", Integer.class, new Integer(30), new Boolean(true)}
	};

	public final static String goalCapturePointTag = "Capture Point";
	public final static Object[][] goalCapturePoint = {
		{"Objective Name: ", String.class, goalCapturePointTag, new Boolean(false)},
		{"Capture Point X coordinate: ", Integer.class, new Integer(0), new Boolean(true)},
		{"Y coordinate: ", Integer.class, new Integer(0), new Boolean(true)},
		{"Finish Within (turns): ", Integer.class, new Integer(30), new Boolean(true)}
	};

	public final static String goalSurviveTag = "Survive";
	public final static Object[][] goalSurvive = {
		{"Objective Name: ", String.class, goalSurviveTag, new Boolean(false)},
		{"Turns to survive: ", Integer.class, new Integer(30), new Boolean(true)}
	};

	public final static Font titleFont = new Font("Roman", Font.BOLD, 16);
	public final static Font contentFont = new Font("monaco", Font.PLAIN, 15);
	public final static JGFont displayFont = new JGFont("monaco", Font.BOLD, 36);
	
	public final static Color achievedGoal = Color.red;
	
	public final static JGColor jgameBG = new JGColor(0, 150, 50);
	
	public final static int displayTime = 700;
	
	public final static FileNameExtensionFilter myImageFilter = new FileNameExtensionFilter("Image File", "bmp","jpg","jpeg","png","gif","tif");
	public final static FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("Json files", "json");
	public final static String defaultJsonExtension = ".json";
	public final static String defaultImageExtension = ".jpg";
	
	public final static void LOG(Class<?> cls, String str) {
		if (DEBUG) System.out.println(cls.toString() + " " + str);
	}

}

