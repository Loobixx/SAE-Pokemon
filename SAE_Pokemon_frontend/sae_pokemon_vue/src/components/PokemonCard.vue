<script setup>
import { computed, ref, nextTick, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useNotify } from '../composables/useNotify.js';

const notify = useNotify();

const props = defineProps({
  pokemon: Object,
  showButtons: { type: Boolean, default: true },

  isCaughtNormal: { type: Boolean, default: false },
  isCaughtShiny: { type: Boolean, default: false },
  isWishedNormal: { type: Boolean, default: false },
  isWishedShiny: { type: Boolean, default: false },
  isWished: { type: Boolean, default: false },

  activeMenuId: [Number, String, null]
});

const emit = defineEmits(['update-lists', 'menu-opened']);

const router = useRouter();

const showMenu = ref(false);
const menuX = ref(0);
const menuY = ref(0);

const handleRightClick = (event) => {
  event.preventDefault();

  emit('menu-opened', props.pokemon.numero);

  showMenu.value = false;
  menuX.value = event.clientX;
  menuY.value = event.clientY;

  nextTick(() => {
    showMenu.value = true;
  });
};

watch(() => props.activeMenuId, (newId) => {
  if (newId !== props.pokemon.numero) {
    showMenu.value = false;
  }
});


const toggleAction = async (type) => {
  const token = localStorage.getItem('token');
  if (!token) return alert("Connectez-vous pour gérer votre collection !");

  const isShiny = props.pokemon.showingShiny;
  let isAlready = false;

  if (type === 'capture') {
    isAlready = isShiny ? props.isCaughtShiny : props.isCaughtNormal;
  } else {
    if (type === 'wish' && (isShiny ? props.isCaughtShiny : props.isCaughtNormal)) {
      return notify.showInfo("Impossible : Vous l'avez déjà capturé !");
    }
    isAlready = isShiny ? props.isWishedShiny : props.isWishedNormal;
  }

  const method = isAlready ? 'DELETE' : 'POST';
  const url = `http://localhost:8080/api/game/${type}`;

  try {
    const response = await fetch(url, {
      method: method,
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ numero: props.pokemon.numero, isShiny: isShiny })
    });

    if (response.ok) {
      showMenu.value = false;
      emit('update-lists');
      notify.showSuccess("Collection mise à jour !");
    } else {
      notify.showError("Erreur lors de la mise à jour.");
    }
  } catch (e) {
    notify.showError("Erreur de connexion.");
  }
};

const currentStatus = computed(() => {
  const isShiny = props.pokemon.showingShiny;
  if (isShiny ? props.isCaughtShiny : props.isCaughtNormal) return 'caught';
  if (isShiny ? props.isWishedShiny : props.isWishedNormal) return 'wished';
  return null;
});

const otherStatus = computed(() => {
  const isShiny = props.pokemon.showingShiny;
  if (isShiny ? props.isCaughtNormal : props.isCaughtShiny) return 'caught';
  if (isShiny ? props.isWishedNormal : props.isWishedShiny) return 'wished';
  return null;
});

const mainConfig = computed(() => {
  const isShiny = props.pokemon.showingShiny;
  const status = currentStatus.value;
  if (status === 'caught') return { show: true, color: isShiny ? 'amber-darken-1' : 'green', icon: 'mdi-pokeball', tooltip: isShiny ? 'Shiny Capturé !' : 'Capturé !' };
  if (status === 'wished') return { show: true, color: isShiny ? 'amber-darken-1' : 'blue-lighten-1', icon: 'mdi-star', tooltip: isShiny ? 'Shiny Recherché' : 'Recherché' };
  return { show: false };
});

const badgeConfig = computed(() => {
  const status = otherStatus.value;
  const isBadgeShiny = !props.pokemon.showingShiny;
  if (status === 'caught') return { show: true, color: isBadgeShiny ? 'amber-darken-1' : 'green', icon: 'mdi-pokeball', tooltip: isBadgeShiny ? 'Shiny aussi capturé' : 'Normal aussi capturé' };
  if (status === 'wished') return { show: true, color: isBadgeShiny ? 'amber-darken-1' : 'blue-lighten-1', icon: 'mdi-star', tooltip: isBadgeShiny ? 'Shiny aussi souhaité' : 'Normal aussi souhaité' };
  return { show: false };
});

const toggleShiny = (pokemon, event) => {
  if (event) event.stopPropagation();
  if (!pokemon.shinyUrl) return alert("Pas de shiny disponible !");
  pokemon.showingShiny = !pokemon.showingShiny;
};

const goToDetail = () => {
  router.push({
    name: 'PokemonDetail',
    params: { id: props.pokemon.id || props.pokemon.numero }
  });
};
</script>

<template>
  <v-hover v-slot="{ isHovering, props }">
    <v-card
      v-bind="props"
      :elevation="isHovering ? 8 : 2"
      class="pokemon-card d-flex flex-column align-center justify-center cursor-pointer transition-swing pa-4"
      rounded="xl"
      height="100%"
      min-height="320"
      @click="goToDetail"
      @contextmenu="handleRightClick"
    >
      <v-menu
        v-model="showMenu"
        :style="{ position: 'absolute', top: `${menuY}px`, left: `${menuX}px` }"
        absolute
        offset-y
        :close-on-content-click="false"
      >
        <v-list density="compact" rounded="lg" elevation="4" width="200">
          <v-list-subheader class="text-caption font-weight-bold text-uppercase">
            {{ pokemon.showingShiny ? '✨ Mode Shiny' : 'Mode Normal' }}
          </v-list-subheader>
          <v-divider class="mb-2"></v-divider>

          <v-list-item @click="toggleAction('capture')" link>
            <template v-slot:prepend>
              <v-icon :color="(pokemon.showingShiny ? isCaughtShiny : isCaughtNormal) ? 'red' : 'green'">
                {{ 'mdi-pokeball' }}
              </v-icon>
            </template>
            <v-list-item-title>{{ (pokemon.showingShiny ? isCaughtShiny : isCaughtNormal) ? 'Relâcher' : 'Capturer' }}</v-list-item-title>
          </v-list-item>

          <v-list-item @click="toggleAction('wish')" link>
            <template v-slot:prepend>
              <v-icon :color="(pokemon.showingShiny ? isWishedShiny : isWishedNormal) ? 'grey' : 'amber'">
                {{ (pokemon.showingShiny ? isWishedShiny : isWishedNormal) ? 'mdi-star-off' : 'mdi-star' }}
              </v-icon>
            </template>
            <v-list-item-title>{{ (pokemon.showingShiny ? isWishedShiny : isWishedNormal) ? 'Retirer vœu' : 'Souhaiter' }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>

      <div class="card-indicators">
        <div class="icon-wrapper">
          <v-avatar v-if="mainConfig.show" :color="mainConfig.color" size="34" class="elevation-3 main-avatar">
            <v-icon color="white" size="18">{{ mainConfig.icon }}</v-icon>
          </v-avatar>
          <v-tooltip v-if="mainConfig.show" activator="parent" location="top">{{ mainConfig.tooltip }}</v-tooltip>

          <div v-if="badgeConfig.show" :class="mainConfig.show ? 'sub-icon-badge' : 'standalone-badge'">
            <v-avatar :color="badgeConfig.color" size="18" class="elevation-2 border-white">
              <v-icon color="white" size="12">{{ badgeConfig.icon }}</v-icon>
            </v-avatar>
            <v-tooltip activator="parent" location="top">{{ badgeConfig.tooltip }}</v-tooltip>
          </div>
        </div>
      </div>

      <div v-if="showButtons" class="card-shiny-btn">
        <v-btn icon variant="text" size="small" :color="pokemon.showingShiny ? 'amber' : 'grey-lighten-1'" :class="{ 'opacity-100': pokemon.showingShiny, 'opacity-50': !pokemon.showingShiny }" @click="toggleShiny(pokemon, $event)">
          <span class="text-h5">✨</span>
          <v-tooltip activator="parent" location="top">{{ pokemon.showingShiny ? 'Revenir en Normal' : 'Voir en Shiny ✨' }}</v-tooltip>
        </v-btn>
      </div>

      <div v-if="!showButtons && pokemon.showingShiny" class="card-shiny-static" title="Version Shiny">
        <span class="text-h5">✨</span>
      </div>

      <v-img :src="pokemon.showingShiny ? (pokemon.shinyUrl || pokemon.normalUrl) : (pokemon.normalUrl || 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png')" width="140" height="140" class="mb-3 mt-4" contain>
        <template v-slot:placeholder><div class="d-flex align-center justify-center fill-height"><v-progress-circular indeterminate color="grey-lighten-4"></v-progress-circular></div></template>
      </v-img>

      <h3 class="text-h6 font-weight-bold text-capitalize text-grey-darken-3 mb-1">{{ pokemon.name }}</h3>
      <span class="text-caption text-grey font-weight-medium">#{{ pokemon.numero }}</span>

    </v-card>
  </v-hover>
</template>

<style scoped>
.pokemon-card { position: relative !important; }
.card-indicators { position: absolute; top: 12px; left: 12px; z-index: 5; }
.icon-wrapper { position: relative; display: inline-flex; align-items: center; justify-content: center; min-width: 20px; min-height: 20px; }
.sub-icon-badge { position: absolute; bottom: -4px; right: -4px; z-index: 10; display: flex; align-items: center; justify-content: center; border-radius: 50%; box-shadow: 0 2px 4px rgba(0,0,0,0.2); }
.standalone-badge { position: relative; display: flex; align-items: center; justify-content: center; border-radius: 50%; box-shadow: 0 2px 4px rgba(0,0,0,0.2); transform: scale(1.1); }
.border-white { border: 2px solid white !important; }
.card-shiny-btn { position: absolute; top: 8px; right: 8px; z-index: 5; }
.card-shiny-static { position: absolute; top: 12px; right: 12px; z-index: 5; filter: drop-shadow(0 0 3px gold); cursor: default; }
.opacity-50 { opacity: 0.5; }
.opacity-100 { opacity: 1; filter: drop-shadow(0 0 5px gold); transform: scale(1.1); }
</style>
