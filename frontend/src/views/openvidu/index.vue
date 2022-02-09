<template>
	<div id="main-container" class="container">
		<div id="join" v-if="!session">
			<div id="img-div"><img src="resources/images/openvidu_grey_bg_transp_cropped.png" /></div>
			<div id="join-dialog" class="jumbotron vertical-center">
				<h1>Join a video session</h1>
				<div class="form-group">
					<p>
						<label>Participant</label>
						<input v-model="myUserName" class="form-control" type="text" required>
					</p>
					<p>
						<label>Session</label>
						<input v-model="mySessionId" class="form-control" type="text" required>
					</p>
					<p class="text-center">
						<button class="btn btn-lg btn-success" @click="joinSession()">Join!</button>
						<button class="btn btn-lg btn-info" @click="SearchAllSession()">Check Session!</button>
						<button class="btn btn-lg btn-danger" @click="CloseSession()">Delete Session!</button>						
					</p>
				</div>
			</div>
		</div>

		<div id="session" v-if="session">
			<div id="session-header">
				<h1 id="session-title">{{ mySessionId }}</h1>
				<input class="btn btn-large btn-danger" type="button" id="buttonLeaveSession" @click="leaveSession" value="Leave session">
			</div>
			<div id="main-video" class="col-md-6">
				<user-video :stream-manager="mainStreamManager" />
			</div>
			<div id="video-container" class="col-md-6">
				<user-video :stream-manager="publisher" />
				<user-video v-for="sub in subscribers" :key="sub.stream.connection.connectionId" :stream-manager="sub"/>
			</div>
		</div>
	</div>
</template>

<script>
import axios from 'axios';
import { OpenVidu } from 'openvidu-browser';
import UserVideo from './UserVideo';
axios.defaults.headers.post['Content-Type'] = 'application/json';

const OPENVIDU_SERVER_URL = "https://i6b201.p.ssafy.io:443";
const OPENVIDU_SERVER_SECRET = "ssafy";

export default {
	name: 'App',

    components: {
		UserVideo,
	},

	data () {
		return {
			OV: undefined,
			session: undefined,
			mainStreamManager: undefined,
			publisher: undefined,
			subscribers: [],			
			mySessionId: 'SessionA',
			myUserName: 'Participant' + Math.floor(Math.random() * 100),
			connectionId: "",
		}
	},

	methods: {
		joinSession () {
			// --- Get an OpenVidu object ---

			// 세션이 존재하지 않는 경우
				alert("세션 만들기!");

				this.OV = new OpenVidu();			

				// --- Init a session ---
				this.session = this.OV.initSession();			

				// --- Specify the actions when events take place in the session ---

				// On every new Stream received...
				this.session.on('streamCreated', ({ stream }) => {
					const subscriber = this.session.subscribe(stream);
					this.subscribers.push(subscriber);
				});

				// On every Stream destroyed...
				this.session.on('streamDestroyed', ({ stream }) => {
					const index = this.subscribers.indexOf(stream.streamManager, 0);
					if (index >= 0) {
						this.subscribers.splice(index, 1);
					}
				});

				// On every asynchronous exception...
				this.session.on('exception', ({ exception }) => {
					console.warn(exception);
				});

				// --- Connect to the session with a valid user token ---

				// 'getToken' method is simulating what your server-side should do.
				// 'token' parameter should be retrieved and returned by your own backend


				this.getToken(this.mySessionId).then(token => {
					this.session.connect(token, { clientData: this.myUserName })
						.then(() => {															
							// --- Get your own camera stream with the desired properties ---

							let publisher = this.OV.initPublisher(undefined, {
								audioSource: undefined, // The source of audio. If undefined default microphone
								videoSource: undefined, // The source of video. If undefined default webcam
								publishAudio: true,  	// Whether you want to start publishing with your audio unmuted or not
								publishVideo: true,  	// Whether you want to start publishing with your video enabled or not
								resolution: '640x480',  // The resolution of your video
								frameRate: 30,			// The frame rate of your video
								insertMode: 'APPEND',	// How the video is inserted in the target element 'video-container'
								mirror: false       	// Whether to mirror your local video or not
							});

							this.mainStreamManager = publisher;
							this.publisher = publisher;

							// --- Publish your stream ---

							this.session.publish(this.publisher);
						})
						.catch(error => {
							console.log('There was an error connecting to the session:', error.code, error.message);
						});
				});		
			window.addEventListener('beforeunload', this.leaveSession)
		},
		checkSession(sessionId){
			axios.get(process.env.VUE_APP_API_URL+"/lecture/search?sessionId="+sessionId)
			.then((response)=>{
				console.log(response.data);
				if(response.data==null) return false;
				return true;
			})
			.catch((error)=>{
				alert(error);
			}
			)			
		},
		leaveSession () {
			// --- Leave the session by calling 'disconnect' method over the Session object ---			
			axios.delete(process.env.VUE_APP_API_URL+"/lecture/connect?sessionId="+this.mySessionId+"&connectionId="+this.connectionId)
				.then((response)=>{
					console.log(response);
				})
				.catch((error)=>{
					alert("세션 나가기 오류");
				})			
			if(this.session) this.session.disconnect();

			this.session = undefined;
			this.mainStreamManager = undefined;
			this.publisher = undefined;
			this.subscribers = [];
			this.OV = undefined;


			this.connectionId = "";

			window.removeEventListener('beforeunload', this.leaveSession);
		},

		updateMainVideoStreamManager (stream) {
			if (this.mainStreamManager === stream) return;
			this.mainStreamManager = stream;
		},

		/**
		 * --------------------------
		 * SERVER-SIDE RESPONSIBILITY
		 * --------------------------
		 * These methods retrieve the mandatory user token from OpenVidu Server.
		 * This behavior MUST BE IN YOUR SERVER-SIDE IN PRODUCTION (by using
		 * the API REST, openvidu-java-client or openvidu-node-client):
		 *   1) Initialize a Session in OpenVidu Server	(POST /openvidu/api/sessions)
		 *   2) Create a Connection in OpenVidu Server (POST /openvidu/api/sessions/<SESSION_ID>/connection)
		 *   3) The Connection.token must be consumed in Session.connect() method
		 */

		getToken (mySessionId) {
			return this.createSession(mySessionId).then(sessionId => this.createToken(sessionId));
		},

		// See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-session
		createSession (sessionId) {		


			return new Promise((resolve, reject) =>{
				axios.post(process.env.VUE_APP_API_URL+"/lecture", {
					customSessionId: sessionId})
					.then(response=>response.data)
					.then(data => resolve(data.id))
					.catch(error => {
						if (error) {
							resolve(sessionId);
						} else {
							console.warn(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`);
							if (window.confirm(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`)) {
								location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`);
							}
							reject(error.response);
						}
					});
			});
		},		

		SearchAllSession(){
			axios.get(process.env.VUE_APP_API_URL+"/lecture/search/all")
			.then((response)=>{
				console.log(response.data);				
			})
			.catch((error)=>{
				alert(error);
			}
			)
		},
		
		CloseSession(){
			const headers = {
				"Authorization": "OPENVIDUAPP:ssafy"
			}
			axios.delete(process.env.VUE_APP_API_URL+"/lecture?sessionId="+this.mySessionId,{headers})
			.then(()=>{
				return;
			})
			.catch((error)=>{
				alert(error);
			}
			)
		},

		joinConnection(sessionId){		
			const headers = {
				"Authorization": "OPENVIDUAPP:ssafy",
			}
			return new Promise((resolve, reject) => {
				axios.post(process.env.VUE_APP_API_URL+"/lecture/connect",{
					customSessionId: sessionId,
				},{headers})
				.then(response => response.data)
				.then((data) =>{ 
					console.log("토큰 받기!!")
					console.log(data)
					resolve(data.token)
				}
				)				 
				.catch(error =>
					reject(error.response));				
			})
		},


		// See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-connection
		createToken (sessionId) {
			return this.joinConnection(sessionId);
		},
	}
}
</script>
