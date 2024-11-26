<template>
  <div style="margin: 0 auto;max-width: 1400px;font-size: 60px;">
    <el-row style="padding: 30px;margin-top: 50px">
      <el-col :span="8" style="width: 360px"><div>Repo Size:</div></el-col>
      <el-col :span="16"><div v-if="repoSize">{{ repoSize }} MB</div></el-col>
    </el-row>
    <el-row style="padding: 30px">
      <el-col :span="8" style="width: 360px"><div>Storage Max:</div></el-col>
      <el-col :span="16"><div v-if="storageMax">{{ storageMax }} MB</div></el-col>
    </el-row>
    <el-row style="padding: 30px">
      <el-col :span="8" style="width: 360px"><div>Num Objects:</div></el-col>
      <el-col :span="16"><div v-if="numObjects">{{ numObjects }}</div></el-col>
    </el-row>

  </div>
</template>
<script>
import { listIndex } from "@/api/index";
export default {
  name: "Index",
  data() {
    return {
      repoSize: null,
      storageMax: null,
      numObjects: null
    }
  },
  created() {
    this.loading = true;

    listIndex(this.queryParams).then(response => {
      console.log(response);
       if (response.data.RepoSize != 0) {
         this.repoSize = (response.data.RepoSize  / (1024 * 1024)).toFixed(2).trimEnd("0");
         if (this.repoSize.indexOf(".") > -1) {
           while (this.repoSize[this.repoSize.length - 1] === '0') { // 判断最后一位是否为'0'
             this.repoSize = this.repoSize.slice(0, -1); // 若是，则删除最后一位
           }
         }
       }
      if (response.data.StorageMax != 0) {
        this.storageMax = (response.data.StorageMax  / (1024 * 1024)).toFixed(2);
        if (this.storageMax.indexOf(".") > -1) {
          while (this.storageMax[this.storageMax.length - 1] === '0') { // 判断最后一位是否为'0'
            this.storageMax = this.storageMax.slice(0, -1); // 若是，则删除最后一位
          }
        }
      }

      if (response.data.NumObjects != 0) {
        this.numObjects = response.data.NumObjects;
      }
    }).finally(()=>{
      this.loading = false;
    })
  },
}

</script>



