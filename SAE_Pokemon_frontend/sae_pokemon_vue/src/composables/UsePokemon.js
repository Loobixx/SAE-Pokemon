import { ref } from 'vue';

const pokemons = ref([]);
const loading = ref(true);
const error = ref(null);

// Fonction utilitaire pour fusionner les lignes de la BDD
const processData = (rawData) => {
  const mergedMap = new Map();

  rawData.forEach(pkmn => {
    const num = pkmn.numero;
    if (!mergedMap.has(num)) {
      mergedMap.set(num, {
        id: pkmn.id,       // ID technique BDD
        numero: num,       // Vrai numéro Pokédex
        name: pkmn.name,
        type: pkmn.type,
        description: pkmn.description,
        normalUrl: null,
        shinyUrl: null,
        showingShiny: false // État pour l'affichage
      });
    }

    const entry = mergedMap.get(num);
    // Gestion de la compatibilité des noms de champs (isShiny vs shiny)
    const isShiny = (pkmn.shiny !== undefined) ? pkmn.shiny : pkmn.isShiny;

    if (isShiny) entry.shinyUrl = pkmn.imageUrl;
    else entry.normalUrl = pkmn.imageUrl;
  });

  return Array.from(mergedMap.values()).sort((a, b) => a.numero - b.numero);
};

export function usePokemon() {
  const fetchPokemons = async () => {
    // 1. Récupérer le token depuis le stockage local
    const token = localStorage.getItem('token');

    try {
      const response = await fetch('http://localhost:8080/api/pokemons', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          // 2. C'est CETTE LIGNE qui manque pour enlever la 403
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        // Si le serveur renvoie 403, le message s'affichera sur ton interface
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
