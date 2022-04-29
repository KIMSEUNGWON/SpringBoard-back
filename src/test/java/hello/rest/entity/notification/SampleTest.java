package hello.rest.entity.notification;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class SampleTest {

    @Autowired
    SampleRepository sampleRepository;

    @Test
    @Rollback(false)
    public void sampleSaveTest() throws Exception {
        //given
        //when
        sampleRepository.save(new Sample(UUID.randomUUID().toString(), NotificationType.Notice));
        sampleRepository.save(new Sample(UUID.randomUUID().toString(), NotificationType.Comment));
        sampleRepository.save(new Sample(UUID.randomUUID().toString(), NotificationType.Reply));
        sampleRepository.save(new Sample(UUID.randomUUID().toString(), NotificationType.Like));

        //then
        List<Sample> all = sampleRepository.findAll();
        for (Sample sample : all) {
            System.out.println("sample = " + sample.getNotificationType().getValue());
        }
    }
}