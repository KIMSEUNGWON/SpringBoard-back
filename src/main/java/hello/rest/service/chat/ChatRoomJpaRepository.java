package hello.rest.service.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    @Query("select new hello.rest.service.chat.ChatRoomDto(cr.roomId, cr.name) from ChatRoom cr")
    List<ChatRoomDto> findAllRooms();

    @Query("select new hello.rest.service.chat.ChatRoomDto(cr.roomId, cr.name) from ChatRoom cr where cr.id = :roomId")
    Optional<ChatRoomDto> findByRoomId(Long roomId);
}
