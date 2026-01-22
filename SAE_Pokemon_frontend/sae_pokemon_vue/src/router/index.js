import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PokemonDetailView from '../views/PokemonDetailView.vue'

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
      meta: { isPublic: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { isPublic: true }
    },
    {
      path: '/pokemon/:id',
      name: 'PokemonDetail',
      component: PokemonDetailView,
      props: true
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');

  if (!to.meta.isPublic && !token) {
    next({ name: 'login' });
  }
  else if (to.meta.isPublic && token) {
    next({ name: 'home' });
  }
  else {
    next();
  }
});

export default router
