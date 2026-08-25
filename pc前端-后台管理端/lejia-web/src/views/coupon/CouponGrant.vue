<!-- 
@name: CouponGrant.vue 
@description: 卡券发放
@author: sx
@url: /coupon/grant
@date: 2020/07/07
-->
<template>
  <div class="table-container">
    <h1 class="title">
      {{ title }}
    </h1>
    <!-- form表单 -->
    <div class="form-box">
      <el-form>
        <el-form-item label="优惠券" :label-width="labelWidth" :required="true">
          <el-select v-model="inputModel.card" placeholder="选择优惠券" filterable>
            <el-option :value="item.pkey" :key="index" :label="item.title" v-for="(item, index) in CouponList">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否指定用户" :label-width="labelWidth" :required="true">
          <el-select v-model="type" placeholder="请选择">
            <el-option label="否" value="0"></el-option>
            <el-option label="是" value="10"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="指定用户" :label-width="labelWidth" :required="true" v-if="type == '10'">
          <!-- <el-select v-model="inputModel.member" placeholder="选择用户" filterable>
            <el-option :value="item.pkey" :key="index" :label="item.name" v-for="(item, index) in UserList"></el-option>
          </el-select> -->
          <search-select ref="memberSelect" :url="api.marketing.queryMember" params="mobile"
            :option="{label:'mobile',value:'pkey'}" @confirm="handleMerChange" :placeholder="'请选择指定用户'"></search-select>
        </el-form-item>
        <el-form-item label="会员属性" :label-width="labelWidth" :required="true" v-else>
          <el-select v-model="inputModel.status" placeholder="请选择">
            <el-option label="所有" value="0"></el-option>
            <!-- <el-option label="年会会员" value="1"></el-option> -->
            <el-option label="普通会员" value="2"></el-option>
            <el-option label="活跃会员" value="3"></el-option>
            <el-option label="非活跃会员" value="4"></el-option>
            <el-option label="新注册会员" value="5"></el-option>
            <el-option label="老会员" value="6"></el-option>
            <el-option label="从未消费会员" value="7"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="el-form-item--submit">
          <el-button type="primary" @click="handleGrant" :loading="loading">
            确认发放
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import qs from 'qs';
import dropdown from '@/assets/js/dropdown';
import SearchSelect from '@/components/global/SearchSelect';
export default {
  data() {
    return {
      labelWidth: '120px',
      loading: false,
      type: '0', //是否为指定用户
      inputModel: {
        card: '',
        member: '',
        status: '',
      },
      CouponList: [],
      UserList: [],
    };
  },
  mounted() {
    dropdown.getCoupon().then((result) => {
      this.CouponList = result;
    });
  },
  components: {
    SearchSelect,
  },
  computed: {
    /**
     * 获取菜单标题
     * @return {[title]} [返回从state状态中获取的选中菜单名]
     */
    title() {
      return this.$store.state.activeName;
    },
  },
  methods: {
    // 获取用户的pkey
    handleMerChange(val) {
      this.inputModel.member = val;
    },
    initData: function () {
      this.type = '0';
      this.inputModel = {
        card: '',
        member: '',
        status: '',
      };
    },
    /**
     * 发货
     */
    handleGrant: function () {
      if (!this.inputModel.card) {
        this.$message.error('请选择优惠券');
        return;
      }
      if (this.type == '10') {
        this.inputModel.status = 10;
        if (!this.inputModel.member) {
          this.$message.error('请选择指定用户');
          return;
        }
      } else {
        if (!this.inputModel.status) {
          this.$message.error('请选择会员属性');
          return;
        }
      }
      this.loading = true;
      const params = this.inputModel;

      axios
        .post(api.marketing.grantCoupon, qs.stringify(params), {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then(() => {
          this.$message.success('发放成功');
          this.initData();
          setTimeout(() => {
            this.loading = false;
          }, 300);
        })
        .catch(() => {
          this.loading = false;
        });
    },
  },
};
</script>