/* eslint-disable no-useless-escape */
/* eslint-disable */
// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import BootstrapVue from 'bootstrap-vue'
Vue.use(BootstrapVue)
Vue.use(require('vue-chartist'))
require('chartist-plugin-legend')


import VueResource from 'vue-resource'
Vue.use(VueResource)
import VTagInput from 'v-tag-input'
Vue.use(VTagInput)

import moment from 'moment'

import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'bootstrap/dist/css/bootstrap.css'

import App from './App'
import Issue from '@/components/Issue'
Vue.component('issue',Issue)

import router from './router'

Vue.config.productionTip = false

Vue.filter('formatHours', function (h) {
    let float = parseFloat(h.replace(/,/, '.'))
    //return moment.utc(seconds * 1000).format('HH:mm:ss')
    return (Math.round(float * 100) / 100).toFixed(1)
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App, Issue }
})
