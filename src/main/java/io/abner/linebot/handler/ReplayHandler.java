package io.abner.linebot.handler;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import io.abner.linebot.dto.CommandDTO;
import io.abner.linebot.dto.FriendInfoDTO;
import io.abner.linebot.repository.CommandRepository;
import io.abner.linebot.repository.FriendInfoRepository;
import io.abner.linebot.service.ActionService;
import io.abner.linebot.service.PushMessageService;
import io.abner.linebot.utility.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@LineMessageHandler
public class ReplayHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReplayHandler.class);

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private FriendInfoRepository friendInfoRepository;

    @Autowired
    private ActionService actionService;

    @Autowired
    private PushMessageService pushMessageService;

    @Value("${line.bot.admin.id}")
    private String adminUserId;

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        logger.info("event : {}", event);

    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        logger.info("event : {}", event);

        String returnMessage = "";

        if (StringUtils.startsWith(event.getMessage().getText(), "#")) {
            String message = StringUtils.substring(event.getMessage().getText(), 1);

            if (checkSpecificCommand(message, event)) {
                return;
            }

            CommandDTO commandDTO = commandRepository.findTopByName(message);

            if (commandDTO != null) {
                returnMessage = commandDTO.getR();
            }

            if (StringUtils.equals(message, "指令")) {
                List<CommandDTO> dtoList = commandRepository.findAllByNameNot(message);
                returnMessage = returnMessage + "\n" + dtoList.stream().map(dto -> dto.getName() + " : " + dto.getR()).collect(Collectors.joining("\n"));
            }

            if (StringUtils.isNotBlank(returnMessage)) {
                replyText(event.getReplyToken(), returnMessage);
            }
        }
    }

    private boolean checkSpecificCommand(String message, MessageEvent<TextMessageContent> event) {
//        String userId = event.getSource().getUserId();
//        if (!StringUtils.equals(userId, adminUserId)) {
//            return false;
//        }

        String command = StringUtils.substring(message, 0, 2);
        String[] args = message.split(" ");
        switch (command) {
            case "新增":
                if (args.length > 2) {
                    CommandDTO dto = new CommandDTO();
                    dto.setName(args[1]);
                    dto.setR(args[2]);
                    actionService.saveOrUpdateCommand(dto);
                    replyText(event.getReplyToken(), args[0] + " 指令執行成功!");
                    return true;
                }
                break;
            case "註冊":
                if (args.length > 1) {
                    saveFriendInfo(event.getSource(), args[1]);
                    replyText(event.getReplyToken(), args[0] + " 註冊執行成功!");
                    return true;
                }
                break;
            case "推播":
                if (args.length > 2) {
                    FriendInfoDTO friendInfoDTO = friendInfoRepository.findTopByDesc(args[1]);
                    if(friendInfoDTO != null){
                        pushMessageService.pushMessage(args[2], friendInfoDTO);
                    }
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
    }

    private void saveFriendInfo(Source source, String message) {
        FriendInfoDTO dto = null;
        UserSource userSource = null;
        GroupSource groupSource = null;

        if (source instanceof UserSource) {
            userSource = (UserSource) source;
            dto = friendInfoRepository.findTopByUserId(userSource.getUserId());
        } else if (source instanceof GroupSource) {
            groupSource = (GroupSource) source;
            dto = friendInfoRepository.findTopByGroupId(groupSource.getGroupId());
        }

        if (dto == null) {
            dto = new FriendInfoDTO();
            dto.setId(IdUtils.generateId());
            if (userSource != null) {
                dto.setUserId(userSource.getUserId());
            } else if (groupSource != null) {
                dto.setGroupId(groupSource.getGroupId());
            }

            dto.setDesc(message);
            friendInfoRepository.save(dto);
        }
    }

    private void replyText(String replyToken, String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }

        this.reply(replyToken, new TextMessage(message));
    }

    private void reply(String replyToken, Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(String replyToken, List<Message> messages) {
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
