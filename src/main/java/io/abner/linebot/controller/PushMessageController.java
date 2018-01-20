package io.abner.linebot.controller;

import io.abner.linebot.dto.FriendInfoDTO;
import io.abner.linebot.repository.FriendInfoRepository;
import io.abner.linebot.service.PushMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/push")
@Api(value = "PushMessageController", description = "")
public class PushMessageController {

    private static final Logger logger = LoggerFactory.getLogger(PushMessageController.class);

    @Autowired
    private FriendInfoRepository friendInfoRepository;

    @Autowired
    private PushMessageService pushMessageService;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String pushMessage(@RequestParam String message, @RequestParam(required = false) String groupId, @RequestParam(required = false) String userId) {
        if (StringUtils.isBlank(groupId) && StringUtils.isBlank(userId)) {
            logger.info("userId and groupId can not be empty in the same time!");
        }

        return pushMessageService.pushMessage(message, groupId, userId);
    }

    @ApiOperation(value="查詢所有已知用戶")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<FriendInfoDTO> findAll() {
        return friendInfoRepository.findAll();
    }


    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteFriendInfo(@RequestParam String desc){
        friendInfoRepository.deleteByDesc(desc);
        return "OK";
    }
}
