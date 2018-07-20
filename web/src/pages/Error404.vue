<template>
  <div class="error-page window-height window-width bg-light column items-center no-wrap">
    <div class="error-code bg-primary flex items-center content-center justify-center">
      404
    </div>
    <div>
      <div class="error-card shadow-4 bg-white column items-center justify-center no-wrap">
        <q-icon name="error_outline" color="yellow-8" />
        <p class="caption text-center">
          You want more ?<br />
          Oops. Nothing here...<br />
          Sinestros killed the page you were searching for !
        </p>
        <p class="text-center group">
          <q-btn
            v-if="canGoBack"
            color="primary"
            push
            @click="goBack"
            icon="keyboard_arrow_left"
          >
            Go back
          </q-btn>
          <q-btn
            color="primary"
            push
            @click="$router.replace('/')"
            icon-right="home"
          >
            Go home
          </q-btn>
        </p>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      canGoBack: window.history.length > 1,
    };
  },
  methods: {
    goBack() {
      window.history.go(-1);
    },
  },
};
</script>

<style lang="stylus" scoped>
  @import "../css/pages/error404.styl"
</style>
