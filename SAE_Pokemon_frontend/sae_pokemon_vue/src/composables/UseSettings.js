import { reactive, watch } from 'vue';

// On initialise avec le LocalStorage ou des valeurs par défaut
const settings = reactive(JSON.parse(localStorage.getItem('pokeSettings')) || {
  maxId: 151, // Gen 1
  volume: 50
});

// Dès que 'settings' change, on sauvegarde automatiquement
watch(settings, (newSettings) => {
  localStorage.setItem('pokeSettings', JSON.stringify(newSettings));
});

export function useSettings() {
  return { settings };
}
