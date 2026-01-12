const app = document.querySelector('#app')

app.innerHTML = `
  <h1>Test connexion Front ↔ Back</h1>
  <button id="btn">Tester la connexion</button>
  <p id="result">En attente...</p>
`

document.querySelector('#btn').addEventListener('click', async () => {
  try {
    const response = await fetch('http://localhost:8080/api/test')
    const data = await response.text()
    document.querySelector('#result').textContent = data
  } catch (error) {
    document.querySelector('#result').textContent =
      '❌ Erreur de connexion au backend'
    console.error(error)
  }
})
