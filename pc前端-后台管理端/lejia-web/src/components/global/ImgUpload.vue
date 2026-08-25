<template lang="html">
  <div class="img-upload">
    <!--   <el-upload action="#" :auto-upload="false" accept="image/*" :show-file-list="false" :before-upload="beforeUpload" :on-change="handleSuccess" v-if="limit==1" :class="imgList.length ? 'hover' : ''">
      <img v-if="imgList.length" :src="imgList[0].url" class="img">
      <i class="el-icon-plus uploader-icon" v-if="!imgList.length"></i>
      <span class="el-upload-list__item-actions index2">
        <span class="el-upload-list__item-delete" @click.stop="handleRemove">
          <i class="el-icon-delete"></i>
        </span>
      </span>
    </el-upload> -->
    <!-- <el-upload action="#" list-type="picture-card" :auto-upload="false" accept="image/*"  :before-upload="beforeUpload" :on-change="handleSuccess" :file-list="imgList" :class="{hide:imgList.length==limit}"> -->
    <el-upload action="#" list-type="picture-card" accept="image/*" :before-upload="beforeUpload"
      :http-request="uploadImg" :file-list="imgList" :on-change="handleSuccess" :class="{hide:imgList.length==limit}">
      <i slot="default" class="el-icon-plus"></i>
      <div slot="file" slot-scope="{file}" class="file-box">
        <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
        <div class="loading-icon" v-show="file.url.indexOf('blob')>=0">
          <i class="el-icon-loading"></i>
        </div>
        <span class="el-upload-list__item-actions">
          <span class="el-upload-list__item-left" @click.stop="handleLeft(file)" v-if="file.name != 0">
            <i class="el-icon-back"></i>
          </span>
          <span class="el-upload-list__item-left" @click.stop="handleDownload(file)" v-if="needDownload">
            <i class="el-icon-download"></i>
          </span>
          <span
          class="el-upload-list__item-preview"
          @click="handlePictureCardPreview(file)"
        >
          <i class="el-icon-zoom-in"></i>
        </span>
          <span class="el-upload-list__item-delete" @click.stop="handleRemove(file)">
            <i class="el-icon-delete"></i>
          </span>
          <span class="el-upload-list__item-right" @click.stop="handleRight(file)" v-if="file.name < imgList.length-1">
            <i class="el-icon-right"></i>
          </span>
        </span>
      </div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible" append-to-body>
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
    <el-dialog v-if="isCube" title="图片裁切" :visible.sync="leadingVisible" custom-class="cropper_dialog"
      :close-on-click-modal="false" append-to-body :before-close="closeDialog">
      <div class="cropper-content">
        <div class="cropper">
          <vue-cropper
            ref="cropper"
            :img="option.img"
            :outputSize="option.size"
            :outputType="outputType"
            :info="true"
            :full="option.full"
            :canMove="option.canMove"
            :canMoveBox="option.canMoveBox"
            :original="option.original"
            :autoCrop="option.autoCrop"
            :autoCropWidth="autoCropWidth"
            :autoCropHeight="autoCropHeight"
            :fixedBox="option.fixedBox"
            :fixed="fixed"
            :fixedNumber="fixedNumber"
            :centerBox="option.centerBox"
            :maxImgSize="3000"
          >
          </vue-cropper>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onCubeImg()">确定</el-button>
        <el-button @click="cancel()">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { VueCropper } from 'vue-cropper';
export default {
  data() {
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      imgServer: api.common.uploadImage,
      imgList: [],
      reBackList: [],
      type: [
        'image/jpg',
        'image/JPG',
        'image/GIF',
        'image/PNG',
        'image/jpeg',
        'image/gif',
        'image/bmp',
        'image/png',
      ],
      loading: false,
      // disabled: false // 是否可上传
      leadingVisible: false,
      option: {
        img: '',
        size: 0.9,
        full: true, //是否输出原图比例的截图
        outputType: 'png',
        canMove: true, //能否拖动图片
        fixedBox: false, //截图固定大小
        original: false, //上传图片是否显示原始宽高 (针对大图 可以铺满)
        canMoveBox: true, //能否拖动截图框
        autoCrop: true, //固定截图框
        centerBox: false,
        fileUpload: '',
        fileinfo: {},
        imgServe: 'http://192.168.129.117:8080/',
      },
      cubeImg: '',
    };
  },
  methods: {
    handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
    /**
     * @desc 下载图片
     */
    handleDownload(file) {
      window.open(file.url);
    },
    uploadImg: async function (f, cube = false) {
      console.log('isCube', this.isCube);
      if (this.isCube && !cube) {
        this.blobToDataURL(f.file, (base64) => {
          this.cubeImg = f;
          this.cubeImg.name = this.imgList.length - 1;
          this.option.img = base64;
          this.leadingVisible = true;
        });
        return
      }
      let params = new FormData();
      console.log("f",f,f.file,f.file.name)
      params.append("title", f.file.name);
      params.append("memo", "杭州小程序");
      params.append('file', f.file);
      await axios
        .post(api.common.uploadImage, params, {
          headers: {
            Authorization: this.$store.state.token,
          },
        })
        .then((response) => {
          this.imgList[this.imgList.length - 1].url = response.url;
          // this.imgList.push({ name: this.imgList.length, url: response.url });
          f.onSuccess(response);

          // if (this.limit == 1) {
          //   this.reBackList = this.imgList[0].url;
          //   this.$emit("change", this.reBackList);
          // } else {
          this.reBackList = this.imgList.map((item) => {
            return item.url;
          });
          if (this.compIndex != -1)
            this.$emit('change', this.reBackList, this.compIndex);
          else this.$emit('change', this.reBackList);
        });
      // this.$emit("loading", false);
    },
    handleSuccess: function (response) {
      this.imgList.push({
        name: this.imgList.length,
        url: response.url,
      });
    },
    handleLeft: function (file) {
      var index;
      this.imgList.forEach((item, i) => {
        if (item == file) index = i;
      });
      if (index != 0) {
        this.imgList[index] = this.imgList.splice(
          index - 1,
          1,
          this.imgList[index]
        )[0];
      } else {
        this.imgList.push(this.imgList.shift());
      }
      this.imgList = this.imgList.map((item, index) => {
        item.name = index;
        return item;
      });
      this.reBackList = this.imgList.map((item) => {
        return item.url;
      });
      if (this.compIndex != -1)
        this.$emit('change', this.reBackList, this.compIndex);
      else this.$emit('change', this.reBackList);
    },
    handleRight: function (file) {
      var index;
      this.imgList.forEach((item, i) => {
        if (item == file) index = i;
      });
      if (index != this.imgList.length - 1) {
        this.imgList[index] = this.imgList.splice(
          index + 1,
          1,
          this.imgList[index]
        )[0];
      } else {
        this.imgList.unshift(this.imgList.splice(index, 1)[0]);
      }
      this.imgList = this.imgList.map((item, index) => {
        item.name = index;
        return item;
      });
      this.reBackList = this.imgList.map((item) => {
        return item.url;
      });
      if (this.compIndex != -1)
        this.$emit('change', this.reBackList, this.compIndex);
      else this.$emit('change', this.reBackList);
    },
    handleRemove: function (file, fileList) {
      this.imgList.splice(file.name, 1);
      this.imgList = this.imgList.map((item, index) => {
        item.name = index;
        return item;
      });
      if (this.limit == 1) {
        if (this.compIndex != -1) this.$emit('change', [], this.compIndex);
        else this.$emit('change', []);
      } else {
        this.reBackList = this.imgList.map((item) => {
          return item.url;
        });
        if (this.compIndex != -1)
          this.$emit('change', this.reBackList, this.compIndex);
        else this.$emit('change', this.reBackList);
      }
    },
    beforeUpload: function (file) {
      const isJPG = this.type.includes(file.type);
      const isLt2M = file.size / 1024 / 1024 < this.maxSize;

      if (!isJPG) {
        this.$message.error('上传文件只能是图片格式!');
        this.imgList.splice(this.imgList.length - 1, 1);
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 ' + this.maxSize + 'MB!');
      }
      return isJPG && isLt2M;
    },
    /**
     * 更新图片
     */
    updateImg: function (imgList) {
      if (!imgList) {
        this.imgList = [];
        return;
      }

      if (typeof imgList == 'string') {
        this.imgList = [
          {
            name: 0,
            url: imgList,
          },
        ];
      } else {
        this.imgList = [];
        imgList.forEach((item, index) => {
          this.imgList.push({
            name: index,
            url: item,
          });
        });
      }
      console.log('update', this.imgList);
      // }
    },
    /**
     * @desc 关闭弹窗
     * @params {Object} file
     */
     closeDialog() {
      this.leadingVisible = false;
      this.handleRemove(this.cubeImg);
      this.cubeImg = '';
    },
    /**
     * @desc 获取裁剪图片信息
     */
     onCubeImg() {
      // this.handleRemove();
      // let _this = this;
      // 获取cropper的截图的base64 数据
      this.$refs.cropper.getCropData((data) => {
        // this.$refs.uploadImg.src = data;
        //this.fileinfo.url = data
        this.leadingVisible = false;
        //先将显示图片地址清空，防止重复显示
        this.option.img = '';
        //将剪裁后base64的图片转化为file格式
        let file = this.dataURLtoFile(data, this.cubeImg.file.name);
        this.cubeImg.file = file;
        this.uploadImg(this.cubeImg, true);
      });
      this.leadingVisible = false;
    },
    /**
     * @desc 取消剪辑
     * @params {Object} file
     */
     cancel() {
      this.leadingVisible = false;
      this.handleRemove(this.cubeImg);
      this.cubeImg = '';
    },
    /**
     * @desc 将base64的图片转换为file文件
     * @param {String} dataUrl 图片路径
     */
     dataURLtoFile(dataUrl, filename) {
      //将base64转换为文件
      var arr = dataUrl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
      }
      return new File([u8arr], filename, {
        type: mime,
      });
    },
    /**
     * @desc 将file的图片转换为base64文件
     * @param {Object} file 图片file对象
     * @param {function} cb 回调函数
     */
     blobToDataURL(file, cb) {
      let reader = new FileReader();
      reader.onload = function (evt) {
        let base64 = evt.target.result;
        cb(base64);
      };
      reader.readAsDataURL(file);
    },
  },
  components: {
    VueCropper,
  },
  props: {
    maxSize: {
      type: Number,
      default: 2,
    },
    limit: {
      type: Number,
      default: 1,
    },
    compIndex: {
      type: Number,
      default: -1,
    },
    needDownload: {
      type: Boolean,
      default: false,
    },
    isCube: {
      //是否需要剪辑图片
      type: Boolean,
      default: false,
    },
    autoCropWidth: {
      type: Number,
      default: 300,
    },
    autoCropHeight: {
      type: Number,
      default: 225,
    },
    fixedNumber: {
      type: Array,
      default: () => {
        return [4, 3];
      },
    },
    fixed: {
      type: Boolean,
      default: false,
    },
    outputType: {
      type: String,
      default: 'png',
    },
  },
};
</script>
<style lang="less">
@img-width: 100px;

.img-upload {
  .el-upload-list {
    height: @img-width;

    .el-upload-list__item {
      margin-bottom: 0;
      width: @img-width;
      height: @img-width;
    }
  }

  .el-upload {
    position: relative;
    width: @img-width;
    height: @img-width;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;

    overflow: hidden;

    &:hover {
      border-color: #409eff;

      .hover {
        .el-upload-list__item-actions {
          // display: block;
          opacity: 1;
        }
      }
    }

    &.el-upload--picture-card {
      width: @img-width;
      height: @img-width;
      line-height: @img-width;

      .el-upload-list__item {
        width: @img-width;
        height: @img-width;
      }
    }
  }

  .uploader-icon {
    z-index: 100;
    position: relative;
    font-size: 28px;
    color: #8c939d;
    width: @img-width;
    height: @img-width;
    line-height: @img-width;
    text-align: center;
  }

  .img {
    max-width: @img-width;
    max-height: @img-width;
    display: block;
  }

  .el-upload-list__item-actions {
    z-index: 0;
    position: absolute;
    left: 0;
    top: 0;
    // display: none;
    width: 100%;
    height: 100%;

    cursor: default;
    font-size: 20px;
    text-align: center;

    color: #fff;
    opacity: 0;

    background-color: rgba(0, 0, 0, 0.5);
    transition: opacity 0.3s;

    &:after {
      display: inline-block;
      content: '';
      height: 100%;
      vertical-align: middle;
    }

    span {
      display: inline-block;
      cursor: pointer;

      & + span {
        margin-left: 5px;
      }
    }

    .el-upload-list__item-delete {
      position: static;
      font-size: inherit;
      color: inherit;
    }
  }

  .hover {
    .el-upload-list__item-actions:hover {
      // display: block;
      opacity: 1;
    }
  }

  .hide .el-upload--picture-card {
    display: none;
  }
}
</style>
<style lang="less" scoped>
/deep/.el-upload-list__item-thumbnail {
  height: auto !important;
}

.file-box {
  position: relative;
  height: 100%;
}

.loading-icon {
  position: absolute;
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  background-color: rgba(0, 0, 0, 0.5);
  color: #fff;
}


.cropper-content {
  display: flex;
  display: -webkit-flex;
  justify-content: flex-end;
  -webkit-justify-content: flex-end;
  width: 700px;
  height: 650px;

  .cropper {
    width: 100%;
    height: 100%;
  }

  .show-preview {
    flex: 1;
    -webkit-flex: 1;
    display: flex;
    display: -webkit-flex;
    justify-content: center;
    -webkit-justify-content: center;

    .preview {
      overflow: hidden;
      border-radius: 50%;
      border: 1px solid #cccccc;
      background: #cccccc;
      margin-left: 40px;
    }
  }
}
</style>