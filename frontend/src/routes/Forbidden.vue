<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-4">
    <div class="bg-white p-10 rounded-lg shadow-xl text-center">
      <h1 class="text-6xl font-bold mb-4">403</h1>
      <h2 class="text-2xl font-semibold mb-2">Zugriff verweigert</h2>
      <p class="mb-6">
        Sie haben nicht die erforderlichen Berechtigungen, um auf diese Seite zuzugreifen.
      </p>
      <button @click="goHome" class="bg-blue-500 hover:bg-blue-700 font-bold py-2 px-4 rounded-lg">
        Zur Startseite
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push({ name: 'StartPage' })
}
</script>
