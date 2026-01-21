<script setup>
import { useRouter } from 'vue-router'; // Import nécessaire pour la redirection

defineProps(['currentTab']);
defineEmits(['update:currentTab', 'openSettings']);

const router = useRouter();

// Fonction de déconnexion
const handleLogout = () => {
  localStorage.removeItem('token'); // Supprime le badge de sécurité
  localStorage.removeItem('user');  // Supprime les infos utilisateur
  router.push('/login');            // Redirige vers la page de login
};
</script>

<template>
  <nav>
    <div class="nav-left-group">
      <button class="nav-main-btn" :class="{ active: currentTab === 'pokedex' }" @click="$emit('update:currentTab', 'pokedex')">
        <span>📖</span> Pokédex
      </button>
      <button class="nav-main-btn" :class="{ active: currentTab === 'caught' }" @click="$emit('update:currentTab', 'caught')">
        <span>✅</span> Attrapés
      </button>
      <button class="nav-main-btn" :class="{ active: currentTab === 'wish' }" @click="$emit('update:currentTab', 'wish')">
        <span>✨</span> Souhaités
      </button>
    </div>

    <div class="nav-right-icons">
      <button class="icon-btn" @click="$emit('openSettings')" title="Réglages">⚙️</button>
      <button class="icon-btn" title="Compte">👤</button>
      <button class="icon-btn logout-btn" @click="handleLogout" title="Déconnexion">🚪</button>
    </div>
  </nav>
</template>

<style scoped>
/* Tes styles existants conservés */
nav {
  background-color: #2c3e50; padding: 1rem 2rem; margin-bottom: 20px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 6px 15px rgba(0,0,0,0.3);
}
.nav-left-group { display: flex; gap: 25px; align-items: center;}
.nav-right-icons { display: flex; gap: 15px; }

.nav-main-btn {
  border: none; border-bottom: 5px solid #b8860b; padding: 10px 30px;
  font-family: 'Segoe UI', sans-serif; font-weight: 800; font-size: 0.95rem;
  color: #2c3e50; cursor: pointer; border-radius: 30px;
  background: linear-gradient(to bottom, #ffd54f, #ffb300);
  display: flex; align-items: center; gap: 10px; text-transform: uppercase;
  box-shadow: 0 4px 10px rgba(0,0,0,0.2); transition: all 0.15s ease;
}
.nav-main-btn:hover { background: linear-gradient(to bottom, #ffe082, #ffca28); }
.nav-main-btn.active { filter: brightness(1.2); transform: translateY(2px); border-bottom-width: 2px;}

.icon-btn {
  font-size: 1.6rem; cursor: pointer; background: rgba(255, 255, 255, 0.15);
  border: none; border-radius: 50%; width: 50px; height: 50px;
  display: flex; align-items: center; justify-content: center;
  transition: transform 0.3s; color: #ffd54f;
}
.icon-btn:hover { transform: rotate(20deg) scale(1.1); }

/* Style spécifique pour le bouton de déconnexion (rouge) */
.logout-btn:hover {
  background: rgba(255, 82, 82, 0.3);
  color: #ff5252;
}
</style>
