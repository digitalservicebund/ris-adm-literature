<script lang="ts" setup>
import * as Sentry from '@sentry/vue'

Sentry.captureException('Error thrown unconditionally')
</script>

<template>This page throws a sentry error.</template>
