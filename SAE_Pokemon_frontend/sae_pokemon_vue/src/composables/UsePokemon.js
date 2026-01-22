import { ref } from 'vue';

const pokemons = ref([]);
const loading = ref(true);
const error = ref(null);

const processData = (rawData) => {
  const mergedMap = new Map();

  rawData.forEach(pkmn => {
    const num = pkmn.numero;
    if (!mergedMap.has(num)) {
      mergedMap.set(num, {
        id: pkmn.id,
        numero: num,
        name: pkmn.name,
        type: pkmn.type,
        description: pkmn.description,
        normalUrl: null,
        shinyUrl: null,
        showingShiny: false
      });
    }

    const entry = mergedMap.get(num);
    const isShiny = (pkmn.shiny !== undefined) ? pkmn.shiny : pkmn.isShiny;

    if (isShiny) entry.shinyUrl = pkmn.imageUrl;
    else entry.normalUrl = pkmn.imageUrl;
  });

  return Array.from(mergedMap.values()).sort((a, b) => a.numero - b.numero);
};

export function usePokemon() {
  const fetchPokemons = async () => {
    const token = localStorage.getItem('token');

    try {
      const response = await fetch('http://localhost:8080/api/pokemons', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error(`Erreur API: ${response.status}`);
      }

      const rawData = await response.json();
      pokemons.value = processData(rawData);
    } catch (err) {
      error.value = err.message;
    } finally {
      loading.value = false;
    }
  };

  return { pokemons, loading, error, fetchPokemons };
}
