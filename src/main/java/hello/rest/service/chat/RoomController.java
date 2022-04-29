package hello.rest.service.chat;

import hello.rest.advice.exception.CResourceNotExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/chat")
@Slf4j
public class RoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @GetMapping("/rooms")
    public List<ChatRoomDto> rooms() {
        log.info("# All Chat Rooms");
//        List<ChatRoomDto> result = chatRoomRepository.findAllRooms();
        List<ChatRoomDto> result = chatRoomJpaRepository.findAllRooms();
        return result;
    }

    @PostMapping("/room")
    public ChatRoomDto create(@RequestParam String name) {
        log.info("# Create Chat Room, name: {}", name);
//        ChatRoomDto result = chatRoomRepository.createChatRoomDto(name);
        ChatRoom room = chatRoomJpaRepository.save(ChatRoom.create(name));
        ChatRoomDto result = new ChatRoomDto(room.getRoomId(), room.getName());
        return result;
    }

    @GetMapping("/room/{roomId}")
    public ChatRoomDto getRoom(@PathVariable Long roomId) {
        log.info("# get Chat Room, roomId: {}", roomId);
//        ChatRoomDto result = chatRoomRepository.findRoomById(roomId);
//        ChatRoomDto result = chatRoomJpaRepository.findByRoomId(roomId).orElseThrow(CResourceNotExistException::new);
        ChatRoom room = chatRoomJpaRepository.findById(roomId).orElseThrow(CResourceNotExistException::new);
        ChatRoomDto result = new ChatRoomDto(room.getRoomId(), room.getName());
        return result;
    }
}
