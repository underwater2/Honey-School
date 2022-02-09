<template>
<div v-if="streamManager">
	<ov-video :stream-manager="streamManager"/>
	<div><p>{{ clientData }}</p></div>	
	<div>
        <button v-if="muteStatus" @click="changeMuteStatus()">mic-on</button>
        <button v-else @click="changeMuteStatus()">mic-off</button>
	</div>
</div>
</template>

<script>
import OvVideo from './OvVideo';

export default {
	name: 'UserVideo',

	components: {
		OvVideo,
	},
	data() {
    	return {
    	  muteStatus: true,
    	};
  	},
	props: {
		streamManager: Object,
	},

	computed: {
		clientData () {
			const { clientData } = this.getConnectionData();		
			return clientData;
		}
	},

	methods: {
		getConnectionData () {						
			return JSON.parse(this.streamManager.stream.connection.data);
		},
		changeMuteStatus() {      	
      	// this.streamManager.stream.subscribeToAudio(this.muteStatus);
      	// this.muteStatus = !this.muteStatus;
    	},
	},
};
</script>
