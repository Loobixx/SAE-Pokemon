const app = document.querySelector('#app')

// --- 1. CSS & STYLES ---
const style = document.createElement('style');
style.innerHTML = `
  nav { background-color: #333; padding: 1rem; margin-bottom: 20px; display: flex; justify-content: center; gap: 15px; }
  nav button { background: #ffcb05; border: none; padding: 10px 20px; font-weight: bold; color: #2a75bb; cursor: pointer; border-radius: 5px; transition: background 0.3s; }
  nav button:hover { background: #c7a008; }
  .card-container { display: flex; flex-wrap: wrap; gap: 20px; justify-content: center; padding: 20px; }
  .card { background: white; border: 1px solid #ddd; border-radius: 10px; width: 200px; text-align: center; padding: 15px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); transition: transform 0.3s ease; }
  .card:hover { transform: scale(1.1); box-shadow: 0 10px 20px rgba(0,0,0,0.2); cursor: pointer; }
  .card img { width: 120px; height: 120px; object-fit: contain; margin-bottom: 10px; }
  .card h3 { margin: 10px 0 5px; text-transform: capitalize; }
  .loading { font-size: 1.5rem; color: #666; text-align: center; margin-top: 50px; }
`;
document.head.appendChild(style);


// --- 2. DONNÉES LOCALES (Pour les autres onglets) ---
const pokemonsAttrapes = [
  { id: 25, name: 'Pikachu', type: 'Elec' }
];
const pokemonsSouhaites = [
  { id: 151, name: 'Mew', type: 'Psy' }
];


// --- 3. FONCTIONS ---

function getNavbarHTML() {
  return `
    <nav>
      <button id="btn-pokedex">📖 Pokédex (Back)</button>
      <button id="btn-caught">✅ Attrapés</button>
      <button id="btn-wish">✨ Souhaités</button>
    </nav>
  `;
}

function attacherListenersNavbar() {
  document.getElementById('btn-pokedex').addEventListener('click', () => afficherPage('pokedex'));
  document.getElementById('btn-caught').addEventListener('click', () => afficherPage('caught'));
  document.getElementById('btn-wish').addEventListener('click', () => afficherPage('wish'));
}

function genererCarteHTML(pokemon) {
  // Astuce : On utilise l'API publique "PokeAPI" pour avoir les images officielles grâce à l'ID du backend
  const imageUrl = `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.id}.png`;

  return `
    <div class="card">
      <img src="${imageUrl}" alt="${pokemon.name}">
      <h3>${pokemon.name}</h3>
      <p>Type: ${pokemon.type}</p>
      <small>#${pokemon.id}</small>
    </div>
  `;
}

// --- 4. GESTION DES PAGES (ASYNC pour attendre le backend) ---

async function afficherPage(pageName) {
  let titre = "";
  let listePokemons = [];
  let couleurFond = "";

  // Affichage d'un chargement si on appelle le backend
  if (pageName === 'pokedex') {
    app.innerHTML = `${getNavbarHTML()}<div class="loading">Chargement des données depuis Java...</div>`;
  }

  try {
    switch(pageName) {
      case 'pokedex':
        titre = "📖 Pokédex (Données du Serveur)";
        couleurFond = "#f4f4f4";
        // 👇 C'EST ICI LA MAGIE : On demande la liste à Spring Boot
        const response = await fetch('http://localhost:8080/api/pokemons');
        listePokemons = await response.json();
        break;

      case 'caught':
        titre = "✅ Mes Pokémons Attrapés";
        listePokemons = pokemonsAttrapes;
        couleurFond = "#e8f5e9";
        break;

      case 'wish':
        titre = "✨ Ma Liste de Souhaits";
        listePokemons = pokemonsSouhaites;
        couleurFond = "#fff3e0";
        break;
    }

    // Génération des cartes
    const cartesHTML = listePokemons.map(pkmn => genererCarteHTML(pkmn)).join('');

    app.innerHTML = `
      ${getNavbarHTML()}
      <div style="text-align:center; background-color: ${couleurFond}; min-height: 80vh; padding-bottom: 20px;">
          <h1>${titre}</h1>
          <p>Il y a ${listePokemons.length} Pokémon(s) chargés.</p>
          <div class="card-container">
              ${cartesHTML.length > 0 ? cartesHTML : '<p>Aucun Pokémon trouvé.</p>'}
          </div>
      </div>
    `;

    attacherListenersNavbar();

  } catch (error) {
    console.error("Erreur :", error);
    app.innerHTML = `${getNavbarHTML()}<h1>❌ Erreur de connexion au Backend</h1><p>Vérifie que Spring Boot est lancé.</p>`;
    attacherListenersNavbar();
  }
}

// --- 5. LANCEMENT ---
afficherPage('pokedex');
