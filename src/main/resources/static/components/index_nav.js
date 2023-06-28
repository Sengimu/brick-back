Vue.component('index_nav', {
    <!-- 导航栏background-color: transparent -->
    template: `
    <div>
        <el-menu style="border-bottom: none;" :default-active="activeIndex"
                 class="el-menu-demo" mode="horizontal"
                 @select="handleSelect">
            <el-row>
                <el-col :span="2" :offset="2" style="text-align: left;">
                    <el-link href="../index.html" :underline="false" style="left: 3px;">
                        <el-image
                                style="width: 120px;height: 21px;margin-top: 17px"
                                :src="logoUrl">
                        </el-image>
                    </el-link>
                </el-col>
                <el-col :span="2" :offset="16">
                    <el-menu-item index="1">登录</el-menu-item>
                </el-col>
            </el-row>
        </el-menu>
    </div>`,
    data() {
        return {
            activeIndex: '',
            view: 0,

            logoUrl: "",
            bgUrl: ""
        }
    },
    mounted() {
        this.getInfo();
    },
    methods: {
        handleSelect(key, keyPath) {
            this.view = key;

            if (key == "1") {
                window.location.href = "../login.html";
            }
        },
        getInfo() {
            let that = this;
            axios.get("/web/info/getInfo")
                .then(function (resp) {
                    if (resp.data.code == 1) {
                        document.title = resp.data.data.title;
                        that.logoUrl = resp.data.data.logoUrl;
                        that.bgUrl = resp.data.data.bgUrl;
                    }
                })
        }
    }
})