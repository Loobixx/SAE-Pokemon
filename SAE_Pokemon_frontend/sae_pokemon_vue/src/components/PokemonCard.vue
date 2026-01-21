<script setup>
defineProps({
  pokemon: Object
});

const toggleShiny = (pokemon) => {
  if (!pokemon.shinyUrl) return alert("Pas de shiny disponible !");
  pokemon.showingShiny = !pokemon.showingShiny;
};
</script>

<template>
  <div class="card">
    <button
      class="shiny-btn"
      :class="{ active: pokemon.showingShiny }"
      @click="toggleShiny(pokemon)"
      title="Voir en Shiny">
      ✨
    </button>

    <img
      :src="pokemon.showingShiny ? (pokemon.shinyUrl || pokemon.normalUrl) : (pokemon.normalUrl || 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png')"
      :alt="pokemon.name"
      loading="lazy"
    >

    <h3>{{ pokemon.name }}</h3>
    <p class="type">{{ pokemon.type }}</p>
    <small>#{{ pokemon.numero }}</small>
    <div class="desc-text">"{{ pokemon.description || '...' }}"</div>
  </div>
</template>

<style scoped>
.card {
  position: relative; background: white; border: none; border-radius: 15px; width: 220px; height: 400px;
  text-align: center; padding: 20px;
  box-shadow: 0 10px 20px rgba(0,0,0,0.05); transition: transform 0.3s;
}
.card:hover { transform: translateY(-8px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }
.card img { width: 140px; height: 140px; object-fit: contain; margin-bottom: 10px; }
.card h3 { margin: 10px 0 5px; text-transform: capitalize; color: #333; }
.type { color: #e67e22; font-weight: bold; margin: 5px 0; }
.desc-text { font-size: 0.85rem; color: #555; font-style: italic; margin-top: 10px; }

.shiny-btn {
  position: absolute; top: 12px; right: 12px; background: none; border: none;
  font-size: 1.5rem; cursor: pointer; opacity: 0.2; transition: all 0.3s;
}
.shiny-btn:hover { transform: scale(1.2); opacity: 0.7; }
.shiny-btn.active { opacity: 1; filter: drop-shadow(0 0 5px gold); transform: scale(1.1); }
</style>
