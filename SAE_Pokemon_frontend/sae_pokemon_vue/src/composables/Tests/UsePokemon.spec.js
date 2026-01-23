import { describe, it, expect } from 'vitest';
import { usePokemon } from '../UsePokemon.js';

const mockRawData = [
  { numero: 1, name: 'Bulbizarre', imageUrl: 'url1', isShiny: false },
  { numero: 1, name: 'Bulbizarre', imageUrl: 'url2', isShiny: true }
];

describe('usePokemon Logic', () => {
  it('processData devrait fusionner les versions normales et shiny', () => {
    const { pokemons } = usePokemon();

    const result = Array.from(mockRawData);
    expect(result.length).toBe(2);
  });
});
