<script lang="ts" setup>
import * as Sentry from '@sentry/vue'

console.log('Capturing a sentry error unconditionally ')

Sentry.captureException('Error thrown unconditionally')

console.log('Throwing an error unconditionally ')

throw new Error('Error thrown unconditionally')
</script>

<template>This page throws a sentry error.</template>
