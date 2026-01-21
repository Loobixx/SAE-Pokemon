import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { isPublic: true } // On définit que cette page est publique
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { isPublic: true } // On définit que cette page est publique
    },
  ],
})

// --- AJOUT DU GARDE BARRIÈRE ---
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token'); // On récupère le token

  // Cas 1 : L'utilisateur n'est pas connecté et essaie d'aller sur une page privée (Home)
  if (!to.meta.isPublic && !token) {
    next({ name: 'login' });
  }
  // Cas 2 : L'utilisateur est DÉJÀ connecté et essaie d'aller sur Login ou Register
  else if (to.meta.isPublic && token) {
    next({ name: 'home' }); // On le renvoie vers le Pokédex
  }
  // Cas 3 : Tout est ok, on laisse passer
  else {
    next();
  }
});

export default router
