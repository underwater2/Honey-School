<template>
  <div class="font-A1">

    <div v-if="isLoading">
      <div class="spinner-border" role="status"></div>
      <p>LOADING...</p>
    </div>
    <!-- Article Detail -->
    <div v-else class="container">
      <div class="row row-padding">
        <div class="btns px-0">
        </div>
        <div class="btns" v-if="currentarticle.board.user.userId === userinfo.userId">
            <button 
            type="button" class="btn buttonall btn-link text-decoration-none px-0"
            @click="$router.go(-1)"
            >
              &lt; 전체 게시판
            </button>
            <button 
              type="button" class="btn btn-link update-btn text-decoration-none"
              @click="$router.push({ name: 'ArticleUpdate', params: { article_id: id }})"
            >
              수정
            </button>
          <button 
            type="button" class="btn btn-link delete-btn text-decoration-none"
            @click="deleteArticle"
          >
            삭제
          </button>
        </div>
        <div class="card-content mx-0 col-12">
          <h4 class="py-2 px-3 text-left"><b>{{ currentarticle.board.title }}</b></h4>
          <hr>
          <div class="px-3">
            <p class="fs-6 d-flex justify-content-between">
              <span>{{ currentarticle.board.user.name }}</span>
              <span>{{ currentarticle.board.date }}</span>
            </p>

            <div v-if="currentarticle.board.content.length > 0" class="content-container">
              <div class="pt-5" pb-4>
                <div v-for="content in currentarticle.board.content.split('\r')" :key="content">
                  <p>{{ content }}</p>
                </div>
              </div>
            </div>
                <!-- <div v-if="currentarticle.board.category==='photo'" class="img-container">
                    <img :src="currentarticle.files" alt="img" class="img-fluid">
                </div> -->
            <div v-if="currentarticle.files.length > comments.files.length" class="content-container">
              <p><b>첨부파일</b></p>
              <div v-for="idx in currentarticle.files.length" :key="idx">
                <a 
                  :href="`https://i6b201.p.ssafy.io:9999/file/${currentarticle.files[idx-1].stored_file_path}`"
                  v-if="currentarticle.files[idx-1].commentId===0"
                  target='_blank'
                >
                  첨부파일 {{idx}}
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="container" >
          <div class="row">
          <hr>
          <!-- comments 작성 -->
          <div class="comment-box pb-4">
            <form role="form">
              <div class="form-group">
                <textarea class="form-control" rows="3" v-model="newComment"></textarea>
              </div>
              <button type="button" class="btn btn-black post-btn" @click="postComment"><fa icon="reply" class="fa-icon"></fa> 작성</button>
            </form>
          </div>
          <!-- comments 조회 -->
          <div v-if="isLoadingCom">
            <p>...LOADING</p>
          </div>
          
          <div v-if="!isLoadingCom">
            <div v-if="comments.length === 0">
              <p>아직 작성된 댓글이 없습니다.</p>
            </div>
            <div v-else>
              <div   
                v-for="comment in comments.comments"
                :key="comment.id"
                  >
                  <div v-if="comment.parent_id === 0 | comment.parentId === 0">
                    <h5 class="comment-list"><fa icon="comment" class="fa-icon-b"></fa> {{ comment.user.name }} </h5>
                    <p class="comment-date"><small class="text-muted">{{ comment.createdAt }}</small></p>
                    
                    <p :class="'collapse show col'+comment.id" id="comment-cont">{{ comment.content }}</p>
                    <div v-for="file in comments.files" :key="file.id"  class="comment-list">
                      <a 
                        :href="`https://i6b201.p.ssafy.io:9999/file/${file.stored_file_path}`"
                        v-if="file.commentId === comment.id"
                        target='_blank'
                      >
                        첨부파일
                      </a>
                    </div>
                    <div v-if="comment.user.userId === userinfo.userId">
                      <!-- 댓글 삭제 -->
                      <button type="button" class="btn btn-black comment-btn" @click="deleteCom(comment.id)"><fa icon="times" class="fa-icon"></fa></button>
                      <!-- 댓글 수정 -->
                      <div v-if="currentarticle.board.category==='assignment' && userinfo.position==='S'">
                      </div>
                      <div v-else>
                        <button class="btn btn-black comment-btn" type="button" data-bs-toggle="collapse" :data-bs-target="'.col'+comment.id" aria-expanded="false" aria-controls="collapseExample" @click="requestEditCom(comment.content)">
                            <fa icon="edit" class="fa-icon"></fa>
                        </button>
                      </div>
                      <div :class="'collapse col'+comment.id">
                        <div class="mx-0">
                          <textarea class="form-control" rows="3" v-model="revisedComment"></textarea>
                          <button type="button" class="btn btn-black post-btn" @click="editCom(comment.id)"><fa icon="edit" class="fa-icon"></fa>수정</button>
                        </div>
                      </div>
                    </div>
                      <!-- 대댓글 작성 -->
                    <div v-if="currentarticle.board.category==='assignment' && userinfo.position==='S'">
                    </div>
                      <div v-else >
                         <div>
                            <button class="btn btn-black comment-btn" type="button" data-bs-toggle="collapse" :data-bs-target="'.re'+comment.id" aria-expanded="false" aria-controls="collapseExample" @click="requestEditCom(comment.content)">
                                <fa icon="reply" class="fa-icon"></fa>
                            </button>
                            <div :class="'collapse re'+comment.id">
                              <div>
                                <hr>
                                <textarea class="form-control" rows="3" v-model="reComment"></textarea>
                                <button type="button" class="btn btn-black post-btn" @click="postReCom(comment.id)"><fa icon="reply" class="fa-icon"></fa>작성</button>
                              </div>
                            </div>
                         </div>
                      </div>
                    <hr>
                  </div>
                  <div v-else class="reply">
                    <h5 class="comment-list"><fa icon="share" class="fa-icon-b"></fa> {{ comment.user.name }} </h5>
                    <p class="comment-date"><small class="text-muted">{{ comment.createdAt }}</small></p>
                    <div v-for="content in comment.content.split('\r')" :key="content" id="comment-cont">
                      <p>{{ content }}</p>
                    </div>
                    <div v-if="comment.user.userId === userinfo.userId">
                      <button type="button" class="btn btn-black comment-btn" @click="deleteCom(comment.id)"><fa icon="times" class="fa-icon"></fa></button>
                    </div>
                    <hr>
                  </div>
                </div>
              </div>
          </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router'
import axios from 'axios';
import router from '../../router';

interface BoardArticles {
  id: number,
  category: string,
  title: string,
  content: string,
  writer: string,
  date: number,
}
interface article {
    [index: string] : any
    comments: Array<any>,
    files: Array<any>,
    board: any,
}

export default {
    name: "ArticleDetail",
    setup() {
        const store = useStore();
        const route = useRoute();

        let isLoading = ref<boolean>(true);
        let isLoadingCom = ref<boolean>(true);
        let id = +route.params.article_id;
        // user 정보 가져오기
        const localStorageData = localStorage.getItem("vuex");
        let userinfoData;
        if (localStorageData !== null) {
        userinfoData = JSON.parse(localStorageData);
        }
        let userinfo = userinfoData.accountStore.userinfo;
        //console.log(userinfo)
        // article detail 요청            
        let currentarticle = ref<article>({comments:[], files:[], board:[]});
        let comments = ref({});
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
                //comments.value = currentarticle.value.comments
            })
            .catch(()=> {
                alert("해당 글을 찾지 못했습니다.")
                router.push({name: 'BoardTable'})
            })  
        } 

        // comment get 요청
        const commentList = () => {
            return axios.get(process.env.VUE_APP_API_URL+`/board/class/${id}/comment`,
            )
            .then((res) => {
                comments.value = res.data
            })
        }
        articleDetail().then(() => {
            isLoading.value = false
						commentList().then(()=> {
						isLoadingCom.value = false
						})
        })

        // 삭제 버튼 클릭
        const deleteArticle = () => {
            // delete 요청 보내기
            axios.delete(process.env.VUE_APP_API_URL+"/board/class/",{
                params:{
                school: userinfo.school,
                grade: userinfo.grade,
                classes: userinfo.class_number,
                id : route.params.article_id
                }
            })
            .then(() => {
                router.push({name: 'BoardTable'})
            })
        }
        // 댓글 작성
        let newComment = ref<string>('');
        const postComment = () => {
            // console.log(newComment.value)
            if (newComment.value.length === 0) {
                alert("댓글 내용을 작성해주세요")
            } else {
                const formData = new FormData()
                formData.append('userId', userinfo.userId)
                formData.append('content', newComment.value)
                axios.post(process.env.VUE_APP_API_URL+`/board/class/${id}/comment/`, formData, 
                { headers: {'Content-Type' : 'multipart/form-data;charset=utf-8' }})
                .then(() => {
                    newComment.value = ''
                    isLoadingCom.value = false
                    commentList()
                })
                .then(() => {
                    isLoadingCom.value = false
                })
                // .catch((e) => {
                //     console.log(e)
                // })
            }
        }
        // 댓글 삭제
        const deleteCom = (comId:number) => {
            axios.delete(process.env.VUE_APP_API_URL+`/board/class/${id}/comment/${comId}`)
            .then(() => {
                isLoadingCom.value = false
                commentList()
            })
            .then(() =>{
                isLoadingCom.value = false
            })
        }
        // 댓글 수정
        let revisedComment = ref<string>('')
        const requestEditCom = (oldCom:string) => {
            revisedComment.value = oldCom
        }
        const editCom = (comId:number) => {
            if (revisedComment.value.length === 0) {
                alert("댓글 내용을 작성해주세요")
            } else {
              const formData = new FormData()
              formData.append('content', revisedComment.value)
                axios.put(process.env.VUE_APP_API_URL+`/board/class/${id}/comment/${comId}`, formData,
                 { headers: {'Content-Type' : 'multipart/form-data;charset=utf-8' }})
                .then(() => {
                    revisedComment.value = ''
                    isLoadingCom.value = false
                    commentList()
                })
                .then(() => {
                    isLoadingCom.value = false
                })
            }
        }
        // 대댓글 작성
        let reComment = ref<string>('')
        const postReCom = (comId:any) => {
            if (reComment.value.length === 0) {
                alert("댓글 내용을 작성해주세요")
            } else {
                const formData = new FormData()
                formData.append('userId', userinfo.userId)
                formData.append('content', reComment.value)
                formData.append('parent_id', comId)
                axios.post(process.env.VUE_APP_API_URL+`/board/class/${id}/comment/`, formData, 
                { headers: {'Content-Type' : 'multipart/form-data;charset=utf-8' }})
                .then(() => {
                    reComment.value = ''
                    isLoadingCom.value = true
                    commentList()
                })
                .then(() => {
                    isLoadingCom.value = false
                })
            }
        }
        return { id, isLoading, userinfo,
        isLoadingCom, commentList, comments, deleteCom,
        currentarticle, deleteArticle, 
        revisedComment, requestEditCom, editCom,
        newComment, postComment, reComment, postReCom, 
        }
    }
}
</script>
<style scoped>
.card-content {
    width: 100%;
    padding: 0px;
    display: inline-block;
    border: none;
    box-shadow: none;
}
.card-body > p{
    text-align: right;
}
.row-padding {
  padding:15px;
}
/* .container {
  margin-left: 20%;
  margin-right: 20%;
} */
.btns {
    margin-bottom:20px;
    text-align: right;
    display: flex;
    justify-content: right;
}
.content-container {
    text-align: left;
}
.comment-box {
    padding-top: 20px;
    background-color: #f8f9fa;
}
.comment-box > h5 {
    text-align: left;
}
.comment-btn {
    display:inline-block; 
    margin-top: -42px;
    float: right;
}
.comment-date{
    margin-top: -65px;
    float: right; 
}
.comment-list {
    text-align: left;
    margin-bottom: 30px;
}
#comment-cont {
    text-align: left;
}
.reply {
    margin-left: 40px;
}
.fa-icon{
    width: 15px;
}
.fa-icon-b{
    width: 30px;
}
.post-btn {
    float: right;
}
button {
    margin:5px;
}
img {
    max-width: 600px;
    height: auto;
    
}
.buttonall {
  /* display: block; */
  /* margin-left: auto; */
  margin-right: auto;

}
hr {
  width: 100%;
}
.btn-black {
  text-decoration: none;
  border: 1px solid black;
  border-radius: 3px;
}
.font-A1{
  font-family: 'Gothic A1', sans-serif;
}
.comment-box {
  background-color: transparent;
}
.form-control {
  border-color: black;
}
.bg-gray {
  background-color: rgb(202, 202, 202);
}
</style>