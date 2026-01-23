import { ref } from 'vue';

const isVisible = ref(false);
const message = ref('');
const color = ref('success');
const timeout = ref(3000);

export function useNotify() {

  // Fonction pour afficher un succès (Vert)
  const showSuccess = (msg) => {
    message.value = msg;
    color.value = 'green-darken-1';
    isVisible.value = true;
  };

  // Fonction pour afficher une erreur (Rouge)
  const showError = (msg) => {
    message.value = msg;
    color.value = 'red-darken-1';
    isVisible.value = true;
  };

  // Fonction pour afficher une info neutre (Bleu)
  const showInfo = (msg) => {
    message.value = msg;
    color.value = 'blue-darken-1';
    isVisible.value = true;
  };

  // On ferme la notification
  const close = () => {
    isVisible.value = false;
  };

  // On retourne les variables et les fonctions pour les utiliser ailleurs
  return {
    isVisible,
    message,
    color,
    timeout,
    showSuccess,
    showError,
    showInfo,
    close
  };
}
