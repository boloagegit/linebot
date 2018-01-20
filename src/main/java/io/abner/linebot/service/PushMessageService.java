package io.abner.linebot.service;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import io.abner.linebot.dto.FriendInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Service
public class PushMessageService {

    @Autowired
    private LineMessagingService lineMessagingService;

    public String pushMessage(String message, FriendInfoDTO dto){
        return  pushMessage(message, dto.getGroupId(), dto.getUserId());
    }

    public String pushMessage(String message, String groupId, String userId){
        TextMessage textMessage = new TextMessage(message);
        PushMessage pushMessage = new PushMessage(
                (StringUtils.isNotBlank(groupId) ? groupId : userId),
                textMessage
        );

        Response<BotApiResponse> apiResponse = null;
        try {
            apiResponse = lineMessagingService
                    .pushMessage(pushMessage)
                    .execute();
            return String.format("Sent messages: %s %s", apiResponse.message(), apiResponse.code());
        } catch (IOException e) {
            e.printStackTrace();
            return String.format("Error in sending messages : %s", e.toString());
        }
    }
}
