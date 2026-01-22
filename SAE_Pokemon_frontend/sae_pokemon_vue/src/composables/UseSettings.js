import { reactive, watch } from 'vue';

const settings = reactive(JSON.parse(localStorage.getItem('pokeSettings')) || {
  maxId: 151, // Gen 1
  volume: 50
});

watch(settings, (newSettings) => {
  localStorage.setItem('pokeSettings', JSON.stringify(newSettings));
});

export function useSettings() {
  return { settings };
}
