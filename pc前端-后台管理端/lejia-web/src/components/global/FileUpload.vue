<template>
  <el-upload ref="uploadFile" action="#" list-type="text" :before-upload="beforeUpload" :http-request="uploadfile"
    :file-list="fileList" :on-change="handleSuccess" :on-remove="handleRemove" :limit="limit" :on-exceed="handleExceed"
    :accept="acceptFile.join(',')" :on-preview="handlepreview">
    <el-button size="small" type="primary">上传附件</el-button>
  </el-upload>
</template>

<script>

  export default {
    data() {
      return {
        fileList: [],
        rebackList: [],
        hasInitFile: false,
        acceptFile: []
      };
    },
    watch: {
      fileUrlList: {
        immediate: true,
        handler(newVal, oldVal) {
          console.log(newVal)
          this.updateFileList(newVal);
        },
      },
      fileType: {
        immediate: true,
        handler(newVal, oldVal) {
          if (newVal.length) {
            this.acceptFile = newVal.map(item => {
              return item.type
            })
          }
        },
      }
    },
    methods: {
      /**
       * @desc 文件点击回调
       */
      handlepreview(file){
        window.open(file.url);
      },
      /**
       * @dsc 超出文件上传数量
       */
      handleExceed(files, fileList) {
        this.$message.warning(`只能上传 ${this.limit} 个文件`);
      },
      /**
       * @desc 上传之前的回调
       * @param {object} file 文件信息
       */
      beforeUpload: function (file) {
        console.log(file.type);
        const isLt2M = file.size / 1024 / 1024 < this.maxSize,
          isJPG = this.acceptFile.includes(file.type),
          typeName = this.fileType.length ? this.fileType.map(item => {
            return item.name
          }) : '';
        if (this.fileType.length && !isJPG) {
          this.$message.warning(`请上传${typeName.join(',')}格式文件`);
          return false;
        }
        if (!isLt2M) {
          this.$message.error("上传文件大小不能超过 " + this.maxSize + "MB!");
          return false;
        }
        return isLt2M;
      },
      /**
       * @desc 上传成功的回调
       * @param {object} response 上传信息
       * @param {object} file 文件信息
       * @param {Array} fileList 文件列表
       */
      handleSuccess: function (response, file, fileList) {
        if (this.isCompInfo) {
          if (response.hasOwnProperty('response')) {
            this.fileList.push({
              name: file.name,
              url: response.response.url,
              createdTime: response.response.createdTime,
              size: response.response.size,
              contentType: response.response.contentType
            });
          } else
            this.fileList.push({
              name: file.name,
              url: '',
              createdTime: '',
              size: '',
              contentType: ''
            });
        } else
          this.fileList.push({
            name: file.name,
            url: response.hasOwnProperty('response') ? response.response.url : ''
          });
      },
      /**
       * @desc 上传文件
       * @param {Object} f 上传的文件
       * @param {Boolean} cube 是否通过剪辑图片后触发
       */
      uploadfile: async function (f, cube = false) {
        this.$emit("update:loading", true);
        if (this.isCube && !cube) {
          this.blobToDataURL(f.file, (base64) => {
            this.cubeImg = f;
            this.option.img = base64;
            this.leadingVisible = true;
          });
        } else {
          let url = api.common.uploadFile,
            params = new FormData();
          console.log("f.file", f);
          params.append("file", f.file);
          await axios
            .post(url, params, {
              headers: {
                "Content-Type": "multipart/form-data;charset=UTF-8",
              },
              timeout: 120000,
            })
            .then((response) => {
              let fileData = {
                url: response.url,
                name: response.fileName,
                createdTime: response.createdTime,
                size: response.size,
                contentType: response.contentType
              };
              this.$set(this.fileList, this.fileList.length - 1, fileData);
              f.onSuccess(response);
              this.rebackList = this.fileList.map((item) => {
                return item.url;
              });
              console.log(this.fileList);
              if (this.isCompInfo) {
                if (this.limit == 1) {
                  this.$emit("update:fileUrlList", this.fileList[0]);
                } else {
                  this.$emit("update:fileUrlList", this.fileList);
                }
              } else {
                if (this.limit == 1) {
                  this.$emit("update:fileUrlList", this.rebackList[0]);
                } else {
                  this.$emit("update:fileUrlList", this.rebackList);
                  console.log(this.rebackList);
                }
              }

            });
        }
        this.$emit("update:loading", false);
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
      /**
       *@desc 更新文件
       */
      updateFileList: function (fileList) {
        if (!fileList) {
          this.fileList = [];
          return;
        }
        if (this.isCompInfo) {

          if (!(fileList instanceof Array)) {
            if (!fileList.hasOwnProperty('url')) {
              this.fileList = [];
              return
            }
            this.fileList = [{
              name: fileList.name,
              url: fileList.url,
              createdTime: fileList.createdTime,
              size: fileList.size,
              contentType: fileList.contentType
            }, ];
          } else {
            this.fileList = [];
            fileList.forEach((item, index) => {
              this.fileList.push({
                name: item.name,
                url: item.url,
                createdTime: item.createdTime,
                size: item.size,
                contentType: item.contentType
              });
            });
          }
        } else {
          if (typeof fileList == "string") {
            let name = fileList.slice(fileList.indexOf("file=") + 5, fileList.indexOf("&code"));
            this.fileList = [{
              name: name,
              url: fileList,
            }, ];
          } else {
            this.fileList = [];
            fileList.forEach((item, index) => {
              let name = item.slice(item.indexOf("file=") + 5, item.indexOf("&code"));
              this.fileList.push({
                name: name,
                url: item,
              });
            });
          }
        }
      },
      handleRemove(file, fileList) {
        this.fileList = fileList;
        this.rebackList = this.fileList.map((item) => {
          return item.url;
        });
        if (this.isCompInfo)
          this.$emit("update:fileUrlList", this.fileList);
        else
          this.$emit("update:fileUrlList", this.rebackList);
      },
    },
    props: {
      maxSize: {
        type: Number, // 单位为K  100 = 100K
        default: 10, // 默认为1M
      },
      fileUrlList: {
        type: [String, Array],
        default: () => {
          return "";
        },
      },
      limit: {
        type: [String, Number],
        default: () => {
          return 1;
        },
      },
      isCompInfo: {
        type: Boolean,
        default: () => {
          return false;
        }
      },
      fileType: {
        type: Array,
        default: () => {
          return []
        }
      }
    },
  };
</script>

<style></style>