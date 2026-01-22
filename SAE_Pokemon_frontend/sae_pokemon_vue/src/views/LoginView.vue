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
                v-model="email"
                label="Email"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                class="mb-2"
                :rules="[v => !!v || 'L\'email est requis']"
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
                :rules="[v => !!v || 'Le mot de passe est requis']"
                required
              ></v-text-field>

              <v-btn
                type="submit"
                color="deep-purple-darken-1"
                size="large"
                block
                class="mt-4 text-white"
                :loading="loading"
              >
                Se connecter
              </v-btn>
            </v-form>
          </v-card-text>

          <v-card-actions class="justify-center">
            <p class="text-body-2">
              Pas encore de compte ?
              <router-link to="/register" class="text-decoration-none font-weight-bold" style="color: #6b5b95;">
                S'inscrire
              </router-link>
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

const router = useRouter();
const email = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);
const errorMessage = ref('');

const handleLogin = async () => {
  if (!email.value || !password.value) {
    errorMessage.value = "Veuillez remplir tous les champs.";
    return;
  }

  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: email.value,
        password: password.value
      })
    });

    if (response.ok) {
      const data = await response.json();

      console.log("Réponse du serveur:", data);

      localStorage.setItem('token', data.token);

      const userToSave = {
        email: data.email || (data.user && data.user.email),
        pseudo: data.pseudo || (data.user && data.user.pseudo)
      };

      if (!userToSave.pseudo) {
        console.warn("Attention: Le pseudo n'a pas été trouvé dans la réponse du serveur.");
        userToSave.pseudo = "Dresseur";
      }

      localStorage.setItem('user', JSON.stringify(userToSave));

      router.push('/');

    } else {
      const errorData = await response.json().catch(() => ({}));
      errorMessage.value = errorData.message || "Email ou mot de passe incorrect.";
    }
  } catch (error) {
    console.error("Erreur login:", error);
    errorMessage.value = "Connexion impossible : vérifiez que le serveur Spring Boot est lancé.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-bg {
  background-color: #2c3e50;
  color: white;
}
</style>
