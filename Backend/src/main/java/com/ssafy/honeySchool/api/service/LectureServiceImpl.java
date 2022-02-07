package com.ssafy.honeySchool.api.service;

import com.ssafy.honeySchool.api.request.LectureReq;
import com.ssafy.honeySchool.api.response.LectureRes;
import com.ssafy.honeySchool.common.util.ThrowingConsumer;
import com.ssafy.honeySchool.db.entity.Lecture;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service("lectureService")
public class LectureServiceImpl implements LectureService{
    WebClient webClient=WebClient.builder()
            .baseUrl("https://i6b201.p.ssafy.io:443")
            .clientConnector(
                    new ReactorClientHttpConnector(
                            HttpClient
                                    .create()
                                    .secure(
                                            ThrowingConsumer.unchecked(
                                                    sslContextSpec -> sslContextSpec.sslContext(
                                                            SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
                                                    )
                                            )
                                    ))).build();

    @Override
    public Mono<String> createLecture(LectureReq lectureReq,String header) {
        return webClient.post()
                .uri("/openvidu/api/sessions/")
                .header(HttpHeaders.AUTHORIZATION,header)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(lectureReq)
                .retrieve()
                .bodyToMono(String.class);
    }

	@Override
	public Mono<String> searchAllLecture(String header) {
		return webClient.get()
				.uri("/openvidu/api/sessions/")
				.header(HttpHeaders.AUTHORIZATION, header)				
				.retrieve()
				.bodyToMono(String.class);
	}

	@Override
	public Mono<String> deleteLecture(String sessionId, String header) {
		return webClient.delete()
				.uri("/openvidu/api/sessions/"+sessionId)
				.header(HttpHeaders.AUTHORIZATION, header)				
				.retrieve()
				.bodyToMono(String.class);
	}

	@Override
	public Mono<String> connectLecture(String sessionId, String header) throws ParseException {
		
		String input = "{\"type\": \"WEBRTC\",\r\n"
				+ "    			\"data\": \"My Server Data\",\r\n"
				+ "    			\"record\": true,\r\n"
				+ "    			\"role\": \"PUBLISHER\",\r\n"
				+ "    			\"kurentoOptions\": {\r\n"
				+ "    			    \"videoMaxRecvBandwidth\": 1000,\r\n"
				+ "    			    \"videoMinRecvBandwidth\": 300,\r\n"
				+ "    			    \"videoMaxSendBandwidth\": 1000,\r\n"
				+ "    			    \"videoMinSendBandwidth\": 300,\r\n"
				+ "    			    \"allowedFilters\": [ \"GStreamerFilter\", \"ZBarFilter\" ]\r\n"
				+ "    			}";
		
		JSONParser parser = new JSONParser();
    	Object obj = parser.parse(input);
			JSONObject jsonObj = (JSONObject) obj;
    	System.out.println(jsonObj.toString());
		return webClient.post()				
		.uri("openvidu/api/sessions/"+sessionId+"/connection")
		.header(HttpHeaders.AUTHORIZATION, header)
		.bodyValue(jsonObj)
		.retrieve()
		.bodyToMono(String.class);
	}
}
