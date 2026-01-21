<script setup>
import { useSettings } from '../composables/useSettings';

const { settings } = useSettings();
defineEmits(['close']);
</script>

<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="settings-box">
      <button class="close-modal" @click="$emit('close')">✖</button>
      <h2>⚙️ Paramètres</h2>
      <hr>

      <div class="form-group">
        <label>Limite Pokédex :</label>
        <select v-model="settings.maxId">
          <option :value="151">Gen 1 : Kanto (1-151)</option>
          <option :value="251">Gen 2 : Johto (1-251)</option>
          <option :value="386">Gen 3 : Hoenn (1-386)</option>
          <option :value="493">Gen 4 : Sinnoh (1-493)</option>
          <option :value="1025">Tout afficher</option>
        </select>
      </div>

      <div class="form-group">
        <label>Volume : {{ settings.volume }}%</label>
        <input type="range" v-model="settings.volume" min="0" max="100">
      </div>

      <button class="save-btn" @click="$emit('close')">💾 Fermer</button>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(3px);
  display: flex; justify-content: center; align-items: center; z-index: 1000;
}
.settings-box {
  background: white; width: 90%; max-width: 450px; padding: 30px;
  border-radius: 20px; text-align: left; position: relative;
}
.close-modal { position: absolute; top: 15px; right: 20px; font-size: 1.5rem; cursor: pointer; background:none; border:none;}
.form-group { margin-bottom: 20px; }
.form-group label { display: block; font-weight: bold; margin-bottom: 8px; color: #333;}
.form-group select, .form-group input { width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ddd; }
.save-btn { width: 100%; padding: 12px; background: #27ae60; color: white; border: none; border-radius: 8px; cursor: pointer; }
</style>
