<!--
* @description 新增自提点
* @fileName PickupLocations.vue
* @author zs
* @date 2024/12/05
!-->
<template>
  <div>
    <el-dialog
      title="添加自提地点"
      :visible.sync="visible"
      width="50%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-input
        v-model="address"
        placeholder="请输入具体地址"
        @keyup.native="getInputVal"
      ></el-input>
      <ul id="suggestionList">
        <li v-for="(item, index) in suggestionList" :key="index">
          <a href="#" @click="setSuggestion(index)"
            >{{ item.title }}
            <span class="item_info">{{ item.address }}</span></a
          >
        </li>
      </ul>
      <div id="pickupMap"></div>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="hide"> 取 消 </el-button>
        <el-button size="medium" type="primary" @click="handleSubmit">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script
  charset="utf-8"
  src="https://map.qq.com/api/gljs?v=1.exp&libraries=service&key=73UBZ-EQ5LO-CYNWJ-SRHOV-627LS-EZF67"
></script>
<script>
export default {
  data() {
    return {
      visible: false,
      address: "",
      latitude: "",
      longitude: "",
      suggestionList: [],
      suggest: "",
      searchs: "",
      markers: "",
      map: "",
      center: {
        lng: 109.45744048529967,
        lat: 36.49771311230842,
      },
    };
  },
  methods: {
    show: function () {
      this.visible = true;
      this.address = "";
      this.$nextTick(() => {
        setTimeout(() => {
          this.initTxMap();
        }, 1000);
      });
    },
    hide: function () {
      this.visible = false;
      this.$emit("refresh");
    },
    handleSubmit: function () {
      if (!this.latitude || !this.longitude || !this.address) {
        this.$message.error("请选择自提地点");
        return;
      }

      this.$emit("confirm", {
        pickupLocation: {
          address:this.address,
          latitude:this.latitude,
          longitude:this.longitude,
        }
      });
      this.hide();
    },
    // 输入关键字搜索
    getInputVal() {
      if (this.address) {
        this.suggest
          .getSuggestions({
            keyword: this.address,
            location: this.map.getCenter(),
          })
          .then((result) => {
            // 以当前所输入关键字获取输入提示
            this.suggestionList = result.data;
          })
          .catch((error) => {
            console.log(error);
          });
      }
    },
    /**
     * @desc 初始化腾讯地图
     */
    initTxMap() {
      if (!this.map) {
        this.map = null;
        var myLatlng = new TMap.LatLng(
            this.latitude || this.center.lat,
            this.longitude || this.center.lng
          ),
          myOptions = {
            zoom: 16,
            center: myLatlng,
          };

        this.map = new TMap.Map("pickupMap", myOptions);
        this.suggestionList = [];
        this.searchs = new TMap.service.Search({ pageSize: 10 }); // 新建一个地点搜索类
        this.suggest = new TMap.service.Suggestion({
          // 新建一个关键字输入提示类
          pageSize: 10, // 返回结果每页条目数
        });
        this.markers = new TMap.MultiMarker({
          map: this.map,
          geometries: [],
        });
        console.log("this.latitude", this.latitude);
        if (this.latitude) {
          var myLatlng = new TMap.LatLng(this.latitude, this.longitude);
          this.markers.setGeometries([]);
          this.markers.updateGeometries([
            {
              id: "0", // 点标注数据数组
              position: myLatlng,
            },
          ]);
        }
      }
    },
    setSuggestion(index) {
      // 点击输入提示后，于地图中用点标记绘制该地点，并显示信息窗体，包含其名称、地址等信息
      this.address = this.suggestionList[index].title;
      this.markers.setGeometries([]);
      this.markers.updateGeometries([
        {
          id: "0", // 点标注数据数组
          position: this.suggestionList[index].location,
        },
      ]);
      this.map.setCenter(this.suggestionList[index].location);
      this.longitude = this.suggestionList[index].location.lng;
      this.latitude = this.suggestionList[index].location.lat;
      this.suggestionList = [];
    },
  },
};
</script>
<style lang="less" scoped>
#pickupMap {
  width: 450px;
  height: 350px;
  border: 1px solid #ccc;
  margin: 20px;
}
.tangram-suggestion-main {
  z-index: 2;
}
#suggestionList {
  z-index: 1001;
  position: absolute;
  list-style-type: none;
  padding: 0;
  margin: 0;
  width: 80%;
}
#suggestionList li a {
  padding: 5px 10px;
  margin-top: -1px;
  background-color: #f6f6f6;
  text-decoration: none;
  font-size: 18px;
  color: black;
  display: block;
  line-height: 20px;
}

#suggestionList li .item_info {
  font-size: 12px;
  color: grey;
}

#suggestionList li a:hover:not(.header) {
  background-color: #eee;
}
</style>