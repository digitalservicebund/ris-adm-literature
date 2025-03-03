<script setup lang="ts">
import TextButton from '@/components/input/TextButton.vue'
import Tooltip from '@/components/ToolTip.vue'
import IconStickyNote from '~icons/ic/outline-sticky-note-2'

const props = defineProps<{
  hidePanelModeBar?: boolean
}>()
</script>

<template>
  <div class="m-24 flex flex-row justify-between">
    <div v-if="!props.hidePanelModeBar" class="flex flex-row -space-x-2">
      <Tooltip shortcut="n" text="Notiz">
        <TextButton
          id="note"
          aria-label="Notiz anzeigen"
          button-type="tertiary"
          class="flex bg-blue-200"
          data-testid="note-button"
          :icon="IconStickyNote"
          size="small"
        />
      </Tooltip>
    </div>
  </div>
</template>
