<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

defineProps(['currentTab']);
defineEmits(['update:currentTab', 'openSettings']);

const router = useRouter();

const currentUser = ref({
  pseudo: "Chargement...",
  email: ""
});

onMounted(() => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      const userData = JSON.parse(userStr);
      currentUser.value = {
        pseudo: userData.pseudo || "Dresseur",
        email: userData.email || ""
      };
    } catch (e) {
      console.error("Erreur lecture user", e);
    }
  }
});

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  router.push('/login');
};
</script>

<template>
  <v-app-bar color="#2c3e50" height="80" elevation="3" class="px-4" style="z-index: 1000 !important;">

    <div class="d-flex ga-4 align-center">
      <button
        v-for="tab in [
          { id: 'pokedex', icon: '📖', label: 'Pokédex' },
          { id: 'caught', icon: '✅', label: 'Attrapés' },
          { id: 'wish', icon: '✨', label: 'Souhaités' }
        ]"
        :key="tab.id"
        class="nav-main-btn"
        :class="{ active: currentTab === tab.id }"
        @click="$emit('update:currentTab', tab.id)"
      >
        <span>{{ tab.icon }}</span> {{ tab.label }}
      </button>
    </div>

    <v-spacer></v-spacer>

    <div class="d-flex align-center ga-4">
      <button class="icon-btn" @click="$emit('openSettings')" title="Réglages">⚙️</button>

      <v-menu min-width="260px" rounded="xl" transition="slide-y-transition" offset="15">
        <template v-slot:activator="{ props }">
          <button class="icon-btn" v-bind="props" title="Compte">
            👤
          </button>
        </template>

        <v-list class="pa-4 dropdown-card-vuetify">
          <v-list-item class="mb-2">
            <template v-slot:prepend>
              <div class="avatar-circle mr-3">👤</div>
            </template>
            <v-list-item-title class="font-weight-bold">{{ currentUser.pseudo }}</v-list-item-title>
            <v-list-item-subtitle>{{ currentUser.email }}</v-list-item-subtitle>
          </v-list-item>

          <v-divider class="my-3"></v-divider>

          <v-btn
            block
            color="#ffebee"
            elevation="0"
            class="text-none text-red-darken-4 font-weight-bold"
            rounded="lg"
            @click="handleLogout"
          >
            <span>🚪</span> Se déconnecter
          </v-btn>
        </v-list>
      </v-menu>
    </div>
  </v-app-bar>
</template>

<style scoped>
.nav-main-btn {
  border: none; border-bottom: 5px solid #b8860b; padding: 10px 25px;
  font-family: 'Segoe UI', sans-serif; font-weight: 800; font-size: 0.95rem;
  color: #2c3e50; cursor: pointer; border-radius: 30px;
  background: linear-gradient(to bottom, #ffd54f, #ffb300);
  display: flex; align-items: center; gap: 10px; text-transform: uppercase;
  box-shadow: 0 4px 10px rgba(0,0,0,0.2); transition: all 0.15s ease;
}
.nav-main-btn.active { filter: brightness(1.2); transform: translateY(2px); border-bottom-width: 2px;}

.icon-btn {
  font-size: 1.6rem; cursor: pointer; background: rgba(255, 255, 255, 0.15);
  border: none; border-radius: 50%; width: 50px; height: 50px;
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.3s; color: #ffd54f;
}

.avatar-circle {
  width: 40px; height: 40px;
  background: #f0f0f0; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; font-size: 1.2rem;
}

/* Style de la carte menu pour qu'elle ressemble à l'ancienne */
.dropdown-card-vuetify {
  box-shadow: 0 10px 30px rgba(0,0,0,0.3) !important;
  border: none !important;
}
</style>
