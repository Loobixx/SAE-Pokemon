<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import NavBar from '../components/NavBar.vue';
import { usePokemon } from '../composables/UsePokemon.js';

// --- INITIALISATION ---
const route = useRoute();
const router = useRouter();
const { pokemons, fetchPokemons } = usePokemon();

const pokemon = ref(null);
const loading = ref(true);
const isShiny = ref(false);

const capturedList = ref([]);
const wishedList = ref([]);

const handleTabNavigation = (tabName) => {
  router.push({ path: '/', query: { tab: tabName } });
};

const loadPokemon = async () => {
  loading.value = true;
  if (pokemons.value.length === 0) {
    await fetchPokemons();
  }
  const idFromUrl = parseInt(route.params.id);
  pokemon.value = pokemons.value.find(p => (p.id || p.numero) === idFromUrl);
  loading.value = false;
};

const fetchUserLists = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  try {
    const resCaught = await fetch('http://localhost:8080/api/game/captured', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (resCaught.ok) capturedList.value = await resCaught.json();

    const resWished = await fetch('http://localhost:8080/api/game/wished', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (resWished.ok) wishedList.value = await resWished.json();
  } catch (e) { console.error("Erreur listes", e); }
};

const currentImage = computed(() => {
  if (!pokemon.value) return '';
  return isShiny.value
    ? (pokemon.value.shinyUrl || pokemon.value.normalUrl)
    : (pokemon.value.normalUrl || '');
});

// Helpers
const checkCaptured = (shiny) => {
  if (!pokemon.value) return false;
  return capturedList.value.some(c => c.numero === pokemon.value.numero && c.isShiny === shiny);
};
const checkWished = (shiny) => {
  if (!pokemon.value) return false;
  return wishedList.value.some(c => c.numero === pokemon.value.numero && c.isShiny === shiny);
};

// Configs Icones
const mainConfig = computed(() => {
  const shiny = isShiny.value;
  if (checkCaptured(shiny)) return { show: true, color: shiny ? 'amber-darken-1' : 'green', icon: 'mdi-pokeball', tooltip: 'Capturé !' };
  if (checkWished(shiny)) return { show: true, color: shiny ? 'amber-darken-1' : 'blue-lighten-1', icon: 'mdi-star', tooltip: 'Recherché' };
  return { show: false };
});

const badgeConfig = computed(() => {
  const otherShiny = !isShiny.value;
  if (checkCaptured(otherShiny)) return { show: true, color: otherShiny ? 'amber-darken-1' : 'green', icon: 'mdi-pokeball', tooltip: 'Autre version capturée' };
  if (checkWished(otherShiny)) return { show: true, color: otherShiny ? 'amber-darken-1' : 'blue-lighten-1', icon: 'mdi-star', tooltip: 'Autre version recherchée' };
  return { show: false };
});


// --- ACTIONS INTELLIGENTES (TOGGLE) ---

const toggleCaught = async () => {
  const token = localStorage.getItem('token');
  if (!token) return router.push('/login');

  const isAlready = checkCaptured(isShiny.value);
  // Si déjà attrapé -> DELETE. Sinon -> POST.
  const method = isAlready ? 'DELETE' : 'POST';
  const url = 'http://localhost:8080/api/game/capture';

  try {
    const response = await fetch(url, {
      method: method,
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ numero: pokemon.value.numero, isShiny: isShiny.value })
    });

    if (response.ok) {
      // Message adapté
      const msg = isAlready
        ? `${pokemon.value.name} a été relâché...`
        : `Bravo ! ${pokemon.value.name} a été capturé !`;

      // On utilise un petit toast ou juste on rafraichit (ici alert simple)
      // alert(msg); // Optionnel, peut être énervant à la longue
      await fetchUserLists();
    } else {
      alert("Erreur : " + await response.text());
    }
  } catch (e) { console.error(e); }
};

const toggleWish = async () => {
  const token = localStorage.getItem('token');
  if (!token) return router.push('/login');

  // Si on l'a déjà capturé, on ne touche à rien (règle métier)
  if (checkCaptured(isShiny.value)) {
    return alert("Impossible : Vous l'avez déjà capturé ! Relâchez-le d'abord.");
  }

  const isAlready = checkWished(isShiny.value);
  const method = isAlready ? 'DELETE' : 'POST';
  const url = 'http://localhost:8080/api/game/wish';

  try {
    const response = await fetch(url, {
      method: method,
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ numero: pokemon.value.numero, isShiny: isShiny.value })
    });

    if (response.ok) {
      await fetchUserLists();
    } else {
      alert("Info : " + await response.text());
    }
  } catch (e) { console.error(e); }
};

onMounted(() => {
  loadPokemon();
  fetchUserLists();
});
</script>

<template>
  <v-app>
    <NavBar currentTab="pokedex" @update:currentTab="handleTabNavigation" />

    <v-main class="bg-grey-lighten-4">
      <v-container v-if="loading" class="text-center mt-12">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
      </v-container>

      <v-container v-else-if="pokemon" class="py-8">
        <v-btn variant="text" color="primary" class="mb-4" @click="router.back()">
          <v-icon start>mdi-arrow-left</v-icon> Retour au Pokédex
        </v-btn>

        <v-card rounded="xl" class="pa-6" elevation="3">
          <v-row>
            <v-col cols="12" md="5" class="d-flex flex-column align-center border-e-md">

              <div class="position-relative mb-6">
                <div class="detail-indicators">
                  <div class="icon-wrapper" v-if="mainConfig.show || badgeConfig.show">
                    <v-avatar v-if="mainConfig.show" :color="mainConfig.color" size="40" class="elevation-3 main-avatar">
                      <v-icon color="white" size="20">{{ mainConfig.icon }}</v-icon>
                    </v-avatar>

                    <div v-if="badgeConfig.show" :class="mainConfig.show ? 'sub-icon-badge' : 'standalone-badge'">
                      <v-avatar :color="badgeConfig.color" size="20" class="elevation-2 border-white">
                        <v-icon color="white" size="12">{{ badgeConfig.icon }}</v-icon>
                      </v-avatar>
                    </div>
                  </div>
                </div>

                <button
                  class="position-absolute top-0 right-0 pa-2 shiny-hover"
                  style="z-index: 10; background: none; border: none; font-size: 2rem; line-height: 1; right: -15px;"
                  :class="{ 'opacity-100': isShiny, 'opacity-20': !isShiny }"
                  @click="isShiny = !isShiny"
                  title="Voir en Shiny"
                >
                  ✨
                </button>

                <v-img
                  :key="currentImage"
                  :src="currentImage"
                  width="300"
                  height="300"
                  class="transition-swing"
                  alt="Pokemon Image"
                ></v-img>
              </div>

              <p class="text-caption text-grey mb-2">Évolutions futures</p>

              <div class="d-flex ga-4 mb-8">
                <div v-for="n in 3" :key="n" class="d-flex align-center justify-center rounded-circle bg-grey-lighten-5"
                     style="width: 60px; height: 60px; border: 2px dashed #e0e0e0;">
                  <v-icon color="grey-lighten-2" size="large">mdi-help</v-icon>
                </div>
              </div>

              <div class="d-flex flex-wrap ga-4 justify-center w-100 px-4">

                <v-btn
                  :prepend-icon="checkCaptured(isShiny) ? 'mdi-check' : 'mdi-pokeball'"
                  :color="checkCaptured(isShiny) ? 'green-lighten-1' : 'green-darken-1'"
                  class="text-white flex-grow-1"
                  @click="toggleCaught"
                >
                  {{ checkCaptured(isShiny) ? 'Capturé (Relâcher)' : 'Capturer' }}
                </v-btn>

                <v-btn
                  :prepend-icon="checkWished(isShiny) ? 'mdi-check' : 'mdi-star'"
                  :color="checkWished(isShiny) ? 'amber-lighten-1' : 'amber-darken-2'"
                  class="text-white flex-grow-1"
                  @click="toggleWish"
                  :disabled="checkCaptured(isShiny)"
                >
                  <span v-if="checkCaptured(isShiny)">Possédé</span>
                  <span v-else-if="checkWished(isShiny)">Souhaité (Retirer)</span>
                  <span v-else>Souhaiter</span>
                </v-btn>

              </div>
            </v-col>

            <v-col cols="12" md="7" class="pl-md-8 pt-6 pt-md-0">
              <div class="d-flex align-center justify-space-between">
                <h1 class="text-h3 font-weight-bold text-capitalize text-blue-grey-darken-3">
                  {{ pokemon.name }}
                </h1>
                <span class="text-h4 text-grey-lighten-1 font-weight-black">
                  #{{ pokemon.numero }}
                </span>
              </div>
              <v-chip class="mt-2 mb-6" color="primary" label variant="outlined">{{ pokemon.type }}</v-chip>
              <v-divider class="mb-6"></v-divider>
              <h3 class="text-h6 font-weight-bold mb-2">Description</h3>
              <p class="text-body-1 text-grey-darken-2" style="line-height: 1.8;">
                "{{ pokemon.description || 'Aucune description disponible.' }}"
              </p>
            </v-col>
          </v-row>
        </v-card>
      </v-container>

      <v-container v-else class="text-center mt-12">
        <h2 class="text-h5 text-red">Pokémon introuvable</h2>
        <v-btn class="mt-4" to="/">Retour</v-btn>
      </v-container>
    </v-main>
  </v-app>
</template>

<style scoped>
/* Copie exacte de votre style précédent */
.detail-indicators { position: absolute; top: 0; left: 0; z-index: 10; margin: 8px; }
.icon-wrapper { position: relative; display: inline-flex; align-items: center; justify-content: center; min-width: 20px; min-height: 20px; }
.sub-icon-badge { position: absolute; bottom: -4px; right: -4px; z-index: 11; display: flex; align-items: center; justify-content: center; border-radius: 50%; box-shadow: 0 2px 4px rgba(0,0,0,0.2); }
.standalone-badge { position: relative; display: flex; align-items: center; justify-content: center; border-radius: 50%; box-shadow: 0 2px 4px rgba(0,0,0,0.2); transform: scale(1.2); }
.border-white { border: 2px solid white !important; }
.opacity-20 { opacity: 0.2; }
.opacity-100 { opacity: 1; filter: drop-shadow(0 0 5px gold); transform: scale(1.1); }
.shiny-hover { transition: all 0.3s ease; cursor: pointer; }
.shiny-hover:hover { transform: scale(1.2); opacity: 0.7; }
</style>
