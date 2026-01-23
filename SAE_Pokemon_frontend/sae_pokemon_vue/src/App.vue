<script setup>
import { RouterView } from 'vue-router'
import { useNotify } from '@/composables/useNotify.js';

const notify = useNotify();
</script>

<template>
  <v-app>
    <RouterView />

    <v-snackbar
      v-model="notify.isVisible.value"
      :color="notify.color.value"
      :timeout="notify.timeout.value"
      location="bottom center"
      elevation="4"
      rounded="pill"
    >
      <div class="d-flex align-center">
        <v-icon start class="mr-2">
          {{ notify.color.value.includes('green') ? 'mdi-check-circle' : (notify.color.value.includes('red') ? 'mdi-alert-circle' : 'mdi-information') }}
        </v-icon>
        <span class="text-body-1 font-weight-medium">{{ notify.message.value }}</span>
      </div>

      <template v-slot:actions>
        <v-btn
          color="white"
          variant="text"
          icon="mdi-close"
          @click="notify.close()"
        >
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<style scoped>
.v-snackbar :deep(.v-snackbar__content) {
  padding: 12px 24px;
}
</style>
