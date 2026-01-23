<script setup>
import { ref, onMounted } from 'vue';
import { useSettings } from '../composables/useSettings.js';
const props = defineProps({ isOpen: Boolean });
const GEN_LIMITS = {
  1: 151,  // Gen 1
  2: 251,  // Gen 2
  3: 386,  // Gen 3
  4: 493,  // Gen 4
  5: 649,  // Gen 5
  6: 721,  // Gen 6
  7: 809,  // Gen 7
  8: 905,  // Gen 8
  9: 1025  // Gen 9
};

const emit = defineEmits(['close']);
const { settings } = useSettings();

const games = ref([]);
const loading = ref(true);

// Variable locale pour le formulaire
const selectedGame = ref(settings.value.activeGameId || null);

const fetchGames = async () => {
  try {
    const token = localStorage.getItem('token');


    const response = await fetch('http://localhost:8080/api/game/list', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.ok) {
      games.value = await response.json();
    }
  } catch (e) {
    console.error("Erreur chargement jeux", e);
  } finally {
    loading.value = false;
  }
};

// --- FORMATAGE ---
const formatGameName = (game) => {
  if (!game) return '';
  const region = game.regionName ? game.regionName.charAt(0).toUpperCase() + game.regionName.slice(1) : '???';
  const gen = game.name.replace('generation-', '').toUpperCase();
  return `Génération ${gen} : ${region}`;
};

const saveSettings = () => {
  if (selectedGame.value) {
    settings.value.activeGameId = selectedGame.value;

    const limit = GEN_LIMITS[selectedGame.value] || 1025;
    settings.value.maxId = limit;
  }
  emit('close');
};

onMounted(() => {
  fetchGames();
});
</script>

<template>
  <v-dialog
    :model-value="isOpen"
    persistent
    max-width="500"
    @update:model-value="val => !val && emit('close')"
  >
    <v-card rounded="xl" elevation="4">
      <v-toolbar color="primary" density="compact">
        <v-toolbar-title class="text-h6 font-weight-bold ml-4">
          <v-icon start icon="mdi-cog" size="small" class="mr-2"></v-icon>
          Paramètres
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn icon="mdi-close" variant="text" @click="emit('close')"></v-btn>
      </v-toolbar>

      <v-card-text class="pt-6 pb-4">
        <p class="text-body-2 text-grey-darken-1 mb-4">
          Choisissez la génération de référence pour votre Pokédex.
        </p>

        <v-select
          v-model="selectedGame"
          :items="games"
          :loading="loading"
          item-title="formattedName"
          item-value="id"
          label="Version du jeu"
          variant="outlined"
          density="comfortable"
          color="primary"
          bg-color="grey-lighten-5"
          prepend-inner-icon="mdi-controller"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item v-bind="props" :title="formatGameName(item.raw)" subtitle="Version officielle"></v-list-item>
          </template>

          <template v-slot:selection="{ item }">
            <span class="font-weight-medium text-primary-darken-1">
              {{ formatGameName(item.raw) }}
            </span>
          </template>
        </v-select>
      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions class="pa-4 bg-grey-lighten-5">
        <v-spacer></v-spacer>
        <v-btn variant="text" color="grey-darken-1" @click="emit('close')">
          Annuler
        </v-btn>
        <v-btn
          color="primary"
          variant="flat"
          class="px-6"
          rounded="pill"
          @click="saveSettings"
        >
          Valider
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
