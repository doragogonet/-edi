<template>
    <div :class="{'has-logo':showLogo}" :style="{ backgroundColor: settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
        <logo v-if="showLogo" :collapse="isCollapse" />
        <el-scrollbar :class="settings.sideTheme" wrap-class="scrollbar-wrapper">
            <el-menu
                :default-active="activeMenu"
                :collapse="isCollapse"
                :background-color="settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
                :text-color="settings.sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
                :unique-opened="true"
                :active-text-color="settings.theme"
                :collapse-transition="false"
                mode="vertical"
            >
                <sidebar-item
                    v-for="(route, index) in sidebarRouters"
                    :key="route.path  + index"
                    :item="route"
                    :base-path="route.path"
                />
              <!-- sxt add start -->
              <el-submenu index="1">
                <template slot="title">
                  <i class="el-icon-circle-plus" @click="handleAdd" style="margin-left: -5px;color: #3880ff"></i>
                  <span slot="title" style="padding-left: 7px">送信先一覧</span>
                </template>
                <router-link v-for="(item, index) in sendToList" :to="{name:'Report',params:{id:item.sendToListId,name:item.sendToListName}}" >
                  <el-menu-item :class="computedClass(index)" @click="clickMenu(index)">
                    <i class="el-icon-caret-right"></i>
                    <span slot="title">{{ item.sendToListName }}</span>
<!--                    <i class="el-icon-delete" style="float: right;font-size: 12px;padding-top: 12px;margin-right: -43px" @click="handleDelete(item)"></i>-->
                  </el-menu-item>
                </router-link>


              </el-submenu>
              <!-- sxt add end -->
            </el-menu>

        </el-scrollbar>

      <!-- sxt add start -->
      <!-- 送信先リストダイアログ -->
      <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="送信先名" prop="sendToListName">
            <el-input maxlength="40" style="width: 600px;" v-model="form.sendToListName" placeholder="送信先名を入力してください" />
          </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">確定</el-button>
          <el-button @click="cancel">キャンセル</el-button>
        </div>
      </el-dialog>
      <!-- sxt add end -->

    </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import variables from "@/assets/styles/variables.scss";
import { listSend_to, getSend_to, delSend_to, addSend_to, updateSend_to } from "@/api/business/send_to";    //sxt add
import emptyVue from "../../../utils/emptyVue";
export default {
    components: { SidebarItem, Logo },

    //sxt add start
    data() {
      return {
        isSelected: true,
        // 送信先リスト表格数据
        sendToList: [],
        // 是否显示弹出层
        open: false,
        // 弹出层标题
        title: "",
        // 总条数
        total: 0,
        clickIndex: null,
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          sendToListName: [
            { required: true, message: "送信先名は必須入力項目です", trigger: "blur" }
          ],
        }
      }
    },
    mounted() {
      emptyVue.$on('data', (value) => { // 监听自定义事件
        if (value == "true") {
          this.getSendToList();
        }
      });
    },
    created() {
      this.getSendToList();
    },
    methods: {
      clickMenu(index) {
        this.clickIndex = index;
        console.log(index)
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "送信先を追加";
      },
      // 表单重置
      reset() {
        this.form = {
          sendToListName: undefined,
        };
        this.resetForm("form");
      },
      /** 查询送信先リスト */
      getSendToList() {
        this.loading = true;
        listSend_to(this.queryParams).then(response => {
          this.sendToList = response.rows;
          this.total = response.total;
          console.log(response)
          this.loading = false;
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
                this.getSendToList();
              });
            } else {
              addSend_to(this.form).then(response => {
                this.$modal.msgSuccess("追加成功しました");
                this.open = false;
                this.getSendToList();
              });
            }
          }
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      /** 删除按钮操作 */
      // handleDelete(row) {
      //   console.log(row)
      //   const sendToListName = row.sendToListName;
      //   const sendToListIds = row.sendToListId || this.ids;
      //   this.$modal.confirm('送信先は' + sendToListName + '"のデータを削除しますか？').then(function() {
      //     return delSend_to(sendToListIds);
      //   }).then(() => {
      //     this.getSendToList();
      //     this.$modal.msgSuccess("削除成功しました");
      //   }).catch(() => {});
      // },
    },
    //sxt add end
    computed: {
        //sxt add start
        computedClass() {
          return function (index) {
            if (this.clickIndex != null )  {
              if (index == this.clickIndex) {
                return 'my-menu-item1';
              }
            }
            return 'my-menu-item2';
          }
        },
        //sxt add end
        ...mapState(["settings"]),
        ...mapGetters(["sidebarRouters", "sidebar"]),
        activeMenu() {
            const route = this.$route;
          console.log(route)
            const { meta, path } = route;
            // if set path, the sidebar will highlight the path you set
            if (meta.activeMenu) {
                return meta.activeMenu;
            }
            return path;
        },
        showLogo() {
            return this.$store.state.settings.sidebarLogo;
        },
        variables() {
            return variables;
        },
        isCollapse() {
            return !this.sidebar.opened;
        }
    }
};
</script>
<style>
.el-submenu .el-menu-item {
  height: 35px;
  line-height: 35px;
}
.my-menu-item1 {
  margin-left: -30px;
  border-top: 1px dotted #f5f6f9;
  background-color: #ff4949;
}
.my-menu-item2 {
  margin-left: -30px;
  /*border-top: 1px dotted #f5f6f9;*/
  color: #ffffff !important;
}
</style>




