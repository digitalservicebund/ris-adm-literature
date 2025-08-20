<script lang="ts" setup>
import FlexContainer from '@/components/FlexContainer.vue'
import FlexItem from '@/components/FlexItem.vue'
import IconBadge from '@/components/IconBadge.vue'
import { useAuthentication } from '@/services/auth'
import IconPermIdentity from '~icons/ic/baseline-perm-identity'
import IconLogout from '~icons/ic/baseline-logout'

const { getUsername, logout } = useAuthentication()
</script>

<template>
  <nav class="flex items-center justify-between border-y border-gray-400 px-16 py-16 print:hidden">
    <div class="flex items-center gap-48">
      <div class="flex flex-col">
        <span class="ris-body1-bold">Rechtsinformationen</span>
        <span class="leading-none text-gray-900">des Bundes</span>
      </div>
      <router-link to="/" class="bg-yellow-500">Suche</router-link>
    </div>
    <div class="flex items-center gap-10">
      <FlexContainer alignItems="items-center">
        <IconPermIdentity data-testid="iconPermIdentity" />
        <FlexItem>
          {{ getUsername() ?? 'Vorname Nachname' }}
        </FlexItem>
        <FlexItem>
          <IconBadge :background-color="'bg-red-300'" color="text-black" :label="'BSG | Staging'" />
        </FlexItem>
        <FlexItem>
          <button @click="logout" class="hover:cursor-pointer" aria-label="Log out">
            <IconLogout class="h-5 w-5" />
          </button>
        </FlexItem>
      </FlexContainer>
    </div>
  </nav>
</template>
