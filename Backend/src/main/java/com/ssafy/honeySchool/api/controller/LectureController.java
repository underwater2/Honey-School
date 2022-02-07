package com.ssafy.honeySchool.api.controller;

import com.ssafy.honeySchool.api.request.LectureReq;
import com.ssafy.honeySchool.api.service.LectureService;
import io.swagger.annotations.Api;
import reactor.core.publisher.Mono;

import java.util.Base64;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "강의 API", tags = {"Lecture"})
@RestController
@RequestMapping("/api/v1/lecture")
public class LectureController {
    @Autowired
    LectureService lectureService;

    private String defaultHeader = "OPENVIDUAPP:ssafy";
    @PostMapping()
    public Mono<String> createLecture(@RequestBody LectureReq lectureReq, @RequestHeader(value = "Authorization") String head){    	

    	String header = "Basic "+ Base64.getEncoder().encodeToString(head.getBytes());
    	System.out.println("real Header :"+header);
        return lectureService.createLecture(lectureReq,header);
        
    }
    @GetMapping()
    public Mono<String> searchAllLecture(@RequestHeader(value = "Authorization") String head) {
    	String header = "Basic "+ Base64.getEncoder().encodeToString(head.getBytes());
    	return lectureService.searchAllLecture(header);
    }
    @DeleteMapping() 
    public Mono<String> deleteLecture(@RequestParam(value = "sessionId") String sessionId, @RequestHeader(value = "Authorization") String head){
    	String header = "Basic "+ Base64.getEncoder().encodeToString(head.getBytes());    	
    	return lectureService.deleteLecture(sessionId, header);
    }
    
    @PostMapping("/connect")
    public Mono<String> connectLecture( @RequestHeader(value = "sessionId") String sessionId) throws ParseException{    	    	
    	System.out.println("Connect연결");
    	return lectureService.connectLecture("sessionId", "Basic "+Base64.getEncoder().encodeToString(this.defaultHeader.getBytes()));    	
    }

}
