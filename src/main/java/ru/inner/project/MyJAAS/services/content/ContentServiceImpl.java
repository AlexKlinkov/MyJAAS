package ru.inner.project.MyJAAS.services.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ContentServiceImpl {

    public List<byte[]> getContent(String pathToNecessaryResource) throws IOException {
        log.debug("getContent method in ContentServiceImpl was started at " + LocalDateTime.now());
        ClassPathResource imgFile = new ClassPathResource(pathToNecessaryResource);
        List<byte[]> result = new ArrayList<>();
        try {
            byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
            result.add(bytes);
        } catch (IOException ex) {
            throw new IOException("Content with path - " + pathToNecessaryResource + " doesn't exist");
        }
        log.debug("getContent method in ContentServiceImpl is returning the result - " + result);
        return result;
    }
}
