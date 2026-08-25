<!-- 
@name: Market.vue 
@description: 市场信息
@author: sx
@url: /base/market
@date: 2020/06/24
-->
<template lang="html">
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- form表单 -->
    <div class="form-box">
      <el-form>
        <el-form-item
          label="市场名称"
          :label-width="labelWidth"
          :required="true"
        >
          <el-input
            v-model="inputModel.name"
            ref="nameInput"
            placeholder="请输入市场名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="菜场编码" :label-width="labelWidth">
          <el-input
            v-model="inputModel.code"
            ref="codeInput"
            placeholder="请输入菜场编码"
          ></el-input>
        </el-form-item>
        <el-form-item label="管理员" :label-width="labelWidth" :required="true">
          <el-input
            v-model="inputModel.manager"
            ref="managerInput"
            :disabled="true"
            placeholder="请输入管理员"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="手机号码"
          :label-width="labelWidth"
          :required="true"
        >
          <el-input
            v-model="inputModel.mobile"
            ref="mobileInput"
            :disabled="true"
            placeholder="请输入手机号码"
          ></el-input>
        </el-form-item>
        <el-form-item label="售后电话" :label-width="labelWidth">
          <el-input
            v-model="inputModel.tel"
            ref="telInput"
            placeholder="请输入售后电话"
          ></el-input>
        </el-form-item>
        <el-form-item label="企业ID" :label-width="labelWidth">
          <el-input
            v-model="inputModel.config.customerServiceId"
            ref="telInput"
            placeholder="请输入企业ID"
          ></el-input>
        </el-form-item>
        <el-form-item label="企业客服链接" :label-width="labelWidth">
          <el-input
            v-model="inputModel.config.customerServiceLink"
            ref="telInput"
            placeholder="请输入企业客服链接"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="地址"
          :label-width="labelWidth"
          :required="true"
          style="height: 38px;"
        >
          <!-- <el-input v-model="inputModel.config.addr" ref="addrInput" placeholder="请输入地址" @keyup.native="getInputVal">
          </el-input> -->
          <el-input
            v-model="inputModel.config.addr"
            id="suggestId"
            class="mapText"
            placeholder="请输入具体地址"
            @keyup.native="getInputVal"
          ></el-input>
          <ul id="suggestionList">
            <li v-for="(item,index) in suggestionList" :key="index"><a href="#" @click="setSuggestion(index)" >{{item.title}} <span class="item_info">{{item.address}}</span></a></li>
          </ul>
        </el-form-item>
        <el-form-item
          label="市场自提点"
          :label-width="labelWidth"
        >
          <div class="pickup-locations">
            <div class="location" v-for="(item,index) in inputModel.pickupLocations" :key="index">
              <span>{{item.address}}</span>
              <el-button type="text" style="color:red;margin-left: 24px;" @click="locationDel(item,index)">删除</el-button>
            </div>
            <el-button type="text" @click="locationAdd()">新增</el-button>
          </div>
        </el-form-item>
        <el-form-item
          label="营业时间"
          :label-width="labelWidth"
          :required="true"
        >
          <el-checkbox-group v-model="weeks">
            <el-checkbox-button
              v-for="week in weeksList"
              :label="week"
              :key="week"
              @change="handleWeeksChange(week)"
              >{{ week }}</el-checkbox-button
            >
          </el-checkbox-group>
        </el-form-item>
        <el-form-item
          label="营业时段"
          :label-width="labelWidth"
          :required="true"
        >
          <div class="times-line" v-for="(item, index) in timesList" :key="index">
            <el-time-picker
              is-range
              refs="timeInput"
              v-model="timesList[index]"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围"
              format="HH:mm"
              value-format="HH:mm"
              :picker-options="{ selectableRange: '18:30:00 - 20:30:00' }"
            >
            </el-time-picker>
            <el-button
              icon="el-icon-plus"
              circle
              @click="handleTimesAdd(index)"
            ></el-button>
            <el-button
              icon="el-icon-delete"
              circle
              @click="handleTimesDelete(index)"
              v-show="timesList.length > 1"
            ></el-button>
          </div>
        </el-form-item>
        <el-form-item label="介绍" :label-width="labelWidth">
          <el-input
            type="textarea"
            :rows="5"
            v-model="inputModel.content"
            ref="contentInput"
            placeholder="请输入介绍"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="市场Logo" :label-width="labelWidth">
          <img-upload
            ref="ImgUpload2"
            :limit="1"
            @change="changeImg2"
          ></img-upload>
        </el-form-item>
        <el-form-item label="市场图片" :label-width="labelWidth">
          <img-upload
            ref="ImgUpload"
            :limit="3"
            @change="changeImg"
          ></img-upload>
        </el-form-item>
        <!-- <el-form-item label="会员介绍" :label-width="labelWidth" >
          <img-upload ref="ImgUpload3" :limit="1" @change="changeImg3"></img-upload>
        </el-form-item> -->
        <el-form-item
          label="营业状态"
          :label-width="labelWidth"
          :required="true"
        >
          <el-switch v-model="inputModel.config.ystatus" active-color="#13ce66">
          </el-switch>
        </el-form-item>
        <el-form-item
          v-if="marketType=='VENDOR_SHOPPING_MALL'"
          label="首页推荐"
          :label-width="labelWidth"
        >
          <el-radio v-model="inputModel.config.goodsType" label="VENDOR_RECOMMEND">精选商户</el-radio>
          <el-radio v-model="inputModel.config.goodsType" label="SINGLE_GOODS_RECOMMEND">今日推荐</el-radio>
        </el-form-item>
        <el-form-item class="el-form-item--submit">
          <el-button type="primary" @click="handleEdit" :loading="loading">
            修改市场信息
          </el-button>
        </el-form-item>
      </el-form>
      <!-- <baidu-map :center="center" :zoom="zoom" @ready="handler" @click="getClickInfo" :scroll-wheel-zoom='true' ak="xaBG3hmMTbWGGGLIqlAmzjnkQMNIiVN0">
        </baidu-map> -->
      <div id="allmap"></div>
    </div>
    <pickup-locations ref="PickupLocations" @confirm="locationAddItem"></pickup-locations>
  </div>
</template>

<script
  charset="utf-8"
  src="https://map.qq.com/api/gljs?v=1.exp&libraries=service&key=73UBZ-EQ5LO-CYNWJ-SRHOV-627LS-EZF67"
></script>
<script>
import qs from "qs";
import ImgUpload from "@/components/global/ImgUpload";
import PickupLocations from "@/components/global/PickupLocations";

export default {
  data() {
    return {
      labelWidth: "140px",
      loading: false,
      inputModel: {
        pkey: "",
        name: "",
        code: "",
        manager: "",
        mobile: "",
        content: "",
        tel: "",
        logo: "",
        photo1: "",
        photo2: "",
        photo3: "",
        config: {
          addr: "",
          yytb: "",
          yyte: "",
          latitude: "",
          longitude: "",
          memberPhoto: "",
          ystatus: true,
          phour: "",
          pminute: "",
          monday: false,
          tuesday: false,
          wednesday: false,
          thursday: false,
          friday: false,
          saturday: false,
          sunday: false,
          wanliSecret: "",
          wanliAppId: "",
          customerServiceId: "",
          customerServiceLink: "",
          goodsType: "",
        },
        pickupHour: "",
        pickupMinute: "",
        enabled: true,
        times: [],
        pickupLocations:[]
      },
      address_detail: "",
      photos: [],
      times: ["", ""],
      center: {
        lng: 109.45744048529967,
        lat: 36.49771311230842
      },
      zoom: 13,
      map: "",
      markers: "",
      searchs: "",
      markersArray: [],
      searchService: "",
      suggestionList: [],
      suggest: "",
      weeks: [],
      weeksList: [
        "全部",
        "周一",
        "周二",
        "周三",
        "周四",
        "周五",
        "周六",
        "周日"
      ],
      weeksEnum: [
        {
          pkey: "monday",
          name: "周一"
        },
        {
          pkey: "tuesday",
          name: "周二"
        },
        {
          pkey: "wednesday",
          name: "周三"
        },
        {
          pkey: "thursday",
          name: "周四"
        },
        {
          pkey: "friday",
          name: "周五"
        },
        {
          pkey: "saturday",
          name: "周六"
        },
        {
          pkey: "sunday",
          name: "周日"
        }
      ],
      weeksRes: {
        monday: false,
        tuesday: false,
        wednesday: false,
        thursday: false,
        friday: false,
        saturday: false,
        sunday: false
      },
      timesList: [['08"00', "22:00"]],
      marketType: this.$store.state.marketType,
    };
  },
  components: {
    ImgUpload,
    PickupLocations
    // BaiduMap
  },

  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    }
  },
  mounted() {
    this.getData();
    const _this =this;
   this.$nextTick(()=> {
    setTimeout(()=>{
      this.initTxMap();
    },1000)
   
   }) 
  },
  methods: {
    /**
     * 新增自提地址
     */
    locationAdd(){
      if(this.inputModel.pickupLocations.length >= 10){
        this.$message.error("最多10个自提点");
        return;
      }
      this.$refs.PickupLocations.show();
    },
    locationAddItem({pickupLocation}){
      console.log(pickupLocation,'pickupLocation');
      this.inputModel.pickupLocations.push(pickupLocation);
    },
    /**
     * 删除自提地址
     */
    locationDel(item,index){
      this.inputModel.pickupLocations.splice(index,1);
    },
    /**
     * 获取列表
     */
    getData:async function() {
      this.loading = true;
      const params = {
        pkey: this.$store.state.marketPkey
      };

    await axios
        .post(api.market.getMarketInfo, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token
          }
        })
        .then(response => {
          // console.log(response)
          this.inputModel = {
            pkey: response.pkey,
            name: response.name,
            code: response.code,
            manager: response.manager,
            mobile: response.mobile,
            content: response.content,
            tel: response.tel,
            logo: response.logo,
            photo1: response.photo1,
            photo2: response.photo2,
            photo3: response.photo3,
            config: {
              addr: response.config.addr,
              yytb: response.config.yytb,
              yyte: response.config.yyte,
              latitude: response.config.latitude,
              longitude: response.config.longitude,
              memberPhoto: response.config.memberPhoto,
              ystatus: response.config.ystatus,
              phour: response.config.phour,
              pminute: response.config.pminute,
              monday: response.config.monday,
              tuesday: response.config.tuesday,
              wednesday: response.config.wednesday,
              thursday: response.config.thursday,
              friday: response.config.friday,
              saturday: response.config.saturday,
              sunday: response.config.sunday,
              isReductionOne: response.config.isReductionOne,
              isReductionTwo: response.config.isReductionTwo,
              reachOne: response.config.reachOne,
              reachTwo: response.config.reachTwo,
              reductionDeliveryOne: response.config.reductionDeliveryOne,
              reductionDeliveryTwo: response.config.reductionDeliveryTwo,
              wanliAppId:response.config.wanliAppId,
              wanliSecret:response.config.wanliSecret,
              customerServiceId: response.config.customerServiceId,
              customerServiceLink: response.config.customerServiceLink,
              goodsType: response.config.goodsType
            },
            pickupHour: response.pickupHour,
            pickupMinute: response.pickupMinute,
            enabled: response.enabled,
            times: response.times,
            pickupLocations: response.pickupLocations,
          };
      
          this.getOpenTime();

          // console.log("inputModel", this.inputModel)
          if (this.inputModel.photo1) {
            this.photos[0] = this.inputModel.photo1;
          }

          if (this.inputModel.photo2) {
            this.photos[1] = this.inputModel.photo2;
          }

          if (this.inputModel.photo3) {
            this.photos[2] = this.inputModel.photo3;
          }

          this.times = [
            this.inputModel.config.yytb,
            this.inputModel.config.yyte
          ];

          this.$refs.ImgUpload.updateImg(this.photos);
          this.$refs.ImgUpload2.updateImg(this.inputModel.logo);
          // this.$refs.ImgUpload3.updateImg(this.inputModel.config.memberPhoto);

          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    },
    getInputVal(e) {
      // this.inputModel.config.addr;
      if(this.inputModel.config.addr) {
        this.search();
      }
    },
    /**
     * @desc 关键字搜索提示配置
     */
    search() {
  var keyword = this.inputModel.config.addr;
  if (keyword) {
    this.suggest
      .getSuggestions({ keyword: keyword, location: this.map.getCenter() })
      .then((result) => {
        // 以当前所输入关键字获取输入提示

        this.suggestionList = result.data;
      })
      .catch((error) => {
        console.log(error);
      });
  }
    },
    setSuggestion(index) {
  // 点击输入提示后，于地图中用点标记绘制该地点，并显示信息窗体，包含其名称、地址等信息
  this.inputModel.config.addr = this.suggestionList[index].title;
  this.markers.setGeometries([]);
  this.markers.updateGeometries([
    {
      id: '0', // 点标注数据数组
      position: this.suggestionList[index].location,
    },
  ]);
  this.map.setCenter(this.suggestionList[index].location);
  this.inputModel.config.longitude = this.suggestionList[index].location.lng;
  this.inputModel.config.latitude = this.suggestionList[index].location.lat;
  this.suggestionList=[];
},
    /**
     * @desc 初始化腾讯地图
     */
    initTxMap() {
      this.map = null;
      var myLatlng = new TMap.LatLng(
          this.inputModel.config.latitude || this.center.lat,
          this.inputModel.config.longitude || this.center.lng
        ),
        myOptions = {
          zoom: 16,
          center: myLatlng,
        };

      this.map = new TMap.Map('allmap', myOptions);
      this.suggestionList = [];
      this.searchs = new TMap.service.Search({ pageSize: 10 }); // 新建一个地点搜索类
      this.suggest = new TMap.service.Suggestion({
        // 新建一个关键字输入提示类
        pageSize: 10, // 返回结果每页条目数
      });
      this.markers = new TMap.MultiMarker({
        map: this.map ,
        geometries: [],
      });
      console.log("this.inputModel.config.latitude",this.inputModel.config.latitude)
      if(this.inputModel.config.latitude ) {
          var myLatlng = new TMap.LatLng(
          this.inputModel.config.latitude,
          this.inputModel.config.longitude
        )
        this.markers.setGeometries([]);
          this.markers.updateGeometries([
            {
              id: '0', // 点标注数据数组
              position: myLatlng,
            },
          ]);
      }
    },
    /**
     * 图片修改事件
     */
    changeImg: function(imgUrl) {
      this.photos = imgUrl;
    },
    /**
     * 图片修改事件
     */
    changeImg2: function(imgUrl) {
      this.inputModel.logo = imgUrl.join();
    },
    /**
     * 会员图片修改事件
     */
    changeImg3(imgUrl) {
      this.inputModel.config.memberPhoto = imgUrl[0];
    },
    /**
     * 点击新增公司
     */
    handleEdit: function() {
      let inputModel = this.inputModel;

      if (!inputModel.name) {
        this.$message.error("请填写市场名称");
        this.$refs.nameInput.focus();
        return;
      }
      if (!inputModel.config.addr) {
        this.$message.error("请填写地址");
        this.$refs.addrInput.focus();
        return;
      }

      if (!this.weeks.length) {
        this.$message.error("请选择营业时间");
        this.$refs.timeInput.focus();
        return;
      }

      if (!this.checkTimeRange()) {
        this.$message.error("营业时段未选择或营业时段重叠，请修改后重试");
        return;
      }

      // 获取每天的营业状态，默认false, 选择改为true
      // this.weeks.forEach(item => {
      this.weeksEnum.forEach(item => {
        const found = this.weeks.find(week => {
          return item.name === week;
        });
        console.log({ found, item });
        if (found) {
          this.inputModel.config[item.pkey] = true;
        } else {
          this.inputModel.config[item.pkey] = false;
        }
      });

      // 获取营业时间段
      this.inputModel.times = this.timesList.map(item => {
        console.log(item);
        const startTime = item[0];
        const endTime = item[1];
        const [startHour, startMinute] = startTime.split(":");
        const [endHour, endMinute] = endTime.split(":");
        return { startHour, startMinute, endHour, endMinute };
      });

      console.log(this.inputModel.times);

      // return

      this.inputModel.photo1 = "";
      this.inputModel.photo2 = "";
      this.inputModel.photo3 = "";
      if (this.photos.length > 0) {
        this.photos.forEach((item, index) => {
          this.inputModel["photo" + (index + 1)] = item;
        });
      }
      // this.inputModel.config.yytb = this.times[0];
      // this.inputModel.config.yyte = this.times[1];
      let params = this.inputModel;
      axios
        .post(api.market.updMarket, params, {
          headers: {
            Authorization: this.$store.state.token,
            "Content-Type": "application/json"
          }
        })
        .then(response => {
          this.$message.success("修改成功");
          this.getData();
        });
    },
   

    /**
     * 获取营业时间和营业时段
     */
    getOpenTime() {
      const config = this.inputModel.config;
      this.weeksEnum.forEach(week => {
        if (config[week.pkey]) {
          this.weeks.push(week.name);
          this.handleWeeksChange(week.name);
        }
      });

      const times = this.inputModel.times;
      this.timesList = times.map(time => {
        const startTime = `${time.startHour}:${time.startMinute}`;
        const EndTime = `${time.endHour}:${time.endMinute}`;
        return [startTime, EndTime];
      });
      if (!this.timesList.length) this.timesList = [["08:00", "22:00"]];
    },
    handleWeeksChange(week) {
      if (week === "全部") {
        this.weeks = JSON.parse(JSON.stringify(this.weeksList));
      } else if (this.weeks.findIndex(item => item === "全部") > -1) {
        this.weeks = [week];
      } else if (this.checkAllWeeks()) {
        this.weeks = JSON.parse(JSON.stringify(this.weeksList));
      }
      console.log("weeksList", this.weeksList);
    },
    checkAllWeeks() {
      const weeks = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"];
      for (let i = 0; i < weeks.length; i++) {
        const week = weeks[i];
        if (!this.weeks.includes(week)) {
          return false;
        }
      }
      return true;
    },
    handleTimesAdd(index) {
      this.timesList.splice(index + 1, 0, ["08:00", "22:00"]);
    },
    handleTimesDelete(index) {
      this.timesList.splice(index, 1);
    },
    handleTimeChange() {
      console.log(this.timesList);
      // 已选择的时间范围
      const timeRanges = this.timesList;
      // 限制最初的时间范围
      let excludedDates = [["00:00:00", "23:59:59"]];

      // 遍历已选范围
      timeRanges.forEach(time => {
        let start = time[0]; // 开始时间
        let end = time[1]; // 结束时间
        // 遍历限制范围
        for (let i = 0; i < excludedDates.length; i++) {
          let range = excludedDates[i]; // 限制范围
          let range_start = range[0]; // 限制范围的开始时间
          let range_end = range[1]; // 限制范围的结束时间
          /**
           * 如果开始时间大于限制范围的开始时间，并且开始时间小于限制范围的结束时间，那么说明该限制范围将被拆分
           * 将限制范围的结束时间改为开始时间，然后新增一个新的限制范围
           * if(start > range_start && start < range_end)
           * [...[range_start, range_end]...] => [...[range_start, start], [start, range_end]...]
           */
          if (
            timeCompare(start, range_start) > 0 &&
            timeCompare(start, range_end) < 0
          ) {
            range[1] = start;
            excludedDates.splice(i + 1, 0, [start, range_end]);
          }
          /**
           * 如果结束时间大于限制范围的开始时间，并且结束时间小于限制范围的结束时间，注意：比较结束时间时，应该与更改后的结束时间进行比较
           * 修改限制范围内容的开始时间为结束时间
           */
          if (
            timeCompare(end, range_start) > 0 &&
            timeCompare(end, range[1]) < 0
          ) {
            range[0] = end;
          }
        }
      });
      this.pickerOptions.selectableRange = excludedDates;
      console.log(this.pickerOptions);

      // 比较两个时间的大小
      function timeCompare(time1, time2) {
        var t1 = new Date();
        var parts = time1.split(":");
        t1.setHours(parts[0], parts[1], parts[2], 0);
        var t2 = new Date();
        parts = time2.split(":");
        t2.setHours(parts[0], 0);

        if (t1.getTime() > t2.getTime()) return 1;
        if (t1.getTime() < t2.getTime()) return -1;
        return 0;
      }
    },
    checkTimeRange() {
      let result = true;
      let excludedDates = this.timesList;
      for (let i = 0; i < this.timesList.length; i++) {
        let range = this.timesList[i];
        if (!range[0] || !range[1]) return (result = false);
        let start = range[0];
        let end = range[1];
        for (let j = 0; j < excludedDates.length; j++) {
          console.log("scend");
          let _range = excludedDates[j];
          let _start = _range[0];
          let _end = _range[1];
          if (timeCompare(start, _start) > 0 && timeCompare(start, _end) < 0) {
            console.log("---1---");
            return (result = false);
          }
          console.log(
            timeCompare(end, _start),
            timeCompare(end, _end),
            end,
            _end
          );
          if (timeCompare(end, _start) > 0 && timeCompare(end, _end) < 0) {
            console.log("---2---");
            return (result = false);
          }
        }
      }
      return result;

      // 比较两个时间的大小
      function timeCompare(time1, time2) {
        var t1 = new Date();
        var parts = time1.split(":");
        t1.setHours(parts[0], parts[1], parts[2], 0);
        var t2 = new Date();
        parts = time2.split(":");
        t2.setHours(parts[0], parts[1], parts[2], 0);

        if (t1.getTime() > t2.getTime()) return 1;
        if (t1.getTime() < t2.getTime()) return -1;
        return 0;
      }
    }
  }
};
</script>
<style lang="less" scoped>
.form-box {
  width: auto;
  display: flex;

  .el-form {
    flex: 1;
  }
}

#allmap {
  width: 450px;
  height: 350px;
  border: 1px solid #ccc;
  margin: 0 20px;
}

.mapText {
  // visibility: hidden;
  // top: -40px;
  // z-index: -1;
  // position: relative;
}

.address {
  .el-row {
    height: 35px;
    margin-bottom: 10px;

    .el-col-24 {
      height: 35px;
    }
  }
}

.tangram-suggestion-main {
  z-index: 2;
}
.times-line {
  display: flex;
  align-items: center;
  .el-date-editor {
    margin-right: 10px;
  }
  & + .times-line {
    margin-top: 10px;
  }
}
#suggestionList {
  z-index: 1000;
      position: absolute;
        list-style-type: none;
        padding: 0;
        margin: 0;
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

    #suggestionList li .item_info{
        font-size: 12px;
        color:grey;
        
    }
    
    #suggestionList li a:hover:not(.header) {
        background-color: #eee;
    }
</style>
