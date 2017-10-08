<template>
  <div class="hello">
    <h1>Piattaforma di analytics delle issue github</h1>
    <h6 class="mb-3"><a href="https://github.com/italia/anpr/issues/314" target="_blank">Issue ANPR #314</a></h6>

    <b-form>
    <b-row class="align-items-center">
        <b-col>
          <b-form-group id="githubUserLabel" label="GitHub User:" label-for="githubUser">
            <b-form-input id="githubUser" type="text" v-model.trim="form.githubUser" required placeholder="Enter GitHub User"></b-form-input>
          </b-form-group>
        </b-col>
        <b-col>
          <b-form-group id="githubRepositoryLabel" label="GitHub Repository:" label-for="githubRepository">
            <b-form-input id="githubRepository" type="text" v-model.trim="form.githubRepository" required placeholder="Enter GitHub Repository"></b-form-input>
          </b-form-group>
        </b-col>
    </b-row>
      <b-button-toolbar key-nav class="mb-3 align-center">
           <b-button-group class="mx-0">
              <b-button v-on:click="invokeApi([{name: 'TicketClosingTime'}, {name: 'TicketGeneral'}, {name: 'TicketTimeSeries'}, {name: 'FirstReplyTime'}])" block variant="success" v-if="form.githubUser != '' && form.githubRepository != ''">Statistica dei Ticket <i class="fa fa-angle-right" aria-hidden="true"></i></b-button>
          </b-button-group>
          <b-button-group class="mx-2">
              <b-button variant="primary" v-on:click="invokeApiIssuesClosedWithoutComments">Cerca Closed Without Comments <i class="fa fa-angle-right" aria-hidden="true"></i></b-button>
          </b-button-group>
      </b-button-toolbar>
    </b-form>

    <b-alert show variant="primary" v-if="loading" class="mb-3"><i class="fa fa-circle-o-notch fa-spin fa-3x" aria-hidden="true"></i><br />Caricamento in corso... (be patient)</b-alert>
    <b-alert show variant="danger" v-if="error">Errore di comunicazione con il microservizio</b-alert>

    <b-card no-body class='pt-3 mb-3' v-if="resultPresent">
        <div class="mb-3">
          <b-btn v-if="resultPresent" class="pull-right mr-3" size="sm" variant="outline-danger" @click="unsetMainResults">Chiudi</b-btn>
          <h3 v-if="this.TicketGeneral != null" class="card-text">Ticket Aperti: {{this.TicketGeneral.openTickets}} - Ticket Chiusi: {{this.TicketGeneral.closedTickets}}</h3>
        </div>      
        <b-tabs ref="tabs" v-model='tabs' card>
            <b-tab title="Tempo di Prima Risposta">
              <div v-if="this.FirstReplyTime != null">
                <p class="card-text"><b>Tempo di risposta per range di ore</b><br>Medio: {{this.FirstReplyTime.average | formatHours}}</p>
                <chartist v-if="tabs == 0" type="Bar" ratio="ct-major-eleventh" :data="this.FirstReplyTime.histogram" :options="charts.bar.options"></chartist>
              </div>
            </b-tab>
            <b-tab title="Tempo di Chiusura Ticket" class="text-center">
            <div v-if="this.TicketClosingTime != null">
                <p class="card-text"><b>Numero di ticket chiusti per range di ore</b><br />Medio: {{this.TicketClosingTime.average | formatHours}}<p>
                <div style="background: #ffffff; padding:3px" class="rounded">
                  <chartist v-if="tabs == 1" type="Bar" ratio="ct-major-eleventh" :data="this.TicketClosingTime.histogram" :options="charts.bar.options"></chartist>
                </div>
            </div>
            </b-tab>
            <b-tab title="Trend Ticket aperti/chiusi">
              <div v-if="this.TicketTimeSeries != null">
                <p class="card-text">Range giorni: {{this.TicketTimeSeries.timeRangeDays}}</p>
                <chartist v-if="tabs == 2" ratio="ct-major-eleventh" type="Line" :data="this.TicketTimeSeries.plot" :options="charts.line.options"></chartist>
              </div>
            </b-tab>
        </b-tabs>
    </b-card>

    <div v-if="form.githubUser != '' && form.githubRepository != ''">
      <b-row class="align-items-center ">
          <b-col>
            <b-card class="mb-5" title="Issues con Label">
              <b-form-group id="label_issuesWithLabels" label="Lista di Label (separate da spazio)" label-for="issuesWithLabels">
                <v-tag-input id="issuesWithLabels" v-model="form.plugins.issuesWithLabels.labelList" class="form-control"></v-tag-input>
                    <div class="label"  v-bind:class="{ 'label-success': isIncludedlabelListMode, 'label-default': !isIncludedlabelListMode }" v-on:click="switchLabelListMode" >{{this.isIncludedlabelListMode ? 'Saranno Incluse' : 'Saranno Escluse'}}</div>
              </b-form-group>
              
              <b-form-group id="label_issueState" label="Issues con Stato" label-for="issueState">
                <select v-model="form.plugins.issuesWithLabels.issueState" id="issueState">
                  <option value="open">Open</option>
                  <option value="closed">Closed</option>
                  <option value="any">Any</option>
                </select>
              </b-form-group>
              <b-button variant="warning" v-on:click="invokeApiIssueWithLabels">Cerca <i class="fa fa-angle-right" aria-hidden="true"></i></b-button>
            </b-card>
          </b-col>
          <b-col>
            <b-card class="mb-5" title="Issues con commenti di utenti">
            <b-form-group id="label_issuesCommentedBy" label="Lista Utenti (separati da spazio)" label-for="issuesCommentedBy">
              <v-tag-input id="issuesCommentedBy" v-model="form.plugins.issuesCommentedBy.userList" class="form-control"></v-tag-input>
                  <div class="label"  v-bind:class="{ 'label-success': isIncludedUserListMode, 'label-default': !isIncludedUserListMode }" v-on:click="switchUserListMode" >{{this.isIncludedUserListMode? 'Saranno Inclusi' : 'Saranno Esclusi'}}</div>
            </b-form-group>
            
            <b-form-group id="label_issueState" label="Issues con Stato" label-for="issueState">
              <select v-model="form.plugins.issuesCommentedBy.issueState" id="issueState">
                <option value="open">Open</option>
                <option value="closed">Closed</option>
                <option value="any">Any</option>
              </select>
            </b-form-group>
            <b-button variant="warning" v-on:click="invokeApiCommentedBy">Cerca <i class="fa fa-angle-right" aria-hidden="true"></i></b-button>      
          </b-card>
          </b-col>
      </b-row>

      <div v-if="this.IssuesClosedWithoutComments != null">
        <b-btn class="pull-right mr-3" size="sm" variant="outline-danger" @click="IssuesClosedWithoutComments = null">Chiudi</b-btn>
        <h2>Issues Chiuse Senza Commenti ({{IssuesClosedWithoutComments.totalNotCommentedIssues}})</h2>
        <div class="card-columns">
          <div v-for="issue in IssuesClosedWithoutComments.notCommentedIssues" ><issue :issue="issue"></issue></div>
        </div>
      </div>

      <div v-if="this.IssuesCommentedBy != null">
        <b-btn class="pull-right mr-3" size="sm" variant="outline-danger" @click="IssuesCommentedBy = null">Chiudi</b-btn>
        <h2>Issues filtrate per Autore commento ({{IssuesCommentedBy.totalCommentedIssues}})</h2>
        <div class="card-columns">
          <div v-for="issue in IssuesCommentedBy.commentedIssues"><issue :issue="issue"></issue></div>
        </div>
      </div>

      <div v-if="this.IssuesWithLabels != null">
        <b-btn class="pull-right mr-3" size="sm" variant="outline-danger" @click="IssuesWithLabels = null">Chiudi</b-btn>
        <h2>Issues filtrate per Label ({{IssuesWithLabels.totalIssues}})</h2>
        <div class="card-columns">
          <div v-for="issue in IssuesWithLabels.issues"><issue :issue="issue"></issue></div>
          </span>
        </div>
      </div>
    </div>
      
  </div>
</template>

<script>
export default {
  name: 'stats',
  computed: {
    apiUrl () { /* base api url computed property to bind user inputs */
      return 'http://localhost:19800/api/analyze/' + this.form.githubUser + '/' + this.form.githubRepository
    },
    isIncludedlabelListMode () {
      /* returns true if the value of the LabelListMode is included, used for style settings */
      return this.form.plugins.issuesWithLabels.labelListMode === 'included'
    },
    isIncludedUserListMode () {
      /* returns true if the value of the userListMode is included, used for style settings */
      return this.form.plugins.issuesCommentedBy.userListMode === 'included'
    },
    resultPresent () {
      /* true if main results are present */
      return this.FirstReplyTime != null || this.TicketClosingTime != null || this.TicketGeneral != null || this.TicketTimeSeries != null
    }
  },
  methods: {
    unsetMainResults () {
      /* clear main resulsts */
      this.FirstReplyTime = null
      this.TicketClosingTime = null
      this.TicketGeneral = null
      this.TicketTimeSeries = null
    },
    switchLabelListMode () {
      /* return the mode of the labelList */
      this.form.plugins.issuesWithLabels.labelListMode = this.isIncludedlabelListMode ? 'excluded' : 'included'
    },
    switchUserListMode () {
      /* return the mode of the userList */
      this.form.plugins.issuesCommentedBy.userListMode = this.isIncludedUserListMode ? 'excluded' : 'included'
    },
    timeRangeToHumanReadable: function (x) {
      /* beutify the lables of the plots, splits the ranges and formats them using Vue custom Filter */
      return x.split('-').map(function (r) {
        return this.$options.filters.formatHours(r)
      }.bind(this)).join(' - ')
    },
    invokeApiIssueWithLabels () {
      this.IssueWithLabels = null // reset current results

      /* Deep copy the object from Component data, otherwise changes to this object will be reflected into the view (2-way binding) */
      let data = [JSON.parse(JSON.stringify(this.form.plugins.issuesWithLabels))] // Plugin parameters for the request
      data[0]['name'] = 'IssuesWithLabels' // Add plugins name to the request
      data[0]['labelList'] = JSON.stringify(this.form.plugins.issuesWithLabels.labelList) // Seriliazion fo array fileds: need to be a json String.

      this.invokeApi(data)
    },
    invokeApiCommentedBy () {
      this.CommentedBy = null // reset current results

      /* Deep copy the object from Component data, otherwise changes to this object will be reflected into the view (2-way binding) */
      let data = [JSON.parse(JSON.stringify(this.form.plugins.issuesCommentedBy))] // Plugin parameters for the request
      data[0]['name'] = 'IssuesCommentedBy' // Add plugins name to the request
      data[0]['userList'] = JSON.stringify(this.form.plugins.issuesCommentedBy.userList) // Seriliazion fo array fileds: need to be a json String.

      this.invokeApi(data)
    },
    invokeApiIssuesClosedWithoutComments () {
      this.ClosedWithoutComments = null // reset current results
      this.invokeApi([{name: 'IssuesClosedWithoutComments'}])  // Add plugins name to the request
    },
    invokeApi (data) {
      this.loading = true
      this.error = false

      this.$http.post(this.apiUrl, data).then(response => { // Success Callback
        let json = response.body

        // Iterates over the plugin responses, array of objects
        // Each object contains the plugin name and specific key-value pairs
        json.forEach(function (pluginResponse) {
          // Use "reflection" to invoke renderer for each plugin, passes the plugin response object as input (Inversion of control)
          this['render_' + pluginResponse.name](pluginResponse)
        }.bind(this))
        this.loading = false
      }, response => { // Error Callback
        this.loading = false
        this.error = true
        console.log('Error in the request')
      })
    },
    render_TicketGeneral (element) {
      this.TicketGeneral = element
    },
    render_TicketTimeSeries (element) {
      element['plot'] = JSON.parse(element['plot']) // A json string parameter that is parsed to become an array
      this.TicketTimeSeries = element
    },
    render_IssuesWithLabels (element) {
      element['issues'] = JSON.parse(element['issues']) // A json string parameter that is parsed to become an array
      this.IssuesWithLabels = element
    },
    render_IssuesClosedWithoutComments (element) {
      element['notCommentedIssues'] = JSON.parse(element['notCommentedIssues']) // A json string parameter that is parsed to become an array
      this.IssuesClosedWithoutComments = element
    },
    render_IssuesCommentedBy (element) {
      element['commentedIssues'] = JSON.parse(element['commentedIssues']) // A json string parameter that is parsed to become an array
      this.IssuesCommentedBy = element
    },
    render_TicketClosingTime (element) {
      // console.log(element.name)
      element['histogram'] = JSON.parse(element['histogram']) // A json string parameter that is parsed to become an array
      element['histogram']['labels'] = element['histogram']['labels'].map(this.timeRangeToHumanReadable) // Maps each element of the label array to beutify the string
      this.TicketClosingTime = element
    },
    render_FirstReplyTime (element) {
      // console.log(element.name)
      element['histogram'] = JSON.parse(element['histogram'])
      element['histogram']['labels'] = element['histogram']['labels'].map(this.timeRangeToHumanReadable) // Maps each element of the label array to beutify the string
      this.FirstReplyTime = element
    }
  },
  data () {
    return {
      tabs: 0, // Model for the current show tab of the main results
      loading: false, // loading state for API invoke, simplification (hp. 1 loading at the time)
      error: false, // error state for last API invocation
      form: { // Bag for all user inputs
        githubUser: 'italia',
        githubRepository: 'anpr',
        plugins: {
          issuesCommentedBy: {
            userList: [],
            userListMode: 'excluded',
            issueState: 'any'
          },
          issuesWithLabels: {
            labelList: [],
            labelListMode: 'excluded',
            issueState: 'any'
          }
        }
      },
      charts: { // Default chart options
        line: {
          options: {
            plugins: [
              this.$chartist.plugins.legend({
                legendNames: ['Aperti', 'Chiusi']
              })
            ],
            fullWidth: true,
            chartPadding: {
              right: 40
            }
          }
        },
        bar: {
          options: {
            distributeSeries: true
          }
        }
      },
      /* Keys to persist reply from the server (objects key-value) */
      FirstReplyTime: null,
      TicketClosingTime: null,
      TicketGeneral: null,
      TicketTimeSeries: null,
      IssuesClosedWithoutComments: null,
      IssuesCommentedBy: null,
      IssuesWithLabels: null
    }
  }
}
</script>
