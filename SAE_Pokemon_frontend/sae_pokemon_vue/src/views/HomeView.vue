<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import NavBar from '../components/NavBar.vue';
import PokemonCard from '../components/PokemonCard.vue';
import SettingsModal from '../components/SettingsModal.vue';

import { usePokemon } from '../composables/UsePokemon.js';
import { useSettings } from '../composables/useSettings.js';

const logout = () => {
  // 1. Supprime le token et les infos utilisateur du stockage du navigateur
  localStorage.removeItem('token');
  localStorage.removeItem('user');

  // 2. Redirige l'utilisateur vers la page de login
  // (Cela déclenchera ton router.beforeEach qui bloquera l'accès aux autres pages)
  router.push('/login');

  // Optionnel : Forcer un rechargement pour réinitialiser tous les états de l'application
  // window.location.reload();
};

// --- LOGIQUE ---
const { pokemons, loading, error, fetchPokemons } = usePokemon();
const { settings } = useSettings();

const currentTab = ref('pokedex');
const isSettingsOpen = ref(false);

// Pagination
const page = ref(1);
const itemsPerPage = 50;

// Listes factices
const caughtList = ref([]);
const wishList = ref([]);

// 1. D'abord on filtre par catégorie/Génération
const baseFilteredPokemons = computed(() => {
  let list = pokemons.value;
  if (currentTab.value === 'caught') list = caughtList.value;
  if (currentTab.value === 'wish') list = wishList.value;

  return list.filter(p => p.numero <= settings.maxId);
});

// 2. Ensuite on calcule le nombre total de pages
const pageCount = computed(() => {
  return Math.ceil(baseFilteredPokemons.value.length / itemsPerPage);
});

// 3. Enfin, on ne garde que les 50 pour la page actuelle
const filteredPokemons = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return baseFilteredPokemons.value.slice(start, end);
});


// --- LOGIQUE DE SCROLL ---
watch(page, () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth' // Pour un effet de défilement fluide
  });
});


// Chargement au démarrage
onMounted(() => {
  fetchPokemons();
});
</script>

<template>
  <v-app>
    <NavBar
      v-model:currentTab="currentTab"
      @openSettings="isSettingsOpen = true"
    />

    <v-main class="bg-grey-lighten-4">
      <v-container fluid class="fill-height align-start">

        <div v-if="loading" class="text-center mt-12">
          <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
          <p class="mt-4 text-h6 text-grey">Chargement des Pokémon...</p>
        </div>

        <v-alert
          v-else-if="error"
          type="error"
          variant="tonal"
          class="mt-5"
          title="Une erreur est survenue"
        >
          {{ error }}
        </v-alert>

        <div v-else class="content">
          <h1 class="text-h3 font-weight-bold mb-2">
            {{ currentTab === 'pokedex' ? '📖 Pokédex' : (currentTab === 'caught' ? '✅ Attrapés' : '✨ Souhaités') }}
          </h1>

          <p class="text-subtitle-1 text-grey-darken-1 mb-6">
            {{ baseFilteredPokemons.length }} Pokémon(s) au total
            <span v-if="currentTab === 'pokedex'">(Filtre: Max {{ settings.maxId }})</span>
          </p>

          <v-icon icon="mdi-pokeball" color="red" size="x-large" class="mb-6"></v-icon>

          <v-row justify="center">
            <v-col
              v-for="pokemon in filteredPokemons"
              :key="pokemon.numero"
              cols="12"
              sm="6"
              md="4"
              lg="2"
              xl="2"
            >
              <PokemonCard :pokemon="pokemon" />
            </v-col>
          </v-row>

          <v-card variant="flat" class="mt-10 pa-4 bg-transparent">
            <v-pagination
              v-model="page"
              :length="pageCount"
              :total-visible="7"
              color="primary"
              rounded="circle"
              elevation="1"
            ></v-pagination>
          </v-card>
        </div>
      </v-container>
    </v-main>

    <SettingsModal
      v-if="isSettingsOpen"
      @close="isSettingsOpen = false"
    />
  </v-app>
</template>

<style scoped>
/* On peut supprimer beaucoup de CSS car Vuetify gère les espacements via les composants */
.content {
  text-align: center;
  padding: 20px 0;
}

h1 {
  color: #2c3e50;
}
</style>

<style scoped>
/* CSS GLOBAL DE LA PAGE */
.content { text-align: center; background-color: #f8f9fa; min-height: 80vh; padding: 20px; }
h1 { color: #2c3e50; font-family: 'Segoe UI', sans-serif; }
.subtitle { color: #666; margin-bottom: 30px; }
.card-container { display: flex; flex-wrap: wrap; gap: 25px; justify-content: center; padding: 30px; }
.loading, .error { font-size: 1.5rem; text-align: center; margin-top: 50px; color: #666; }
.error { color: #c0392b; }
</style>
