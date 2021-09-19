<template>
<div class="login" id="login">
   ...
    <div class="log-email">
        <input type="text" placeholder="Email" :class="'log-input' + (account==''?' log-input-empty':'')" v-model="account"><input type="password" placeholder="Password" :class="'log-input' + (password==''?' log-input-empty':'')"  v-model="password">
        <a href="javascript:;" class="log-btn" @click="login">Login</a>
    </div>
    ...
</div>
</template>
<script>
import Loading from './Loading.vue'
export default {
  name: 'Login',
  data(){
    return {
          isLoging: false,
        account: '',
        password: ''
    }
  },
  components:{
    Loading
  },
  methods:{

    //登录逻辑
    login(){
        if(this.account!='' && this.password!=''){
            this.toLogin();
        }
    }

}
</script>