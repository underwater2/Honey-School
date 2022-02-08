<template>
<div v-if="streamManager">
	<ov-video :stream-manager="streamManager"/>
	<div><p>{{ clientData }}</p></div>	
</div>
</template>

<script>
import OvVideo from './OvVideo';

export default {
	name: 'UserVideo',

	components: {
		OvVideo,
	},

	props: {
		streamManager: Object,
	},

	computed: {
		clientData () {
			const { clientData } = this.getConnectionData();
			console.log("클라이언트");
			console.log(clientData);
			return "권씨";
			// return "clientData";
		},
	},

	methods: {
		getConnectionData () {
			const { connection } = this.streamManager.stream;			
			console.log("커넥션");
			console.log(connection);
			let a = JSON.parse("{\r\"clientData\":\"참가자요\"\r}");
			console.log("a : "+a); 
			return JSON.parse("{\"clientData \":\"참가자요\"}");
			// return JSON.parse(connection.data);
		},
	},
};
</script>
