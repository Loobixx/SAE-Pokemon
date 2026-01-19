const app = document.querySelector('#app')

// --- 1. CSS & STYLES ---
const style = document.createElement('style');
style.innerHTML = `
  /* --- NAVIGATION GLOBALE --- */
  nav {
    background-color: #2c3e50;
    padding: 1rem 2rem;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 6px 15px rgba(0,0,0,0.3);
  }
  .nav-left-group { display: flex; gap: 25px; align-items: center;}
  .nav-right-icons { display: flex; gap: 15px; }

  /* --- BOUTONS NAVIGATION --- */
  .nav-main-btn {
    border: none;
    border-bottom: 5px solid #b8860b;
    padding: 10px 30px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    font-weight: 800;
    font-size: 0.95rem;
    color: #2c3e50;
    cursor: pointer;
    border-radius: 30px;
    background: linear-gradient(to bottom, #ffd54f, #ffb300);
    display: flex;
    align-items: center;
    gap: 10px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.2);
    transition: all 0.15s ease;
  }
  .nav-main-btn span { font-size: 1.3rem; }
  .nav-main-btn:hover {
     background: linear-gradient(to bottom, #ffe082, #ffca28);
     box-shadow: 0 6px 15px rgba(255, 215, 0, 0.3);
  }
  .nav-main-btn:active {
    transform: translateY(5px);
    border-bottom-width: 0px;
    box-shadow: none;
    background: #ffb300;
  }

  /* --- ICONES --- */
  .icon-btn {
    font-size: 1.6rem; cursor: pointer; background: rgba(255, 255, 255, 0.15);
    border: none; border-radius: 50%; width: 50px; height: 50px;
    display: flex; align-items: center; justify-content: center;
    transition: transform 0.3s, background 0.3s; color: #ffd54f;
  }
  .icon-btn:hover { transform: rotate(20deg) scale(1.1); background: rgba(255, 255, 255, 0.25); }

  /* --- CARTES --- */
  .card-container { display: flex; flex-wrap: wrap; gap: 25px; justify-content: center; padding: 30px; }
  .card {
    position: relative; background: white; border: none; border-radius: 15px; width: 210px;
    text-align: center; padding: 20px;
    box-shadow: 0 10px 20px rgba(0,0,0,0.05), 0 6px 6px rgba(0,0,0,0.05);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  .card:hover { transform: translateY(-8px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }
  .card img { width: 130px; height: 130px; object-fit: contain; margin-bottom: 15px; }
  .card h3 { margin: 10px 0 5px; text-transform: capitalize; font-family: 'Segoe UI', sans-serif; color: #333;}
  .card p { color: #666; font-weight: 500;}
  .card small { color: #999; font-weight: bold;}

  /* --- SHINY BTN --- */
  .shiny-btn {
    position: absolute; top: 12px; right: 12px; background: none; border: none;
    font-size: 1.6rem; cursor: pointer; opacity: 0.2;
    transition: transform 0.3s, opacity 0.3s;
  }
  .shiny-btn:hover { transform: scale(1.4) rotate(20deg); opacity: 0.7; }
  .shiny-btn.active { opacity: 1; filter: drop-shadow(0 0 8px gold); animation: pulse 1.5s infinite; }
  @keyframes pulse { 0% { transform: scale(1); } 50% { transform: scale(1.1); } 100% { transform: scale(1); } }

  .loading { font-size: 1.5rem; color: #666; text-align: center; margin-top: 50px; font-weight: bold; }
  h1 { font-family: 'Segoe UI', sans-serif; color: #2c3e50; margin-bottom: 10px; }
`;
document.head.appendChild(style);


// --- 2. DONNÉES LOCALES (Pour l'instant statiques) ---
// À terme, on pourra aussi sauvegarder ça en base de données
const pokemonsAttrapes = [];
const pokemonsSouhaites = [];


// --- 3. FONCTIONS ---

function getNavbarHTML() {
  return `
    <nav>
      <div class="nav-left-group">
        <button id="btn-pokedex" class="nav-main-btn"><span>📖</span> Pokédex</button>
        <button id="btn-caught" class="nav-main-btn"><span>✅</span> Attrapés</button>
        <button id="btn-wish" class="nav-main-btn"><span>✨</span> Souhaités</button>
      </div>
      <div class="nav-right-icons">
        <button id="btn-settings" class="icon-btn" title="Réglages">⚙️</button>
        <button id="btn-account" class="icon-btn" title="Compte">👤</button>
      </div>
    </nav>
  `;
}

// Fonction Shiny
// Nouvelle fonction Shiny : plus de replace() hasardeux !
// On bascule simplement entre les deux URLs qu'on a stockées
window.toggleShiny = function(btn, pokemonId) {
  const img = document.getElementById(`img-${pokemonId}`);

  // On récupère les liens stockés dans la balise img
  const normalSrc = img.getAttribute('data-normal');
  const shinySrc = img.getAttribute('data-shiny');

  // On vérifie quelle est la source actuelle et on inverse
  if (img.src === shinySrc) {
    // Retour à la normale
    img.src = normalSrc;
    btn.classList.remove('active');
  } else {
    // Passage en Shiny
    img.src = shinySrc;
    btn.classList.add('active');
  }
};

function attacherListenersNavbar() {
  document.getElementById('btn-pokedex').addEventListener('click', () => afficherPage('pokedex'));
  document.getElementById('btn-caught').addEventListener('click', () => afficherPage('caught'));
  document.getElementById('btn-wish').addEventListener('click', () => afficherPage('wish'));
  document.getElementById('btn-settings').addEventListener('click', () => alert("🔧 Page réglages à venir !"));
  document.getElementById('btn-account').addEventListener('click', () => alert("👤 Page compte à venir !"));
}

function genererCarteHTML(pokemon) {
  // Sécurité : si l'image est vide, on met une image par défaut
  const normalUrl = pokemon.imageUrl || 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png';
  const shinyUrl = pokemon.shinyImageUrl || 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png';

  return `
    <div class="card">
      <button class="shiny-btn" onclick="toggleShiny(this, ${pokemon.id})" title="Voir en Shiny">✨</button>

      <img id="img-${pokemon.id}"
           src="${normalUrl}"
           data-normal="${normalUrl}"
           data-shiny="${shinyUrl}"
           alt="${pokemon.name}"
           loading="lazy">

      <h3>${pokemon.name}</h3>
      <p>Type: ${pokemon.type}</p>
      <small>#${pokemon.id}</small>
    </div>
  `;
}

// --- 4. GESTION DES PAGES ---

async function afficherPage(pageName) {
  app.innerHTML = `${getNavbarHTML()}<div class="loading">Chargement des données...</div>`;

  let titre = "";
  let listePokemons = [];
  const couleurFond = "#f8f9fa";

  try {
    switch(pageName) {
      case 'pokedex':
        titre = "📖 Pokédex";

        // --- C'EST ICI QUE LA CONNEXION SE FAIT ---
        // On appelle ton Controller Java
        const response = await fetch('http://localhost:8080/api/pokemons');

        if (!response.ok) {
          throw new Error(`Erreur HTTP: ${response.status}`);
        }

        // On convertit la réponse JSON en tableau d'objets JS
        listePokemons = await response.json();
        break;

      case 'caught':
        titre = "✅ Mes Pokémons Attrapés";
        listePokemons = pokemonsAttrapes;
        break;

      case 'wish':
        titre = "✨ Ma Liste de Souhaits";
        listePokemons = pokemonsSouhaites;
        break;
    }

    const cartesHTML = listePokemons.map(pkmn => genererCarteHTML(pkmn)).join('');

    app.innerHTML = `
      ${getNavbarHTML()}
      <div style="text-align:center; background-color: ${couleurFond}; min-height: calc(100vh - 80px); padding-bottom: 40px; padding-top: 20px;">
          <h1>${titre}</h1>
          <p style="color: #666; margin-bottom: 30px;">Il y a ${listePokemons.length} Pokémon(s) chargés.</p>
          <div class="card-container">
              ${cartesHTML.length > 0 ? cartesHTML : '<p>Aucun Pokémon trouvé ou base de données vide.</p>'}
          </div>
      </div>
    `;

    attacherListenersNavbar();

    // Active button style
    document.querySelectorAll('.nav-main-btn').forEach(btn => {
      if(btn.id.includes(pageName)) btn.style.filter = "brightness(1.1)";
    });

  } catch (error) {
    console.error("Erreur :", error);
    app.innerHTML = `
        ${getNavbarHTML()}
        <div style="text-align:center; padding: 50px; color: #c0392b;">
            <h1>❌ Erreur de connexion au Serveur</h1>
            <p>Impossible de joindre <code>localhost:8080</code>.</p>
            <p>Vérifie que ton application Spring Boot est bien lancée (Run).</p>
            <br>
            <small>${error.message}</small>
        </div>`;
    attacherListenersNavbar();
  }
}

// --- 5. LANCEMENT ---
afficherPage('pokedex');
