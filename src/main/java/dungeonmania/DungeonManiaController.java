package dungeonmania;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

public class DungeonManiaController {
    private Game game = null;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException(dungeonName + " is not a dungeon that exists");
        }

        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }

        try {
            GameBuilder builder = new GameBuilder(configName, dungeonName);
            game = builder.buildGame();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return null;
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.tick(itemUsedId));
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        return ResponseBuilder.getDungeonResponse(game.tick(movementDirection));
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        List<String> validBuildables = List.of("bow", "shield", "midnight_armour", "sceptre");
        if (!validBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Only bow, shield, midnight_armour and sceptre can be built");
        }

        return ResponseBuilder.getDungeonResponse(game.build(buildable));
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.interact(entityId));
    }

    /**
     * /game/save
     * @throws IOException
     * @throws NullPointerException
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        String path = System.getProperty("user.dir").toString() + "/saves";
        if (!FileLoader.directoryExists(path)) {
            System.out.println("Could not create saves directory");
            return null;
        }
        path = path + "/" + name + ".json";
        FileWriter file = null;
        GameState gameState = new GameState(game);
        JSONObject gameData = gameState.toJSON();
        gameData.put("name", name);
        try {
            file = new FileWriter(path);
            file.write(gameData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (file != null) {
                    file.flush();
                    file.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        String path = System.getProperty("user.dir").toString() + "/saves/" + name + ".json";
        String dataString = "{\"yes\":\"yes\"}";
        FileReader file = null;
        try {
            file = new FileReader(path);
            dataString = new String(Files.readAllBytes(Paths.get(path)));
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file == null) {
            throw new IllegalArgumentException("Unkown save: " + name);
        }
        JSONObject data = new JSONObject(dataString);
        JSONObject config = data.getJSONObject("config");
        JSONObject dungeon = data.getJSONObject("dungeon");
        try {
            GameBuilder builder = new GameBuilder(name, config, dungeon);
            game = builder.buildGame();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        String path = System.getProperty("user.dir").toString() + "/saves";
        // Source: https://stackabuse.com/java-list-files-in-a-directory/
        try {
            File f = new File(path);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".json");
                }
            };
            File[] files = f.listFiles(filter);
            List<String> fileNames = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                fileNames.add(files[i].getName().substring(0, files[i].getName().length() - 5));
            }
            return fileNames;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(
            int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException {
        // let maze be a 2D array of booleans (of size width and height) default false
        // // false representing a wall and true representing empty space
        int height = Math.abs(yStart - yEnd) + 1;
        int width = Math.abs(xStart - xEnd) + 1;
        boolean[][] boolMap = new boolean[width][height];
        Random random = new Random();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boolMap[i][j] = false;
            }
        }
        // maze[start] = empty
        boolMap[xStart][yStart] = true;
        // let options be a list of positions
        // add to options all neighbours of 'start' not on boundary that are of distance 2 away and are walls
        List<Position> options = new ArrayList<>();
        options.addAll(addNeighbours(boolMap, false, new Position(xStart, yStart), 2));
        // while options is not empty:
        while (!options.isEmpty()) {
            // let next = remove random from options
            int randIndex = random.nextInt(options.size());
            Position next = options.remove(randIndex);
            // let neighbours = each neighbour of distance 2 from next not on boundary that are empty
            List<Position> neighbours = new ArrayList<>();
            neighbours.addAll(addNeighbours(boolMap, true, next, 2));
            // if neighbours is not empty:
            if (!neighbours.isEmpty()) {
            // let neighbour = random from neighbours
                randIndex = random.nextInt(neighbours.size());
                Position neighbour = neighbours.remove(randIndex);
                // maze[ next ] = empty (i.e. true)
                boolMap[next.getX()][next.getY()] = true;
                // maze[ position inbetween next and neighbour ] = empty (i.e. true)
                if (next.getX() == neighbour.getX()) { // up down inline
                    int yOffset = (neighbour.getY() - next.getY()) / 2;
                    boolMap[next.getX()][next.getY() + yOffset] = true;
                } else if (next.getY() == neighbour.getY()) { // left right inline
                    int xOffset = (neighbour.getX() - next.getX()) / 2;
                    boolMap[next.getX() + xOffset][next.getY()] = true;
                } else { // diagonal
                    boolean leftRight = random.nextBoolean(); // if we go left right to get to the neighbour
                    if (leftRight) {
                        boolMap[neighbour.getX()][next.getY()] = true;
                    } else {
                        boolMap[next.getX()][neighbour.getY()] = true;
                    }
                }
                // maze[ neighbour ] = empty (i.e. true)
                boolMap[neighbour.getX()][neighbour.getY()] = true;
            }
            // add to options all neighbours of 'next' not on boundary that are of distance 2 away and are walls
            options.addAll(addNeighbours(boolMap, false, next, 2));
        }
        // // at the end there is still a case where our end position isn't connected to the map
        // // we don't necessarily need this, you can just keep randomly generating maps (was original intention)
        // // but this will make it consistently have a pathway between the two.
        // if maze[end] is a wall:
        if (!boolMap[xEnd][yEnd]) {
            // maze[end] = empty
            boolMap[xEnd][yEnd] = true;
            // let neighbours = neighbours not on boundary of distance 1 from maze[end]
            List<Position> neighbours = new ArrayList<>();
            neighbours.addAll(addNeighbours(boolMap, true, new Position(xEnd, yEnd), 1));
            // if there are no cells in neighbours that are empty:
            if (neighbours.isEmpty()) {
                // let's connect it to the grid
                // let neighbour = random from neighbours
                neighbours.addAll(addNeighbours(boolMap, false, new Position(xEnd, yEnd), 1));
                int randIndex = random.nextInt(neighbours.size());
                Position neighbour = neighbours.get(randIndex);
                // maze[neighbour] = empty
                boolMap[neighbour.getX()][neighbour.getY()] = true;
            }
        }
        JSONObject dungeon = boolMapToJSON(boolMap, xStart, yStart, xEnd, yEnd);
        try {
            GameBuilder builder = new GameBuilder(configName, dungeon);
            game = builder.buildGame();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject boolMapToJSON(boolean[][] map, int xStart, int yStart, int xEnd, int yEnd) {
        JSONObject dungeon = new JSONObject();
        JSONArray entities = new JSONArray();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (!map[i][j]) { // wall!
                    JSONObject wall = new JSONObject();
                    wall.put("type", "wall");
                    wall.put("x", i);
                    wall.put("y", j);
                    entities.put(wall);
                }
            }
        }
        JSONObject player = new JSONObject();
        player.put("type", "player");
        player.put("x", xStart);
        player.put("y", yStart);
        entities.put(player);

        JSONObject exit = new JSONObject();
        exit.put("type", "exit");
        exit.put("x", xEnd);
        exit.put("y", yEnd);
        entities.put(exit);

        dungeon.put("entities", entities);
        dungeon.put("goal-condition", new JSONObject("{\"goal\": \"exit\"}"));
        return dungeon;
    }

    private List<Position> addNeighbours(
        boolean[][] boolMap,
        boolean b,
        Position fromPos,
        int distance
        ) {
        List<Position> optionsCopy = new ArrayList<>();
        int x = fromPos.getX();
        int y = fromPos.getY();
        for (int i = -distance; i < distance; i++) {
            for (int j = -distance; j < distance; j++) {
                if (Math.abs(i) + Math.abs(j) == distance) { // check distance
                    if (
                        x + i >= 0
                        && x + i < boolMap.length
                        && y + j >= 0
                        && y + j < boolMap[0].length
                    ) { // checking in bounds
                        if (boolMap[x + i][y + j] == b) {
                            optionsCopy.add(new Position(x + i, y + j));
                        }
                    }
                }
            }
        }
        return optionsCopy;
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        return null;
    }
}
