package io.abner.linebot.repository;

import io.abner.linebot.dto.FriendInfoDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendInfoRepository extends MongoRepository<FriendInfoDTO, Long> {

    FriendInfoDTO findTopByUserId(String userId);

    FriendInfoDTO findTopByGroupId(String groupId);

    void deleteByDesc(String desc);

    FriendInfoDTO findTopByDesc(String desc);
}
