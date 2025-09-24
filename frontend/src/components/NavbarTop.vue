<script lang="ts" setup>
import { computed } from 'vue'
import IconBadge from '@/components/IconBadge.vue'
import { useAuthentication } from '@/services/auth'
import IconPermIdentity from '~icons/ic/baseline-perm-identity'
import IconLogout from '~icons/ic/baseline-logout'
import FlexContainer from '@/components/FlexContainer.vue'
import FlexItem from '@/components/FlexItem.vue'

const { getUsername, logout, getRealmRoles } = useAuthentication()

const userRolesLabel = computed(() => {
  // TODO: decide and show correct String (lit type vs. Dokumentationsstelle) // NOSONAR
  if (getRealmRoles) {
    const roles = getRealmRoles()
    return roles.length > 0 ? roles[0] : 'Keine Rolle' // take the first role for now
  }
  return 'Rolle lädt...'
})
</script>

<template>
  <nav
    class="flex items-center justify-between border-y border-gray-400 px-24 py-16 print:hidden bg-white"
  >
    <div class="flex items-center gap-44">
      <div class="flex flex-col">
        <span class="ris-body1-bold">Rechtsinformationen</span>
        <span class="leading-none text-gray-900">des Bundes</span>
      </div>
      <router-link to="/" class="ris-label1-regular p-8 hover:bg-yellow-500 hover:underline"
        >Suche</router-link
      >
    </div>
    <div class="flex items-center gap-10">
      <FlexContainer alignItems="items-center">
        <IconPermIdentity data-testid="iconPermIdentity" />
        <FlexItem>
          {{ getUsername() ?? 'Vorname Nachname' }}
        </FlexItem>
        <FlexItem>
          <IconBadge
            :background-color="'bg-red-300'"
            color="text-black"
            :label="`${userRolesLabel ?? ''} | staging`"
          />
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
