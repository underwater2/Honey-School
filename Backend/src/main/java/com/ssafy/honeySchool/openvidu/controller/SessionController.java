package com.ssafy.honeySchool.openvidu.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.RecordingMode;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "http://localhost:8080/")
@RequestMapping("/api/v1/openvidu")
public class SessionController {

	private static RestTemplate restTemplate;
	// OpenVidu object as entrypoint of the SDK
	private OpenVidu openVidu;

	// Collection to pair session names and OpenVidu Session objects
	private Map<String, Session> mapSessions = new ConcurrentHashMap<>();
	// Collection to pair session names and tokens (the inner Map pairs tokens and
	// role associated)
	private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

	// URL where our OpenVidu server is listening
	private String OPENVIDU_URL = "";
	// Secret shared with our OpenVidu server
	private String SECRET = "";

	public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
		System.out.println("오픈비두 만들기 진입 "+secret+" "+openviduUrl);
		this.SECRET = secret;
		this.OPENVIDU_URL = openviduUrl;
		this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
	}

	@PostMapping("/session")
	public String createSession(@RequestBody Map<String, String> map) {
		System.out.println("세선만들기 접속");
		String sessionName = map.get("session-name");
		System.out.println(map.toString());
		if(this.openVidu==null) System.out.println("오픈 비두 생성되지 않았다.");
		SessionProperties properties = new SessionProperties.Builder().recordingMode(RecordingMode.MANUAL).customSessionId(sessionName).build();
		
		try {			
//			Session session = this.openVidu.createSession(properties);
			long start = System.currentTimeMillis();
			String result = initSession();
			long end = System.currentTimeMillis();
			System.out.println(start);
			System.out.println(end);
			System.out.println(result);
			return result;
		} catch (Exception e) {
			return "세션만들기 실패";
		}		
	}
	public static String initSession(){
		WebClient client = WebClient.create("https://i6b201.p.ssafy.io:443/api");
		System.out.println("이까지는 옴");
		client = WebClient.builder()
				.defaultHeader("auth", "username: OPENVIDUAPP, password: ssafy")
				.build();
		String json = client.post()
		.uri("/sessions")
		.retrieve()
		.bodyToMono(String.class)
		.block();
		
		System.out.println("이까지는 옴2");
		return json;
	}
	
	@RequestMapping(value = "/leave-session", method = RequestMethod.POST)
	public String removeUser(@RequestParam(name = "session-name") String sessionName,
			@RequestParam(name = "token") String token, Model model, HttpSession httpSession) throws Exception {

		try {
			checkUserLogged(httpSession);
		} catch (Exception e) {
			return "index";
		}
		System.out.println("Removing user | sessioName=" + sessionName + ", token=" + token);

		// If the session exists ("TUTORIAL" in this case)
		if (this.mapSessions.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {

			// If the token exists
			if (this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
				// User left the session
				if (this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
					// Last user left: session must be removed
					this.mapSessions.remove(sessionName);
				}
				return "redirect:/dashboard";

			} else {
				// The TOKEN wasn't valid
				System.out.println("Problems in the app server: the TOKEN wasn't valid");
				return "redirect:/dashboard";
			}

		} else {
			// The SESSION does not exist
			System.out.println("Problems in the app server: the SESSION does not exist");
			return "redirect:/dashboard";
		}
	}

	private void checkUserLogged(HttpSession httpSession) throws Exception {
		if (httpSession == null || httpSession.getAttribute("loggedUser") == null) {
			throw new Exception("User not logged");
		}
	}

}
