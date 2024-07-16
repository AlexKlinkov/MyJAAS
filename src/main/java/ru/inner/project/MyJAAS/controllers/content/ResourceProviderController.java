package ru.inner.project.MyJAAS.controllers.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.inner.project.MyJAAS.services.content.ContentServiceImpl;
import ru.inner.project.MyJAAS.utils.GetPetitionerInformation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class ResourceProviderController {

    @Autowired
    private ContentServiceImpl contentService;
    @Autowired
    private GetPetitionerInformation petitionerInformation;

    @GetMapping("/api/v1/authorized/resources/free")
    @ResponseBody
    public ResponseEntity<?> getFreeResource() throws IOException {
        return new ResponseEntity<>(contentService.getContent(getResourceUrl("FREE")).get(0),
                getNecessaryResponseHttpHeaders(MediaType.IMAGE_JPEG), HttpStatus.OK);
    }

    @GetMapping("/api/v1/authorized/resources/standard")
    @ResponseBody
    public ResponseEntity<?> getStandardResource() throws IOException {
        if (!isResourceAccessibleByUser(Set.of("STANDARD", "PREMIUM"))) {
            return new ResponseEntity<>("This resource doesn't available, you need have at least standard " +
                    "subscription", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(contentService.getContent(getResourceUrl("STANDARD")).get(0),
                getNecessaryResponseHttpHeaders(MediaType.IMAGE_JPEG), HttpStatus.OK);
    }

    @GetMapping("/api/v1/authorized/resources/premium")
    @ResponseBody
    public ResponseEntity<?> getPremiumResource() throws IOException {
        if (!isResourceAccessibleByUser(Set.of("PREMIUM"))) {
            return new ResponseEntity<>("This resource doesn't available, you need have premium subscription",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(contentService.getContent(getResourceUrl("PREMIUM")).get(0),
                getNecessaryResponseHttpHeaders(MediaType.IMAGE_JPEG), HttpStatus.OK);
    }

    // Auxiliaries methods
    private boolean isResourceAccessibleByUser(Set<String> subscription) {
        return subscription.contains(petitionerInformation.getUserSubscriptionWhoMakesQuery());
    }

    private String getResourceUrl(String levelOfSubscription) {
        Map<String, String> dictionaryWithUrl = new HashMap<>();
        dictionaryWithUrl.put("FREE", "static/test_resources/free_resource.jpeg");
        dictionaryWithUrl.put("STANDARD", "static/test_resources/standard_resource.jpeg");
        dictionaryWithUrl.put("PREMIUM", "static/test_resources/premium_resource.jpeg");
        return dictionaryWithUrl.get(levelOfSubscription);
    }

    // it's necessary to tell browsers how to render data which was returned user as an answer
    private HttpHeaders getNecessaryResponseHttpHeaders(MediaType mediaType) {
        HttpHeaders responseHeaders = new HttpHeaders(); // Create HttpHeaders with appropriate content type
        responseHeaders.setContentType(mediaType);
        return responseHeaders;
    }

}
