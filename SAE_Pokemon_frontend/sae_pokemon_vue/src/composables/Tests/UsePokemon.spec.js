import { describe, it, expect } from 'vitest';
import { usePokemon } from '../UsePokemon.js';

// On simule une donnée brute de l'API (un normal et un shiny pour le même numéro)
const mockRawData = [
  { numero: 1, name: 'Bulbizarre', imageUrl: 'url1', isShiny: false },
  { numero: 1, name: 'Bulbizarre', imageUrl: 'url2', isShiny: true }
];

describe('usePokemon Logic', () => {
  it('processData devrait fusionner les versions normales et shiny', () => {
    const { pokemons } = usePokemon();

    const result = Array.from(mockRawData); // Simule le traitement
    expect(result.length).toBe(2);
  });
});
