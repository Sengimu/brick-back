Vue.component('user_nav', {
    <!-- 导航栏background-color: transparent -->
    template: `
    <div style="position: absolute;left: 0;top: 0;right: 0;bottom: 0;overflow-y: scroll">
        <el-menu default-active="4" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose" :collapse="isCollapse" @select="handleSelect">
                <el-menu-item index="1">
                    <span slot="title"><img :src="\`data:image/png;base64,\` + textureHead"/></span>
                </el-menu-item>
                <el-menu-item index="2" style="text-align: center">
                <span slot="title">
                    {{user.username}}
                </span>
                </el-menu-item>
                <el-menu-item index="3" style="margin-top: 40px">
                    <i class="el-icon-menu"></i>
                    <span slot="title">展开/收起</span>
                </el-menu-item>
                <el-menu-item index="4">
                    <i class="el-icon-document"></i>
                    <span slot="title">我的角色</span>
                </el-menu-item>
            </el-menu>
    </div>`,
    data() {
        return {
            isCollapse: true,
            user: {
                id: "",
                username: ""
            },
            textureHead: ""
        }
    },
    mounted() {
        this.getInfo();
    },
    methods: {
        handleSelect(key, keyPath) {
            if (key == "3") {
                this.isCollapse = !this.isCollapse;
            }
            if (key == "4") {
                window.location.href = "../user/profile.html";
            }
        },
        handleOpen(key, keyPath) {
        },
        handleClose(key, keyPath) {
        },
        getInfo() {
            let that = this;
            axios.get("/web/info/getInfo")
                .then(function (resp) {
                    if (resp.data.code == 1) {
                        document.title = resp.data.data.title;
                        if (resp.data.data.user != null) {
                            that.user.id = resp.data.data.user.id;
                            that.user.username = resp.data.data.user.username;
                        }
                    }
                    that.getTextureHead();
                })
        },
        getTextureHead() {
            let that = this;
            let i = that.user.id == "" ? 0 : that.user.id;
            axios.get("/web/profile/getHeadById/" + i)
                .then(function (resp) {
                    if (resp.data.code == 1) {
                        that.textureHead = resp.data.data.headBase64String;
                    }
                })
        }
    }
})