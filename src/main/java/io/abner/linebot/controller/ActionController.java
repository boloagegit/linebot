package io.abner.linebot.controller;

import io.abner.linebot.dto.CommandDTO;
import io.abner.linebot.service.ActionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(value="ActionController")
@RequestMapping("/api/action")
public class ActionController {

    @Autowired
    private ActionService actionService;

    @ApiOperation(value="指令", notes="可做指令的CRUD")
    @ApiImplicitParam(name = "dto", value = "指令", required = true, dataType = "CommandDTO")
    @PostMapping(value = "/{type}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String doAction(@PathVariable String type, @RequestBody CommandDTO dto) {

        switch (type) {
            case "insert":
                actionService.saveOrUpdateCommand(dto);
                break;
            case "update":
                actionService.updateCommand(dto.getName(), dto.getR());
                break;
            case "delete":
                actionService.deleteCommand(dto.getName());
                break;
            default:
                break;
        }

        return "OK";
    }

    @ApiOperation(value="查詢所有指令")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CommandDTO> findAllCommand(){
        return actionService.findAllCommand();
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteCommand(@RequestParam String name){
        actionService.deleteCommand(name);
        return "OK";
    }
}
