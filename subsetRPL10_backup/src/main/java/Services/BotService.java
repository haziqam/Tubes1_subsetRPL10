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
    private int tickTracker = 0;

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
        // var objectList = gameState.getGameObjects();
       
        int worldRadius = getWorldRadius();
        double botRadius = getBotRadius();
        int botSize = getBot().getSize();
        double nearRadius = 0.2 * worldRadius; 

        List<GameObject> playerList = gameState.getPlayerGameObjects().stream()
                                    .sorted((Comparator
                                    .comparing(item -> getDistanceBetween(bot, item))))
                                    .collect(Collectors.toList());

        List<GameObject> nearbyObjectList = gameState.getGameObjects()
                                            .stream().filter(item -> getDistanceBetween(bot, item) <= nearRadius)
                                            .sorted(Comparator
                                            .comparing(item -> item.getGameObjectType().getProfit()))
                                            .collect(Collectors.toList());       

        if (worldRadius == 0 || playerList == null) {
            //selama delay tidak perlu mengganti action atau heading
            return;
        }

        playerList.remove(0);   
        //elemen pertama dihapus karena bot terdekat adalah bot sendiri, jadi tidak dijadikan pertimbangan

        List<GameObject> nearbyPlayerList = playerList.stream()
                                            .filter(player -> this.getEffectiveDistanceTo(player) <= nearRadius)
                                            .collect(Collectors.toList());

        if (!nearbyPlayerList.isEmpty()) {
            boolean allSmaller = true;
            GameObject largestEdiblePlayer = null;
            GameObject dangerousPlayer = null;
            int maxPlayerSize = -1;

            System.out.println(this.getBot().torpedoSalvoCount);

            for (GameObject player : nearbyPlayerList) {
                if (player.getSize() > this.getBot().getSize()) {
                    dangerousPlayer = player;
                    allSmaller = false;
                    break;
                }
                else if (player.getSize() > maxPlayerSize) {
                    maxPlayerSize = player.getSize();
                    largestEdiblePlayer = player;
                }
            }

            if (allSmaller) {
                playerAction.heading = getHeadingBetween(largestEdiblePlayer);
                playerAction.action = PlayerActions.FORWARD;
            }
            else {
                if (this.getBot().getSize() > 50) {
                    if (this.getBot().torpedoSalvoCount == 5) {
                        playerAction.heading = getHeadingBetween(dangerousPlayer);
                        playerAction.action = PlayerActions.FIRETORPEDOES;
                        tickTracker++;
                    }
                    else {
                        playerAction.heading = (getHeadingBetween(dangerousPlayer) + 180) % 360;
                        playerAction.action = PlayerActions.STARTAFTERBURNER;
                        this.afterburnerStatus = true;
                    }
                }
                else {
                    playerAction.heading = (getHeadingBetween(dangerousPlayer) + 180) % 360;
                    playerAction.action = PlayerActions.FORWARD;
                }
            }
            
            this.playerAction = playerAction;
            return;
        }        
        
        if (this.afterburnerStatus) {
            playerAction.action = PlayerActions.STOPAFTERBURNER;
            this.afterburnerStatus = false;
            this.playerAction = playerAction;
            return;
        }

        if (botRadius + 2 * botSize >= worldRadius - 80) {
            playerAction.heading = getHeadingToCenter();
            playerAction.action = PlayerActions.FORWARD;
            this.playerAction = playerAction;
            return;
        }
        
        if (this.getBot().getSize() >= 50) {
            if (this.getBot().torpedoSalvoCount == 5 && tickTracker % 10 == 0) {
                GameObject targetBot = playerList.stream()
                                        .max(Comparator.comparing(player -> player.getSize()))
                                        .get();
                playerAction.heading =  getHeadingBetween(targetBot);
                playerAction.action = PlayerActions.FIRETORPEDOES;
                tickTracker++;
                this.playerAction = playerAction;
                return;
            }   
        }

        if (!nearbyObjectList.isEmpty()) {
            GameObject mostDangerousObj = nearbyObjectList.get(nearbyObjectList.size()-1);
            GameObject mostProfitableObj = nearbyObjectList.get(0);

            if (mostDangerousObj.getGameObjectType() == ObjectTypes.SUPERNOVABOMB) {
                playerAction.heading = (getHeadingBetween(mostDangerousObj) + 90) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
            else if (mostDangerousObj.getGameObjectType() == ObjectTypes.TORPEDOSALVO) {
                if (this.getBot().getSize() > 50) {
                    if (this.getBot().shieldCount > 0) {
                        playerAction.action = PlayerActions.USESHIELD;
                    }
                }
                else {
                    playerAction.heading = (getHeadingBetween(mostDangerousObj) + 90) % 360;
                    playerAction.action = PlayerActions.FORWARD;
                }
            }
            // mungkin bisa diganti jadi if gascloud in nearby objectlist
            else if (mostDangerousObj.getGameObjectType() == ObjectTypes.GASCLOUD) {
                playerAction.heading = (getHeadingBetween(mostDangerousObj) + 180) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
            else {
                playerAction.heading = getHeadingBetween(mostProfitableObj);
                playerAction.action = PlayerActions.FORWARD;
            }
            this.playerAction = playerAction;
            return;
        }
       
       // Default action jika tidak ada kondisi di atas yang memenuhi: FORWARD
        playerAction.action = PlayerActions.FORWARD;
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
