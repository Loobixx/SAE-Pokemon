<script setup>
import { ref, onMounted } from 'vue'; // 1. J'ai ajouté onMounted ici
import { useRouter } from 'vue-router';

defineProps(['currentTab']);
defineEmits(['update:currentTab', 'openSettings']);

const router = useRouter();
const isUserMenuOpen = ref(false);

// 2. On met des valeurs vides par défaut (plus de Sacha !)
const currentUser = ref({
  pseudo: "Chargement...",
  email: ""
});

// 3. C'est ce bloc qui va chercher le vrai user connecté
onMounted(() => {
  // On récupère le texte sauvegardé lors du login
  const userStr = localStorage.getItem('user');

  if (userStr) {
    try {
      // On convertit le texte en objet
      const userData = JSON.parse(userStr);

      // On met à jour la variable avec les vraies données
      currentUser.value = {
        pseudo: userData.pseudo || "Dresseur", // Sécurité si pas de pseudo
        email: userData.email || ""
      };
    } catch (e) {
      console.error("Erreur lecture user", e);
    }
  }
});

// Fonction de déconnexion
const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  router.push('/login');
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

      <div class="user-menu-wrapper">
        <button
          class="icon-btn"
          title="Compte"
          @click="isUserMenuOpen = !isUserMenuOpen"
        >
          👤
        </button>

        <div v-if="isUserMenuOpen" class="dropdown-card">
          <div class="user-info">
            <div class="avatar-circle">👤</div>
            <div>
              <p class="pseudo">{{ currentUser.pseudo }}</p>
              <p class="email">{{ currentUser.email }}</p>
            </div>
          </div>

          <hr class="divider">

          <button class="menu-logout-btn" @click="handleLogout">
            <span>🚪</span> Se déconnecter
          </button>
        </div>
      </div>

    </div>
  </nav>
</template>

<style scoped>
nav {
  background-color: #2c3e50; padding: 1rem 2rem; margin-bottom: 20px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 6px 15px rgba(0,0,0,0.3);
  position: relative;
  z-index: 10;
}
.nav-left-group { display: flex; gap: 25px; align-items: center;}
.nav-right-icons { display: flex; gap: 15px; align-items: center; }

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

/* Wrapper pour positionner le menu par rapport au bouton */
.user-menu-wrapper {
  position: relative;
}

/* La carte du menu déroulant */
.dropdown-card {
  position: absolute;
  top: 60px;
  right: 0;
  width: 260px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 100;
  animation: fadeIn 0.2s ease-out;
}

/* Infos utilisateur */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar-circle {
  width: 40px; height: 40px;
  background: #f0f0f0; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; font-size: 1.2rem;
}
.pseudo {
  font-weight: bold; color: #2c3e50; margin: 0; font-size: 1rem;
}
.email {
  color: #7f8c8d; margin: 0; font-size: 0.8rem;
}

/* Séparateur */
.divider {
  border: 0; border-top: 1px solid #eee; margin: 5px 0;
}

/* Nouveau bouton logout dans le menu */
.menu-logout-btn {
  background: #ffebee;
  color: #c62828;
  border: none;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  transition: background 0.2s;
}
.menu-logout-btn:hover {
  background: #ffcdd2;
}

/* Animation d'apparition simple */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
