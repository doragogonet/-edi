<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="メールアドレス" prop="sendToListAddress" label-width="110px">
        <el-input style="width: 260px"
          v-model="queryParams.sendToListAddress"
          class="el-input-width"
          placeholder="メールアドレスを入力してください"
          clearable
          autosize
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="担当者" prop="sendToListPersonInCharge">
        <el-input
          v-model="queryParams.sendToListPersonInCharge"
          placeholder="担当者を入力してください"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="社名" prop="sendToListCompany">
        <el-input
          v-model="queryParams.sendToListCompany"
          placeholder="社名を入力してください"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">検索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">リセット</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['business:send_to:add']"
        >追加</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['business:send_to:edit']"
        >修正</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['business:send_to:remove']"
        >削除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['business:send_to:export']"
        >エクスポート</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="send_toList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="sendToListId" width="60" />
      <el-table-column label="メールアドレス" prop="sendToListAddress" width="220" />
      <el-table-column label="担当者" prop="sendToListPersonInCharge" width="150" />
      <el-table-column label="社名" prop="sendToListCompany" width="250" />
      <el-table-column label="ソース元" prop="sendToListSource" width="450" />
      <el-table-column label="備考" align="center" prop="sendToListRemark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['business:send_to:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['business:send_to:remove']"
          >删除</el-button>
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

    <!-- 添加或修改配信先リスト对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="メールアドレス" prop="sendToListAddress">
          <el-input v-model="form.sendToListAddress" placeholder="メールアドレスを入力してください" />
        </el-form-item>
        <el-form-item label="担当者" prop="sendToListPersonInCharge">
          <el-input v-model="form.sendToListPersonInCharge" placeholder="担当者を入力してください" />
        </el-form-item>
        <el-form-item label="社名" prop="sendToListCompany">
          <el-input v-model="form.sendToListCompany" placeholder="社名を入力してください" />
        </el-form-item>
        <el-form-item label="ソース元" prop="sendToListSource">
          <el-input v-model="form.sendToListSource" placeholder="ソース元を入力してください" />
        </el-form-item>
        <el-form-item label="備考" prop="sendToListRemark">
          <el-input v-model="form.sendToListRemark" type="textarea" placeholder="備考を入力してください" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">確定</el-button>
        <el-button @click="cancel">キャンセル</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style>
  .el-input-width {
    width: 230px;
  }
</style>

<script>
import { listSend_to, getSend_to, delSend_to, addSend_to, updateSend_to } from "@/api/business/send_to";

export default {
  name: "Send_to",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 配信先リスト表格数据
      send_toList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sendToListAddress: null,
        sendToListPersonInCharge: null,
        sendToListCompany: null,
        sendToListSource: null,
        sendToListRemark: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        sendToListAddress: [
          { required: true, message: "メールアドレスは必須入力項目です", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询配信先リスト列表 */
    getList() {
      this.loading = true;
      listSend_to(this.queryParams).then(response => {
        this.send_toList = response.rows;
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
        sendToListId: null,
        sendToListAddress: null,
        sendToListPersonInCharge: null,
        sendToListCompany: null,
        sendToListSource: null,
        sendToListRemark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.sendToListId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "配信先リストを追加";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const sendToListId = row.sendToListId || this.ids
      getSend_to(sendToListId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "配信先リストを修正";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.sendToListId != null) {
            updateSend_to(this.form).then(response => {
              this.$modal.msgSuccess("修正成功しました");
              this.open = false;
              this.getList();
            });
          } else {
            addSend_to(this.form).then(response => {
              this.$modal.msgSuccess("追加成功しました");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      // const sendToListIds = row.sendToListId || this.ids;
      const sendToListId = row.sendToListId;
      this.$modal.confirm('配信先リストのIDは"' + sendToListId + '"のデータを削除しますか？').then(function() {
        return delSend_to(sendToListId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("削除成功しました");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('business/send_to/export', {
        ...this.queryParams
      }, `send_to_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
