<script lang="ts" setup>
import { ref, type Ref } from 'vue'
import { useRoute } from 'vue-router'
import DocumentUnitInfoPanel from '@/components/DocumentUnitInfoPanel.vue'
import NavbarSide from '@/components/NavbarSide.vue'
import SideToggle from '@/components/SideToggle.vue'
import { useAdmVwvMenuItems } from '@/composables/useAdmVwvMenuItems'

const route = useRoute()
const menuItems = useAdmVwvMenuItems('KSNR707', route.query)

const showNavigationPanelRef: Ref<boolean> = ref(route.query.showNavigationPanel !== 'false')

function toggleNavigationPanel(expand?: boolean) {
  showNavigationPanelRef.value = expand === undefined ? !showNavigationPanelRef.value : expand
}
</script>

<template>
  <div class="flex w-screen grow">
    <div class="sticky top-0 z-50 flex flex-col border-r-1 border-solid border-gray-400 bg-white">
      <SideToggle
        class="sticky top-0 z-20"
        data-testid="side-toggle-navigation"
        :is-expanded="showNavigationPanelRef"
        label="Navigation"
        tabindex="0"
        test-id="side-toggle-navigation"
        @update:is-expanded="toggleNavigationPanel"
      >
        <NavbarSide :is-child="false" :menu-items="menuItems" :route="route" />
      </SideToggle>
    </div>
    <div class="flex w-full min-w-0 flex-col bg-gray-100">
      <DocumentUnitInfoPanel data-testid="document-unit-info-panel" :heading="'KSNR054920707'" />
      <div class="flex grow flex-col items-start">
        <FlexContainer
          class="h-full w-full flex-grow"
          :class="route.path.includes('preview') ? 'flex-row bg-white' : 'flex-row-reverse'"
          >Hallo Welt</FlexContainer
        >
      </div>
    </div>
  </div>
</template>
