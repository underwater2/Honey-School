<template>
  <div class="assignment">
  <!-- 이전 버튼 -->
    <div v-if="beforearticleidx === -1" class="beforebtn">
      <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" fill="currentColor" class="bi bi-arrow-left-circle-fill" viewBox="0 0 16 16">
          <path d="M8 0a8 8 0 1 0 0 16A8 8 0 0 0 8 0zm3.5 7.5a.5.5 0 0 1 0 1H5.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L5.707 7.5H11.5z"/>
      </svg>
    </div>
    <div v-else class="beforebtn btns">
      <svg @click="$router.push({name: 'Assignment', params: { article_id: beforearticleid }})" xmlns="http://www.w3.org/2000/svg" width="120" height="120" fill="#F52532" class="bi bi-arrow-left-circle-fill" viewBox="0 0 16 16">
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
      <svg @click="$router.push({name: 'Assignment', params: { article_id: nextarticleid }})" xmlns="http://www.w3.org/2000/svg" width="120" height="120" fill="#F52532" class="bi bi-arrow-right-circle-fill" viewBox="0 0 16 16">
          <path d="M8 0a8 8 0 1 1 0 16A8 8 0 0 1 8 0zM4.5 7.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5H4.5z"/>
      </svg>
    </div>
      <div v-if="isLoading">
        <div class="spinner-border" role="status"></div>
        <p>LOADING...</p>
      </div>
      <div v-else>
      <div class="article">
        <h1 v-if="done"><span class="badge bg-warning articleBadge">숙제 완료!</span></h1><p></p>
        <h1>{{currentarticle.board.title}}</h1>
        <span class="articleRight">
          <p class="fs-5 pb-4">작성 날짜 : 
           <span class="color-orange font-A1-500">{{ currentarticle.board.date.split(' ')[0] }}</span>
          </p>
        </span>
        <div v-for="content in currentarticle.board.content.split('\r')" :key="content">
          <h4>{{ content }}</h4>
        </div>
      </div>
      <div>
      <div v-if="done">
        <div class="articleContent">
          <div v-if="teacherCom === ''">
              <b><h3 class="py-2">선생님의 한마디</h3></b>
              <h4>아직 선생님께서 확인중이에요</h4> 
          </div>
          <div v-else>
            <h3>선생님의 한마디</h3>
            <div v-for="content in teacherCom.content.split('\r')" :key="content">
              <h3>{{ content }}</h3>
            </div>
          </div>
        </div>
        <div class="image">
          <h1><span class="badge bg-warning imageBadge">완료한 {{userinfo.name}} 학생의 숙제</span></h1>
          <img :src="`https://i6b201.p.ssafy.io:9999/file/${myimg}`" alt="&emsp; &emsp; &emsp; &emsp; 제출한 숙제를 불러올 수 없습니다. 다시 시도해주세요. &emsp; &emsp; &emsp; &emsp;" class="assignmentImg">
        </div>
      </div>
      <div class="canvas" v-else>
        <Canvas 
          :article="currentarticle"
          @submitted='submitted'
        />
      </div>
      <div class="footer"></div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, ref, watch } from 'vue'
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import axios from 'axios';
import Canvas from '../../components/Board/Canvas.vue'
import BoardArticles from "../../types/board/BoardArticles";
interface ArticleArray {
  [index: number] : any
}
interface User {
  [index: string] : any
  userId: string
}
interface Comment {
  [index: number] : any
  id: number
  user: User
  parent_id:number
}
interface File {
  [index: string] : any
  commentId: number
  stored_file_path: string
}
type Comments = Array<Comment>;
type Files = Array<File>;
interface Article {
  [index: string] : any
  comments: Comments
  files: Files

}
export default ({
  name: 'Assignment',
  components: { Canvas},
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

    let currentarticle = ref<Article>({comments:[], files:[]});
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
    let done = ref<boolean>(false)
    let teacherCom = ref<any>('')
    let myimg = ref<any>('')
    let comId = ref<any>('')
    const checkDone = () => {
      for (var i = 0; i < currentarticle.value.comments.length; ++i) {
        const userId = currentarticle.value.comments[i].user.userId
        if (userId === userinfo.userId) {
          done.value = true
          comId.value = currentarticle.value.comments[i].id
        } else if (comId.value === currentarticle.value.comments[i].parent_id) {
          teacherCom.value = currentarticle.value.comments[i]
        }
      }
      for (var j = 0; j < currentarticle.value.files.length; ++j) {
        const fileId = currentarticle.value.files[j].commentId
        if (fileId === comId.value) {
          myimg.value = currentarticle.value.files[j].stored_file_path
        }
      }
    }

    const articles = computed(() => store.state.boardStore.assignment);
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
      checkDone()
    }).then(() => {
        isLoading.value = false
    })
    const submitted = () => {
      isLoading.value = true
      articleDetail().then(()=>{
        checkDone()
      }).then(() => {
        isLoading.value = false
      })
    }

    watch(() => route.params, (newVal, oldVal) => {
      isLoading.value = true
      articleDetail().then(() => {
      done.value=false
      myimg.value=''
      comId.value=''
      teacherCom.value=''
      getCurrentArticle()
      checkDone() 
      }).then(() => {
        isLoading.value = false
      })
    })
    return { isLoading, userinfo, articles, getCurrentArticle, currentarticle, 
    beforearticleidx, nextarticleidx, beforearticleid, nextarticleid, articlesdata,
    done, comId, myimg, teacherCom, submitted }
  }
})
</script>
<style scoped>
.assignment {
  background-color: #f0e7f7;
  padding-top: 30px;
  font-family: "Gothic A1", sans-serif;
}
.assignmentImg {
  border: 3px solid black;
  border-radius: 20px;
  height: auto;
  width: 1200px;
  margin-bottom: 30px;
  padding-top:50px;
  background-color: #ffff;

}
.articleContent {
  background-color: #ffff;
  border-radius: 20px;
  margin: 15px auto;
  width: 1200px;
  padding: 15px;
  position:relative;

}
.article {
  border-radius: 20px;
  margin: 15px auto;
  width: 1200px;
  padding: 15px;
  position:relative;
}
.articleBadge {
  position:absolute;
  /* transform: rotate(-20deg); */
  /* bottom: 80px; */
  left: 0;
  top: 0;
  /* font-weight: 300; */
}
.articleRight {
  text-align: right;
}
.canvas {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
}
.nextbtn {
  float:right;
  margin-right:30px;  
}
.beforebtn {
  float:left;
  margin-left:30px;
}
.btns {
  cursor: pointer;
}
.image {
  position: relative;
}
.imageBadge {
  /* background-color: #D1D1D1; */
  position:absolute;
  top: 4px;
  left: 33%;
  right: 33%;
  /* font-weight: 300; */
}
.color-orange {
    color: #ff7f08;
}
.footer {
  height: 70px;
}
</style>