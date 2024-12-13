<script lang="ts" setup>
import { ref } from "vue"
import { useRoute } from "vue-router"

const route = useRoute()
const fontColor = ref<string>()

</script>

<template>
  <nav
    class="flex items-center justify-between border-y border-gray-400 py-16 pe-16 print:hidden"
  >
    <div class="flex items-center gap-44">
      <div class="flex items-center">
        <span class="px-[1rem] text-14 font-bold uppercase leading-16">
          <span aria-hidden="true" :style="{ color: fontColor }">
            Rechtsinformationen</span
          >
          <br />
          <span aria-hidden="true">des Bundes</span>
        </span>
      </div>
    </div>
  </nav>
</template>
