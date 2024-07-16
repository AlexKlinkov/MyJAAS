package ru.inner.project.MyJAAS.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;

import java.util.*;

@UtilityClass
public class ResourceListWithEndpointInfo {
    // This method allows to get necessary information about url endpoint, also it's possible (on developing level) to
    // add info to each existent endpoint abiding an order for each point.
    public static List<Object> getResourceInfo(String keyUrl) {
        Map<String, List<Object>> dictionaryWithUrl = new HashMap<>();
        dictionaryWithUrl.put("/api/v1/authorized/resources/free", List.of(MediaType.IMAGE_JPEG));
        dictionaryWithUrl.put("/api/v1/authorized/resources/standard", List.of(MediaType.IMAGE_JPEG));
        dictionaryWithUrl.put("/api/v1/authorized/resources/premium", List.of(MediaType.IMAGE_JPEG));
        return dictionaryWithUrl.get(keyUrl);
    }

}
