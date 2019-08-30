package ai.cuebook.thehelicoptergame.controllers;

import ai.cuebook.thehelicoptergame.dto.request.EventRequest;
import ai.cuebook.thehelicoptergame.dto.request.StartGameRequest;
import ai.cuebook.thehelicoptergame.entity.mongo.WarGame;
import ai.cuebook.thehelicoptergame.manager.WarGameManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("warGame")
public class WarGameController {

    @Autowired
    private WarGameManager warGameManager;

    @RequestMapping(value = "/startWar",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WarGame startWar(@RequestBody StartGameRequest startGameRequest){
        return warGameManager.startWar(startGameRequest);
    }

    @RequestMapping(value = "/startFirstRound",method = RequestMethod.POST)
    @ResponseBody
    public void startFirstRound(){
        warGameManager.startFirstRound();
    }

    @RequestMapping(value = "/startSecondRound",method = RequestMethod.POST)
    @ResponseBody
    public void startSecondRound(){
        warGameManager.startSecondRound();
    }

    @RequestMapping(value = "/pushEventToQueue",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void pushEventToQueue(@RequestBody EventRequest eventRequest){
        warGameManager.pushEventToQueue(eventRequest);
    }



}
