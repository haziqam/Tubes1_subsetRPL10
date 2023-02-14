package Services;

import Enums.*;
import Models.*;


import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private boolean afterburnerStatus;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        
        playerAction.heading = new Random().nextInt(360);
        playerAction.action = PlayerActions.FORWARD;      

        if (!gameState.getGameObjects().isEmpty()) {
           var foodList = gameState.getGameObjects()
                   .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                   .sorted(Comparator
                           .comparing(item -> getDistanceBetween(bot, item)))
                   .collect(Collectors.toList());

           playerAction.heading = getHeadingBetween(foodList.get(0));
       }

        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        //diganti dari var ke double
        double triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        double triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        //diganti dari var ke int
        int direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private double getBotRadius() {
        //method baru, digunakan untuk mendapatkan jarak bot dengan titik pusat (0,0)
        double currentX = this.getBot().getPosition().getX();
        double currentY = this.getBot().getPosition().getY();
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    private int getHeadingToCenter() {
        //method baru, digunakan untuk mendapatkan heading ke titik pusat (0,0)
        double currentX = this.getBot().getPosition().getX();
        double currentY = this.getBot().getPosition().getY();
        int headingToCenter = 180 + toDegrees(Math.atan2(currentY, currentX));
        return (headingToCenter) % 360;
    }

    private int getWorldRadius() {
        //method baru, digunakan untuk mendapatkan radius (ukuran) dari peta saat ini, 
        //karena ukuran peta akan selalu berkurang menurut aturan game
        if (gameState.getWorld().getRadius() == null) {
            // pada saat game dimulai, terdapat delay selama beberapa tick yang menyebabkan radius bernilai null
            return 0;
        }
        return gameState.getWorld().getRadius();
    }

    private double getEffectiveDistanceTo(GameObject other) {
        return getDistanceBetween(this.getBot(), other) - this.getBot().getSize() - other.getSize();
    }
}
