<template>
  <div class="submit-form">
    <form>
      <div class="row mb-3">
        <label for="title" class="col-sm-2 col-form-label">제목</label>
        <div class="col-sm-10">
          <input class="form-control" id="title" required v-model="article.title" type="text">
        </div>
      </div>
      <fieldset class="row mb-3">
        <legend class="col-form-label col-sm-2 pt-0">분류</legend>
        <div class="col-sm-10">
          <div class="form-check form-check-inline me-5">
            <input class="form-check-input" type="radio" name="timetable" id="inlineRadio1" value="timetable" v-model="article.category">
            <label class="form-check-label" for="inlineRadio1">시간표</label>
          </div>
          <div class="form-check form-check-inline me-5">
            <input class="form-check-input" type="radio" name="notice" id="inlineRadio2" value="notice" v-model="article.category">
            <label class="form-check-label" for="inlineRadio2">알림장</label>
          </div>
          <div class="form-check form-check-inline me-5">
            <input class="form-check-input" type="radio" name="handouts" id="inlineRadio3" value="handouts" v-model="article.category">
            <label class="form-check-label" for="inlineRadio3">유인물</label>
          </div>
          <div class="form-check form-check-inline me-5">
            <input class="form-check-input" type="radio" name="assignment" id="inlineRadio4" value="assignment" v-model="article.category">
            <label class="form-check-label" for="inlineRadio4">숙제</label>
          </div>
          <div class="form-check form-check-inline me-5">
            <input class="form-check-input" type="radio" name="photo" id="inlineRadio5" value="photo" v-model="article.category">
            <label class="form-check-label" for="inlineRadio5">사진첩</label>
          </div>
        </div>
      </fieldset>
        <div v-if="article.category === 'timetable'">
          <div class="row mb-3">
            <label for="week" class="col-sm-2 col-form-label">시간표 주차</label>
            <div class="col-sm-10">
              <input @change="getDate"  type="week" class="form-control" id="week" name="week" min="2022-W1" style="display:inline" v-model="timeweek" required>
            </div>
          </div>
          <div class="row mb-3">
            <table class="table">
              <thead>
                <tr>
                  <th scope="col"> 교시 </th>
                  <th scope="col">시작 시간</th>
                  <th scope="col">종료 시간</th>
                  <th scope="col">월</th>
                  <th scope="col">화</th>
                  <th scope="col">수</th>
                  <th scope="col">목</th>
                  <th scope="col">금</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <th>1</th>
                  <td id="start1" value="9:00">09:00</td>
                  <td id="end1">09:40</td>
                  <td><input type="text" class="form-control" id="mon1" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="tue1" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="wed1" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="thu1" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="fri1" placeholder="과목명"></td>
                </tr>
                <tr>
                  <th>2</th>
                  <td id="start2">09:50</td>
                  <td id="end2">10:30</td>
                  <td><input type="text" class="form-control" id="mon2" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="tue2" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="wed2" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="thu2" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="fri2" placeholder="과목명"></td>
                </tr>
                <tr>
                  <th>3</th>
                  <td id="start3">10:40</td>
                  <td id="end3">11:20</td>
                  <td><input type="text" class="form-control" id="mon3" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="tue3" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="wed3" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="thu3" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="fri3" placeholder="과목명"></td>
                </tr>
                <tr>
                  <th>4</th>
                  <td id="start4">11:30</td>
                  <td id="end4">12:10</td>
                  <td><input type="text" class="form-control" id="mon4" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="tue4" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="wed4" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="thu4" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="fri4" placeholder="과목명"></td>
                </tr>
                <tr>
                  <th class="table-warning"></th>
                  <td class="table-warning">12:10</td>
                  <td class="table-warning">13:00</td>
                  <td class="table-warning">점</td>
                  <td class="table-warning">심</td>
                  <td class="table-warning">시</td>
                  <td class="table-warning">간</td>
                  <td class="table-warning"></td>
                </tr>
                <tr>
                  <th>5</th>
                  <td id="start5">13:00</td>
                  <td id="end5">13:40</td>
                  <td><input type="text" class="form-control" id="mon5" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="tue5" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="wed5" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="thu5" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="fri5" placeholder="과목명"></td>
                </tr>
                <tr>
                  <th>6</th>
                  <td id="start6">13:50</td>
                  <td id="end6">14:30</td>
                  <td><input type="text" class="form-control" id="mon6" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="tue6" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="wed6" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="thu6" placeholder="과목명"></td>
                  <td><input type="text" class="form-control" id="fri6" placeholder="과목명"></td>
                </tr>
              </tbody>
            </table>
          </div>
          <button @click="saveTimetable" class="btn btn-success" type="button">작성하기</button>
        </div>
        <div v-else>
          <div class="row mb-3">
            <label for="content" class="col-sm-2 col-form-label">내용</label>
            <div class="col-sm-10">
              <textarea class="form-control" id="content" rows="10" required v-model="article.content" type="text"></textarea>
            </div>
          </div>
          <div class="row mb-3">
            <label for="title" class="col-sm-2 col-form-label">첨부</label>
            <div class="col-sm-10">
              <input @change="fileSelect()" type="file" multiple ref="file" class="form-control" id="inputGroupFile04" aria-describedby="inputGroupFileAddon04" aria-label="Upload">
              <!-- <button @click="clearFiles" class="btn btn-outline-danger">파일 전체 삭제</button> -->
            </div>
          </div>
          <button @click="saveArticle" class="btn btn-success" type="button">작성하기</button>
        </div>

    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";
import { mapState } from "vuex";
import Article from "../../types/board/Article";
import axios from "axios";
import router from '../../router';
interface timeTable {
  [index: number] : Array<any>;
};
export default defineComponent({
  name: "CreateArticle",
  data() {
    return {
      article: {
        id: null,
        category: "",
        title: "",
        content: "",
        file_link: "",
      } as Article,
      submitted: false,
      timeweek: "",
      monDate: "" as any,
    };
  },
  computed: {
    ...mapState('accountStore', ['userinfo'])
  },
  methods: {
    fileSelect() {
      const uploadedfile = this.$refs.file as any
      this.article.file_link= uploadedfile.files;
    },
    clearFiles(event:any) {
      event.preventDefault()
      this.article.file_link=''
    },
    saveArticle() {
      if (this.article.category.length <=0 || this.article.title.length <=0 || this. article.content.length <=0) {
        window.alert('제목, 분류 및 내용을 모두 작성해주세요')
        return false;
      }

      const formData = new FormData()
      formData.append('category', this.article.category);
      formData.append('title', this.article.title);
      formData.append('content', this.article.content);
      if (this.article.file_link.length > 0) {
        for (var index=0; index < this.article.file_link.length; index++) {
            formData.append('files', this.article.file_link[index]);
        }
      }
      formData.append('school', this.userinfo.school);
      formData.append('grade', this.userinfo.grade);
      formData.append('classes', this.userinfo.class_number);
      formData.append('userId', this.userinfo.userId);

      this.submitted = true
      //console.log(...formData.entries())
      // POST 요청
      axios.post(process.env.VUE_APP_API_URL+"/board/class",formData, 
      { headers: {'Content-Type' : 'multipart/form-data;charset=utf-8' } }
      )
      .then((res)=>{
        router.push({name: 'BoardTable'})
      })
      .catch(()=>{
        alert("글 작성 실패")
      })
    },
    // 시간표 작성
    saveTimetable() {
        if (this.article.category.length <=0 || this.timeweek.length <=0) {
          window.alert('분류 및 내용을 모두 작성해주세요')
        return false;
        }
        const day=['mon','tue','wed','thu','fri'];
        let timetable = [[], [], [], [], []] as timeTable;
        for (var i=1; i<7; i++) {
          day.forEach((d) => {
            const subject = {
              'start_time': (document.getElementById(`start${i}`) as HTMLInputElement).innerHTML,
              'end_time': (document.getElementById(`end${i}`) as HTMLInputElement).innerHTML,
              'subject': (document.getElementById(`${d}${i}`) as HTMLInputElement).value,
            }
            timetable[day.indexOf(d)].push(subject)
          })
        }
        let data:any = {
          'year': +this.timeweek.split('-')[0],
          'week': +this.timeweek.split('W')[1],
          'school': this.userinfo.school,
          'grade': this.userinfo.grade,
          'classes': this.userinfo.class_number,
          '1': timetable[0],
          '2': timetable[1],
          '3': timetable[2],
          '4': timetable[3],
          '5': timetable[4]
        }

        axios.post(process.env.VUE_APP_API_URL+"/timetable/week", data)
        .then(() => {
          router.push({name: 'BoardTable'})
        })
     },
  },
});
</script>

<style>
.submit-form {
  margin: 50px auto;
  width: 100vh;
}
</style>