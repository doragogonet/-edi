<template>
  <div class="app-container">
    <el-form :model="form" ref="form" size="small" :inline="true" :rules="rules" label-width="120px" :label-position=labelPosition>
      <el-row :gutter="1">
        <el-col :span="24">
          <el-form-item label="送信先 　　　" prop="sendToListName">
            <span style="font-weight: bold;color: #3880ff">{{form.sendToListName}}</span>
          </el-form-item>
          <el-form-item style="padding-left: 2em">
            <el-button type="primary" icon="el-icon-delete" size="mini" @click="handleDelete">削除</el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="1">
        <el-col :span="24">
          <el-form-item ref="uploadElement" label="アップロード" prop="file">
            <el-upload
              ref="upload"
              :limit="1"
              :file-list="form.fileList"
              accept=".pdf"
              :before-upload="handleBeforeUpload"
              :action="upload.url"
              :headers="upload.headers"
              :on-progress="handleFileUploadProgress"
              :on-success="handleFileSuccess"
              :on-error="handleUploadError"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              :on-preview="handlePreview"
              :auto-upload="true">
              <el-button slot="trigger" size="mini" type="primary" plain style="width: 120px;">ファイル選択</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="種別" prop="reportType">
            <el-select
              v-model="form.reportType"
              placeholder="種別"
              clearable
              style="width: 140px"
            >
              <el-option
                v-for="dict in dict.type.ete_report_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            label=""
            prop="otherTypeName"
            v-if="form.reportType == 9"
            :rules="[{ required: true, message: 'そのたの種別は必須入力項目です', trigger: 'blur' }]">
            <el-input
              v-model="form.otherTypeName"
              placeholder="その他の種別を入力してください"
              clearable
              style="width: 250px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="1">
        <el-col :span="24">
          <el-form-item label="タイトル　　　" prop="reportTitle">
<!--            <el-input maxlength="80" v-model="form.reportTitle" style="width: 1080px;" placeholder="タイトルを入力してください" />-->
            <el-input type="textarea" autosize maxlength="80" v-model="form.reportTitle" style="width: 610px;" placeholder="タイトルを入力してください" />
          </el-form-item>

          <el-form-item style="float: right">
            <el-button type="primary" icon="el-icon-document-add" size="mini" @click="submitForm">IPFS作成</el-button>
          </el-form-item>
        </el-col>
      </el-row>

    </el-form>

    <el-card class="box-card" shadow="never" style="border: 1px solid black;word-wrap:break-word">
      <div>
        <el-button icon="el-icon-document-copy" size="mini" style="float: right; padding: 3px 0" type="text" @click="copyUrl">コピー</el-button>
        <textarea id="textArea" readonly style="height:0px;margin:0; padding:0;opacity: 0;"></textarea>
      </div>
      <div id="doragogoOfHtmlUrl" v-if="doragogoOfHtmlUrl">
        {{ doragogoOfHtmlUrl }}
      </div>
<!--      <div style="padding-top: 18px" id="decryptionCode" v-if="decryptionCode">-->
<!--        復号コード：{{ decryptionCode }}-->
<!--      </div>-->
    </el-card>

    <el-table
      v-loading="loading"
      :data="reportList"
      :row-style="{height: '0'}"
      :cell-style="{padding: '3px',borderColor:'#383a3e'}"
      :header-row-style="{height: '0'}"
      :header-cell-style="{padding: '0',borderColor:'#383a3e'}"
      style="margin-top: 20px;">
      <el-table-column label="文書" align="center" width="60">
        <template slot-scope="scope">
          <el-row>
            <el-col :span="24">
              <el-link
                :href= "doragogoUrl + scope.row.cid2"
                type="primary"
                target="_blank"
                class="smail-font"
              ><i class="el-icon-document"></i></el-link>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-link
                :href= "doragogoUrl + scope.row.cid1"
                type="primary"
                target="_blank"
                class="smail-font"
              ><i class="el-icon-document-checked"></i></el-link>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
      <el-table-column label="CID" align="center">
        <el-table-column label="（復号コード）" align="center">
          <template slot-scope="scope">
            <el-row>
              <el-tooltip class="item" effect="dark" :content="scope.row.cid3" placement="top-start">
                <el-col :span="24" style="text-align: left;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                  <span>{{ scope.row.cid3 }}</span>
                </el-col>
              </el-tooltip>
            </el-row>
            <el-row>
              <el-col :span="24" style="text-align: left">
                <span>（{{ scope.row.decryptionCode }}）</span>
              </el-col>
            </el-row>
          </template>
        </el-table-column >
      </el-table-column >

      <el-table-column label="種別" align="center" prop="reportTypeName" width="100" />
      <el-table-column label="送信日付" align="center" width="140">
        <el-table-column label="返信日付" align="center" width="140">
          <template slot-scope="scope">
            <el-row>
              <el-col :span="24">
                <span>{{ scope.row.sendDate }}</span>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <span>{{ scope.row.replyDate }}</span>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column label="タイムスタンプコード" align="center" prop="stampCode">
        <el-table-column label="タイトル" align="center">
          <template slot-scope="scope">
            <el-row>
              <el-tooltip class="item" effect="dark" :content="scope.row.stampCode" placement="top-start">
                <el-col :span="24" style="text-align: left;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                  <span>{{ scope.row.stampCode }}</span>
                </el-col>
              </el-tooltip>
            </el-row>
            <el-row>
              <el-tooltip class="item" effect="dark" :content="scope.row.reportTitle" placement="top-start">
                <el-col :span="24" style="text-align: left;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                  <span>{{ scope.row.reportTitle }}</span>
                </el-col>
              </el-tooltip>
            </el-row>
          </template>
        </el-table-column >
      </el-table-column>
      <el-table-column label="送信者" align="center" width="200">
        <el-table-column label="受信者" align="center" width="200">
          <template slot-scope="scope">
            <el-row>
              <el-col :span="24" style="text-align: left;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                <span>{{ scope.row.createBy }}（送信）</span>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24" style="text-align: left;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                <span v-show="scope.row.sendToName">{{ scope.row.sendToName }}（受信）</span>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
      </el-table-column>

      <el-table-column label="状態" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <!-- sxt 20240401 del start -->
<!--          <div-->
<!--            v-for="dict in dict.type.ete_report_status"-->
<!--            :key="dict.value"-->
<!--            :label="dict.label"-->
<!--            :value="dict.value">-->
<!--            <span v-if="scope.row.status == dict.value">{{ dict.label }}</span>-->
<!--          </div>-->
          <!-- sxt 20240401 del end -->

          <!-- sxt 20240401 add start -->
          <div v-if="scope.row.status != 0"
            v-for="dict in dict.type.ete_report_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value">
            <span v-if="scope.row.status == dict.value">{{ dict.label }}</span>
          </div>
          <div v-if="scope.row.status == 0">
            <el-button
              type="primary"
              size="mini"
              icon="el-icon-delete"
              @click="handleDeleteDetail(scope.row)"
              v-if="scope.row.status == 0"
            >削除</el-button>
          </div>
          <!-- sxt 20240401 add end -->
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>
<script>
import { getToken } from "@/utils/auth";
import {  delSend_to } from "@/api/business/send_to";
import {getDoragogoUrl, listReport, addReport, delReport, updateReport} from "@/api/business/report";
import emptyVue from "../../../utils/emptyVue";
import {delUser} from "@/api/system/user";

export default {
  name: "Report",
  dicts: ['ete_report_type','ete_report_status'],
  // components: { Treeselect },
  data() {
    return {
      VUE_APP_BASE_API:"",    // 需要在这里定义一个变量 然后在html中使用这个变量
      doragogoUrl: "",        // doragogo服务器Url
      doragogoOfHtmlUrl: "",  // doragogo服务器上HTML的Url
      decryptionCode: "",     //復号コード
      labelPosition: "right",
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 電子帳簿表格数据
      reportList: [],
      tableHeight:"calc(78.5vh)",
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        reportType: [
          { required: true, message: "種別は必須入力項目です", trigger: "change" }
        ],
        file: [
          { required: true, message: "文書を選択してください", trigger: ["blur", 'change'] },
        ],
      },
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sendToListId: "",
      },
      // 上传参数
      upload: {
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/common/upload",
      }
    };
  },
  mounted(){
    this.VUE_APP_BASE_API = process.env.VUE_APP_BASE_API;
  },
  created() {
    this.reset();
    this.form.sendToListName = this.$route.params.name;
    this.form.sendToListId = this.$route.params.id;
    // sxt 20240301 add start
    getDoragogoUrl().then(response => {
      this.doragogoUrl = response.msg;
    });
    // sxt 20240301 add end

    this.getList();
    // this.getConfigKey("sys.user.initPassword").then(response => {
    //   this.initPassword = response.msg;
    // });
  },
  methods: {
    handlePreview(file) {
      console.log(file);
      if (file.status == "success") {
        // window.open("/dev-api" + file.response.fileName);
        window.open(process.env.VUE_APP_BASE_API + file.response.fileName);
      }
    },
    // 文件状态改变时
    handleFileChange(file, fileList) {
      //文件列表
      this.form.fileList = fileList;
    },
    // 文件列表移除文件时
    handleFileRemove(file, fileList) {
      //文件列表
      this.form.fileList = fileList;
      this.rules.file[0].required = true;
    },

    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.isUploading = false;
      //文件列表
      this.form.fileList = fileList;
      //文件上传成功，不做必须入力验证
      this.rules.file[0].required = false;
      // 如果上传文件，就把必填校验动态移除
      this.$refs.uploadElement.clearValidate();
    },
    // 上传失败
    handleUploadError(err) {
      this.$modal.msgError("ファイルのアップロードに失敗しました。もう一度やり直してください");
      this.$modal.closeLoading()
    },
    handleBeforeUpload(file) {
      // 校检文件类型
      if (this.fileType) {
        const fileName = file.name.split('.');
        const fileExt = fileName[fileName.length - 1];
        const isTypeOk = this.fileType.indexOf(fileExt) >= 0;
        if (!isTypeOk) {
          this.$modal.msgError(`ファイル形式が間違っている、 ${this.fileType.join("/")}形式の*ファイルをアップロードしてください`);
          return false;
        }
      }

      const isLt2M = file.size / 1024 / 1024 < 10;

      if (!isLt2M) {
        this.$message.error('アップロードファイルのサイズは10MBを超えることはできません。');
      }
      return isLt2M;
    },

    /** 删除按钮操作 */
    handleDelete() {

      var that = this;
      this.$modal.confirm('送信先は「' + this.form.sendToListName  + '」のデータを削除しますか？').then(function() {
        // alert(that.form.sendToListId)
        return delSend_to(that.form.sendToListId);
      }).then(() => {
        emptyVue.$emit('data', "true");
        this.$modal.msgSuccess("削除成功しました");

        this.$router.push({ path: '/index'});
      }).catch(() => {
        // alert(3)
      });
    },

    //sxt 20240401 add start
    /** 删除按钮操作 */
    handleDeleteDetail(row) {
      const cid3 = row.cid3
      let eleReport = {stampCode: row.stampCode, status: 9}
      this.$modal.confirm('CIDは「' + cid3  + '」のデータを削除しますか？').then(function() {
        return updateReport(eleReport);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("削除成功しました");
      }).catch(() => {
        // alert(3)
      });
    },
    //sxt 20240401 add end

    copyUrl() {
      let textArea = document.getElementById('textArea');
      // let ipfsOfHtmlUrl = document.getElementById('ipfsOfHtmlUrl');
      let doragogoOfHtmlUrl = document.getElementById('doragogoOfHtmlUrl');
      let decryptionCode = document.getElementById('decryptionCode');

      // textArea.innerHTML = doragogoOfHtmlUrl.innerHTML  + "\n" + "\n" + decryptionCode.innerHTML;
      textArea.innerHTML = doragogoOfHtmlUrl.innerHTML;
      textArea.select();

      try{
        document.execCommand('copy');
        this.$modal.msgSuccess("コピー成功しました");
      } catch(err){
        //alert("コピー失敗しました");
      }
    },

    /** 查询用户列表 */
    getList() {
      this.queryParams.sendToListId = this.$route.params.id;
      this.loading = true;
      listReport(this.queryParams).then(response => {
        console.log(response)
        this.reportList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        sendToListId: undefined,
        sendToListName: undefined,
        reportType: undefined,
        otherTypeName: undefined,
        reportTitle: undefined,
        reportRemark: undefined,
        fileName: undefined,
      };
      this.resetForm("form");
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        console.log(this.rules)
        if (valid) {
          if (this.form.sendToListId != null) {
            let fileList = this.form.fileList.filter((item)=>item.status == "success");
            for (let file of fileList) {
              //只能上传一个文件，所以只循环一次
              this.form.originalFilename = file.response.fileName;
              //fileName 跟新到DB中，存的是原始文件名
              this.form.fileName = file.response.originalFilename;
            }

            //タイトルを設定

            //内容を設定

            //受信者を設定
            this.form.sendToName = this.form.sendToListName;
            //種別名を設定
            //種別はその他の場合
            if (this.form.reportType == 9) {
              this.form.reportTypeName = this.form.otherTypeName;
            } else {
              //種別字典数据
              for (let dictData of this.dict.type.ete_report_type) {
                if (dictData.value == this.form.reportType) {
                  this.form.reportTypeName = dictData.label;
                }
              }
            }

            this.loading = true;
            addReport(this.form).then(response =>{
              console.log(response)
              this.doragogoOfHtmlUrl = this.doragogoUrl + response.data.cid3;         //htmlのurl
              this.decryptionCode = response.data.decryptionCode;                               //復号コード
              this.open = false;
              this.getList();
              this.$modal.msgSuccess("IPFS作成しました");
              //清空文件列表
              this.$refs.upload.clearFiles();
              this.form.fileList = [];
              this.rules.file[0].required = true;
              //sxt 20240403 add start
              this.$refs.form.resetFields();
              //sxt 20240403 add end
              this.loading = false;
            }).catch(()=>{
              this.open = false;
              this.loading = false;
            })

          }
        }
      });
    },

  }
};
</script>
//sxt 20240401 add start
<style>
.el-message-box {
  width: 500px;
}
</style>
//sxt 20240401 add end
