<template>
  <v-container fluid class="fill-height register-bg">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card class="elevation-12 pa-5" rounded="xl">
          <v-card-item class="text-center">
            <v-icon icon="mdi-account-plus" color="primary" size="64" class="mb-2"></v-icon>
            <v-card-title class="text-h4 font-weight-bold">Inscription</v-card-title>
            <v-card-subtitle>Créez votre compte de dresseur</v-card-subtitle>
          </v-card-item>

          <v-card-text>
            <v-form ref="registerForm" @submit.prevent="handleRegister">
              <v-text-field
                v-model="username"
                label="Pseudo"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                class="mb-2"
                :rules="[v => !!v || 'Le pseudo est requis']"
                required
              ></v-text-field>

              <v-text-field
                v-model="email"
                label="Adresse Email"
                prepend-inner-icon="mdi-email"
                type="email"
                variant="outlined"
                class="mb-2"
                :rules="[v => /.+@.+\..+/.test(v) || 'Email invalide']"
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
                class="mb-2"
                required
              ></v-text-field>

              <v-text-field
                v-model="passwordConfirm"
                label="Confirmer le mot de passe"
                prepend-inner-icon="mdi-lock-check"
                :type="showPasswordConfirm ? 'text' : 'password'"
                :append-inner-icon="showPasswordConfirm ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showPasswordConfirm = !showPasswordConfirm"
                variant="outlined"
                :rules="[v => v === password || 'Les mots de passe ne correspondent pas']"
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
                Créer mon compte
              </v-btn>
            </v-form>
          </v-card-text>

          <v-card-actions class="justify-center">
            <p class="text-body-2">
              Déjà un compte ?
              <router-link to="/login" class="text-primary font-weight-bold">Se connecter</router-link>
            </p>
          </v-card-actions>

          <v-alert v-if="errorMessage" type="error" variant="tonal" class="mt-3">
            {{ errorMessage }}
          </v-alert>

          <v-alert v-if="successMessage" type="success" variant="tonal" class="mt-3">
            {{ successMessage }}
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
const registerForm = ref(null);

const username = ref('');
const email = ref('');
const password = ref('');
const passwordConfirm = ref('');

const showPassword = ref(false);
const showPasswordConfirm = ref(false);
const loading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const handleRegister = async () => {
  const { valid } = await registerForm.value.validate();
  if (!valid) return;

  loading.value = true;
  errorMessage.value = '';

  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        pseudo: username.value,
        email: email.value,
        password: password.value
      })
    });

    if (response.ok) {
      successMessage.value = "Compte créé avec succès !";
      setTimeout(() => router.push('/login'), 2000);
    } else {
      const errorText = await response.text();
      errorMessage.value = errorText || "Erreur lors de l'inscription.";
    }
  } catch (error) {
    errorMessage.value = "Erreur réseau : le serveur est-il lancé ?";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-bg {
  background-color: #f5f5f5;
}
</style>
