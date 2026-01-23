import { ref, watch } from 'vue';

// 1. ÉTAT GLOBAL (Singleton)
const settings = ref({
  activeGameId: 1,
  maxId: 1025
});

// 2. INITIALISATION SÉCURISÉE
const storedSettings = localStorage.getItem('pokedex_settings');
if (storedSettings) {
  try {
    const parsed = JSON.parse(storedSettings);

    settings.value = { ...settings.value, ...parsed };

    if (!settings.value.maxId || settings.value.maxId === 0) {
      settings.value.maxId = 1025;
    }

  } catch (e) {
    console.error("Erreur lecture settings", e);
    localStorage.removeItem('pokedex_settings');
  }
}

// 3. SAUVEGARDE AUTOMATIQUE
watch(settings, (newVal) => {
  localStorage.setItem('pokedex_settings', JSON.stringify(newVal));
}, { deep: true });


export function useSettings() {
  return { settings };
}
