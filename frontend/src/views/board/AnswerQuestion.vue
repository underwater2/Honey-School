<template>
    <!-- 이전 버튼 -->
  <div v-if="beforearticleidx === -1" class="beforebtn">
    <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" fill="currentColor" class="bi bi-arrow-left-circle-fill" viewBox="0 0 16 16">
        <path d="M8 0a8 8 0 1 0 0 16A8 8 0 0 0 8 0zm3.5 7.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z"/>
    </svg>
  </div>
  <div v-else class="beforebtn btns">
    <svg  @click="$router.push({name: 'AnswerQuestion', params: { article_id: beforearticleid }})" xmlns="http://www.w3.org/2000/svg" width="120" height="120" fill="yellow" class="bi bi-arrow-left-circle-fill" viewBox="0 0 16 16">
        <path d="M8 0a8 8 0 1 0 0 16A8 8 0 0 0 8 0zm3.5 7.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z"/>
    </svg>
  </div>

    <!-- 이후 버튼 -->
  <div v-if="nextarticleidx === -1" class="nextbtn">
    <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" fill="currentColor" class="bi bi-arrow-right-circle-fill" viewBox="0 0 16 16">
      <path d="M8 0a8 8 0 1 1 0 16A8 8 0 0 1 8 0zM4.5 7.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5H4.5z"/>
    </svg>
  </div>
  <div v-else class="nextbtn btns">
    <svg @click="$router.push({name: 'AnswerQuestion', params: { article_id: nextarticleid }})" xmlns="http://www.w3.org/2000/svg" width="120" height="120" fill="yellow" class="bi bi-arrow-right-circle-fill" viewBox="0 0 16 16">
      <path d="M8 0a8 8 0 1 1 0 16A8 8 0 0 1 8 0zM4.5 7.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5H4.5z"/>
    </svg>
  </div>

  <div style="background-color:#F99D07">
    <div class="note">
       <div v-if="isLoading">
        <div class="spinner-border" role="status"></div>
        <p>LOADING...</p>
      </div>
      <div>
        <div class="content">
          <div class="anscontent">
            <h2 class="card-title">질문 내용</h2>
            <hr align="center" noshade>
            <p class="fs-5 my-3">작성 날짜 : <span class="color-orange font-A1-500">{{currentarticle.board.date.split(" ")[0] }}</span></p>
            <a :href="`https://i6b201.p.ssafy.io:9999/file/${currentarticle.files[0].stored_file_path}`" target='_black'> <img src="@/assets/board/recordfile.png" alt="" class="fileimg">질문 내용 듣기</a>
          </div>
          <div class="anscontent">
            <h2 class="card-title">선생님의 답변</h2>
            <hr align="center" noshade>
            <div v-if="currentarticle.comments.length > 0">
              <div v-for="idx in currentarticle.comments.length" :key="idx">
                 <p class="fs-5 my-3">작성자 : {{ currentarticle.board.user.name }} &nbsp; | &nbsp; 작성 날짜 : <span class="color-orange font-A1-500">{{currentarticle.board.date.split(" ")[0] }}</span></p>
                <div v-for="content in currentarticle.comments[idx-1].content.split('\r')" :key="content">
                  <p>{{ content }}</p>
                </div>
              </div>
            </div>
            <div v-else>
                <p class="py-2">아직 작성된 답변이 없습니다. 답변을 기다려주세요.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, ref, watch } from 'vue'
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import axios from "axios";
import BoardArticles from "../../types/board/BoardArticles";
import router from '../../router';
interface ArticleArray {
    [index: number] : any
}

export default {
  name:"AnswerQuestion",

  setup() {
    const store = useStore();
    const route = useRoute();

    let isLoading = ref<boolean>(true);
    const localStorageData = localStorage.getItem("vuex");
    let userinfoData;
    if (localStorageData !== null) {
    userinfoData = JSON.parse(localStorageData);
    }
    let userinfo = userinfoData.accountStore.userinfo;

    let currentarticle = ref({});
    const articleDetail = () => {
      return axios.get(process.env.VUE_APP_API_URL+"/board/class/detail",{
        params:{
        school: userinfo.school,
        grade: userinfo.grade,
        classes: userinfo.class_number,
        id : route.params.article_id
        }
      })
      .then((response)=>{
        currentarticle.value = response.data
      })
    }

    const articles = computed(() => store.state.boardStore.question);
    const articlesdata = articles.value as ArticleArray
    
    let nextarticleidx = ref<number>(articles.value.length);
    let nextarticleid = ref<number>(0);

    let beforearticleidx = ref<number>(-1);
    let beforearticleid = ref<number>(0);

  const getCurrentArticle = () => {
    for (var i = 0; i < articles.value.length; ++i) {
      const article = articlesdata[i] as BoardArticles
    if (article.id === +route.params.article_id) {
        if (i == 0) {
          beforearticleidx.value = -1
        }
        if (i >= 1) {
          beforearticleidx.value = i - 1
          beforearticleid.value = articlesdata[i - 1].id
        }
        if (i < articles.value.length - 1) {
          nextarticleidx.value = i + 1
          nextarticleid.value = articlesdata[i + 1].id
        } else if (i === articles.value.length -1) {
          nextarticleidx.value = -1
        }
        break }
      } 
    }
    articleDetail().then(() => {
      getCurrentArticle()
      isLoading.value = false
    })
      
    watch(() => route.params, (newVal, oldVal) => {
      isLoading.value = true
      articleDetail().then(() => {
      getCurrentArticle()
      isLoading.value = false
      })
    })
    return { userinfo, isLoading, articles, getCurrentArticle, currentarticle, beforearticleidx, nextarticleidx, beforearticleid, nextarticleid, articlesdata }
  }
}
</script>
<style scoped>
.note {
  margin: auto;
  background-image: url('../../assets/board/ans-background.png');
  height: 90vh;
  width: 150vh;
  background-position: center;
  background-size: contain;
  background-repeat: no-repeat;
  font-family: "Gothic A1", sans-serif;
}
.btns {
  cursor: pointer;
}
button {
  margin:5px;
}

.nextbtn {
  float:right;
  margin:80px; 
}
.beforebtn {
  float:left;
  margin:80px;
}
.content {
  padding-top: 20vh;
  margin-bottom: 50px;
}
.content > p {
  margin-right: 25vh;
}
.anscontent {
  border-radius: 18px;
  /* box-shadow: 3px 3px 5px rgba(0,0,0,0.2);
   */
  border: 0.5px solid rgba(0, 0, 0, 0.2);
  border-radius: 20px;
  height: 55vh;
  width: 600px;
  margin: 0 30px;
  padding: 30px 10px;
}
.fileimg {
  width: 30px;
  height: 30px;
}
.content {
  display: flex;
  justify-content: space-around;
}
.font-A1-500{
  font-family: 'Gothic A1', sans-serif;
  font-weight: 500;
  color: #ff7f08;
}
hr {
  border: none;
  border-top: 5px dotted rgb(0, 0, 0);
  color: #fff;
  background-color: #fff;
  height: 1px;
  width: 90%;
  margin: 10px auto;
}

</style>
