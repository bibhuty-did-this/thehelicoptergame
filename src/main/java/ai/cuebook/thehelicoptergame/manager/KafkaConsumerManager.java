package ai.cuebook.thehelicoptergame.manager;

import ai.cuebook.thehelicoptergame.dao.redis.KeyValueDAO;
import ai.cuebook.thehelicoptergame.entity.kafka.WarEvent;
import ai.cuebook.thehelicoptergame.enums.Actions;
import ai.cuebook.thehelicoptergame.enums.Player;
import ai.cuebook.thehelicoptergame.enums.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KafkaConsumerManager {

    private final Long FIVE_MINUTES=300000L;
    private final Integer ROTATE=10;
    private static final String BATCH = "BATCH";
    private static final String TIME = "TIME";

    @Autowired
    private KeyValueDAO keyValueDAO;


    public void processMessage(WarEvent warEvent){
        Actions action=warEvent.getAction();
        switch (action){
            /**
             * Assumption:
             *  + Only one helicopter at a time
             *  + Sample space of x-axis(0-10) and y-axis(-10,10)
             *  + Default speed 1 unit per second
             *  + Getting out of sample space the helicopter will crash and no one will get points
             * */
            case START_HELICOPTER:

                // Get the helicopter count if present or create now one
                String totalHelicopters=keyValueDAO.getById(action.toString(),RedisKeys.TOTAL_HELICOPTERS.toString());
                int helicopterCount=1;
                if(!StringUtils.isEmpty(totalHelicopters)){
                    helicopterCount=Integer.parseInt(totalHelicopters)+1;
                }
                totalHelicopters=Integer.toString(helicopterCount);

                // Take care of the constrain of 5 helicopter in 5 min
                if(helicopterCount>5){
                    String fifthPreviousHelicopterStartTime=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER.toString().concat(Integer.toString(helicopterCount-5)));
                    if(!StringUtils.isEmpty(fifthPreviousHelicopterStartTime)){
                        Long time=Long.parseLong(fifthPreviousHelicopterStartTime);
                        if(time-System.currentTimeMillis()>FIVE_MINUTES){
                            System.out.println("Can not fly another helicopter as there would be more than 5 within 5 minutes");
                            break;
                        }
                    }
                }

                System.out.println("Starting to fly the helicopter");

                // Increase total helicopter count
                keyValueDAO.update(action.toString(),RedisKeys.TOTAL_HELICOPTERS.toString(),totalHelicopters);

                // Set their initial position and initial speed
                keyValueDAO.add(action.toString(),RedisKeys.HELICOPTER_Y.toString(),"10");
                keyValueDAO.add(action.toString(),RedisKeys.HELICOPTER_X.toString(),"-10");
                keyValueDAO.add(action.toString(),RedisKeys.HELICOPTER_SPEED.toString(),"1");
                keyValueDAO.add(action.toString(),RedisKeys.HELICOPTER_PREVIOUS_ACTION_TIME.toString(),Long.toString(System.currentTimeMillis()));
                keyValueDAO.add(action.toString(),RedisKeys.HELICOPTER_PREVIOUS_ACTION_TIME.toString(),Long.toString(System.currentTimeMillis()));
                keyValueDAO.add(action.toString(),RedisKeys.HELICOPTER_BOMB_PERSONNEL.toString(),Integer.toString(4));

                // Set the helicopter's start time in cache
                keyValueDAO.add(action.toString(),RedisKeys.HELICOPTER.toString().concat(totalHelicopters),Long.toString(System.currentTimeMillis()));

                break;

            /**
             * Assumption:
             *  + Can't go down below ground level or it'll crash
             * */
            case GO_DOWN:

                // Get the coordinates
                String helicopterMovementY=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_Y.toString());
                String helicopterMovementX=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_X.toString());
                String helicopterSpeed=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_SPEED.toString());
                String currentHelicopterPreviousTime=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_PREVIOUS_ACTION_TIME.toString());

                // See if the helicopter is in range in x-axis
                int xAxisCurrent=Integer.parseInt(helicopterMovementX)+(Integer.parseInt(helicopterSpeed)*(int)((System.currentTimeMillis()-Long.parseLong(currentHelicopterPreviousTime))/1000));
                if(xAxisCurrent>10){
                    System.out.println("helicopter crashed");

                    // Whenever a helicopter crashed,just start another one
                    warEvent.setAction(Actions.START_HELICOPTER);

                    processMessage(warEvent);
                    break;
                }

                // Upper shift is not allowed
                int shiftY=warEvent.getMovement();
                if(shiftY>0) {
                    System.out.println("Not possible to go up");
                    break;
                }

                // Calculate the position when it goes down
                int currentY=Integer.parseInt(helicopterMovementY);
                int nowY=currentY-shiftY;

                // Helicopter can't go below ground
                if(nowY<0){
                    System.out.println("Helicopter is crashed");
                    // Whenever a helicopter crashed,just start another one
                    warEvent.setAction(Actions.START_HELICOPTER);
                    break;
                }

                // If everything goes fine then helicopter is in a correct position
                String position=Integer.toString(nowY);
                keyValueDAO.update(action.toString(), RedisKeys.HELICOPTER_Y.toString(),position);
                break;
            case DRIVE_FAST:

                // Get the current speed also before increasing it calculate the current position
                String currentHelicopterSpeed=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_SPEED.toString());
                String currentHelicopterPosition=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_X.toString());
                String previousTimeWhenSpeedWasRecorded=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_PREVIOUS_ACTION_TIME.toString());

                // See if the helicopter is in range in x-axis or it is crashed
                int currentHorizontalPosition=Integer.parseInt(currentHelicopterPosition)+(Integer.parseInt(currentHelicopterSpeed)*(int)((System.currentTimeMillis()-Long.parseLong(previousTimeWhenSpeedWasRecorded))/1000));
                if(currentHorizontalPosition>10){
                    System.out.println("helicopter crashed");
                    // Whenever a helicopter crashed,just start another one
                    warEvent.setAction(Actions.START_HELICOPTER);
                    break;
                }

                // Increase the helicopter speed and store the time when it's speed was increased
                keyValueDAO.update(action.toString(),RedisKeys.HELICOPTER_PREVIOUS_ACTION_TIME.toString(),Long.toString(System.currentTimeMillis()));
                keyValueDAO.update(action.toString(),RedisKeys.HELICOPTER_SPEED.toString(),Integer.toString(Integer.parseInt(currentHelicopterSpeed)+1));
                break;
            /**
             * Assumption:
             *  + Height doesn't matter to bomb personell, they can jump from anywhere
             *  + They can only be killed when they are at the ground
             *  + They move at speed 1 unit per second towards the gun
             */
            case DROP_BOMB_PERSONEL:

                // See if we are eligible to drop or not
                String totalBombPersonellFlying=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_BOMB_PERSONNEL.toString());

                if(StringUtils.isEmpty(totalBombPersonellFlying)){
                    System.out.println("No bomb personell to drop");
                    break;
                }


                int totalPersonell=Integer.parseInt(totalBombPersonellFlying);

                // See if the helicopter is in range in x-axis or it is crashed
                String previousDropPosition=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_X.toString());
                String helicopterSpeedAtPreviousDropPosition=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_PREVIOUS_ACTION_TIME.toString());
                int currentDropPosition=Integer.parseInt(previousDropPosition)+(Integer.parseInt(helicopterSpeedAtPreviousDropPosition)*(int)((System.currentTimeMillis()-Long.parseLong(helicopterSpeedAtPreviousDropPosition))/1000));

                if(currentDropPosition>10){
                    System.out.println("Helicopter crashed");
                    // Whenever a helicopter crashed,just start another one
                    warEvent.setAction(Actions.START_HELICOPTER);
                    break;
                }

                // Update bomb personell and location of current person and the current x position of soldier and time of drop
                keyValueDAO.add(warEvent.getPlayerNo().toString().concat(BATCH),RedisKeys.GROUND_SOLDIER_DROP_POSITION.toString().concat(Integer.toString(totalPersonell)),Integer.toString(currentDropPosition));
                keyValueDAO.add(warEvent.getPlayerNo().toString().concat(TIME),RedisKeys.GROUND_SOLDIER_DROP_TIME.toString().concat(Integer.toString(totalPersonell)),Long.toString(System.currentTimeMillis()));
                keyValueDAO.update(action.toString(),RedisKeys.HELICOPTER_BOMB_PERSONNEL.toString(),Integer.toString(totalPersonell-1));

                break;
            /**
             * Assumption:
             *  + Rotate 10deg to left
             *  + If it goes beyond 180 deg then then don't allow it
             * */
            case ROTATE_LEFT:
                String currentAngleForLeftRotation=keyValueDAO.getById(action.toString(),RedisKeys.GUN_ANGLE.toString());
                if(StringUtils.isEmpty(currentAngleForLeftRotation)){
                    currentAngleForLeftRotation=Integer.toString(Math.min(180,Integer.parseInt(currentAngleForLeftRotation)+ROTATE));
                }else{
                    currentAngleForLeftRotation=Long.toString(90);
                }
                keyValueDAO.update(action.toString(),RedisKeys.GUN_ANGLE.toString(),currentAngleForLeftRotation);
                break;
            /**
             * Assumption:
             *  + Rotate 10deg to right
             *  + If it goes beyond 0 deg then then don't allow it
             * */
            case ROTATE_RIGHT:
                String currentAngleForRightRotation=keyValueDAO.getById(action.toString(),RedisKeys.GUN_ANGLE.toString());
                if(StringUtils.isEmpty(currentAngleForRightRotation)){
                    currentAngleForRightRotation=Integer.toString(Math.max(0,Integer.parseInt(currentAngleForRightRotation)-ROTATE));
                }else{
                    currentAngleForRightRotation=Long.toString(90);
                }
                keyValueDAO.update(action.toString(),RedisKeys.GUN_ANGLE.toString(),currentAngleForRightRotation);
                break;
            /**
             * Assumption:
             *  + Bomb personells can only be killed if they are at x-axis or inside helicopter, not in air
             * Logic:
             *  + Find the line slope of helicopter using y/x and convert it into degrees, in case the absolute differnece
             *    of both the slopes is in between 10 degrees, the helicopter dies and the points will be awarded to the
             *    shooter along with the number of personnels inside the helicopter.
             *  + When you are shooting horizontally you are killing all the bomb personell who are in that direction.
             *  + The bomb personell needs to hit the gun 10 times for it to destroy and once they are down they start
             *    hitting once per second from any direction.
             *
             */
            case SHOOT:
                String helicopterAtY=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_Y.toString());
                String helicopterPreviousPositionAtX=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_X.toString());
                String throttleOfHelicopter=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_SPEED.toString());
                String recordedTimeOfThrottleSpeed=keyValueDAO.getById(action.toString(),RedisKeys.HELICOPTER_PREVIOUS_ACTION_TIME.toString());

                // See if the helicopter is in range in x-axis
                int helicopterAtX=Integer.parseInt(helicopterPreviousPositionAtX)+(Integer.parseInt(throttleOfHelicopter)*(int)((System.currentTimeMillis()-Long.parseLong(recordedTimeOfThrottleSpeed))/1000));
                if(helicopterAtX>10){
                    System.out.println("helicopter crashed");

                    // Whenever a helicopter crashed,just start another one
                    warEvent.setAction(Actions.START_HELICOPTER);

                    processMessage(warEvent);
                    break;
                }
                int helicopterAngle=(int)((Math.atan((Double.parseDouble(helicopterAtY)/(double)helicopterAtX))*180)/ Math.PI);
                int gunAngle= Integer.parseInt(keyValueDAO.getById(action.toString(),RedisKeys.GUN_ANGLE.toString()));

                int score=0;
                // If the gun is horizontal and shooting it'll kill the personells also on the ground
                if(gunAngle<=10){

                }else if(gunAngle>=170){

                }

                // Condition for helicopter being dead
                if(Math.abs(helicopterAngle-gunAngle)<=10){
                    System.out.println("Helicopter is crashed");
                    score+=50;
                    if(Player.FIRST_PLAYER.equals(warEvent.getPlayerNo())){
                        String points=keyValueDAO.getById(Actions.PLAYER_1_SCORE.toString(),RedisKeys.PLAYER_1_POINTS.toString());
                        if(!StringUtils.isEmpty(points)){
                            score+=Integer.parseInt(points);
                        }
                        keyValueDAO.update(Actions.PLAYER_1_SCORE.toString(),RedisKeys.PLAYER_1_POINTS.toString(),Integer.toString(score));
                    }else{
                        String points=keyValueDAO.getById(Actions.PLAYER_2_SCORE.toString(),RedisKeys.PLAYER_2_POINTS.toString());
                        if(!StringUtils.isEmpty(points)){
                            score+=Integer.parseInt(points);
                        }
                        keyValueDAO.update(Actions.PLAYER_2_SCORE.toString(),RedisKeys.PLAYER_2_POINTS.toString(),Integer.toString(score));
                    }

                    // Whenever a helicopter crashed,just start another one
                    warEvent.setAction(Actions.START_HELICOPTER);
                    break;
                }

                break;
            /**
             * Logic:
             *  + This method either needs to be run using a cron or front-end needs to call it time and again
             *    using the exposed API, then only it'll work.
             */
            case BOMB_PERSONELL_SHOOTS_BACK:

                break;
            default:
                System.out.println("Please specify a proper action");
                break;
        }
    }
}
