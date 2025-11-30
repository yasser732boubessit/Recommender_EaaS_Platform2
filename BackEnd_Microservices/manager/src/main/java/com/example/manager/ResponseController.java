package com.example.manager;
import com.example.manager.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@RestController
@RequestMapping("/responses")
public class ResponseController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/all")
    public List<RequestResponse> getAllResponses() {
        return mongoTemplate.findAll(RequestResponse.class, "responses");
    }
}
