<template>
  <!-- Don't drop "q-app" class -->
  <div id="q-app">
    <q-layout
      view="lHh Lpr lFf"
      :left-class="{'bg-primary': false}"
    >
      <q-layout-header>
        <q-toolbar color="header" text-color="header">
          <q-btn
            flat
            @click="menuExpand()"
            class="burger-icon"
            :icon="opened ? 'close' : 'menu'"
          >
          </q-btn>

          <q-toolbar-title>
            Anti-monitor
            <div slot="subtitle">Récapitualtif des projets Micro services</div>
          </q-toolbar-title>
        </q-toolbar>
      </q-layout-header>
      <q-layout-drawer

        v-model="opened"
        :content-class="$q.theme === 'mat' ? 'bg-grey-2' : null"
      >
        <div class="tool-bar">
          <q-list no-border link inset-delimiter>
            <div class="tool-bar-header">
              <q-list-header class="main">
                <img src="./assets/sinestro_corps_logo_small.png" alt="logo anti-monitor">
                <h3>&nbsp;Anti-monitor</h3>
              </q-list-header>
              <q-list-header>
                <h4>Menu</h4>
              </q-list-header>
            </div>
            <q-item to="/" exact>
              <q-item-side icon="home"/>
              <q-item-main label="Welcome" sublabel="Page de résumé"/>
            </q-item>
            <q-collapsible icon="fa-cogs" label="Micro services" opened>
              <q-item to="/projects-list">
                <q-item-side icon="view_list"/>
                <q-item-main label="Liste des projets" sublabel="Résumé des derniers build"/>
              </q-item>
              <q-item to="/tables">
                <q-item-side icon="border_all"/>
                <q-item-main label="Liste des tables" sublabel="liaisons entre les services et les tables"/>
              </q-item>
              <q-item to="/apis-list">
                <q-item-side icon="explore"/>
                <q-item-main label="Liste des apis" sublabel="Liste des traitements"/>
              </q-item>
              <q-item to="/dependencies">
                <q-item-side icon="link"/>
                <q-item-main label="Dépendances" sublabel="Dépendance entre les Micro Services"/>
              </q-item>
            </q-collapsible>
            <q-item to="/configuration">
              <q-item-side icon="build"/>
              <q-item-main label="Console d'administration" sublabel="Configuration et outils"/>
            </q-item>
          </q-list>
        </div>
      </q-layout-drawer>
      <q-page-container>
        <router-view/>
      </q-page-container>
    </q-layout>
  </div>
</template>

<script>

  /*
   * Root component
   */
  export default {
    name: 'App',
    data() {
      return {
        opened: this.$q.platform.is.desktop
      }
    },
    methods: {
      menuExpand() {
        if (!this.opened) {
          this.opened = true
        }
        else {
          this.opened = false
        }
      }
    }
  }
</script>
<style lang="stylus">
  @import '~variables'
  .bg-header
    background $background-header !important

  .text-header
    color $color-header !important

  #q-app
    .q-layout-drawer-delimiter
      box-shadow none
    header.q-layout-header
      box-shadow none
    .q-toolbar.text-header.bg-header
      border-bottom 1px solid $border
      .q-btn
        width 45px
        height 45px
        transition all .5s ease-in-out
      .burger-icon
        position relative
        cursor pointer
        .q-icon
          margin-right 0
    .tool-bar
      background-color $primary
      height 100%
      .q-collapsible-sub-item
        padding-left 0
        padding-right 0
      .tool-bar-header
        .q-list-header
          display flex
          justify-content flex-start
          align-items center
          color white
          &.main
            background-color $secondary
            justify-content center
            padding 9px 9px 9px 16px
            border-bottom 1px solid #1d4a41
            h3
              line-height 25px
              letter-spacing: .5px
              font-weight: 700
              margin 0
          h4
            text-transform: uppercase
            font-size: 14px
            font-weight: 500
            padding-left: 16px
            margin 0
      .q-item
        .q-item-side
          color: white
        .q-item-section
          .q-item-label
            color: white
          .q-item-sublabel
            color: $grey-6
      .q-item:focus
      .q-item:hover
      .q-item.router-link-active
        background-color $background
        border-left 6px solid $secondary
        transition all .2s ease-out
        .q-item-section
          color: $grey-10
          .q-item-label
            color: black
          .q-item-sublabel
            color: $grey-8
      .q-item:focus
      .q-item:hover
        border-left:0

  .q-list
    padding-top 0
    .sidebar-header
      &.q-list-header
        line-height 25px
        background-color $primary
        display flex
        justify-content flex-start
        align-items center

        color $white

        h3
          text-shadow: 1px 1px $indigo-10
          letter-spacing: .5px
          font-weight: 700
          font-size: 11px
          text-transform: uppercase
          margin 20px 0 10px
      .side-header
        padding 9px 9px 9px 16px
        background-color $secondary
        border-bottom 1px solid #1d4a41
        span
          font-size 22px
          padding-left 15px



</style>
