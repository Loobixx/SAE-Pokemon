<template>
  <v-container fluid class="fill-height login-bg">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="elevation-12 pa-5" rounded="xl">
          <v-card-item class="text-center">
            <v-icon icon="mdi-pokeball" color="red" size="64" class="mb-2"></v-icon>
            <v-card-title class="text-h4 font-weight-bold">Connexion</v-card-title>
            <v-card-subtitle>Accédez à votre Pokédex personnel</v-card-subtitle>
          </v-card-item>

          <v-card-text>
            <v-form @submit.prevent="handleLogin">
              <v-text-field
                v-model="username"
                label="Nom d'utilisateur"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-2"
                required
              ></v-text-field>

              <v-text-field
                v-model="password"
                label="Mot de passe"
                prepend-inner-icon="mdi-lock"
                :type="showPassword ? 'text' : 'password'"
                :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showPassword = !showPassword"
                variant="outlined"
                required
              ></v-text-field>

              <v-btn
                type="submit"
                color="primary"
                size="large"
                block
                class="mt-4"
                :loading="loading"
              >
                Se connecter
              </v-btn>
            </v-form>
          </v-card-text>

          <v-card-actions class="justify-center">
            <p class="text-body-2">
              Pas encore de compte ?
              <router-link to="/register" class="text-primary font-weight-bold">S'inscrire</router-link>
            </p>
          </v-card-actions>

          <v-alert v-if="errorMessage" type="error" variant="tonal" class="mt-3">
            {{ errorMessage }}
          </v-alert>
        </v-card>
      </v-col>
      </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// import { useAuth } from '../composables/useAuth'; // Tu créeras ça plus tard

const router = useRouter();
const username = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);
const errorMessage = ref('');

const handleLogin = async () => {
  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: username.value,
        password: password.value
      })
    });

    // 1. On vérifie d'abord si la réponse est OK
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify({ email: data.email, pseudo: data.pseudo }));
      router.push('/');
    } else {
      // 2. Si erreur (401, 403...), on essaie de lire le message d'erreur
      const errorData = await response.json().catch(() => ({}));
      errorMessage.value = errorData.message || "Email ou mot de passe incorrect.";
    }
  } catch (error) {
    // 3. Ici, c'est une vraie erreur réseau (serveur éteint, pas de réseau)
    console.error("Erreur login:", error);
    errorMessage.value = "Connexion impossible : le serveur ne répond pas.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-bg {
  /* On garde le même fond que ton Pokédex pour la cohérence */
  background-color: #f5f5f5;
}
</style>
