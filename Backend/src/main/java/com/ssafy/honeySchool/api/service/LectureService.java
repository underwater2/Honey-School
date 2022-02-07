package com.ssafy.honeySchool.api.service;

import org.json.simple.parser.ParseException;

import com.ssafy.honeySchool.api.request.LectureReq;
import com.ssafy.honeySchool.api.response.LectureRes;
import com.ssafy.honeySchool.db.entity.Lecture;

import reactor.core.publisher.Mono;

public interface LectureService {
    Mono<String> createLecture(LectureReq lectureReq, String header);
    Mono<String> searchAllLecture(String header);
    Mono<String> deleteLecture(String sessionId, String header);
    Mono<String> connectLecture(String sessionId, String header) throws ParseException;
}
