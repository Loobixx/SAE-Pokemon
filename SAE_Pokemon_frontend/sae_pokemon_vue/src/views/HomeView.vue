<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';

import NavBar from '../components/NavBar.vue';
import PokemonCard from '../components/PokemonCard.vue';
import SettingsModal from '../components/SettingsModal.vue';

import { usePokemon } from '../composables/UsePokemon.js';
import { useSettings } from '../composables/useSettings.js';

const router = useRouter();
const route = useRoute();

const { pokemons, loading, error, fetchPokemons } = usePokemon();
const { settings } = useSettings();

const currentTab = ref('pokedex');
const isSettingsOpen = ref(false);

const page = ref(1);
const itemsPerPage = 50;

// --- GESTION DU MENU UNIQUE (Le Chef d'Orchestre) ---
const activeMenuId = ref(null);

const handleMenuOpened = (id) => {
  activeMenuId.value = id; // On note qui est ouvert
};
// ----------------------------------------------------

const capturedList = ref([]);
const wishedList = ref([]);

const capturedKeys = computed(() =>
  new Set(capturedList.value.map(c => `${c.numero}-${c.isShiny}`))
);

const wishedKeys = computed(() =>
  new Set(wishedList.value.map(w => `${w.numero}-${w.isShiny}`))
);

const refreshLists = async () => {
  await fetchCapturedList();
  await fetchWishedList();
};

const pageTitle = computed(() => {
  switch (currentTab.value) {
    case 'caught': return '✅ Attrapés';
    case 'wish': return '✨ Souhaités';
    default: return '📖 Pokédex';
  }
});

const baseFilteredPokemons = computed(() => {
  if (!pokemons.value.length) return [];

  if (currentTab.value === 'caught') {
    return capturedList.value.map(capture => {
      const staticData = pokemons.value.find(p => p.numero === capture.numero);
      if (!staticData) return null;
      return { ...staticData, showingShiny: capture.isShiny };
    }).filter(p => p !== null);
  }
  else if (currentTab.value === 'wish') {
    return wishedList.value
      .filter(wish => {
        const alreadyCaught = capturedList.value.some(cap =>
          cap.numero === wish.numero && cap.isShiny === wish.isShiny
        );
        return !alreadyCaught;
      })
      .map(wish => {
        const staticData = pokemons.value.find(p => p.numero === wish.numero);
        if (!staticData) return null;
        return { ...staticData, showingShiny: wish.isShiny };
      }).filter(p => p !== null);
  }

  // --- C'EST ICI QUE CELA BLOQUAIT ---
  if (currentTab.value === 'pokedex') {
    // On ajoute .value ici !
    return pokemons.value.filter(p => p.numero <= settings.value.maxId);
  }

  return pokemons.value;
});

const pageCount = computed(() => {
  return Math.ceil(baseFilteredPokemons.value.length / itemsPerPage);
});

const filteredPokemons = computed(() => {
  const start = (page.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return baseFilteredPokemons.value.slice(start, end);
});

const fetchCapturedList = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  try {
    const response = await fetch('http://localhost:8080/api/game/captured', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (response.ok) capturedList.value = await response.json();
  } catch (e) { console.error(e); }
};

const fetchWishedList = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  try {
    const response = await fetch('http://localhost:8080/api/game/wished', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (response.ok) wishedList.value = await response.json();
  } catch (e) { console.error(e); }
};

watch(page, () => { window.scrollTo({ top: 0, behavior: 'smooth' }); });
watch(currentTab, () => { page.value = 1; });

onMounted(() => {
  fetchPokemons();
  fetchCapturedList();
  fetchWishedList();
  if (route.query.tab) {
    currentTab.value = route.query.tab;
    router.replace({ query: null });
  }
});
</script>

<template>
  <v-app>
    <NavBar v-model:currentTab="currentTab" @openSettings="isSettingsOpen = true" />

    <v-main class="bg-grey-lighten-4" style="padding-top: 100px !important;">
      <v-container fluid class="py-8">
        <v-row justify="center" class="mb-6">
          <v-col cols="12" class="text-center">
            <h1 class="text-h3 font-weight-bold mb-2 text-primary-dark">{{ pageTitle }}</h1>
            <p class="text-subtitle-1 text-grey-darken-1">
              {{ baseFilteredPokemons.length }} Pokémon(s)
              <span v-if="currentTab === 'pokedex'" class="text-caption">(Filtre: Max {{ settings.maxId }})</span>
            </p>
            <div class="d-flex justify-center mt-3">
              <v-icon icon="mdi-pokeball" color="red" size="40"></v-icon>
            </div>
          </v-col>
        </v-row>

        <v-row v-if="loading" justify="center" class="mt-12">
          <v-col cols="12" class="text-center">
            <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
            <p class="mt-4 text-h6 text-grey">Attrapez-les tous...</p>
          </v-col>
        </v-row>

        <v-row v-else-if="error" justify="center" class="mt-5">
          <v-col cols="12" md="8">
            <v-alert type="error" variant="tonal" title="Erreur de chargement">{{ error }}</v-alert>
          </v-col>
        </v-row>

        <v-row v-else-if="baseFilteredPokemons.length === 0" justify="center" class="mt-10">
          <v-col cols="12" class="text-center empty-state">
            <v-icon icon="mdi-pokeball" size="100" color="grey-lighten-2"></v-icon>
            <h3 class="text-h5 text-grey mt-4">{{ currentTab === 'wish' ? 'Tous vos vœux sont exaucés !' : 'Aucun Pokémon ici.' }}</h3>
          </v-col>
        </v-row>

        <template v-else>
          <div class="pokemon-grid">
            <PokemonCard
              v-for="pokemon in filteredPokemons"
              :key="pokemon.numero + '-' + pokemon.showingShiny"
              :pokemon="pokemon"
              :show-buttons="currentTab === 'pokedex'"
              :is-caught-normal="capturedKeys.has(`${pokemon.numero}-false`)"
              :is-caught-shiny="capturedKeys.has(`${pokemon.numero}-true`)"
              :is-wished-normal="wishedKeys.has(`${pokemon.numero}-false`)"
              :is-wished-shiny="wishedKeys.has(`${pokemon.numero}-true`)"
              :is-wished="wishedKeys.has(`${pokemon.numero}-${pokemon.showingShiny || false}`)"
              @update-lists="refreshLists"

              :active-menu-id="activeMenuId"
              @menu-opened="handleMenuOpened"
            />
          </div>

          <v-row justify="center" class="mt-8 mb-4">
            <v-col cols="auto">
              <v-pagination v-model="page" :length="pageCount" :total-visible="7" color="primary" rounded="circle" elevation="1"></v-pagination>
            </v-col>
          </v-row>
        </template>

      </v-container>
    </v-main>
    <SettingsModal
      :is-open="isSettingsOpen"
      @close="isSettingsOpen = false"
    />  </v-app>
  <NavBar
    v-model:currentTab="currentTab"
    @openSettings="isSettingsOpen = true"
  />
</template>

<style scoped>
.text-primary-dark { color: #2c3e50; }
.empty-state { animation: fadeIn 0.5s ease-in; }
.pokemon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
  padding: 10px;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
