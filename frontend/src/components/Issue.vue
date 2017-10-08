<template>
    <b-card :border-variant="headerStyle"
                :header-bg-variant="headerStyle"
                header-text-variant="white"
                :header="'n. ' + issue.number"
                :footer="'User: ' + issue.user.login"
                class="text-center">
            <p class="card-text"><a target="_blank" :href='issue.htmlUrl'>{{issue.title}}</a></p>
            <p class="card-text" v-if="issue.labels.length > 0"><b>Labels:</b> <span v-for="l in issue.labels"><span  :style="'color: #' + l.color">{{l.name}}</span>, </span></p>
            <p class="card-text">{{issue.body}}</p>
    </b-card>
</template>

<script>
export default {
  name: 'issue',
  computed: {
    headerStyle () {
      if (this.issue.state === 'closed') {
        return 'danger'
      }
      if (this.issue.state === 'open') {
        return 'success'
      }
    }
  },
  methods: {
  },
  props: ['issue'],
  data () {
    return {
    }
  }
}
</script>
