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
        int worldRadius = getWorldRadius();
        double botRadius = getBotRadius();
        int botSize = getBot().getSize();

        //List yang berisi bot lain terurut berdasarkan jarak
        List<GameObject> dangerousPlayerList = gameState.getPlayerGameObjects().stream()
                                    .filter(item -> (item.getSize() > botSize && getDistanceBetween(bot,item) <0.4*worldRadius))
                                    .sorted((Comparator
                                    .comparing(item -> getDistanceBetween(bot, item))))
                                    .collect(Collectors.toList());

        //List yang berisi object lain yang berbahaya
        List<GameObject> avoidObjectList = gameState.getGameObjects().stream()
                                    .filter(item -> ((item.getGameObjectType() == ObjectTypes.GASCLOUD || item.getGameObjectType() == ObjectTypes.TORPEDOSALVO || item.getGameObjectType() == ObjectTypes.SUPERNOVABOMB || item.getGameObjectType() == ObjectTypes.ASTEROIDFIELD) && getDistanceBetween(bot,item) < 0.2*worldRadius))
                                    .sorted(Comparator
                                    .comparing(item -> getDistanceBetween(bot,item)))
                                    .collect(Collectors.toList());
        
        //List yang berisi object yang menguntungkan
        List<GameObject> goodList = gameState.getGameObjects().stream()
                                    .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPERFOOD || item.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP))
                                    .sorted(Comparator
                                    .comparing(item -> item.getGameObjectType().getProfit()))
                                    .collect(Collectors.toList());
        
        //List yang berisi pemain yang dapat dimakan
        List<GameObject> ediblePlayerList = gameState.getPlayerGameObjects().stream()
                                    .filter(item -> item.getSize() < botSize)
                                    .sorted((Comparator
                                    .comparing(item -> item.getSize())))
                                    .collect(Collectors.toList());

        List<GameObject> teleporterList = gameState.getPlayerGameObjects().stream()
                                    .filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                                    .collect(Collectors.toList());
        if (worldRadius == 0 || dangerousPlayerList == null) {
            //selama delay tidak perlu mengganti action atau heading
            return;
        }

        //Menghindari ujung map
        if (botRadius + 2 * botSize >= worldRadius - 80) {
            playerAction.heading = getHeadingToCenter();
            playerAction.action = PlayerActions.FORWARD;
            this.playerAction = playerAction;
            return;
        }

        if (!dangerousPlayerList.isEmpty() && !avoidObjectList.isEmpty()){
            double dangerousPlayerDistance = getDistanceBetween(bot, dangerousPlayerList.get(0));
            double avoidObjectDistance = getDistanceBetween(bot, dangerousPlayerList.get(0));

            if (dangerousPlayerDistance >= avoidObjectDistance) {
                this.playerAction = avoidPlayer(dangerousPlayerList);
            }
            else{
                this.playerAction = avoidObject(avoidObjectList);
            }
            return;
        }
        else if (!dangerousPlayerList.isEmpty()){
            //Jika terdeteksi bot yang lebih besar
            this.playerAction = avoidPlayer(dangerousPlayerList);
            return;
        }
        else if (!avoidObjectList.isEmpty()){
            //Jika terdeteksi benda berbahaya
            this.playerAction = avoidObject(avoidObjectList);
            return;
        }

        if(this.afterburnerStatus) {
            this.afterburnerStatus = false;
            playerAction.action = PlayerActions.STOPAFTERBURNER;
            this.playerAction = playerAction;
            return;
        }

        //Tidak ada yang perlu dihindari, bot bisa mencari objek yang menguntungkan
        
        if(!ediblePlayerList.isEmpty() ){
            GameObject priority = ediblePlayerList.get(0);

            if(!teleporterList.isEmpty()) {
                if(getDistanceBetween(teleporterList.get(0), priority) < 10) {
                    playerAction.action = PlayerActions.TELEPORT;
                    playerAction.heading = getHeadingBetween(priority);
                }
            }
            else if(this.getBot().teleporterCount > 0 && (botSize - priority.getSize() >30)){
                playerAction.action = PlayerActions.FIRETELEPORTER;
                playerAction.heading = getHeadingBetween(priority);
            }
            else {
                playerAction.heading = getHeadingBetween(priority);
                playerAction.action = PlayerActions.FORWARD;
            }
            return;
        }

        if (!goodList.isEmpty()){
            GameObject priority = goodList.get(0);
            playerAction.heading = getHeadingBetween(priority);
            playerAction.action = PlayerActions.FORWARD;
            return;
        }

       // Default action jika tidak ada kondisi di atas yang memenuhi: FORWARD
        playerAction.action = PlayerActions.FORWARD;
        this.playerAction = playerAction;
        return;
    }

    public PlayerAction avoidPlayer(List<GameObject> dangerousPlayerList){
        GameObject nearestPlayer = dangerousPlayerList.get(0);
        int botSize = getBot().getSize();


        if (botSize > 50){
            if(this.getBot().torpedoSalvoCount == 5){
                playerAction.heading = getHeadingBetween(nearestPlayer);
                playerAction.action = PlayerActions.FIRETORPEDOES;
            }
            else {
                playerAction.heading = (getHeadingBetween(nearestPlayer)+180)%360;
                playerAction.action = PlayerActions.FORWARD;
            }
        }
        else if (botSize >40){
            playerAction.heading = (getHeadingBetween(nearestPlayer)+180)%360;
            playerAction.action = PlayerActions.STARTAFTERBURNER;
            this.afterburnerStatus = true;
        }
        else {
            playerAction.heading = (getHeadingBetween(nearestPlayer)+180)%360;
            playerAction.action = PlayerActions.FORWARD;
        }   
        return playerAction;
    }


    public PlayerAction avoidObject(List<GameObject> avoidObjectList){
        GameObject nearestObject = avoidObjectList.get(0);
        int botSize = getBot().getSize();

        if (nearestObject.getGameObjectType() == ObjectTypes.ASTEROIDFIELD) {
            playerAction.heading = (getHeadingBetween(nearestObject) + 90)%360;
            playerAction.action = PlayerActions.FORWARD;
        }
        else if (nearestObject.getGameObjectType() == ObjectTypes.GASCLOUD  || nearestObject.getGameObjectType() == ObjectTypes.SUPERNOVABOMB){
            playerAction.heading = (getHeadingBetween(nearestObject) + 180)%360;
            playerAction.action = PlayerActions.FORWARD;
        }
        else if (nearestObject.getGameObjectType() == ObjectTypes.TORPEDOSALVO){
            if (botSize > 80) {
                if (this.getBot().shieldCount > 0) {
                    playerAction.action = PlayerActions.USESHIELD;
                }
            }
            else if (botSize > 50) {
                if (this.getBot().torpedoSalvoCount == 5){
                    playerAction.heading = getHeadingBetween(nearestObject);
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                }
            }
            else {
                playerAction.heading = (getHeadingBetween(nearestObject) + 90) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
        }
        return playerAction;   
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
